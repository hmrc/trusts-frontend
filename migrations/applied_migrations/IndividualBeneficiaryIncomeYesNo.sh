#!/bin/bash

echo ""
echo "Applying migration IndividualBeneficiaryIncomeYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualBeneficiaryIncomeYesNo                        controllers.register.beneficiaries.IndividualBeneficiaryIncomeYesNoController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualBeneficiaryIncomeYesNo                        controllers.register.beneficiaries.IndividualBeneficiaryIncomeYesNoController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualBeneficiaryIncomeYesNo                  controllers.register.beneficiaries.IndividualBeneficiaryIncomeYesNoController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualBeneficiaryIncomeYesNo                  controllers.register.beneficiaries.IndividualBeneficiaryIncomeYesNoController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualBeneficiaryIncomeYesNo.title = individualBeneficiaryIncomeYesNo" >> ../conf/messages.en
echo "individualBeneficiaryIncomeYesNo.heading = individualBeneficiaryIncomeYesNo" >> ../conf/messages.en
echo "individualBeneficiaryIncomeYesNo.checkYourAnswersLabel = individualBeneficiaryIncomeYesNo" >> ../conf/messages.en
echo "individualBeneficiaryIncomeYesNo.error.required = Select yes if individualBeneficiaryIncomeYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryIncomeYesNoUserAnswersEntry: Arbitrary[(IndividualBeneficiaryIncomeYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualBeneficiaryIncomeYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryIncomeYesNoPage: Arbitrary[IndividualBeneficiaryIncomeYesNoPage.type] =";\
    print "    Arbitrary(IndividualBeneficiaryIncomeYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualBeneficiaryIncomeYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def individualBeneficiaryIncomeYesNo: Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryIncomeYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"individualBeneficiaryIncomeYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.IndividualBeneficiaryIncomeYesNoController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IndividualBeneficiaryIncomeYesNo completed"
