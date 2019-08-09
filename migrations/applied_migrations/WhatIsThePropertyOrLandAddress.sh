#!/bin/bash

echo ""
echo "Applying migration WhatIsThePropertyOrLandAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/whatIsThePropertyOrLandAddress                        controllers.WhatIsThePropertyOrLandAddressController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/whatIsThePropertyOrLandAddress                        controllers.WhatIsThePropertyOrLandAddressController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeWhatIsThePropertyOrLandAddress                  controllers.WhatIsThePropertyOrLandAddressController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeWhatIsThePropertyOrLandAddress                  controllers.WhatIsThePropertyOrLandAddressController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whatIsThePropertyOrLandAddress.title = whatIsThePropertyOrLandAddress" >> ../conf/messages.en
echo "whatIsThePropertyOrLandAddress.heading = whatIsThePropertyOrLandAddress" >> ../conf/messages.en
echo "whatIsThePropertyOrLandAddress.checkYourAnswersLabel = whatIsThePropertyOrLandAddress" >> ../conf/messages.en
echo "whatIsThePropertyOrLandAddress.error.required = Enter whatIsThePropertyOrLandAddress" >> ../conf/messages.en
echo "whatIsThePropertyOrLandAddress.error.length = WhatIsThePropertyOrLandAddress must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatIsThePropertyOrLandAddressUserAnswersEntry: Arbitrary[(WhatIsThePropertyOrLandAddressPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhatIsThePropertyOrLandAddressPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatIsThePropertyOrLandAddressPage: Arbitrary[WhatIsThePropertyOrLandAddressPage.type] =";\
    print "    Arbitrary(WhatIsThePropertyOrLandAddressPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhatIsThePropertyOrLandAddressPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def whatIsThePropertyOrLandAddress: Option[AnswerRow] = userAnswers.get(WhatIsThePropertyOrLandAddressPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"whatIsThePropertyOrLandAddress.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.WhatIsThePropertyOrLandAddressController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhatIsThePropertyOrLandAddress completed"
