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
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.charitiesclaimsstubs.controllers.ClaimsStubsValidationController
import uk.gov.hmrc.charitiesclaimsstubs.models.{DeleteScheduleResponse, GetUploadSummaryResponse}

class ClaimsStubsValidationControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val controller: ClaimsStubsValidationController = app.injector.instanceOf[ClaimsStubsValidationController]

  "ClaimsStubsValidationController" should {

    "getUploadSummary" should {

      "return all 4 schedule types for default claimId" in {
        val request = FakeRequest(GET, "/charities-claims-validation/test-claim-123/upload-results")
        val result  = controller.getUploadSummary("test-claim-123")(request)

        status(result) shouldBe OK
        val response = contentAsJson(result).as[GetUploadSummaryResponse]
        response.uploads.size                                shouldBe 4
        response.uploads.map(_.validationType)                 should contain allOf (
          "GiftAid",
          "OtherIncome",
          "CommunityBuildings",
          "ConnectedCharities"
        )
        response.uploads.forall(_.fileStatus == "VALIDATED") shouldBe true
      }

      "return empty uploads array for claimId 000000" in {
        val request = FakeRequest(GET, "/charities-claims-validation/000000/upload-results")
        val result  = controller.getUploadSummary("000000")(request)

        status(result) shouldBe OK
        val response = contentAsJson(result).as[GetUploadSummaryResponse]
        response.uploads shouldBe empty
      }

      "return uploads without GiftAid for claimId no-giftaid" in {
        val request = FakeRequest(GET, "/charities-claims-validation/no-giftaid/upload-results")
        val result  = controller.getUploadSummary("no-giftaid")(request)

        status(result) shouldBe OK
        val response = contentAsJson(result).as[GetUploadSummaryResponse]
        response.uploads.size                shouldBe 3
        response.uploads.map(_.validationType) should not contain "GiftAid"
        response.uploads.map(_.validationType) should contain allOf (
          "OtherIncome",
          "CommunityBuildings",
          "ConnectedCharities"
        )
      }

      "return 500 error for claimId error-claim" in {
        val request = FakeRequest(GET, "/charities-claims-validation/error-claim/upload-results")
        val result  = controller.getUploadSummary("error-claim")(request)

        status(result) shouldBe INTERNAL_SERVER_ERROR
      }

      "return 404 for claimId not-found" in {
        val request = FakeRequest(GET, "/charities-claims-validation/not-found/upload-results")
        val result  = controller.getUploadSummary("not-found")(request)

        status(result) shouldBe NOT_FOUND
      }
    }

    "deleteSchedule" should {

      "return success true for valid claimId and reference" in {
        val request = FakeRequest(DELETE, "/charities-claims-validation/test-claim/upload-results/test-ref")
        val result  = controller.deleteSchedule("test-claim", "test-ref")(request)

        status(result) shouldBe OK
        val response = contentAsJson(result).as[DeleteScheduleResponse]
        response.success shouldBe true
      }

      "return success false for claimId 000000 and reference 000000" in {
        val request = FakeRequest(DELETE, "/charities-claims-validation/000000/upload-results/000000")
        val result  = controller.deleteSchedule("000000", "000000")(request)

        status(result) shouldBe OK
        val response = contentAsJson(result).as[DeleteScheduleResponse]
        response.success shouldBe false
      }

      "return success false for invalid-ref" in {
        val request = FakeRequest(DELETE, "/charities-claims-validation/test-claim/upload-results/invalid-ref")
        val result  = controller.deleteSchedule("test-claim", "invalid-ref")(request)

        status(result) shouldBe OK
        val response = contentAsJson(result).as[DeleteScheduleResponse]
        response.success shouldBe false
      }
    }
  }
}
