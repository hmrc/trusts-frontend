#!/bin/bash

echo ""
echo "Applying migration SettlorKindOfTrust"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/settlorKindOfTrust                        controllers.SettlorKindOfTrustController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/settlorKindOfTrust                        controllers.SettlorKindOfTrustController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeSettlorKindOfTrust                  controllers.SettlorKindOfTrustController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeSettlorKindOfTrust                  controllers.SettlorKindOfTrustController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorKindOfTrust.title = settlorKindOfTrust" >> ../conf/messages.en
echo "settlorKindOfTrust.heading = settlorKindOfTrust" >> ../conf/messages.en
echo "settlorKindOfTrust.employees = A trust for the employees of a company" >> ../conf/messages.en
echo "settlorKindOfTrust.building = A trust for a building or building with tenants" >> ../conf/messages.en
echo "settlorKindOfTrust.checkYourAnswersLabel = settlorKindOfTrust" >> ../conf/messages.en
echo "settlorKindOfTrust.error.required = Select settlorKindOfTrust" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorKindOfTrustUserAnswersEntry: Arbitrary[(SettlorKindOfTrustPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorKindOfTrustPage.type]";\
    print "        value <- arbitrary[SettlorKindOfTrust].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorKindOfTrustPage: Arbitrary[SettlorKindOfTrustPage.type] =";\
    print "    Arbitrary(SettlorKindOfTrustPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorKindOfTrust: Arbitrary[SettlorKindOfTrust] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(SettlorKindOfTrust.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorKindOfTrustPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def settlorKindOfTrust: Option[AnswerRow] = userAnswers.get(SettlorKindOfTrustPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorKindOfTrust.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(messages(s\"settlorKindOfTrust.$x\")),";\
     print "        routes.SettlorKindOfTrustController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorKindOfTrust completed"
