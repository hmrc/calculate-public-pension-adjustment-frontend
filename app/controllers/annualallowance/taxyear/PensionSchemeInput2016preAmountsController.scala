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

package controllers.annualallowance.taxyear

import controllers.actions._
import forms.annualallowance.taxyear.PensionSchemeInput2016preAmountsFormProvider
import models.tasklist.sections.AASection
import models.{Mode, Period, SchemeIndex}
import pages.annualallowance.taxyear.{PensionSchemeDetailsPage, PensionSchemeInputAmounts1516Period1Page, PensionSchemeInputAmountsPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.annualallowance.taxyear.PensionSchemeInput2016preAmountsView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PensionSchemeInput2016preAmountsController @Inject()(
                                                            override val messagesApi: MessagesApi,
                                                            sessionRepository: SessionRepository,
                                                            identify: IdentifierAction,
                                                            getData: DataRetrievalAction,
                                                            requireData: DataRequiredAction,
                                                            formProvider: PensionSchemeInput2016preAmountsFormProvider,
                                                            val controllerComponents: MessagesControllerComponents,
                                                            view: PensionSchemeInput2016preAmountsView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode, period: Period, schemeIndex: SchemeIndex): Action[AnyContent] =
    (identify andThen getData andThen requireData) { implicit request =>
      val preparedForm = request.userAnswers.get(PensionSchemeInputAmounts1516Period1Page(period, schemeIndex)) match {
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
      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, mode, period, schemeIndex, schemeName.getOrElse("")))),
          value =>
            for {
              updatedAnswers <-
                Future.fromTry(request.userAnswers.set(PensionSchemeInputAmounts1516Period1Page(period, schemeIndex), value))
              redirectUrl     = PensionSchemeInputAmounts1516Period1Page(period, schemeIndex).navigate(mode, updatedAnswers).url
              answersWithNav  = AASection(period).saveNavigation(updatedAnswers, redirectUrl)
              _              <- sessionRepository.set(answersWithNav)
            } yield Redirect(redirectUrl)
        )
    }
}
