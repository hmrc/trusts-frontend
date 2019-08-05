#!/bin/bash

echo ""
echo "Applying migration PropertyOrLandDescription"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/propertyOrLandDescription                        controllers.propertyorland.PropertyOrLandDescriptionController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/propertyOrLandDescription                        controllers.propertyorland.PropertyOrLandDescriptionController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changePropertyOrLandDescription                  controllers.propertyorland.PropertyOrLandDescriptionController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changePropertyOrLandDescription                  controllers.propertyorland.PropertyOrLandDescriptionController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "propertyOrLandDescription.title = propertyOrLandDescription" >> ../conf/messages.en
echo "propertyOrLandDescription.heading = propertyOrLandDescription" >> ../conf/messages.en
echo "propertyOrLandDescription.checkYourAnswersLabel = propertyOrLandDescription" >> ../conf/messages.en
echo "propertyOrLandDescription.error.required = Enter propertyOrLandDescription" >> ../conf/messages.en
echo "propertyOrLandDescription.error.length = PropertyOrLandDescription must be 56 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPropertyOrLandDescriptionUserAnswersEntry: Arbitrary[(PropertyOrLandDescriptionPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[PropertyOrLandDescriptionPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryPropertyOrLandDescriptionPage: Arbitrary[PropertyOrLandDescriptionPage.type] =";\
    print "    Arbitrary(PropertyOrLandDescriptionPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(PropertyOrLandDescriptionPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def propertyOrLandDescription: Option[AnswerRow] = userAnswers.get(PropertyOrLandDescriptionPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"propertyOrLandDescription.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.PropertyOrLandDescriptionController.onPageLoad(CheckMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration PropertyOrLandDescription completed"
