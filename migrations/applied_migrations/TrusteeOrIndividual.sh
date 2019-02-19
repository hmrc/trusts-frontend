#!/bin/bash

echo ""
echo "Applying migration TrusteeOrIndividual"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trusteeOrIndividual                        controllers.TrusteeOrIndividualController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trusteeOrIndividual                        controllers.TrusteeOrIndividualController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTrusteeOrIndividual                  controllers.TrusteeOrIndividualController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTrusteeOrIndividual                  controllers.TrusteeOrIndividualController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trusteeOrIndividual.title = trusteeOrIndividual" >> ../conf/messages.en
echo "trusteeOrIndividual.heading = trusteeOrIndividual" >> ../conf/messages.en
echo "trusteeOrIndividual.individual = individual" >> ../conf/messages.en
echo "trusteeOrIndividual.business = business" >> ../conf/messages.en
echo "trusteeOrIndividual.checkYourAnswersLabel = trusteeOrIndividual" >> ../conf/messages.en
echo "trusteeOrIndividual.error.required = Select trusteeOrIndividual" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrusteeOrIndividualUserAnswersEntry: Arbitrary[(TrusteeOrIndividualPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TrusteeOrIndividualPage.type]";\
    print "        value <- arbitrary[TrusteeOrIndividual].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrusteeOrIndividualPage: Arbitrary[TrusteeOrIndividualPage.type] =";\
    print "    Arbitrary(TrusteeOrIndividualPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrusteeOrIndividual: Arbitrary[TrusteeOrIndividual] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(TrusteeOrIndividual.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TrusteeOrIndividualPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def trusteeOrIndividual: Option[AnswerRow] = userAnswers.get(TrusteeOrIndividualPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"trusteeOrIndividual.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(messages(s\"trusteeOrIndividual.$x\")),";\
     print "        routes.TrusteeOrIndividualController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TrusteeOrIndividual completed"
