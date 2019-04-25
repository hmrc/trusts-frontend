#!/bin/bash

echo ""
echo "Applying migration IndividualBeneficiaryDateOfBirth"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualBeneficiaryDateOfBirth                  controllers.IndividualBeneficiaryDateOfBirthController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualBeneficiaryDateOfBirth                  controllers.IndividualBeneficiaryDateOfBirthController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualBeneficiaryDateOfBirth                        controllers.IndividualBeneficiaryDateOfBirthController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualBeneficiaryDateOfBirth                        controllers.IndividualBeneficiaryDateOfBirthController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualBeneficiaryDateOfBirth.title = IndividualBeneficiaryDateOfBirth" >> ../conf/messages.en
echo "individualBeneficiaryDateOfBirth.heading = IndividualBeneficiaryDateOfBirth" >> ../conf/messages.en
echo "individualBeneficiaryDateOfBirth.checkYourAnswersLabel = IndividualBeneficiaryDateOfBirth" >> ../conf/messages.en
echo "individualBeneficiaryDateOfBirth.error.required.all = Enter the individualBeneficiaryDateOfBirth" >> ../conf/messages.en
echo "individualBeneficiaryDateOfBirth.error.required.two = The individualBeneficiaryDateOfBirth" must include {0} and {1} >> ../conf/messages.en
echo "individualBeneficiaryDateOfBirth.error.required = The individualBeneficiaryDateOfBirth must include {0}" >> ../conf/messages.en
echo "individualBeneficiaryDateOfBirth.error.invalid = Enter a real IndividualBeneficiaryDateOfBirth" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryDateOfBirthUserAnswersEntry: Arbitrary[(IndividualBeneficiaryDateOfBirthPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualBeneficiaryDateOfBirthPage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryDateOfBirthPage: Arbitrary[IndividualBeneficiaryDateOfBirthPage.type] =";\
    print "    Arbitrary(IndividualBeneficiaryDateOfBirthPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualBeneficiaryDateOfBirthPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def individualBeneficiaryDateOfBirth: Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryDateOfBirthPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"individualBeneficiaryDateOfBirth.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x.format(dateFormatter)),";\
     print "        routes.IndividualBeneficiaryDateOfBirthController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IndividualBeneficiaryDateOfBirth completed"
