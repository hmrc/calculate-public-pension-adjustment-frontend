#!/bin/bash

echo ""
echo "Applying migration AdminFeeDetails"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /adminFeeDetails                        controllers.AdminFeeDetailsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /adminFeeDetails                        controllers.AdminFeeDetailsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAdminFeeDetails                  controllers.AdminFeeDetailsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAdminFeeDetails                  controllers.AdminFeeDetailsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "adminFeeDetails.title = adminFeeDetails" >> ../conf/messages.en
echo "adminFeeDetails.heading = adminFeeDetails" >> ../conf/messages.en
echo "adminFeeDetails.hint = adminFeeDetails" >> ../conf/messages.en
echo "adminFeeDetails.checkYourAnswersLabel = adminFeeDetails" >> ../conf/messages.en
echo "adminFeeDetails.error.required = Enter adminFeeDetails" >> ../conf/messages.en
echo "adminFeeDetails.error.length = AdminFeeDetails must be 500 characters or less" >> ../conf/messages.en
echo "adminFeeDetails.change.hidden = AdminFeeDetails" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAdminFeeDetailsUserAnswersEntry: Arbitrary[(AdminFeeDetailsPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AdminFeeDetailsPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAdminFeeDetailsPage: Arbitrary[AdminFeeDetailsPage.type] =";\
    print "    Arbitrary(AdminFeeDetailsPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AdminFeeDetailsPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration AdminFeeDetails completed"
