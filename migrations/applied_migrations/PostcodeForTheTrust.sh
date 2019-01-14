#!/bin/bash

echo ""
echo "Applying migration PostcodeForTheTrust"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /postcodeForTheTrust                        controllers.PostcodeForTheTrustController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /postcodeForTheTrust                        controllers.PostcodeForTheTrustController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changePostcodeForTheTrust                  controllers.PostcodeForTheTrustController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changePostcodeForTheTrust                  controllers.PostcodeForTheTrustController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "postcodeForTheTrust.title = postcodeForTheTrust" >> ../conf/messages.en
echo "postcodeForTheTrust.heading = postcodeForTheTrust" >> ../conf/messages.en
echo "postcodeForTheTrust.checkYourAnswersLabel = postcodeForTheTrust" >> ../conf/messages.en
echo "postcodeForTheTrust.error.required = Enter postcodeForTheTrust" >> ../conf/messages.en
echo "postcodeForTheTrust.error.length = PostcodeForTheTrust must be 6 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPostcodeForTheTrustUserAnswersEntry: Arbitrary[(PostcodeForTheTrustPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[PostcodeForTheTrustPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPostcodeForTheTrustPage: Arbitrary[PostcodeForTheTrustPage.type] =";\
    print "    Arbitrary(PostcodeForTheTrustPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(PostcodeForTheTrustPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def postcodeForTheTrust: Option[AnswerRow] = userAnswers.get(PostcodeForTheTrustPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"postcodeForTheTrust.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.PostcodeForTheTrustController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration PostcodeForTheTrust completed"
