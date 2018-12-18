#!/bin/bash

echo ""
echo "Applying migration TrustResidentOffshore"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trustResidentOffshore                        controllers.TrustResidentOffshoreController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trustResidentOffshore                        controllers.TrustResidentOffshoreController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTrustResidentOffshore                  controllers.TrustResidentOffshoreController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTrustResidentOffshore                  controllers.TrustResidentOffshoreController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trustResidentOffshore.title = trustResidentOffshore" >> ../conf/messages.en
echo "trustResidentOffshore.heading = trustResidentOffshore" >> ../conf/messages.en
echo "trustResidentOffshore.checkYourAnswersLabel = trustResidentOffshore" >> ../conf/messages.en
echo "trustResidentOffshore.error.required = Select yes if trustResidentOffshore" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustResidentOffshoreUserAnswersEntry: Arbitrary[(TrustResidentOffshorePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TrustResidentOffshorePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustResidentOffshorePage: Arbitrary[TrustResidentOffshorePage.type] =";\
    print "    Arbitrary(TrustResidentOffshorePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TrustResidentOffshorePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def trustResidentOffshore: Option[AnswerRow] = userAnswers.get(TrustResidentOffshorePage) map {";\
     print "    x => AnswerRow(\"trustResidentOffshore.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.TrustResidentOffshoreController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TrustResidentOffshore completed"
