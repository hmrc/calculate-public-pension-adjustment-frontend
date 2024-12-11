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

package services

import models.{PSTR, PensionSchemeDetails, Period, PrivatePensionSchemeDetails, SchemeIndex, UserAnswers, WhichPrivateScheme}
import pages.annualallowance.taxyear.{PensionSchemeDetailsPage, PrivatePensionSchemeDetailsPage}
import play.api.i18n.Messages

object PrivateSchemeService {

  def whichScheme(answers: UserAnswers)(implicit messages: Messages): WhichPrivateScheme = {
    val schemeRefs: Seq[PSTR]        = allSchemeRefs(answers)
    val schemeRefsOrNew: Seq[String] = schemeRefs.map(pstr => pstr.value)

    WhichPrivateScheme(schemeRefsOrNew :+ messages("site.newPSTR"))
  }

  def maybeAddSchemeDetailsToPeriod(
    answers: UserAnswers,
    schemeRef: String,
    period: Period,
    schemeIndex: SchemeIndex
  )(implicit messages: Messages): UserAnswers =
    if (schemeRef != messages("site.newPSTR")) {
      val schemeName: Option[String] = findSchemeName(answers, schemeRef)

      schemeName match {
        case Some(schemeName) =>
          answers.set(PrivatePensionSchemeDetailsPage(period, schemeIndex), PrivatePensionSchemeDetails(schemeName, schemeRef)).get
        case None             => answers
      }
    } else {
      answers
    }

  private def allSchemeRefs(answers: UserAnswers): Seq[PSTR] =
    allSchemeDetails(answers).map(detail => PSTR(detail.schemeTaxRef)).distinct

  private def findSchemeName(answers: UserAnswers, schemeRef: String): Option[String] = {
    val allDetails: Seq[PrivatePensionSchemeDetails] = allSchemeDetails(answers)
    allDetails.find(details => details.schemeTaxRef == schemeRef).map(psd => psd.schemeName)
  }

  private def allSchemeDetails(answers: UserAnswers): Seq[PrivatePensionSchemeDetails] = {
    val allSchemeDetailsOptions: Seq[Option[PrivatePensionSchemeDetails]] = for {
      period <- PeriodService.allRemedyPeriods
      index  <- allSchemeIndices
    } yield answers.get(PrivatePensionSchemeDetailsPage(period, index))
    val allSchemeDetails: Seq[PrivatePensionSchemeDetails]                = allSchemeDetailsOptions.flatten
    allSchemeDetails
  }

  private def allSchemeIndices: Seq[SchemeIndex] = 0.to(4).map(i => SchemeIndex(i))
}
