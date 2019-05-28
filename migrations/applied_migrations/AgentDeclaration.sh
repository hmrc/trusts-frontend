#!/bin/bash

echo ""
echo "Applying migration AgentDeclaration"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /agentDeclaration                        controllers.AgentDeclarationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /agentDeclaration                        controllers.AgentDeclarationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAgentDeclaration                  controllers.AgentDeclarationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAgentDeclaration                  controllers.AgentDeclarationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "agentDeclaration.title = agentDeclaration" >> ../conf/messages.en
echo "agentDeclaration.heading = agentDeclaration" >> ../conf/messages.en
echo "agentDeclaration.checkYourAnswersLabel = agentDeclaration" >> ../conf/messages.en
echo "agentDeclaration.error.required = Enter agentDeclaration" >> ../conf/messages.en
echo "agentDeclaration.error.length = AgentDeclaration must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgentDeclarationUserAnswersEntry: Arbitrary[(AgentDeclarationPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AgentDeclarationPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgentDeclarationPage: Arbitrary[AgentDeclarationPage.type] =";\
    print "    Arbitrary(AgentDeclarationPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AgentDeclarationPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def agentDeclaration: Option[AnswerRow] = userAnswers.get(AgentDeclarationPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"agentDeclaration.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.AgentDeclarationController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AgentDeclaration completed"
