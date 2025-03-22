plugins {
	kotlin("jvm") version "2.1.10"
	kotlin("plugin.spring") version "2.1.10"
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "2.1.10"
}

group = "dev.gamerzero"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(23)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-quartz")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-web")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.telegram:telegrambots-springboot-longpolling-starter:8.0.0")
	implementation("org.telegram:telegrambots-client:8.0.0")
	implementation("org.telegram:telegrambots-abilities:8.0.0")
	implementation("dev.langchain4j:langchain4j-open-ai-spring-boot-starter:1.0.0-beta2")
	implementation("dev.langchain4j:langchain4j-spring-boot-starter:1.0.0-beta2")
	implementation("dev.langchain4j:langchain4j-easy-rag:1.0.0-beta2")
	implementation("dev.langchain4j:langchain4j-pgvector:1.0.0-beta2")
	implementation("io.github.thibaultmeyer:cuid:2.0.3")
	implementation("com.aallam.openai:openai-client:4.0.1")
	implementation("io.ktor:ktor-client-java:3.1.1")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
