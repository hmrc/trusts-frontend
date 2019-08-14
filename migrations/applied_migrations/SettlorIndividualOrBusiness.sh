#!/bin/bash

echo ""
echo "Applying migration SettlorIndividualOrBusiness"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/settlorIndividualOrBusiness                        controllers.SettlorIndividualOrBusinessController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/settlorIndividualOrBusiness                        controllers.SettlorIndividualOrBusinessController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeSettlorIndividualOrBusiness                  controllers.SettlorIndividualOrBusinessController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeSettlorIndividualOrBusiness                  controllers.SettlorIndividualOrBusinessController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "settlorIndividualOrBusiness.title = Is the settlor an individual or a business?" >> ../conf/messages.en
echo "settlorIndividualOrBusiness.heading = Is the settlor an individual or a business?" >> ../conf/messages.en
echo "settlorIndividualOrBusiness.individual = Individual" >> ../conf/messages.en
echo "settlorIndividualOrBusiness.business = Business" >> ../conf/messages.en
echo "settlorIndividualOrBusiness.checkYourAnswersLabel = Is the settlor an individual or a business?" >> ../conf/messages.en
echo "settlorIndividualOrBusiness.error.required = Select settlorIndividualOrBusiness" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualOrBusinessUserAnswersEntry: Arbitrary[(SettlorIndividualOrBusinessPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SettlorIndividualOrBusinessPage.type]";\
    print "        value <- arbitrary[SettlorIndividualOrBusiness].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualOrBusinessPage: Arbitrary[SettlorIndividualOrBusinessPage.type] =";\
    print "    Arbitrary(SettlorIndividualOrBusinessPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySettlorIndividualOrBusiness: Arbitrary[SettlorIndividualOrBusiness] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(SettlorIndividualOrBusiness.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SettlorIndividualOrBusinessPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def settlorIndividualOrBusiness: Option[AnswerRow] = userAnswers.get(SettlorIndividualOrBusinessPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"settlorIndividualOrBusiness.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(messages(s\"settlorIndividualOrBusiness.$x\")),";\
     print "        routes.SettlorIndividualOrBusinessController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SettlorIndividualOrBusiness completed"
