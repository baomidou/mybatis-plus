apply(plugin = "org.jetbrains.kotlin.jvm")

dependencies {
    val lib: Map<String, Any> by rootProject.extra

    api(project(":mybatis-plus-core"))
    api("${lib["mybatis-spring"]}")

    implementation("${lib["kotlin-stdlib-jdk8"]}")
    implementation("${lib["kotlin-reflect"]}")
    implementation("${lib["spring-context-support"]}")
    implementation("${lib["spring-jdbc"]}")
    implementation("${lib["slf4j-api"]}")
    implementation("${lib["p6spy"]}")

    testImplementation("${lib["spring-web"]}")
    testImplementation("${lib["javax.servlet-api"]}")
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
    testImplementation("${lib["logback-classic"]}")
}
