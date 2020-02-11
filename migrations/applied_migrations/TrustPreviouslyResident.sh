#!/bin/bash

echo ""
echo "Applying migration TrustPreviouslyResident"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trustPreviouslyResident                        controllers.register.trust_details.TrustPreviouslyResidentController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trustPreviouslyResident                        controllers.register.trust_details.TrustPreviouslyResidentController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTrustPreviouslyResident                  controllers.register.trust_details.TrustPreviouslyResidentController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTrustPreviouslyResident                  controllers.register.trust_details.TrustPreviouslyResidentController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trustPreviouslyResident.title = trustPreviouslyResident" >> ../conf/messages.en
echo "trustPreviouslyResident.heading = trustPreviouslyResident" >> ../conf/messages.en
echo "trustPreviouslyResident.checkYourAnswersLabel = trustPreviouslyResident" >> ../conf/messages.en
echo "trustPreviouslyResident.error.required = Enter trustPreviouslyResident" >> ../conf/messages.en
echo "trustPreviouslyResident.error.length = TrustPreviouslyResident must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustPreviouslyResidentUserAnswersEntry: Arbitrary[(TrustPreviouslyResidentPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TrustPreviouslyResidentPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustPreviouslyResidentPage: Arbitrary[TrustPreviouslyResidentPage.type] =";\
    print "    Arbitrary(TrustPreviouslyResidentPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TrustPreviouslyResidentPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def trustPreviouslyResident: Option[AnswerRow] = userAnswers.get(TrustPreviouslyResidentPage) map {";\
     print "    x => AnswerRow(\"trustPreviouslyResident.checkYourAnswersLabel\", s\"$x\", false, routes.TrustPreviouslyResidentController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TrustPreviouslyResident completed"
