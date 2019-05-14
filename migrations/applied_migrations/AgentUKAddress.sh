#!/bin/bash

echo ""
echo "Applying migration AgentUKAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /agentUKAddress                        controllers.AgentUKAddressController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /agentUKAddress                        controllers.AgentUKAddressController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAgentUKAddress                  controllers.AgentUKAddressController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAgentUKAddress                  controllers.AgentUKAddressController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "agentUKAddress.title = agentUKAddress" >> ../conf/messages.en
echo "agentUKAddress.heading = agentUKAddress" >> ../conf/messages.en
echo "agentUKAddress.field1 = Field 1" >> ../conf/messages.en
echo "agentUKAddress.field2 = Field 2" >> ../conf/messages.en
echo "agentUKAddress.checkYourAnswersLabel = agentUKAddress" >> ../conf/messages.en
echo "agentUKAddress.error.field1.required = Enter field1" >> ../conf/messages.en
echo "agentUKAddress.error.field2.required = Enter field2" >> ../conf/messages.en
echo "agentUKAddress.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "agentUKAddress.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgentUKAddressUserAnswersEntry: Arbitrary[(AgentUKAddressPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AgentUKAddressPage.type]";\
    print "        value <- arbitrary[AgentUKAddress].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgentUKAddressPage: Arbitrary[AgentUKAddressPage.type] =";\
    print "    Arbitrary(AgentUKAddressPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgentUKAddress: Arbitrary[AgentUKAddress] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield AgentUKAddress(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AgentUKAddressPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def agentUKAddress: Option[AnswerRow] = userAnswers.get(AgentUKAddressPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"agentUKAddress.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(s\"${x.field1} ${x.field2}\"),";\
     print "        routes.AgentUKAddressController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AgentUKAddress completed"
