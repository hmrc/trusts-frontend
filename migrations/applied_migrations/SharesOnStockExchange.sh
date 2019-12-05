#!/bin/bash

echo ""
echo "Applying migration SharesOnStockExchange"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /sharesOnStockExchange                        controllers.register.asset.shares.SharesOnStockExchangeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /sharesOnStockExchange                        controllers.register.asset.shares.SharesOnStockExchangeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSharesOnStockExchange                  controllers.register.asset.shares.SharesOnStockExchangeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSharesOnStockExchange                  controllers.register.asset.shares.SharesOnStockExchangeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "sharesOnStockExchange.title = sharesOnStockExchange" >> ../conf/messages.en
echo "sharesOnStockExchange.heading = sharesOnStockExchange" >> ../conf/messages.en
echo "sharesOnStockExchange.checkYourAnswersLabel = sharesOnStockExchange" >> ../conf/messages.en
echo "sharesOnStockExchange.error.required = Select yes if sharesOnStockExchange" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySharesOnStockExchangeUserAnswersEntry: Arbitrary[(SharesOnStockExchangePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SharesOnStockExchangePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySharesOnStockExchangePage: Arbitrary[SharesOnStockExchangePage.type] =";\
    print "    Arbitrary(SharesOnStockExchangePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SharesOnStockExchangePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def sharesOnStockExchange: Option[AnswerRow] = userAnswers.get(SharesOnStockExchangePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"sharesOnStockExchange.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.SharesOnStockExchangeController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SharesOnStockExchange completed"
