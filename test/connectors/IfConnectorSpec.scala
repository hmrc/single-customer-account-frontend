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

package connectors

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.{ok, urlEqualTo}
import controllers.actions.AuthActionImpl
import fixtures.RetrievalOps.Ops
import fixtures.{SpecBase, WireMockHelper}
import models.integrationframework.{IFContactDetail, IFContactDetails, IfAddress, IfAddressList, IfDesignatoryDetails, IfDetails, IfName, IfNameList}
import play.api.libs.ws.WSClient
import play.api.test.Helpers.running
import uk.gov.hmrc.auth.core.{AuthConnector, ConfidenceLevel, CredentialStrength, Enrolments}
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate
import scala.concurrent.Future

class IfConnectorSpec extends SpecBase with WireMockHelper {

  lazy val mockAuthConnector: AuthConnector = mock[AuthConnector]
  lazy val authAction = new AuthActionImpl(mockAuthConnector, frontendAppConfigInstance, bodyParserInstance)
  lazy val connector : IFConnector = new IFConnector(injector.instanceOf[WSClient],frontendAppConfigInstance)
  server.start()


  applicationBuilder.configure(
    "microservice.services.integration-framework.port" -> server.port(),
    "metrics.enabled" -> false,
    "auditing.enabled" -> false,
    "auditing.traceRequests" -> false
  )
    .build()

  val nino = "AA999999A"

  val designatoryDetailsFields: String =
    "details(marriageStatusType),nameList(name(nameSequenceNumber,nameType,titleType," +
      "requestedName,nameStartDate,nameEndDate,firstForename,secondForename,surname))," +
      "addressList(address(addressSequenceNumber,countryCode,addressType,addressStartDate," +
      "addressEndDate,addressLine1,addressLine2,addressLine3,addressLine4,addressLine5," +
      "addressPostcode))"

  val contactDetailsFields: String = "contactDetails(code,type,detail)"

  /*"calling If connector getDesignatoryDetails" must {
    "return IfDesignatoryDetails when response can be parsed" in {
      val url = s"/individuals/details/NINO/${nino}?fields=$designatoryDetailsFields"
      running(app) {

        val expectedObj = Some(IfDesignatoryDetails(
          details = IfDetails(Option(4)),
          nameList = IfNameList(Seq(
            IfName(
              nameSequenceNumber = Some(1),
              nameType = Some(1),
              titleType = Some(1),
              firstForename = Some("John"),
              secondForename = Some("X"),
              surname = Some("Johnson"),
              requestedName = Some("Johnny")
            ))),
          addressList = IfAddressList(Seq(IfAddress(
            addressSequenceNumber = Some(2),
            countryCode = Some(1),
            addressType = Some(1),
            addressLine1 = Some("1 Home Road"),
            addressLine2 = Some("Home Town"),
            addressLine3 = Some("Home Region"),
            addressLine4 = Some("Home Area"),
            addressLine5 = Some("Home Shire"),
            addressPostcode = Some("XX77 6YY")
          )))
        ))

        server.stubFor(
          WireMock.get(urlEqualTo(url))
            .willReturn(ok(expectedObj.toString()))
        )

        connector.getDesignatoryDetails(Some(Nino(nino))).futureValue mustEqual expectedObj
      }
    }
  }*/


/* "calling If connector getContactsDetails" must {
    "return IfContactDetails when response can be parsed" in {
      Thread.sleep(3000)
      val url = "http://localhost:8421/individuals/details/contact/nino/AA999999A?fields=contactDetails(code,type,detail)"
      println(url)
      running(app) {
        val contactDetails = Some(IFContactDetails(
          contactDetails = Some(Seq(
            IFContactDetail(code = 9, contactType = "MOBILE TELEPHONE", detail = "07123 987654"),
            IFContactDetail(code = 7, contactType = "DAYTIME TELEPHONE", detail = "07123 987654"),
            IFContactDetail(code = 8, contactType = "EVENING TELEPHONE", detail = "07123 987654"),
            IFContactDetail(code = 11, contactType = "PRIMARY E-MAIL", detail = "fred.blogs@hotmail.com")
          ))))
      server.stubFor(
        WireMock.get(urlEqualTo(url))
          .willReturn(ok(contactDetails.toString))
      )
        Thread.sleep(3000)
        /*whenReady((connector.getContactDetails(Some(Nino(nino))))){
          res => res.get.contactDetails mustEqual contactDetails
        }*/
      connector.getContactDetails(Some(Nino(nino))).futureValue mustEqual contactDetails
    }
  }
}*/
}
