#!/bin/bash

echo ""
echo "Applying migration PartnershipStartDate"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/partnershipStartDate                  controllers.register.asset.partnership.PartnershipStartDateController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/partnershipStartDate                  controllers.register.asset.partnership.PartnershipStartDateController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changePartnershipStartDate                        controllers.register.asset.partnership.PartnershipStartDateController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changePartnershipStartDate                        controllers.register.asset.partnership.PartnershipStartDateController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnershipStartDate.title = PartnershipStartDate" >> ../conf/messages.en
echo "partnershipStartDate.heading = PartnershipStartDate" >> ../conf/messages.en
echo "partnershipStartDate.checkYourAnswersLabel = PartnershipStartDate" >> ../conf/messages.en
echo "partnershipStartDate.error.required.all = Enter the partnershipStartDate" >> ../conf/messages.en
echo "partnershipStartDate.error.required.two = The partnershipStartDate" must include {0} and {1} >> ../conf/messages.en
echo "partnershipStartDate.error.required = The partnershipStartDate must include {0}" >> ../conf/messages.en
echo "partnershipStartDate.error.invalid = Enter a real PartnershipStartDate" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPartnershipStartDateUserAnswersEntry: Arbitrary[(PartnershipStartDatePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[PartnershipStartDatePage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPartnershipStartDatePage: Arbitrary[PartnershipStartDatePage.type] =";\
    print "    Arbitrary(PartnershipStartDatePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(PartnershipStartDatePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def partnershipStartDate: Option[AnswerRow] = userAnswers.get(PartnershipStartDatePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"partnershipStartDate.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x.format(dateFormatter)),";\
     print "        routes.PartnershipStartDateController.onPageLoad(NormalMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration PartnershipStartDate completed"
