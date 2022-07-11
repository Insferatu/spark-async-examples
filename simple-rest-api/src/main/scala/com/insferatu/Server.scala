package com.insferatu

import cats.effect.Async
import org.http4s.server.Router
import org.http4s.blaze.server.BlazeServerBuilder
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.insferatu.monitoring.MetricsRepository
import com.insferatu.service.Predictor
import io.chrisdavenport.epimetheus.CollectorRegistry

object Server {
  def start[F[_] : Async]: F[Unit] = {
    for {
      registry <- CollectorRegistry.build[F]
      metrics <- MetricsRepository.instance(registry)
      predictor <- Predictor.instance
      routes <- Routes.instance(metrics)
      httpApp = Router(
        "/metrics" -> routes.metricsRoutes,
        "/prediction" -> routes.predictionRoutes(predictor)
      ).orNotFound
      _ <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(httpApp)
        .serve
        .compile
        .drain
    } yield ()
  }
}
