#!/bin/bash

echo ""
echo "Applying migration SharePortfolioName"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /sharePortfolioName                        controllers.SharePortfolioNameController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /sharePortfolioName                        controllers.SharePortfolioNameController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSharePortfolioName                  controllers.SharePortfolioNameController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSharePortfolioName                  controllers.SharePortfolioNameController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "sharePortfolioName.title = sharePortfolioName" >> ../conf/messages.en
echo "sharePortfolioName.heading = sharePortfolioName" >> ../conf/messages.en
echo "sharePortfolioName.checkYourAnswersLabel = sharePortfolioName" >> ../conf/messages.en
echo "sharePortfolioName.error.required = Enter sharePortfolioName" >> ../conf/messages.en
echo "sharePortfolioName.error.length = SharePortfolioName must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySharePortfolioNameUserAnswersEntry: Arbitrary[(SharePortfolioNamePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SharePortfolioNamePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySharePortfolioNamePage: Arbitrary[SharePortfolioNamePage.type] =";\
    print "    Arbitrary(SharePortfolioNamePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SharePortfolioNamePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def sharePortfolioName: Option[AnswerRow] = userAnswers.get(SharePortfolioNamePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"sharePortfolioName.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.SharePortfolioNameController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SharePortfolioName completed"
