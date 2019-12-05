#!/bin/bash

echo ""
echo "Applying migration AgentAnswer"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /agentAnswer                       controllers.register.agents.AgentAnswerController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "agentAnswer.title = agentAnswer" >> ../conf/messages.en
echo "agentAnswer.heading = agentAnswer" >> ../conf/messages.en

echo "Migration AgentAnswer completed"
