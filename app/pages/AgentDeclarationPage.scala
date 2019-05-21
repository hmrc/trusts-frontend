package pages

import play.api.libs.json.JsPath

case object AgentDeclarationPage extends QuestionPage[String] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "agentDeclaration"
}
