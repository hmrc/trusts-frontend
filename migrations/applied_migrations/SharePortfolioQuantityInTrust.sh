#!/bin/bash

echo ""
echo "Applying migration SharePortfolioQuantityInTrust"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /sharePortfolioQuantityInTrust                        controllers.register.asset.shares.SharePortfolioQuantityInTrustController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /sharePortfolioQuantityInTrust                        controllers.register.asset.shares.SharePortfolioQuantityInTrustController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSharePortfolioQuantityInTrust                  controllers.register.asset.shares.SharePortfolioQuantityInTrustController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSharePortfolioQuantityInTrust                  controllers.register.asset.shares.SharePortfolioQuantityInTrustController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "sharePortfolioQuantityInTrust.title = sharePortfolioQuantityInTrust" >> ../conf/messages.en
echo "sharePortfolioQuantityInTrust.heading = sharePortfolioQuantityInTrust" >> ../conf/messages.en
echo "sharePortfolioQuantityInTrust.checkYourAnswersLabel = sharePortfolioQuantityInTrust" >> ../conf/messages.en
echo "sharePortfolioQuantityInTrust.error.required = Enter sharePortfolioQuantityInTrust" >> ../conf/messages.en
echo "sharePortfolioQuantityInTrust.error.length = SharePortfolioQuantityInTrust must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySharePortfolioQuantityInTrustUserAnswersEntry: Arbitrary[(SharePortfolioQuantityInTrustPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SharePortfolioQuantityInTrustPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySharePortfolioQuantityInTrustPage: Arbitrary[SharePortfolioQuantityInTrustPage.type] =";\
    print "    Arbitrary(SharePortfolioQuantityInTrustPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SharePortfolioQuantityInTrustPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def sharePortfolioQuantityInTrust: Option[AnswerRow] = userAnswers.get(SharePortfolioQuantityInTrustPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"sharePortfolioQuantityInTrust.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.SharePortfolioQuantityInTrustController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SharePortfolioQuantityInTrust completed"
