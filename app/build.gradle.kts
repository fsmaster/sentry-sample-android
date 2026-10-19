plugins {
    id("com.android.application")
    id("io.sentry.android.gradle")
}

android {
    namespace = "com.fsmaster.sentrysample"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.fsmaster.sentrysample"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        manifestPlaceholders["sentryDsn"] = providers.gradleProperty("sentryDsn").get()
    }

    buildTypes {
        release {
            // R8 renames classes to a, b, c... The ProGuard mapping uploaded by
            // the Sentry plugin is what turns them back into real names.
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            signingConfig = signingConfigs.getByName("debug") // sample only
        }
    }
}

sentry {
    org.set("fsmaster")
    projectName.set("sdk-android")
    authToken.set(providers.environmentVariable("SENTRY_AUTH_TOKEN").orElse(""))

    includeProguardMapping.set(true)
    autoUploadProguardMapping.set(providers.environmentVariable("SENTRY_AUTH_TOKEN").isPresent)
    // send source files too, so Sentry shows Kotlin code next to the frame
    includeSourceContext.set(providers.environmentVariable("SENTRY_AUTH_TOKEN").isPresent)
}
