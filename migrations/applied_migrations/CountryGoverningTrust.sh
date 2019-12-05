#!/bin/bash

echo ""
echo "Applying migration CountryGoverningTrust"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /countryGoverningTrust                        controllers.register.CountryGoverningTrustController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /countryGoverningTrust                        controllers.register.CountryGoverningTrustController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeCountryGoverningTrust                  controllers.register.CountryGoverningTrustController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeCountryGoverningTrust                  controllers.register.CountryGoverningTrustController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "countryGoverningTrust.title = countryGoverningTrust" >> ../conf/messages.en
echo "countryGoverningTrust.heading = countryGoverningTrust" >> ../conf/messages.en
echo "countryGoverningTrust.checkYourAnswersLabel = countryGoverningTrust" >> ../conf/messages.en
echo "countryGoverningTrust.error.required = Enter countryGoverningTrust" >> ../conf/messages.en
echo "countryGoverningTrust.error.length = CountryGoverningTrust must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCountryGoverningTrustUserAnswersEntry: Arbitrary[(CountryGoverningTrustPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[CountryGoverningTrustPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCountryGoverningTrustPage: Arbitrary[CountryGoverningTrustPage.type] =";\
    print "    Arbitrary(CountryGoverningTrustPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(CountryGoverningTrustPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def countryGoverningTrust: Option[AnswerRow] = userAnswers.get(CountryGoverningTrustPage) map {";\
     print "    x => AnswerRow(\"countryGoverningTrust.checkYourAnswersLabel\", s\"$x\", false, routes.CountryGoverningTrustController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration CountryGoverningTrust completed"
