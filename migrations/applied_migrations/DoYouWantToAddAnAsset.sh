#!/bin/bash

echo ""
echo "Applying migration DoYouWantToAddAnAsset"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/doYouWantToAddAnAsset                        controllers.DoYouWantToAddAnAssetController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/doYouWantToAddAnAsset                        controllers.DoYouWantToAddAnAssetController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeDoYouWantToAddAnAsset                  controllers.DoYouWantToAddAnAssetController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeDoYouWantToAddAnAsset                  controllers.DoYouWantToAddAnAssetController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "doYouWantToAddAnAsset.title = doYouWantToAddAnAsset" >> ../conf/messages.en
echo "doYouWantToAddAnAsset.heading = doYouWantToAddAnAsset" >> ../conf/messages.en
echo "doYouWantToAddAnAsset.checkYourAnswersLabel = doYouWantToAddAnAsset" >> ../conf/messages.en
echo "doYouWantToAddAnAsset.error.required = Select yes if doYouWantToAddAnAsset" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDoYouWantToAddAnAssetUserAnswersEntry: Arbitrary[(DoYouWantToAddAnAssetPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[DoYouWantToAddAnAssetPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDoYouWantToAddAnAssetPage: Arbitrary[DoYouWantToAddAnAssetPage.type] =";\
    print "    Arbitrary(DoYouWantToAddAnAssetPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(DoYouWantToAddAnAssetPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def doYouWantToAddAnAsset: Option[AnswerRow] = userAnswers.get(DoYouWantToAddAnAssetPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"doYouWantToAddAnAsset.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.DoYouWantToAddAnAssetController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration DoYouWantToAddAnAsset completed"
