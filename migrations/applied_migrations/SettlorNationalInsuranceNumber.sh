#!/bin/bash

echo ""
echo "Applying migration SettlorNationalInsuranceNumber"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /settlorNationalInsuranceNumber                        controllers.SettlorNationalInsuranceNumberController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /settlorNationalInsuranceNumber                        controllers.SettlorNationalInsuranceNumberController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSettlorNationalInsuranceNumber                  controllers.SettlorNationalInsuranceNumberController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSettlorNationalInsuranceNumber                  controllers.SettlorNationalInsuranceNumberController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorNationalInsuranceNumber.title = settlorNationalInsuranceNumber" >> ../conf/messages.en
echo "settlorNationalInsuranceNumber.heading = settlorNationalInsuranceNumber" >> ../conf/messages.en
echo "settlorNationalInsuranceNumber.checkYourAnswersLabel = settlorNationalInsuranceNumber" >> ../conf/messages.en
echo "settlorNationalInsuranceNumber.error.required = Enter settlorNationalInsuranceNumber" >> ../conf/messages.en
echo "settlorNationalInsuranceNumber.error.length = SettlorNationalInsuranceNumber must be 9 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorNationalInsuranceNumberUserAnswersEntry: Arbitrary[(SettlorNationalInsuranceNumberPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorNationalInsuranceNumberPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorNationalInsuranceNumberPage: Arbitrary[SettlorNationalInsuranceNumberPage.type] =";\
    print "    Arbitrary(SettlorNationalInsuranceNumberPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorNationalInsuranceNumberPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def settlorNationalInsuranceNumber: Option[AnswerRow] = userAnswers.get(SettlorNationalInsuranceNumberPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorNationalInsuranceNumber.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.SettlorNationalInsuranceNumberController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorNationalInsuranceNumber completed"
