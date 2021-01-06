import ru.mipt.npm.gradle.Maturity

plugins {
    id("ru.mipt.npm.mpp")
    id("ru.mipt.npm.native")
}

kotlin.sourceSets.commonMain {
    dependencies {
        api(project(":kmath-core"))
    }
}

readme {
    description = "Complex numbers and quaternions."
    maturity = Maturity.DEVELOPMENT
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        id = "complex",
        description = "Complex Numbers",
        ref = "src/commonMain/kotlin/kscience/kmath/complex/Complex.kt"
    )

    feature(
        id = "quaternion",
        description = "Quaternions",
        ref = "src/commonMain/kotlin/kscience/kmath/complex/Quaternion.kt"
    )
}
