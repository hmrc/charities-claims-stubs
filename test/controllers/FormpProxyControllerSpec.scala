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

package controllers

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.*

import java.util.UUID

class FormpProxyControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val charityReference = UUID.randomUUID().toString

  "FormpProxyController" should {

    "saveUnregulatedDonation" should {
      "return OK when the unregulated donation is saved" in {
        val request =
          FakeRequest(POST, s"/formp-proxy/charities/$charityReference/unregulated-donations")
            .withJsonBody(Json.obj("claimId" -> 1, "amount" -> 100))

        val result = route(app, request).get

        status(result)                                                  shouldBe OK
        contentAsJson(result).as[JsObject].value("success").as[Boolean] shouldBe true
      }
    }

    "getTotalUnregulatedDonations" should {
      "return the total unregulated donations for a charity" in {
        val request = FakeRequest(GET, s"/formp-proxy/charities/$charityReference/unregulated-donations")

        val result = route(app, request).get

        contentAsJson(result)
          .as[JsObject]
          .value("unregulatedDonationsTotal")
          .as[BigDecimal] shouldBe 100
      }

      "return NOT_FOUND when the charity reference is not found" in {
        val request = FakeRequest(GET, s"/formp-proxy/charities/${UUID.randomUUID().toString}/unregulated-donations")
        val result  = route(app, request).get
        status(result) shouldBe NOT_FOUND
      }
    }
  }
}
