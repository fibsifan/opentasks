fun gitVersion(): String {
    val stdout = java.io.ByteArrayOutputStream()
    exec {
        commandLine("git", "describe", "--tags", "--always", "--dirty")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

tasks.wrapper {
    gradleVersion = "7.5.1"
    distributionType = Wrapper.DistributionType.ALL
}

allprojects {
    version = gitVersion()
}

apply(from = "dependencies.gradle")
