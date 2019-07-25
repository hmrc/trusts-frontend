#!/bin/bash

echo ""
echo "Applying migration ShareValueInTrust"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /shareValueInTrust                        controllers.ShareValueInTrustController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /shareValueInTrust                        controllers.ShareValueInTrustController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeShareValueInTrust                  controllers.ShareValueInTrustController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeShareValueInTrust                  controllers.ShareValueInTrustController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "shareValueInTrust.title = shareValueInTrust" >> ../conf/messages.en
echo "shareValueInTrust.heading = shareValueInTrust" >> ../conf/messages.en
echo "shareValueInTrust.checkYourAnswersLabel = shareValueInTrust" >> ../conf/messages.en
echo "shareValueInTrust.error.required = Enter shareValueInTrust" >> ../conf/messages.en
echo "shareValueInTrust.error.length = ShareValueInTrust must be 12 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryShareValueInTrustUserAnswersEntry: Arbitrary[(ShareValueInTrustPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ShareValueInTrustPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryShareValueInTrustPage: Arbitrary[ShareValueInTrustPage.type] =";\
    print "    Arbitrary(ShareValueInTrustPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ShareValueInTrustPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def shareValueInTrust: Option[AnswerRow] = userAnswers.get(ShareValueInTrustPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"shareValueInTrust.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.ShareValueInTrustController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ShareValueInTrust completed"
