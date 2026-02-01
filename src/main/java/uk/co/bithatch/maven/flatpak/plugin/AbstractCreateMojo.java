package uk.co.bithatch.maven.flatpak.plugin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import edu.emory.mathcs.backport.java.util.Collections;

public abstract class AbstractCreateMojo extends AbstractMojo {

	protected static final String DEFAULT_SDK = "org.freedesktop.Sdk";
	protected static final String DEFAULT_RUNTIME = "25.08";
	protected static final String DEFAULT_CATEGORY = "Utility";

	@Parameter
	protected Manifest manifest;

	@Parameter(defaultValue = "${project.build.directory}/app", required = true)
	protected File appDirectory;

	@Parameter(defaultValue = "false")
	private boolean skip;

	@Parameter(required = true, readonly = true, property = "project")
	protected MavenProject project;

	@Parameter(defaultValue = "${basedir}/src/flatpak", required = true)
	protected File flatpakDataDirectory;

	@Parameter
	private File screenshotsDirectory;

	@Parameter
	private File thumbnailsDirectory;

	@Parameter(defaultValue = "${project.artifactId}", required = true)
	private String appModuleName;

	@Parameter(defaultValue = "icon")
	private String iconName;

	@Parameter
	protected File iconFile;

	@Parameter
	private String[] imageTypes = new String[] { "svg", "png", "gif", "jpg", "jpeg" };

	@Parameter
	private DesktopEntry desktopEntry;

	@Parameter
	private MetaInfo metaInfo;

	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	protected MavenSession session;

	@Parameter
	private String categories;

	@Parameter
	@Deprecated
	private String runtime;

	@Parameter
	@Deprecated
	private String runtimeVersion;

	@Parameter
	@Deprecated
	private String sdk;

	protected Module appModule;

	@Override
	public void execute() throws MojoExecutionException {
		if (skip) {
			getLog().info("Skipping plugin execution");
			return;
		}

		if (manifest == null) {
			manifest = new Manifest();
		}

		addManifestDefaults();

		appModule = manifest.getModule(appModuleName);
		if (appModule == null) {
			for(Module module : manifest.getModules()) {
				if(module.getName() == null) {
					appModule = module;
					break;
				}
			}
			if(appModule == null) {
				getLog().info(MessageFormat.format("No module in manifest for {0}, so generating one", appModuleName));
				appModule = new Module();
				manifest.getModules().add(appModule);
			}
			else {
				getLog().info(MessageFormat.format("Unnanemd module in manifest for {0}, so using that one", appModuleName));
			}
			appModule.setName(appModuleName);
		}
		if (appModule.getBuildSystem() == null || appModule.getBuildSystem().equals("")) {
			appModule.setBuildSystem("simple");
			getLog().info(MessageFormat.format("No build system in manifest for {0}, so using simple", appModuleName));
		}
		if (appModule.getName() == null || appModule.getName().equals("")) {
			getLog().info(MessageFormat.format("No app module name, so using {0}", manifest.getCommand()));
			appModule.setName(manifest.getCommand());
		}
		if (!"simple".equals(appModule.getBuildSystem())) {
			throw new UnsupportedOperationException("Build system is not 'simple'.");
		}

		addInitial();
		appDirectory.mkdirs();

		try {

			addIcon(appModule);
			addDesktopEntry(appModule);
			addMetaInfo(appModule);
			addFlatpakResource(appModule);
			addOther();

			try (OutputStream out = new FileOutputStream(new File(appDirectory, manifest.getAppId() + ".yml"))) {
				writeManifest(manifest, new OutputStreamWriter(out));
			}

			if (!desktopEntry.isIgnore()) {
				try (OutputStream out = new FileOutputStream(getDesktopEntryFile())) {
					writeDesktopEntry(out, desktopEntry);
				}
			}

			File metaInfoFile = getMetaInfoFile();
			try (Writer out = new FileWriter(metaInfoFile)) {
				writeMetaInfo(metaInfo, out);
			}
		} catch (IOException | NoSuchAlgorithmException | URISyntaxException e) {
			throw new MojoExecutionException("Failed to write manifiest.", e);
		}
	}

