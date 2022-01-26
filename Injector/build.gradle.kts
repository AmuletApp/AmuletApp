plugins {
	id("com.android.library")
	kotlin("android") version "1.6.10"
	id("redditvanced")
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
			"-Xno-receiver-assertions"
	}

	buildFeatures {
		viewBinding = true
	}
}

dependencies {
	implementation(project(":Common"))
	implementation("com.beust:klaxon:5.5")
	implementation("com.github.Aliucord:pine:83f67b2cdb")

	val redditVersion: String by project
	redditApk("::$redditVersion")
}
