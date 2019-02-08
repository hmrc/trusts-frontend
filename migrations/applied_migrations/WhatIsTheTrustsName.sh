#!/bin/bash

echo ""
echo "Applying migration WhatIsTheTrustsName"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whatIsTheTrustsName                        controllers.WhatIsTheTrustsNameController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whatIsTheTrustsName                        controllers.WhatIsTheTrustsNameController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhatIsTheTrustsName                  controllers.WhatIsTheTrustsNameController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhatIsTheTrustsName                  controllers.WhatIsTheTrustsNameController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whatIsTheTrustsName.title = whatIsTheTrustsName" >> ../conf/messages.en
echo "whatIsTheTrustsName.heading = whatIsTheTrustsName" >> ../conf/messages.en
echo "whatIsTheTrustsName.checkYourAnswersLabel = whatIsTheTrustsName" >> ../conf/messages.en
echo "whatIsTheTrustsName.error.required = Enter whatIsTheTrustsName" >> ../conf/messages.en
echo "whatIsTheTrustsName.error.length = WhatIsTheTrustsName must be 53 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatIsTheTrustsNameUserAnswersEntry: Arbitrary[(WhatIsTheTrustsNamePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhatIsTheTrustsNamePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatIsTheTrustsNamePage: Arbitrary[WhatIsTheTrustsNamePage.type] =";\
    print "    Arbitrary(WhatIsTheTrustsNamePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhatIsTheTrustsNamePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whatIsTheTrustsName: Option[AnswerRow] = userAnswers.get(WhatIsTheTrustsNamePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"whatIsTheTrustsName.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.WhatIsTheTrustsNameController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhatIsTheTrustsName completed"
