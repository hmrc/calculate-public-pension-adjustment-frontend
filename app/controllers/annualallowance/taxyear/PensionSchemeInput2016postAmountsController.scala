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
import forms.annualallowance.taxyear.PensionSchemeInput2016postAmountsFormProvider
import models.tasklist.sections.AASection
import models.{Mode, Period, SchemeIndex}
import pages.annualallowance.taxyear.{PensionSchemeDetailsPage, PensionSchemeInput2016postAmountsPage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserDataService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.annualallowance.taxyear.PensionSchemeInput2016postAmountsView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PensionSchemeInput2016postAmountsController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PensionSchemeInput2016postAmountsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PensionSchemeInput2016postAmountsView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mode: Mode, period: Period, schemeIndex: SchemeIndex): Action[AnyContent] =
    (identify andThen getData andThen requireData) { implicit request =>
      val form         = formProvider(dateString)
      val preparedForm = request.userAnswers.get(PensionSchemeInput2016postAmountsPage(period, schemeIndex)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }
      val schemeName   = request.userAnswers.get(PensionSchemeDetailsPage(period, schemeIndex)).map { answer =>
        answer.schemeName
      }

      Ok(view(preparedForm, mode, period, schemeIndex, schemeName.getOrElse("")))

    }

  def onSubmit(mode: Mode, period: Period, schemeIndex: SchemeIndex): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request =>
      val schemeName = request.userAnswers.get(PensionSchemeDetailsPage(period, schemeIndex)).map { answer =>
        answer.schemeName
      }
      val form       = formProvider(dateString)
      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, mode, period, schemeIndex, schemeName.getOrElse("")))),
          value =>
            for {
              updatedAnswers <-
                Future
                  .fromTry(request.userAnswers.set(PensionSchemeInput2016postAmountsPage(period, schemeIndex), value))
              redirectUrl     =
                PensionSchemeInput2016postAmountsPage(period, schemeIndex).navigate(mode, updatedAnswers).url
              answersWithNav  = AASection(period).saveNavigation(updatedAnswers, redirectUrl)
              _              <- userDataService.set(answersWithNav)
            } yield Redirect(redirectUrl)
        )
    }

  private def dateString(implicit messages: Messages): String =
    messages("pensionSchemeInputAmounts.2016-post.date")
}
