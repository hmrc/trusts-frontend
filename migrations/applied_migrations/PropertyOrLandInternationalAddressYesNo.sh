#!/bin/bash

echo ""
echo "Applying migration PropertyOrLandInternationalAddressYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/propertyOrLandInternationalAddressYesNo                        controllers.PropertyOrLandInternationalAddressYesNoController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/propertyOrLandInternationalAddressYesNo                        controllers.PropertyOrLandInternationalAddressYesNoController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changePropertyOrLandInternationalAddressYesNo                  controllers.PropertyOrLandInternationalAddressYesNoController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changePropertyOrLandInternationalAddressYesNo                  controllers.PropertyOrLandInternationalAddressYesNoController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "propertyOrLandInternationalAddressYesNo.title = propertyOrLandInternationalAddressYesNo" >> ../conf/messages.en
echo "propertyOrLandInternationalAddressYesNo.heading = propertyOrLandInternationalAddressYesNo" >> ../conf/messages.en
echo "propertyOrLandInternationalAddressYesNo.field1 = Field 1" >> ../conf/messages.en
echo "propertyOrLandInternationalAddressYesNo.field2 = Field 2" >> ../conf/messages.en
echo "propertyOrLandInternationalAddressYesNo.checkYourAnswersLabel = propertyOrLandInternationalAddressYesNo" >> ../conf/messages.en
echo "propertyOrLandInternationalAddressYesNo.error.field1.required = Enter field1" >> ../conf/messages.en
echo "propertyOrLandInternationalAddressYesNo.error.field2.required = Enter field2" >> ../conf/messages.en
echo "propertyOrLandInternationalAddressYesNo.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "propertyOrLandInternationalAddressYesNo.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPropertyOrLandInternationalAddressYesNoUserAnswersEntry: Arbitrary[(PropertyOrLandInternationalAddressYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[PropertyOrLandInternationalAddressYesNoPage.type]";\
    print "        value <- arbitrary[PropertyOrLandInternationalAddressYesNo].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPropertyOrLandInternationalAddressYesNoPage: Arbitrary[PropertyOrLandInternationalAddressYesNoPage.type] =";\
    print "    Arbitrary(PropertyOrLandInternationalAddressYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPropertyOrLandInternationalAddressYesNo: Arbitrary[PropertyOrLandInternationalAddressYesNo] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield PropertyOrLandInternationalAddressYesNo(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(PropertyOrLandInternationalAddressYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def propertyOrLandInternationalAddressYesNo: Option[AnswerRow] = userAnswers.get(PropertyOrLandInternationalAddressYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"propertyOrLandInternationalAddressYesNo.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(s\"${x.field1} ${x.field2}\"),";\
     print "        routes.PropertyOrLandInternationalAddressYesNoController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration PropertyOrLandInternationalAddressYesNo completed"
