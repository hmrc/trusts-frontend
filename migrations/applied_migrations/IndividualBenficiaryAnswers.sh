#!/bin/bash

echo ""
echo "Applying migration IndividualBenficiaryAnswers"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /individualBenficiaryAnswers                       controllers.IndividualBenficiaryAnswersController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualBenficiaryAnswers.title = individualBenficiaryAnswers" >> ../conf/messages.en
echo "individualBenficiaryAnswers.heading = individualBenficiaryAnswers" >> ../conf/messages.en

echo "Migration IndividualBenficiaryAnswers completed"
