#!/bin/bash

echo ""
echo "Applying migration AddAssets"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /addAssets                        controllers.register.asset.AddAssetsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /addAssets                        controllers.register.asset.AddAssetsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAddAssets                  controllers.register.asset.AddAssetsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAddAssets                  controllers.register.asset.AddAssetsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "addAssets.title = addAssets" >> ../conf/messages.en
echo "addAssets.heading = addAssets" >> ../conf/messages.en
echo "addAssets.option1 = Option 1" >> ../conf/messages.en
echo "addAssets.option2 = Option 2" >> ../conf/messages.en
echo "addAssets.checkYourAnswersLabel = addAssets" >> ../conf/messages.en
echo "addAssets.error.required = Select addAssets" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAddAssetsUserAnswersEntry: Arbitrary[(AddAssetsPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AddAssetsPage.type]";\
    print "        value <- arbitrary[AddAssets].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAddAssetsPage: Arbitrary[AddAssetsPage.type] =";\
    print "    Arbitrary(AddAssetsPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAddAssets: Arbitrary[AddAssets] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(AddAssets.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AddAssetsPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def addAssets: Option[AnswerRow] = userAnswers.get(AddAssetsPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"addAssets.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(messages(s\"addAssets.$x\")),";\
     print "        routes.AddAssetsController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AddAssets completed"
