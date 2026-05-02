package uk.co.bithatch.maven.flatpak.plugin;

import java.io.File;
import java.util.List;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(threadSafe = true, name = "export", defaultPhase = LifecyclePhase.PACKAGE,  requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, requiresProject = true)
public class ExportMojo extends BuildMojo {

	@Parameter(defaultValue = "${project.build.directory}/flatpak-build", required = true)
	private File repo;

	@Override
	protected void buildArgs(List<String> args) {
	}

	@Override
	protected File getDefaultRepo() {
		return repo;
	}

}
