dependencies {
    val lib: Map<String, Any> by rootProject.extra

    api(project(":mybatis-plus-annotation"))
    api("${lib["mybatis"]}")
    api("${lib["jsqlparser"]}")

    implementation("${lib["cglib"]}")

    testImplementation("${lib["mybatis-ehcache"]}")
    testImplementation("${lib["logback-classic"]}")
    testImplementation("${lib["commons-dbcp2"]}") {
        exclude(module = "commons-logging")
    }
    testImplementation("${lib["aspectjweaver"]}")
    testImplementation("${lib["hikaricp"]}")
    testImplementation("${lib["druid"]}")
    testImplementation("${lib["fastjson"]}")
    testImplementation("${lib["tomcatjdbc"]}")
}
