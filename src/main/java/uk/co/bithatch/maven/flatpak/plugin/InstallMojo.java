package uk.co.bithatch.maven.flatpak.plugin;

import java.io.File;
import java.util.List;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(threadSafe = true, name = "install", defaultPhase = LifecyclePhase.INSTALL, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, requiresProject = true)
public class InstallMojo extends BuildMojo {

	@Parameter(defaultValue = "true")
	private boolean user;

	@Override
	protected void buildArgs(List<String> args) {
		if (user) {
			args.add("--user");
		}
		args.add("--install");		
	}

	@Override
	protected File getDefaultRepo() {
		return new File(System.getProperty("user.home") + File.separator + ".m2" + File.separator + "flatpak-repo");
	}

}
