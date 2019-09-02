#!/bin/bash

echo ""
echo "Applying migration SettlorBusinessDetails"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/settlorBusinessDetails                        controllers.SettlorBusinessDetailsController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/settlorBusinessDetails                        controllers.SettlorBusinessDetailsController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeSettlorBusinessDetails                  controllers.SettlorBusinessDetailsController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeSettlorBusinessDetails                  controllers.SettlorBusinessDetailsController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorBusinessDetails.title = settlorBusinessDetails" >> ../conf/messages.en
echo "settlorBusinessDetails.heading = settlorBusinessDetails" >> ../conf/messages.en
echo "settlorBusinessDetails.checkYourAnswersLabel = settlorBusinessDetails" >> ../conf/messages.en
echo "settlorBusinessDetails.error.required = Enter settlorBusinessDetails" >> ../conf/messages.en
echo "settlorBusinessDetails.error.length = SettlorBusinessDetails must be 105 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorBusinessDetailsUserAnswersEntry: Arbitrary[(SettlorBusinessDetailsPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorBusinessDetailsPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorBusinessDetailsPage: Arbitrary[SettlorBusinessDetailsPage.type] =";\
    print "    Arbitrary(SettlorBusinessDetailsPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorBusinessDetailsPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def settlorBusinessDetails: Option[AnswerRow] = userAnswers.get(SettlorBusinessDetailsPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorBusinessDetails.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.SettlorBusinessDetailsController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorBusinessDetails completed"
