#!/bin/bash

echo ""
echo "Applying migration SettlorIndividualIDCardYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/settlorIndividualIDCardYesNo                        controllers.SettlorIndividualIDCardYesNoController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/settlorIndividualIDCardYesNo                        controllers.SettlorIndividualIDCardYesNoController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeSettlorIndividualIDCardYesNo                  controllers.SettlorIndividualIDCardYesNoController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeSettlorIndividualIDCardYesNo                  controllers.SettlorIndividualIDCardYesNoController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorIndividualIDCardYesNo.title = settlorIndividualIDCardYesNo" >> ../conf/messages.en
echo "settlorIndividualIDCardYesNo.heading = settlorIndividualIDCardYesNo" >> ../conf/messages.en
echo "settlorIndividualIDCardYesNo.checkYourAnswersLabel = settlorIndividualIDCardYesNo" >> ../conf/messages.en
echo "settlorIndividualIDCardYesNo.error.required = Select yes if settlorIndividualIDCardYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualIDCardYesNoUserAnswersEntry: Arbitrary[(SettlorIndividualIDCardYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorIndividualIDCardYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualIDCardYesNoPage: Arbitrary[SettlorIndividualIDCardYesNoPage.type] =";\
    print "    Arbitrary(SettlorIndividualIDCardYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorIndividualIDCardYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def settlorIndividualIDCardYesNo: Option[AnswerRow] = userAnswers.get(SettlorIndividualIDCardYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorIndividualIDCardYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.SettlorIndividualIDCardYesNoController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorIndividualIDCardYesNo completed"
