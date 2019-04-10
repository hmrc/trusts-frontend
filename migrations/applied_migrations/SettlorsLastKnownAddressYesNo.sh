#!/bin/bash

echo ""
echo "Applying migration SettlorsLastKnownAddressYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /settlorsLastKnownAddressYesNo                        controllers.SettlorsLastKnownAddressYesNoController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /settlorsLastKnownAddressYesNo                        controllers.SettlorsLastKnownAddressYesNoController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSettlorsLastKnownAddressYesNo                  controllers.SettlorsLastKnownAddressYesNoController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSettlorsLastKnownAddressYesNo                  controllers.SettlorsLastKnownAddressYesNoController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorsLastKnownAddressYesNo.title = settlorsLastKnownAddressYesNo" >> ../conf/messages.en
echo "settlorsLastKnownAddressYesNo.heading = settlorsLastKnownAddressYesNo" >> ../conf/messages.en
echo "settlorsLastKnownAddressYesNo.checkYourAnswersLabel = settlorsLastKnownAddressYesNo" >> ../conf/messages.en
echo "settlorsLastKnownAddressYesNo.error.required = Select yes if settlorsLastKnownAddressYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorsLastKnownAddressYesNoUserAnswersEntry: Arbitrary[(SettlorsLastKnownAddressYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorsLastKnownAddressYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorsLastKnownAddressYesNoPage: Arbitrary[SettlorsLastKnownAddressYesNoPage.type] =";\
    print "    Arbitrary(SettlorsLastKnownAddressYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorsLastKnownAddressYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def settlorsLastKnownAddressYesNo: Option[AnswerRow] = userAnswers.get(SettlorsLastKnownAddressYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorsLastKnownAddressYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.SettlorsLastKnownAddressYesNoController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorsLastKnownAddressYesNo completed"
