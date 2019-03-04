#!/bin/bash

echo ""
echo "Applying migration TelephoneNumber"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /telephoneNumber                        controllers.TelephoneNumberController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /telephoneNumber                        controllers.TelephoneNumberController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTelephoneNumber                  controllers.TelephoneNumberController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTelephoneNumber                  controllers.TelephoneNumberController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "telephoneNumber.title = telephoneNumber" >> ../conf/messages.en
echo "telephoneNumber.heading = telephoneNumber" >> ../conf/messages.en
echo "telephoneNumber.checkYourAnswersLabel = telephoneNumber" >> ../conf/messages.en
echo "telephoneNumber.error.required = Enter telephoneNumber" >> ../conf/messages.en
echo "telephoneNumber.error.length = TelephoneNumber must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTelephoneNumberUserAnswersEntry: Arbitrary[(TelephoneNumberPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TelephoneNumberPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTelephoneNumberPage: Arbitrary[TelephoneNumberPage.type] =";\
    print "    Arbitrary(TelephoneNumberPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TelephoneNumberPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def telephoneNumber: Option[AnswerRow] = userAnswers.get(TelephoneNumberPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"telephoneNumber.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.TelephoneNumberController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TelephoneNumber completed"
