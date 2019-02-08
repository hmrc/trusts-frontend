#!/bin/bash

echo ""
echo "Applying migration UTRSentbyPostController"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /UTRSentByPostController                       controllers.UTRSentbyPostControllerController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "UTRSentByPostController.title = UTRSentByPostController" >> ../conf/messages.en
echo "UTRSentByPostController.heading = UTRSentByPostController" >> ../conf/messages.en

echo "Migration UTRSentbyPostController completed"
