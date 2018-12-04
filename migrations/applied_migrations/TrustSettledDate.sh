#!/bin/bash

echo "Applying migration TrustSettledDate"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trustSettledDate                        controllers.TrustSettledDateController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trustSettledDate                        controllers.TrustSettledDateController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTrustSettledDate                  controllers.TrustSettledDateController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTrustSettledDate                  controllers.TrustSettledDateController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trustSettledDate.title = trustSettledDate" >> ../conf/messages.en
echo "trustSettledDate.heading = trustSettledDate" >> ../conf/messages.en
echo "trustSettledDate.checkYourAnswersLabel = trustSettledDate" >> ../conf/messages.en
echo "trustSettledDate.error.required = Enter trustSettledDate" >> ../conf/messages.en
echo "trustSettledDate.error.length = TrustSettledDate must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustSettledDateUserAnswersEntry: Arbitrary[(TrustSettledDatePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TrustSettledDatePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustSettledDatePage: Arbitrary[TrustSettledDatePage.type] =";\
    print "    Arbitrary(TrustSettledDatePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TrustSettledDatePage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def trustSettledDate: Option[AnswerRow] = userAnswers.get(TrustSettledDatePage) map {";\
     print "    x => AnswerRow(\"trustSettledDate.checkYourAnswersLabel\", s\"$x\", false, routes.TrustSettledDateController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TrustSettledDate completed"
