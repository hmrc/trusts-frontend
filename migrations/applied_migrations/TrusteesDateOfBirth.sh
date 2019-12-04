#!/bin/bash

echo ""
echo "Applying migration TrusteesDateOfBirth"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trusteesDateOfBirth                  controllers.register.trustees.TrusteesDateOfBirthController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trusteesDateOfBirth                  controllers.register.trustees.TrusteesDateOfBirthController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTrusteesDateOfBirth                        controllers.register.trustees.TrusteesDateOfBirthController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTrusteesDateOfBirth                        controllers.register.trustees.TrusteesDateOfBirthController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trusteesDateOfBirth.title = TrusteesDateOfBirth" >> ../conf/messages.en
echo "trusteesDateOfBirth.heading = TrusteesDateOfBirth" >> ../conf/messages.en
echo "trusteesDateOfBirth.checkYourAnswersLabel = TrusteesDateOfBirth" >> ../conf/messages.en
echo "trusteesDateOfBirth.error.required.all = Enter the trusteesDateOfBirth" >> ../conf/messages.en
echo "trusteesDateOfBirth.error.required.two = The trusteesDateOfBirth" must include {0} and {1} >> ../conf/messages.en
echo "trusteesDateOfBirth.error.required = The trusteesDateOfBirth must include {0}" >> ../conf/messages.en
echo "trusteesDateOfBirth.error.invalid = Enter a real TrusteesDateOfBirth" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrusteesDateOfBirthUserAnswersEntry: Arbitrary[(TrusteesDateOfBirthPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TrusteesDateOfBirthPage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrusteesDateOfBirthPage: Arbitrary[TrusteesDateOfBirthPage.type] =";\
    print "    Arbitrary(TrusteesDateOfBirthPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TrusteesDateOfBirthPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def trusteesDateOfBirth: Option[AnswerRow] = userAnswers.get(TrusteesDateOfBirthPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"trusteesDateOfBirth.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x.format(dateFormatter)),";\
     print "        routes.TrusteesDateOfBirthController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TrusteesDateOfBirth completed"
