import com.android.build.api.dsl.ApplicationExtension
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ktlint)
}

val environments = listOf("dev", "staging", "prod")

fun loadProperties(path: String): Properties = Properties().apply {
    val file = rootProject.file(path)
    if (file.exists()) file.inputStream().use(::load)
}

val localProperties = loadProperties("local.properties")

fun secret(key: String): String? = System.getenv(key)?.takeIf { it.isNotBlank() }
    ?: localProperties.getProperty(key)?.trim()?.takeIf { it.isNotEmpty() }

fun envConfig(name: String): Properties = loadProperties("config/env/$name.properties").also {
    require(!it.isEmpty) { "Missing config/env/$name.properties" }
}

val firebaseConfigured = environments.any { file("src/$it/google-services.json").exists() }
if (firebaseConfigured) {
    apply(plugin = "com.google.gms.google-services")
    apply(plugin = "com.google.firebase.crashlytics")
}

val releaseSigning = listOf("KEYSTORE_PATH", "KEYSTORE_PASSWORD", "KEY_ALIAS", "KEY_PASSWORD").associateWith(::secret)
val hasReleaseSigning = releaseSigning.values.all { it != null }

extensions.configure<ApplicationExtension> {
    namespace = "com.noshitechinc.restaurant"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.noshitechinc.restaurant"
        minSdk = 25
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(releaseSigning.getValue("KEYSTORE_PATH")!!)
                storePassword = releaseSigning.getValue("KEYSTORE_PASSWORD")
                keyAlias = releaseSigning.getValue("KEY_ALIAS")
                keyPassword = releaseSigning.getValue("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            isPseudoLocalesEnabled = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (hasReleaseSigning) signingConfig = signingConfigs.getByName("release")
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        environments.forEach { env ->
            create(env) {
                dimension = "environment"
                val config = envConfig(env)
                fun value(key: String): String =
                    requireNotNull(config.getProperty(key)) { "$key missing in config/env/$env.properties" }.trim()
                value("APPLICATION_ID_SUFFIX").takeIf { it.isNotEmpty() }?.let { applicationIdSuffix = it }
                resValue("string", "app_name", value("APP_NAME"))
                buildConfigField("String", "ENVIRONMENT", "\"$env\"")
                buildConfigField("String", "API_BASE_URL", "\"${value("API_BASE_URL")}\"")
                buildConfigField("String", "HTTP_LOG_LEVEL", "\"${value("HTTP_LOG_LEVEL")}\"")
                buildConfigField("boolean", "CRASH_REPORTING_ENABLED", value("CRASH_REPORTING_ENABLED"))
                buildConfigField("long", "CONNECT_TIMEOUT_SECONDS", "${value("CONNECT_TIMEOUT_SECONDS")}L")
                buildConfigField("long", "READ_TIMEOUT_SECONDS", "${value("READ_TIMEOUT_SECONDS")}L")
                manifestPlaceholders["crashlyticsCollectionEnabled"] = value("CRASH_REPORTING_ENABLED")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }

    lint {
        abortOnError = true
    }
}

ktlint {
    version.set(libs.versions.ktlint.get())
    android.set(true)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.timber)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)

    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.okhttp.mockwebserver)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
