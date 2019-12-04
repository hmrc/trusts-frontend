#!/bin/bash

echo ""
echo "Applying migration TrustRegisteredOnline"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /trustRegisteredOnline                        controllers.register.TrustRegisteredOnlineController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /trustRegisteredOnline                        controllers.register.TrustRegisteredOnlineController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTrustRegisteredOnline                  controllers.register.TrustRegisteredOnlineController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTrustRegisteredOnline                  controllers.register.TrustRegisteredOnlineController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trustRegisteredOnline.title = trustRegisteredOnline" >> ../conf/messages.en
echo "trustRegisteredOnline.heading = trustRegisteredOnline" >> ../conf/messages.en
echo "trustRegisteredOnline.checkYourAnswersLabel = trustRegisteredOnline" >> ../conf/messages.en
echo "trustRegisteredOnline.error.required = Select yes if trustRegisteredOnline" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustRegisteredOnlineUserAnswersEntry: Arbitrary[(TrustRegisteredOnlinePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TrustRegisteredOnlinePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTrustRegisteredOnlinePage: Arbitrary[TrustRegisteredOnlinePage.type] =";\
    print "    Arbitrary(TrustRegisteredOnlinePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TrustRegisteredOnlinePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def trustRegisteredOnline: Option[AnswerRow] = userAnswers.get(TrustRegisteredOnlinePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"trustRegisteredOnline.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.TrustRegisteredOnlineController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TrustRegisteredOnline completed"
