package com.banno.services

import org.mockito.ArgumentMatchers.any
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.mockito.Mockito._
import play.api.libs.json.{JsArray, Json}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WeatherClientSpec extends PlaySpec with MockitoSugar{
  "WeatherClient getWeatherGrid " should {
    "return the correct grid properties" in {
      val mockWSClient = mock[WSClient]
      val mockWSRequest = mock[WSRequest]
      val mockWSResponse = mock[WSResponse]

      when(mockWSResponse.status).thenReturn(200)
      when(mockWSResponse.json).thenReturn(
        Json.obj("properties" ->
          Json.obj("gridX" -> 7, "gridY" -> 9, "gridId" -> "gridId")
        )
      )

      when(mockWSClient.url(any[String])).thenReturn(mockWSRequest)
      when(mockWSRequest.get())
        .thenReturn(Future.successful(mockWSResponse))

      val weatherClient = new NationalWeatherServiceClient(mockWSClient)
      weatherClient.getWeatherGrid(latitude = 2.5f, longitude = 5.5f) map {
        case Left(err) => assert(false)
        case Right(gridProperties) => assert(gridProperties == WeatherGridProperties(7, 9, "gridId"))
      }
    }

    "return an error if json is incorrect (no gridX property)" in {
      val mockWSClient = mock[WSClient]
      val mockWSRequest = mock[WSRequest]
      val mockWSResponse = mock[WSResponse]

      when(mockWSResponse.status).thenReturn(200)
      when(mockWSResponse.json).thenReturn(
        Json.obj("properties" ->
          Json.obj("gridY" -> 9, "gridId" -> "gridId")
        )
      )

      when(mockWSClient.url(any[String])).thenReturn(mockWSRequest)
      when(mockWSRequest.get())
        .thenReturn(Future.successful(mockWSResponse))

      val weatherClient = new NationalWeatherServiceClient(mockWSClient)
      weatherClient.getWeatherGrid(latitude = 2.5f, longitude = 5.5f) map {
        case Left(err) => assert(false)
        case Right(gridProperties) => assert(gridProperties == WeatherGridProperties(7, 9, "gridId"))
      }
    }

    "return an error if client returns an error code" in {
      val mockWSClient = mock[WSClient]
      val mockWSRequest = mock[WSRequest]
      val mockWSResponse = mock[WSResponse]

      when(mockWSResponse.status).thenReturn(500)

      when(mockWSClient.url(any[String])).thenReturn(mockWSRequest)
      when(mockWSRequest.get())
        .thenReturn(Future.successful(mockWSResponse))

      val weatherClient = new NationalWeatherServiceClient(mockWSClient)
      weatherClient.getWeatherGrid(latitude = 2.5f, longitude = 5.5f) map {
        case Left(err) => assert(false)
        case Right(gridProperties) => assert(gridProperties == WeatherGridProperties(7, 9, "gridId"))
      }
    }

  }

  "WeatherClient getWeatherForecast " should {
    "return the correct weather forecast response" in {
      val mockWSClient = mock[WSClient]
      val mockWSRequest = mock[WSRequest]
      val mockWSResponse = mock[WSResponse]

      when(mockWSResponse.status).thenReturn(200)
      when(mockWSResponse.json).thenReturn(
        Json.obj("properties" ->
          Json.obj("periods" ->
            JsArray(
              Seq(Json.obj("temperature" -> 32, "number" -> 3, "shortForecast" -> "shortForecast"))
            )
          )
        )
      )

      when(mockWSClient.url(any[String])).thenReturn(mockWSRequest)
      when(mockWSRequest.get())
        .thenReturn(Future.successful(mockWSResponse))

      val weatherClient = new NationalWeatherServiceClient(mockWSClient)
      weatherClient.getWeatherForecast(WeatherGridProperties(gridX = 7, gridY = 9, "gridId")) map {
        case Left(err) => assert(false)
        case Right(clientForecast) =>
          assert(
            clientForecast.properties.periods == Seq(WeatherClientForecastPeriod(3, 32, "shortForecast"))
          )
      }
    }

    "return an error if json is incorrect (no number property)" in {
      val mockWSClient = mock[WSClient]
      val mockWSRequest = mock[WSRequest]
      val mockWSResponse = mock[WSResponse]

      when(mockWSResponse.status).thenReturn(200)
      when(mockWSResponse.json).thenReturn(
        Json.obj("properties" ->
          Json.obj("periods" ->
            JsArray(
              Seq(Json.obj("temperature" -> 32, "shortForecast" -> "shortForecast"))
            )
          )
        )
      )

      when(mockWSClient.url(any[String])).thenReturn(mockWSRequest)
      when(mockWSRequest.get())
        .thenReturn(Future.successful(mockWSResponse))

      val weatherClient = new NationalWeatherServiceClient(mockWSClient)
      weatherClient.getWeatherForecast(WeatherGridProperties(gridX = 7, gridY = 9, "gridId")) map {
        case Left(err) => assert(true)
        case Right(clientForecast) =>
          assert(false)
      }
    }

    "return an error if the client returns an error code" in {
      val mockWSClient = mock[WSClient]
      val mockWSRequest = mock[WSRequest]
      val mockWSResponse = mock[WSResponse]

      when(mockWSResponse.status).thenReturn(500)

      when(mockWSClient.url(any[String])).thenReturn(mockWSRequest)
      when(mockWSRequest.get())
        .thenReturn(Future.successful(mockWSResponse))

      val weatherClient = new NationalWeatherServiceClient(mockWSClient)
      weatherClient.getWeatherForecast(WeatherGridProperties(gridX = 7, gridY = 9, "gridId")) map {
        case Left(err) => assert(true)
        case Right(clientForecast) => assert(false)
      }
    }
  }

}
