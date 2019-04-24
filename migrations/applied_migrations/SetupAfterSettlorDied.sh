#!/bin/bash

echo ""
echo "Applying migration SetupAfterSettlorDied"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /setupAfterSettlorDied                        controllers.SetupAfterSettlorDiedController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /setupAfterSettlorDied                        controllers.SetupAfterSettlorDiedController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSetupAfterSettlorDied                  controllers.SetupAfterSettlorDiedController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSetupAfterSettlorDied                  controllers.SetupAfterSettlorDiedController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "setupAfterSettlorDied.title = setupAfterSettlorDied" >> ../conf/messages.en
echo "setupAfterSettlorDied.heading = setupAfterSettlorDied" >> ../conf/messages.en
echo "setupAfterSettlorDied.checkYourAnswersLabel = setupAfterSettlorDied" >> ../conf/messages.en
echo "setupAfterSettlorDied.error.required = Select yes if setupAfterSettlorDied" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySetupAfterSettlorDiedUserAnswersEntry: Arbitrary[(SetupAfterSettlorDiedPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SetupAfterSettlorDiedPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySetupAfterSettlorDiedPage: Arbitrary[SetupAfterSettlorDiedPage.type] =";\
    print "    Arbitrary(SetupAfterSettlorDiedPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SetupAfterSettlorDiedPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def setupAfterSettlorDied: Option[AnswerRow] = userAnswers.get(SetupAfterSettlorDiedPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"setupAfterSettlorDied.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.SetupAfterSettlorDiedController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SetupAfterSettlorDied completed"
