package com.banno.services

import com.banno.values.ServiceError
import com.google.inject.ImplementedBy
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json, Reads}
import play.api.libs.ws.WSClient

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[NationalWeatherServiceClient])
trait WeatherClient {
  def getWeatherGrid(latitude: Float, longitude: Float): Future[Either[ServiceError, WeatherGridProperties]]
  def getWeatherForecast(gridProperties: WeatherGridProperties): Future[Either[ServiceError, WeatherClientForecast]]
}

@Singleton
class NationalWeatherServiceClient @Inject()(wsClient: WSClient) extends WeatherClient{

  override def getWeatherGrid(latitude: Float, longitude: Float): Future[Either[ServiceError, WeatherGridProperties]] = {
    val url = s"https://api.weather.gov/points/$latitude,$longitude"

    wsClient.url(url).get() map {
      response => {
        if(response.status == 200) {
          val json = response.json
          parseJson[GetGridPointResponse](json)
            .map(_.properties)
        } else {
          Left(ServiceError(response.body, Some(response.status)))
        }
      }
    }

  }

  override def getWeatherForecast(gridProperties: WeatherGridProperties): Future[Either[ServiceError, WeatherClientForecast]] = {
    val (office, x, y) = (gridProperties.gridId, gridProperties.gridX, gridProperties.gridY)
    val url = s"https://api.weather.gov/gridpoints/$office/$x,$y/forecast"
    wsClient.url(url).get() map {
      response => {
        if(response.status == 200) {
          val json = response.json
          parseJson[WeatherClientForecast](json)
        } else {
          Left(ServiceError(response.body, Some(response.status)))
        }
      }
    }

  }

  private def parseJson[T](jsValue: JsValue)(implicit reads: Reads[T]): Either[ServiceError, T] = {
    Json.fromJson[T](jsValue) match {
      case JsSuccess(value, _) => Right(value)
      case JsError(errors) => Left(new ServiceError(errors.mkString))
    }
  }

}

case class WeatherClientForecast(properties: WeatherClientForecastProperties)
object WeatherClientForecast {
  implicit val reads: Reads[WeatherClientForecast] = Json.reads[WeatherClientForecast]
}

case class WeatherClientForecastProperties(periods: Seq[WeatherClientForecastPeriod])
object WeatherClientForecastProperties {
  implicit val reads: Reads[WeatherClientForecastProperties] = Json.reads[WeatherClientForecastProperties]
}

case class WeatherClientForecastPeriod(number: Int, temperature: Int, shortForecast: String)
object WeatherClientForecastPeriod {
  implicit val reads: Reads[WeatherClientForecastPeriod] = Json.reads[WeatherClientForecastPeriod]
}

case class GetGridPointResponse(properties: WeatherGridProperties)
object GetGridPointResponse {
  implicit val reads: Reads[GetGridPointResponse] = Json.reads[GetGridPointResponse]
}

case class WeatherGridProperties(gridX: Int, gridY: Int, gridId: String)
object WeatherGridProperties {
  implicit val reads: Reads[WeatherGridProperties] = Json.reads[WeatherGridProperties]
}
