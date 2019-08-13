#!/bin/bash

echo ""
echo "Applying migration AddABeneficiaryYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/addABeneficiaryYesNo                        controllers.AddABeneficiaryYesNoController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/addABeneficiaryYesNo                        controllers.AddABeneficiaryYesNoController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeAddABeneficiaryYesNo                  controllers.AddABeneficiaryYesNoController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeAddABeneficiaryYesNo                  controllers.AddABeneficiaryYesNoController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "addABeneficiaryYesNo.title = addABeneficiaryYesNo" >> ../conf/messages.en
echo "addABeneficiaryYesNo.heading = addABeneficiaryYesNo" >> ../conf/messages.en
echo "addABeneficiaryYesNo.checkYourAnswersLabel = addABeneficiaryYesNo" >> ../conf/messages.en
echo "addABeneficiaryYesNo.error.required = Select yes if addABeneficiaryYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAddABeneficiaryYesNoUserAnswersEntry: Arbitrary[(AddABeneficiaryYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AddABeneficiaryYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAddABeneficiaryYesNoPage: Arbitrary[AddABeneficiaryYesNoPage.type] =";\
    print "    Arbitrary(AddABeneficiaryYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AddABeneficiaryYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def addABeneficiaryYesNo: Option[AnswerRow] = userAnswers.get(AddABeneficiaryYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"addABeneficiaryYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.AddABeneficiaryYesNoController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AddABeneficiaryYesNo completed"
