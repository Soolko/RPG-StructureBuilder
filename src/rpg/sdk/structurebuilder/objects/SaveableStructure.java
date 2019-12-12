package rpg.sdk.structurebuilder.objects;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.yaml.snakeyaml.Yaml;

import rpg.world.structure.Structure;

public class SaveableStructure extends Structure
{
	public void save(File output) throws IOException
	{
		// Clear
		output.delete();
		output.createNewFile();
		
		String yaml = new Yaml().dump(this);
		try(FileWriter fw = new FileWriter(output)) { fw.write(yaml); }
	}
}
