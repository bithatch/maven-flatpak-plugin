package uk.co.bithatch.maven.flatpak.plugin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Extension {
	private String directory;
	private boolean bundle;
	private boolean removeAfterBuild;

	@JsonProperty(value = "directory", index = 0)
	public final String getDirectory() {
		return directory;
	}

	public final void setDirectory(String directory) {
		this.directory = directory;
	}

	@JsonProperty(value = "bundle", index = 1, defaultValue = "false")
	public final boolean isBundle() {
		return bundle;
	}

	public final void setBundle(boolean bundle) {
		this.bundle = bundle;
	}

	@JsonProperty(value = "remove-after-build", index = 2, defaultValue = "false")
	public final boolean isRemoveAfterBuild() {
		return removeAfterBuild;
	}

	public final void setRemoveAfterBuild(boolean removeAfterBuild) {
		this.removeAfterBuild = removeAfterBuild;
	}

}
