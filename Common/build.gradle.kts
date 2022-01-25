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
			"-Xno-receiver-assertions" +
			"-Xuse-experimental=kotlinx.serialization.ExperimentalSerializationApi"
	}
}

dependencies {
	implementation("com.beust:klaxon:5.5")
	redditApk("::405543")
}
