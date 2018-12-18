#!/bin/bash

echo ""
echo "Applying migration AdministrationOutsideUK"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /administrationOutsideUK                        controllers.AdministrationOutsideUKController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /administrationOutsideUK                        controllers.AdministrationOutsideUKController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAdministrationOutsideUK                  controllers.AdministrationOutsideUKController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAdministrationOutsideUK                  controllers.AdministrationOutsideUKController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "administrationOutsideUK.title = administrationOutsideUK" >> ../conf/messages.en
echo "administrationOutsideUK.heading = administrationOutsideUK" >> ../conf/messages.en
echo "administrationOutsideUK.checkYourAnswersLabel = administrationOutsideUK" >> ../conf/messages.en
echo "administrationOutsideUK.error.required = Select yes if administrationOutsideUK" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAdministrationOutsideUKUserAnswersEntry: Arbitrary[(AdministrationOutsideUKPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AdministrationOutsideUKPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAdministrationOutsideUKPage: Arbitrary[AdministrationOutsideUKPage.type] =";\
    print "    Arbitrary(AdministrationOutsideUKPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AdministrationOutsideUKPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def administrationOutsideUK: Option[AnswerRow] = userAnswers.get(AdministrationOutsideUKPage) map {";\
     print "    x => AnswerRow(\"administrationOutsideUK.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.AdministrationOutsideUKController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AdministrationOutsideUK completed"
