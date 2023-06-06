#!/bin/bash

echo ""
echo "Applying migration SchemeNameAndTaxRef"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /schemeNameAndTaxRef                        controllers.SchemeNameAndTaxRefController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /schemeNameAndTaxRef                        controllers.SchemeNameAndTaxRefController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSchemeNameAndTaxRef                  controllers.SchemeNameAndTaxRefController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSchemeNameAndTaxRef                  controllers.SchemeNameAndTaxRefController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "schemeNameAndTaxRef.title = schemeNameAndTaxRef" >> ../conf/messages.en
echo "schemeNameAndTaxRef.heading = schemeNameAndTaxRef" >> ../conf/messages.en
echo "schemeNameAndTaxRef.checkYourAnswersLabel = schemeNameAndTaxRef" >> ../conf/messages.en
echo "schemeNameAndTaxRef.error.required = Enter schemeNameAndTaxRef" >> ../conf/messages.en
echo "schemeNameAndTaxRef.error.length = SchemeNameAndTaxRef must be 100 characters or less" >> ../conf/messages.en
echo "schemeNameAndTaxRef.change.hidden = SchemeNameAndTaxRef" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySchemeNameAndTaxRefUserAnswersEntry: Arbitrary[(SchemeNameAndTaxRefPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SchemeNameAndTaxRefPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySchemeNameAndTaxRefPage: Arbitrary[SchemeNameAndTaxRefPage.type] =";\
    print "    Arbitrary(SchemeNameAndTaxRefPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SchemeNameAndTaxRefPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration SchemeNameAndTaxRef completed"
