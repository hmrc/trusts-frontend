#!/bin/bash

echo ""
echo "Applying migration SettlorIndividualAddressUK"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/settlorIndividualAddressUK                        controllers.SettlorIndividualAddressUKController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/settlorIndividualAddressUK                        controllers.SettlorIndividualAddressUKController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeSettlorIndividualAddressUK                  controllers.SettlorIndividualAddressUKController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeSettlorIndividualAddressUK                  controllers.SettlorIndividualAddressUKController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorIndividualAddressUK.title = settlorIndividualAddressUK" >> ../conf/messages.en
echo "settlorIndividualAddressUK.heading = settlorIndividualAddressUK" >> ../conf/messages.en
echo "settlorIndividualAddressUK.field1 = Field 1" >> ../conf/messages.en
echo "settlorIndividualAddressUK.field2 = Field 2" >> ../conf/messages.en
echo "settlorIndividualAddressUK.checkYourAnswersLabel = settlorIndividualAddressUK" >> ../conf/messages.en
echo "settlorIndividualAddressUK.error.field1.required = Enter field1" >> ../conf/messages.en
echo "settlorIndividualAddressUK.error.field2.required = Enter field2" >> ../conf/messages.en
echo "settlorIndividualAddressUK.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "settlorIndividualAddressUK.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualAddressUKUserAnswersEntry: Arbitrary[(SettlorIndividualAddressUKPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorIndividualAddressUKPage.type]";\
    print "        value <- arbitrary[SettlorIndividualAddressUK].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualAddressUKPage: Arbitrary[SettlorIndividualAddressUKPage.type] =";\
    print "    Arbitrary(SettlorIndividualAddressUKPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualAddressUK: Arbitrary[SettlorIndividualAddressUK] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield SettlorIndividualAddressUK(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorIndividualAddressUKPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def settlorIndividualAddressUK: Option[AnswerRow] = userAnswers.get(SettlorIndividualAddressUKPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorIndividualAddressUK.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(s\"${x.field1} ${x.field2}\"),";\
     print "        routes.SettlorIndividualAddressUKController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorIndividualAddressUK completed"
