#!/bin/bash

echo ""
echo "Applying migration PropertyLandValueTrust"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/propertyLandValueTrust                        controllers.register.asset.property_or_land.PropertyLandValueTrustController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/propertyLandValueTrust                        controllers.register.asset.property_or_land.PropertyLandValueTrustController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changePropertyLandValueTrust                  controllers.register.asset.property_or_land.PropertyLandValueTrustController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changePropertyLandValueTrust                  controllers.register.asset.property_or_land.PropertyLandValueTrustController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "propertyLandValueTrust.title = propertyLandValueTrust" >> ../conf/messages.en
echo "propertyLandValueTrust.heading = propertyLandValueTrust" >> ../conf/messages.en
echo "propertyLandValueTrust.field1 = Field 1" >> ../conf/messages.en
echo "propertyLandValueTrust.field2 = Field 2" >> ../conf/messages.en
echo "propertyLandValueTrust.checkYourAnswersLabel = propertyLandValueTrust" >> ../conf/messages.en
echo "propertyLandValueTrust.error.field1.required = Enter field1" >> ../conf/messages.en
echo "propertyLandValueTrust.error.field2.required = Enter field2" >> ../conf/messages.en
echo "propertyLandValueTrust.error.field1.length = field1 must be 12 characters or less" >> ../conf/messages.en
echo "propertyLandValueTrust.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPropertyLandValueTrustUserAnswersEntry: Arbitrary[(PropertyLandValueTrustPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[PropertyLandValueTrustPage.type]";\
    print "        value <- arbitrary[PropertyLandValueTrust].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPropertyLandValueTrustPage: Arbitrary[PropertyLandValueTrustPage.type] =";\
    print "    Arbitrary(PropertyLandValueTrustPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPropertyLandValueTrust: Arbitrary[PropertyLandValueTrust] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield PropertyLandValueTrust(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(PropertyLandValueTrustPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def propertyLandValueTrust: Option[AnswerRow] = userAnswers.get(PropertyLandValueTrustPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"propertyLandValueTrust.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(s\"${x.field1} ${x.field2}\"),";\
     print "        routes.PropertyLandValueTrustController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration PropertyLandValueTrust completed"
