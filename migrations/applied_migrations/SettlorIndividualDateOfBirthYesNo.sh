#!/bin/bash

echo ""
echo "Applying migration SettlorIndividualDateOfBirthYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/settlorIndividualDateOfBirthYesNo                        controllers.SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/settlorIndividualDateOfBirthYesNo                        controllers.SettlorIndividualDateOfBirthYesNoController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeSettlorIndividualDateOfBirthYesNo                  controllers.SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeSettlorIndividualDateOfBirthYesNo                  controllers.SettlorIndividualDateOfBirthYesNoController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorIndividualDateOfBirthYesNo.title = settlorIndividualDateOfBirthYesNo" >> ../conf/messages.en
echo "settlorIndividualDateOfBirthYesNo.heading = settlorIndividualDateOfBirthYesNo" >> ../conf/messages.en
echo "settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel = settlorIndividualDateOfBirthYesNo" >> ../conf/messages.en
echo "settlorIndividualDateOfBirthYesNo.error.required = Select yes if settlorIndividualDateOfBirthYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualDateOfBirthYesNoUserAnswersEntry: Arbitrary[(SettlorIndividualDateOfBirthYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorIndividualDateOfBirthYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualDateOfBirthYesNoPage: Arbitrary[SettlorIndividualDateOfBirthYesNoPage.type] =";\
    print "    Arbitrary(SettlorIndividualDateOfBirthYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorIndividualDateOfBirthYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def settlorIndividualDateOfBirthYesNo: Option[AnswerRow] = userAnswers.get(SettlorIndividualDateOfBirthYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorIndividualDateOfBirthYesNo completed"
