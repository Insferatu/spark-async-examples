package com.insferatu.service

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.insferatu.model.{EnrichedWeatherMeasurement, WeatherMeasurement}
import org.http4s.blaze.client.BlazeClientBuilder

object PredictionProviderSingleton {
//  @transient val clientResource = BlazeClientBuilder[IO].resource
  @transient val predictionProviderIO = PredictionProvider.instance[IO]

  def processRows(rows: Iterator[WeatherMeasurement]): Iterator[EnrichedWeatherMeasurement] = {
    BlazeClientBuilder[IO].resource.use { httpClient =>
      for {
        predictionProvider <- predictionProviderIO
        enrichedMeasurements <- predictionProvider.processRows(httpClient, rows.toList)
      } yield enrichedMeasurements.iterator
    }.unsafeRunSync()
  }
}
