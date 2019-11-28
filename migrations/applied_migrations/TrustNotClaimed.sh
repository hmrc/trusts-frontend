#!/bin/bash

echo ""
echo "Applying migration TrustNotClaimed"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /:draftId/trustNotClaimed                       controllers.playback.TrustNotClaimedController.onPageLoad(draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "trustNotClaimed.title = trustNotClaimed" >> ../conf/messages.en
echo "trustNotClaimed.heading = trustNotClaimed" >> ../conf/messages.en

echo "Migration TrustNotClaimed completed"
