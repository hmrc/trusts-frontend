#!/bin/bash

echo ""
echo "Applying migration WhatIsTheUTR"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /WhatIsTheUTR                  controllers.register.WhatIsTheUTRController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /WhatIsTheUTR                  controllers.register.WhatIsTheUTRController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhatIsTheUTR                        controllers.register.WhatIsTheUTRController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhatIsTheUTR                        controllers.register.WhatIsTheUTRController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "WhatIsTheUTR.title = WhatIsTheUTR" >> ../conf/messages.en
echo "WhatIsTheUTR.heading = WhatIsTheUTR" >> ../conf/messages.en
echo "WhatIsTheUTR.checkYourAnswersLabel = WhatIsTheUTR" >> ../conf/messages.en
echo "WhatIsTheUTR.error.nonNumeric = Enter your WhatIsTheUTR using numbers" >> ../conf/messages.en
echo "WhatIsTheUTR.error.required = Enter your WhatIsTheUTR" >> ../conf/messages.en
echo "WhatIsTheUTR.error.wholeNumber = Enter your WhatIsTheUTR using whole numbers" >> ../conf/messages.en
echo "WhatIsTheUTR.error.outOfRange = WhatIsTheUTR must be between {0} and {1}" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatIsTheUTRUserAnswersEntry: Arbitrary[(WhatIsTheUTRPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhatIsTheUTRPage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatIsTheUTRPage: Arbitrary[WhatIsTheUTRPage.type] =";\
    print "    Arbitrary(WhatIsTheUTRPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhatIsTheUTRPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def WhatIsTheUTR: Option[AnswerRow] = userAnswers.get(WhatIsTheUTRPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"WhatIsTheUTR.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x.toString),";\
     print "        routes.WhatIsTheUTRController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\	     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhatIsTheUTR completed"
