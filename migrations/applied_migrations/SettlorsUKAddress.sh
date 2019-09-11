#!/bin/bash

echo ""
echo "Applying migration SettlorsUKAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /settlorsUKAddress                        controllers.deceased_settlor.SettlorsUKAddressController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /settlorsUKAddress                        controllers.deceased_settlor.SettlorsUKAddressController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSettlorsUKAddress                  controllers.deceased_settlor.SettlorsUKAddressController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSettlorsUKAddress                  controllers.deceased_settlor.SettlorsUKAddressController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorsUKAddress.title = settlorsUKAddress" >> ../conf/messages.en
echo "settlorsUKAddress.heading = settlorsUKAddress" >> ../conf/messages.en
echo "settlorsUKAddress.field1 = Field 1" >> ../conf/messages.en
echo "settlorsUKAddress.field2 = Field 2" >> ../conf/messages.en
echo "settlorsUKAddress.checkYourAnswersLabel = settlorsUKAddress" >> ../conf/messages.en
echo "settlorsUKAddress.error.field1.required = Enter field1" >> ../conf/messages.en
echo "settlorsUKAddress.error.field2.required = Enter field2" >> ../conf/messages.en
echo "settlorsUKAddress.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "settlorsUKAddress.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorsUKAddressUserAnswersEntry: Arbitrary[(SettlorsUKAddressPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorsUKAddressPage.type]";\
    print "        value <- arbitrary[SettlorsUKAddress].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorsUKAddressPage: Arbitrary[SettlorsUKAddressPage.type] =";\
    print "    Arbitrary(SettlorsUKAddressPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorsUKAddress: Arbitrary[SettlorsUKAddress] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield SettlorsUKAddress(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorsUKAddressPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def settlorsUKAddress: Option[AnswerRow] = userAnswers.get(SettlorsUKAddressPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorsUKAddress.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(s\"${x.field1} ${x.field2}\"),";\
     print "        routes.SettlorsUKAddressController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorsUKAddress completed"
