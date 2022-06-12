rootProject.name = "service-gradle-kotlin"

//include(
//    ""
//)

rootProject.children.forEach {
    it.name = it.name.replace("/", "_")
}