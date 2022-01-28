import com.github.redditvanced.gradle.ProjectType

plugins {
	kotlin("android") version "1.6.10"
	id("com.android.library")
	id("maven-publish")
	id("redditvanced")
}

group = "com.github.redditvanced"
version = "1.0.0"

android {
	compileSdk = 30

	defaultConfig {
		minSdk = 24
		targetSdk = 30
	}

	buildTypes {
		release {
			isMinifyEnabled = false
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

	buildFeatures {
		viewBinding = true
	}
}

redditVanced {
	redditVancedBackend.set("http://localhost:8080")
	projectType.set(ProjectType.CORE)
}

dependencies {
	api(project(":Common"))
	api(files("../.assets/pine.jar"))

	implementation("com.beust:klaxon:5.5")

	val redditVersion: String by project
	redditApk("::$redditVersion")
}

afterEvaluate {
	publishing {
		publications {
			register(project.name, MavenPublication::class) {
				from(components["debug"])
				artifact(tasks["debugSourcesJar"])
			}
		}
	}
}
