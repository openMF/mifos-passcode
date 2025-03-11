/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
plugins {
    alias(libs.plugins.mifospay.cmp.feature)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.mifos.library.passcode"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose)

            implementation(libs.jb.kotlin.stdlib)
            implementation(libs.kotlin.reflect)

            api(libs.protobuf.kotlin.lite)
            implementation(libs.kotlinx.serialization.core)

            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.serialization)
            implementation(libs.multiplatform.settings.coroutines)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.core)

            implementation(project(":core:ui"))
            implementation(project(":core:designsystem"))
            implementation(project(":core:data"))

            implementation(libs.findLibrary("jb.composeRuntime").get())
            implementation(libs.findLibrary("jb.composeViewmodel").get())
            implementation(libs.findLibrary("jb.lifecycleViewmodel").get())
            implementation(libs.findLibrary("jb.lifecycleViewmodelSavedState").get())
            implementation(libs.findLibrary("jb.savedstate").get())
            implementation(libs.findLibrary("jb.bundle").get())
            implementation(libs.findLibrary("jb.composeNavigation").get())
            implementation(libs.findLibrary("kotlinx.collections.immutable").get())
        }

        androidMain.dependencies {
            implementation(libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
            implementation(libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
            implementation(libs.findLibrary("androidx.tracing.ktx").get())

            implementation(platform(libs.findLibrary("koin-bom").get()))
            implementation(libs.findLibrary("koin-android").get())
            implementation(libs.findLibrary("koin.androidx.compose").get())
            implementation(libs.findLibrary("koin.android").get())
            implementation(libs.findLibrary("koin.androidx.navigation").get())
            implementation(libs.findLibrary("koin.androidx.compose").get())
            implementation(libs.findLibrary("koin.core.viewmodel").get())
        }

        androidInstrumentedTest.dependencies {
            implementation(libs.findLibrary("androidx.navigation.testing").get())
            implementation(libs.findLibrary("androidx.compose.ui.test").get())
            implementation(libs.findLibrary("androidx.lifecycle.runtimeTesting").get())
        }

        androidTest.dependencies {
            implementation(libs.findLibrary("koin.test.junit4").get())
        }

        desktopMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

// Setup protobuf configuration, generating lite Java and Kotlin classes
protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}