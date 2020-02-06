#!/bin/bash

echo ""
echo "Applying migration TrusteesBasedInTheUK"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/trusteesBasedInTheUK                        controllers.register.TrusteesBasedInTheUKController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/trusteesBasedInTheUK                        controllers.register.TrusteesBasedInTheUKController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeTrusteesBasedInTheUK                  controllers.register.TrusteesBasedInTheUKController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeTrusteesBasedInTheUK                  controllers.register.TrusteesBasedInTheUKController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trusteesBasedInTheUK.title = TrusteesBasedInTheUK" >> ../conf/messages.en
echo "trusteesBasedInTheUK.heading = TrusteesBasedInTheUK" >> ../conf/messages.en
echo "trusteesBasedInTheUK.option1 = All the trustees based in the UK" >> ../conf/messages.en
echo "trusteesBasedInTheUK.option2 = None of the trustees are based in the UK" >> ../conf/messages.en
echo "trusteesBasedInTheUK.checkYourAnswersLabel = TrusteesBasedInTheUK" >> ../conf/messages.en
echo "trusteesBasedInTheUK.error.required = Select trusteesBasedInTheUK" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrusteesBasedInTheUKUserAnswersEntry: Arbitrary[(TrusteesBasedInTheUKPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TrusteesBasedInTheUKPage.type]";\
    print "        value <- arbitrary[TrusteesBasedInTheUK].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrusteesBasedInTheUKPage: Arbitrary[TrusteesBasedInTheUKPage.type] =";\
    print "    Arbitrary(TrusteesBasedInTheUKPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrusteesBasedInTheUK: Arbitrary[TrusteesBasedInTheUK] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(TrusteesBasedInTheUK.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TrusteesBasedInTheUKPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def trusteesBasedInTheUK: Option[AnswerRow] = userAnswers.get(TrusteesBasedInTheUKPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"trusteesBasedInTheUK.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(messages(s\"trusteesBasedInTheUK.$x\")),";\
     print "        routes.TrusteesBasedInTheUKController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TrusteesBasedInTheUK completed"
