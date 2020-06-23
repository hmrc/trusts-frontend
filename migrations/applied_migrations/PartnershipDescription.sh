#!/bin/bash

echo ""
echo "Applying migration PartnershipDescription"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/partnershipDescription                        controllers.PartnershipDescriptionController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/partnershipDescription                        controllers.PartnershipDescriptionController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changePartnershipDescription                  controllers.PartnershipDescriptionController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changePartnershipDescription                  controllers.PartnershipDescriptionController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "partnershipDescription.title = partnershipDescription" >> ../conf/messages.en
echo "partnershipDescription.heading = partnershipDescription" >> ../conf/messages.en
echo "partnershipDescription.checkYourAnswersLabel = partnershipDescription" >> ../conf/messages.en
echo "partnershipDescription.error.required = Enter partnershipDescription" >> ../conf/messages.en
echo "partnershipDescription.error.length = PartnershipDescription must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPartnershipDescriptionUserAnswersEntry: Arbitrary[(PartnershipDescriptionPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[PartnershipDescriptionPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPartnershipDescriptionPage: Arbitrary[PartnershipDescriptionPage.type] =";\
    print "    Arbitrary(PartnershipDescriptionPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(PartnershipDescriptionPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def partnershipDescription: Option[AnswerRow] = userAnswers.get(PartnershipDescriptionPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"partnershipDescription.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.PartnershipDescriptionController.onPageLoad(NormalMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration PartnershipDescription completed"
