#!/bin/bash

echo ""
echo "Applying migration SettlorIndividualDateOfBirth"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/settlorIndividualDateOfBirth                  controllers.SettlorIndividualDateOfBirthController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/settlorIndividualDateOfBirth                  controllers.SettlorIndividualDateOfBirthController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeSettlorIndividualDateOfBirth                        controllers.SettlorIndividualDateOfBirthController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeSettlorIndividualDateOfBirth                        controllers.SettlorIndividualDateOfBirthController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorIndividualDateOfBirth.title = SettlorIndividualDateOfBirth" >> ../conf/messages.en
echo "settlorIndividualDateOfBirth.heading = SettlorIndividualDateOfBirth" >> ../conf/messages.en
echo "settlorIndividualDateOfBirth.checkYourAnswersLabel = SettlorIndividualDateOfBirth" >> ../conf/messages.en
echo "settlorIndividualDateOfBirth.error.required.all = Enter the settlorIndividualDateOfBirth" >> ../conf/messages.en
echo "settlorIndividualDateOfBirth.error.required.two = The settlorIndividualDateOfBirth" must include {0} and {1} >> ../conf/messages.en
echo "settlorIndividualDateOfBirth.error.required = The settlorIndividualDateOfBirth must include {0}" >> ../conf/messages.en
echo "settlorIndividualDateOfBirth.error.invalid = Enter a real SettlorIndividualDateOfBirth" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualDateOfBirthUserAnswersEntry: Arbitrary[(SettlorIndividualDateOfBirthPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorIndividualDateOfBirthPage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualDateOfBirthPage: Arbitrary[SettlorIndividualDateOfBirthPage.type] =";\
    print "    Arbitrary(SettlorIndividualDateOfBirthPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorIndividualDateOfBirthPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def settlorIndividualDateOfBirth: Option[AnswerRow] = userAnswers.get(SettlorIndividualDateOfBirthPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorIndividualDateOfBirth.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x.format(dateFormatter)),";\
     print "        routes.SettlorIndividualDateOfBirthController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorIndividualDateOfBirth completed"
