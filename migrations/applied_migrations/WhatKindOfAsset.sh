#!/bin/bash

echo ""
echo "Applying migration WhatKindOfAsset"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whatKindOfAsset                        controllers.register.asset.WhatKindOfAssetController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whatKindOfAsset                        controllers.register.asset.WhatKindOfAssetController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhatKindOfAsset                  controllers.register.asset.WhatKindOfAssetController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhatKindOfAsset                  controllers.register.asset.WhatKindOfAssetController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whatKindOfAsset.title = WhatKindOfAsset" >> ../conf/messages.en
echo "whatKindOfAsset.heading = WhatKindOfAsset" >> ../conf/messages.en
echo "whatKindOfAsset.one = one" >> ../conf/messages.en
echo "whatKindOfAsset.two = two" >> ../conf/messages.en
echo "whatKindOfAsset.checkYourAnswersLabel = WhatKindOfAsset" >> ../conf/messages.en
echo "whatKindOfAsset.error.required = Select whatKindOfAsset" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatKindOfAssetUserAnswersEntry: Arbitrary[(WhatKindOfAssetPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhatKindOfAssetPage.type]";\
    print "        value <- arbitrary[WhatKindOfAsset].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatKindOfAssetPage: Arbitrary[WhatKindOfAssetPage.type] =";\
    print "    Arbitrary(WhatKindOfAssetPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatKindOfAsset: Arbitrary[WhatKindOfAsset] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(WhatKindOfAsset.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhatKindOfAssetPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whatKindOfAsset: Option[AnswerRow] = userAnswers.get(WhatKindOfAssetPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"whatKindOfAsset.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(messages(s\"whatKindOfAsset.$x\")),";\
     print "        routes.WhatKindOfAssetController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhatKindOfAsset completed"
