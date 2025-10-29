package uk.co.bithatch.maven.flatpak.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Source {

	/* All */
	private List<String> onlyArches =  new ArrayList<>();
	private List<String> skipArches =  new ArrayList<>();
	private String dest;
	private String type;
	
	/* Common */
	private String path;
	private String url;
	private String md5;
	private String sha256;
	private String sha512;
	private String destFilename;

	/* Htto */
	private List<String> mirrorUrls =  new ArrayList<>();
	private String referer;
	private boolean disableHttpCompression;

	/* Archive */
	private boolean gitInit;
	private String archiveType;
	private int stripComponents;
	
	/* Git */
	private String branch;
	private String tag;
	private String commit;
	private boolean disableFsckobjects;
	private boolean disableShallowClone;
	private boolean disableSubmodules;
	private boolean disableLfs;
	
	/* Bzr, SVN */
	private String revision;
	
	/* Directory */
	private List<String> skip =  new ArrayList<>();
	
	/* Script,  Shell */
	private List<String> commands =  new ArrayList<>();
	
	/* Inline */
	private String contents;
	private boolean base64;
	
	/* Patch */
	private List<String> paths =  new ArrayList<>();
	private boolean useGit;
	private boolean useGitAm;
	private List<String> options =  new ArrayList<>();
	
	/* Extra Data */
	private String filename;
	private long size;
	private String installedSize;
	
	public Source() {
	}
	
	Source(String type, String path) {
		this.path = path;
		this.type = type;
	}

	public final String getDest() {
		return dest;
	}

	public final void setDest(String dest) {
		this.dest = dest;
	}

	public final String getType() {
		return type;
	}

	public final void setType(String type) {
		this.type = type;
	}

	@JsonProperty(required = false)
	public final String getPath() {
		return path;
	}

	public final void setPath(String path) {
		this.path = path;
	}

	@JsonProperty(required = false)
	public final String getUrl() {
		return url;
	}

	public final void setUrl(String url) {
		this.url = url;
	}

	@JsonProperty(required = false)
	public final String getSha256() {
		return sha256;
	}

	public final void setSha256(String sha256) {
		this.sha256 = sha256;
	}

	@JsonProperty(value = "only-arches")
	public final List<String> getOnlyArches() {
		return onlyArches;
	}

	@JsonProperty(value = "skip-arches")
	public final List<String> getSkipArches() {
		return skipArches;
	}

	@JsonProperty(value = "md5")
	public final String getMd5() {
		return md5;
	}

	public final void setMd5(String md5) {
		this.md5 = md5;
	}

	@JsonProperty(value = "sha512")
	public final String getSha512() {
		return sha512;
	}

	public final void setSha512(String sha512) {
		this.sha512 = sha512;
	}

	@JsonProperty(value = "dest-filename")
	public final String getDestFilename() {
		return destFilename;
	}

	public final void setDestFilename(String destFilename) {
		this.destFilename = destFilename;
	}

	@JsonProperty(value = "referer")
	public final String getReferer() {
		return referer;
	}

	public final void setReferer(String referer) {
		this.referer = referer;
	}

	@JsonProperty(value = "disable-http-compression", defaultValue = "false")
	public final boolean isDisableHttpCompression() {
		return disableHttpCompression;
	}

	public final void setDisableHttpCompression(boolean disableHttpCompression) {
		this.disableHttpCompression = disableHttpCompression;
	}

	@JsonProperty(value = "git-init", defaultValue = "false")
	public final boolean isGitInit() {
		return gitInit;
	}

	public final void setGitInit(boolean gitInit) {
		this.gitInit = gitInit;
	}

	@JsonProperty(value = "archive-type")
	public final String getArchiveType() {
		return archiveType;
	}

	public final void setArchiveType(String archiveType) {
		this.archiveType = archiveType;
	}

	@JsonProperty(value = "strip-components")
	public final int getStripComponents() {
		return stripComponents;
	}

	public final void setStripComponents(int stripComponents) {
		this.stripComponents = stripComponents;
	}

	@JsonProperty(value = "branch")
	public final String getBranch() {
		return branch;
	}

	public final void setBranch(String branch) {
		this.branch = branch;
	}

	@JsonProperty(value = "tag")
	public final String getTag() {
		return tag;
	}

	public final void setTag(String tag) {
		this.tag = tag;
	}

	@JsonProperty(value = "commit")
	public final String getCommit() {
		return commit;
	}

	public final void setCommit(String commit) {
		this.commit = commit;
	}

	@JsonProperty(value = "disable-fsckobjects", defaultValue = "false")
	public final boolean isDisableFsckobjects() {
		return disableFsckobjects;
	}

	public final void setDisableFsckobjects(boolean disableFsckobjects) {
		this.disableFsckobjects = disableFsckobjects;
	}

	@JsonProperty(value = "disable-shallow-clone", defaultValue = "false")
	public final boolean isDisableShallowClone() {
		return disableShallowClone;
	}

	public final void setDisableShallowClone(boolean disableShallowClone) {
		this.disableShallowClone = disableShallowClone;
	}

	@JsonProperty(value = "disable-submodules", defaultValue = "false")
	public final boolean isDisableSubmodules() {
		return disableSubmodules;
	}

	public final void setDisableSubmodules(boolean disableSubmodules) {
		this.disableSubmodules = disableSubmodules;
	}

	@JsonProperty(value = "disable-lfs", defaultValue = "false")
	public final boolean isDisableLfs() {
		return disableLfs;
	}

	public final void setDisableLfs(boolean disableLfs) {
		this.disableLfs = disableLfs;
	}

	@JsonProperty(value = "revision")
	public final String getRevision() {
		return revision;
	}

	public final void setRevision(String revision) {
		this.revision = revision;
	}

	@JsonProperty(value = "contents")
	public final String getContents() {
		return contents;
	}

	public final void setContents(String contents) {
		this.contents = contents;
	}

	@JsonProperty(value = "base64", defaultValue = "false")
	public final boolean isBase64() {
		return base64;
	}

	public final void setBase64(boolean base64) {
		this.base64 = base64;
	}

	@JsonProperty(value = "use-git", defaultValue = "false")
	public final boolean isUseGit() {
		return useGit;
	}

	public final void setUseGit(boolean useGit) {
		this.useGit = useGit;
	}

	@JsonProperty(value = "use-git-am", defaultValue = "false")
	public final boolean isUseGitAm() {
		return useGitAm;
	}

	public final void setUseGitAm(boolean useGitAm) {
		this.useGitAm = useGitAm;
	}

	@JsonProperty(value = "filename")
	public final String getFilename() {
		return filename;
	}

	public final void setFilename(String filename) {
		this.filename = filename;
	}

	@JsonProperty(value = "size")
	public final long getSize() {
		return size;
	}

	public final void setSize(long size) {
		this.size = size;
	}

	@JsonProperty(value = "installed-size")
	public final String getInstalledSize() {
		return installedSize;
	}

	public final void setInstalledSize(String installedSize) {
		this.installedSize = installedSize;
	}

	@JsonProperty(value = "mirror-urls")
	public final List<String> getMirrorUrls() {
		return mirrorUrls;
	}

	@JsonProperty(value = "skip")
	public final List<String> getSkip() {
		return skip;
	}

	@JsonProperty(value = "commands")
	public final List<String> getCommands() {
		return commands;
	}

	@JsonIgnore
	public final String getCommand() {
		return String.join(System.lineSeparator(), commands);
	}

	public final void setCommand(String command) {
		this.commands.clear();
		this.commands.addAll(
				Arrays.asList(command.split("\\r?\\n")).stream().map(s -> s.trim()).collect(Collectors.toList()));
	}

	@JsonProperty(value = "paths")
	public final List<String> getPaths() {
		return paths;
	}

	@JsonProperty(value = "options")
	public final List<String> getOptions() {
		return options;
	}
}
