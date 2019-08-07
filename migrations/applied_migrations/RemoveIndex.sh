#!/bin/bash

echo ""
echo "Applying migration RemoveIndex"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/removeIndex                        controllers.RemoveIndexController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/removeIndex                        controllers.RemoveIndexController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeRemoveIndex                  controllers.RemoveIndexController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeRemoveIndex                  controllers.RemoveIndexController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "removeIndex.title = removeIndex" >> ../conf/messages.en
echo "removeIndex.heading = removeIndex" >> ../conf/messages.en
echo "removeIndex.checkYourAnswersLabel = removeIndex" >> ../conf/messages.en
echo "removeIndex.error.required = Select yes if removeIndex" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryRemoveIndexUserAnswersEntry: Arbitrary[(RemoveIndexPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[RemoveIndexPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryRemoveIndexPage: Arbitrary[RemoveIndexPage.type] =";\
    print "    Arbitrary(RemoveIndexPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(RemoveIndexPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def removeIndex: Option[AnswerRow] = userAnswers.get(RemoveIndexPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"removeIndex.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.RemoveIndexController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration RemoveIndex completed"
