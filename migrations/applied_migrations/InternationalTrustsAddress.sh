#!/bin/bash

echo "Applying migration InternationalTrustsAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /internationalTrustsAddress                        controllers.InternationalTrustsAddressController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /internationalTrustsAddress                        controllers.InternationalTrustsAddressController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeInternationalTrustsAddress                  controllers.InternationalTrustsAddressController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeInternationalTrustsAddress                  controllers.InternationalTrustsAddressController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "internationalTrustsAddress.title = internationalTrustsAddress" >> ../conf/messages.en
echo "internationalTrustsAddress.heading = internationalTrustsAddress" >> ../conf/messages.en
echo "internationalTrustsAddress.field1 = Field 1" >> ../conf/messages.en
echo "internationalTrustsAddress.field2 = Field 2" >> ../conf/messages.en
echo "internationalTrustsAddress.checkYourAnswersLabel = internationalTrustsAddress" >> ../conf/messages.en
echo "internationalTrustsAddress.error.field1.required = Enter field1" >> ../conf/messages.en
echo "internationalTrustsAddress.error.field2.required = Enter field2" >> ../conf/messages.en
echo "internationalTrustsAddress.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "internationalTrustsAddress.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryInternationalTrustsAddressUserAnswersEntry: Arbitrary[(InternationalTrustsAddressPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[InternationalTrustsAddressPage.type]";\
    print "        value <- arbitrary[InternationalTrustsAddress].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryInternationalTrustsAddressPage: Arbitrary[InternationalTrustsAddressPage.type] =";\
    print "    Arbitrary(InternationalTrustsAddressPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryInternationalTrustsAddress: Arbitrary[InternationalTrustsAddress] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield InternationalTrustsAddress(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to CacheMapGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(InternationalTrustsAddressPage.type, JsValue)] ::";\
    next }1' ../test/generators/CacheMapGenerator.scala > tmp && mv tmp ../test/generators/CacheMapGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def internationalTrustsAddress: Option[AnswerRow] = userAnswers.get(InternationalTrustsAddressPage) map {";\
     print "    x => AnswerRow(\"internationalTrustsAddress.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.InternationalTrustsAddressController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration InternationalTrustsAddress completed"