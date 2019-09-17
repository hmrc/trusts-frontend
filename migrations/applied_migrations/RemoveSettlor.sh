#!/bin/bash

echo ""
echo "Applying migration RemoveSettlor"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/removeSettlor                        controllers.living_settlor.RemoveSettlorController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/removeSettlor                        controllers.living_settlor.RemoveSettlorController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeRemoveSettlor                  controllers.living_settlor.RemoveSettlorController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeRemoveSettlor                  controllers.living_settlor.RemoveSettlorController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "removeSettlor.title = removeSettlor" >> ../conf/messages.en
echo "removeSettlor.heading = removeSettlor" >> ../conf/messages.en
echo "removeSettlor.checkYourAnswersLabel = removeSettlor" >> ../conf/messages.en
echo "removeSettlor.error.required = Select yes if removeSettlor" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryRemoveSettlorUserAnswersEntry: Arbitrary[(RemoveSettlorPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[RemoveSettlorPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryRemoveSettlorPage: Arbitrary[RemoveSettlorPage.type] =";\
    print "    Arbitrary(RemoveSettlorPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(RemoveSettlorPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def removeSettlor: Option[AnswerRow] = userAnswers.get(RemoveSettlorPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"removeSettlor.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.RemoveSettlorController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration RemoveSettlor completed"
