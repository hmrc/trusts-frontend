package pages

class $className$PageSpec extends PageBehaviours {

  "$className$Page" must {

    beRetrievable[$className$]($className$Page)

    beSettable[$className$]($className$Page)

    beRemovable[$className$]($className$Page)
  }
}
