#!/bin/bash

echo ""
echo "Applying migration AgentAddressYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /agentAddressYesNo                        controllers.register.agents.AgentAddressYesNoController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /agentAddressYesNo                        controllers.register.agents.AgentAddressYesNoController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAgentAddressYesNo                  controllers.register.agents.AgentAddressYesNoController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAgentAddressYesNo                  controllers.register.agents.AgentAddressYesNoController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "agentAddressYesNo.title = agentAddressYesNo" >> ../conf/messages.en
echo "agentAddressYesNo.heading = agentAddressYesNo" >> ../conf/messages.en
echo "agentAddressYesNo.checkYourAnswersLabel = agentAddressYesNo" >> ../conf/messages.en
echo "agentAddressYesNo.error.required = Select yes if agentAddressYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgentAddressYesNoUserAnswersEntry: Arbitrary[(AgentAddressYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AgentAddressYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgentAddressYesNoPage: Arbitrary[AgentAddressYesNoPage.type] =";\
    print "    Arbitrary(AgentAddressYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AgentAddressYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def agentAddressYesNo: Option[AnswerRow] = userAnswers.get(AgentAddressYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"agentAddressYesNo.checkYourAnswersLabel\",";\
     print "        yesOrNo(x),";\
     print "        routes.AgentAddressYesNoController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AgentAddressYesNo completed"
