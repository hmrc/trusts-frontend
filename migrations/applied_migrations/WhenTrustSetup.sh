#!/bin/bash

echo ""
echo "Applying migration WhenTrustSetup"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whenTrustSetup                  controllers.register.trust_details.WhenTrustSetupController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whenTrustSetup                  controllers.register.trust_details.WhenTrustSetupController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhenTrustSetup                        controllers.register.trust_details.WhenTrustSetupController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhenTrustSetup                        controllers.register.trust_details.WhenTrustSetupController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whenTrustSetup.title = WhenTrustSetup" >> ../conf/messages.en
echo "whenTrustSetup.heading = WhenTrustSetup" >> ../conf/messages.en
echo "whenTrustSetup.checkYourAnswersLabel = WhenTrustSetup" >> ../conf/messages.en
echo "whenTrustSetup.error.required.all = Enter the whenTrustSetup" >> ../conf/messages.en
echo "whenTrustSetup.error.required.two = The whenTrustSetup" must include {0} and {1} >> ../conf/messages.en
echo "whenTrustSetup.error.required = The whenTrustSetup must include {0}" >> ../conf/messages.en
echo "whenTrustSetup.error.invalid = Enter a real WhenTrustSetup" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhenTrustSetupUserAnswersEntry: Arbitrary[(WhenTrustSetupPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhenTrustSetupPage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhenTrustSetupPage: Arbitrary[WhenTrustSetupPage.type] =";\
    print "    Arbitrary(WhenTrustSetupPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhenTrustSetupPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whenTrustSetup: Option[AnswerRow] = userAnswers.get(WhenTrustSetupPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"whenTrustSetup.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x.format(dateFormatter)),";\
     print "        routes.WhenTrustSetupController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhenTrustSetup completed"
