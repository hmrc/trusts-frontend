#!/bin/bash

echo ""
echo "Applying migration AddABeneficiary"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /addABeneficiary                        controllers.AddABeneficiaryController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /addABeneficiary                        controllers.AddABeneficiaryController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAddABeneficiary                  controllers.AddABeneficiaryController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAddABeneficiary                  controllers.AddABeneficiaryController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "addABeneficiary.title = AddABeneficiary" >> ../conf/messages.en
echo "addABeneficiary.heading = AddABeneficiary" >> ../conf/messages.en
echo "addABeneficiary.1 = 1" >> ../conf/messages.en
echo "addABeneficiary.2 = 2" >> ../conf/messages.en
echo "addABeneficiary.checkYourAnswersLabel = AddABeneficiary" >> ../conf/messages.en
echo "addABeneficiary.error.required = Select addABeneficiary" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAddABeneficiaryUserAnswersEntry: Arbitrary[(AddABeneficiaryPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AddABeneficiaryPage.type]";\
    print "        value <- arbitrary[AddABeneficiary].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAddABeneficiaryPage: Arbitrary[AddABeneficiaryPage.type] =";\
    print "    Arbitrary(AddABeneficiaryPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAddABeneficiary: Arbitrary[AddABeneficiary] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(AddABeneficiary.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AddABeneficiaryPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def addABeneficiary: Option[AnswerRow] = userAnswers.get(AddABeneficiaryPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"addABeneficiary.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(messages(s\"addABeneficiary.$x\")),";\
     print "        routes.AddABeneficiaryController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AddABeneficiary completed"
