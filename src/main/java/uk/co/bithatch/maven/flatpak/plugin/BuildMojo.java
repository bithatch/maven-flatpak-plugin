package uk.co.bithatch.maven.flatpak.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class BuildMojo extends AbstractFlatpakMojo {

	@Parameter(defaultValue = "${project.build.directory}/app", required = true)
	private File appDir;

	@Parameter
	private File manifestFile;

	@Parameter(defaultValue = "${project.build.directory}/flatpak-build", required = true)
	private File buildDirectory;

	@Parameter(defaultValue = "${project.build.directory}/flatpak-builder", required = true)
	private File stateDirectory;

	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	private MavenSession session;

	@Parameter(defaultValue = "flatpak-builder", required = true)
	private String flatpakBuilderCommand;

	@Parameter(defaultValue = "true")
	private boolean forceClean;

	@Parameter(defaultValue = "flathub", required = true)
	private String deps;

	@Parameter(defaultValue = "", property = "flatpak.appId")
	private String appId;

	@Parameter
	private File repo;

	@Override
	public void execute() throws MojoExecutionException {
		if (skip) {
			getLog().info("Skipping plugin execution");
			return;
		}

		String thisAppId = appId == null || appId.equals("") 
					? FlatpakMojo.normalisePackage(project.getGroupId()) + "." + FlatpakMojo.normalizeName(project.getArtifactId())
					: appId;

		
		List<String> args = new ArrayList<String>();
		args.add(flatpakBuilderCommand);
		
		if(forceClean)
			args.add("--force-clean");
		
		if(!deps.equals(""))
			args.add("--install-deps-from=" + deps);

		if(repo == null) {
			args.add("--repo=" + getDefaultRepo().getAbsolutePath());
		}
		else {
			args.add("--repo=" + repo.getAbsolutePath());
		}

		if(stateDirectory == null ) {
			args.add("--state-dir=" + System.getProperty("user.home") + File.separator + ".cache" + File.separator + "flatpak-builder-state");
		}
		else {
			args.add("--state-dir=" + stateDirectory);
		}
		buildArgs(args);
		
		args.add(buildDirectory.getAbsolutePath());
		
		if(manifestFile == null) {
			args.add(appDir.getAbsolutePath() + File.separator + thisAppId + ".yml");
		}
		else {
			args.add(manifestFile.getAbsolutePath());
		}

		ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);
		pb.directory(project.getBasedir());
		try {
			Process process = pb.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			try {
				String line;
				while(( line = in.readLine()) != null) {
					getLog().info(line);
				}
			}
			finally {
				in.close();
			}
		}
		catch(IOException ioe) {
			throw new MojoExecutionException("Failed to run Flatpak Builder command.", ioe);
		}
	}

	protected abstract File getDefaultRepo();

	protected abstract void buildArgs(List<String> args); 

	@Override
	protected boolean isIgnoreSnapshotRemotes() {
		throw new IllegalStateException();
	}


	@Override
	protected boolean isIncludeVersion() {
		throw new IllegalStateException();
	}

	@Override
	protected File getSourcesFile() {
		throw new IllegalStateException();
	}

}
