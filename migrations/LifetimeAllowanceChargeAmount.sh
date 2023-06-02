#!/bin/bash

echo ""
echo "Applying migration LifetimeAllowanceChargeAmount"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /lifetimeAllowanceChargeAmount                  controllers.LifetimeAllowanceChargeAmountController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /lifetimeAllowanceChargeAmount                  controllers.LifetimeAllowanceChargeAmountController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeLifetimeAllowanceChargeAmount                        controllers.LifetimeAllowanceChargeAmountController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeLifetimeAllowanceChargeAmount                        controllers.LifetimeAllowanceChargeAmountController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "lifetimeAllowanceChargeAmount.title = LifetimeAllowanceChargeAmount" >> ../conf/messages.en
echo "lifetimeAllowanceChargeAmount.heading = LifetimeAllowanceChargeAmount" >> ../conf/messages.en
echo "lifetimeAllowanceChargeAmount.checkYourAnswersLabel = LifetimeAllowanceChargeAmount" >> ../conf/messages.en
echo "lifetimeAllowanceChargeAmount.error.nonNumeric = Enter your lifetimeAllowanceChargeAmount using numbers" >> ../conf/messages.en
echo "lifetimeAllowanceChargeAmount.error.required = Enter your lifetimeAllowanceChargeAmount" >> ../conf/messages.en
echo "lifetimeAllowanceChargeAmount.error.wholeNumber = Enter your lifetimeAllowanceChargeAmount using whole numbers" >> ../conf/messages.en
echo "lifetimeAllowanceChargeAmount.error.outOfRange = LifetimeAllowanceChargeAmount must be between {0} and {1}" >> ../conf/messages.en
echo "lifetimeAllowanceChargeAmount.change.hidden = LifetimeAllowanceChargeAmount" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryLifetimeAllowanceChargeAmountUserAnswersEntry: Arbitrary[(LifetimeAllowanceChargeAmountPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[LifetimeAllowanceChargeAmountPage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryLifetimeAllowanceChargeAmountPage: Arbitrary[LifetimeAllowanceChargeAmountPage.type] =";\
    print "    Arbitrary(LifetimeAllowanceChargeAmountPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(LifetimeAllowanceChargeAmountPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration LifetimeAllowanceChargeAmount completed"
