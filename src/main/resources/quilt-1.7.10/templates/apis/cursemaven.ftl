repositories {
    exclusiveContent {
        forRepository {
            maven {
                url "https://cursemaven.com"
            }
        }
        filter {
            includeGroup "curse.maven"
        }
    }
}

dependencies {
    implementation fg.deobf("curse.maven:ctm-267602:2642375")
}