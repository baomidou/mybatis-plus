import groovy.util.Node
import groovy.util.NodeList
import nl.javadude.gradle.plugins.license.License

buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.21")
        classpath("com.netflix.nebula:gradle-extra-configurations-plugin:3.0.3")
        classpath("gradle.plugin.com.hierynomus.gradle.plugins:license-gradle-plugin:0.15.0")
    }
}

plugins {
    `java-library`
    signing
    `maven-publish`
}

// versions
val javaVersion = JavaVersion.VERSION_1_8
val mybatisSpringVersion = "2.0.0"
val mybatisVersion = "3.5.0"
val springVersion = "5.1.4.RELEASE"
val jsqlparserVersion = "1.4"
val junitVersion = "5.4.0"
val lombokVersion = "1.18.4"
val cglibVersion = "3.2.10"

// libs
val lib = mapOf(
    "kotlin-reflect"             to "org.jetbrains.kotlin:kotlin-reflect:1.3.21",
    "kotlin-stdlib-jdk8"         to "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.21",
    "jsqlparser"                 to "com.github.jsqlparser:jsqlparser:$jsqlparserVersion",
    "mybatis-spring"             to "org.mybatis:mybatis-spring:$mybatisSpringVersion",
    "mybatis"                    to "org.mybatis:mybatis:$mybatisVersion",
    "spring-context-support"     to "org.springframework:spring-context-support:$springVersion",
    "spring-jdbc"                to "org.springframework:spring-jdbc:$springVersion",
    "spring-tx"                  to "org.springframework:spring-tx:$springVersion",
    "spring-web"                 to "org.springframework:spring-web:$springVersion",
    "spring-aop"                 to "org.springframework:spring-aop:$springVersion",
    "cglib"                      to "cglib:cglib:$cglibVersion",
    "lombok"                     to "org.projectlombok:lombok:$lombokVersion",

    "javax.servlet-api"          to "javax.servlet:javax.servlet-api:4.0.1",
    "aspectjweaver"              to "org.aspectj:aspectjweaver:1.9.2",
    "mockito"                    to "org.mockito:mockito-core:2.24.0",
    "mybatis-ehcache"            to "org.mybatis.caches:mybatis-ehcache:1.1.0",
    "slf4j-api"                  to "org.slf4j:slf4j-api:1.7.25",
    "logback-classic"            to "ch.qos.logback:logback-classic:1.2.3",
    // test
    "spring-test"                to "org.springframework:spring-test:$springVersion",
    "junit-jupiter-api"          to "org.junit.jupiter:junit-jupiter-api:$junitVersion",
    "junit-jupiter-engine"       to "org.junit.jupiter:junit-jupiter-engine:$junitVersion",
    "mockito-all"                to "org.mockito:mockito-all:1.10.19",
    "fastjson"                   to "com.alibaba:fastjson:1.2.56",
    "jackson"                    to "com.fasterxml.jackson.core:jackson-databind:2.9.6",
    "tomcatjdbc"                 to "org.apache.tomcat:tomcat-jdbc:9.0.16",
    // datasource
    "p6spy"                      to "p6spy:p6spy:3.8.1",
    "hikaricp"                   to "com.zaxxer:HikariCP:3.3.0",
    "druid"                      to "com.alibaba:druid:1.1.13",
    "commons-dbcp2"              to "org.apache.commons:commons-dbcp2:2.5.0",
    "sqlserver"                  to "com.microsoft.sqlserver:sqljdbc4:4.0",
    "postgresql"                 to "org.postgresql:postgresql:42.2.5",
    "oracle"                     to fileTree("libs/ojdbc-11.2.0.3-jdk16.jar"),
    "h2"                         to "com.h2database:h2:1.4.197",
    "mysql"                      to "mysql:mysql-connector-java:8.0.15",
    // code generator
    "velocity"                   to "org.apache.velocity:velocity-engine-core:2.0",
    "freemarker"                 to "org.freemarker:freemarker:2.3.28",
    "beetl"                      to "com.ibeetl:beetl:2.9.8",
    "lagarto"                    to "org.jodd:jodd-lagarto:5.0.7",
    "swagger-annotations"        to "io.swagger:swagger-annotations:1.5.21"
)
// ext
extra["lib"] = lib

allprojects {
    group = "com.baomidou"
    version = "3.1.0"
}

description = "Mybatis 增强工具包 - 只做增强不做改变，简化CRUD操作"

