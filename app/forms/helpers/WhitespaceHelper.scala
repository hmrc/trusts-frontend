package forms.helpers

object WhitespaceHelper {

  val emptyToNone: Option[String] => Option[String] = _.filter(_.nonEmpty)
  val trimWhitespace: String => String = _.trim

}
