#!/bin/bash

echo ""
echo "Applying migration WhoPaidLTACharge"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whoPaidLTACharge                        controllers.WhoPaidLTAChargeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whoPaidLTACharge                        controllers.WhoPaidLTAChargeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhoPaidLTACharge                  controllers.WhoPaidLTAChargeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhoPaidLTACharge                  controllers.WhoPaidLTAChargeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whoPaidLTACharge.title = Who paid your lifetime allowance charge?" >> ../conf/messages.en
echo "whoPaidLTACharge.heading = Who paid your lifetime allowance charge?" >> ../conf/messages.en
echo "whoPaidLTACharge.you = you" >> ../conf/messages.en
echo "whoPaidLTACharge.pension scheme = pensionSchempensionScheme" >> ../conf/messages.en
echo "whoPaidLTACharge.checkYourAnswersLabel = Who paid your lifetime allowance charge?" >> ../conf/messages.en
echo "whoPaidLTACharge.error.required = Select whoPaidLTACharge" >> ../conf/messages.en
echo "whoPaidLTACharge.change.hidden = WhoPaidLTACharge" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhoPaidLTAChargeUserAnswersEntry: Arbitrary[(WhoPaidLTAChargePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhoPaidLTAChargePage.type]";\
    print "        value <- arbitrary[WhoPaidLTACharge].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhoPaidLTAChargePage: Arbitrary[WhoPaidLTAChargePage.type] =";\
    print "    Arbitrary(WhoPaidLTAChargePage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhoPaidLTACharge: Arbitrary[WhoPaidLTACharge] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(WhoPaidLTACharge.values.toSeq)";\
    print "    }";\
    next }1' ../test-utils/generators/ModelGenerators.scala > tmp && mv tmp ../test-utils/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhoPaidLTAChargePage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration WhoPaidLTACharge completed"
