#!/bin/bash

echo ""
echo "Applying migration SettlorIndividualAddressUKYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/settlorIndividualAddressUKYesNo                        controllers.SettlorIndividualAddressUKYesNoController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/settlorIndividualAddressUKYesNo                        controllers.SettlorIndividualAddressUKYesNoController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeSettlorIndividualAddressUKYesNo                  controllers.SettlorIndividualAddressUKYesNoController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeSettlorIndividualAddressUKYesNo                  controllers.SettlorIndividualAddressUKYesNoController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorIndividualAddressUKYesNo.title = settlorIndividualAddressUKYesNo" >> ../conf/messages.en
echo "settlorIndividualAddressUKYesNo.heading = settlorIndividualAddressUKYesNo" >> ../conf/messages.en
echo "settlorIndividualAddressUKYesNo.checkYourAnswersLabel = settlorIndividualAddressUKYesNo" >> ../conf/messages.en
echo "settlorIndividualAddressUKYesNo.error.required = Select yes if settlorIndividualAddressUKYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualAddressUKYesNoUserAnswersEntry: Arbitrary[(SettlorIndividualAddressUKYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorIndividualAddressUKYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualAddressUKYesNoPage: Arbitrary[SettlorIndividualAddressUKYesNoPage.type] =";\
    print "    Arbitrary(SettlorIndividualAddressUKYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorIndividualAddressUKYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def settlorIndividualAddressUKYesNo: Option[AnswerRow] = userAnswers.get(SettlorIndividualAddressUKYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorIndividualAddressUKYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.SettlorIndividualAddressUKYesNoController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorIndividualAddressUKYesNo completed"
