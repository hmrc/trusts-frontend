#!/bin/bash

echo ""
echo "Applying migration SettlorIndividualPassport"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/settlorIndividualPassport                        controllers.SettlorIndividualPassportController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/settlorIndividualPassport                        controllers.SettlorIndividualPassportController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeSettlorIndividualPassport                  controllers.SettlorIndividualPassportController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeSettlorIndividualPassport                  controllers.SettlorIndividualPassportController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorIndividualPassport.title = settlorIndividualPassport" >> ../conf/messages.en
echo "settlorIndividualPassport.heading = settlorIndividualPassport" >> ../conf/messages.en
echo "settlorIndividualPassport.field1 = Field 1" >> ../conf/messages.en
echo "settlorIndividualPassport.field2 = Field 2" >> ../conf/messages.en
echo "settlorIndividualPassport.checkYourAnswersLabel = settlorIndividualPassport" >> ../conf/messages.en
echo "settlorIndividualPassport.error.field1.required = Enter field1" >> ../conf/messages.en
echo "settlorIndividualPassport.error.field2.required = Enter field2" >> ../conf/messages.en
echo "settlorIndividualPassport.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "settlorIndividualPassport.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualPassportUserAnswersEntry: Arbitrary[(SettlorIndividualPassportPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorIndividualPassportPage.type]";\
    print "        value <- arbitrary[SettlorIndividualPassport].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualPassportPage: Arbitrary[SettlorIndividualPassportPage.type] =";\
    print "    Arbitrary(SettlorIndividualPassportPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualPassport: Arbitrary[SettlorIndividualPassport] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield SettlorIndividualPassport(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorIndividualPassportPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def settlorIndividualPassport: Option[AnswerRow] = userAnswers.get(SettlorIndividualPassportPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorIndividualPassport.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(s\"${x.field1} ${x.field2}\"),";\
     print "        routes.SettlorIndividualPassportController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorIndividualPassport completed"
