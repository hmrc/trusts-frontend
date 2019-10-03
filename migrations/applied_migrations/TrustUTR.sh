#!/bin/bash

echo ""
echo "Applying migration TrustUTR"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/trustUTR                        controllers.TrustUTRController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/trustUTR                        controllers.TrustUTRController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeTrustUTR                  controllers.TrustUTRController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeTrustUTR                  controllers.TrustUTRController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trustUTR.title = trustUTR" >> ../conf/messages.en
echo "trustUTR.heading = trustUTR" >> ../conf/messages.en
echo "trustUTR.checkYourAnswersLabel = trustUTR" >> ../conf/messages.en
echo "trustUTR.error.required = Enter trustUTR" >> ../conf/messages.en
echo "trustUTR.error.length = TrustUTR must be 10 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustUTRUserAnswersEntry: Arbitrary[(TrustUTRPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TrustUTRPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustUTRPage: Arbitrary[TrustUTRPage.type] =";\
    print "    Arbitrary(TrustUTRPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TrustUTRPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def trustUTR: Option[AnswerRow] = userAnswers.get(TrustUTRPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"trustUTR.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.TrustUTRController.onPageLoad(NormalMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TrustUTR completed"
