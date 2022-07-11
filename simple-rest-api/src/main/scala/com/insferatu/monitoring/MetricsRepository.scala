package com.insferatu.monitoring

import cats.effect.Sync
import io.chrisdavenport.epimetheus.{CollectorRegistry, Counter, Gauge, Name}
import cats.syntax.flatMap._
import cats.syntax.functor._

trait MetricsRepository[F[_]] {
  def currentlyProcessingRequests: Gauge[F]
  def successfullyProcessedRequests: Counter[F]
  def getMetrics: F[String]
}

case class PrometheusMetricsRepository[F[_]](
                                              currentlyProcessingRequests: Gauge[F],
                                              successfullyProcessedRequests: Counter[F]
                                            )(private val registry: CollectorRegistry[F])
  extends MetricsRepository[F] {
  def getMetrics: F[String] = registry.write004
}

object MetricsRepository {
  def instance[F[_] : Sync](registry: CollectorRegistry[F]): F[MetricsRepository[F]] =
    for {
      currentlyProcessingRequests <- Gauge.noLabels(registry, Name("currently_processing_requests"), "Hint")
      successfullyProcessedRequests <- Counter.noLabels(registry, Name("successfully_processed_requests"), "Hint")
    } yield PrometheusMetricsRepository(currentlyProcessingRequests, successfullyProcessedRequests)(registry)
}