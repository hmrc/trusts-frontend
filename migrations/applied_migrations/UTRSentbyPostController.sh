#!/bin/bash

echo ""
echo "Applying migration UTRSentbyPostController"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /uTRSentbyPostController                       controllers.UTRSentbyPostControllerController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "uTRSentbyPostController.title = uTRSentbyPostController" >> ../conf/messages.en
echo "uTRSentbyPostController.heading = uTRSentbyPostController" >> ../conf/messages.en

echo "Migration UTRSentbyPostController completed"
