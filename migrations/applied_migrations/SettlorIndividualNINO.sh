#!/bin/bash

echo ""
echo "Applying migration SettlorIndividualNINO"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/settlorIndividualNINO                        controllers.SettlorIndividualNINOController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/settlorIndividualNINO                        controllers.SettlorIndividualNINOController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeSettlorIndividualNINO                  controllers.SettlorIndividualNINOController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeSettlorIndividualNINO                  controllers.SettlorIndividualNINOController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorIndividualNINO.title = settlorIndividualNINO" >> ../conf/messages.en
echo "settlorIndividualNINO.heading = settlorIndividualNINO" >> ../conf/messages.en
echo "settlorIndividualNINO.checkYourAnswersLabel = settlorIndividualNINO" >> ../conf/messages.en
echo "settlorIndividualNINO.error.required = Enter settlorIndividualNINO" >> ../conf/messages.en
echo "settlorIndividualNINO.error.length = SettlorIndividualNINO must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualNINOUserAnswersEntry: Arbitrary[(SettlorIndividualNINOPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorIndividualNINOPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualNINOPage: Arbitrary[SettlorIndividualNINOPage.type] =";\
    print "    Arbitrary(SettlorIndividualNINOPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorIndividualNINOPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def settlorIndividualNINO: Option[AnswerRow] = userAnswers.get(SettlorIndividualNINOPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorIndividualNINO.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.SettlorIndividualNINOController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorIndividualNINO completed"
