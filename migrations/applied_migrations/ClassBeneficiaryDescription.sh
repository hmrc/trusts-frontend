#!/bin/bash

echo ""
echo "Applying migration ClassBeneficiaryDescription"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /classBeneficiaryDescription                        controllers.ClassBeneficiaryDescriptionController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /classBeneficiaryDescription                        controllers.ClassBeneficiaryDescriptionController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeClassBeneficiaryDescription                  controllers.ClassBeneficiaryDescriptionController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeClassBeneficiaryDescription                  controllers.ClassBeneficiaryDescriptionController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "classBeneficiaryDescription.title = classBeneficiaryDescription" >> ../conf/messages.en
echo "classBeneficiaryDescription.heading = classBeneficiaryDescription" >> ../conf/messages.en
echo "classBeneficiaryDescription.checkYourAnswersLabel = classBeneficiaryDescription" >> ../conf/messages.en
echo "classBeneficiaryDescription.error.required = Enter classBeneficiaryDescription" >> ../conf/messages.en
echo "classBeneficiaryDescription.error.length = ClassBeneficiaryDescription must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryClassBeneficiaryDescriptionUserAnswersEntry: Arbitrary[(ClassBeneficiaryDescriptionPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ClassBeneficiaryDescriptionPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryClassBeneficiaryDescriptionPage: Arbitrary[ClassBeneficiaryDescriptionPage.type] =";\
    print "    Arbitrary(ClassBeneficiaryDescriptionPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ClassBeneficiaryDescriptionPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def classBeneficiaryDescription: Option[AnswerRow] = userAnswers.get(ClassBeneficiaryDescriptionPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"classBeneficiaryDescription.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(x),";\
     print "        routes.ClassBeneficiaryDescriptionController.onPageLoad(CheckMode).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ClassBeneficiaryDescription completed"
