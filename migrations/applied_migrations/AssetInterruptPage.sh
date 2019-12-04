#!/bin/bash

echo ""
echo "Applying migration AssetInterruptPage"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /assetInterruptPage                       controllers.register.asset.AssetInterruptPageController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "assetInterruptPage.title = assetInterruptPage" >> ../conf/messages.en
echo "assetInterruptPage.heading = assetInterruptPage" >> ../conf/messages.en

echo "Migration AssetInterruptPage completed"
