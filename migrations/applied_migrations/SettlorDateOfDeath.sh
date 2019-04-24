#!/bin/bash

echo ""
echo "Applying migration SettlorDateOfDeath"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /settlorDateOfDeath                  controllers.SettlorDateOfDeathController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /settlorDateOfDeath                  controllers.SettlorDateOfDeathController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSettlorDateOfDeath                        controllers.SettlorDateOfDeathController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSettlorDateOfDeath                        controllers.SettlorDateOfDeathController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorDateOfDeath.title = SettlorDateOfDeath" >> ../conf/messages.en
echo "settlorDateOfDeath.heading = SettlorDateOfDeath" >> ../conf/messages.en
echo "settlorDateOfDeath.checkYourAnswersLabel = SettlorDateOfDeath" >> ../conf/messages.en
echo "settlorDateOfDeath.error.required.all = Enter the settlorDateOfDeath" >> ../conf/messages.en
echo "settlorDateOfDeath.error.required.two = The settlorDateOfDeath" must include {0} and {1} >> ../conf/messages.en
echo "settlorDateOfDeath.error.required = The settlorDateOfDeath must include {0}" >> ../conf/messages.en
echo "settlorDateOfDeath.error.invalid = Enter a real SettlorDateOfDeath" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorDateOfDeathUserAnswersEntry: Arbitrary[(SettlorDateOfDeathPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorDateOfDeathPage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorDateOfDeathPage: Arbitrary[SettlorDateOfDeathPage.type] =";\
    print "    Arbitrary(SettlorDateOfDeathPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorDateOfDeathPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def settlorDateOfDeath: Option[AnswerRow] = userAnswers.get(SettlorDateOfDeathPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorDateOfDeath.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x.format(dateFormatter)),";\
     print "        routes.SettlorDateOfDeathController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorDateOfDeath completed"
