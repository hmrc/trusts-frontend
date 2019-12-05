#!/bin/bash

echo ""
echo "Applying migration TaskList"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /taskList                       controllers.register.TaskListController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "taskList.title = taskList" >> ../conf/messages.en
echo "taskList.heading = taskList" >> ../conf/messages.en

echo "Migration TaskList completed"
