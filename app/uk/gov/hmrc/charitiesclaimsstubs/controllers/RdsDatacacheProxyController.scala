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
import com.google.inject.Singleton
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import scala.concurrent.Future

@Singleton
class RdsDatacacheProxyController @Inject() (
  val controllerComponents: ControllerComponents
) extends BaseController {

  private val organisationNameMap = Map(
    "1111111111" -> "The Royal Society for the Prevention of Cruelty to Animals",
    "2222222222" -> "The British Heart Foundation",
    "3333333333" -> "The National Trust",
    "4444444444" -> "Help the Aged",
    "5555555555" -> "Stop AI Slop Foundation",
    "6666666666" -> "Retired but not Forgotten Developers Help Society",
    "7777777777" -> "Wildlife Trust of South West England",
    "8888888888" -> "Save the Seals Association",
    "9999999999" -> "Never Give Up On Your Dreams Foundation",
    "1010101010" -> "Always Be Prepared Foundation",
    "1111111111" -> "Go Get Em Tigers Foundation",
    "1212121212" -> "To the Moon and Back Foundation",
    "1313131313" -> "Around the World in 80 Days Foundation",
    "1414141414" -> "Bring Back the Future Foundation",
    "1515151515" -> "Again and Again and Again Foundation",
    "1616161616" -> "Birds of a Feather Foundation",
    "1717171717" -> "School Meals For All Trust",
    "1818181818" -> "Pizza sHeritage and Taste of Italy Charity",
    "1919191919" -> "Small Steps for Big Changes Charity",
    "2020202020" -> "One Big Family Trust",
    "2121212121" -> "We scare because we care association",
    "2222222222" -> "Bees for Bees Charity",
    "2323232323" -> "Give Us Money, Money, Money and more Money Foundation",
    "2424242424" -> "Healthy Food for Kids and Cats Foundation",
    "2525252525" -> "Cancer Research UK Foundation",
    "2626262626" -> "Lotte's Little Angels Foundation",
    "2727272727" -> "Lotterry loosers help line",
    "2828282828" -> "Beer and Cider Lovers Association",
    "2929292929" -> "Cricket for the Blind Foundation",
    "3030303030" -> "Girls of the Universe Association"
  )

  private val agentNameMap = Map(
    "001" -> "James Smith",
    "002" -> "Jane Doe",
    "003" -> "Agata Chris",
    "004" -> "Martha Stew",
    "005" -> "Samantha Clark",
    "006" -> "Rachel Dark",
    "007" -> "Roger Rabbit",
    "008" -> "Mary Poppin",
    "009" -> "Elizabeth Swifty",
    "011" -> "Charles Mickeymouse",
    "012" -> "Diana Duck",
    "013" -> "Ethan Elephant",
    "014" -> "Fiona Fox",
    "015" -> "George Giraffe",
    "016" -> "Hannah Hippo",
    "017" -> "Ivy Iguana",
    "018" -> "Jack Jaguar",
    "019" -> "Katie Kangaroo",
    "020" -> "Liam Lion",
    "021" -> "Mia Mouse",
    "022" -> "Nate Narwhal",
    "023" -> "Olivia Owl",
    "024" -> "Paul Panda",
    "025" -> "Quinn Quokka",
    "026" -> "Rory Rabbit",
    "027" -> "Sammy Snake",
    "028" -> "Tara Turtle",
    "029" -> "Ulysses Unicorn",
    "030" -> "Victoria Vulture",
    "031" -> "William Wolf",
    "032" -> "Xavier X-ray",
    "033" -> "Yvonne Yak",
    "034" -> "Zachary Zebra",
    "035" -> "Abigail Ant",
    "036" -> "Bobby Bear",
    "037" -> "Charlie Cat",
    "038" -> "Diana Dog",
    "039" -> "Ethan Eagle",
    "040" -> "Fiona Frog",
    "041" -> "George Giraffe",
    "042" -> "Hannah Hippo",
    "043" -> "Ivy Iguana",
    "044" -> "Jack Jaguar",
    "045" -> "Katie Kangaroo",
    "046" -> "Liam Lion",
    "047" -> "Mia Mouse",
    "048" -> "Nate Narwhal",
    "049" -> "Olivia Owl",
    "050" -> "Paul Panda",
    "051" -> "Quinn Quokka",
    "052" -> "Rory Rabbit",
    "053" -> "Sammy Snake",
    "054" -> "Tara Turtle",
    "055" -> "Ulysses Unicorn",
    "056" -> "Victoria Vulture",
    "057" -> "William Wolf",
    "058" -> "Xavier X-ray",
    "059" -> "Yvonne Yak",
    "060" -> "Zachary Zebra",
    "061" -> "Abigail Ant",
    "062" -> "Bobby Bear",
    "063" -> "Charlie Cat",
    "064" -> "Diana Dog",
    "065" -> "Ethan Eagle",
    "066" -> "Fiona Frog",
    "067" -> "George Giraffe",
    "068" -> "Hannah Hippo",
    "069" -> "Ivy Iguana",
    "070" -> "Jack Jaguar",
    "071" -> "Katie Kangaroo",
    "072" -> "Liam Lion",
    "073" -> "Mia Mouse",
    "074" -> "Nate Narwhal",
    "075" -> "Olivia Owl",
    "076" -> "Paul Panda",
    "077" -> "Quinn Quokka",
    "078" -> "Rory Rabbit",
    "079" -> "Sammy Snake",
    "080" -> "Tara Turtle",
    "081" -> "Ulysses Unicorn",
    "082" -> "Victoria Vulture",
    "083" -> "William Wolf",
    "084" -> "Xavier X-ray",
    "085" -> "Yvonne Yak",
    "086" -> "Zachary Zebra",
    "087" -> "Abigail Ant",
    "088" -> "Bobby Bear",
    "089" -> "Charlie Cat",
    "090" -> "Diana Dog",
    "091" -> "Ethan Eagle",
    "092" -> "Fiona Frog",
    "093" -> "George Giraffe",
    "094" -> "Hannah Hippo",
    "095" -> "Ivy Iguana",
    "096" -> "Jack Jaguar",
    "097" -> "Katie Kangaroo",
    "098" -> "Liam Lion",
    "099" -> "Mia Mouse",
    "100" -> "Nate Narwhal"
  )

  def getAgentName(agentRef: String): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(
      Ok(Json.obj("agentName" -> agentNameMap.get(agentRef.takeRight(3)).getOrElse(s"Agent $agentRef")))
    )
  }

  def getOrganisationName(charityRef: String): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(
      Ok(
        Json.obj(
          "organisationName" -> organisationNameMap.get(charityRef.takeRight(10)).getOrElse(s"Organisation $charityRef")
        )
      )
    )
  }

}
