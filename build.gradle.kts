plugins {
    id("java")
    id("war")
    id("org.gretty") version "4.1.3" // Gretty плагін
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// 🛠️ ВИПРАВЛЕННЯ: БЛОК GRETTY МАЄ БУТИ АКТИВНИМ
gretty {
    httpPort = 8080
    contextPath = "/api" // Встановлює URI розгортання: http://localhost:8080/api
    servletContainer = "tomcat10" // Використовує Tomcat, сумісний з Jakarta EE
}

dependencies {
    // Веб-залежності (додано для компіляції)
    // Примітка: видалено дублікат compileOnly, залишаємо один
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")

    // Тестові залежності
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:3.26.3")

    // Core залежності
    implementation("org.jetbrains:annotations:24.0.1")
    implementation("org.slf4j:slf4j-api:2.0.9")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.9")

    // Jackson залежності
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")

    // Валідація
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("org.glassfish:jakarta.el:4.0.2")
}

tasks.test {
    useJUnitPlatform()
}