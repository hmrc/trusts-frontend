#!/bin/bash

echo ""
echo "Applying migration IndividualBeneficiaryIncome"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualBeneficiaryIncome                        controllers.IndividualBeneficiaryIncomeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualBeneficiaryIncome                        controllers.IndividualBeneficiaryIncomeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualBeneficiaryIncome                  controllers.IndividualBeneficiaryIncomeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualBeneficiaryIncome                  controllers.IndividualBeneficiaryIncomeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualBeneficiaryIncome.title = individualBeneficiaryIncome" >> ../conf/messages.en
echo "individualBeneficiaryIncome.heading = individualBeneficiaryIncome" >> ../conf/messages.en
echo "individualBeneficiaryIncome.checkYourAnswersLabel = individualBeneficiaryIncome" >> ../conf/messages.en
echo "individualBeneficiaryIncome.error.required = Enter individualBeneficiaryIncome" >> ../conf/messages.en
echo "individualBeneficiaryIncome.error.length = IndividualBeneficiaryIncome must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryIncomeUserAnswersEntry: Arbitrary[(IndividualBeneficiaryIncomePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualBeneficiaryIncomePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryIncomePage: Arbitrary[IndividualBeneficiaryIncomePage.type] =";\
    print "    Arbitrary(IndividualBeneficiaryIncomePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualBeneficiaryIncomePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def individualBeneficiaryIncome: Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryIncomePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"individualBeneficiaryIncome.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.IndividualBeneficiaryIncomeController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IndividualBeneficiaryIncome completed"
