#!/bin/bash

echo ""
echo "Applying migration AgentInternalReference"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /agentInternalReference                        controllers.AgentInternalReferenceController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /agentInternalReference                        controllers.AgentInternalReferenceController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAgentInternalReference                  controllers.AgentInternalReferenceController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAgentInternalReference                  controllers.AgentInternalReferenceController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "agentInternalReference.title = agentInternalReference" >> ../conf/messages.en
echo "agentInternalReference.heading = agentInternalReference" >> ../conf/messages.en
echo "agentInternalReference.checkYourAnswersLabel = agentInternalReference" >> ../conf/messages.en
echo "agentInternalReference.error.required = Enter agentInternalReference" >> ../conf/messages.en
echo "agentInternalReference.error.length = AgentInternalReference must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgentInternalReferenceUserAnswersEntry: Arbitrary[(AgentInternalReferencePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AgentInternalReferencePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgentInternalReferencePage: Arbitrary[AgentInternalReferencePage.type] =";\
    print "    Arbitrary(AgentInternalReferencePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AgentInternalReferencePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def agentInternalReference: Option[AnswerRow] = userAnswers.get(AgentInternalReferencePage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"agentInternalReference.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.AgentInternalReferenceController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AgentInternalReference completed"