subprojects {
    // 插件
    apply(plugin = "org.gradle.java-library")
    apply(plugin = "org.gradle.maven-publish")
    apply(plugin = "org.gradle.signing")
    apply(plugin = "com.github.hierynomus.license")

    // Java 版本
    configure<JavaPluginConvention> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    // 编译器配置
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.isDeprecation = true
        options.compilerArgs.add("-parameters")
    }

    tasks.withType<Jar> {
        afterEvaluate {
            manifest {
                attributes["Implementation-Version"] = version
            }
        }
    }
    tasks.withType<License> {
        encoding = "UTF-8"
        header = rootProject.file("license.txt")
        include("**/*.java","**/*.kt")
        mapping("java","SLASHSTAR_STYLE")
        mapping("kt","SLASHSTAR_STYLE")
        extra["year"] = 2019
        extra["name"] = "hubin"
        extra["email"] = "jobob@qq.com"
    }

    repositories {
        mavenLocal()
        maven("https://maven.aliyun.com/nexus/contencommons-dbcpt/groups/public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("http://www.cameliatk.jp/maven2/repository/thirdparty")
        jcenter()
    }

    dependencies {
        annotationProcessor("${lib["lombok"]}")
        compileOnly("${lib["lombok"]}")

        testAnnotationProcessor("${lib["lombok"]}")
        testCompileOnly("${lib["lombok"]}")
        testImplementation("${lib["junit-jupiter-api"]}")
        testRuntimeOnly("${lib["junit-jupiter-engine"]}")
        testCompileOnly("${lib["mockito-all"]}")
        testImplementation("org.mockito:mockito-junit-jupiter:2.24.0")
        testImplementation("${lib["lagarto"]}")
    }

    val sourcesJar by tasks.registering(Jar::class) {
        dependsOn(JavaPlugin.CLASSES_TASK_NAME)
        archiveClassifier.set("sources")
        from(sourceSets["main"].allJava)
    }

    tasks.withType<Javadoc> {
        options.encoding = "UTF-8"
        isFailOnError = false
        (options as? StandardJavadocDocletOptions)?.also {
            it.charSet = "UTF-8"
            it.isAuthor = true
            it.isVersion = true
            it.links = listOf("https://docs.oracle.com/javase/8/docs/api")
            if (JavaVersion.current().isJava9Compatible) {
                it.addBooleanOption("html5", true)
            }
        }
    }

    tasks.withType<Test> {
        dependsOn("cleanTest", "generatePomFileForMavenJavaPublication")
        useJUnitPlatform()
        exclude("**/generator/**")
        exclude("**/postgres/**")
        exclude("**/mysql/**")
    }

    val javadocJar by tasks.registering(Jar::class) {
        dependsOn(JavaPlugin.JAVADOC_TASK_NAME)
        archiveClassifier.set("javadoc")
        from(tasks["javadoc"])
    }

    tasks.whenTaskAdded {
        if (this.name.contains("signMavenJavaPublication")) {
            this.enabled = File(project.property("signing.secretKeyRingFile") as String).isFile
        }
    }

    publishing {
        repositories {
            maven {
                val userName = System.getProperty("un")
                val passWord = System.getProperty("ps")
                val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
                val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
                setUrl(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)

                credentials {
                    username = userName
                    password = passWord
                }
            }
        }
        publications {
            register("mavenJava", MavenPublication::class) {
                pom {
                    name.set("mybatis-plus")
                    description.set("An enhanced toolkit of Mybatis to simplify development.")
                    inceptionYear.set("2016")
                    url.set("https://github.com/baomidou/mybatis-plus")

                    artifactId = project.name
                    groupId = "${project.group}"
                    version = "${project.version}"
                    packaging = "jar"

                    organization {
                        name.set("baomidou")
                    }

                    scm {
                        connection.set("scm:git@github.com:Codearte/gradle-nexus-staging-plugin.git")
                        developerConnection.set("scm:git@github.com:Codearte/gradle-nexus-staging-plugin.git")
                        url.set("https://github.com/baomidou/mybatis-plus")
                    }

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }

                    developers {
                        developer {
                            id.set("baomidou")
                            name.set("hubin")
                            email.set("jobob@qq.com")
                        }
                    }

                    withXml {
                        fun Any?.asNode() = this as Node
                        fun Any?.asNodeList() = this as NodeList

                        val root = asNode()
                        root["dependencies"].asNodeList().getAt("*").forEach {
                            val dependency = it.asNode()
                            if (dependency["scope"].asNodeList().text() == "runtime") {
                                if (project.configurations.findByName("implementation")?.allDependencies?.none { dep ->
                                        dep.name == dependency["artifactId"].asNodeList().text()
                                    } == false) {
                                    dependency["scope"].asNodeList().forEach { it.asNode().setValue("compile") }
                                    dependency.appendNode("optional", true)
                                }
                            }
                        }
                    }
                }

                from(components["java"])
                artifact(sourcesJar.get())
                artifact(javadocJar.get())
            }
        }
    }

    signing {
        sign(publishing.publications.getByName("mavenJava"))
    }
}
