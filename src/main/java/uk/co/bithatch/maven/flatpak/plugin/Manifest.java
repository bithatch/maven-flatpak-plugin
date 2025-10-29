package uk.co.bithatch.maven.flatpak.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Manifest {

	private String appId;
	private String runtime;
	private String runtimeVersion;
	private String sdk;
	private List<String> sdkExtensions = new ArrayList<>();
	private String command;
	private List<Module> modules = new ArrayList<>();
	private List<String> finishArgs = new ArrayList<>();
	private String branch;
	private String defaultBranch;
	private String collectionId;
	private String extensionTag;
	private int tokenType;
	private String var;
	private String metadata;
	private boolean buildRuntime;
	private boolean buildExtension;
	private boolean separateLocales = true;
	private String idPlatform;
	private String metadataPlatform;
	private boolean writableSdk = false;
	private boolean appstreamCompose = true;
	private List<String> platformExtensions = new ArrayList<>();
	private String base;
	private String baseVersion;
	private List<String> baseExtensions = new ArrayList<>();
	private List<String> inheritExtensions = new ArrayList<>();
	private List<String> inheritSdkExtensions = new ArrayList<>();
	private List<String> tags = new ArrayList<>();
	private BuildOptions buildOptions;
	private Map<String, Extension> addExtensions;
	private List<String> cleanup = new ArrayList<>();
	private List<String> cleanupCommands = new ArrayList<>();
	private List<String> cleanupPlatform = new ArrayList<>();
	private List<String> cleanupPlatformCommands = new ArrayList<>();
	private List<String> preparePlatformCommands = new ArrayList<>();
	private List<String> finishCommands = new ArrayList<>();
	private String renameDesktopFile;
	private String renameAppdataFile;
	private String renameMimeFile;
	private String renameIcon;
	private List<String> renameMimeIcons = new ArrayList<>();
	private String appdataLicense;
	private boolean copyIcon;
	private String desktopFileNamePrefix;
	private String desktopFileNameSuffix;

	@JsonProperty(value = "add-extensions", index = 42)
	public final Map<String, Extension> getAddExtensions() {
		return addExtensions;
	}

	@JsonProperty(value = "appdata-license", index = 41)
	public final String getAppdataLicense() {
		return appdataLicense;
	}

	public final void setAppdataLicense(String appdataLicense) {
		this.appdataLicense = appdataLicense;
	}

	@JsonProperty(value = "rename-mime-icons", index = 40)
	public final List<String> getRenameMimeIcons() {
		return renameMimeIcons;
	}

	@JsonProperty(value = "rename-desktop-file", index = 39)
	public final String getRenameDesktopFile() {
		return renameDesktopFile;
	}

	public final void setRenameDesktopFile(String renameDesktopFile) {
		this.renameDesktopFile = renameDesktopFile;
	}

	@JsonProperty(value = "rename-appdata-file", index = 38)
	public final String getRenameAppdataFile() {
		return renameAppdataFile;
	}

	public final void setRenameAppdataFile(String renameAppdataFile) {
		this.renameAppdataFile = renameAppdataFile;
	}

	@JsonProperty(value = "rename-mime-file", index = 37)
	public final String getRenameMimeFile() {
		return renameMimeFile;
	}

	public final void setRenameMimeFile(String renameMimeFile) {
		this.renameMimeFile = renameMimeFile;
	}

	@JsonProperty(value = "rename-icon", index = 36)
	public final String getRenameIcon() {
		return renameIcon;
	}

	public final void setRenameIcon(String renameIcon) {
		this.renameIcon = renameIcon;
	}

	@JsonProperty(value = "copy-icon", index = 35, defaultValue="false")
	public final boolean isCopyIcon() {
		return copyIcon;
	}

	public final void setCopyIcon(boolean copyIcon) {
		this.copyIcon = copyIcon;
	}

	@JsonProperty(value = "desktop-file-name-prefix", index = 35)
	public final String getDesktopFileNamePrefix() {
		return desktopFileNamePrefix;
	}

	public final void setDesktopFileNamePrefix(String desktopFileNamePrefix) {
		this.desktopFileNamePrefix = desktopFileNamePrefix;
	}

	@JsonProperty(value = "desktop-file-name-suffix", index = 34)
	public final String getDesktopFileNameSuffix() {
		return desktopFileNameSuffix;
	}

	public final void setDesktopFileNameSuffix(String desktopFileNameSuffix) {
		this.desktopFileNameSuffix = desktopFileNameSuffix;
	}

	@JsonProperty(value = "cleanup", index = 33)
	public final List<String> getCleanup() {
		return cleanup;
	}

	@JsonProperty(value = "cleanup-commands", index = 32)
	public final List<String> getCleanupCommands() {
		return cleanupCommands;
	}

	@JsonProperty(value = "cleanup-platform", index = 31)
	public final List<String> getCleanupPlatform() {
		return cleanupPlatform;
	}

	@JsonProperty(value = "cleanup-platform-commands", index = 30)
	public final List<String> getCleanupPlatformCommands() {
		return cleanupPlatformCommands;
	}

	@JsonProperty(value = "prepare-platform-commands", index = 29)
	public final List<String> getPreparePlatformCommands() {
		return preparePlatformCommands;
	}

	@JsonProperty(value = "finish-commands", index = 28)
	public final List<String> getFinishCommands() {
		return finishCommands;
	}

	@JsonProperty(value = "build-options", index = 27)
	public final BuildOptions getBuildOptions() {
		return buildOptions;
	}

	public final void setBuildOptions(BuildOptions buildOptions) {
		this.buildOptions = buildOptions;
	}

	@JsonProperty(value = "inherited-extensions", index = 26)
	public final List<String> getInheritExtensions() {
		return inheritExtensions;
	}

	@JsonProperty(value = "inherit-sdk-extensions", index = 25)
	public final List<String> getInheritSdkExtensions() {
		return inheritSdkExtensions;
	}

	@JsonProperty(value = "tags", index = 24)
	public final List<String> getTags() {
		return tags;
	}

	@JsonProperty(value = "writable-sdk", index = 18)
	public final boolean isWritableSdk() {
		return writableSdk;
	}

	public final void setWritableSdk(boolean writableSdk) {
		this.writableSdk = writableSdk;
	}

	@JsonProperty(value = "appstream-compose", index = 19, defaultValue="true")
	public final boolean isAppstreamCompose() {
		return appstreamCompose;
	}

	public final void setAppstreamCompose(boolean appstreamCompose) {
		this.appstreamCompose = appstreamCompose;
	}

	@JsonProperty(value = "base", index = 20)
	public final String getBase() {
		return base;
	}

	public final void setBase(String base) {
		this.base = base;
	}

	@JsonProperty(value = "base-version", index = 21)
	public final String getBaseVersion() {
		return baseVersion;
	}

	public final void setBaseVersion(String baseVersion) {
		this.baseVersion = baseVersion;
	}

	@JsonProperty(value = "platform-extensions", index = 22)
	public final List<String> getPlatformExtensions() {
		return platformExtensions;
	}

	@JsonProperty(value = "base-extensions", index = 23)
	public final List<String> getBaseExtensions() {
		return baseExtensions;
	}

	@JsonProperty(value = "metadata-platform", index = 17)
	public final String getMetadataPlatform() {
		return metadataPlatform;
	}

	public final void setMetadataPlatform(String metadataPlatform) {
		this.metadataPlatform = metadataPlatform;
	}

	@JsonProperty(value = "id-platform", index = 16)
	public final String getIdPlatform() {
		return idPlatform;
	}

	public final void setIdPlatform(String idPlatform) {
		this.idPlatform = idPlatform;
	}

	@JsonProperty(value = "separate-locales", index = 15, defaultValue = "true")
	public final boolean isSeparateLocales() {
		return separateLocales;
	}

	public final void setSeparateLocales(boolean separateLocales) {
		this.separateLocales = separateLocales;
	}

	@JsonProperty(value = "build-runtime", index = 14, defaultValue = "false")
	public final boolean getBuildRuntime() {
		return buildRuntime;
	}

	public final void setBuildRuntime(boolean buildRuntime) {
		this.buildRuntime = buildRuntime;
	}

	@JsonProperty(value = "build-extension", index = 13, defaultValue = "false")
	public final boolean isBuildExtension() {
		return buildExtension;
	}

	public final void setBuildExtension(boolean buildExtension) {
		this.buildExtension = buildExtension;
	}

	@JsonProperty(value = "metadata", index = 12)
	public final String getMetadata() {
		return metadata;
	}

	public final void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	@JsonProperty(value = "var", index = 12)
	public final String getVar() {
		return var;
	}

	public final void setVar(String var) {
		this.var = var;
	}

	@JsonProperty(value = "token-type", index = 11, defaultValue = "0")
	public final int getTokenType() {
		return tokenType;
	}

	public final void setTokenType(int tokenType) {
		this.tokenType = tokenType;
	}

	@JsonProperty(value = "collection-tag", index = 10)
	public final String getExtensionTag() {
		return extensionTag;
	}

	public final void setExtensionTag(String extensionTag) {
		this.extensionTag = extensionTag;
	}

	@JsonProperty(value = "collection-id", index = 9)
	public final String getCollectionId() {
		return collectionId;
	}

	public final void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

	@JsonProperty(value = "branch", index = 7)
	public final String getBranch() {
		return branch;
	}

	public final void setBranch(String branch) {
		this.branch = branch;
	}

	@JsonProperty(value = "default-branch", index = 8)
	public final String getDefaultBranch() {
		return defaultBranch;
	}

	public final void setDefaultBranch(String defaultBranch) {
		this.defaultBranch = defaultBranch;
	}

	@JsonProperty(value = "app-id", index = 0)
	public final String getAppId() {
		return appId;
	}

	public final void setAppId(String appId) {
		this.appId = appId;
	}

	@JsonProperty(value = "runtime", index= 1)
	public final String getRuntime() {
		return runtime;
	}

	public final void setRuntime(String runtime) {
		this.runtime = runtime;
	}

	@JsonProperty(value = "runtime-version", index= 2)
	public final String getRuntimeVersion() {
		return runtimeVersion;
	}

	public final void setRuntimeVersion(String runtimeVersion) {
		this.runtimeVersion = runtimeVersion;
	}

	@JsonProperty(value = "sdk", index= 3)
	public final String getSdk() {
		return sdk;
	}

	public final void setSdk(String sdk) {
		this.sdk = sdk;
	}

	@JsonProperty(value = "command", index = 5)
	public final String getCommand() {
		return command;
	}

	public final void setCommand(String command) {
		this.command = command;
	}

	@JsonProperty(value = "sdk-extensions", index = 4)
	public final List<String> getSdkExtensions() {
		return sdkExtensions;
	}

	@JsonProperty(value = "modules", index = 6)
	public final List<Module> getModules() {
		return modules;
	}

	public final List<String> getFinishArgs() {
		return finishArgs;
	}

	@JsonProperty(value = "finish-args", index = 999)
	public final void setFinishArgs(List<String> finishArgs) {
		this.finishArgs = finishArgs;
	}
	
	Module getModule(String name) {
		for(Module module : modules) {
			if(name.equals(module.getName())) {
				return module;
			}
		}
		return null;
	}
}
