package uk.co.bithatch.maven.flatpak.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BuildOptions {
	private boolean strip = true;
	private boolean noDebuginfo;
	private boolean noDebuginfoCompression;
	private String cflags;
	private boolean cflagsOverride;
	private String cppflags;
	private boolean cppflagsOverride;
	private String cxxFlags;
	private boolean cxxFlagsOverride;
	private String ldflags;
	private boolean ldFlagsOverride;
	private String prefix;
	private String libdir;
	private String appendPath;
	private String prependPath;
	private String appendLdLibraryPath;
	private String prependLdLibraryPath;
	private String appendPkgConfigPath;
	private String prependPkgConfigPath;
	private List<String> secretEnv = new ArrayList<>();
	private List<String> buildArgs = new ArrayList<>();
	private List<String> testArgs = new ArrayList<>();
	private List<String> configOpts = new ArrayList<>();
	private List<String> secretOpts = new ArrayList<>();
	private List<String> makeArgs = new ArrayList<>();
	private List<String> makeInstallArgs = new ArrayList<>();
	private Map<String, BuildOptions> arch = new HashMap<>();

	@JsonProperty(value = "strip", index = 1000, defaultValue = "true")
	public final boolean isStrip() {
		return strip;
	}

	@JsonProperty(value = "no-debuginfo", index = 1001, defaultValue="false")
	public final boolean isNoDebuginfo() {
		return noDebuginfo;
	}

	public final void setNoDebuginfo(boolean noDebugInfo) {
		this.noDebuginfo = noDebugInfo;
	}

	public final void setStrip(boolean strip) {
		this.strip = strip;
	}

	@JsonProperty(value = "no-debuginfo-compression", index = 1002, defaultValue="false")
	public final boolean isNoDebuginfoCompression() {
		return noDebuginfoCompression;
	}

	public final void setNoDebuginfoCompression(boolean noDebuginfoCompression) {
		this.noDebuginfoCompression = noDebuginfoCompression;
	}

	@JsonProperty(value = "cflags", index = 1003)
	public final String getCflags() {
		return cflags;
	}

	public final void setCflags(String cflags) {
		this.cflags = cflags;
	}

	@JsonProperty(value = "cflags-override", index = 1004, defaultValue = "false")
	public final boolean isCflagsOverride() {
		return cflagsOverride;
	}

	public final void setCflagsOverride(boolean cflagsOverride) {
		this.cflagsOverride = cflagsOverride;
	}

	@JsonProperty(value = "cppflags", index = 1005)
	public final String getCppflags() {
		return cppflags;
	}

	public final void setCppflags(String cppflags) {
		this.cppflags = cppflags;
	}

	@JsonProperty(value = "cppflags-override", index = 1006, defaultValue = "false")
	public final boolean isCppflagsOverride() {
		return cppflagsOverride;
	}

	public final void setCppflagsOverride(boolean cppflagsOverride) {
		this.cppflagsOverride = cppflagsOverride;
	}

	@JsonProperty(value = "cxxflags-override", index = 1007)
	public final String getCxxFlags() {
		return cxxFlags;
	}

	public final void setCxxFlags(String cxxFlags) {
		this.cxxFlags = cxxFlags;
	}

	@JsonProperty(value = "cxxflags-override", index = 1008, defaultValue = "false")
	public final boolean isCxxFlagsOverride() {
		return cxxFlagsOverride;
	}

	public final void setCxxFlagsOverride(boolean cxxFlagsOverride) {
		this.cxxFlagsOverride = cxxFlagsOverride;
	}

	@JsonProperty(value = "ldflags", index = 1009, defaultValue = "false")
	public final String getLdflags() {
		return ldflags;
	}

	public final void setLdflags(String ldflags) {
		this.ldflags = ldflags;
	}

	@JsonProperty(value = "ldflags-override", index = 1010, defaultValue = "false")
	public final boolean isLdFlagsOverride() {
		return ldFlagsOverride;
	}

	public final void setLdFlagsOverride(boolean ldFlagsOverride) {
		this.ldFlagsOverride = ldFlagsOverride;
	}

	@JsonProperty(value = "prefix", index = 1011)
	public final String getPrefix() {
		return prefix;
	}

	public final void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@JsonProperty(value = "libdir", index = 1012)
	public final String getLibdir() {
		return libdir;
	}

	public final void setLibdir(String libdir) {
		this.libdir = libdir;
	}

	@JsonProperty(value = "append-path", index = 1013)
	public final String getAppendPath() {
		return appendPath;
	}

	public final void setAppendPath(String appendPath) {
		this.appendPath = appendPath;
	}

	@JsonProperty(value = "prepend-path", index = 1014)
	public final String getPrependPath() {
		return prependPath;
	}

	public final void setPrependPath(String prependPath) {
		this.prependPath = prependPath;
	}

	@JsonProperty(value = "append-library-path", index = 1015)
	public final String getAppendLdLibraryPath() {
		return appendLdLibraryPath;
	}

	public final void setAppendLdLibraryPath(String appendLdLibraryPath) {
		this.appendLdLibraryPath = appendLdLibraryPath;
	}

	@JsonProperty(value = "prepend-lb-library-path", index = 1016)
	public final String getPrependLdLibraryPath() {
		return prependLdLibraryPath;
	}

	public final void setPrependLdLibraryPath(String prependLdLibraryPath) {
		this.prependLdLibraryPath = prependLdLibraryPath;
	}

	@JsonProperty(value = "append-pkg-config-path", index = 1017)
	public final String getAppendPkgConfigPath() {
		return appendPkgConfigPath;
	}

	public final void setAppendPkgConfigPath(String appendPkgConfigPath) {
		this.appendPkgConfigPath = appendPkgConfigPath;
	}

	@JsonProperty(value = "prepend-pkg-config-path", index = 1018)
	public final String getPrependPkgConfigPath() {
		return prependPkgConfigPath;
	}

	public final void setPrependPkgConfigPath(String prependPkgConfigPath) {
		this.prependPkgConfigPath = prependPkgConfigPath;
	}

	@JsonProperty(value = "secret-env", index = 1019)
	public final List<String> getSecretEnv() {
		return secretEnv;
	}

	@JsonProperty(value = "build-args", index = 1020)
	public final List<String> getBuildArgs() {
		return buildArgs;
	}

	@JsonProperty(value = "test-args", index = 1021)
	public final List<String> getTestArgs() {
		return testArgs;
	}

	@JsonProperty(value = "config-opts", index = 1022)
	public final List<String> getConfigOpts() {
		return configOpts;
	}

	@JsonProperty(value = "secret-opts", index = 1022)
	public final List<String> getSecretOpts() {
		return secretOpts;
	}

	@JsonProperty(value = "make-args", index = 1023)
	public final List<String> getMakeArgs() {
		return makeArgs;
	}

	@JsonProperty(value = "make-install-args", index = 1024)
	public final List<String> getMakeInstallArgs() {
		return makeInstallArgs;
	}

	@JsonProperty(value = "arch", index = 1025)
	public final Map<String, BuildOptions> getArch() {
		return arch;
	}
}
