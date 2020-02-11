#!/bin/bash

echo ""
echo "Applying migration EstablishedUnderScotsLaw"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /establishedUnderScotsLaw                        controllers.register.trust_details.EstablishedUnderScotsLawController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /establishedUnderScotsLaw                        controllers.register.trust_details.EstablishedUnderScotsLawController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeEstablishedUnderScotsLaw                  controllers.register.trust_details.EstablishedUnderScotsLawController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeEstablishedUnderScotsLaw                  controllers.register.trust_details.EstablishedUnderScotsLawController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "establishedUnderScotsLaw.title = establishedUnderScotsLaw" >> ../conf/messages.en
echo "establishedUnderScotsLaw.heading = establishedUnderScotsLaw" >> ../conf/messages.en
echo "establishedUnderScotsLaw.checkYourAnswersLabel = establishedUnderScotsLaw" >> ../conf/messages.en
echo "establishedUnderScotsLaw.error.required = Select yes if establishedUnderScotsLaw" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryEstablishedUnderScotsLawUserAnswersEntry: Arbitrary[(EstablishedUnderScotsLawPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[EstablishedUnderScotsLawPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryEstablishedUnderScotsLawPage: Arbitrary[EstablishedUnderScotsLawPage.type] =";\
    print "    Arbitrary(EstablishedUnderScotsLawPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(EstablishedUnderScotsLawPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def establishedUnderScotsLaw: Option[AnswerRow] = userAnswers.get(EstablishedUnderScotsLawPage) map {";\
     print "    x => AnswerRow(\"establishedUnderScotsLaw.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.EstablishedUnderScotsLawController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration EstablishedUnderScotsLaw completed"
