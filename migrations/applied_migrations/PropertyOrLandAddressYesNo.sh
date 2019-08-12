#!/bin/bash

echo ""
echo "Applying migration PropertyOrLandAddressYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/propertyOrLandAddressYesNo                        controllers.property_or_land.PropertyOrLandAddressYesNoController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/propertyOrLandAddressYesNo                        controllers.property_or_land.PropertyOrLandAddressYesNoController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changePropertyOrLandAddressYesNo                  controllers.property_or_land.PropertyOrLandAddressYesNoController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changePropertyOrLandAddressYesNo                  controllers.property_or_land.PropertyOrLandAddressYesNoController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "propertyOrLandAddressYesNo.title = propertyOrLandAddressYesNo" >> ../conf/messages.en
echo "propertyOrLandAddressYesNo.heading = propertyOrLandAddressYesNo" >> ../conf/messages.en
echo "propertyOrLandAddressYesNo.checkYourAnswersLabel = propertyOrLandAddressYesNo" >> ../conf/messages.en
echo "propertyOrLandAddressYesNo.error.required = Select yes if propertyOrLandAddressYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPropertyOrLandAddressYesNoUserAnswersEntry: Arbitrary[(PropertyOrLandAddressYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[PropertyOrLandAddressYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPropertyOrLandAddressYesNoPage: Arbitrary[PropertyOrLandAddressYesNoPage.type] =";\
    print "    Arbitrary(PropertyOrLandAddressYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(PropertyOrLandAddressYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def propertyOrLandAddressYesNo: Option[AnswerRow] = userAnswers.get(PropertyOrLandAddressYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"propertyOrLandAddressYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.PropertyOrLandAddressYesNoController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration PropertyOrLandAddressYesNo completed"
