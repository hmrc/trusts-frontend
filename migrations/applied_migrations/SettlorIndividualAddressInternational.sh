#!/bin/bash

echo ""
echo "Applying migration SettlorIndividualAddressInternational"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/settlorIndividualAddressInternational                        controllers.SettlorIndividualAddressInternationalController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/settlorIndividualAddressInternational                        controllers.SettlorIndividualAddressInternationalController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeSettlorIndividualAddressInternational                  controllers.SettlorIndividualAddressInternationalController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeSettlorIndividualAddressInternational                  controllers.SettlorIndividualAddressInternationalController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorIndividualAddressInternational.title = settlorIndividualAddressInternational" >> ../conf/messages.en
echo "settlorIndividualAddressInternational.heading = settlorIndividualAddressInternational" >> ../conf/messages.en
echo "settlorIndividualAddressInternational.field1 = Field 1" >> ../conf/messages.en
echo "settlorIndividualAddressInternational.field2 = Field 2" >> ../conf/messages.en
echo "settlorIndividualAddressInternational.checkYourAnswersLabel = settlorIndividualAddressInternational" >> ../conf/messages.en
echo "settlorIndividualAddressInternational.error.field1.required = Enter field1" >> ../conf/messages.en
echo "settlorIndividualAddressInternational.error.field2.required = Enter field2" >> ../conf/messages.en
echo "settlorIndividualAddressInternational.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "settlorIndividualAddressInternational.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualAddressInternationalUserAnswersEntry: Arbitrary[(SettlorIndividualAddressInternationalPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorIndividualAddressInternationalPage.type]";\
    print "        value <- arbitrary[SettlorIndividualAddressInternational].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualAddressInternationalPage: Arbitrary[SettlorIndividualAddressInternationalPage.type] =";\
    print "    Arbitrary(SettlorIndividualAddressInternationalPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualAddressInternational: Arbitrary[SettlorIndividualAddressInternational] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield SettlorIndividualAddressInternational(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorIndividualAddressInternationalPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def settlorIndividualAddressInternational: Option[AnswerRow] = userAnswers.get(SettlorIndividualAddressInternationalPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorIndividualAddressInternational.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(s\"${x.field1} ${x.field2}\"),";\
     print "        routes.SettlorIndividualAddressInternationalController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorIndividualAddressInternational completed"
