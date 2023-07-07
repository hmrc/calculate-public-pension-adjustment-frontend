/*
 * Copyright 2023 HM Revenue & Customs
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

import controllers.actions._
import models.CalculationResults.{CalculationResponse, CalculationResultsViewModel}
import play.api.data.Form
import play.api.data.Forms.ignored
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.CalculationResultsService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.CalculationResultsView

import javax.inject.Inject
import scala.io.Source

class CalculationResultsController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: CalculationResultsView,
  calculationResultsService: CalculationResultsService
) extends FrontendBaseController
    with I18nSupport {

  val form = Form("_" -> ignored(()))

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val source: String                         = Source.fromFile("app/assets/CalculationResults.json").getLines().mkString
    val json: JsValue                          = Json.parse(source)
    val calculationResult: CalculationResponse = json.as[CalculationResponse]

    val calculationResultsViewModel: CalculationResultsViewModel =
      calculationResultsService.calculationResultsViewModel(calculationResult)
    Ok(view(form, calculationResultsViewModel))
  }
}
