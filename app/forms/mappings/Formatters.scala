/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms.mappings

import forms.Validation
import models.Enumerable
import play.api.data.FormError
import play.api.data.format.Formatter

import scala.util.control.Exception.nonFatalCatch

trait Formatters {

  private[mappings] def ninoFormatter(errorKey: String): Formatter[String] = new Formatter[String] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] =
      data.get(key) match {
        case None | Some("") => Left(Seq(FormError(key, errorKey)))
        case Some(s) => Right(s.trim().replace(" ","").toUpperCase())
    }

    override def unbind(key: String, value: String): Map[String, String] =
      Map(key -> value)
  }


  private[mappings] def stringFormatter(errorKey: String): Formatter[String] = new Formatter[String] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] =
      data.get(key) match {
        case None | Some("") => Left(Seq(FormError(key, errorKey)))
        case Some(s) => Right(s.trim.replace("‘", "'").replace("’", "'"))
      }

    override def unbind(key: String, value: String): Map[String, String] =
      Map(key -> value)
  }

  private[mappings] def utrFormatter(requiredKey: String, invalidKey: String, lengthKey: String): Formatter[String] = new Formatter[String] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
      val fixedLength = 10
      val trimmedUtr = data.get(key).map(s => s.replaceAll("\\s",""))
      trimmedUtr match {
        case None | Some("") => Left(Seq(FormError(key, requiredKey)))
        case Some(s) if s.length != fixedLength => Left(Seq(FormError(key, lengthKey, Seq(fixedLength))))
        case Some(s) if !s.matches(Validation.utrRegex) => Left(Seq(FormError(key, invalidKey)))
        case Some(s) => Right(s)
      }
    }

    override def unbind(key: String, value: String): Map[String, String] =
      Map(key -> value)
  }

  private[mappings] def postcodeFormatter(requiredKey: String, invalidKey : String): Formatter[String] = new Formatter[String] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] =
      data.get(key) match {
        case None | Some("") => Left(Seq(FormError(key, requiredKey)))
        case Some(s) =>
          val trimmed = s.trim.toUpperCase
            trimmed.matches(Validation.postcodeRegex) match {
            case true => Right(trimmed)
            case false => Left(Seq(FormError(key, invalidKey)))
          }
      }

    override def unbind(key: String, value: String): Map[String, String] =
      Map(key -> value)
  }

  private[mappings] def currencyFormatter(requiredKey: String, invalidKey : String): Formatter[String] = new Formatter[String] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] =
      data.get(key) match {
        case None | Some("") => Left(Seq(FormError(key, requiredKey)))
        case Some(s) =>
          val trimmed = s.trim.replaceAll(",", "")
          Right(trimmed)
      }

    override def unbind(key: String, value: String): Map[String, String] =
      Map(key -> value)
  }

  private[mappings] def booleanFormatter(requiredKey: String, invalidKey: String): Formatter[Boolean] =
    new Formatter[Boolean] {

      private val baseFormatter = stringFormatter(requiredKey)

      override def bind(key: String, data: Map[String, String]) =
        baseFormatter
          .bind(key, data)
          .flatMap {
          case "true" => Right(true)
          case "false" => Right(false)
          case _ => Left(Seq(FormError(key, invalidKey)))
        }

      def unbind(key: String, value: Boolean) = Map(key -> value.toString)
    }

  private[mappings] def intFormatter(requiredKey: String, wholeNumberKey: String, nonNumericKey: String): Formatter[Int] =
    new Formatter[Int] {

      val decimalRegexp = """^-?(\d*\.\d*)$"""

      private val baseFormatter = stringFormatter(requiredKey)

      override def bind(key: String, data: Map[String, String]) =
        baseFormatter
          .bind(key, data)
          .map(_.replace(",", ""))
          .flatMap {
          case s if s.matches(decimalRegexp) =>
            Left(Seq(FormError(key, wholeNumberKey)))
          case s =>
            nonFatalCatch
              .either(s.toInt)
              .left.map(_ => Seq(FormError(key, nonNumericKey)))
        }

      override def unbind(key: String, value: Int) =
        baseFormatter.unbind(key, value.toString)
    }

  private[mappings] def longFormatter(requiredKey: String, wholeNumberKey: String, nonNumericKey: String): Formatter[Long] =
    new Formatter[Long] {

      val decimalRegexp = """^-?(\d*\.\d*)$"""

      private val baseFormatter = stringFormatter(requiredKey)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Long] =
        baseFormatter
          .bind(key, data)
          .map(_.replace(",", ""))
          .flatMap {
          case s if s.matches(decimalRegexp) =>
            Left(Seq(FormError(key, wholeNumberKey)))
          case s =>
            nonFatalCatch
              .either(s.toLong)
              .left.map(_ => Seq(FormError(key, nonNumericKey)))
        }

      override def unbind(key: String, value: Long): Map[String, String] =
        baseFormatter.unbind(key, value.toString)
    }

  private[mappings] def enumerableFormatter[A](requiredKey: String, invalidKey: String)(implicit ev: Enumerable[A]): Formatter[A] =
    new Formatter[A] {

      private val baseFormatter = stringFormatter(requiredKey)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], A] =
        {
          baseFormatter.bind(key, data).flatMap {
            str =>
              ev.withName(str).map(Right.apply).getOrElse(Left(Seq(FormError(key, invalidKey))))
          }
        }

      override def unbind(key: String, value: A): Map[String, String] =
        baseFormatter.unbind(key, value.toString)
    }

  private[mappings] def longValueFormatter(requiredKey: String,
                                           wholeNumberKey: String,
                                           invalidKey: String,
                                           maxValueKey: String,
                                           minValueKey: String,
                                           maxValue: Long): Formatter[Long] =
    new Formatter[Long] {

      private val baseFormatter = longFormatter(requiredKey, wholeNumberKey, invalidKey)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Long] = {
        val baseValue = baseFormatter.bind(key, data)
        baseValue match {
          case Right(value) if value < 1L => Left(Seq(FormError(key, minValueKey)))
          case Right(value) if value >= maxValue => Left(Seq(FormError(key, maxValueKey)))
          case _ => baseValue
        }
      }
      override def unbind(key: String, value: Long): Map[String, String] = baseFormatter.unbind(key, value)
    }
}
