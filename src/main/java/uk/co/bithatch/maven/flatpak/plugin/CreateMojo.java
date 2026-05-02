package uk.co.bithatch.maven.flatpak.plugin;

import java.io.File;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(threadSafe = true, name = "create", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, requiresProject = true)
public class CreateMojo extends AbstractCreateMojo {

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
