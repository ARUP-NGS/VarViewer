package varviewer.server.variant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.broad.tribble.readers.TabixReader;

import varviewer.server.AbstractVariantServer;
import varviewer.shared.Interval;
import varviewer.shared.IntervalList;
import varviewer.shared.Variant;
import varviewer.shared.VariantRequest;

public class UncompressedCSVReader extends AbstractVariantReader {
	
	public UncompressedCSVReader(String path) throws IOException {
		super(new File(path));
	}

	/**
	 * Returns all variants in a VariantCollection
	 * @return
	 * @throws IOException 
	 */
	public VariantCollection toVariantCollection() throws IOException {
		List<Variant> vars = new ArrayList<Variant>(1024);
		BufferedReader reader = new BufferedReader( new FileReader(varFile));
		String line = reader.readLine();
		initializeHeader(line);
		while(line != null) {
			Variant var = variantFromString(line);
			if (var != null)
				vars.add(var);
			line = reader.readLine();
		}
		reader.close();
		return new VariantCollection(vars);
	}
	

}
