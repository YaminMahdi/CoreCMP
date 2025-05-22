
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

//buildscript {
//    repositories {
//        mavenCentral()
//    }
//    dependencies {
////        classpath(libs.kotlin.gradle.plugin)
//        classpath(libs.buildkonfig.gradle.plugin)
//    }
//}
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
//    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.ksp)
}

kotlin {
    jvmToolchain(ProjectConfig.javaVersion.toString().toInt())
    compilerOptions {
        freeCompilerArgs.addAll("-Xcontext-receivers", "-Xwhen-guards", "-Xnon-local-break-continue")
    }
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
//            implementation(libs.androidx.material.icons.extended)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.bundles.koin)
//            implementation("io.insert-koin:koin-core:4.1.0-RC1") {
//                exclude(group = "io.insert-koin", module = "koin-core-annotations-jvm")
//            }
            api(libs.koin.annotations)
            implementation(libs.bundles.ktor)
            implementation(libs.bundles.coil)


            implementation(libs.material.icons.core)
            implementation(libs.material.icons.extended)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
//            implementation(libs.bundles.nav)

            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.serialization)
            if(ProjectConfig.IS_DEBUG)
                implementation(libs.ktor.monitor.logging)
            else
                implementation(libs.ktor.monitor.logging.no.op)
            implementation(libs.kotlinx.datetime)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
    // KSP Common sourceSet
    sourceSets.named("commonMain").configure {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }
}

buildkonfig {
    packageName = ProjectConfig.packageName
    defaultConfigs {
        buildConfigField(Type.STRING, "packageName", ProjectConfig.packageName, const = true)
        buildConfigField(Type.STRING, "versionName", ProjectConfig.versionName, const = true)
        buildConfigField(Type.INT, "versionCode", ProjectConfig.versionCode.toString(), const = true)
        buildConfigField(Type.BOOLEAN, "IS_DEBUG", ProjectConfig.IS_DEBUG.toString(), const = true)
        buildConfigField(
            Type.STRING, "BASE_URL",
            if (ProjectConfig.IS_DEBUG)
                ProjectConfig.BASE_URL_DEV
            else
                ProjectConfig.BASE_URL_LIVE,
            const = true
        )
    }
}

android {
    namespace = ProjectConfig.packageName
    compileSdk = ProjectConfig.compileSdk

    defaultConfig {
        applicationId = ProjectConfig.packageName
        minSdk = ProjectConfig.minSdk
        targetSdk = ProjectConfig.targetSdk
        versionCode = ProjectConfig.versionCode
        versionName = ProjectConfig.versionName
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    applicationVariants.all {
        outputs.filterIsInstance<com.android.build.gradle.internal.api.BaseVariantOutputImpl>()
            .forEach { output ->
                output.outputFileName = "${rootProject.name.replace(' ', '_')}_v"
                output.outputFileName += "$versionName-$name.apk"
            }
    }
}

compose.desktop {
    application {
        mainClass = "${ProjectConfig.packageName}.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = ProjectConfig.packageName
            packageVersion = ProjectConfig.versionName
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
}

configurations.all {
    exclude(group = "io.insert-koin", module = "koin-core-annotations-jvm")
}


// Trigger Common Metadata Generation from Native tasks
project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if(name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
