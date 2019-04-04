#!/bin/bash

echo ""
echo "Applying migration AssetMoneyValue"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /assetMoneyValue                        controllers.AssetMoneyValueController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /assetMoneyValue                        controllers.AssetMoneyValueController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAssetMoneyValue                  controllers.AssetMoneyValueController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAssetMoneyValue                  controllers.AssetMoneyValueController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "assetMoneyValue.title = assetMoneyValue" >> ../conf/messages.en
echo "assetMoneyValue.heading = assetMoneyValue" >> ../conf/messages.en
echo "assetMoneyValue.checkYourAnswersLabel = assetMoneyValue" >> ../conf/messages.en
echo "assetMoneyValue.error.required = Enter assetMoneyValue" >> ../conf/messages.en
echo "assetMoneyValue.error.length = AssetMoneyValue must be 12 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAssetMoneyValueUserAnswersEntry: Arbitrary[(AssetMoneyValuePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AssetMoneyValuePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAssetMoneyValuePage: Arbitrary[AssetMoneyValuePage.type] =";\
    print "    Arbitrary(AssetMoneyValuePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AssetMoneyValuePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def assetMoneyValue: Option[AnswerRow] = userAnswers.get(AssetMoneyValuePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"assetMoneyValue.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.AssetMoneyValueController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AssetMoneyValue completed"
