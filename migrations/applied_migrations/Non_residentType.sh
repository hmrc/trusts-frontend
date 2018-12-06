#!/bin/bash

echo ""
echo "Applying migration Non_residentType"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /non-residentType                        controllers.Non-residentTypeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /non-residentType                        controllers.Non-residentTypeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeNon-residentType                  controllers.Non-residentTypeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeNon-residentType                  controllers.Non-residentTypeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "non-residentType.title = What is the non-resident type" >> ../conf/messages.en
echo "non-residentType.heading = What is the non-resident type" >> ../conf/messages.en
echo "non-residentType.1 = Settlor domiciled trustees are non-resident" >> ../conf/messages.en
echo "non-residentType.2 = Settlor non-domiciled becomes domiciled then resident" >> ../conf/messages.en
echo "non-residentType.checkYourAnswersLabel = What is the non-resident type" >> ../conf/messages.en
echo "non-residentType.error.required = Select non-residentType" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryNon-residentTypeUserAnswersEntry: Arbitrary[(Non-residentTypePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[Non-residentTypePage.type]";\
    print "        value <- arbitrary[Non-residentType].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryNon-residentTypePage: Arbitrary[Non-residentTypePage.type] =";\
    print "    Arbitrary(Non-residentTypePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryNon-residentType: Arbitrary[Non-residentType] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(Non-residentType.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(Non-residentTypePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def non-residentType: Option[AnswerRow] = userAnswers.get(Non-residentTypePage) map {";\
     print "    x => AnswerRow(\"non-residentType.checkYourAnswersLabel\", s\"non-residentType.$x\", true, routes.Non-residentTypeController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration Non_residentType completed"
