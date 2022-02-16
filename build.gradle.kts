plugins {
    `kotlin-dsl`
    id("org.web3j") version ("4.8.7")
}

buildscript {

    extra["minSdkVersion"] = Configs.minSdkVersion
    extra["compileSdkVersion"] = Configs.compileSdkVersion
    extra["targetSdkVersion"] = Configs.targetSdkVersion.toString()

    repositories {
        mavenCentral()
        maven(url = "https://www.jitpack.io")
        jcenter()
        google()
    }

    dependencies {
        classpath(ProjectRootLibraries.classpathGradle)
        classpath(ProjectRootLibraries.classpathKotlinGradle)
        classpath(ProjectRootLibraries.classpathDaggerHiltVersion)
        classpath(ProjectRootLibraries.classPathGoogleService)
        classpath(ProjectRootLibraries.classPathFirebasePerfs)
        classpath(ProjectRootLibraries.classpathCrashlytics)
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven(url = "https://www.jitpack.io")
        jcenter()
        google()
    }
}

node {
    version.set("14.16.0")
    npmVersion.set("7.8.0")
    download.set(true)
}