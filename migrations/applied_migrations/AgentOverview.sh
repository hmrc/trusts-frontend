#!/bin/bash

echo ""
echo "Applying migration AgentOverview"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /agentOverview                       controllers.AgentOverviewController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "agentOverview.title = agentOverview" >> ../conf/messages.en
echo "agentOverview.heading = agentOverview" >> ../conf/messages.en

echo "Migration AgentOverview completed"