	protected void addInitial() {
	}

	protected void addOther() throws MojoExecutionException, NoSuchAlgorithmException, IOException, URISyntaxException {
//				DirectoryScanner scanner = new DirectoryScanner();
//				FileSet fileSet = install.getSource();
//				File base = (fileSet.getDirectory().equals("") ? project.getBasedir()
//						: new File(project.getBasedir(), fileSet.getDirectory())).getCanonicalFile()
//						.getAbsoluteFile();
//				scanner.setBasedir(base);
//				if (fileSet == null || fileSet.getIncludes() == null || fileSet.getIncludes().size() == 0) {
//					scanner.setIncludes(new String[] { "**/*" });
//				} else {
//					scanner.setIncludes((String[]) fileSet.getIncludes().toArray(new String[0]));
//				}
//				if (fileSet != null && fileSet.getExcludes() != null) {
//					scanner.setExcludes((String[]) fileSet.getExcludes().toArray(new String[0]));
//				}
//				scanner.scan();
//
//				File target = appDirectory;
//				if (!install.getTarget().equals("")) {
//					target = new File(target, install.getTarget());
//				}
//
//				for (String rel : scanner.getIncludedFiles()) {
//
//					File source = new File(base, rel);
//					copy("Install", source, new File(target, rel).getCanonicalFile(), source.lastModified());
//
//					if (install.getTarget().equals("")) {
//						appModule.getSources().add(new Source(rel));
//						appModule.getBuildCommands().add(formatInstall(appModule, rel, "/app"));
//					} else {
//						appModule.getSources().add(new Source(install.getTarget() + "/" + rel));
//						appModule.getBuildCommands().add(formatInstall(appModule, install.getTarget() + "/" + rel,
//								"/app/" + install.getTarget()));
//					}
//				}
	}

	@SuppressWarnings("unchecked")
	private List<File> getImageFiles(File dir) {
		if (!dir.exists())
			return Collections.emptyList();
		return Arrays.asList(dir.listFiles((d, n) -> {
			for (String ext : imageTypes) {
				if (n.toLowerCase().endsWith("." + ext)) {
					return true;
				}
			}
			return false;
		}));
	}

	protected List<File> getImageFiles(Module appModule, String type, File root) throws IOException {
		return getImageFiles(resolveFlatpakDataDir(type, root));
	}

	private void addIcon(Module appModule) throws IOException {
		if (iconFile == null) {
			List<File> icons = getImageFiles(appModule, "icons", null);
			if (icons.size() > 0) {
				for (File f : icons) {
					if (f.getName().startsWith(iconName + ".")) {
						iconFile = f;
						break;
					}
				}
				if (iconFile == null) {
					iconFile = icons.get(0);
				}
				getLog().info("Discovered icon " + iconFile);
			}
			else {
				getLog().warn("No icon specified, and none found in " + flatpakDataDirectory + " (of types " + String.join(", ", imageTypes) + ")");
			}
		}
		if (iconFile != null) {
			String ext = getExtension(iconFile);
			String appIconFile = manifest.getAppId() + "." + ext;
			copy("Icon file", iconFile, new File(appDirectory, appIconFile), iconFile.lastModified());
			appModule.getBuildCommands().add(formatInstall(appModule, appIconFile,
					"/app/share/icons/hicolor/" + getIconDirForTypeAndSize(iconFile) + "/apps"));
			appModule.getSources().add(new Source("file", appIconFile));
		}
	}

	private String getIconDirForTypeAndSize(File iconFile) {
		String ext = getExtension(iconFile);
		if (ext.equals("svg")) {
			return "scalable";
		}
		try {
			BufferedImage bim = ImageIO.read(iconFile);
			int size = Math.max(bim.getWidth(), bim.getHeight());
			for (int s : new int[] { 512, 256, 192, 128, 96, 72, 64, 48, 40, 36, 32, 28, 24, 22, 20, 16, 8 }) {
				if (size >= s) {
					return s + "x" + s;
				}
			}
		} catch (IOException e) {
		}
		return "256x256";
	}

