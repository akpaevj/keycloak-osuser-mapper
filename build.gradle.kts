plugins {
    id("java")
}

group = "ru.akpaev.keycloak"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.keycloak:keycloak-core:24.0.1")
    implementation("org.keycloak:keycloak-server-spi:24.0.1")
    implementation("org.keycloak:keycloak-server-spi-private:24.0.1")
    implementation("org.keycloak:keycloak-services:24.0.1")
}

tasks.jar {
    archiveFileName = "${project.name}.jar"
}

tasks.test {
    useJUnitPlatform()
}