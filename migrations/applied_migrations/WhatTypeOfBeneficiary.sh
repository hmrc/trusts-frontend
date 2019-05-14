#!/bin/bash

echo ""
echo "Applying migration WhatTypeOfBeneficiary"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whatTypeOfBeneficiary                        controllers.WhatTypeOfBeneficiaryController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whatTypeOfBeneficiary                        controllers.WhatTypeOfBeneficiaryController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhatTypeOfBeneficiary                  controllers.WhatTypeOfBeneficiaryController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhatTypeOfBeneficiary                  controllers.WhatTypeOfBeneficiaryController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whatTypeOfBeneficiary.title = whatTypeOfBeneficiary" >> ../conf/messages.en
echo "whatTypeOfBeneficiary.heading = whatTypeOfBeneficiary" >> ../conf/messages.en
echo "whatTypeOfBeneficiary.option1 = Option 1" >> ../conf/messages.en
echo "whatTypeOfBeneficiary.option2 = Option 2" >> ../conf/messages.en
echo "whatTypeOfBeneficiary.checkYourAnswersLabel = whatTypeOfBeneficiary" >> ../conf/messages.en
echo "whatTypeOfBeneficiary.error.required = Select whatTypeOfBeneficiary" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatTypeOfBeneficiaryUserAnswersEntry: Arbitrary[(WhatTypeOfBeneficiaryPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhatTypeOfBeneficiaryPage.type]";\
    print "        value <- arbitrary[WhatTypeOfBeneficiary].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatTypeOfBeneficiaryPage: Arbitrary[WhatTypeOfBeneficiaryPage.type] =";\
    print "    Arbitrary(WhatTypeOfBeneficiaryPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatTypeOfBeneficiary: Arbitrary[WhatTypeOfBeneficiary] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(WhatTypeOfBeneficiary.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhatTypeOfBeneficiaryPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whatTypeOfBeneficiary: Option[AnswerRow] = userAnswers.get(WhatTypeOfBeneficiaryPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"whatTypeOfBeneficiary.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(messages(s\"whatTypeOfBeneficiary.$x\")),";\
     print "        routes.WhatTypeOfBeneficiaryController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhatTypeOfBeneficiary completed"
