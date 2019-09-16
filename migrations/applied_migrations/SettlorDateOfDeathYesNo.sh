#!/bin/bash

echo ""
echo "Applying migration SettlorDateOfDeathYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /settlorDateOfDeathYesNo                        controllers.deceased_settlor.SettlorDateOfDeathYesNoController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /settlorDateOfDeathYesNo                        controllers.deceased_settlor.SettlorDateOfDeathYesNoController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSettlorDateOfDeathYesNo                  controllers.deceased_settlor.SettlorDateOfDeathYesNoController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSettlorDateOfDeathYesNo                  controllers.deceased_settlor.SettlorDateOfDeathYesNoController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorDateOfDeathYesNo.title = settlorDateOfDeathYesNo" >> ../conf/messages.en
echo "settlorDateOfDeathYesNo.heading = settlorDateOfDeathYesNo" >> ../conf/messages.en
echo "settlorDateOfDeathYesNo.checkYourAnswersLabel = settlorDateOfDeathYesNo" >> ../conf/messages.en
echo "settlorDateOfDeathYesNo.error.required = Select yes if settlorDateOfDeathYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorDateOfDeathYesNoUserAnswersEntry: Arbitrary[(SettlorDateOfDeathYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorDateOfDeathYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorDateOfDeathYesNoPage: Arbitrary[SettlorDateOfDeathYesNoPage.type] =";\
    print "    Arbitrary(SettlorDateOfDeathYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorDateOfDeathYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def settlorDateOfDeathYesNo: Option[AnswerRow] = userAnswers.get(SettlorDateOfDeathYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorDateOfDeathYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.SettlorDateOfDeathYesNoController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorDateOfDeathYesNo completed"
