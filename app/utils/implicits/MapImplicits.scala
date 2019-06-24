package utils.implicits


object MapImplicits {

  implicit class NonEmptyValueMap(x : Map[String, String]) {

    def clean : Map[String, String] = {
      x.filter {case (_, value) => value.nonEmpty}
    }

  }

}