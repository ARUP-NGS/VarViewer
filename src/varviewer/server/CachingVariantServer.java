package varviewer.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import varviewer.server.variant.VariantCollection;
import varviewer.shared.PedigreeFilter;
import varviewer.shared.Variant;
import varviewer.shared.VariantFilter;
import varviewer.shared.VariantRequest;

/**
 * A VariantServer that maintains the most recently requested variants in memory
 * for faster response times. 
 * @author brendan
 *
 */
public class CachingVariantServer extends AbstractVariantServer {

	private String currentSampleID = null; //
	private VariantCollection currentVariants = null;
	private SampleSource source;
	
	public CachingVariantServer(SampleSource sampleSource) {
		this.source = sampleSource;
	}
	
	
	@Override
	public List<Variant> getVariants(VariantRequest req) {
		StringBuilder msg = new StringBuilder();
		for(String id : req.getSampleIDs()) {
			msg.append(id + ", ");
		}
		
		Logger.getLogger(CachingVariantServer.class).info("Processing request for variants for sample(s) " + msg);
		
		//No variants loaded, automatically attempt to load
		if (currentVariants == null) {
			Logger.getLogger(CachingVariantServer.class).info("No variant list currently loaded, reading new list from file");
			loadVariants(req.getSampleIDs());
		}
		
		//Check to see if current sample ID matches the loaded sample, if so filter and return the variants 
		if (currentSampleID != null && req.getSampleIDs().contains(currentSampleID)) {
			Logger.getLogger(CachingVariantServer.class).info("Returning variants for sample(s): " + msg);
			List<Variant> vars = currentVariants.getVariantsInIntervals(req.getIntervals());
			if (req.getFilters() != null && req.getFilters().size()>0) {
				vars = applyFilters(vars, req.getFilters());
			}
			return vars;
		}
		
		//Current variants is not null but sample IDs don't match, so try to load new variants
		loadVariants(req.getSampleIDs());
		
		//if variant load failed (bad sample id) then current variants may still be null
		if (currentVariants != null) {
			Logger.getLogger(CachingVariantServer.class).info("Returning variants for sample(s): " + msg);
			List<Variant> vars = currentVariants.getVariantsInIntervals(req.getIntervals());
			if (req.getFilters() != null && req.getFilters().size()>0) {
				vars = applyFilters(vars, req.getFilters());
			}
			return vars;
		}
		
		
		Logger.getLogger(CachingVariantServer.class).warn("Could not find or load variants for sample(s): " + msg);
		return null;		
	}

	private void loadVariants(List<String> ids) {
		try {
			//Force re-loading of sample info
			source.initialize();
		} catch (IOException e) {
			Logger.getLogger(CachingVariantServer.class).warn("IOError re-loading variants: " + e.getMessage());
			e.printStackTrace();
		}
		
		for(String id : ids) {
			currentVariants = source.getVariantsForSample(id);
			currentSampleID = id;
			return;
		}
	}
	
	/**
	 * Returns a new list of variants containing only those variants that pass ALL 
	 * filters in the list
	 * @param vars
	 * @param filters
	 * @return
	 */
	private List<Variant> applyFilters(List<Variant> vars, List<VariantFilter> filters) {
		List<Variant> passingVars = new ArrayList<Variant>(1024);
		
		//Kind of a hack here... pedigree-based filters need to be 'initialized' with a SampleSource
		//before they work, right now we do this here. 
		
		for(VariantFilter filter : filters) {
			if (filter instanceof PedigreeFilter) {
				PedigreeFilter pedFilter = (PedigreeFilter)filter;
				pedFilter.setVariantSource(source);
			}
		}
		
		for(Variant var : vars) {
			boolean passes = true;
			for(VariantFilter filter : filters) {
				if (! filter.variantPasses(var)) {
					passes = false;
					break;
				}
			}
			if (passes)
				passingVars.add(var);
		}
		
		//Similar hack here, PedigreeFilters also apply an annotation, but they need to be told
		//to do so to a given list of variants. We do this here so they don't waste time annotating
		//variants that will be filtered out, but this functionality should be encapsulated somewhere
		//else at some point
		for(VariantFilter filter : filters) {
			if (filter instanceof PedigreeFilter) {
				PedigreeFilter pedFilter = (PedigreeFilter)filter;
				pedFilter.applyAnnotations(passingVars);
			}
		}
		
		Logger.getLogger(CachingVariantServer.class).info(passingVars.size() + " of " + vars.size() + " total vars passed filters");
		return passingVars;
	}

}
