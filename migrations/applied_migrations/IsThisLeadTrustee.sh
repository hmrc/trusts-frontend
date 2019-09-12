#!/bin/bash

echo ""
echo "Applying migration IsThisLeadTrustee"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /isThisLeadTrustee                        controllers.trustees.IsThisLeadTrusteeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /isThisLeadTrustee                        controllers.trustees.IsThisLeadTrusteeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIsThisLeadTrustee                  controllers.trustees.IsThisLeadTrusteeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIsThisLeadTrustee                  controllers.trustees.IsThisLeadTrusteeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "isThisLeadTrustee.title = isThisLeadTrustee" >> ../conf/messages.en
echo "isThisLeadTrustee.heading = isThisLeadTrustee" >> ../conf/messages.en
echo "isThisLeadTrustee.checkYourAnswersLabel = isThisLeadTrustee" >> ../conf/messages.en
echo "isThisLeadTrustee.error.required = Select yes if isThisLeadTrustee" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIsThisLeadTrusteeUserAnswersEntry: Arbitrary[(IsThisLeadTrusteePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IsThisLeadTrusteePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIsThisLeadTrusteePage: Arbitrary[IsThisLeadTrusteePage.type] =";\
    print "    Arbitrary(IsThisLeadTrusteePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IsThisLeadTrusteePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def isThisLeadTrustee: Option[AnswerRow] = userAnswers.get(IsThisLeadTrusteePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"isThisLeadTrustee.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.IsThisLeadTrusteeController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration IsThisLeadTrustee completed"
