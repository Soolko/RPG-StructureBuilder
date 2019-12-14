package rpg.sdk.structurebuilder;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class ResourceManager
{
	public File resourceDirectory;
	
	public List<File> listFiles() throws IOException
	{
		List<Path> found = new ArrayList<Path>();
		Files.walk(Paths.get(resourceDirectory.getPath())).filter(Files::isRegularFile).forEach(found::add);
		
		List<File> files = new ArrayList<File>();
		for(Path p : found) files.add(p.toFile());
		
		return files;
	}
	
	// Colour definitions
	public static final Color hoveredColour = Color.cyan;
	public static final Color selectedColour = Color.red;
}
