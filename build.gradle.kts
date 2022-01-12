buildscript {
	repositories {
		google()
		mavenCentral()
		maven("https://jitpack.io")
	}
	dependencies {
		classpath("com.android.tools.build:gradle:7.0.4")
		classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.5.0")
	}
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
