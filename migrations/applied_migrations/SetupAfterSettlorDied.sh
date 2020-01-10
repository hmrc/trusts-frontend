#!/bin/bash

echo ""
echo "Applying migration SetUpAfterSettlorDied"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /setUpAfterSettlorDied                        controllers.register.settlors.deceased_settlor.SetUpAfterSettlorDiedController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /setUpAfterSettlorDied                        controllers.register.settlors.deceased_settlor.SetUpAfterSettlorDiedController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSetUpAfterSettlorDied                  controllers.register.settlors.deceased_settlor.SetUpAfterSettlorDiedController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSetUpAfterSettlorDied                  controllers.register.settlors.deceased_settlor.SetUpAfterSettlorDiedController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "setUpAfterSettlorDied.title = setUpAfterSettlorDied" >> ../conf/messages.en
echo "setUpAfterSettlorDied.heading = setUpAfterSettlorDied" >> ../conf/messages.en
echo "setUpAfterSettlorDied.checkYourAnswersLabel = setUpAfterSettlorDied" >> ../conf/messages.en
echo "setUpAfterSettlorDied.error.required = Select yes if setUpAfterSettlorDied" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySetUpAfterSettlorDiedUserAnswersEntry: Arbitrary[(SetUpAfterSettlorDiedPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SetUpAfterSettlorDiedPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySetUpAfterSettlorDiedPage: Arbitrary[SetUpAfterSettlorDiedPage.type] =";\
    print "    Arbitrary(SetUpAfterSettlorDiedPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SetUpAfterSettlorDiedPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def setUpAfterSettlorDied: Option[AnswerRow] = userAnswers.get(SetUpAfterSettlorDiedPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"setUpAfterSettlorDied.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.SetUpAfterSettlorDiedController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SetUpAfterSettlorDied completed"
