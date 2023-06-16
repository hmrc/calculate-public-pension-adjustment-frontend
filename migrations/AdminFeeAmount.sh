#!/bin/bash

echo ""
echo "Applying migration AdminFeeAmount"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /adminFeeAmount                  controllers.AdminFeeAmountController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /adminFeeAmount                  controllers.AdminFeeAmountController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAdminFeeAmount                        controllers.AdminFeeAmountController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAdminFeeAmount                        controllers.AdminFeeAmountController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "adminFeeAmount.title = AdminFeeAmount" >> ../conf/messages.en
echo "adminFeeAmount.heading = AdminFeeAmount" >> ../conf/messages.en
echo "adminFeeAmount.checkYourAnswersLabel = AdminFeeAmount" >> ../conf/messages.en
echo "adminFeeAmount.error.nonNumeric = Enter your adminFeeAmount using numbers" >> ../conf/messages.en
echo "adminFeeAmount.error.required = Enter your adminFeeAmount" >> ../conf/messages.en
echo "adminFeeAmount.error.wholeNumber = Enter your adminFeeAmount using whole numbers" >> ../conf/messages.en
echo "adminFeeAmount.error.outOfRange = AdminFeeAmount must be between {0} and {1}" >> ../conf/messages.en
echo "adminFeeAmount.change.hidden = AdminFeeAmount" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAdminFeeAmountUserAnswersEntry: Arbitrary[(AdminFeeAmountPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AdminFeeAmountPage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAdminFeeAmountPage: Arbitrary[AdminFeeAmountPage.type] =";\
    print "    Arbitrary(AdminFeeAmountPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AdminFeeAmountPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration AdminFeeAmount completed"
