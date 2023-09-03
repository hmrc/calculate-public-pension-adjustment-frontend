#!/bin/bash

echo ""
echo "Applying migration MultipleBenefitCrystallisationEvent"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /multipleBenefitCrystallisationEvent                        controllers.MultipleBenefitCrystallisationEventController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /multipleBenefitCrystallisationEvent                        controllers.MultipleBenefitCrystallisationEventController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeMultipleBenefitCrystallisationEvent                  controllers.MultipleBenefitCrystallisationEventController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeMultipleBenefitCrystallisationEvent                  controllers.MultipleBenefitCrystallisationEventController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "multipleBenefitCrystallisationEvent.title = multipleBenefitCrystallisationEvent" >> ../conf/messages.en
echo "multipleBenefitCrystallisationEvent.heading = multipleBenefitCrystallisationEvent" >> ../conf/messages.en
echo "multipleBenefitCrystallisationEvent.checkYourAnswersLabel = multipleBenefitCrystallisationEvent" >> ../conf/messages.en
echo "multipleBenefitCrystallisationEvent.error.required = Select yes if multipleBenefitCrystallisationEvent" >> ../conf/messages.en
echo "multipleBenefitCrystallisationEvent.change.hidden = MultipleBenefitCrystallisationEvent" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryMultipleBenefitCrystallisationEventUserAnswersEntry: Arbitrary[(MultipleBenefitCrystallisationEventPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[MultipleBenefitCrystallisationEventPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryMultipleBenefitCrystallisationEventPage: Arbitrary[MultipleBenefitCrystallisationEventPage.type] =";\
    print "    Arbitrary(MultipleBenefitCrystallisationEventPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(MultipleBenefitCrystallisationEventPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration MultipleBenefitCrystallisationEvent completed"
