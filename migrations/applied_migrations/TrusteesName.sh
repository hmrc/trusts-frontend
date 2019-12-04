#!/bin/bash

echo ""
echo "Applying migration TrusteesName"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trusteesName                        controllers.register.trustees.TrusteesNameController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trusteesName                        controllers.register.trustees.TrusteesNameController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTrusteesName                  controllers.register.trustees.TrusteesNameController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTrusteesName                  controllers.register.trustees.TrusteesNameController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trusteesName.title = trusteesName" >> ../conf/messages.en
echo "trusteesName.heading = trusteesName" >> ../conf/messages.en
echo "trusteesName.checkYourAnswersLabel = trusteesName" >> ../conf/messages.en
echo "trusteesName.error.required = Enter trusteesName" >> ../conf/messages.en
echo "trusteesName.error.length = TrusteesName must be 53 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrusteesNameUserAnswersEntry: Arbitrary[(TrusteesNamePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TrusteesNamePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrusteesNamePage: Arbitrary[TrusteesNamePage.type] =";\
    print "    Arbitrary(TrusteesNamePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TrusteesNamePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def trusteesName: Option[AnswerRow] = userAnswers.get(TrusteesNamePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"trusteesName.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.TrusteesNameController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TrusteesName completed"
