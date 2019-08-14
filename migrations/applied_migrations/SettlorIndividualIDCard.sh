#!/bin/bash

echo ""
echo "Applying migration SettlorIndividualIDCard"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/settlorIndividualIDCard                        controllers.SettlorIndividualIDCardController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/settlorIndividualIDCard                        controllers.SettlorIndividualIDCardController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeSettlorIndividualIDCard                  controllers.SettlorIndividualIDCardController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeSettlorIndividualIDCard                  controllers.SettlorIndividualIDCardController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorIndividualIDCard.title = settlorIndividualIDCard" >> ../conf/messages.en
echo "settlorIndividualIDCard.heading = settlorIndividualIDCard" >> ../conf/messages.en
echo "settlorIndividualIDCard.field1 = Field 1" >> ../conf/messages.en
echo "settlorIndividualIDCard.field2 = Field 2" >> ../conf/messages.en
echo "settlorIndividualIDCard.checkYourAnswersLabel = settlorIndividualIDCard" >> ../conf/messages.en
echo "settlorIndividualIDCard.error.field1.required = Enter field1" >> ../conf/messages.en
echo "settlorIndividualIDCard.error.field2.required = Enter field2" >> ../conf/messages.en
echo "settlorIndividualIDCard.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "settlorIndividualIDCard.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualIDCardUserAnswersEntry: Arbitrary[(SettlorIndividualIDCardPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorIndividualIDCardPage.type]";\
    print "        value <- arbitrary[SettlorIndividualIDCard].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualIDCardPage: Arbitrary[SettlorIndividualIDCardPage.type] =";\
    print "    Arbitrary(SettlorIndividualIDCardPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualIDCard: Arbitrary[SettlorIndividualIDCard] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield SettlorIndividualIDCard(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorIndividualIDCardPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def settlorIndividualIDCard: Option[AnswerRow] = userAnswers.get(SettlorIndividualIDCardPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorIndividualIDCard.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(s\"${x.field1} ${x.field2}\"),";\
     print "        routes.SettlorIndividualIDCardController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorIndividualIDCard completed"
