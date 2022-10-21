package controllers

import com.banno.services.{Forecast, WeatherService}
import com.banno.values.ServiceError
import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import play.api.libs.json.Json

import scala.concurrent.Future

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class HomeControllerSpec extends PlaySpec with MockitoSugar{

  "HomeController GET" should {

    "return forecast json when forecast is returned from weather service" in {
      val mockWeatherService = mock[WeatherService]
      when(mockWeatherService.getCurrentForecast(latitude = 2.5f, longitude = 5.0f)).thenReturn(
        Future.successful(Right(Forecast("shortDescription", 32, "temperatureCharacterization")))
      )
      val controller = new HomeController(stubControllerComponents(), mockWeatherService)
      val result = controller.index(latitude = 2.5f, longitude =  5.0f).apply(FakeRequest(GET, "/"))

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe Json.obj(
        "shortDescription" -> "shortDescription",
        "temperature" -> 32,
        "temperatureCharacterization" -> "temperatureCharacterization"
      )
    }

    "return error json when error is returned from weather service" in {
      val mockWeatherService = mock[WeatherService]
      when(mockWeatherService.getCurrentForecast(latitude = 2.5f, longitude = 5.0f)).thenReturn(
        Future.successful(Left(ServiceError("a service error")))
      )
      val controller = new HomeController(stubControllerComponents(), mockWeatherService)
      val result = controller.index(latitude = 2.5f, longitude =  5.0f).apply(FakeRequest(GET, "/"))

      status(result) mustBe INTERNAL_SERVER_ERROR
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe Json.obj(
        "error" -> "a service error"
      )
    }

  }
}
