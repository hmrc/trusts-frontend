#!/bin/bash

echo ""
echo "Applying migration TrusteeRemoveYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/trusteeRemoveYesNo                        controllers.TrusteeRemoveYesNoController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/trusteeRemoveYesNo                        controllers.TrusteeRemoveYesNoController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeTrusteeRemoveYesNo                  controllers.TrusteeRemoveYesNoController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeTrusteeRemoveYesNo                  controllers.TrusteeRemoveYesNoController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trusteeRemoveYesNo.title = trusteeRemoveYesNo" >> ../conf/messages.en
echo "trusteeRemoveYesNo.heading = trusteeRemoveYesNo" >> ../conf/messages.en
echo "trusteeRemoveYesNo.checkYourAnswersLabel = trusteeRemoveYesNo" >> ../conf/messages.en
echo "trusteeRemoveYesNo.error.required = Select yes if trusteeRemoveYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrusteeRemoveYesNoUserAnswersEntry: Arbitrary[(TrusteeRemoveYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TrusteeRemoveYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrusteeRemoveYesNoPage: Arbitrary[TrusteeRemoveYesNoPage.type] =";\
    print "    Arbitrary(TrusteeRemoveYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TrusteeRemoveYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def trusteeRemoveYesNo: Option[AnswerRow] = userAnswers.get(TrusteeRemoveYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"trusteeRemoveYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.TrusteeRemoveYesNoController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TrusteeRemoveYesNo completed"
