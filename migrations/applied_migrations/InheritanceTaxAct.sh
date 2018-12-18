#!/bin/bash

echo ""
echo "Applying migration InheritanceTaxAct"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /inheritanceTaxAct                        controllers.InheritanceTaxActController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /inheritanceTaxAct                        controllers.InheritanceTaxActController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeInheritanceTaxAct                  controllers.InheritanceTaxActController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeInheritanceTaxAct                  controllers.InheritanceTaxActController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "inheritanceTaxAct.title = inheritanceTaxAct" >> ../conf/messages.en
echo "inheritanceTaxAct.heading = inheritanceTaxAct" >> ../conf/messages.en
echo "inheritanceTaxAct.checkYourAnswersLabel = inheritanceTaxAct" >> ../conf/messages.en
echo "inheritanceTaxAct.error.required = Select yes if inheritanceTaxAct" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryInheritanceTaxActUserAnswersEntry: Arbitrary[(InheritanceTaxActPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[InheritanceTaxActPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryInheritanceTaxActPage: Arbitrary[InheritanceTaxActPage.type] =";\
    print "    Arbitrary(InheritanceTaxActPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(InheritanceTaxActPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def inheritanceTaxAct: Option[AnswerRow] = userAnswers.get(InheritanceTaxActPage) map {";\
     print "    x => AnswerRow(\"inheritanceTaxAct.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.InheritanceTaxActController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration InheritanceTaxAct completed"
