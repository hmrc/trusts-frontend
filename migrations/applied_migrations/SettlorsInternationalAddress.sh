#!/bin/bash

echo ""
echo "Applying migration SettlorsInternationalAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /settlorsInternationalAddress                        controllers.deceased_settlor.SettlorsInternationalAddressController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /settlorsInternationalAddress                        controllers.deceased_settlor.SettlorsInternationalAddressController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSettlorsInternationalAddress                  controllers.deceased_settlor.SettlorsInternationalAddressController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSettlorsInternationalAddress                  controllers.deceased_settlor.SettlorsInternationalAddressController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorsInternationalAddress.title = settlorsInternationalAddress" >> ../conf/messages.en
echo "settlorsInternationalAddress.heading = settlorsInternationalAddress" >> ../conf/messages.en
echo "settlorsInternationalAddress.field1 = Field 1" >> ../conf/messages.en
echo "settlorsInternationalAddress.field2 = Field 2" >> ../conf/messages.en
echo "settlorsInternationalAddress.checkYourAnswersLabel = settlorsInternationalAddress" >> ../conf/messages.en
echo "settlorsInternationalAddress.error.field1.required = Enter field1" >> ../conf/messages.en
echo "settlorsInternationalAddress.error.field2.required = Enter field2" >> ../conf/messages.en
echo "settlorsInternationalAddress.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "settlorsInternationalAddress.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorsInternationalAddressUserAnswersEntry: Arbitrary[(SettlorsInternationalAddressPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorsInternationalAddressPage.type]";\
    print "        value <- arbitrary[SettlorsInternationalAddress].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorsInternationalAddressPage: Arbitrary[SettlorsInternationalAddressPage.type] =";\
    print "    Arbitrary(SettlorsInternationalAddressPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorsInternationalAddress: Arbitrary[SettlorsInternationalAddress] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield SettlorsInternationalAddress(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorsInternationalAddressPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def settlorsInternationalAddress: Option[AnswerRow] = userAnswers.get(SettlorsInternationalAddressPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorsInternationalAddress.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(s\"${x.field1} ${x.field2}\"),";\
     print "        routes.SettlorsInternationalAddressController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorsInternationalAddress completed"
