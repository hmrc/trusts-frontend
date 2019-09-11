#!/bin/bash

echo ""
echo "Applying migration DeceasedSettlorAnswer"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /deceasedSettlorAnswer                       controllers.deceased_settlor.DeceasedSettlorAnswerController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "deceasedSettlorAnswer.title = deceasedSettlorAnswer" >> ../conf/messages.en
echo "deceasedSettlorAnswer.heading = deceasedSettlorAnswer" >> ../conf/messages.en

echo "Migration DeceasedSettlorAnswer completed"
