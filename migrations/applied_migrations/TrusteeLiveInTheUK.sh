#!/bin/bash

echo ""
echo "Applying migration TrusteeLiveInTheUK"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trusteeLiveInTheUK                        controllers.TrusteeLiveInTheUKController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trusteeLiveInTheUK                        controllers.TrusteeLiveInTheUKController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTrusteeLiveInTheUK                  controllers.TrusteeLiveInTheUKController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTrusteeLiveInTheUK                  controllers.TrusteeLiveInTheUKController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trusteeLiveInTheUK.title = trusteeLiveInTheUK" >> ../conf/messages.en
echo "trusteeLiveInTheUK.heading = trusteeLiveInTheUK" >> ../conf/messages.en
echo "trusteeLiveInTheUK.checkYourAnswersLabel = trusteeLiveInTheUK" >> ../conf/messages.en
echo "trusteeLiveInTheUK.error.required = Select yes if trusteeLiveInTheUK" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrusteeLiveInTheUKUserAnswersEntry: Arbitrary[(TrusteeLiveInTheUKPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TrusteeLiveInTheUKPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrusteeLiveInTheUKPage: Arbitrary[TrusteeLiveInTheUKPage.type] =";\
    print "    Arbitrary(TrusteeLiveInTheUKPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TrusteeLiveInTheUKPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def trusteeLiveInTheUK: Option[AnswerRow] = userAnswers.get(TrusteeLiveInTheUKPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"trusteeLiveInTheUK.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.TrusteeLiveInTheUKController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TrusteeLiveInTheUK completed"
