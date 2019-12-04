#!/bin/bash

echo ""
echo "Applying migration IndividualBeneficiaryNationalInsuranceNumber"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualBeneficiaryNationalInsuranceNumber                        controllers.register.beneficiaries.IndividualBeneficiaryNationalInsuranceNumberController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualBeneficiaryNationalInsuranceNumber                        controllers.register.beneficiaries.IndividualBeneficiaryNationalInsuranceNumberController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualBeneficiaryNationalInsuranceNumber                  controllers.register.beneficiaries.IndividualBeneficiaryNationalInsuranceNumberController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualBeneficiaryNationalInsuranceNumber                  controllers.register.beneficiaries.IndividualBeneficiaryNationalInsuranceNumberController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualBeneficiaryNationalInsuranceNumber.title = individualBeneficiaryNationalInsuranceNumber" >> ../conf/messages.en
echo "individualBeneficiaryNationalInsuranceNumber.heading = individualBeneficiaryNationalInsuranceNumber" >> ../conf/messages.en
echo "individualBeneficiaryNationalInsuranceNumber.checkYourAnswersLabel = individualBeneficiaryNationalInsuranceNumber" >> ../conf/messages.en
echo "individualBeneficiaryNationalInsuranceNumber.error.required = Enter individualBeneficiaryNationalInsuranceNumber" >> ../conf/messages.en
echo "individualBeneficiaryNationalInsuranceNumber.error.length = IndividualBeneficiaryNationalInsuranceNumber must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryNationalInsuranceNumberUserAnswersEntry: Arbitrary[(IndividualBeneficiaryNationalInsuranceNumberPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualBeneficiaryNationalInsuranceNumberPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryNationalInsuranceNumberPage: Arbitrary[IndividualBeneficiaryNationalInsuranceNumberPage.type] =";\
    print "    Arbitrary(IndividualBeneficiaryNationalInsuranceNumberPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualBeneficiaryNationalInsuranceNumberPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def individualBeneficiaryNationalInsuranceNumber: Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryNationalInsuranceNumberPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"individualBeneficiaryNationalInsuranceNumber.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.IndividualBeneficiaryNationalInsuranceNumberController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IndividualBeneficiaryNationalInsuranceNumber completed"
