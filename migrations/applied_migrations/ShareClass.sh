#!/bin/bash

echo ""
echo "Applying migration ShareClass"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /shareClass                        controllers.shares.ShareClassController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /shareClass                        controllers.shares.ShareClassController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeShareClass                  controllers.shares.ShareClassController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeShareClass                  controllers.shares.ShareClassController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "shareClass.title = What class are the shares" >> ../conf/messages.en
echo "shareClass.heading = What class are the shares" >> ../conf/messages.en
echo "shareClass.class = Ordinary" >> ../conf/messages.en
echo "shareClass.class = Preference" >> ../conf/messages.en
echo "shareClass.checkYourAnswersLabel = What class are the shares" >> ../conf/messages.en
echo "shareClass.error.required = Select shareClass" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryShareClassUserAnswersEntry: Arbitrary[(ShareClassPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ShareClassPage.type]";\
    print "        value <- arbitrary[ShareClass].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryShareClassPage: Arbitrary[ShareClassPage.type] =";\
    print "    Arbitrary(ShareClassPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryShareClass: Arbitrary[ShareClass] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(ShareClass.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ShareClassPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def shareClass: Option[AnswerRow] = userAnswers.get(ShareClassPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"shareClass.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(messages(s\"shareClass.$x\")),";\
     print "        routes.ShareClassController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ShareClass completed"
