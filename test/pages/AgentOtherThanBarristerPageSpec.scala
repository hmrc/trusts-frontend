package pages

import pages.behaviours.PageBehaviours

class AgentOtherThanBarristerPageSpec extends PageBehaviours {

  "AgentOtherThanBarristerPage" must {

    beRetrievable[Boolean](AgentOtherThanBarristerPage)

    beSettable[Boolean](AgentOtherThanBarristerPage)

    beRemovable[Boolean](AgentOtherThanBarristerPage)
  }
}
