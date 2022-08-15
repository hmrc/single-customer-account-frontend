package controllers

import fixtures.SpecBase
import connectors.CharitiesShortLivedCache
import controllers.actions.{AuthAction, AuthActionImpl, AuthIdentifierAction, CitizenDetailsAction, CitizenDetailsActionImpl, FakeAuthIdentifierAction}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._

class HomeControllerSpec extends SpecBase with MockitoSugar {
  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[AuthAction].to[AuthActionImpl],
        bind[CitizenDetailsAction].to[CitizenDetailsActionImpl]
      )
  lazy val controller: HomeController = inject[HomeController]

}
