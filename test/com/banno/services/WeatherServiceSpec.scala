package com.banno.services

import com.banno.values.ServiceError
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.mockito.Mockito._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import scala.concurrent.Future

class WeatherServiceSpec extends PlaySpec with MockitoSugar{

  "WeatherService " should {
    "return the correct weather forecast period" in {
      val weatherGridProperties = WeatherGridProperties(7, 9, "gridId")

      val mockWeatherClient = mock[WeatherClient]
      when(mockWeatherClient.getWeatherGrid(2.5f, 5.5f)).thenReturn(
        Future.successful(Right(weatherGridProperties))
      )

      when(mockWeatherClient.getWeatherForecast(weatherGridProperties)).thenReturn(
        Future.successful(Right(
          WeatherClientForecast(WeatherClientForecastProperties(
              Seq(
                WeatherClientForecastPeriod(3, 32, "wrongThing"),
                WeatherClientForecastPeriod(1, 32, "shortForecast"),
                WeatherClientForecastPeriod(5, 32, "alsoWrong")
              )
          ))
        ))
      )

      val weatherService = new DefaultWeatherService(mockWeatherClient)
      weatherService.getCurrentForecast(latitude = 2.5f, longitude = 5.5f) map {
        case Left(err) => assert(false)
        case Right(forecast) => assert(forecast == Forecast("shortForecast", 32, "temperatureCharacterization"))
      }
    }

    "return an error if getting the weather grid errors out" in {
      val weatherGridProperties = WeatherGridProperties(7, 9, "gridId")

      val mockWeatherClient = mock[WeatherClient]
      when(mockWeatherClient.getWeatherGrid(2.5f, 5.5f)).thenReturn(
        Future.successful(Left(ServiceError("a service error")))
      )

      val weatherService = new DefaultWeatherService(mockWeatherClient)
      weatherService.getCurrentForecast(latitude = 2.5f, longitude = 5.5f) map {
        case Left(err) => assert(err.errorMessage == "a service error")
        case Right(forecast) => assert(false)
      }
    }

    "return an error if getting the weather forecast errors out" in {
      val weatherGridProperties = WeatherGridProperties(7, 9, "gridId")

      val mockWeatherClient = mock[WeatherClient]
      when(mockWeatherClient.getWeatherGrid(2.5f, 5.5f)).thenReturn(
        Future.successful(Right(weatherGridProperties))
      )

      when(mockWeatherClient.getWeatherForecast(weatherGridProperties)).thenReturn(
        Future.successful(Left(
         ServiceError("a service error")
        ))
      )

      val weatherService = new DefaultWeatherService(mockWeatherClient)
      weatherService.getCurrentForecast(latitude = 2.5f, longitude = 5.5f) map {
        case Left(err) => assert(err.errorMessage == "a service error")
        case Right(forecast) => assert(false)
      }
    }
  }

}
