package varviewer.server.variant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import varviewer.shared.Variant;

/**
 * An in-memory collection of variants
 * @author brendan
 *
 */
public class VariantCollection {

	protected Map<String, List<Variant>>  vars = new HashMap<String, List<Variant>>();
	
	/**
	 * Build a new variant pool from the given list of variants
	 * @param varList
	 */
	public VariantCollection(List<Variant> varList) {
		for(Variant v : varList) {
			List<Variant> contig = vars.get(v.getChrom());
			if (contig == null) {
				contig = new ArrayList<Variant>(2048);
				vars.put(v.getChrom(), contig);
			}
			contig.add(v);
		}
	}
	
	public VariantCollection(VCFReader reader) throws IOException {
		int lineNumber = 0;
		do {
			Variant rec = reader.toVariant();
			if (rec == null) {
				System.err.println("Warning, could not import variant from line: " + reader.getCurrentLine() );
			}
			else {
				this.addRecordNoSort(rec);
			}
			lineNumber++;
		} while (reader.advanceLine());
		sortAllContigs();
	}
	
	/**
	 * Sort all variant records in each contig
	 */
	public void sortAllContigs() {
		for(String contig : getContigs()) {
			List<Variant> records = getVariantsForContig(contig);
			Collections.sort(records);
		}
	}
	
	/**
	 * Add a new record to the pool but do not sort the contig it was added to. This is 
	 * faster if you're adding lots of variants (from a VCFFile, for instance), but
	 * requires that all contigs are sorted 
	 * @param rec
	 */
	public void addRecordNoSort(Variant rec) {
		List<Variant> contigVars = vars.get( rec.getChrom() ); 
		if (contigVars == null) {
			contigVars = new ArrayList<Variant>(2048);
			vars.put(rec.getChrom(), contigVars);
		}
		contigVars.add(rec);
	}
	
	public int getContigCount() {
		return vars.size();
	}

	public Collection<String> getContigs() {
		return vars.keySet();
	}
	
	/**
	 * Returns a reference to the list of variants in the given contig, modifications
	 * to the list will modify this collection
	 * @param contig
	 * @return
	 */
	public List<Variant> getVariantsForContig(String contig) {
		List<Variant> varList = vars.get(contig);
		if (varList != null)
			return varList;
		else 
			return new ArrayList<Variant>();
	}
}