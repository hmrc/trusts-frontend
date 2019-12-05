#!/bin/bash

echo ""
echo "Applying migration ShareCompanyName"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /shareCompanyName                        controllers.register.asset.shares.ShareCompanyNameController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /shareCompanyName                        controllers.register.asset.shares.ShareCompanyNameController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeShareCompanyName                  controllers.register.asset.shares.ShareCompanyNameController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeShareCompanyName                  controllers.register.asset.shares.ShareCompanyNameController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "shareCompanyName.title = shareCompanyName" >> ../conf/messages.en
echo "shareCompanyName.heading = shareCompanyName" >> ../conf/messages.en
echo "shareCompanyName.checkYourAnswersLabel = shareCompanyName" >> ../conf/messages.en
echo "shareCompanyName.error.required = Enter shareCompanyName" >> ../conf/messages.en
echo "shareCompanyName.error.length = ShareCompanyName must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryShareCompanyNameUserAnswersEntry: Arbitrary[(ShareCompanyNamePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ShareCompanyNamePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryShareCompanyNamePage: Arbitrary[ShareCompanyNamePage.type] =";\
    print "    Arbitrary(ShareCompanyNamePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ShareCompanyNamePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def shareCompanyName: Option[AnswerRow] = userAnswers.get(ShareCompanyNamePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"shareCompanyName.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.ShareCompanyNameController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ShareCompanyName completed"
