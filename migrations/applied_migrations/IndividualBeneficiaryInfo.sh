#!/bin/bash

echo ""
echo "Applying migration IndividualBeneficiaryInfo"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /individualBeneficiaryInfo                       controllers.register.beneficiaries.IndividualBeneficiaryInfoController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualBeneficiaryInfo.title = individualBeneficiaryInfo" >> ../conf/messages.en
echo "individualBeneficiaryInfo.heading = individualBeneficiaryInfo" >> ../conf/messages.en

echo "Migration IndividualBeneficiaryInfo completed"
