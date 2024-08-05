package connectors

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.concurrent.ScalaFutures
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, UpstreamErrorResponse}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.play.bootstrap.http.DefaultHttpClient

import scala.concurrent.{ExecutionContext, Future}
import cats.data.EitherT
import fixtures.WireMockHelper

class CarbonIntensityDataConnectorSpec extends AnyWordSpec with Matchers with ScalaFutures with WireMockHelper {

//  override lazy val app: Application = new GuiceApplicationBuilder()
//    .configure("microservice.services.carbon-intensity.port" -> server.port())
//    .build()

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  val fromDate: String = "2023-05-15T12:00Z"
  val toDate: String = "2023-05-16T12:00Z"
  val postcode: String = "LS2" // HANDLE FULLPOST CODE - API returns 200 with null when full postcode is passed
  val carbonIntensityUrl: String = s"/regional/intensity/$fromDate/$toDate/postcode/$postcode"

  lazy val carbonIntensityDataConnector: CarbonIntensityDataConnector = {
    val httpClient: HttpClientV2 = inject[HttpClientV2]
    val httpClientResponse: HttpClientResponse = inject[HttpClientResponse]
    new CarbonIntensityDataConnector(httpClient, appConfig, httpClientResponse)
  }

  def stubGet(url: String, responseStatus: Int, responseBody: Option[String]): StubMapping = server.stubFor {
    val baseResponse = aResponse().withStatus(responseStatus).withHeader(CONTENT_TYPE, JSON)
    val response = responseBody.fold(baseResponse)(body => baseResponse.withBody(body))
    get(url).willReturn(response)
  }

  // TODO: WIP
  "Calling getCarbonIntensityData" must {

    "return OK when called with valid parameters" in {
      val responseBody = Json.obj("data" -> "test").toString()
      stubGet(carbonIntensityUrl, OK, Some(responseBody))

      val result: Either[UpstreamErrorResponse, HttpResponse] =
        carbonIntensityDataConnector.getCarbonIntensityData(fromDate, toDate, postcode).value.futureValue

      result mustBe a[Right[_, _]]
      result.getOrElse(HttpResponse(BAD_REQUEST, "")).status mustBe OK
    }

    "return NOT_FOUND when data is not found for given parameters" in {
      stubGet(carbonIntensityUrl, NOT_FOUND, None)

      val result = carbonIntensityDataConnector
        .getCarbonIntensityData(fromDate, toDate, postcode)
        .value
        .futureValue

      result mustBe a[Left[_, _]]
      result.swap.getOrElse(UpstreamErrorResponse("", OK)).statusCode mustBe NOT_FOUND
    }

    "return given status code when an unexpected status is returned" in {
      stubGet(carbonIntensityUrl, IM_A_TEAPOT, None)

      val result = carbonIntensityDataConnector
        .getCarbonIntensityData(fromDate, toDate, postcode)
        .value
        .futureValue

      result mustBe a[Left[_, _]]
      result.swap.getOrElse(UpstreamErrorResponse("", OK)).statusCode mustBe IM_A_TEAPOT
    }
  }
}
