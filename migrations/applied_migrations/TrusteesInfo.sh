#!/bin/bash

echo ""
echo "Applying migration TrusteesInfo"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /trusteesInfo                       controllers.TrusteesInfoController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trusteesInfo.title = trusteesInfo" >> ../conf/messages.en
echo "trusteesInfo.heading = trusteesInfo" >> ../conf/messages.en

echo "Migration TrusteesInfo completed"
