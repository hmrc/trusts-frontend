#!/bin/bash

echo ""
echo "Applying migration CannotMakeChanges"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /cannotMakeChanges                       controllers.CannotMakeChangesController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "cannotMakeChanges.title = cannotMakeChanges" >> ../conf/messages.en
echo "cannotMakeChanges.heading = cannotMakeChanges" >> ../conf/messages.en

echo "Migration CannotMakeChanges completed"
