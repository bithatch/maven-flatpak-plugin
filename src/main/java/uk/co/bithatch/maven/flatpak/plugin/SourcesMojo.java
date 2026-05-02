package uk.co.bithatch.maven.flatpak.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResult;

@Mojo(threadSafe = true, name = "sources", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, requiresProject = true)
public class SourcesMojo extends AbstractFlatpakMojo {

	@Parameter(defaultValue = "false")
	private boolean ignoreSnapshotRemotes;

	@Parameter(defaultValue = "false")
	private boolean includeVersion;

	@Parameter()
	private boolean includeProject = true;

	@Parameter(defaultValue = "false")
	private boolean attachedArtifacts = true;

	@Parameter
	private List<String> excludeArtifacts;

	@Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true, required = true)
	private List<RemoteRepository> repositories;

	@Parameter(defaultValue = "${project.build.directory}/app", required = true)
	private File appDirectory;

	@Parameter(defaultValue = "${project.build.directory}/app/flatpak-sources.json", required = true)
	private File sourcesFile;

	@Parameter(defaultValue = "false")
	private boolean remotesFromOriginalSource;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		appDirectory.mkdirs();
		sourcesFile.getParentFile().mkdirs();
		
		Module module = new Module();
		

		for (Artifact a : project.getArtifacts()) {
			doArtifact(module, a);
		}
		if (attachedArtifacts) {
			for (Artifact a : project.getAttachedArtifacts()) {
				doArtifact(module, a);
			}
		}
		if (includeProject)
			doArtifact(module, project.getArtifact());

		writeSources(module);
	}

	@Override
	protected File getSourcesFile() {
		return sourcesFile;
	}

	private void doArtifact(Module module, Artifact a) throws MojoExecutionException {
		
		getLog().debug(String.format("Processing %s", a));
		try {

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
				return;
			}
	
			File file = aetherArtifact.getFile();
			if (file == null || !file.exists()) {
				getLog().warn("Artifact " + artifactId
						+ " has no attached file. Its content will not be copied in the target directory.");
				return;
			}
			addSource(remotesFromOriginalSource, appDirectory, module,  a, resolutionResult, file);
			
		}
		catch(URISyntaxException urise) {
			throw new MojoExecutionException("Failed to generate flatpak sources.", urise);
		}
		catch(IOException urise) {
			throw new MojoExecutionException("Failed to generate flatpak sources.", urise);
		}
		catch(NoSuchAlgorithmException urise) {
			throw new MojoExecutionException("Failed to generate flatpak sources.", urise);
		}
	}

	@Override
	protected boolean isIgnoreSnapshotRemotes() {
		return ignoreSnapshotRemotes;
	}

	@Override
	protected boolean isIncludeVersion() {
		return includeVersion;
	}

}
