#!/bin/bash

echo ""
echo "Applying migration SettlorIndividualPassportYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/settlorIndividualPassportYesNo                        controllers.SettlorIndividualPassportYesNoController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/settlorIndividualPassportYesNo                        controllers.SettlorIndividualPassportYesNoController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeSettlorIndividualPassportYesNo                  controllers.SettlorIndividualPassportYesNoController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeSettlorIndividualPassportYesNo                  controllers.SettlorIndividualPassportYesNoController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorIndividualPassportYesNo.title = settlorIndividualPassportYesNo" >> ../conf/messages.en
echo "settlorIndividualPassportYesNo.heading = settlorIndividualPassportYesNo" >> ../conf/messages.en
echo "settlorIndividualPassportYesNo.checkYourAnswersLabel = settlorIndividualPassportYesNo" >> ../conf/messages.en
echo "settlorIndividualPassportYesNo.error.required = Select yes if settlorIndividualPassportYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualPassportYesNoUserAnswersEntry: Arbitrary[(SettlorIndividualPassportYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorIndividualPassportYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualPassportYesNoPage: Arbitrary[SettlorIndividualPassportYesNoPage.type] =";\
    print "    Arbitrary(SettlorIndividualPassportYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorIndividualPassportYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def settlorIndividualPassportYesNo: Option[AnswerRow] = userAnswers.get(SettlorIndividualPassportYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorIndividualPassportYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.SettlorIndividualPassportYesNoController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorIndividualPassportYesNo completed"
