#!/bin/bash

echo ""
echo "Applying migration SettlorHandoverReliefYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/settlorHandoverReliefYesNo                        controllers.register.settlors.living_settlor.SettlorHandoverReliefYesNoController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/settlorHandoverReliefYesNo                        controllers.register.settlors.living_settlor.SettlorHandoverReliefYesNoController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeSettlorHandoverReliefYesNo                  controllers.register.settlors.living_settlor.SettlorHandoverReliefYesNoController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeSettlorHandoverReliefYesNo                  controllers.register.settlors.living_settlor.SettlorHandoverReliefYesNoController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorHandoverReliefYesNo.title = settlorHandoverReliefYesNo" >> ../conf/messages.en
echo "settlorHandoverReliefYesNo.heading = settlorHandoverReliefYesNo" >> ../conf/messages.en
echo "settlorHandoverReliefYesNo.checkYourAnswersLabel = settlorHandoverReliefYesNo" >> ../conf/messages.en
echo "settlorHandoverReliefYesNo.error.required = Select yes if settlorHandoverReliefYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorHandoverReliefYesNoUserAnswersEntry: Arbitrary[(SettlorHandoverReliefYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorHandoverReliefYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorHandoverReliefYesNoPage: Arbitrary[SettlorHandoverReliefYesNoPage.type] =";\
    print "    Arbitrary(SettlorHandoverReliefYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorHandoverReliefYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def settlorHandoverReliefYesNo: Option[AnswerRow] = userAnswers.get(SettlorHandoverReliefYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorHandoverReliefYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.SettlorHandoverReliefYesNoController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorHandoverReliefYesNo completed"
