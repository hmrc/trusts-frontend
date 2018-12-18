#!/bin/bash

echo ""
echo "Applying migration TrustResidentInUK"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trustResidentInUK                        controllers.TrustResidentInUKController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trustResidentInUK                        controllers.TrustResidentInUKController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTrustResidentInUK                  controllers.TrustResidentInUKController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTrustResidentInUK                  controllers.TrustResidentInUKController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trustResidentInUK.title = trustResidentInUK" >> ../conf/messages.en
echo "trustResidentInUK.heading = trustResidentInUK" >> ../conf/messages.en
echo "trustResidentInUK.checkYourAnswersLabel = trustResidentInUK" >> ../conf/messages.en
echo "trustResidentInUK.error.required = Select yes if trustResidentInUK" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustResidentInUKUserAnswersEntry: Arbitrary[(TrustResidentInUKPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TrustResidentInUKPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustResidentInUKPage: Arbitrary[TrustResidentInUKPage.type] =";\
    print "    Arbitrary(TrustResidentInUKPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TrustResidentInUKPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def trustResidentInUK: Option[AnswerRow] = userAnswers.get(TrustResidentInUKPage) map {";\
     print "    x => AnswerRow(\"trustResidentInUK.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.TrustResidentInUKController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TrustResidentInUK completed"
