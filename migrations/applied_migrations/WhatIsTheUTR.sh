#!/bin/bash

echo ""
echo "Applying migration WhatIsTheUTR"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whatIsTheUTR                        controllers.WhatIsTheUTRController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whatIsTheUTR                        controllers.WhatIsTheUTRController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhatIsTheUTR                  controllers.WhatIsTheUTRController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhatIsTheUTR                  controllers.WhatIsTheUTRController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whatIsTheUTR.title = whatIsTheUTR" >> ../conf/messages.en
echo "whatIsTheUTR.heading = whatIsTheUTR" >> ../conf/messages.en
echo "whatIsTheUTR.checkYourAnswersLabel = whatIsTheUTR" >> ../conf/messages.en
echo "whatIsTheUTR.error.required = Enter whatIsTheUTR" >> ../conf/messages.en
echo "whatIsTheUTR.error.length = WhatIsTheUTR must be 10 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatIsTheUTRUserAnswersEntry: Arbitrary[(WhatIsTheUTRPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhatIsTheUTRPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
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
     print "  def whatIsTheUTR: Option[AnswerRow] = userAnswers.get(WhatIsTheUTRPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"whatIsTheUTR.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.WhatIsTheUTRController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhatIsTheUTR completed"
