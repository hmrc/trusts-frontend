package pages

class $className$Spec extends PageBehaviours {

  "$className$Page" must {

    beRetrievable[$className$]($className$Page)

    beSettable[$className$]($className$Page)

    beRemovable[$className$]($className$Page)
  }
}
