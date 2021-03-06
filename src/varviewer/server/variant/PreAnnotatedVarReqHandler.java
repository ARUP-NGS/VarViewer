package varviewer.server.variant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import varviewer.server.FilterExecutor;
import varviewer.server.SimpleFilterExecutor;
import varviewer.server.VariantRequestHandler;
import varviewer.server.sampleSource.SampleSource;
import varviewer.shared.varFilters.PedigreeFilter;
import varviewer.shared.variant.AnnotationIndex;
import varviewer.shared.variant.Variant;
import varviewer.shared.variant.VariantFilter;
import varviewer.shared.variant.VariantRequest;
import varviewer.shared.variant.VariantRequestResult;

public class PreAnnotatedVarReqHandler implements VariantRequestHandler {

	SampleSource variantSource = null;
	boolean profile = false;
	
	public SampleSource getVariantSource() {
		return variantSource;
	}

	public void setVariantSource(SampleSource variantSource) {
		this.variantSource = variantSource;
	}

	@Override
	public VariantRequestResult queryVariant(VariantRequest req) {
		Date begin = new Date();
		
		VariantRequestResult result = new VariantRequestResult();
		result.setSampleID(req.getSamples().get(0).getSampleID());
		
		VariantCollection vars = variantSource.getVariantsForSample(req.getSamples().get(0));
		
		//If there are no variants in this sample (sample exists, but has zero variants) then
		//return a new empty result
		if (vars == null || vars.getAnnoIndex() == null ) {
			result.setVars(new ArrayList<Variant>());
			return result;
		}
		
		//A bit ugly here... filters often use Annotations to do their filtering, so they need
		//access to the AnnotationIndex. Here, we just go through all filters and set the index. 
		AnnotationIndex index = vars.getAnnoIndex();
		if (index == null) {
			throw new IllegalStateException("Annotation index was not set for variants from sample : " + req.getSamples().get(0).getSampleID());
		}
		for(VariantFilter filter : req.getFilters()) {
			filter.setAnnotationIndex(index);
		}

		//Kind of a hack here... pedigree-based filters need to be 'initialized' with a SampleSource
		//before they work, right now we do this here. 
		for(VariantFilter filter : req.getFilters()) {
			if (filter instanceof PedigreeFilter) {
				PedigreeFilter pedFilter = (PedigreeFilter)filter;
				pedFilter.setVariantSource(variantSource);
			}
		}

		Date filterTime = new Date();
		
		FilterExecutor filterExec = new SimpleFilterExecutor();
		List<Variant> passingVars = filterExec.filterAll(vars, req.getFilters());

		Date endFilter = new Date();
		double readSecs = (filterTime.getTime() - begin.getTime())/1000.0;
		double filterSecs = (endFilter.getTime() - filterTime.getTime())/1000.0;
		
		Logger.getLogger(getClass()).info("Pre-annotated var handler identified " + passingVars.size() + " variants, read time: " + readSecs + " secs, filter time: " + filterSecs + " secs.");

		
		//Similar hack here, PedigreeFilters also apply an annotation, but they need to be told
		//to do so to a given list of variants. We do this here so they don't waste time annotating
		//variants that will be filtered out, but this functionality should be encapsulated somewhere
		//else at some point
		
		for(VariantFilter filter : req.getFilters()) {
			if (filter instanceof PedigreeFilter) {
				PedigreeFilter pedFilter = (PedigreeFilter)filter;
				String key = pedFilter.getPedSample().getRelSample().getSampleID() + "-zygosity";
				int valIndex = index.getIndexForKey(key);
				if (valIndex == -1) {
					valIndex = index.addKey(key, false);
				}
				pedFilter.applyAnnotations(valIndex, passingVars);
			}
		}


		result.setVars(passingVars);
		Logger.getLogger(getClass()).info("Returning new request result for sample with id: " + req.getSamples().get(0).getSampleID());
		return result;
	}


}
