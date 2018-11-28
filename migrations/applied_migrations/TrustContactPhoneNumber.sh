#!/bin/bash

echo "Applying migration TrustContactPhoneNumber"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trustContactPhoneNumber                        controllers.TrustContactPhoneNumberController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trustContactPhoneNumber                        controllers.TrustContactPhoneNumberController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTrustContactPhoneNumber                  controllers.TrustContactPhoneNumberController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTrustContactPhoneNumber                  controllers.TrustContactPhoneNumberController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trustContactPhoneNumber.title = trustContactPhoneNumber" >> ../conf/messages.en
echo "trustContactPhoneNumber.heading = trustContactPhoneNumber" >> ../conf/messages.en
echo "trustContactPhoneNumber.checkYourAnswersLabel = trustContactPhoneNumber" >> ../conf/messages.en
echo "trustContactPhoneNumber.error.required = Enter trustContactPhoneNumber" >> ../conf/messages.en
echo "trustContactPhoneNumber.error.length = TrustContactPhoneNumber must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustContactPhoneNumberUserAnswersEntry: Arbitrary[(TrustContactPhoneNumberPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TrustContactPhoneNumberPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustContactPhoneNumberPage: Arbitrary[TrustContactPhoneNumberPage.type] =";\
    print "    Arbitrary(TrustContactPhoneNumberPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TrustContactPhoneNumberPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def trustContactPhoneNumber: Option[AnswerRow] = userAnswers.get(TrustContactPhoneNumberPage) map {";\
     print "    x => AnswerRow(\"trustContactPhoneNumber.checkYourAnswersLabel\", s\"$x\", false, routes.TrustContactPhoneNumberController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TrustContactPhoneNumber completed"
