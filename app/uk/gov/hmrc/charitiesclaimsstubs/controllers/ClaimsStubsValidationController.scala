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

package uk.gov.hmrc.charitiesclaimsstubs.controllers

import com.google.inject.Inject
import uk.gov.hmrc.charitiesclaimsstubs.models.{DeleteScheduleResponse, GetUploadSummaryResponse, UploadSummary}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import scala.concurrent.Future

class ClaimsStubsValidationController @Inject() (
  val controllerComponents: ControllerComponents
) extends BaseController {

  def getUploadSummary(claimId: String): Action[AnyContent] = Action.async { implicit request =>
    claimId match {
      case "000000" =>
        // use for empty uploads array for error testing
        val response = GetUploadSummaryResponse(uploads = Seq.empty)
        Future.successful(Ok(Json.toJson(response)))

      case "no-giftaid" =>
        val response = GetUploadSummaryResponse(
          uploads = Seq(
            UploadSummary("501beba6-fb65-4952-93fc-f83be323fde6", "OtherIncome", "VALIDATED"),
            UploadSummary("111a5578-8393-4cd1-be0e-d8ef1b78d811", "CommunityBuildings", "VALIDATED"),
            UploadSummary("22da5578-8393-4cd1-be0e-d8ef1b78d822", "ConnectedCharities", "VALIDATED")
          )
        )
        Future.successful(Ok(Json.toJson(response)))

      case "error-claim" =>
        Future.successful(InternalServerError(Json.obj("error" -> "Internal server error")))

      case "not-found" =>
        Future.successful(NotFound(Json.obj("error" -> "Claim not found")))

      case _ =>
        // default returns all 4 possible schedule types
        val response = GetUploadSummaryResponse(
          uploads = Seq(
            UploadSummary(
              reference = "f5da5578-8393-4cd1-be0e-d8ef1b78d8e7",
              validationType = "GiftAid",
              fileStatus = "VALIDATED"
            ),
            UploadSummary(
              reference = "501beba6-fb65-4952-93fc-f83be323fde6",
              validationType = "OtherIncome",
              fileStatus = "VALIDATED"
            ),
            UploadSummary(
              reference = "111a5578-8393-4cd1-be0e-d8ef1b78d811",
              validationType = "CommunityBuildings",
              fileStatus = "VALIDATED"
            ),
            UploadSummary(
              reference = "22da5578-8393-4cd1-be0e-d8ef1b78d822",
              validationType = "ConnectedCharities",
              fileStatus = "VALIDATED"
            )
          )
        )
        Future.successful(Ok(Json.toJson(response)))
    }
  }

  def deleteSchedule(claimId: String, reference: String): Action[AnyContent] = Action.async { implicit request =>
    (claimId, reference) match {
      case ("000000", "000000") =>
        // use to test delete false response
        val response = DeleteScheduleResponse(success = false)
        Future.successful(Ok(Json.toJson(response)))

      case (_, "invalid-ref") =>
        val response = DeleteScheduleResponse(success = false)
        Future.successful(Ok(Json.toJson(response)))

      case _ =>
        val response = DeleteScheduleResponse(success = true)
        Future.successful(Ok(Json.toJson(response)))
    }
  }
}
