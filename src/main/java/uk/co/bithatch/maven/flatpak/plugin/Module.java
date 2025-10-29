package uk.co.bithatch.maven.flatpak.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Module {

	private String name;
	private String buildSystem;
	private List<String> buildCommands = new ArrayList<>();
	private List<Source> sources = new ArrayList<>();
	private BuildOptions buildOptions;
	private List<String> secretEnv = new ArrayList<>();
	private List<String> configOpts = new ArrayList<>();
	private List<String> secretOpts = new ArrayList<>();
	private List<String> makeArgs = new ArrayList<>();
	private List<String> makeInstallArgs = new ArrayList<>();
	private boolean rmConfigure;
	private boolean noAutogen;
	private boolean noParallelMake;
	private String installRule;
	private boolean noMakeInstall;
	private boolean noPythonTimestampFix;
	private boolean cmake;
	private boolean builddir;
	private boolean subdir;
	private List<String> postInstall = new ArrayList<>();
	private List<String> cleanup = new ArrayList<>();
	private List<String> ensureWritable = new ArrayList<>();
	private List<String> onlyArches = new ArrayList<>();
	private List<String> skipArches = new ArrayList<>();
	private List<String> cleanupPlatform = new ArrayList<>();
	private boolean runTests;
	private String testRule;
	private List<String> testCommands = new ArrayList<>();
	private List<String> licenseFiles = new ArrayList<>();

	@JsonProperty(value = "name", index = 0)
	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	@JsonProperty(value = "buildsystem", index = 1)
	public final String getBuildSystem() {
		return buildSystem;
	}

	public final void setBuildSystem(String buildSystem) {
		this.buildSystem = buildSystem;
	}

	@JsonProperty(value = "build-commands", index = 2)
	public final List<String> getBuildCommands() {
		return buildCommands;
	}

	@JsonIgnore
	public final String getBuildCommand() {
		return String.join(System.lineSeparator(), buildCommands);
	}

	public final void setBuildCommand(String buildCommand) {
		this.buildCommands.clear();
		this.buildCommands.addAll(
				Arrays.asList(buildCommand.split("\\r?\\n")).stream().map(s -> s.trim()).collect(Collectors.toList()));
	}

	@JsonProperty(value = "sources", index = 3)
	public final List<Source> getSources() {
		return sources;
	}

	@JsonProperty(value = "build-options", index = 4)
	public final BuildOptions getBuildOptions() {
		return buildOptions;
	}

	public final void setBuildOptions(BuildOptions buildOptions) {
		this.buildOptions = buildOptions;
	}

	@JsonProperty(value = "rm-configure", index = 5, defaultValue = "false")
	public final boolean isRmConfigure() {
		return rmConfigure;
	}

	public final void setRmConfigure(boolean rmConfigure) {
		this.rmConfigure = rmConfigure;
	}

	@JsonProperty(value = "no-autogen", index = 6, defaultValue = "false")
	public final boolean isNoAutogen() {
		return noAutogen;
	}

	public final void setNoAutogen(boolean noAutogen) {
		this.noAutogen = noAutogen;
	}

	@JsonProperty(value = "no-parallel-make", index = 7, defaultValue = "false")
	public final boolean isNoParallelMake() {
		return noParallelMake;
	}

	public final void setNoParallelMake(boolean noParallelMake) {
		this.noParallelMake = noParallelMake;
	}

	@JsonProperty(value = "install-rule", index = 8)
	public final String getInstallRule() {
		return installRule;
	}

	public final void setInstallRule(String installRule) {
		this.installRule = installRule;
	}

	@JsonProperty(value = "no-make-install", index = 9, defaultValue = "false")
	public final boolean isNoMakeInstall() {
		return noMakeInstall;
	}

	public final void setNoMakeInstall(boolean noMakeInstall) {
		this.noMakeInstall = noMakeInstall;
	}

	@JsonProperty(value = "no-python-timestamp-fix", index = 10, defaultValue = "false")
	public final boolean isNoPythonTimestampFix() {
		return noPythonTimestampFix;
	}

	public final void setNoPythonTimestampFix(boolean noPythonTimestampFix) {
		this.noPythonTimestampFix = noPythonTimestampFix;
	}

	@JsonProperty(value = "cmake", index = 11, defaultValue = "false")
	public final boolean isCmake() {
		return cmake;
	}

	public final void setCmake(boolean cmake) {
		this.cmake = cmake;
	}

	@JsonProperty(value = "builddir", index = 12, defaultValue = "false")
	public final boolean isBuilddir() {
		return builddir;
	}

	public final void setBuilddir(boolean builddir) {
		this.builddir = builddir;
	}

	@JsonProperty(value = "subdir", index = 13, defaultValue = "false")
	public final boolean isSubdir() {
		return subdir;
	}

	public final void setSubdir(boolean subdir) {
		this.subdir = subdir;
	}

	@JsonProperty(value = "run-tests", index = 14, defaultValue = "false")
	public final boolean isRunTests() {
		return runTests;
	}

	public final void setRunTests(boolean runTests) {
		this.runTests = runTests;
	}

	@JsonProperty(value = "test-rule", index = 15)
	public final String getTestRule() {
		return testRule;
	}

	public final void setTestRule(String testRule) {
		this.testRule = testRule;
	}

	@JsonProperty(value = "secret-env", index = 16)
	public final List<String> getSecretEnv() {
		return secretEnv;
	}

	@JsonProperty(value = "config-opts", index = 17)
	public final List<String> getConfigOpts() {
		return configOpts;
	}

	@JsonProperty(value = "secret-opts", index = 18)
	public final List<String> getSecretOpts() {
		return secretOpts;
	}

	@JsonProperty(value = "make-args", index = 19)
	public final List<String> getMakeArgs() {
		return makeArgs;
	}

	@JsonProperty(value = "make-install-args", index = 20)
	public final List<String> getMakeInstallArgs() {
		return makeInstallArgs;
	}

	@JsonProperty(value = "post-install", index = 21)
	public final List<String> getPostInstall() {
		return postInstall;
	}

	@JsonProperty(value = "cleanup", index = 22)
	public final List<String> getCleanup() {
		return cleanup;
	}

	@JsonProperty(value = "ensure-writable", index = 23)
	public final List<String> getEnsureWritable() {
		return ensureWritable;
	}

	@JsonProperty(value = "only-arches", index = 24)
	public final List<String> getOnlyArches() {
		return onlyArches;
	}

	@JsonProperty(value = "skip-arches", index = 25)
	public final List<String> getSkipArches() {
		return skipArches;
	}

	@JsonProperty(value = "cleanup-platform", index = 26)
	public final List<String> getCleanupPlatform() {
		return cleanupPlatform;
	}

	@JsonProperty(value = "test-commands", index = 27)
	public final List<String> getTestCommands() {
		return testCommands;
	}

	@JsonIgnore
	public final String getTestCommand() {
		return String.join(System.lineSeparator(), testCommands);
	}

	public final void setTestCommand(String testCommand) {
		this.testCommands.clear();
		this.testCommands.addAll(
				Arrays.asList(testCommand.split("\\r?\\n")).stream().map(s -> s.trim()).collect(Collectors.toList()));
	}

	@JsonProperty(value = "license-files", index = 28)
	public final List<String> getLicenseFiles() {
		return licenseFiles;
	}
}
