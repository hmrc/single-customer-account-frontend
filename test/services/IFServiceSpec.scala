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

package services

import connectors.IFConnector
import fixtures.SpecBase
import models.integrationframework.{Address, ContactDetails, IFContactDetail, IFContactDetails, IfAddress, IfAddressList, IfDesignatoryDetails, IfDetails, IfName, IfNameList, Name, PersonalDetails, PersonalDetailsResponse}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.domain.Nino

import scala.concurrent.Future

class IFServiceSpec extends SpecBase with ScalaFutures with BeforeAndAfterEach{

 private val mockIfConnector = mock[IFConnector]

 override lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", s"/personal-details/")
  val nino = "AA999999A"
 override protected def beforeEach(): Unit = {
  Mockito.reset(mockIfConnector)
 }

 "getPersonalDetails" must {

   "when IF returns successful response" in {

     val expectedObj = Some(IfDesignatoryDetails(
       details = IfDetails(None),
       nameList = IfNameList(Seq(
         IfName(
           firstForename = Some("John"),
           surname = Some("Johnson"),
         ))),
       addressList = IfAddressList(Seq()
     )))
     val contactDetailsResponse = IFContactDetails(None)

     when(mockIfConnector.getDesignatoryDetails(any())(any()))
       .thenReturn(Future.successful(expectedObj))

     when(mockIfConnector.getContactDetails(any()))
       .thenReturn(Future.successful(Some(contactDetailsResponse)))

     val service = new IFService(mockIfConnector)

     val expected = PersonalDetailsResponse(
       details = PersonalDetails(
         name = Some(Name(
           firstForename = Some("John"),
           surname = Some("Johnson")
         )))
     )

     service.getPersonalDetails(Some(Nino(nino))).futureValue shouldBe expected
   }

    "must return most recent known-as name if there is there is no real name" in {

    val designatoryDetailsResponse = Some(IfDesignatoryDetails(
     IfDetails(None),
     IfNameList(Seq(
      IfName(
       nameSequenceNumber = Some(1),
       nameType = Some(2),
       firstForename = Some("John"),
       surname = Some("Johnson")
      ),
      IfName(
       nameSequenceNumber = Some(2),
       nameType = Some(2),
       firstForename = Some("Brian"),
       surname = Some("Brianson")
      )
     )),
     IfAddressList(Seq())
    ))

    val contactDetailsResponse = IFContactDetails(None)

    when(mockIfConnector.getDesignatoryDetails(any())(any()))
      .thenReturn(Future.successful(designatoryDetailsResponse))

    when(mockIfConnector.getContactDetails(any()))
      .thenReturn(Future.successful(Some(contactDetailsResponse)))

    val service = new IFService(mockIfConnector)

    val expected = PersonalDetailsResponse(
     details = PersonalDetails(
      name = Some(Name(
       firstForename = Some("Brian"),
       surname = Some("Brianson")
      )))
    )

    service.getPersonalDetails(Some(Nino(nino))).futureValue shouldBe expected
   }

   "must return real name with highest nameSequenceNumber" in {

    val designatoryDetailsResponse = Some(IfDesignatoryDetails(
     IfDetails(None),
     IfNameList(Seq(
      IfName(
       nameSequenceNumber = Some(1),
       nameType = Some(1),
       firstForename = Some("John"),
       surname = Some("Johnson")
      ),
      IfName(
       nameSequenceNumber = Some(2),
       nameType = Some(1),
       firstForename = Some("Brian"),
       surname = Some("Brianson")
      )
     )),
     IfAddressList(Seq())
    ))

    val contactDetailsResponse = IFContactDetails(None)

    when(mockIfConnector.getDesignatoryDetails(any())(any()))
      .thenReturn(Future.successful(designatoryDetailsResponse))

    when(mockIfConnector.getContactDetails(any()))
      .thenReturn(Future.successful(Some(contactDetailsResponse)))

    val service = new IFService(mockIfConnector)

    val expected = PersonalDetailsResponse(
     details = PersonalDetails(
      name = Some(Name(
       firstForename = Some("Brian"),
       surname = Some("Brianson")
      )))
    )

    service.getPersonalDetails(Some(Nino(nino))).futureValue shouldBe expected
   }

   "must return no name or address if no name or address is returned" in {

    val designatoryDetailsResponse = Some(IfDesignatoryDetails(
     IfDetails(None),
     IfNameList(Seq()),
     IfAddressList(Seq())
    ))

    val contactDetailsResponse = IFContactDetails(None)

    when(mockIfConnector.getDesignatoryDetails(any())(any()))
      .thenReturn(Future.successful(designatoryDetailsResponse))

    when(mockIfConnector.getContactDetails(any()))
      .thenReturn(Future.successful(Some(contactDetailsResponse)))

    val service = new IFService(mockIfConnector)

    val expected = PersonalDetailsResponse()

    service.getPersonalDetails(Some(Nino(nino))).futureValue shouldBe expected
   }

   "must return most recent residential and correspondence addresses" in {

    val designatoryDetailsResponse = Some(IfDesignatoryDetails(
     IfDetails(None),
     IfNameList(Seq()),
     IfAddressList(Seq(
      IfAddress(
       addressLine1 = Some("Residential 1"),
       addressSequenceNumber = Some(1),
       addressType = Some(1)),
      IfAddress(
       addressLine1 = Some("Residential 2"),
       addressSequenceNumber = Some(2),
       addressType = Some(1)),
      IfAddress(
       addressLine1 = Some("Correspondence 1"),
       addressSequenceNumber = Some(3),
       addressType = Some(2)),
      IfAddress(
       addressLine1 = Some("Correspondence 2"),
       addressSequenceNumber = Some(4),
       addressType = Some(2)),
     ))
    ))


    val contactDetailsResponse = IFContactDetails(None)

    when(mockIfConnector.getDesignatoryDetails(any())(any()))
      .thenReturn(Future.successful(designatoryDetailsResponse))

    when(mockIfConnector.getContactDetails(any()))
      .thenReturn(Future.successful(Some(contactDetailsResponse)))

    val service = new IFService(mockIfConnector)

    val expected = PersonalDetailsResponse(
     residentialAddress = Some(Address(addressLine1 = Some("Residential 2"))),
     correspondenceAddress = Some(Address(addressLine1 = Some("Correspondence 2")))
    )

    service.getPersonalDetails(Some(Nino(nino))).futureValue shouldBe expected
   }


   "must prioritise daytime phone number if returned" in {

    val designatoryDetailsResponse = Some(IfDesignatoryDetails(
     IfDetails(None),
     IfNameList(Seq()),
     IfAddressList(Seq())))

    val contactDetailsResponse = IFContactDetails(Some(
     Seq(
      IFContactDetail(9, "MOBILE TELEPHONE", "07123 987654"),
      IFContactDetail(7, "DAYTIME TELEPHONE", "01613214567"),
      IFContactDetail(8, "EVENING TELEPHONE", "01619873210")
     )
    ))

    when(mockIfConnector.getDesignatoryDetails(any())(any()))
      .thenReturn(Future.successful(designatoryDetailsResponse))

    when(mockIfConnector.getContactDetails(any()))
      .thenReturn(Future.successful(Some(contactDetailsResponse)))

    val service = new IFService(mockIfConnector)

    val expected = PersonalDetailsResponse(
     contactDetails = Some(ContactDetails(
      phoneNumber = Some("01613214567")
     ))
    )

    service.getPersonalDetails(Some(Nino(nino))).futureValue shouldBe expected
   }

   "must prioritise evening phone number if no daytime phone number returned" in {

    val designatoryDetailsResponse = Some(IfDesignatoryDetails(
     IfDetails(None),
     IfNameList(Seq()),
     IfAddressList(Seq())))

    val contactDetailsResponse = IFContactDetails(Some(
     Seq(
      IFContactDetail(8, "EVENING TELEPHONE", "01619873210"),
      IFContactDetail(9, "MOBILE TELEPHONE", "07123 987654"),
     )
    ))

    when(mockIfConnector.getDesignatoryDetails(any())(any()))
      .thenReturn(Future.successful(designatoryDetailsResponse))

    when(mockIfConnector.getContactDetails(any()))
      .thenReturn(Future.successful(Some(contactDetailsResponse)))

    val service = new IFService(mockIfConnector)

    val expected = PersonalDetailsResponse(
     contactDetails = Some(ContactDetails(
      phoneNumber = Some("01619873210")
     ))
    )

    service.getPersonalDetails(Some(Nino(nino))).futureValue shouldBe expected
   }

   "must return mobile phone number if no other phone number returned" in {

    val designatoryDetailsResponse = Some(IfDesignatoryDetails(
     IfDetails(None),
     IfNameList(Seq()),
     IfAddressList(Seq())))

    val contactDetailsResponse = IFContactDetails(Some(
     Seq(
      IFContactDetail(9, "MOBILE TELEPHONE", "07123 987654")
     )
    ))

    when(mockIfConnector.getDesignatoryDetails(any())(any()))
      .thenReturn(Future.successful(designatoryDetailsResponse))

    when(mockIfConnector.getContactDetails(any()))
      .thenReturn(Future.successful(Some(contactDetailsResponse)))

    val service = new IFService(mockIfConnector)

    val expected = PersonalDetailsResponse(
     contactDetails = Some(ContactDetails(
      phoneNumber = Some("07123 987654")
     ))
    )

    service.getPersonalDetails(Some(Nino(nino))).futureValue shouldBe expected
   }


   "must prioritise primary e-mail address over secondary e-mail address" in {

    val designatoryDetailsResponse = Some(IfDesignatoryDetails(
     IfDetails(None),
     IfNameList(Seq()),
     IfAddressList(Seq())))

    val contactDetailsResponse = IFContactDetails(Some(
     Seq(
      IFContactDetail(11, "PRIMARY E-MAIL", "fred.blogs@hotmail.com"),
      IFContactDetail(12, "SECONDARY E-MAIL", "john.blogs@hotmail.com")
     )
    ))

    when(mockIfConnector.getDesignatoryDetails(any())(any()))
      .thenReturn(Future.successful(designatoryDetailsResponse))

    when(mockIfConnector.getContactDetails(any()))
      .thenReturn(Future.successful(Some(contactDetailsResponse)))

    val service = new IFService(mockIfConnector)

    val expected = PersonalDetailsResponse(
     contactDetails = Some(ContactDetails(
      email = Some("fred.blogs@hotmail.com")
     ))
    )

    service.getPersonalDetails(Some(Nino(nino))).futureValue shouldBe expected
   }

   "must return secondary e-mail address if no primary e-mail address returned" in {

    val designatoryDetailsResponse = Some(IfDesignatoryDetails(
     IfDetails(None),
     IfNameList(Seq()),
     IfAddressList(Seq())))

    val contactDetailsResponse = IFContactDetails(Some(
     Seq(
      IFContactDetail(12, "SECONDARY E-MAIL", "john.blogs@hotmail.com")
     )
    ))

    when(mockIfConnector.getDesignatoryDetails(any())(any()))
      .thenReturn(Future.successful(designatoryDetailsResponse))

    when(mockIfConnector.getContactDetails(any()))
      .thenReturn(Future.successful(Some(contactDetailsResponse)))

    val service = new IFService(mockIfConnector)

    val expected = PersonalDetailsResponse(
     contactDetails = Some(ContactDetails(
      email = Some("john.blogs@hotmail.com")
     ))
    )

    service.getPersonalDetails(Some(Nino(nino))).futureValue shouldBe expected
   }

  "must return empty respone" in {


   when(mockIfConnector.getDesignatoryDetails(any())(any()))
     .thenReturn(Future.successful(None))

   when(mockIfConnector.getContactDetails(any()))
     .thenReturn(Future.successful(None))

   val service = new IFService(mockIfConnector)

   val expected = PersonalDetailsResponse()
   service.getPersonalDetails(Some(Nino(nino))).futureValue shouldBe expected
  }
  }
}
