package pages.annualallowance.taxyear

import models.UserAnswers
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object InterestFromSavingsPage extends QuestionPage[Int] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "interestFromSavings"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = {
    answers.get(InterestFromSavingsPage) match {
      case Some(_) => controllers.routes.JourneyRecoveryController.onPageLoad(None)
      case _ => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  override protected def navigateInCheckMode(answers: UserAnswers): Call = {
    answers.get(InterestFromSavingsPage) match {
      case Some(_) => controllers.routes.JourneyRecoveryController.onPageLoad(None)
      case _ => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }
}
