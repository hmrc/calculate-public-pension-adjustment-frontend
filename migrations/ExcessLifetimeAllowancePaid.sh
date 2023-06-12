#!/bin/bash

echo ""
echo "Applying migration ExcessLifetimeAllowancePaid"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /excessLifetimeAllowancePaid                        controllers.ExcessLifetimeAllowancePaidController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /excessLifetimeAllowancePaid                        controllers.ExcessLifetimeAllowancePaidController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeExcessLifetimeAllowancePaid                  controllers.ExcessLifetimeAllowancePaidController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeExcessLifetimeAllowancePaid                  controllers.ExcessLifetimeAllowancePaidController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "excessLifetimeAllowancePaid.title = How was the excess above your lifetime allowance paid to you?" >> ../conf/messages.en
echo "excessLifetimeAllowancePaid.heading = How was the excess above your lifetime allowance paid to you?" >> ../conf/messages.en
echo "excessLifetimeAllowancePaid.annualPayment = annualPayment" >> ../conf/messages.en
echo "excessLifetimeAllowancePaid.lumpSum = lumpSum" >> ../conf/messages.en
echo "excessLifetimeAllowancePaid.checkYourAnswersLabel = How was the excess above your lifetime allowance paid to you?" >> ../conf/messages.en
echo "excessLifetimeAllowancePaid.error.required = Select excessLifetimeAllowancePaid" >> ../conf/messages.en
echo "excessLifetimeAllowancePaid.change.hidden = ExcessLifetimeAllowancePaid" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryExcessLifetimeAllowancePaidUserAnswersEntry: Arbitrary[(ExcessLifetimeAllowancePaidPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ExcessLifetimeAllowancePaidPage.type]";\
    print "        value <- arbitrary[ExcessLifetimeAllowancePaid].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryExcessLifetimeAllowancePaidPage: Arbitrary[ExcessLifetimeAllowancePaidPage.type] =";\
    print "    Arbitrary(ExcessLifetimeAllowancePaidPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryExcessLifetimeAllowancePaid: Arbitrary[ExcessLifetimeAllowancePaid] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(ExcessLifetimeAllowancePaid.values.toSeq)";\
    print "    }";\
    next }1' ../test-utils/generators/ModelGenerators.scala > tmp && mv tmp ../test-utils/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ExcessLifetimeAllowancePaidPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration ExcessLifetimeAllowancePaid completed"
