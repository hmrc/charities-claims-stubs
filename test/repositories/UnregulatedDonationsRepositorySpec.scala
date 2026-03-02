/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package repositories

import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport
import org.scalatest.concurrent.ScalaFutures
import uk.gov.hmrc.charitiesclaimsstubs.repositories.UnregulatedDonationsRepository
import uk.gov.hmrc.charitiesclaimsstubs.models.UnregulatedDonation
import org.scalatest.matchers.should.Matchers
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.*
import scala.concurrent.Future

class UnregulatedDonationsRepositorySpec
    extends AnyWordSpec
    with DefaultPlayMongoRepositorySupport[UnregulatedDonation]
    with Matchers
    with ScalaFutures {

  override def checkTtlIndex: Boolean = false

  override val repository: UnregulatedDonationsRepository =
    new UnregulatedDonationsRepository(mongoComponent)

  def waitFor(duration: FiniteDuration): Future[Unit] =
    Future(Thread.sleep(duration.toMillis))

  "findAll" should {
    "find none initially" in
      repository.findAll().futureValue.shouldBe(Seq.empty)

    "find by charity reference should return none initially" in
      repository.findByCharityReference("1234567890").futureValue.shouldBe(None)

    "update and find by charity reference should return the updated total unregulated donations value" in
      {
        for {
          _       <- repository.updateTotalUnregulatedDonations("CHARITY_1", 100)
          _       <- waitFor(1.second)
          _       <- repository.updateTotalUnregulatedDonations("CHARITY_2", 500)
          _       <- repository.updateTotalUnregulatedDonations("CHARITY_1", 200)
          _       <- waitFor(1.second)
          result1 <- repository.findByCharityReference("CHARITY_1")
          result2 <- repository.findByCharityReference("CHARITY_2")
        } yield (result1, result2)
      }.futureValue
        .shouldBe(
          (
            Some(
              UnregulatedDonation(
                charityReference = "CHARITY_1",
                totalUnregulatedDonations = 300
              )
            ),
            Some(
              UnregulatedDonation(
                charityReference = "CHARITY_2",
                totalUnregulatedDonations = 500
              )
            )
          )
        )
  }

  "seedIfAbsent" should {
    "insert a new entry when the charity reference does not exist" in {
      for {
        _      <- repository.seedIfAbsent("SEED_CHARITY_1", 5000)
        result <- repository.findByCharityReference("SEED_CHARITY_1")
      } yield result
    }.futureValue.shouldBe(
      Some(UnregulatedDonation("SEED_CHARITY_1", 5000))
    )

    "not overwrite an existing entry when called again with a different amount" in {
      // seed once, then call again — the original value should remain
      for {
        _      <- repository.seedIfAbsent("SEED_CHARITY_2", 3000)
        _      <- repository.seedIfAbsent("SEED_CHARITY_2", 9999)
        result <- repository.findByCharityReference("SEED_CHARITY_2")
      } yield result
    }.futureValue.shouldBe(
      Some(UnregulatedDonation("SEED_CHARITY_2", 3000))
    )

    "allow updateTotalUnregulatedDonations to increment a seeded entry" in {
      // seed 5000, then increment by 200 — total should be 5200
      for {
        _      <- repository.seedIfAbsent("SEED_CHARITY_3", 5000)
        _      <- repository.updateTotalUnregulatedDonations("SEED_CHARITY_3", 200)
        result <- repository.findByCharityReference("SEED_CHARITY_3")
      } yield result
    }.futureValue.shouldBe(
      Some(UnregulatedDonation("SEED_CHARITY_3", 5200))
    )
  }
}
