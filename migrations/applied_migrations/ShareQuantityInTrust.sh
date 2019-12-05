#!/bin/bash

echo ""
echo "Applying migration ShareQuantityInTrust"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /shareQuantityInTrust                        controllers.register.asset.shares.ShareQuantityInTrustController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /shareQuantityInTrust                        controllers.register.asset.shares.ShareQuantityInTrustController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeShareQuantityInTrust                  controllers.register.asset.shares.ShareQuantityInTrustController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeShareQuantityInTrust                  controllers.register.asset.shares.ShareQuantityInTrustController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "shareQuantityInTrust.title = shareQuantityInTrust" >> ../conf/messages.en
echo "shareQuantityInTrust.heading = shareQuantityInTrust" >> ../conf/messages.en
echo "shareQuantityInTrust.checkYourAnswersLabel = shareQuantityInTrust" >> ../conf/messages.en
echo "shareQuantityInTrust.error.required = Enter shareQuantityInTrust" >> ../conf/messages.en
echo "shareQuantityInTrust.error.length = ShareQuantityInTrust must be 12 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryShareQuantityInTrustUserAnswersEntry: Arbitrary[(ShareQuantityInTrustPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ShareQuantityInTrustPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryShareQuantityInTrustPage: Arbitrary[ShareQuantityInTrustPage.type] =";\
    print "    Arbitrary(ShareQuantityInTrustPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ShareQuantityInTrustPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def shareQuantityInTrust: Option[AnswerRow] = userAnswers.get(ShareQuantityInTrustPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"shareQuantityInTrust.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.ShareQuantityInTrustController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ShareQuantityInTrust completed"
