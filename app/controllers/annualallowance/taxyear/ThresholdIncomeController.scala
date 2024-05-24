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
import forms.annualallowance.taxyear.ThresholdIncomeFormProvider
import models.tasklist.sections.AASection
import models.{Mode, Period}
import pages.annualallowance.taxyear.ThresholdIncomePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserDataService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.annualallowance.taxyear.ThresholdIncomeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ThresholdIncomeController @Inject() (
                                            override val messagesApi: MessagesApi,
                                            userDataService: UserDataService,
                                            identify: IdentifierAction,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            formProvider: ThresholdIncomeFormProvider,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: ThresholdIncomeView
                                          )(implicit ec: ExecutionContext)
  extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mode: Mode, period: Period): Action[AnyContent] =
    (identify andThen getData andThen requireData) { implicit request =>
      val form         = formProvider(period)
      val preparedForm = request.userAnswers.get(ThresholdIncomePage(period)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, period))
    }

  def onSubmit(mode: Mode, period: Period): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request =>
      val form = formProvider(period)
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, period))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(ThresholdIncomePage(period), value))
              redirectUrl     = ThresholdIncomePage(period).navigate(mode, updatedAnswers).url
              answersWithNav  = AASection(period).saveNavigation(updatedAnswers, redirectUrl)
              _              <- userDataService.set(answersWithNav)
            } yield Redirect(redirectUrl)
        )
    }
}