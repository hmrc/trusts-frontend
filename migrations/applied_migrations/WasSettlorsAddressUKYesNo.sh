#!/bin/bash

echo ""
echo "Applying migration WasSettlorsAddressUKYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /wasSettlorsAddressUKYesNo                        controllers.deceased_settlor.WasSettlorsAddressUKYesNoController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /wasSettlorsAddressUKYesNo                        controllers.deceased_settlor.WasSettlorsAddressUKYesNoController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWasSettlorsAddressUKYesNo                  controllers.deceased_settlor.WasSettlorsAddressUKYesNoController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWasSettlorsAddressUKYesNo                  controllers.deceased_settlor.WasSettlorsAddressUKYesNoController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "wasSettlorsAddressUKYesNo.title = wasSettlorsAddressUKYesNo" >> ../conf/messages.en
echo "wasSettlorsAddressUKYesNo.heading = wasSettlorsAddressUKYesNo" >> ../conf/messages.en
echo "wasSettlorsAddressUKYesNo.checkYourAnswersLabel = wasSettlorsAddressUKYesNo" >> ../conf/messages.en
echo "wasSettlorsAddressUKYesNo.error.required = Select yes if wasSettlorsAddressUKYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWasSettlorsAddressUKYesNoUserAnswersEntry: Arbitrary[(WasSettlorsAddressUKYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WasSettlorsAddressUKYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWasSettlorsAddressUKYesNoPage: Arbitrary[WasSettlorsAddressUKYesNoPage.type] =";\
    print "    Arbitrary(WasSettlorsAddressUKYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WasSettlorsAddressUKYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def wasSettlorsAddressUKYesNo: Option[AnswerRow] = userAnswers.get(WasSettlorsAddressUKYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"wasSettlorsAddressUKYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.WasSettlorsAddressUKYesNoController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WasSettlorsAddressUKYesNo completed"
