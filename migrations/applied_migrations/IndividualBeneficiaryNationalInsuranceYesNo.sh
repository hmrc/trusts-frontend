#!/bin/bash

echo ""
echo "Applying migration IndividualBeneficiaryNationalInsuranceYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualBeneficiaryNationalInsuranceYesNo                        controllers.register.beneficiaries.IndividualBeneficiaryNationalInsuranceYesNoController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualBeneficiaryNationalInsuranceYesNo                        controllers.register.beneficiaries.IndividualBeneficiaryNationalInsuranceYesNoController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualBeneficiaryNationalInsuranceYesNo                  controllers.register.beneficiaries.IndividualBeneficiaryNationalInsuranceYesNoController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualBeneficiaryNationalInsuranceYesNo                  controllers.register.beneficiaries.IndividualBeneficiaryNationalInsuranceYesNoController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualBeneficiaryNationalInsuranceYesNo.title = individualBeneficiaryNationalInsuranceYesNo" >> ../conf/messages.en
echo "individualBeneficiaryNationalInsuranceYesNo.heading = individualBeneficiaryNationalInsuranceYesNo" >> ../conf/messages.en
echo "individualBeneficiaryNationalInsuranceYesNo.checkYourAnswersLabel = individualBeneficiaryNationalInsuranceYesNo" >> ../conf/messages.en
echo "individualBeneficiaryNationalInsuranceYesNo.error.required = Select yes if individualBeneficiaryNationalInsuranceYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryNationalInsuranceYesNoUserAnswersEntry: Arbitrary[(IndividualBeneficiaryNationalInsuranceYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualBeneficiaryNationalInsuranceYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryNationalInsuranceYesNoPage: Arbitrary[IndividualBeneficiaryNationalInsuranceYesNoPage.type] =";\
    print "    Arbitrary(IndividualBeneficiaryNationalInsuranceYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualBeneficiaryNationalInsuranceYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def individualBeneficiaryNationalInsuranceYesNo: Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryNationalInsuranceYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"individualBeneficiaryNationalInsuranceYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.IndividualBeneficiaryNationalInsuranceYesNoController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IndividualBeneficiaryNationalInsuranceYesNo completed"