	private void addFlatpakResource(Module appModule) throws FileNotFoundException, IOException {
		for (File f : getImageFiles(appModule, "screenshots", screenshotsDirectory)) {
			copy("Flatpak resource", f, new File(appDirectory, f.getName()), f.lastModified());
		}
		for (File f : getImageFiles(appModule, "thumbnails", thumbnailsDirectory)) {
			copy("Flatpak resource", f, new File(appDirectory, f.getName()), f.lastModified());
		}
	}

	private void addMetaInfo(Module appModule) throws FileNotFoundException, IOException {
		if (metaInfo == null) {
			metaInfo = new MetaInfo();
		}
		if (metaInfo.getType() == null || desktopEntry.getType().equals("")) {
			if (desktopEntry == null || desktopEntry.isIgnore()) {
				metaInfo.setType("console-application");
			} else {
				metaInfo.setType("desktop-application");
			}
		}
		if (metaInfo.getId() == null || metaInfo.getId().equals("")) {
			metaInfo.setId(manifest.getAppId());
		}
		if ((metaInfo.getName() == null || metaInfo.getName().equals("")) && project.getName() != null
				&& !project.getName().equals("")) {
			metaInfo.setName(project.getName());
		}
		String desc = project.getDescription();
		if ((metaInfo.getSummary() == null || metaInfo.getSummary().equals(""))) {
			if(desc != null && !desc.equals(""))
				metaInfo.setSummary(firstSentence(desc));
			else
				throw new IllegalArgumentException("A summary is required for metainfo. You should add a standard <description> tag to your POM, or a <summary> tag to the <metainfo> tag of the plugin configuration.");
		}
		if ((metaInfo.getDescription() == null || metaInfo.getDescription().equals(""))
				&& desc != null && !desc.equals("")) {
			metaInfo.setDescription("<p>" + desc + "</p>");
		}
		if ((metaInfo.getProjectLicense() == null || metaInfo.getProjectLicense().equals(""))
				&& !project.getLicenses().isEmpty()) {
			metaInfo.setProjectLicense(project.getLicenses().get(0).getName());
		}
		if ((metaInfo.getMetaDataLicense() == null || metaInfo.getMetaDataLicense().equals(""))) {
			metaInfo.setMetaDataLicense("FSFAP");
		}
		if (!metaInfo.getUrl().containsKey("homePage") && project.getUrl() != null) {
			metaInfo.getUrl().put("homepage", project.getUrl());
		}
		if (!metaInfo.getUrl().containsKey("vcs-browserPage") && project.getScm() != null
				&& project.getScm().getUrl() != null && !project.getScm().getUrl().equals("")) {
			metaInfo.getUrl().put("vcs-browser", project.getScm().getUrl());
		}
		if (!metaInfo.getUrl().containsKey("vcs-browserPage") && project.getIssueManagement() != null
				&& project.getIssueManagement().getUrl() != null && !project.getIssueManagement().getUrl().equals("")) {
			metaInfo.getUrl().put("bugtracker", project.getIssueManagement().getUrl());
		}
		if (!metaInfo.getUrl().containsKey("contact") && !project.getDevelopers().isEmpty()
				&& project.getDevelopers().get(0).getUrl() != null
				&& !project.getDevelopers().get(0).getUrl().equals("")) {
			metaInfo.getUrl().put("contact", project.getDevelopers().get(0).getUrl());
		}
		if ((metaInfo.getProjectGroup() == null || metaInfo.getProjectGroup().equals(""))
				&& project.getOrganization() != null && project.getOrganization().getName() != null
				&& !project.getOrganization().getName().equals("")) {
			metaInfo.setProjectGroup(project.getOrganization().getName());
		}

		if ((metaInfo.getDeveloperName() == null || metaInfo.getDeveloperName().equals(""))
				&& !project.getDevelopers().isEmpty()) {
			metaInfo.setDeveloperName(project.getDevelopers().get(0).getName());
		}

		File metaInfoFile = getMetaInfoFile();

		appModule.getBuildCommands().add(formatInstall(appModule, metaInfoFile.getName(), "/app/share/appdata"));
		appModule.getSources().add(new Source("file", metaInfoFile.getName()));
	}

