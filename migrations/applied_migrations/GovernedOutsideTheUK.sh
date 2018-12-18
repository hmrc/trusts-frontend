#!/bin/bash

echo ""
echo "Applying migration GovernedOutsideTheUK"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /governedOutsideTheUK                        controllers.GovernedOutsideTheUKController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /governedOutsideTheUK                        controllers.GovernedOutsideTheUKController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeGovernedOutsideTheUK                  controllers.GovernedOutsideTheUKController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeGovernedOutsideTheUK                  controllers.GovernedOutsideTheUKController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "governedOutsideTheUK.title = governedOutsideTheUK" >> ../conf/messages.en
echo "governedOutsideTheUK.heading = governedOutsideTheUK" >> ../conf/messages.en
echo "governedOutsideTheUK.checkYourAnswersLabel = governedOutsideTheUK" >> ../conf/messages.en
echo "governedOutsideTheUK.error.required = Select yes if governedOutsideTheUK" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryGovernedOutsideTheUKUserAnswersEntry: Arbitrary[(GovernedOutsideTheUKPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[GovernedOutsideTheUKPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryGovernedOutsideTheUKPage: Arbitrary[GovernedOutsideTheUKPage.type] =";\
    print "    Arbitrary(GovernedOutsideTheUKPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(GovernedOutsideTheUKPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def governedOutsideTheUK: Option[AnswerRow] = userAnswers.get(GovernedOutsideTheUKPage) map {";\
     print "    x => AnswerRow(\"governedOutsideTheUK.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.GovernedOutsideTheUKController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration GovernedOutsideTheUK completed"
