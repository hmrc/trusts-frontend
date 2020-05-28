/*
 * Copyright 2020 HM Revenue & Customs
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

package filters

import akka.stream.Materializer
import com.google.inject.Inject
import config.FrontendAppConfig
import play.api.mvc.{Call, RequestHeader, Result}
import play.api.{Configuration, Logger}
import uk.gov.hmrc.whitelist.AkamaiWhitelistFilter

import scala.concurrent.Future

class WhitelistFilter @Inject() (
                                  config: Configuration,
                                  override val mat: Materializer
                                ) extends AkamaiWhitelistFilter {

  override def whitelist: Seq[String] = {
    config
      .underlying
      .getString("filters.whitelist.ips")
      .split(",")
      .map(_.trim)
      .filter(_.nonEmpty)
  }

  override def destination: Call = {
    val path = config.underlying.getString("filters.whitelist.destination")
    Call("GET", path)
  }

  override def excludedPaths: Seq[Call] = {
    val excludedPaths = config.underlying.getString("filters.whitelist.excluded").split(",")
    Logger.info(s"[WhitelistFilter] excludedPaths $excludedPaths")
    excludedPaths.map {
      path =>
        Call("GET", path.trim)
    }
  }
}

class TrustWhitelistFilter @Inject()(
                                      config: FrontendAppConfig,
                                      override val mat: Materializer
                             ) extends WhitelistFilter(config.configuration, mat) {

  override def apply(f: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {
    if (config.enableWhitelist) {
      super.apply(f)(rh)
    } else {
      f(rh)
    }
  }

}
