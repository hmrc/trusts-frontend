#!/bin/bash

echo ""
echo "Applying migration SettlorsDateOfBirth"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /settlorsDateOfBirth                  controllers.SettlorsDateOfBirthController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /settlorsDateOfBirth                  controllers.SettlorsDateOfBirthController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSettlorsDateOfBirth                        controllers.SettlorsDateOfBirthController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSettlorsDateOfBirth                        controllers.SettlorsDateOfBirthController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorsDateOfBirth.title = SettlorsDateOfBirth" >> ../conf/messages.en
echo "settlorsDateOfBirth.heading = SettlorsDateOfBirth" >> ../conf/messages.en
echo "settlorsDateOfBirth.checkYourAnswersLabel = SettlorsDateOfBirth" >> ../conf/messages.en
echo "settlorsDateOfBirth.error.required.all = Enter the settlorsDateOfBirth" >> ../conf/messages.en
echo "settlorsDateOfBirth.error.required.two = The settlorsDateOfBirth" must include {0} and {1} >> ../conf/messages.en
echo "settlorsDateOfBirth.error.required = The settlorsDateOfBirth must include {0}" >> ../conf/messages.en
echo "settlorsDateOfBirth.error.invalid = Enter a real SettlorsDateOfBirth" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorsDateOfBirthUserAnswersEntry: Arbitrary[(SettlorsDateOfBirthPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorsDateOfBirthPage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorsDateOfBirthPage: Arbitrary[SettlorsDateOfBirthPage.type] =";\
    print "    Arbitrary(SettlorsDateOfBirthPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorsDateOfBirthPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def settlorsDateOfBirth: Option[AnswerRow] = userAnswers.get(SettlorsDateOfBirthPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorsDateOfBirth.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x.format(dateFormatter)),";\
     print "        routes.SettlorsDateOfBirthController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorsDateOfBirth completed"
