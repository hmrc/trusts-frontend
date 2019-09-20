#!/bin/bash

echo ""
echo "Applying migration SettlorDetails"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/settlorDetails                        controllers.SettlorDetailsController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/settlorDetails                        controllers.SettlorDetailsController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeSettlorDetails                  controllers.SettlorDetailsController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeSettlorDetails                  controllers.SettlorDetailsController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorDetails.title = settlorDetails" >> ../conf/messages.en
echo "settlorDetails.heading = settlorDetails" >> ../conf/messages.en
echo "settlorDetails.option1 = Unique Taxpayer Reference" >> ../conf/messages.en
echo "settlorDetails.option2 = Address" >> ../conf/messages.en
echo "settlorDetails.checkYourAnswersLabel = settlorDetails" >> ../conf/messages.en
echo "settlorDetails.error.required = Select settlorDetails" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorDetailsUserAnswersEntry: Arbitrary[(SettlorDetailsPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorDetailsPage.type]";\
    print "        value <- arbitrary[SettlorDetails].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorDetailsPage: Arbitrary[SettlorDetailsPage.type] =";\
    print "    Arbitrary(SettlorDetailsPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorDetails: Arbitrary[SettlorDetails] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(SettlorDetails.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorDetailsPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def settlorDetails: Option[AnswerRow] = userAnswers.get(SettlorDetailsPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorDetails.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(messages(s\"settlorDetails.$x\")),";\
     print "        routes.SettlorDetailsController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorDetails completed"
