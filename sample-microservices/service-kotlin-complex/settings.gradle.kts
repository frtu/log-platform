rootProject.name = "service-kotlin-complex"

//include(
//    ""
//)

rootProject.children.forEach {
    it.name = it.name.replace("/", "_")
}