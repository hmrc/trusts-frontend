#!/bin/bash

echo ""
echo "Applying migration MyNewPage"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /myNewPage                       controllers.MyNewPageController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "myNewPage.title = myNewPage" >> ../conf/messages.en
echo "myNewPage.heading = myNewPage" >> ../conf/messages.en

echo "Migration MyNewPage completed"
