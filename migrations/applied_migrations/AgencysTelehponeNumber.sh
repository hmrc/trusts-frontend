#!/bin/bash

echo ""
echo "Applying migration AgencysTelehponeNumber"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /agencysTelehponeNumber                        controllers.AgencysTelehponeNumberController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /agencysTelehponeNumber                        controllers.AgencysTelehponeNumberController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAgencysTelehponeNumber                  controllers.AgencysTelehponeNumberController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAgencysTelehponeNumber                  controllers.AgencysTelehponeNumberController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "agencysTelehponeNumber.title = agencysTelehponeNumber" >> ../conf/messages.en
echo "agencysTelehponeNumber.heading = agencysTelehponeNumber" >> ../conf/messages.en
echo "agencysTelehponeNumber.checkYourAnswersLabel = agencysTelehponeNumber" >> ../conf/messages.en
echo "agencysTelehponeNumber.error.required = Enter agencysTelehponeNumber" >> ../conf/messages.en
echo "agencysTelehponeNumber.error.length = AgencysTelehponeNumber must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgencysTelehponeNumberUserAnswersEntry: Arbitrary[(AgencysTelehponeNumberPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AgencysTelehponeNumberPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgencysTelehponeNumberPage: Arbitrary[AgencysTelehponeNumberPage.type] =";\
    print "    Arbitrary(AgencysTelehponeNumberPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AgencysTelehponeNumberPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def agencysTelehponeNumber: Option[AnswerRow] = userAnswers.get(AgencysTelehponeNumberPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"agencysTelehponeNumber.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.AgencysTelehponeNumberController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AgencysTelehponeNumber completed"
