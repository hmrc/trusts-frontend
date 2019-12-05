#!/bin/bash

echo ""
echo "Applying migration TrusteesNino"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trusteesNino                        controllers.register.trustees.TrusteesNinoController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trusteesNino                        controllers.register.trustees.TrusteesNinoController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTrusteesNino                  controllers.register.trustees.TrusteesNinoController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTrusteesNino                  controllers.register.trustees.TrusteesNinoController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trusteesNino.title = trusteesNino" >> ../conf/messages.en
echo "trusteesNino.heading = trusteesNino" >> ../conf/messages.en
echo "trusteesNino.checkYourAnswersLabel = trusteesNino" >> ../conf/messages.en
echo "trusteesNino.error.required = Enter trusteesNino" >> ../conf/messages.en
echo "trusteesNino.error.length = TrusteesNino must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrusteesNinoUserAnswersEntry: Arbitrary[(TrusteesNinoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TrusteesNinoPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrusteesNinoPage: Arbitrary[TrusteesNinoPage.type] =";\
    print "    Arbitrary(TrusteesNinoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TrusteesNinoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def trusteesNino: Option[AnswerRow] = userAnswers.get(TrusteesNinoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"trusteesNino.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.TrusteesNinoController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TrusteesNino completed"
