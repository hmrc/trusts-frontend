#!/bin/bash

echo ""
echo "Applying migration TrustName"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trustName                        controllers.register.TrustNameController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trustName                        controllers.register.TrustNameController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTrustName                  controllers.register.TrustNameController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTrustName                  controllers.register.TrustNameController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trustName.title = trustName" >> ../conf/messages.en
echo "trustName.heading = trustName" >> ../conf/messages.en
echo "trustName.checkYourAnswersLabel = trustName" >> ../conf/messages.en
echo "trustName.error.required = Enter trustName" >> ../conf/messages.en
echo "trustName.error.length = TrustName must be 53 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustNameUserAnswersEntry: Arbitrary[(TrustNamePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TrustNamePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustNamePage: Arbitrary[TrustNamePage.type] =";\
    print "    Arbitrary(TrustNamePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TrustNamePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def trustName: Option[AnswerRow] = userAnswers.get(TrustNamePage) map {";\
     print "    x => AnswerRow(\"trustName.checkYourAnswersLabel\", s\"$x\", false, routes.TrustNameController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TrustName completed"
