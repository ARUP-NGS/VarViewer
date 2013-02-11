package varviewer.server;

import java.io.IOException;
import java.util.List;

import varviewer.server.variant.VariantCollection;
import varviewer.shared.HasSamples;
import varviewer.shared.SampleInfo;
import varviewer.shared.SampleTreeNode;

/**
 * Objects which generate a list of SampleInfo objects should implement this interface
 * @author brendan
 *
 */
public interface SampleSource extends HasSamples {

	
	/**
	 * Read (or re-load) the source of the variants to search for changes
	 * @param sampleID
	 * @return
	 */
	public void initialize() throws IOException;
	
	/**
	 * True if this source contains a sample with the given id
	 * @param sampleID
	 * @return
	 */
	public boolean containsSample(String sampleID);
	
	/**
	 * Retrieve a list of all SampleInfo objects representing samples available through this source
	 * @return
	 */
	public List<SampleInfo> getAllSamples();
	
	
	/**
	 * Retrieve the root of the tree containing all samples
	 * @return
	 */
	public SampleTreeNode getSampleTreeRoot();
	
	/**
	 * Obtain sampleInfo for the given sample 
	 * @param sampleID
	 * @return
	 */
	public SampleInfo getInfoForSample(String sampleID);
	
	/**
	 * Obtain all variants in this sample in a VariantCollection object 
	 * @param sampleID
	 * @return
	 */
	public VariantCollection getVariantsForSample(String sampleID);
}
