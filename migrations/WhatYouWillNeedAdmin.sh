#!/bin/bash

echo ""
echo "Applying migration WhatYouWillNeedAdmin"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /whatYouWillNeedAdmin                       controllers.WhatYouWillNeedAdminController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whatYouWillNeedAdmin.title = whatYouWillNeedAdmin" >> ../conf/messages.en
echo "whatYouWillNeedAdmin.heading = whatYouWillNeedAdmin" >> ../conf/messages.en

echo "Migration WhatYouWillNeedAdmin completed"
