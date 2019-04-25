#!/bin/bash

echo ""
echo "Applying migration IndividualBeneficiaryAddressUK"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualBeneficiaryAddressUK                        controllers.IndividualBeneficiaryAddressUKController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualBeneficiaryAddressUK                        controllers.IndividualBeneficiaryAddressUKController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualBeneficiaryAddressUK                  controllers.IndividualBeneficiaryAddressUKController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualBeneficiaryAddressUK                  controllers.IndividualBeneficiaryAddressUKController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualBeneficiaryAddressUK.title = individualBeneficiaryAddressUK" >> ../conf/messages.en
echo "individualBeneficiaryAddressUK.heading = individualBeneficiaryAddressUK" >> ../conf/messages.en
echo "individualBeneficiaryAddressUK.field1 = Field 1" >> ../conf/messages.en
echo "individualBeneficiaryAddressUK.field2 = Field 2" >> ../conf/messages.en
echo "individualBeneficiaryAddressUK.checkYourAnswersLabel = individualBeneficiaryAddressUK" >> ../conf/messages.en
echo "individualBeneficiaryAddressUK.error.field1.required = Enter field1" >> ../conf/messages.en
echo "individualBeneficiaryAddressUK.error.field2.required = Enter field2" >> ../conf/messages.en
echo "individualBeneficiaryAddressUK.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "individualBeneficiaryAddressUK.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryAddressUKUserAnswersEntry: Arbitrary[(IndividualBeneficiaryAddressUKPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualBeneficiaryAddressUKPage.type]";\
    print "        value <- arbitrary[IndividualBeneficiaryAddressUK].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryAddressUKPage: Arbitrary[IndividualBeneficiaryAddressUKPage.type] =";\
    print "    Arbitrary(IndividualBeneficiaryAddressUKPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryAddressUK: Arbitrary[IndividualBeneficiaryAddressUK] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield IndividualBeneficiaryAddressUK(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualBeneficiaryAddressUKPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def individualBeneficiaryAddressUK: Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryAddressUKPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"individualBeneficiaryAddressUK.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(s\"${x.field1} ${x.field2}\"),";\
     print "        routes.IndividualBeneficiaryAddressUKController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IndividualBeneficiaryAddressUK completed"
