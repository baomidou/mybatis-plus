dependencies {
    val lib: Map<String, Any> by rootProject.extra

    api(project(":mybatis-plus-extension"))

    implementation("${lib["velocity"]}")
    implementation("${lib["freemarker"]}")
    implementation("${lib["beetl"]}")

    testCompile("${lib["sqlserver"]}")
    testCompile("${lib["postgresql"]}")
    testCompile(lib["oracle"] as ConfigurableFileTree)
    testCompile("${lib["h2"]}")
    testCompile("${lib["mysql"]}")
    testCompile("${lib["logback-classic"]}")
}
