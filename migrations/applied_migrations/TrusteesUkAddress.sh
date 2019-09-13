#!/bin/bash

echo ""
echo "Applying migration TrusteesUkAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trusteesUkAddress                        controllers.trustees.TrusteesUkAddressController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trusteesUkAddress                        controllers.trustees.TrusteesUkAddressController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTrusteesUkAddress                  controllers.trustees.TrusteesUkAddressController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTrusteesUkAddress                  controllers.trustees.TrusteesUkAddressController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trusteesUkAddress.title = trusteesUkAddress" >> ../conf/messages.en
echo "trusteesUkAddress.heading = trusteesUkAddress" >> ../conf/messages.en
echo "trusteesUkAddress.field1 = Field 1" >> ../conf/messages.en
echo "trusteesUkAddress.field2 = Field 2" >> ../conf/messages.en
echo "trusteesUkAddress.checkYourAnswersLabel = trusteesUkAddress" >> ../conf/messages.en
echo "trusteesUkAddress.error.field1.required = Enter field1" >> ../conf/messages.en
echo "trusteesUkAddress.error.field2.required = Enter field2" >> ../conf/messages.en
echo "trusteesUkAddress.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "trusteesUkAddress.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrusteesUkAddressUserAnswersEntry: Arbitrary[(TrusteesUkAddressPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TrusteesUkAddressPage.type]";\
    print "        value <- arbitrary[TrusteesUkAddress].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrusteesUkAddressPage: Arbitrary[TrusteesUkAddressPage.type] =";\
    print "    Arbitrary(TrusteesUkAddressPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrusteesUkAddress: Arbitrary[TrusteesUkAddress] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield TrusteesUkAddress(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TrusteesUkAddressPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def trusteesUkAddress: Option[AnswerRow] = userAnswers.get(TrusteesUkAddressPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"trusteesUkAddress.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(s\"${x.field1} ${x.field2}\"),";\
     print "        routes.TrusteesUkAddressController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TrusteesUkAddress completed"