	private String firstSentence(String description) {
		int idx = description.indexOf(". ");
		if (idx == -1) {
			idx = description.indexOf(".");
		}
		if (idx == -1) {
			return description;
		}
		return description.substring(0, idx);
	}

	private File getMetaInfoFile() {
		return new File(appDirectory, manifest.getAppId() + ".metainfo.xml");
	}

	private void addDesktopEntry(Module appModule) throws FileNotFoundException, IOException {
		if (desktopEntry == null) {
			desktopEntry = new DesktopEntry();
		}
		if (!desktopEntry.isIgnore()) {
			if (desktopEntry.getType() == null || desktopEntry.getType().equals("")) {
				desktopEntry.setType("Application");
			}
			if (desktopEntry.getName() == null || desktopEntry.getName().equals("")) {
				desktopEntry.setName(project.getName());
			}
			if (desktopEntry.getComment() == null || desktopEntry.getComment().equals("")) {
				desktopEntry.setComment(project.getDescription());
			}
			if (desktopEntry.getExec() == null || desktopEntry.getExec().equals("")) {
				desktopEntry.setExec(manifest.getCommand());
			}
			if ((desktopEntry.getIcon() == null || desktopEntry.getIcon().equals("")) && iconFile != null) {
				desktopEntry.setIcon(manifest.getAppId());
			}
			if (desktopEntry.getCategories() == null || desktopEntry.getCategories().isEmpty()) {
				if (categories == null || categories.equals("")) {
					desktopEntry.setCategories(DEFAULT_CATEGORY);
				} else {
					desktopEntry.setCategories(categories.trim());
				}
			}
			File desktopFile = getDesktopEntryFile();

			appModule.getBuildCommands()
					.add(formatInstall(appModule, desktopFile.getName(), "/app/share/applications"));
			appModule.getSources().add(new Source("file", desktopFile.getName()));
		}
	}

	private File getDesktopEntryFile() {
		return new File(appDirectory, manifest.getAppId() + ".desktop");
	}

