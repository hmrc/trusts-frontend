#!/bin/bash

echo ""
echo "Applying migration SettlorIndividualName"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/settlorIndividualName                        controllers.SettlorIndividualNameController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/settlorIndividualName                        controllers.SettlorIndividualNameController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeSettlorIndividualName                  controllers.SettlorIndividualNameController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeSettlorIndividualName                  controllers.SettlorIndividualNameController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorIndividualName.title = settlorIndividualName" >> ../conf/messages.en
echo "settlorIndividualName.heading = settlorIndividualName" >> ../conf/messages.en
echo "settlorIndividualName.field1 = Field 1" >> ../conf/messages.en
echo "settlorIndividualName.field2 = Field 2" >> ../conf/messages.en
echo "settlorIndividualName.checkYourAnswersLabel = settlorIndividualName" >> ../conf/messages.en
echo "settlorIndividualName.error.field1.required = Enter field1" >> ../conf/messages.en
echo "settlorIndividualName.error.field2.required = Enter field2" >> ../conf/messages.en
echo "settlorIndividualName.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "settlorIndividualName.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualNameUserAnswersEntry: Arbitrary[(SettlorIndividualNamePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorIndividualNamePage.type]";\
    print "        value <- arbitrary[SettlorIndividualName].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualNamePage: Arbitrary[SettlorIndividualNamePage.type] =";\
    print "    Arbitrary(SettlorIndividualNamePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualName: Arbitrary[SettlorIndividualName] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield SettlorIndividualName(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorIndividualNamePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def settlorIndividualName: Option[AnswerRow] = userAnswers.get(SettlorIndividualNamePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorIndividualName.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(s\"${x.field1} ${x.field2}\"),";\
     print "        routes.SettlorIndividualNameController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorIndividualName completed"
