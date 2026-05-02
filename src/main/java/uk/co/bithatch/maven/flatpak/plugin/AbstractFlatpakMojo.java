package uk.co.bithatch.maven.flatpak.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractFlatpakMojo extends AbstractMojo {

	@Parameter(defaultValue = "false")
	protected boolean skip;
	
	@Parameter(required = true, readonly = true, property = "project")
	protected MavenProject project;

	@Parameter(defaultValue = "${repositorySystemSession}", readonly = true, required = true)
	private RepositorySystemSession repoSession;

	@Component
	private RepositorySystem repoSystem;

	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	protected MavenSession session;

	public AbstractFlatpakMojo() {
		super();
	}

	protected final void writeSources(Module module) throws MojoExecutionException {
		ObjectMapper mapper = new ObjectMapper();
		try(PrintWriter wtr = new PrintWriter(new FileWriter(getSourcesFile()), true)) {
			wtr.println(mapper.writeValueAsString(module.getSources()));
		} catch(IOException urise) {
			throw new MojoExecutionException("Failed to generate flatpak sources.", urise);
		}
	}

	protected final String formatInstall(Module module, String entryPath, String dir) {
		return formatInstall(module, entryPath, entryPath, dir);
	}

	protected final String formatInstall(Module module, String sourcePath, String entryPath, String dir) {
		return String.format("install -D %s %s/%s", sourcePath, dir, entryPath);
	}

	protected final static String getExtension(File file) {
		String n = file.getName().toLowerCase();
		int idx = n.lastIndexOf('.');
		return idx == -1 ? n : n.substring(idx + 1);
	}

	protected final static String getBasePath(String path) {
		int idx = path.lastIndexOf('/');
		return idx == -1 ? path : path.substring(idx + 1);
	}
	
	protected final void addSource(boolean remotesFromOriginalSource, File appDirectory, Module appModule, Artifact a, ArtifactResult resolutionResult, File file)
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

	protected final ArtifactResult resolveRemoteArtifact(Set<MavenProject> visitedProjects, MavenProject project,
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

	protected final String mavenUrl(String base, String groupId, String artifactId, String baseVersion, String version,
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

	protected final String mavenUrl(ArtifactResult result) {
		if (result.getArtifact().isSnapshot() && isIgnoreSnapshotRemotes()) {
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

	protected abstract boolean isIgnoreSnapshotRemotes();

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

	protected final boolean isRemote(String path) {
		return path != null && (path.startsWith("http:") || path.startsWith("https:"));
	}

	protected final String validateUrl(String url) {
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

	protected final String getFileName(Artifact a) {
		return getFileName(a.getArtifactId(), a.getVersion(), a.getClassifier(), a.getType());
	}

	protected final String getFileName(org.eclipse.aether.artifact.Artifact a) {
		return getFileName(a.getArtifactId(), a.getVersion(), a.getClassifier(), a.getExtension());
	}

	protected final String getFileName(String artifactId, String version, String classifier, String type) {
		StringBuilder fn = new StringBuilder();
		fn.append(artifactId);
		if (isIncludeVersion()) {
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

	protected abstract boolean isIncludeVersion();

	protected abstract File getSourcesFile();


	protected final boolean containsArtifact(Collection<String> artifactNames, org.eclipse.aether.artifact.Artifact artifact) {
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

	protected final boolean isModuleJar(org.eclipse.aether.artifact.Artifact a) throws IOException {
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

	protected final boolean isModuleJar(File file) throws IOException {
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

	protected static String getFileChecksum(MessageDigest digest, File file) throws IOException {
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
}