#!/bin/bash

echo ""
echo "Applying migration DeclarationWhatNext"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/declarationWhatNext                        controllers.DeclarationWhatNextController.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/declarationWhatNext                        controllers.DeclarationWhatNextController.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/changeDeclarationWhatNext                  controllers.DeclarationWhatNextController.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/changeDeclarationWhatNext                  controllers.DeclarationWhatNextController.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "declarationWhatNext.title = What do you want to do next?" >> ../conf/messages.en
echo "declarationWhatNext.heading = What do you want to do next?" >> ../conf/messages.en
echo "declarationWhatNext.declare the trust is up to date = Declare the trust is up to date" >> ../conf/messages.en
echo "declarationWhatNext.makeChanges = Make changes to the trust and declare" >> ../conf/messages.en
echo "declarationWhatNext.checkYourAnswersLabel = What do you want to do next?" >> ../conf/messages.en
echo "declarationWhatNext.error.required = Select declarationWhatNext" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDeclarationWhatNextUserAnswersEntry: Arbitrary[(DeclarationWhatNextPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[DeclarationWhatNextPage.type]";\
    print "        value <- arbitrary[DeclarationWhatNext].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDeclarationWhatNextPage: Arbitrary[DeclarationWhatNextPage.type] =";\
    print "    Arbitrary(DeclarationWhatNextPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDeclarationWhatNext: Arbitrary[DeclarationWhatNext] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(DeclarationWhatNext.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(DeclarationWhatNextPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def declarationWhatNext: Option[AnswerRow] = userAnswers.get(DeclarationWhatNextPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"declarationWhatNext.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(messages(s\"declarationWhatNext.$x\")),";\
     print "        routes.DeclarationWhatNextController.onPageLoad(NormalMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration DeclarationWhatNext completed"
