package com.banno.services

import com.banno.values.ServiceError
import com.google.inject.ImplementedBy
import play.api.libs.json.{Json, Writes}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[DefaultWeatherService])
trait WeatherService {
  def getCurrentForecast(latitude: Float, longitude : Float): Future[Either[ServiceError, Forecast]]
}

@Singleton
class DefaultWeatherService @Inject()(weatherServiceClient: WeatherClient) extends WeatherService {

  override def getCurrentForecast(latitude: Float, longitude : Float): Future[Either[ServiceError, Forecast]] = {
    weatherServiceClient.getWeatherGrid(latitude, longitude) flatMap {
      case Left(clientError) => Future.successful(Left(new ServiceError(s"Returned error from client library ${clientError.errorMessage}")))
      case Right(gridPoint) =>

        //Could cache grid data
        weatherServiceClient.getWeatherForecast(gridPoint) map {
          case Left(clientError) => Left(new ServiceError(s"Returned error from weather client ${clientError.errorMessage}"))
          case Right(clientForecast) =>
            val mostCurrent = clientForecast.properties.periods.minBy(_.number) //might throw exception
            val temperatureCharacterization = getTemperatureCharacterization(mostCurrent.temperature)
            val temperature = mostCurrent.temperature
            Right(Forecast( mostCurrent.shortForecast, temperature, temperatureCharacterization))
        }
    }

  }

  //This assumes temperature is always in Fahrenheit.
  //Also assumes that temperature characteristic is not a set of values passed around a larger program so a simple String and not an Enum is used here
  private def getTemperatureCharacterization(temperature: Int): String = {
    if(temperature > 80){
      "hot"
    } else if(temperature < 60) {
      "cold"
    } else {
      "moderate"
    }
  }

}

case class Forecast (shortDescription: String, temperature: Int, temperatureCharacterization: String)
object Forecast {
  implicit val writes: Writes[Forecast] = Json.writes[Forecast]
}