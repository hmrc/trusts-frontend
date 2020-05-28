package pages

class $className$PageSpec extends PageBehaviours {

  "$className$Page" must {

    beRetrievable[Int]($className$Page)

    beSettable[Int]($className$Page)

    beRemovable[Int]($className$Page)
  }
}
