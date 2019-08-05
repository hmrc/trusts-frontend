#!/bin/bash

echo ""
echo "Applying migration SharePortfolioValueInTrust"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /sharePortfolioValueInTrust                        controllers.shares.SharePortfolioValueInTrustController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /sharePortfolioValueInTrust                        controllers.shares.SharePortfolioValueInTrustController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSharePortfolioValueInTrust                  controllers.shares.SharePortfolioValueInTrustController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSharePortfolioValueInTrust                  controllers.shares.SharePortfolioValueInTrustController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "sharePortfolioValueInTrust.title = sharePortfolioValueInTrust" >> ../conf/messages.en
echo "sharePortfolioValueInTrust.heading = sharePortfolioValueInTrust" >> ../conf/messages.en
echo "sharePortfolioValueInTrust.checkYourAnswersLabel = sharePortfolioValueInTrust" >> ../conf/messages.en
echo "sharePortfolioValueInTrust.error.required = Enter sharePortfolioValueInTrust" >> ../conf/messages.en
echo "sharePortfolioValueInTrust.error.length = SharePortfolioValueInTrust must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySharePortfolioValueInTrustUserAnswersEntry: Arbitrary[(SharePortfolioValueInTrustPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SharePortfolioValueInTrustPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySharePortfolioValueInTrustPage: Arbitrary[SharePortfolioValueInTrustPage.type] =";\
    print "    Arbitrary(SharePortfolioValueInTrustPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SharePortfolioValueInTrustPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def sharePortfolioValueInTrust: Option[AnswerRow] = userAnswers.get(SharePortfolioValueInTrustPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"sharePortfolioValueInTrust.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.SharePortfolioValueInTrustController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SharePortfolioValueInTrust completed"
