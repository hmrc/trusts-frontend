#!/bin/bash

echo "Applying migration TrustAddressUKYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trustAddressUKYesNo                        controllers.TrustAddressUKYesNoController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trustAddressUKYesNo                        controllers.TrustAddressUKYesNoController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTrustAddressUKYesNo                  controllers.TrustAddressUKYesNoController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTrustAddressUKYesNo                  controllers.TrustAddressUKYesNoController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trustAddressUKYesNo.title = trustAddressUKYesNo" >> ../conf/messages.en
echo "trustAddressUKYesNo.heading = trustAddressUKYesNo" >> ../conf/messages.en
echo "trustAddressUKYesNo.checkYourAnswersLabel = trustAddressUKYesNo" >> ../conf/messages.en
echo "trustAddressUKYesNo.error.required = Select yes if trustAddressUKYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustAddressUKYesNoUserAnswersEntry: Arbitrary[(TrustAddressUKYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TrustAddressUKYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustAddressUKYesNoPage: Arbitrary[TrustAddressUKYesNoPage.type] =";\
    print "    Arbitrary(TrustAddressUKYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TrustAddressUKYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def trustAddressUKYesNo: Option[AnswerRow] = userAnswers.get(TrustAddressUKYesNoPage) map {";\
     print "    x => AnswerRow(\"trustAddressUKYesNo.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.TrustAddressUKYesNoController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TrustAddressUKYesNo completed"
