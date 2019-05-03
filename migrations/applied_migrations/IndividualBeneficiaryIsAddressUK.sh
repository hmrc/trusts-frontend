#!/bin/bash

echo ""
echo "Applying migration IndividualBeneficiaryIsAddressUK"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualBeneficiaryIsAddressUK                        controllers.IndividualBeneficiaryIsAddressUKController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualBeneficiaryIsAddressUK                        controllers.IndividualBeneficiaryIsAddressUKController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualBeneficiaryIsAddressUK                  controllers.IndividualBeneficiaryIsAddressUKController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualBeneficiaryIsAddressUK                  controllers.IndividualBeneficiaryIsAddressUKController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualBeneficiaryIsAddressUK.title = individualBeneficiaryIsAddressUK" >> ../conf/messages.en
echo "individualBeneficiaryIsAddressUK.heading = individualBeneficiaryIsAddressUK" >> ../conf/messages.en
echo "individualBeneficiaryIsAddressUK.checkYourAnswersLabel = individualBeneficiaryIsAddressUK" >> ../conf/messages.en
echo "individualBeneficiaryIsAddressUK.error.required = Select yes if individualBeneficiaryIsAddressUK" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryIsAddressUKUserAnswersEntry: Arbitrary[(IndividualBeneficiaryIsAddressUKPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualBeneficiaryIsAddressUKPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryIsAddressUKPage: Arbitrary[IndividualBeneficiaryIsAddressUKPage.type] =";\
    print "    Arbitrary(IndividualBeneficiaryIsAddressUKPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualBeneficiaryIsAddressUKPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def individualBeneficiaryIsAddressUK: Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryIsAddressUKPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"individualBeneficiaryIsAddressUK.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.IndividualBeneficiaryIsAddressUKController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IndividualBeneficiaryIsAddressUK completed"
