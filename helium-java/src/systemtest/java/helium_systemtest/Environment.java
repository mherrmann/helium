package helium_systemtest;

import java.io.File;

import static com.heliumhq.api_impl.application_context.DevelopmentAPIConfig.
		getHeliumFile;

public class Environment {
	public static String getTargetDir() {
		return getHeliumFile("helium-java/target");
	}
	public static String getTestDistDir() {
		return getHeliumFile("helium-java/target/test-dist");
	}
	public static String getTestDistFile(String relPath) {
		return getHeliumFile("helium-java/target/test-dist/" + relPath);
	}
	public static String getSystemtestResource(String relPath) {
		String result =
			getHeliumFile("helium-java/src/systemtest/resources/" + relPath);
		if (new File(result).exists())
			return result;
		return getHeliumFile("src/systemtest/resources/" + relPath);
	}
}
