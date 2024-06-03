package controllers.annualallowance.taxyear

import controllers.actions._
import forms.annualallowance.taxyear.InterestFromSavingsFormProvider
import models.Mode
import pages.annualallowance.taxyear.InterestFromSavingsPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserDataService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class InterestFromSavingsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        userDataService: UserDataService,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: InterestFromSavingsFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: InterestFromSavingsView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(InterestFromSavingsPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(InterestFromSavingsPage, value))
            _              <- userDataService.set(updatedAnswers)
          } yield Redirect(InterestFromSavingsPage.navigate(mode, updatedAnswers))
      )
  }
}
