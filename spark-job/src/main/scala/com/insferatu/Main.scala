package com.insferatu

import com.insferatu.model.WeatherMeasurement
import com.insferatu.service.PredictionProviderSingleton
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{DoubleType, IntegerType, MapType, StringType, StructField, StructType, TimestampType}

object Main {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .config("spark.executor.cores", 2)
      .getOrCreate

    import spark.implicits._

    val schema = StructType(
      Array(
        StructField("formattedDate", TimestampType, nullable = true),
        StructField("summary", StringType, nullable = true),
        StructField("precipType", StringType, nullable = true),
        StructField("temperature", DoubleType, nullable = true),
        StructField("apparentTemperature", DoubleType, nullable = true),
        StructField("humidity", DoubleType, nullable = true),
        StructField("windSpeed", DoubleType, nullable = true),
        StructField("windBearing", DoubleType, nullable = true),
        StructField("visibility", DoubleType, nullable = true),
        StructField("loudCover", DoubleType, nullable = true),
        StructField("pressure", DoubleType, nullable = true),
        StructField("dailySummary", StringType, nullable = true)
      )
    )

    spark.read
      .option("header", true)
      .schema(schema)
      .csv("weatherHistory.csv")
      .as[WeatherMeasurement]
      .mapPartitions(PredictionProviderSingleton.processRows)
      .show(10, false)
//      .printSchema()
//      .describe()
//      .show(100, false)

  }
}
