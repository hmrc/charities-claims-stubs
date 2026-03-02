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
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import uk.gov.hmrc.charitiesclaimsstubs.models.SaveUnregulatedDonationRequest
import uk.gov.hmrc.charitiesclaimsstubs.repositories.UnregulatedDonationsRepository
import scala.concurrent.{ExecutionContext, Future}

class FormpProxyController @Inject() (
  val controllerComponents: ControllerComponents,
  val unregulatedDonationsRepository: UnregulatedDonationsRepository
)(using ExecutionContext)
    extends BaseController {

  private val dynamicCharityRefPattern = """^charity-ref-(\d+)$""".r // example: charity-ref-5000

  def getTotalUnregulatedDonations(charityReference: String): Action[AnyContent] =
    Action.async { implicit request =>
      unregulatedDonationsRepository.findByCharityReference(charityReference).flatMap {
        case Some(unregulatedDonation) =>
          // if found in MongoDB — we return the stored total
          Future.successful(Ok(Json.obj("unregulatedDonationsTotal" -> unregulatedDonation.totalUnregulatedDonations)))

        case None =>
          charityReference match {
            case dynamicCharityRefPattern(amount) =>
              // seed the dynamic value into MongoDB, then return it
              val seedAmount = BigDecimal(amount)
              unregulatedDonationsRepository.seedIfAbsent(charityReference, seedAmount).map { _ =>
                Ok(Json.obj("unregulatedDonationsTotal" -> seedAmount))
              }
            case _                                =>
              // no match in DB and not a dynamic pattern — not found
              Future.successful(NotFound)
          }
      }
    }

  def saveUnregulatedDonation(charityReference: String): Action[SaveUnregulatedDonationRequest] =
    Action.async(parse.json[SaveUnregulatedDonationRequest]) { implicit request =>
      unregulatedDonationsRepository
        .updateTotalUnregulatedDonations(charityReference, request.body.amount)
        .map { _ =>
          Ok(Json.obj("success" -> true))
        }
    }
}
