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

package config

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl._
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrlPolicy.Id
import uk.gov.hmrc.play.bootstrap.binders.{AbsoluteWithHostnameFromAllowlist, RedirectUrl, RedirectUrlPolicy}

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration) {

  val host: String    = configuration.get[String]("host")
  val appName: String = configuration.get[String]("appName")

  private val contactHost                  = configuration.get[String]("contact-frontend.host")
  private val contactFormServiceIdentifier = "calculate-public-pension-adjustment-frontend"

  def feedbackUrl(implicit request: RequestHeader): String = {
    val backUrl: String                  = host + request.uri
    val allowedRedirectUrls: Seq[String] = configuration.get[Seq[String]]("urls.allowedRedirects")
    val policy: RedirectUrlPolicy[Id]    = AbsoluteWithHostnameFromAllowlist(allowedRedirectUrls: _*)
    val safeBackUrl                      = RedirectUrl(backUrl).get(policy).encodedUrl
    s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier&backUrl=$safeBackUrl"
  }

  val baseUrl: String                        = configuration.get[String]("urls.base")
  val loginUrl: String                       = configuration.get[String]("urls.login")
  val loginContinueUrl: String               = configuration.get[String]("urls.loginContinue")
  val signOutUrl: String                     = configuration.get[String]("urls.signOut")
  val submitFrontend: String                 = configuration.get[String]("urls.submitFrontend")
  val redirectToStartPage: String            = configuration.get[String]("urls.redirectToStartPage")
  val redirectToOutstandingTasksPage: String = configuration.get[String]("urls.redirectToOutstandingTasksPage")
  val redirectToTaskListPage: String         = configuration.get[String]("urls.redirectToTaskListPage")

  val confidenceUpliftUrl: String = configuration.get[String]("urls.confidenceUplift")
  val upliftCompletionUrl         = configuration.get[String]("urls.upliftCompletion")
  val upliftFailureUrl            = configuration.get[String]("urls.upliftFailure")
  val requiredAuthConfidenceLevel = configuration.get[String]("required-auth-confidence-level")

  private val exitSurveyBaseUrl: String = configuration.get[Service]("microservice.services.feedback-frontend").baseUrl
  val exitSurveyUrl: String             = s"$exitSurveyBaseUrl/feedback/calculate-public-pension-adjustment-frontend"

  val cppaBaseUrl: String =
    configuration.get[Service]("microservice.services.calculate-public-pension-adjustment").baseUrl

//  TODO - Remove to add back welsh translantion
//  val languageTranslationEnabled: Boolean =
//    configuration.get[Boolean]("features.welsh-translation")

  val languageTranslationEnabled: Boolean = true

  val origin = configuration.get[String]("origin")

  def languageMap: Map[String, Lang] = Map(
    "en" -> Lang("en"),
    "cy" -> Lang("cy")
  )

  val calculationReviewEnabled = configuration.get[Boolean]("feature-flags.calculation-review")

  val timeout: Int   = configuration.get[Int]("timeout-dialog.timeout")
  val countdown: Int = configuration.get[Int]("timeout-dialog.countdown")

  val cacheTtl: Int = configuration.get[Int]("mongodb.timeToLiveInSeconds")

  val beforeCalculationAuditEventName =
    configuration.get[String]("auditing.before-calculation-request-event-name")

  val calculationAuditEventName =
    configuration.get[String]("auditing.calculation-request-event-name")

  val calculationStartAuditEventName =
    configuration.get[String]("auditing.calculation-start-event-name")

  val calculationTaskListAuditEventName =
    configuration.get[String]("auditing.calculation-task-list-event-name")

  val triageJourneyNotImpactedKickOff =
    configuration.get[String]("auditing.triage-journey-not-impacted-kick-off-event-name")

  val triageJourneyNotEligibleNoRpssKickOff =
    configuration.get[String]("auditing.triage-journey-not-eligible-no-rpss-kick-off-event-name")

  val triageJourneyNotEligiblePiaDecreaseKickOff =
    configuration.get[String]("auditing.triage-journey-not-eligible-pia-decrease-kick-off-event-name")

  val triageJourneyNotImpactedNoBceKickOff =
    configuration.get[String]("auditing.triage-journey-not-impacted-no-bce-kick-off-event-name")

  val triageJourneyNotImpactedNoChangeKickOff =
    configuration.get[String]("auditing.triage-journey-not-impacted-no-change-kick-off-event-name")

  val cannotUseLtaServiceNoChargeKickOff =
    configuration.get[String]("auditing.cannot-use-lta-service-no-change-kick-off-event-name")

  val eligibility =
    configuration.get[String]("auditing.eligibility")

}
