#!/bin/bash

echo ""
echo "Applying migration SettlorsBasedInTheUK"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/settlorsBasedInTheUK                        controllers.SettlorsBasedInTheUKController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/settlorsBasedInTheUK                        controllers.SettlorsBasedInTheUKController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeSettlorsBasedInTheUK                  controllers.SettlorsBasedInTheUKController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeSettlorsBasedInTheUK                  controllers.SettlorsBasedInTheUKController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorsBasedInTheUK.title = settlorsBasedInTheUK" >> ../conf/messages.en
echo "settlorsBasedInTheUK.heading = settlorsBasedInTheUK" >> ../conf/messages.en
echo "settlorsBasedInTheUK.checkYourAnswersLabel = settlorsBasedInTheUK" >> ../conf/messages.en
echo "settlorsBasedInTheUK.error.required = Select yes if settlorsBasedInTheUK" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorsBasedInTheUKUserAnswersEntry: Arbitrary[(SettlorsBasedInTheUKPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorsBasedInTheUKPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorsBasedInTheUKPage: Arbitrary[SettlorsBasedInTheUKPage.type] =";\
    print "    Arbitrary(SettlorsBasedInTheUKPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorsBasedInTheUKPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def settlorsBasedInTheUK: Option[AnswerRow] = userAnswers.get(SettlorsBasedInTheUKPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorsBasedInTheUK.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.SettlorsBasedInTheUKController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorsBasedInTheUK completed"
