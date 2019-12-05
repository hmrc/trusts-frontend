#!/bin/bash

echo ""
echo "Applying migration IndividualBeneficiaryName"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualBeneficiaryName                        controllers.register.beneficiaries.IndividualBeneficiaryNameController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualBeneficiaryName                        controllers.register.beneficiaries.IndividualBeneficiaryNameController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualBeneficiaryName                  controllers.register.beneficiaries.IndividualBeneficiaryNameController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualBeneficiaryName                  controllers.register.beneficiaries.IndividualBeneficiaryNameController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualBeneficiaryName.title = individualBeneficiaryName" >> ../conf/messages.en
echo "individualBeneficiaryName.heading = individualBeneficiaryName" >> ../conf/messages.en
echo "individualBeneficiaryName.field1 = Field 1" >> ../conf/messages.en
echo "individualBeneficiaryName.field2 = Field 2" >> ../conf/messages.en
echo "individualBeneficiaryName.checkYourAnswersLabel = individualBeneficiaryName" >> ../conf/messages.en
echo "individualBeneficiaryName.error.field1.required = Enter field1" >> ../conf/messages.en
echo "individualBeneficiaryName.error.field2.required = Enter field2" >> ../conf/messages.en
echo "individualBeneficiaryName.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "individualBeneficiaryName.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryNameUserAnswersEntry: Arbitrary[(IndividualBeneficiaryNamePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualBeneficiaryNamePage.type]";\
    print "        value <- arbitrary[IndividualBeneficiaryName].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryNamePage: Arbitrary[IndividualBeneficiaryNamePage.type] =";\
    print "    Arbitrary(IndividualBeneficiaryNamePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBeneficiaryName: Arbitrary[IndividualBeneficiaryName] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield IndividualBeneficiaryName(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualBeneficiaryNamePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def individualBeneficiaryName: Option[AnswerRow] = userAnswers.get(IndividualBeneficiaryNamePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"individualBeneficiaryName.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(s\"${x.field1} ${x.field2}\"),";\
     print "        routes.IndividualBeneficiaryNameController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IndividualBeneficiaryName completed"
