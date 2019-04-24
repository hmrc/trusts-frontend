#!/bin/bash

echo ""
echo "Applying migration SettlorDateOfBirthYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /settlorDateOfBirthYesNo                        controllers.SettlorDateOfBirthYesNoController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /settlorDateOfBirthYesNo                        controllers.SettlorDateOfBirthYesNoController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSettlorDateOfBirthYesNo                  controllers.SettlorDateOfBirthYesNoController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSettlorDateOfBirthYesNo                  controllers.SettlorDateOfBirthYesNoController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorDateOfBirthYesNo.title = settlorDateOfBirthYesNo" >> ../conf/messages.en
echo "settlorDateOfBirthYesNo.heading = settlorDateOfBirthYesNo" >> ../conf/messages.en
echo "settlorDateOfBirthYesNo.checkYourAnswersLabel = settlorDateOfBirthYesNo" >> ../conf/messages.en
echo "settlorDateOfBirthYesNo.error.required = Select yes if settlorDateOfBirthYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorDateOfBirthYesNoUserAnswersEntry: Arbitrary[(SettlorDateOfBirthYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorDateOfBirthYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorDateOfBirthYesNoPage: Arbitrary[SettlorDateOfBirthYesNoPage.type] =";\
    print "    Arbitrary(SettlorDateOfBirthYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorDateOfBirthYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def settlorDateOfBirthYesNo: Option[AnswerRow] = userAnswers.get(SettlorDateOfBirthYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorDateOfBirthYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.SettlorDateOfBirthYesNoController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorDateOfBirthYesNo completed"
