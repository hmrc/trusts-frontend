#!/bin/bash

echo "Applying migration TrustAddressUK"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trustAddressUK                        controllers.TrustAddressUKController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trustAddressUK                        controllers.TrustAddressUKController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTrustAddressUK                  controllers.TrustAddressUKController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTrustAddressUK                  controllers.TrustAddressUKController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trustAddressUK.title = trustAddressUK" >> ../conf/messages.en
echo "trustAddressUK.heading = trustAddressUK" >> ../conf/messages.en
echo "trustAddressUK.field1 = Field 1" >> ../conf/messages.en
echo "trustAddressUK.field2 = Field 2" >> ../conf/messages.en
echo "trustAddressUK.checkYourAnswersLabel = trustAddressUK" >> ../conf/messages.en
echo "trustAddressUK.error.field1.required = Enter field1" >> ../conf/messages.en
echo "trustAddressUK.error.field2.required = Enter field2" >> ../conf/messages.en
echo "trustAddressUK.error.field1.length = field1 must be 56 characters or less" >> ../conf/messages.en
echo "trustAddressUK.error.field2.length = field2 must be 56 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustAddressUKUserAnswersEntry: Arbitrary[(TrustAddressUKPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TrustAddressUKPage.type]";\
    print "        value <- arbitrary[TrustAddressUK].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustAddressUKPage: Arbitrary[TrustAddressUKPage.type] =";\
    print "    Arbitrary(TrustAddressUKPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustAddressUK: Arbitrary[TrustAddressUK] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield TrustAddressUK(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TrustAddressUKPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def trustAddressUK: Option[AnswerRow] = userAnswers.get(TrustAddressUKPage) map {";\
     print "    x => AnswerRow(\"trustAddressUK.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.TrustAddressUKController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TrustAddressUK completed"