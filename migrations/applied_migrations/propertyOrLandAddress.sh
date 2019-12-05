#!/bin/bash

echo ""
echo "Applying migration PropertyOrLandAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/propertyOrLandAddress                        controllers.register.asset.property_or_land.PropertyOrLandAddressController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/propertyOrLandAddress                        controllers.register.asset.property_or_land.PropertyOrLandAddressController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changePropertyOrLandAddress                  controllers.register.asset.property_or_land.PropertyOrLandAddressController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changePropertyOrLandAddress                  controllers.register.asset.property_or_land.PropertyOrLandAddressController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "propertyOrLandAddress.title = propertyOrLandAddress" >> ../conf/messages.en
echo "propertyOrLandAddress.heading = propertyOrLandAddress" >> ../conf/messages.en
echo "propertyOrLandAddress.checkYourAnswersLabel = propertyOrLandAddress" >> ../conf/messages.en
echo "propertyOrLandAddress.error.required = Select yes if propertyOrLandAddress" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPropertyOrLandAddressUserAnswersEntry: Arbitrary[(PropertyOrLandAddressPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[PropertyOrLandAddressPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPropertyOrLandAddressPage: Arbitrary[PropertyOrLandAddressPage.type] =";\
    print "    Arbitrary(PropertyOrLandAddressPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(PropertyOrLandAddressPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def propertyOrLandAddress: Option[AnswerRow] = userAnswers.get(PropertyOrLandAddressPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"propertyOrLandAddress.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.PropertyOrLandAddressController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration PropertyOrLandAddress completed"
