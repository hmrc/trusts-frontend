#!/bin/bash

echo ""
echo "Applying migration IndividualBeneficiaryVulnerableYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualBeneficiaryVulnerableYesNo                        controllers.register.beneficiaries.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualBeneficiaryVulnerableYesNo                        controllers.register.beneficiaries.IndividualBeneficiaryVulnerableYesNoController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualBeneficiaryVulnerableYesNo                  controllers.register.beneficiaries.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualBeneficiaryVulnerableYesNo                  controllers.register.beneficiaries.IndividualBeneficiaryVulnerableYesNoController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualBeneficiaryVulnerableYesNo.title = individualBeneficiaryVulnerableYesNo" >> ../conf/messages.en
echo "individualBeneficiaryVulnerableYesNo.heading = individualBeneficiaryVulnerableYesNo" >> ../conf/messages.en
echo "individualBeneficiaryVulnerableYesNo.checkYourAnswersLabel = individualBeneficiaryVulnerableYesNo" >> ../conf/messages.en
echo "individualBeneficiaryVulnerableYesNo.error.required = Select yes if individualBeneficiaryVulnerableYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryVulnerableYesNoUserAnswersEntry: Arbitrary[(IndividualBeneficiaryVulnerableYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualBeneficiaryVulnerableYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryVulnerableYesNoPage: Arbitrary[IndividualBeneficiaryVulnerableYesNoPage.type] =";\
    print "    Arbitrary(IndividualBeneficiaryVulnerableYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualBeneficiaryVulnerableYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def individualBeneficiaryVulnerableYesNo: Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryVulnerableYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"individualBeneficiaryVulnerableYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IndividualBeneficiaryVulnerableYesNo completed"
