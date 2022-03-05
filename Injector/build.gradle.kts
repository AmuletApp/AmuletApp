import com.github.redditvanced.gradle.ProjectType

plugins {
	id("com.android.application")
	id("kotlin-android")
	id("maven-publish")
	id("redditvanced")
}

group = "com.github.redditvanced"
version = "1.0.0"

android {
	namespace = "com.github.redditvanced.injector"
	compileSdk = 30

	defaultConfig {
		minSdk = 24
		targetSdk = 30
	}

	sourceSets {
		named("main") {
			java.srcDir("src/main/kotlin")
		}
	}

	buildTypes {
		all {
			isMinifyEnabled = false
			multiDexEnabled = false
		}
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}

	kotlinOptions {
		jvmTarget = "11"
		freeCompilerArgs = freeCompilerArgs +
			"-Xno-call-assertions" +
			"-Xno-param-assertions" +
			"-Xno-receiver-assertions"
	}
}

redditVanced {
	projectType.set(ProjectType.INJECTOR)
}

dependencies {
	val redditVersion: String by project
	redditApk("::$redditVersion")

	implementation(project(":Common"))
	implementation("com.beust:klaxon:5.5")
//	implementation("com.github.Aliucord:pine:83f67b2cdb")
	implementation(files("../.assets/pine.jar"))
}

task<Jar>("sourcesJar") {
	from(android.sourceSets.named("main").get().java.srcDirs)
	archiveClassifier.set("sources")
}

afterEvaluate {
	publishing {
		publications {
			register(project.name, MavenPublication::class) {
				artifact(tasks["bundleReleaseClasses"].outputs.files.singleFile)
				artifact(tasks["sourcesJar"])
				artifact(tasks["make"])
			}
		}

		repositories {
			val username = System.getenv("MAVEN_USERNAME")
			val password = System.getenv("MAVEN_PASSWORD")

			if (username == null || password == null)
				mavenLocal()
			else maven {
				credentials {
					this.username = username
					this.password = password
				}
				setUrl("https://redditvanced.ddns.net/maven/releases")
			}
		}
	}
}
