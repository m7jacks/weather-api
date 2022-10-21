package controllers

import com.banno.services.WeatherService

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents, weatherService: WeatherService) extends BaseController {

  def index(latitude: Float, longitude: Float) = Action.async { implicit request: Request[AnyContent] =>
    weatherService.getCurrentForecast(latitude, longitude) map {
      case Left(serviceError) => InternalServerError(Json.obj("error" -> serviceError.toString))
      case Right(forecast) => Ok(Json.toJson(forecast))
    }
  }
}
