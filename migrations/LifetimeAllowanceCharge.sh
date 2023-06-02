#!/bin/bash

echo ""
echo "Applying migration LifetimeAllowanceCharge"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /lifetimeAllowanceCharge                        controllers.LifetimeAllowanceChargeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /lifetimeAllowanceCharge                        controllers.LifetimeAllowanceChargeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeLifetimeAllowanceCharge                  controllers.LifetimeAllowanceChargeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeLifetimeAllowanceCharge                  controllers.LifetimeAllowanceChargeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "lifetimeAllowanceCharge.title = lifetimeAllowanceCharge" >> ../conf/messages.en
echo "lifetimeAllowanceCharge.heading = lifetimeAllowanceCharge" >> ../conf/messages.en
echo "lifetimeAllowanceCharge.checkYourAnswersLabel = lifetimeAllowanceCharge" >> ../conf/messages.en
echo "lifetimeAllowanceCharge.error.required = Select yes if lifetimeAllowanceCharge" >> ../conf/messages.en
echo "lifetimeAllowanceCharge.change.hidden = LifetimeAllowanceCharge" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryLifetimeAllowanceChargeUserAnswersEntry: Arbitrary[(LifetimeAllowanceChargePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[LifetimeAllowanceChargePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryLifetimeAllowanceChargePage: Arbitrary[LifetimeAllowanceChargePage.type] =";\
    print "    Arbitrary(LifetimeAllowanceChargePage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(LifetimeAllowanceChargePage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration LifetimeAllowanceCharge completed"
