#!/bin/bash

echo ""
echo "Applying migration AgentName"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /agentName                        controllers.register.agents.AgentNameController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /agentName                        controllers.register.agents.AgentNameController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAgentName                  controllers.register.agents.AgentNameController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAgentName                  controllers.register.agents.AgentNameController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "agentName.title = agentName" >> ../conf/messages.en
echo "agentName.heading = agentName" >> ../conf/messages.en
echo "agentName.checkYourAnswersLabel = agentName" >> ../conf/messages.en
echo "agentName.error.required = Enter agentName" >> ../conf/messages.en
echo "agentName.error.length = AgentName must be 56 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgentNameUserAnswersEntry: Arbitrary[(AgentNamePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AgentNamePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgentNamePage: Arbitrary[AgentNamePage.type] =";\
    print "    Arbitrary(AgentNamePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AgentNamePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def agentName: Option[AnswerRow] = userAnswers.get(AgentNamePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"agentName.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.AgentNameController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AgentName completed"
