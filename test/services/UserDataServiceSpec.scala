package services

import base.SpecBase
import connectors.UserAnswersConnector
import org.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier

class UserDataServiceSpec extends SpecBase with MockitoSugar {

  val mockUserAnswersConnector = mock[UserAnswersConnector]

  val service = new UserDataService(mockUserAnswersConnector)

  "UserDataService" - {
    "should call connector when checking submission status" in {
      implicit val hc = HeaderCarrier()

      service.checkSubmissionStatusWithId("id")

      verify(mockUserAnswersConnector, times(1))
        .checkSubmissionStatusWithId("id")
    }
  }
}
