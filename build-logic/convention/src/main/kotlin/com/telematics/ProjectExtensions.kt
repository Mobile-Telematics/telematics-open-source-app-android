/*
 * Copyright 2023 The Android Open Source Project
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.telematics

import com.android.build.api.dsl.BaseFlavor
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.LibraryDefaultConfig
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

// For flavors usage
fun BaseFlavor.buildConfigBoolean(name: String, value: Boolean) =
    buildConfigField("Boolean", name, value.toString())

fun BaseFlavor.buildConfigInteger(name: String, value: Int) =
    buildConfigField("int", name, value.toString())


fun BaseFlavor.buildConfigString(name: String, value: String) =
    buildConfigField("String", name, "\"$value\"")

// For build types usage
fun BuildType.buildConfigString(name: String, value: String) =
    buildConfigField("String", name, "\"$value\"")

fun BuildType.buildConfigInteger(name: String, value: Int) =
    buildConfigField("int", name, value.toString())

fun LibraryDefaultConfig.buildConfigString(name: String, value: String) =
    buildConfigField("String", name, "\"$value\"")

fun LibraryDefaultConfig.buildConfigInteger(name: String, value: Int) =
    buildConfigField("int", name, value.toString())

fun LibraryDefaultConfig.buildConfigLong(name: String, value: Long) =
    buildConfigField("long", name, value.toString())