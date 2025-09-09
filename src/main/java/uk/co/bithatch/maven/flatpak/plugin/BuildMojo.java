package uk.co.bithatch.maven.flatpak.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(threadSafe = true, name = "build", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, requiresProject = true)
public class BuildMojo extends AbstractMojo {

	@Parameter(defaultValue = "false")
	private boolean skip;

	@Parameter(required = true, readonly = true, property = "project")
	private MavenProject project;

	@Parameter(defaultValue = "${project.build.directory}/app", required = true)
	private File appDir;

	@Parameter
	private File manifestFile;

	@Parameter(defaultValue = "${project.build.directory}/flatpak-build", required = true)
	private File buildDirectory;

	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	private MavenSession session;

	@Parameter(defaultValue = "flatpak-builder", required = true)
	private String flatpakBuilderCommand;

	@Parameter(defaultValue = "true")
	private boolean forceClean;

	@Parameter(defaultValue = "false")
	private boolean install;

	@Parameter(defaultValue = "flathub", required = true)
	private String deps;

	@Parameter(defaultValue = "", property = "flatpak.appId")
	private String appId;


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
		
		args.add("--user");
		
		if(!deps.equals(""))
			args.add("--install-deps-from=" + deps);

		args.add("--repo=repo");
		
		if(install)
			args.add("--install");
		
		args.add(buildDirectory.getAbsolutePath());
		
		if(manifestFile == null) {
			args.add(appDir.getAbsolutePath() + File.separator + thisAppId + ".yml");
		}
		else {
			args.add(manifestFile.getAbsolutePath());
		}

		ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);
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


}
