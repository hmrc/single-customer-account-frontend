package connectors

import com.google.inject.Inject
import config.FrontendAppConfig
import models.integrationframework.ActionDetails
import play.api.Logging
import play.api.http.Status.{NOT_FOUND, OK}
import play.api.libs.ws.WSClient
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderNames

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class ActionsConnector @Inject()(
                                  val wsClient: WSClient,
                                  appConfig: FrontendAppConfig
                                )(implicit executionContext: ExecutionContext) extends Logging {

  private def setHeaders = Seq(
    (HeaderNames.authorisation, s"Bearer ${appConfig.integrationFrameworkAuthToken}"),
    "Environment" -> appConfig.integrationFrameworkEnvironment,
    "CorrelationId" -> UUID.randomUUID().toString
  )

  def getActionDetails(nino: Nino): Future[Seq[ActionDetails]] = {

    wsClient.url(s"${appConfig.capabilitiesDataBaseUrl}/single-customer-account-capabilities/actions/${nino.nino}")
      .withHttpHeaders(setHeaders: _*)
      .get().map {
      case response if response.status >= OK && response.status < 300 =>
        logger.info(s"[ActionsConnector][getActionDetails] IF successful response code: ${response.status}")
        response.json.as[Seq[ActionDetails]]
      case response if response.status == NOT_FOUND =>
        logger.info("[ActionsConnector][getActionDetails] IF returned code 404 NOT FOUND")
        Seq.empty
      case response =>
        logger.warn(s"[ActionsConnector][getActionDetails] IF returned unknown code: ${response.status}")
        Seq.empty
    }.recover {
      case ex: Exception =>
        logger.error(s"[ActionsConnector][getActionDetails] exception: ${ex.getMessage}")
        Seq.empty
    }

  }

}
