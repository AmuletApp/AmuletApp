plugins {
	id("com.android.library")
	kotlin("android") version "1.6.10"
	kotlin("plugin.serialization") version "1.6.10"
}

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
			"-Xno-receiver-assertions" +
			"-Xuse-experimental=kotlinx.serialization.ExperimentalSerializationApi"
	}

	buildFeatures {
		viewBinding = true
	}
}

dependencies {
//	discord("com.discord:discord:${findProperty("discord_version")}")
//	implementation("androidx.appcompat:appcompat:1.3.1")

	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
	implementation("com.github.Aliucord:pine:83f67b2cdb")
	compileOnly(files("../.assets/com.reddit.frontpage-dex2jar.jar"))
}
