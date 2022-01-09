plugins {
	id("com.android.library")
	id("maven-publish")
	id("kotlin-android")
	id("org.jetbrains.dokka")
	kotlin("plugin.serialization") version "1.6.0"
}

fun getGitHash(): String {
	val stdout = org.apache.commons.io.output.ByteArrayOutputStream()
	exec {
		commandLine = listOf("git", "rev-parse", "--short", "HEAD")
		standardOutput = stdout
		isIgnoreExitValue = true
	}
	return stdout.toString().trim()
}

android {
	compileSdk = 30

	lintOptions.disable("GradleDependency")

	defaultConfig {
		minSdk = 24
		targetSdk = 30

		buildConfigField("String", "GIT_REVISION", "\"${getGitHash()}\"")
//        buildConfigField("int", "DISCORD_VERSION", findProperty("discord_version") as String)
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
			"-Xno-receiver-assertions" +
			"-Xuse-experimental=kotlinx.serialization.ExperimentalSerializationApi"
	}

	buildFeatures {
		viewBinding = true
	}
}

dependencies {
//    api("androidx.appcompat:appcompat:1.3.1")
//    api("androidx.constraintlayout:constraintlayout:2.1.1")
	api("com.google.android.material:material:1.4.0")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

//    discord("com.discord:discord:${findProperty("discord_version")}")
	api(files("../.assets/pine.jar"))
	compileOnly(files("../.assets/com.reddit.frontpage-dex2jar.jar"))
}

tasks.dokkaHtml.configure {
	dokkaSourceSets {
		named("main") {
			noAndroidSdkLink.set(false)
			includeNonPublic.set(false)
		}
	}
}

tasks.dokkaJavadoc.configure {
	dokkaSourceSets {
		named("main") {
			noAndroidSdkLink.set(false)
			includeNonPublic.set(false)
		}
	}
}

afterEvaluate {
	publishing {
		publications {
			register(project.name, MavenPublication::class) {
				group = "com.github.RedditVanced"

				from(components["debug"])
			}
		}
	}
}
