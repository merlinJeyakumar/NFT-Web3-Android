apply {
    plugin("com.github.node-gradle.node")
}

plugins {
    `kotlin-dsl`
    id("org.web3j") version ("4.8.8")
    id("com.github.node-gradle.node") version ("3.1.1")
    id("org.web3j.solidity") version ("0.3.2")
}

buildscript {

    extra["minSdkVersion"] = Configs.minSdkVersion
    extra["compileSdkVersion"] = Configs.compileSdkVersion
    extra["targetSdkVersion"] = Configs.targetSdkVersion.toString()

    repositories {
        gradlePluginPortal()
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
        classpath("com.github.node-gradle:gradle-node-plugin:3.1.1")
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
    download.set(false)
    nodeModulesDir.set(File("node_modules/"))
    distBaseUrl.set("http://nodejs.org/dist")
    npmWorkDir.set(nodeModulesDir.get())
    workDir.set(nodeModulesDir.get())
}

sourceSets {
    main {
        solidity {

        }
    }
    test {
        solidity {

        }
    }
}

