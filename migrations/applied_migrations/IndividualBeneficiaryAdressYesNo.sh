#!/bin/bash

echo ""
echo "Applying migration IndividualBeneficiaryAdressYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualBeneficiaryAdressYesNo                        controllers.IndividualBeneficiaryAdressYesNoController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualBeneficiaryAdressYesNo                        controllers.IndividualBeneficiaryAdressYesNoController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualBeneficiaryAdressYesNo                  controllers.IndividualBeneficiaryAdressYesNoController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualBeneficiaryAdressYesNo                  controllers.IndividualBeneficiaryAdressYesNoController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualBeneficiaryAdressYesNo.title = individualBeneficiaryAdressYesNo" >> ../conf/messages.en
echo "individualBeneficiaryAdressYesNo.heading = individualBeneficiaryAdressYesNo" >> ../conf/messages.en
echo "individualBeneficiaryAdressYesNo.checkYourAnswersLabel = individualBeneficiaryAdressYesNo" >> ../conf/messages.en
echo "individualBeneficiaryAdressYesNo.error.required = Select yes if individualBeneficiaryAdressYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryAdressYesNoUserAnswersEntry: Arbitrary[(IndividualBeneficiaryAdressYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualBeneficiaryAdressYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryAdressYesNoPage: Arbitrary[IndividualBeneficiaryAdressYesNoPage.type] =";\
    print "    Arbitrary(IndividualBeneficiaryAdressYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualBeneficiaryAdressYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def individualBeneficiaryAdressYesNo: Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryAdressYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"individualBeneficiaryAdressYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.IndividualBeneficiaryAdressYesNoController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IndividualBeneficiaryAdressYesNo completed"
