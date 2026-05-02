package uk.co.bithatch.maven.flatpak.plugin;

import java.io.File;
import java.util.List;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(threadSafe = true, name = "run", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, requiresProject = true)
public class RunMojo extends BuildMojo {

	@Override
	protected void buildArgs(List<String> args) {
		args.add("--run");		
	}

	@Override
	protected File getDefaultRepo() {
		return new File(System.getProperty("user.home") + File.separator + ".m2" + File.separator + "flatpak-repo");
	}

}
