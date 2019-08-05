#!/bin/bash

echo ""
echo "Applying migration TrustOwnAllThePropertyOrLand"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/trustOwnAllThePropertyOrLand                        controllers.property_or_land.TrustOwnAllThePropertyOrLandController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/trustOwnAllThePropertyOrLand                        controllers.property_or_land.TrustOwnAllThePropertyOrLandController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeTrustOwnAllThePropertyOrLand                  controllers.property_or_land.TrustOwnAllThePropertyOrLandController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeTrustOwnAllThePropertyOrLand                  controllers.property_or_land.TrustOwnAllThePropertyOrLandController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trustOwnAllThePropertyOrLand.title = trustOwnAllThePropertyOrLand" >> ../conf/messages.en
echo "trustOwnAllThePropertyOrLand.heading = trustOwnAllThePropertyOrLand" >> ../conf/messages.en
echo "trustOwnAllThePropertyOrLand.checkYourAnswersLabel = trustOwnAllThePropertyOrLand" >> ../conf/messages.en
echo "trustOwnAllThePropertyOrLand.error.required = Select yes if trustOwnAllThePropertyOrLand" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustOwnAllThePropertyOrLandUserAnswersEntry: Arbitrary[(TrustOwnAllThePropertyOrLandPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TrustOwnAllThePropertyOrLandPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustOwnAllThePropertyOrLandPage: Arbitrary[TrustOwnAllThePropertyOrLandPage.type] =";\
    print "    Arbitrary(TrustOwnAllThePropertyOrLandPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TrustOwnAllThePropertyOrLandPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def trustOwnAllThePropertyOrLand: Option[AnswerRow] = userAnswers.get(TrustOwnAllThePropertyOrLandPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"trustOwnAllThePropertyOrLand.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.TrustOwnAllThePropertyOrLandController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TrustOwnAllThePropertyOrLand completed"
