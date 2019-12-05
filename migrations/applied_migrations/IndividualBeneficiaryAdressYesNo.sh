#!/bin/bash

echo ""
echo "Applying migration IndividualBeneficiaryAddressYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualBeneficiaryAddressYesNo                        controllers.register.beneficiaries.IndividualBeneficiaryAddressYesNoController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualBeneficiaryAddressYesNo                        controllers.register.beneficiaries.IndividualBeneficiaryAddressYesNoController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualBeneficiaryAddressYesNo                  controllers.register.beneficiaries.IndividualBeneficiaryAddressYesNoController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualBeneficiaryAddressYesNo                  controllers.register.beneficiaries.IndividualBeneficiaryAddressYesNoController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualBeneficiaryAddressYesNo.title = individualBeneficiaryAddressYesNo" >> ../conf/messages.en
echo "individualBeneficiaryAddressYesNo.heading = individualBeneficiaryAddressYesNo" >> ../conf/messages.en
echo "individualBeneficiaryAddressYesNo.checkYourAnswersLabel = individualBeneficiaryAddressYesNo" >> ../conf/messages.en
echo "individualBeneficiaryAddressYesNo.error.required = Select yes if individualBeneficiaryAddressYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryAddressYesNoUserAnswersEntry: Arbitrary[(IndividualBeneficiaryAddressYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualBeneficiaryAddressYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryAddressYesNoPage: Arbitrary[IndividualBeneficiaryAddressYesNoPage.type] =";\
    print "    Arbitrary(IndividualBeneficiaryAddressYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualBeneficiaryAddressYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def individualBeneficiaryAddressYesNo: Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryAddressYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"individualBeneficiaryAddressYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.IndividualBeneficiaryAddressYesNoController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IndividualBeneficiaryAddressYesNo completed"
