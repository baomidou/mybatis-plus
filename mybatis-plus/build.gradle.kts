dependencies {
    val lib: Map<String, Any> by rootProject.extra

    api(project(":mybatis-plus-extension"))
    implementation(project(":mybatis-plus-generator"))

    testImplementation("${lib["spring-web"]}")
    testImplementation("${lib["javax.servlet-api"]}")

    testImplementation("${lib["spring-test"]}")
    testImplementation("${lib["fastjson"]}")

    testImplementation("${lib["hikaricp"]}")
    testImplementation("${lib["commons-dbcp2"]}")
    testImplementation("${lib["druid"]}")
    testImplementation("${lib["tomcatjdbc"]}")

    testImplementation("${lib["h2"]}")
    testImplementation("${lib["sqlserver"]}")
    testImplementation("${lib["postgresql"]}")
    testImplementation(lib["oracle"] as ConfigurableFileTree)
    testImplementation("${lib["mysql"]}")
    testImplementation("${lib["jackson"]}")
    testImplementation("${lib["logback-classic"]}")

    testImplementation("${lib["spring-context-support"]}")
    testImplementation("${lib["spring-jdbc"]}")
}
