#!/bin/bash

echo ""
echo "Applying migration trusteeAUKCitizen"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trusteeAUKCitizen                        controllers.trusteeAUKCitizenController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trusteeAUKCitizen                        controllers.trusteeAUKCitizenController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changetrusteeAUKCitizen                  controllers.trusteeAUKCitizenController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changetrusteeAUKCitizen                  controllers.trusteeAUKCitizenController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trusteeAUKCitizen.title = trusteeAUKCitizen" >> ../conf/messages.en
echo "trusteeAUKCitizen.heading = trusteeAUKCitizen" >> ../conf/messages.en
echo "trusteeAUKCitizen.checkYourAnswersLabel = trusteeAUKCitizen" >> ../conf/messages.en
echo "trusteeAUKCitizen.error.required = Select yes if trusteeAUKCitizen" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarytrusteeAUKCitizenUserAnswersEntry: Arbitrary[(trusteeAUKCitizenPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[trusteeAUKCitizenPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarytrusteeAUKCitizenPage: Arbitrary[trusteeAUKCitizenPage.type] =";\
    print "    Arbitrary(trusteeAUKCitizenPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(trusteeAUKCitizenPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def trusteeAUKCitizen: Option[AnswerRow] = userAnswers.get(trusteeAUKCitizenPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"trusteeAUKCitizen.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.trusteeAUKCitizenController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration trusteeAUKCitizen completed"
