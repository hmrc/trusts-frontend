#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:draftId/$className;format="decap"$                        controllers.$className$Controller.onPageLoad(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/$className;format="decap"$                        controllers.$className$Controller.onSubmit(mode: Mode = NormalMode, draftId: String)" >> ../conf/app.routes

echo "GET        /:draftId/change$className$                  controllers.$className$Controller.onPageLoad(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes
echo "POST       /:draftId/change$className$                  controllers.$className$Controller.onSubmit(mode: Mode = CheckMode, draftId: String)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$className;format="decap"$.title = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.heading = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.field1 = Field 1" >> ../conf/messages.en
echo "$className;format="decap"$.field2 = Field 2" >> ../conf/messages.en
echo "$className;format="decap"$.checkYourAnswersLabel = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.error.field1.required = Enter field1" >> ../conf/messages.en
echo "$className;format="decap"$.error.field2.required = Enter field2" >> ../conf/messages.en
echo "$className;format="decap"$.error.field1.length = field1 must be $field1MaxLength$ characters or less" >> ../conf/messages.en
echo "$className;format="decap"$.error.field2.length = field2 must be $field2MaxLength$ characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrary$className$UserAnswersEntry: Arbitrary[($className$Page.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[$className$Page.type]";\
    print "        value <- arbitrary[$className$].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrary$className$Page: Arbitrary[$className$Page.type] =";\
    print "    Arbitrary($className$Page)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrary$className$: Arbitrary[$className$] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        field1 <- arbitrary[String]";\
    print "        field2 <- arbitrary[String]";\
    print "      } yield $className$(field1, field2)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[($className$Page.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class / {\
     print;\
     print "";\
     print "  def $className;format="decap"$: Option[AnswerRow] = userAnswers.get($className$Page) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        \"$className;format="decap"$.checkYourAnswersLabel\",";\
     print "        HtmlFormat.escape(s\"\${x.field1} \${x.field2}\"),";\
     print "        routes.$className$Controller.onPageLoad(NormalMode, draftId).url";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration $className;format="snake"$ completed"
