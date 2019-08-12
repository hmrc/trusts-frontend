#!/bin/bash

echo ""
echo "Applying migration propertyOrLandAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /propertyOrLandAddress                        controllers.propertyOrLandAddressController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /propertyOrLandAddress                        controllers.propertyOrLandAddressController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changepropertyOrLandAddress                  controllers.propertyOrLandAddressController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changepropertyOrLandAddress                  controllers.propertyOrLandAddressController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

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
    print "  implicit lazy val arbitrarypropertyOrLandAddressUserAnswersEntry: Arbitrary[(propertyOrLandAddressPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[propertyOrLandAddressPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarypropertyOrLandAddressPage: Arbitrary[propertyOrLandAddressPage.type] =";\
    print "    Arbitrary(propertyOrLandAddressPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(propertyOrLandAddressPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def propertyOrLandAddress: Option[AnswerRow] = userAnswers.get(propertyOrLandAddressPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"propertyOrLandAddress.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.propertyOrLandAddressController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration propertyOrLandAddress completed"
