#!/bin/bash

echo ""
echo "Applying migration ReportingChange"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /reportingChange                        controllers.ReportingChangeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /reportingChange                        controllers.ReportingChangeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReportingChange                  controllers.ReportingChangeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReportingChange                  controllers.ReportingChangeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "reportingChange.title = What are you reporting?" >> ../conf/messages.en
echo "reportingChange.heading = What are you reporting?" >> ../conf/messages.en
echo "reportingChange.annualAllowance = annualAllowance" >> ../conf/messages.en
echo "reportingChange.lifetimeAllowance = lifetimeAllowance" >> ../conf/messages.en
echo "reportingChange.checkYourAnswersLabel = What are you reporting?" >> ../conf/messages.en
echo "reportingChange.error.required = Select reportingChange" >> ../conf/messages.en
echo "reportingChange.change.hidden = ReportingChange" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReportingChangeUserAnswersEntry: Arbitrary[(ReportingChangePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReportingChangePage.type]";\
    print "        value <- arbitrary[ReportingChange].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReportingChangePage: Arbitrary[ReportingChangePage.type] =";\
    print "    Arbitrary(ReportingChangePage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReportingChange: Arbitrary[ReportingChange] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(ReportingChange.values)";\
    print "    }";\
    next }1' ../test-utils/generators/ModelGenerators.scala > tmp && mv tmp ../test-utils/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReportingChangePage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration ReportingChange completed"
