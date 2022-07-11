package com.insferatu.service

import cats.effect.{Sync, Temporal}
import cats.effect.kernel.Async
import com.insferatu.model.{PredictionInput, PredictionResult}
import cats.syntax.flatMap._
import cats.syntax.functor._

import scala.util.Random
import scala.concurrent.duration._

trait Predictor[F[_]] {
  def predict(predictionInput: PredictionInput): F[PredictionResult]
}

case class RandomPredictor[F[_]: Async](random: Random) extends Predictor[F] {
  override def predict(predictionInput: PredictionInput): F[PredictionResult] = {
    for {
      randomDuration <- Sync[F].delay(random.between(500, 2000))
      _ <- Temporal[F].sleep(randomDuration.milliseconds)
      randomPrediction <- Sync[F].delay(random.between(0d, 1d))
    } yield PredictionResult(randomPrediction)
  }
}

object Predictor {
  private val randomSeed = 3845147L

  def instance[F[_]: Async]: F[Predictor[F]] =
    for {
      random <- Sync[F].delay(new Random(randomSeed))
    } yield RandomPredictor(random)
}
