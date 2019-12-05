#!/bin/bash

echo ""
echo "Applying migration AgentInternationalAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /agentInternationalAddress                        controllers.register.agents.AgentInternationalAddressController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /agentInternationalAddress                        controllers.register.agents.AgentInternationalAddressController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAgentInternationalAddress                  controllers.register.agents.AgentInternationalAddressController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAgentInternationalAddress                  controllers.register.agents.AgentInternationalAddressController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "agentInternationalAddress.title = agentInternationalAddress" >> ../conf/messages.en
echo "agentInternationalAddress.heading = agentInternationalAddress" >> ../conf/messages.en
echo "agentInternationalAddress.field1 = Field 1" >> ../conf/messages.en
echo "agentInternationalAddress.field2 = Field 2" >> ../conf/messages.en
echo "agentInternationalAddress.checkYourAnswersLabel = agentInternationalAddress" >> ../conf/messages.en
echo "agentInternationalAddress.error.field1.required = Enter field1" >> ../conf/messages.en
echo "agentInternationalAddress.error.field2.required = Enter field2" >> ../conf/messages.en
echo "agentInternationalAddress.error.field1.length = field1 must be 100 characters or less" >> ../conf/messages.en
echo "agentInternationalAddress.error.field2.length = field2 must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgentInternationalAddressUserAnswersEntry: Arbitrary[(AgentInternationalAddressPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AgentInternationalAddressPage.type]";\
    print "        value <- arbitrary[AgentInternationalAddress].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgentInternationalAddressPage: Arbitrary[AgentInternationalAddressPage.type] =";\
    print "    Arbitrary(AgentInternationalAddressPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgentInternationalAddress: Arbitrary[AgentInternationalAddress] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield AgentInternationalAddress(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AgentInternationalAddressPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def agentInternationalAddress: Option[AnswerRow] = userAnswers.get(AgentInternationalAddressPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"agentInternationalAddress.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(s\"${x.field1} ${x.field2}\"),";\
     print "        routes.AgentInternationalAddressController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AgentInternationalAddress completed"
