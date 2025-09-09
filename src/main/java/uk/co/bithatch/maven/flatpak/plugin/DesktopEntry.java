package uk.co.bithatch.maven.flatpak.plugin;

import java.util.LinkedHashMap;
import java.util.Map;

public class DesktopEntry {
	private String type;
	private String name;
	private Map<String, String> names = new LinkedHashMap<>();
	private String exec;
	private String icon;
	private Map<String, String> icons = new LinkedHashMap<>();
	private String comment;
	private Map<String, String> comments = new LinkedHashMap<>();
	private boolean ignore;
	private String categories;

	public final boolean isIgnore() {
		return ignore;
	}

	public final void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}

	public final String getType() {
		return type;
	}

	public final void setType(String type) {
		this.type = type;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getExec() {
		return exec;
	}

	public final void setExec(String exec) {
		this.exec = exec;
	}

	public final String getIcon() {
		return icon;
	}

	public final void setIcon(String icon) {
		this.icon = icon;
	}

	public final String getComment() {
		return comment;
	}

	public final void setComment(String comment) {
		this.comment = comment;
	}

	public final Map<String, String> getNames() {
		return names;
	}

	public final Map<String, String> getIcons() {
		return icons;
	}

	public final Map<String, String> getComments() {
		return comments;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

}
