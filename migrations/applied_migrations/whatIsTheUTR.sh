#!/bin/bash

echo ""
echo "Applying migration whatIsTheUTR"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whatIsTheUTR                  controllers.whatIsTheUTRController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whatIsTheUTR                  controllers.whatIsTheUTRController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changewhatIsTheUTR                        controllers.whatIsTheUTRController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changewhatIsTheUTR                        controllers.whatIsTheUTRController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whatIsTheUTR.title = whatIsTheUTR" >> ../conf/messages.en
echo "whatIsTheUTR.heading = whatIsTheUTR" >> ../conf/messages.en
echo "whatIsTheUTR.checkYourAnswersLabel = whatIsTheUTR" >> ../conf/messages.en
echo "whatIsTheUTR.error.nonNumeric = Enter your whatIsTheUTR using numbers" >> ../conf/messages.en
echo "whatIsTheUTR.error.required = Enter your whatIsTheUTR" >> ../conf/messages.en
echo "whatIsTheUTR.error.wholeNumber = Enter your whatIsTheUTR using whole numbers" >> ../conf/messages.en
echo "whatIsTheUTR.error.outOfRange = whatIsTheUTR must be between {0} and {1}" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarywhatIsTheUTRUserAnswersEntry: Arbitrary[(whatIsTheUTRPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[whatIsTheUTRPage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarywhatIsTheUTRPage: Arbitrary[whatIsTheUTRPage.type] =";\
    print "    Arbitrary(whatIsTheUTRPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(whatIsTheUTRPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whatIsTheUTR: Option[AnswerRow] = userAnswers.get(whatIsTheUTRPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"whatIsTheUTR.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x.toString),";\
     print "        routes.whatIsTheUTRController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\	     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration whatIsTheUTR completed"
