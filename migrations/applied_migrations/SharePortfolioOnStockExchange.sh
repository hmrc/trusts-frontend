#!/bin/bash

echo ""
echo "Applying migration SharePortfolioOnStockExchange"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /sharePortfolioOnStockExchange                        controllers.SharePortfolioOnStockExchangeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /sharePortfolioOnStockExchange                        controllers.SharePortfolioOnStockExchangeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSharePortfolioOnStockExchange                  controllers.SharePortfolioOnStockExchangeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSharePortfolioOnStockExchange                  controllers.SharePortfolioOnStockExchangeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "sharePortfolioOnStockExchange.title = sharePortfolioOnStockExchange" >> ../conf/messages.en
echo "sharePortfolioOnStockExchange.heading = sharePortfolioOnStockExchange" >> ../conf/messages.en
echo "sharePortfolioOnStockExchange.checkYourAnswersLabel = sharePortfolioOnStockExchange" >> ../conf/messages.en
echo "sharePortfolioOnStockExchange.error.required = Select yes if sharePortfolioOnStockExchange" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySharePortfolioOnStockExchangeUserAnswersEntry: Arbitrary[(SharePortfolioOnStockExchangePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SharePortfolioOnStockExchangePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySharePortfolioOnStockExchangePage: Arbitrary[SharePortfolioOnStockExchangePage.type] =";\
    print "    Arbitrary(SharePortfolioOnStockExchangePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SharePortfolioOnStockExchangePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def sharePortfolioOnStockExchange: Option[AnswerRow] = userAnswers.get(SharePortfolioOnStockExchangePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"sharePortfolioOnStockExchange.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.SharePortfolioOnStockExchangeController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SharePortfolioOnStockExchange completed"
