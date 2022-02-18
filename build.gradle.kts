buildscript {
	repositories {
		google()
		mavenCentral()
		maven("https://redditvanced.ddns.net/maven/releases")
		maven("https://jitpack.io")
	}
	dependencies {
		classpath("com.android.tools.build:gradle:7.0.4")
		classpath("com.github.redditvanced:gradle:1.0.3")
	}
}

allprojects {
    repositories {
        google()
        mavenCentral()
		maven("https://redditvanced.ddns.net/maven/releases")
        maven("https://jitpack.io")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
