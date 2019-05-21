package pages

import pages.behaviours.PageBehaviours


class AgentDeclarationPageSpec extends PageBehaviours {

  "AgentDeclarationPage" must {

    beRetrievable[String](AgentDeclarationPage)

    beSettable[String](AgentDeclarationPage)

    beRemovable[String](AgentDeclarationPage)
  }
}