	private void writeDesktopEntry(OutputStream out, DesktopEntry desktopEntry) {
		try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out))) {
			writer.println("[Desktop Entry]");
			writer.println(String.format("Type=%s", desktopEntry.getType()));
			writer.println(String.format("Name=%s", desktopEntry.getName()));
			for (Map.Entry<String, String> en : desktopEntry.getNames().entrySet()) {
				writer.println(String.format("Name[%s]=%s", en.getKey(), en.getValue()));
			}
			writer.println(String.format("Exec=%s", desktopEntry.getExec()));
			if (desktopEntry.getIcon() != null) {
				writer.println(String.format("Icon=%s", desktopEntry.getIcon()));
			}
			for (Map.Entry<String, String> en : desktopEntry.getIcons().entrySet()) {
				writer.println(String.format("Icon[%s]=%s", en.getKey(), en.getValue()));
			}
			if (desktopEntry.getComment() != null) {
				writer.println(String.format("Comment=%s", desktopEntry.getComment()));
			}
			for (Map.Entry<String, String> en : desktopEntry.getComments().entrySet()) {
				writer.println(String.format("Comment[%s]=%s", en.getKey(), en.getValue()));
			}
			writer.println(String.format("Categories=%s", desktopEntry.getCategories()));
		}
	}

	protected void addManifestDefaults() {
		if (manifest.getAppId() == null || manifest.getAppId().equals("")) {
			String genAppId = normalisePackage(project.getGroupId()) + "." + normalizeName(project.getArtifactId());
			getLog().info(MessageFormat.format("No app ID in manifest, so using generated {0}", genAppId));
			manifest.setAppId(genAppId);
		}

		if (manifest.getRuntime() == null || manifest.getRuntime().equals("")) {
			manifest.setRuntime("org.freedesktop.Platform");
		}

		if (runtime != null && !runtime.equals("")) {
			manifest.setRuntime(runtime);
		} else if (manifest.getRuntimeVersion() == null || manifest.getRuntimeVersion().equals("")) {
			manifest.setRuntimeVersion(calcRuntime());
		}

		if (sdk != null && !sdk.equals("")) {
			manifest.setRuntime(sdk);
		} else if (manifest.getSdk() == null || manifest.getSdk().equals("")) {
			manifest.setSdk(DEFAULT_SDK);
		}

		if (manifest.getCommand() == null || manifest.getCommand().equals("")) {
			if(desktopEntry == null || desktopEntry.getExec() == null) {
				manifest.setCommand(project.getArtifactId());
				getLog().info(MessageFormat.format("No command in manifest, and no desktop entry, so using artifact ID {0}", project.getArtifactId()));
			}
			else {
				manifest.setCommand(desktopEntry.getExec());
				getLog().info(MessageFormat.format("No command in manifest, so using desktop entry {0}", desktopEntry.getExec()));
			}
		}

		if (manifest.getFinishArgs().isEmpty()) {
			manifest.getFinishArgs().add("--socket=x11");
			manifest.getFinishArgs().add("--share=ipc");
			manifest.getFinishArgs().add("--share=network");
			manifest.getFinishArgs().add("--filesystem=home");
		}
	}

	protected String calcRuntime() {
		return DEFAULT_RUNTIME;
	}

	private void writeManifest(Manifest manifest, Writer writer)
			throws StreamWriteException, DatabindException, IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.writeValue(writer, manifest);
	}

	private void writeMetaInfo(MetaInfo metaInfo, Writer writer)
			throws StreamWriteException, DatabindException, IOException {
		XmlMapper mapper = new XmlMapper();
		new PrintWriter(writer, true).println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		mapper.writeValue(writer, metaInfo);
	}

	static String normalisePackage(String pkg) {
		return pkg.replace('-', '_');
	}

	static String normalizeName(String name) {
		StringBuilder b = new StringBuilder();
		char[] ch = name.toCharArray();
		boolean upperNext = true;
		for (char c : ch) {
			if (c == '.' || c == '-' || c == '_') {
				upperNext = true;
				continue;
			}
			if (upperNext) {
				b.append(Character.toUpperCase(c));
				upperNext = false;
			} else {
				b.append(c);
			}
		}
		return b.toString();
	}

	protected final String formatInstall(Module module, String entryPath, String dir) {
		return formatInstall(module, entryPath, entryPath, dir);
	}

	protected final String formatInstall(Module module, String sourcePath, String entryPath, String dir) {
		return String.format("install -D %s %s/%s", sourcePath, dir, entryPath);
	}

	protected void copy(String reason, File p1, File p2, long mod) throws IOException {
		getLog().debug(String.format("Copy %s - %s to %s", reason, p1.getAbsolutePath(), p2.getAbsolutePath()));
		File pp2 = p2.getParentFile();
		if (!pp2.exists() && !p2.getParentFile().mkdirs()) {
			throw new IOException("Could not create target directory " + pp2);
		}
		try (OutputStream out = new FileOutputStream(p2)) {
			Files.copy(p1.toPath(), out);
		}
		p2.setLastModified(mod);
	}

	private File resolveFlatpakDataDir(String type, File specific) {
		if (specific == null) {
			return new File(flatpakDataDirectory, type);
		} else
			return specific;
	}

	private String getExtension(File file) {
		String n = file.getName().toLowerCase();
		int idx = n.lastIndexOf('.');
		return idx == -1 ? n : n.substring(idx + 1);
	}

}
