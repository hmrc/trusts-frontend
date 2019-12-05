#!/bin/bash

echo ""
echo "Applying migration IndividualBeneficiaryDateOfBirthYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualBeneficiaryDateOfBirthYesNo                        controllers.register.beneficiaries.IndividualBeneficiaryDateOfBirthYesNoController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualBeneficiaryDateOfBirthYesNo                        controllers.register.beneficiaries.IndividualBeneficiaryDateOfBirthYesNoController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualBeneficiaryDateOfBirthYesNo                  controllers.register.beneficiaries.IndividualBeneficiaryDateOfBirthYesNoController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualBeneficiaryDateOfBirthYesNo                  controllers.register.beneficiaries.IndividualBeneficiaryDateOfBirthYesNoController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualBeneficiaryDateOfBirthYesNo.title = individualBeneficiaryDateOfBirthYesNo" >> ../conf/messages.en
echo "individualBeneficiaryDateOfBirthYesNo.heading = individualBeneficiaryDateOfBirthYesNo" >> ../conf/messages.en
echo "individualBeneficiaryDateOfBirthYesNo.checkYourAnswersLabel = individualBeneficiaryDateOfBirthYesNo" >> ../conf/messages.en
echo "individualBeneficiaryDateOfBirthYesNo.error.required = Select yes if individualBeneficiaryDateOfBirthYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryDateOfBirthYesNoUserAnswersEntry: Arbitrary[(IndividualBeneficiaryDateOfBirthYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualBeneficiaryDateOfBirthYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryDateOfBirthYesNoPage: Arbitrary[IndividualBeneficiaryDateOfBirthYesNoPage.type] =";\
    print "    Arbitrary(IndividualBeneficiaryDateOfBirthYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualBeneficiaryDateOfBirthYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def individualBeneficiaryDateOfBirthYesNo: Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryDateOfBirthYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"individualBeneficiaryDateOfBirthYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.IndividualBeneficiaryDateOfBirthYesNoController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IndividualBeneficiaryDateOfBirthYesNo completed"
