fun gitVersion(): String {
    val stdout = java.io.ByteArrayOutputStream()
    exec {
        commandLine("git", "describe", "--tags", "--always", "--dirty")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

tasks.wrapper {
    gradleVersion = "7.6.6"
    distributionType = Wrapper.DistributionType.ALL
}

allprojects {
    version = gitVersion()
}
