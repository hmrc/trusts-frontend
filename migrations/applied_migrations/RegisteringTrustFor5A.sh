#!/bin/bash

echo ""
echo "Applying migration RegisteringTrustFor5A"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /registeringTrustFor5A                        controllers.register.RegisteringTrustFor5AController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /registeringTrustFor5A                        controllers.register.RegisteringTrustFor5AController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeRegisteringTrustFor5A                  controllers.register.RegisteringTrustFor5AController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeRegisteringTrustFor5A                  controllers.register.RegisteringTrustFor5AController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "registeringTrustFor5A.title = registeringTrustFor5A" >> ../conf/messages.en
echo "registeringTrustFor5A.heading = registeringTrustFor5A" >> ../conf/messages.en
echo "registeringTrustFor5A.checkYourAnswersLabel = registeringTrustFor5A" >> ../conf/messages.en
echo "registeringTrustFor5A.error.required = Select yes if registeringTrustFor5A" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryRegisteringTrustFor5AUserAnswersEntry: Arbitrary[(RegisteringTrustFor5APage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[RegisteringTrustFor5APage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryRegisteringTrustFor5APage: Arbitrary[RegisteringTrustFor5APage.type] =";\
    print "    Arbitrary(RegisteringTrustFor5APage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(RegisteringTrustFor5APage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def registeringTrustFor5A: Option[AnswerRow] = userAnswers.get(RegisteringTrustFor5APage) map {";\
     print "    x => AnswerRow(\"registeringTrustFor5A.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.RegisteringTrustFor5AController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration RegisteringTrustFor5A completed"
