package uk.co.bithatch.maven.flatpak.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;

@Mojo(threadSafe = true, name = "generate", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, requiresProject = true)
public class FlatpakMojo extends AbstractCreateMojo {

	@Parameter
	private List<String> excludeArtifacts;

	@Parameter(required = true, readonly = true, property = "project")
	private MavenProject project;

	@Parameter()
	private boolean includeProject = true;

	@Parameter
	private String[] launcherPreCommands;

	@Parameter
	private String[] launcherPostCommands;

	@Parameter(required = true)
	private String mainClass;

	@Parameter(defaultValue = "${maven.compiler.source}")
	private int javaSdkExtensionVersion;

	@Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true, required = true)
	private List<RemoteRepository> repositories;

	@Parameter(defaultValue = "${repositorySystemSession}", readonly = true, required = true)
	private RepositorySystemSession repoSession;

	@Parameter(defaultValue = "true")
	private boolean remotesFromOriginalSource;

	@Parameter(defaultValue = "false")
	private boolean ignoreSnapshotRemotes;

	@Parameter(defaultValue = "false")
	private boolean verifyRemotes;

	@Parameter(defaultValue = "true", property = "modules")
	private boolean modules = true;

	@Parameter(defaultValue = "true", property = "classpath")
	private boolean classpath = true;

	@Parameter(defaultValue = "false")
	private boolean includeVersion;

	@Parameter(defaultValue = "false")
	private boolean attachedArtifacts = true;

	@Parameter
	private List<String> vmArgs;

	@Component
	private RepositorySystem repoSystem;

	/**
	 * List of system modules (overrides automatic detection of type)
	 */
	@Parameter
	private List<String> systemModules;
	/**
	 * List of classpath jars (overrides automatic detection of type)
	 */
	@Parameter
	private List<String> classpathArtifacts;
	/**
	 * List of automatic modules
	 */
	@Parameter
	private List<String> automaticArtifacts;

	@Parameter
	private boolean mainArtificateIsModule;
	
	@Parameter
	private String sdk;

	@Parameter
	private File splashFile;

	@Parameter(defaultValue = "splash")
	private String splashName;

	@Override
	protected void addManifestDefaults() {
		super.addManifestDefaults();
		addSdkExtensionModule();
	}

	@Override
	protected void addInitial() {
		addExtensions();
	}

	@Override
	protected void addOther() throws MojoExecutionException, NoSuchAlgorithmException, IOException, URISyntaxException {

		List<String> classPaths = new ArrayList<>();
		List<String> modulePaths = new ArrayList<>();


		for (Artifact a : project.getArtifacts()) {
			doArtifact(appModule, a, classPaths, modulePaths);
		}
		if (attachedArtifacts) {
			for (Artifact a : project.getAttachedArtifacts()) {
				mainArtificateIsModule |= doArtifact(appModule, a, classPaths, modulePaths);
			}
		}
		if (includeProject)
			mainArtificateIsModule |= doArtifact(appModule, project.getArtifact(), classPaths, modulePaths);

		addLauncher(appModule, classPaths, modulePaths, mainArtificateIsModule);
		
		addSplash(appModule);
		
		super.addOther();
	}

	private void addLauncher(Module appModule, List<String> classPaths, List<String> modulePaths,
			boolean mainArtificateIsModule)
			throws StreamWriteException, DatabindException, IOException, FileNotFoundException {
		appModule.getBuildCommands().add(formatInstall(appModule, manifest.getCommand(), "/app/bin"));
		appModule.getSources().add(new Source("file", manifest.getCommand()));
		try (OutputStream out = new FileOutputStream(new File(appDirectory, manifest.getCommand()))) {
			writeLauncher(manifest, new OutputStreamWriter(out), classPaths, modulePaths, mainArtificateIsModule);
		}
	}

	private void addExtensions() {
		boolean hasJdkExtension = false;
		for (String sdkExtension : manifest.getSdkExtensions()) {
			if (sdkExtension.contains("openjdk")) {
				hasJdkExtension = true;
			}
		}
		if (!hasJdkExtension) {
			if (javaSdkExtensionVersion < 12) {
				manifest.getSdkExtensions().add("org.freedesktop.Sdk.Extension.openjdk11");
			} else if (javaSdkExtensionVersion < 22) {
				manifest.getSdkExtensions().add("org.freedesktop.Sdk.Extension.openjdk17");
			} else if (javaSdkExtensionVersion < 26) {
				manifest.getSdkExtensions().add("org.freedesktop.Sdk.Extension.openjdk25");
			} else{
				manifest.getSdkExtensions().add("org.freedesktop.Sdk.Extension.openjdk21");
			}
		}
	}

	private void addSdkExtensionModule() {
		Module sdkExtensionModule = manifest.getModule("openjdk");
		if (sdkExtensionModule == null) {
			String jdkName = "openjdk";
			for (String ext : manifest.getSdkExtensions()) {
				if (ext.contains("openjdk")) {
					int idx = ext.lastIndexOf('.');
					jdkName = idx == -1 ? ext : ext.substring(idx + 1);
				}
			}
			sdkExtensionModule = new Module();
			sdkExtensionModule.setBuildSystem("simple");
			sdkExtensionModule.setName("openjdk");
			sdkExtensionModule.getBuildCommands().add("/usr/lib/sdk/" + jdkName + "/install.sh");
			manifest.getModules().add(0, sdkExtensionModule);
		}
	}

	private void writeLauncher(Manifest manifest, Writer writer, List<String> classPaths, List<String> modulePaths,
			boolean mainArtificateIsModule) throws StreamWriteException, DatabindException, IOException {
		try (PrintWriter pw = new PrintWriter(writer, true)) {
			pw.println("#!/bin/bash");
			if(launcherPreCommands != null) {
				for(String s : launcherPreCommands) {
					pw.println(s);
				}
			}
			StringBuilder execLine = new StringBuilder("/app/jre/bin/java ");
			List<String> vmopts = new ArrayList<>();
			scriptArgs(vmopts, classPaths, modulePaths);
			execLine.append(String.join(" ", vmopts));
			execLine.append(" ");
			if (mainArtificateIsModule) {
				execLine.append("-m ");
			}
			execLine.append(mainClass);
			pw.println(execLine.toString());
			if(launcherPostCommands != null) {
				for(String s : launcherPostCommands) {
					pw.println(s);
				}
			}
		}
	}

	private void addSplash(Module appModule) throws IOException {
		if (splashFile == null) {
			List<File> icons = getImageFiles(flatpakDataDirectory);
			if (icons.size() > 0) {
				for (File f : icons) {
					if (f.getName().startsWith(splashName + ".")) {
						splashFile = f;
						break;
					}
				}
				if (splashFile == null) {
					splashFile = icons.get(0);
				}
			}
		}
		if (splashFile != null) {
			String ext = getExtension(iconFile);
			String splashIconFile = manifest.getAppId() + "." + ext;
			copy("Splashfile", splashFile, new File(appDirectory, splashIconFile), splashFile.lastModified());
			appModule.getBuildCommands().add(formatInstall(appModule, splashIconFile,
					"/app/share/pixmaps/" + manifest.getAppId() + ".splash." + getExtension(splashFile) + "/apps"));
			appModule.getSources().add(new Source("file", splashIconFile));
		}
	}

	private void scriptArgs(List<String> vmopts, List<String> classPaths, List<String> modulePaths) {

		if (splashFile != null) {
			vmopts.add("-splash:" + manifest.getAppId() + ".splash." + getExtension(splashFile));
		}
		if (!modulePaths.isEmpty()) {
			vmopts.add("-p");
			vmopts.add(String.join(File.pathSeparator,
					modulePaths.stream().map(s -> "/app/share/" + s).collect(Collectors.toList())));
		}
		if (!classPaths.isEmpty()) {
			vmopts.add("-cp");
			vmopts.add(String.join(File.pathSeparator,
					classPaths.stream().map(s -> "/app/share/" + s).collect(Collectors.toList())));
		}

		if (systemModules != null && systemModules.size() > 0) {
			vmopts.add("--add-modules");
			vmopts.add(String.join(",", systemModules));
		}
		if (vmArgs != null) {
			for (String vmArg : vmArgs) {
				vmopts.add(vmArg);
			}
		}
	}

	private boolean doArtifact(Module appModule, Artifact a, List<String> classPaths, List<String> modulePaths)
			throws MojoExecutionException, IOException, URISyntaxException, NoSuchAlgorithmException {

		getLog().debug(String.format("Processing %s", a.getFile().getName()));

		String artifactId = a.getArtifactId();
		org.eclipse.aether.artifact.Artifact aetherArtifact = new DefaultArtifact(a.getGroupId(), a.getArtifactId(),
				a.getClassifier(), a.getType(), a.getVersion());

		ArtifactResult resolutionResult = resolveRemoteArtifact(new HashSet<MavenProject>(), project, aetherArtifact,
				this.repositories);
		if (resolutionResult == null)
			throw new MojoExecutionException("Artifact " + aetherArtifact.getGroupId() + ":"
					+ aetherArtifact.getArtifactId() + " could not be resolved.");

		aetherArtifact = resolutionResult.getArtifact();

		if (containsArtifact(excludeArtifacts, aetherArtifact)) {

			getLog().info("Artifact " + artifactId + " is explicitly excluded.");
			return false;
		}

		File file = aetherArtifact.getFile();
		if (file == null || !file.exists()) {
			getLog().warn("Artifact " + artifactId
					+ " has no attached file. Its content will not be copied in the target model directory.");
			return false;
		}

		install(appModule, a, resolutionResult, file);
		if (isModule(aetherArtifact)) {
			modulePaths.add(getFileName(aetherArtifact));
			return true;
		} else {
			classPaths.add(getFileName(aetherArtifact));
			return false;
		}
	}

	private void install(Module appModule, Artifact a, ArtifactResult resolutionResult, File file)
			throws IOException, URISyntaxException, NoSuchAlgorithmException {
		String entryPath = getFileName(a);
		getLog().info(String.format("Adding %s", a.getFile().getName()));
		String remoteUrl = validateUrl(mavenUrl(resolutionResult));
		Source entry = new Source();
		if (remotesFromOriginalSource) {
			if (isRemote(remoteUrl)) {
				entry.setType("file");
				entry.setUrl(remoteUrl);
				entry.setSha256(getFileChecksum(MessageDigest.getInstance("SHA-256"), a.getFile()));
				appModule.getBuildCommands().add(formatInstall(appModule, getBasePath(remoteUrl),  entryPath, "/app/share"));
			} else {
				copy("Jar from Maven", file, new File(appDirectory, entryPath), file.lastModified());
				entry.setType("file");
				entry.setPath(entryPath);
				appModule.getBuildCommands().add(formatInstall(appModule, entryPath, "/app/share"));
			}
		} else {
			entry.setType("file");
			entry.setPath(entryPath);
			copy("Jar from Local", a.getFile(), new File(appDirectory, entryPath), file.lastModified());
			appModule.getBuildCommands().add(formatInstall(appModule, entryPath, "/app/share"));
		}
		appModule.getSources().add(entry);
	}

	private String getFileName(Artifact a) {
		return getFileName(a.getArtifactId(), a.getVersion(), a.getClassifier(), a.getType());
	}

	private String mavenUrl(String base, String groupId, String artifactId, String baseVersion, String version,
			String classifier) {
		StringBuilder builder = new StringBuilder();
		builder.append(base + '/');
		builder.append(groupId.replace('.', '/') + "/");
		builder.append(artifactId + "/");
		builder.append(baseVersion + "/");
		builder.append(artifactId + "-" + version);

		if (classifier != null && classifier.length() > 0) {
			builder.append('-' + classifier);
		}

		builder.append(".jar");

		return builder.toString();
	}

	private String validateUrl(String url) {
		if (url == null) {
			return url;
		} else {
			try {
				URL u = new URL(url);
				URLConnection conx = u.openConnection();
				conx.getInputStream().close();
				return url;
			} catch (Exception e) {
				getLog().warn(
						MessageFormat.format("{0} will use local copy as remote failed verification check.", url));
				return null;
			}
		}
	}

	private String mavenUrl(ArtifactResult result) {
		if (result.getArtifact().isSnapshot() && ignoreSnapshotRemotes) {
			return null;
		}

		org.eclipse.aether.repository.ArtifactRepository repo = result.getRepository();
		MavenProject project = this.project;
		if (project != null) {
			String url = mavenUrlForProject(result, repo, project);
			if (url != null)
				return url;
		}
		while (project != null) {
			List<MavenProject> collectedProjects = project.getCollectedProjects();
			if (collectedProjects != null) {
				for (MavenProject p : collectedProjects) {
					String url = mavenUrlForProject(result, repo, p);
					if (url != null)
						return url;
				}
			}
			project = project.getParent();
		}
		return null;
	}

	private String mavenUrlForProject(ArtifactResult result, org.eclipse.aether.repository.ArtifactRepository repo,
			MavenProject p) {
		for (RemoteRepository r : p.getRemoteProjectRepositories()) {
			if (r.getId().equals(repo.getId())) {
				String url = r.getUrl();
				return mavenUrl(url, result.getArtifact().getGroupId(), result.getArtifact().getArtifactId(),
						result.getArtifact().getBaseVersion(), result.getArtifact().getVersion(),
						result.getArtifact().getClassifier());
			}
		}
		return null;
	}

	private String getFileName(org.eclipse.aether.artifact.Artifact a) {
		return getFileName(a.getArtifactId(), a.getVersion(), a.getClassifier(), a.getExtension());
	}

	private String getFileName(String artifactId, String version, String classifier, String type) {
		StringBuilder fn = new StringBuilder();
		fn.append(artifactId);
		if (includeVersion) {
			fn.append("-");
			fn.append(version);
		}
		if (classifier != null && classifier.length() > 0) {
			fn.append("-");
			fn.append(classifier);
		}
		fn.append(".");
		fn.append(type);
		return fn.toString();
	}

	private boolean containsArtifact(Collection<String> artifactNames, org.eclipse.aether.artifact.Artifact artifact) {
		if (artifactNames == null)
			return false;
		String k = artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getClassifier();
		if (artifactNames.contains(k))
			return true;
		k = artifact.getGroupId() + ":" + artifact.getArtifactId();
		if (artifactNames.contains(k))
			return true;
		k = artifact.getArtifactId();
		if (artifactNames.contains(k))
			return true;
		k = artifact.getGroupId();
		if (artifactNames.contains(k))
			return true;
		return false;
	}

	private boolean isModule(org.eclipse.aether.artifact.Artifact a) throws IOException {
		if (!modules)
			return false;
		
		if(!classpath)
			return true;

		if (automaticArtifacts != null && containsArtifact(new LinkedHashSet<>(automaticArtifacts), a))
			return true;

		if (classpathArtifacts != null && containsArtifact(new LinkedHashSet<>(classpathArtifacts), a))
			return false;

		/* Detect */
		return isModuleJar(a);
	}

	private boolean isModuleJar(org.eclipse.aether.artifact.Artifact a) throws IOException {
		File file = a.getFile();
		if (file == null) {
			getLog().warn(String.format("%s has a null file?", a));
		} else {
			if ("jar".equals(a.getExtension())) {
				return isModuleJar(file);
			}
		}
		return false;
	}

	private boolean isModuleJar(File file) throws IOException {
		try (JarFile jarFile = new JarFile(file)) {
			Enumeration<JarEntry> enumOfJar = jarFile.entries();
			java.util.jar.Manifest mf = jarFile.getManifest();
			if (mf != null) {
				if (mf.getMainAttributes().getValue("Automatic-Module-Name") != null)
					return true;
			}
			while (enumOfJar.hasMoreElements()) {
				JarEntry entry = enumOfJar.nextElement();
				if (entry.getName().equals("module-info.class")
						|| entry.getName().matches("META-INF/versions/.*/module-info.class")) {
					return true;
				}
			}
		}
		return false;
	}

	private ArtifactResult resolveRemoteArtifact(Set<MavenProject> visitedProjects, MavenProject project,
			org.eclipse.aether.artifact.Artifact aetherArtifact, List<RemoteRepository> repos)
			throws MojoExecutionException {
		ArtifactRequest req = new ArtifactRequest().setRepositories(repos).setArtifact(aetherArtifact);
		ArtifactResult resolutionResult = null;
		visitedProjects.add(project);
		try {
			resolutionResult = this.repoSystem.resolveArtifact(this.repoSession, req);

		} catch (ArtifactResolutionException e) {
			if (project.getParent() == null) {
				/* Reached the root (reactor), now look in child module repositories too */
				for (MavenProject p : session.getAllProjects()) {
					if (!visitedProjects.contains(p)) {
						try {
							resolutionResult = resolveRemoteArtifact(visitedProjects, p, aetherArtifact,
									p.getRemoteProjectRepositories());
							if (resolutionResult != null)
								break;
						} catch (MojoExecutionException mee) {
						}
					}
				}
			} else if (!visitedProjects.contains(project.getParent()))
				return resolveRemoteArtifact(visitedProjects, project.getParent(), aetherArtifact,
						project.getParent().getRemoteProjectRepositories());
		}
		return resolutionResult;
	}

	private boolean isRemote(String path) {
		return path != null && (path.startsWith("http:") || path.startsWith("https:"));
	}

	private static String getFileChecksum(MessageDigest digest, File file) throws IOException {
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] byteArray = new byte[1024];
			int bytesCount = 0;
			while ((bytesCount = fis.read(byteArray)) != -1) {
				digest.update(byteArray, 0, bytesCount);
			}
			;
		}
		byte[] bytes = digest.digest();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	private String getExtension(File file) {
		String n = file.getName().toLowerCase();
		int idx = n.lastIndexOf('.');
		return idx == -1 ? n : n.substring(idx + 1);
	}

	private String getBasePath(String path) {
		int idx = path.lastIndexOf('/');
		return idx == -1 ? path : path.substring(idx + 1);
	}
}
