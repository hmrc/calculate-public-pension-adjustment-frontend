package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, post, urlEqualTo}
import generators.Generators
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status.{NOT_FOUND, OK}
import play.api.libs.json.Json
import play.api.test.Helpers.running
import uk.gov.hmrc.http.HeaderCarrier

class UserAnswerConnectorSpec extends SpecBase with WireMockHelper with ScalaCheckPropertyChecks with Generators {

  private def application: Application =
    applicationBuilder()
      .configure("microservice.services.calculate-public-pension-adjustment.port" -> server.port)
      .build()

  "checkSubmissionStatus" - {
    "must return a Success response containing submission status when server returns OK" in {
      implicit val hc = HeaderCarrier()

      val url = s"/calculate-public-pension-adjustment/check-submission-status/id"
      val app = application

      running(app) {
        val connector = app.injector.instanceOf[UserAnswersConnector]

        val responseBody = Json.parse("{\"uniqueId\":\"uniqueId\",\"submissionStarted\":true}").toString()

        server.stubFor(get(urlEqualTo(url)).willReturn(aResponse().withStatus(OK).withBody(responseBody)))

        val result = connector.checkSubmissionStatusWithId("id")(hc).futureValue

        result.get.uniqueId mustBe "uniqueId"
        result.get.submissionStarted mustBe true
      }
    }

    "must return empty when server returns Not Found" in {
      implicit val hc = HeaderCarrier()

      val url = s"/calculate-public-pension-adjustment/check-submission-status/id"
      val app = application

      running(app) {
        val connector = app.injector.instanceOf[UserAnswersConnector]

        server.stubFor(get(urlEqualTo(url)).willReturn(aResponse().withStatus(NOT_FOUND)))

        val result = connector.checkSubmissionStatusWithId("id")(hc).futureValue

        result.isEmpty mustBe true
      }
    }
  }
}
