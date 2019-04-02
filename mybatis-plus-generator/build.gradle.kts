dependencies {
    val lib: Map<String, Any> by rootProject.extra

    api(project(":mybatis-plus-extension"))

    implementation("${lib["velocity"]}")
    implementation("${lib["freemarker"]}")
    implementation("${lib["beetl"]}")

    testImplementation("${lib["sqlserver"]}")
    testImplementation("${lib["postgresql"]}")
    testImplementation(lib["oracle"] as ConfigurableFileTree)
    testImplementation("${lib["h2"]}")
    testImplementation("${lib["mysql"]}")
    testImplementation("${lib["logback-classic"]}")
    testImplementation("${lib["swagger-annotations"]}")
}
