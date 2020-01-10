#!/bin/bash

echo ""
echo "Applying migration HoldoverReliefYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/holdoverReliefYesNo                        controllers.register.settlors.living_settlor.HoldoverReliefYesNoController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/holdoverReliefYesNo                        controllers.register.settlors.living_settlor.HoldoverReliefYesNoController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeHoldoverReliefYesNo                  controllers.register.settlors.living_settlor.HoldoverReliefYesNoController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeHoldoverReliefYesNo                  controllers.register.settlors.living_settlor.HoldoverReliefYesNoController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "holdoverReliefYesNo.title = holdoverReliefYesNo" >> ../conf/messages.en
echo "holdoverReliefYesNo.heading = holdoverReliefYesNo" >> ../conf/messages.en
echo "holdoverReliefYesNo.checkYourAnswersLabel = holdoverReliefYesNo" >> ../conf/messages.en
echo "holdoverReliefYesNo.error.required = Select yes if holdoverReliefYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHoldoverReliefYesNoUserAnswersEntry: Arbitrary[(HoldoverReliefYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[HoldoverReliefYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHoldoverReliefYesNoPage: Arbitrary[HoldoverReliefYesNoPage.type] =";\
    print "    Arbitrary(HoldoverReliefYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(HoldoverReliefYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def holdoverReliefYesNo: Option[AnswerRow] = userAnswers.get(HoldoverReliefYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"holdoverReliefYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.HoldoverReliefYesNoController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration HoldoverReliefYesNo completed"
