/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.annualallowance.taxyear

import controllers.actions._
import forms.annualallowance.taxyear.PensionSchemeDetailsFormProvider
import models.PSTR.{New, NewInWelsh}
import models.tasklist.sections.AASection
import models.{Mode, Period, SchemeIndex}
import pages.annualallowance.taxyear.{PensionSchemeDetailsPage, WhichSchemePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{PeriodService, UserDataService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.annualallowance.taxyear.PensionSchemeDetailsView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PensionSchemeDetailsController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PensionSchemeDetailsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PensionSchemeDetailsView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode, period: Period, schemeIndex: SchemeIndex): Action[AnyContent] =
    (identify andThen getData andThen requireData) { implicit request =>
      val preparedForm = {
        val whichScheme          = request.userAnswers.get(WhichSchemePage(period, schemeIndex))
        val pensionSchemeDetails = request.userAnswers.get(PensionSchemeDetailsPage(period, schemeIndex))

        pensionSchemeDetails match {
          case None        => form
          case Some(value) =>
            val isFirstPeriod = PeriodService.isFirstPeriod(request.userAnswers, period)
            val isNewScheme   = whichScheme.contains(New) || whichScheme.contains(NewInWelsh)

            (isFirstPeriod, isNewScheme, pensionSchemeDetails.isEmpty) match {
              case (true, _, _)    => form.fill(value)
              case (_, true, true) => form
              case (_, _, _)       => form.fill(value)
            }
        }
      }

      Ok(view(preparedForm, mode, period, schemeIndex))
    }

  def onSubmit(mode: Mode, period: Period, schemeIndex: SchemeIndex): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, period, schemeIndex))),
          value =>
            for {
              updatedAnswers <-
                Future.fromTry(request.userAnswers.set(PensionSchemeDetailsPage(period, schemeIndex), value))
              redirectUrl     = PensionSchemeDetailsPage(period, schemeIndex).navigate(mode, updatedAnswers).url
              answersWithNav  = AASection(period).saveNavigation(updatedAnswers, redirectUrl)
              _              <- userDataService.set(answersWithNav)
            } yield Redirect(redirectUrl)
        )
    }
}
