#!/bin/bash

echo ""
echo "Applying migration AgentOtherThanBarrister"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /agentOtherThanBarrister                        controllers.AgentOtherThanBarristerController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /agentOtherThanBarrister                        controllers.AgentOtherThanBarristerController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAgentOtherThanBarrister                  controllers.AgentOtherThanBarristerController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAgentOtherThanBarrister                  controllers.AgentOtherThanBarristerController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "agentOtherThanBarrister.title = agentOtherThanBarrister" >> ../conf/messages.en
echo "agentOtherThanBarrister.heading = agentOtherThanBarrister" >> ../conf/messages.en
echo "agentOtherThanBarrister.checkYourAnswersLabel = agentOtherThanBarrister" >> ../conf/messages.en
echo "agentOtherThanBarrister.error.required = Select yes if agentOtherThanBarrister" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgentOtherThanBarristerUserAnswersEntry: Arbitrary[(AgentOtherThanBarristerPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AgentOtherThanBarristerPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgentOtherThanBarristerPage: Arbitrary[AgentOtherThanBarristerPage.type] =";\
    print "    Arbitrary(AgentOtherThanBarristerPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AgentOtherThanBarristerPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def agentOtherThanBarrister: Option[AnswerRow] = userAnswers.get(AgentOtherThanBarristerPage) map {";\
     print "    x => AnswerRow(\"agentOtherThanBarrister.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.AgentOtherThanBarristerController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AgentOtherThanBarrister completed"
