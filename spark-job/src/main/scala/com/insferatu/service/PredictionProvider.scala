package com.insferatu.service

import cats.Parallel
import cats.effect.Resource
import cats.effect.syntax.concurrent._
import cats.effect.kernel.Concurrent
import com.insferatu.model.{EnrichedWeatherMeasurement, WeatherMeasurement}
import org.http4s.client.Client
import cats.syntax.functor._
import cats.syntax.parallel._
import cats.syntax.applicative._
import cats.instances.list._
import io.circe.Json
import io.circe.syntax._
import io.circe.Decoder._
import io.circe.Encoder._
import org.http4s.{Headers, Method, Request, Uri}
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._

trait PredictionProvider[F[_]] {
  def processRows(httpClient: Client[F], measurements: List[WeatherMeasurement]): F[List[EnrichedWeatherMeasurement]]
}

case class ConcretePredictionProvider[F[_]: Concurrent: Parallel](uri: Uri) extends PredictionProvider[F] {
  override def processRows(httpClient: Client[F], measurements: List[WeatherMeasurement]): F[List[EnrichedWeatherMeasurement]] = {
    measurements.parTraverse { measurement =>
      val req =
        Request[F](method = Method.GET, uri = uri / "prediction" / "predict")
          .withEntity(
            Json.obj(
              "temperature" -> measurement.temperature.asJson,
              "humidity" -> measurement.humidity.asJson,
              "pressure" -> measurement.pressure.asJson
            )
          )
      for {
        a <- httpClient.expect[Json](req)
        probabilityOfRain = a.hcursor.downField("probabilityOfRain").as[Double].getOrElse(0d)
      } yield EnrichedWeatherMeasurement(measurement, probabilityOfRain)
    }
  }
}

object PredictionProvider {
  def instance[F[_]: Concurrent: Parallel]: F[PredictionProvider[F]] = {
    ConcretePredictionProvider[F](Uri.unsafeFromString("http://localhost:8080")).pure[F].widen[PredictionProvider[F]]
  }
}
