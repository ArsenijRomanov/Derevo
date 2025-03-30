plugins {
    kotlin("jvm") version "2.1.0"
    jacoco
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:5.5.5") // Основная библиотека
    testImplementation("io.kotest:kotest-assertions-core:5.5.5") // Матчеры (shouldBe и др.)
    testImplementation("io.kotest:kotest-property:5.5.5") // Property-based тестирование
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.0") // Параметризованные тесты
}

tasks.test {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(false)
        html.required.set(true)
        csv.required.set(false)
    }

    // Исключаем сгенерированные классы и тесты
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it).exclude(
                "**/*Test*",
                "**/Test*",
                "**/testing/**"
            )
        })
    )
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }

        rule {
            element = "CLASS"
            excludes = listOf("*.BinaryTreeKt") // Исключаем файлы-расширения
            limit {
                counter = "LINE"
                minimum = "0.70".toBigDecimal()
            }
        }
    }
}

tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}
