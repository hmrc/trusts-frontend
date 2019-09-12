#!/bin/bash

echo ""
echo "Applying migration TrusteesAnswerPage"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /trusteesAnswerPage                       controllers.trustees.TrusteesAnswerPageController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trusteesAnswerPage.title = trusteesAnswerPage" >> ../conf/messages.en
echo "trusteesAnswerPage.heading = trusteesAnswerPage" >> ../conf/messages.en

echo "Migration TrusteesAnswerPage completed"
