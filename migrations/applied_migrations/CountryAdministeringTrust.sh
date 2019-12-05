#!/bin/bash

echo ""
echo "Applying migration CountryAdministeringTrust"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /countryAdministeringTrust                        controllers.register.CountryAdministeringTrustController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /countryAdministeringTrust                        controllers.register.CountryAdministeringTrustController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeCountryAdministeringTrust                  controllers.register.CountryAdministeringTrustController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeCountryAdministeringTrust                  controllers.register.CountryAdministeringTrustController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "countryAdministeringTrust.title = countryAdministeringTrust" >> ../conf/messages.en
echo "countryAdministeringTrust.heading = countryAdministeringTrust" >> ../conf/messages.en
echo "countryAdministeringTrust.checkYourAnswersLabel = countryAdministeringTrust" >> ../conf/messages.en
echo "countryAdministeringTrust.error.required = Enter countryAdministeringTrust" >> ../conf/messages.en
echo "countryAdministeringTrust.error.length = CountryAdministeringTrust must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCountryAdministeringTrustUserAnswersEntry: Arbitrary[(CountryAdministeringTrustPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[CountryAdministeringTrustPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCountryAdministeringTrustPage: Arbitrary[CountryAdministeringTrustPage.type] =";\
    print "    Arbitrary(CountryAdministeringTrustPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(CountryAdministeringTrustPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def countryAdministeringTrust: Option[AnswerRow] = userAnswers.get(CountryAdministeringTrustPage) map {";\
     print "    x => AnswerRow(\"countryAdministeringTrust.checkYourAnswersLabel\", s\"$x\", false, routes.CountryAdministeringTrustController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration CountryAdministeringTrust completed"
