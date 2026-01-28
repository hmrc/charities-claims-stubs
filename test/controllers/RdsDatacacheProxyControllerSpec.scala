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
import play.api.libs.json.JsObject
import play.api.test.FakeRequest
import play.api.test.Helpers.*

import java.util.UUID

class RdsDatacacheProxyControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val charityReference = UUID.randomUUID().toString

  "RdsDatacacheProxyController" should {

    "getAgentName" should {
      "return the agent name for a given agent reference" in {
        val agentReference = UUID.randomUUID().toString
        val request        = FakeRequest(GET, s"/rds-datacache-proxy/charities/agents/$agentReference")
        val result         = route(app, request).get
        status(result)                                                   shouldBe OK
        contentAsJson(result).as[JsObject].value("agentName").as[String] shouldBe s"Agent $agentReference"
      }
    }

    "getOrganisationName" should {
      "return the organisation name for a given charity reference" in {
        val charityReference = UUID.randomUUID().toString
        val request          = FakeRequest(GET, s"/rds-datacache-proxy/charities/organisations/$charityReference")
        val result           = route(app, request).get
        status(result) shouldBe OK
        contentAsJson(result)
          .as[JsObject]
          .value("organisationName")
          .as[String]  shouldBe s"Organisation $charityReference"
      }
    }
  }
}
