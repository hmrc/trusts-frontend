#!/bin/bash

echo ""
echo "Applying migration KindOfTrust"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/kindOfTrust                        controllers.register.settlors.living_settlor.KindOfTrustController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/kindOfTrust                        controllers.register.settlors.living_settlor.KindOfTrustController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeKindOfTrust                  controllers.register.settlors.living_settlor.KindOfTrustController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeKindOfTrust                  controllers.register.settlors.living_settlor.KindOfTrustController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "kindOfTrust.title = kindOfTrust" >> ../conf/messages.en
echo "kindOfTrust.heading = kindOfTrust" >> ../conf/messages.en
echo "kindOfTrust.employees = A trust for the employees of a company" >> ../conf/messages.en
echo "kindOfTrust.building = A trust for a building or building with tenants" >> ../conf/messages.en
echo "kindOfTrust.checkYourAnswersLabel = kindOfTrust" >> ../conf/messages.en
echo "kindOfTrust.error.required = Select kindOfTrust" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryKindOfTrustUserAnswersEntry: Arbitrary[(KindOfTrustPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[KindOfTrustPage.type]";\
    print "        value <- arbitrary[KindOfTrust].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryKindOfTrustPage: Arbitrary[KindOfTrustPage.type] =";\
    print "    Arbitrary(KindOfTrustPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryKindOfTrust: Arbitrary[KindOfTrust] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(KindOfTrust.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(KindOfTrustPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def kindOfTrust: Option[AnswerRow] = userAnswers.get(KindOfTrustPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"kindOfTrust.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(messages(s\"kindOfTrust.$x\")),";\
     print "        routes.KindOfTrustController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration KindOfTrust completed"
