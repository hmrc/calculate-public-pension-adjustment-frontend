#!/bin/bash

echo ""
echo "Applying migration LtaPensionSchemeDetails"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /ltaPensionSchemeDetails                        controllers.LtaPensionSchemeDetailsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /ltaPensionSchemeDetails                        controllers.LtaPensionSchemeDetailsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeLtaPensionSchemeDetails                  controllers.LtaPensionSchemeDetailsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeLtaPensionSchemeDetails                  controllers.LtaPensionSchemeDetailsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "ltaPensionSchemeDetails.title = ltaPensionSchemeDetails" >> ../conf/messages.en
echo "ltaPensionSchemeDetails.heading = ltaPensionSchemeDetails" >> ../conf/messages.en
echo "ltaPensionSchemeDetails.Name = Name" >> ../conf/messages.en
echo "ltaPensionSchemeDetails.Reference = Reference" >> ../conf/messages.en
echo "ltaPensionSchemeDetails.checkYourAnswersLabel = LtaPensionSchemeDetails" >> ../conf/messages.en
echo "ltaPensionSchemeDetails.error.Name.required = Enter Name" >> ../conf/messages.en
echo "ltaPensionSchemeDetails.error.Reference.required = Enter Reference" >> ../conf/messages.en
echo "ltaPensionSchemeDetails.error.Name.length = Name must be 20 characters or less" >> ../conf/messages.en
echo "ltaPensionSchemeDetails.error.Reference.length = Reference must be 20 characters or less" >> ../conf/messages.en
echo "ltaPensionSchemeDetails.Name.change.hidden = Name" >> ../conf/messages.en
echo "ltaPensionSchemeDetails.Reference.change.hidden = Reference" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryLtaPensionSchemeDetailsUserAnswersEntry: Arbitrary[(LtaPensionSchemeDetailsPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[LtaPensionSchemeDetailsPage.type]";\
    print "        value <- arbitrary[LtaPensionSchemeDetails].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryLtaPensionSchemeDetailsPage: Arbitrary[LtaPensionSchemeDetailsPage.type] =";\
    print "    Arbitrary(LtaPensionSchemeDetailsPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryLtaPensionSchemeDetails: Arbitrary[LtaPensionSchemeDetails] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        Name <- arbitrary[String]";\
    print "        Reference <- arbitrary[String]";\
    print "      } yield LtaPensionSchemeDetails(Name, Reference)";\
    print "    }";\
    next }1' ../test-utils/generators/ModelGenerators.scala > tmp && mv tmp ../test-utils/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(LtaPensionSchemeDetailsPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration LtaPensionSchemeDetails completed"
