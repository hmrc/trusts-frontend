#!/bin/bash

echo ""
echo "Applying migration AddATrustee"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /addATrustee                        controllers.register.trustees.AddATrusteeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /addATrustee                        controllers.register.trustees.AddATrusteeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAddATrustee                  controllers.register.trustees.AddATrusteeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAddATrustee                  controllers.register.trustees.AddATrusteeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "addATrustee.title = Add A Trustee" >> ../conf/messages.en
echo "addATrustee.heading = Add A Trustee" >> ../conf/messages.en
echo "addATrustee.1 = Yes, I want to add them now" >> ../conf/messages.en
echo "addATrustee.2 = Yes, I want to add them later" >> ../conf/messages.en
echo "addATrustee.checkYourAnswersLabel = Add A Trustee" >> ../conf/messages.en
echo "addATrustee.error.required = Select addATrustee" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAddATrusteeUserAnswersEntry: Arbitrary[(AddATrusteePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AddATrusteePage.type]";\
    print "        value <- arbitrary[AddATrustee].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAddATrusteePage: Arbitrary[AddATrusteePage.type] =";\
    print "    Arbitrary(AddATrusteePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAddATrustee: Arbitrary[AddATrustee] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(AddATrustee.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AddATrusteePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def addATrustee: Option[AnswerRow] = userAnswers.get(AddATrusteePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"addATrustee.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(messages(s\"addATrustee.$x\")),";\
     print "        routes.AddATrusteeController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AddATrustee completed"
