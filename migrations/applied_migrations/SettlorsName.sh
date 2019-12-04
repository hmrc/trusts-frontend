#!/bin/bash

echo ""
echo "Applying migration SettlorsName"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /settlorsName                        controllers.register.settlors.deceased_settlor.SettlorsNameController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /settlorsName                        controllers.register.settlors.deceased_settlor.SettlorsNameController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSettlorsName                  controllers.register.settlors.deceased_settlor.SettlorsNameController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSettlorsName                  controllers.register.settlors.deceased_settlor.SettlorsNameController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorsName.title = settlorsName" >> ../conf/messages.en
echo "settlorsName.heading = settlorsName" >> ../conf/messages.en
echo "settlorsName.field1 = Field 1" >> ../conf/messages.en
echo "settlorsName.field2 = Field 2" >> ../conf/messages.en
echo "settlorsName.checkYourAnswersLabel = settlorsName" >> ../conf/messages.en
echo "settlorsName.error.field1.required = Enter field1" >> ../conf/messages.en
echo "settlorsName.error.field2.required = Enter field2" >> ../conf/messages.en
echo "settlorsName.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "settlorsName.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorsNameUserAnswersEntry: Arbitrary[(SettlorsNamePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorsNamePage.type]";\
    print "        value <- arbitrary[SettlorsName].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorsNamePage: Arbitrary[SettlorsNamePage.type] =";\
    print "    Arbitrary(SettlorsNamePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorsName: Arbitrary[SettlorsName] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield SettlorsName(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorsNamePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def settlorsName: Option[AnswerRow] = userAnswers.get(SettlorsNamePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorsName.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(s\"${x.field1} ${x.field2}\"),";\
     print "        routes.SettlorsNameController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorsName completed"
