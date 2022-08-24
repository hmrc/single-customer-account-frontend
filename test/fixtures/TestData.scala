/*
 * Copyright 2022 HM Revenue & Customs
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

package fixtures

import models.auth.{AuthenticatedIFRequest, AuthenticatedRequest}
import models.integrationframework.{Address, CitizenDetails, Person}
import play.api.mvc.Request
import uk.gov.hmrc.auth.core.retrieve.v2.TrustedHelper
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name}
import uk.gov.hmrc.auth.core.{ConfidenceLevel, Enrolment, EnrolmentIdentifier}

import java.time.LocalDate

object TestData {

  object Requests {

    def authenticatedRequest[A](request: Request[A]): AuthenticatedRequest[A] = AuthenticatedRequest(
      nino = Some(uk.gov.hmrc.domain.Nino("AA111111A")),
      credentials = Credentials(
        "providerId",
        "providerType"
      ),
      confidenceLevel = ConfidenceLevel.L250,
      name = Some(Name(Some("John"), Some("Smith"))),
      trustedHelper = Some(TrustedHelper("principalName", "attorneyName", "returnLinkUrl", "principalNino")),
      profile = Some("profile"),
      enrolments = Set(Enrolment("key", Seq(EnrolmentIdentifier("key", "value")), "state", None)),
      request = request
    )

    def authenticatedDetailsRequest[A](authenticatedRequest: AuthenticatedRequest[A]): AuthenticatedIFRequest[A] = AuthenticatedIFRequest(
      authenticatedRequest = authenticatedRequest,
      ifData = Some(CitizenDetailsData.citizenDetails)
    )
  }

  object CitizenDetailsData {

    def citizenDetails: CitizenDetails = CitizenDetails(
      person = person,
      address = Some(address1),
      correspondenceAddress = Some(address1))

    val person: Person = Person(
      firstName = Some("John"),
      lastName = Some("Smith"),
      middleName = Some("Andrew"),
      initials = Some("J.S"),
      title = Some("Mr"),
      honours = Some("honours"),
      sex = Some("male"),
      dateOfBirth = Some(LocalDate.of(1990, 1, 1)),
      nino = Some(uk.gov.hmrc.auth.core.Nino(hasNino = true, Some("AA111111A")))
    )

    val address1: Address = Address(
      line1 = Some("1 test road"),
      line2 = Some("test town"),
      line3 = Some("test city"),
      line4 = Some("test county"),
      line5 = Some("test"),
      postcode = Some("TEST10"),
      country = Some("UK"),
      startDate = Some(LocalDate.of(2000, 1, 1)),
      endDate = None,
      `type` = Some("type"),
      isRls = true
    )
  }

}
