package varviewer.shared.varFilters;

import java.io.Serializable;

import varviewer.shared.Variant;
import varviewer.shared.VariantFilter;

/**
 * A variant filter that passes variants with a numeric value associated with the given
 * key that is LESS than the given value. For instance, if the key is "pop.freq", and 
 * the max value is 0.01, this will pass all variants with pop.freq less than 0.01
 * @author brendan
 *
 */
public class MaxFreqFilter implements VariantFilter, Serializable {

	//Maximum allowable pop frequency
	double maxFreq = Double.MAX_VALUE;
	
	//Max arup value
	int arupMax = 100;
	
	public MaxFreqFilter() {
		//Required no-arg constructor
	}
	
	
	/**
	 * Set the maximum frequency allowed by this filter
	 * @param freq
	 */
	public void setMaxFreq(double freq) {
		this.maxFreq = freq;
	}
	
	public double getMaxFreq() {
		return maxFreq;
	}
	
	
	
	public int getArupMax() {
		return arupMax;
	}

	public void setArupMax(int arupMax) {
		this.arupMax = arupMax;
	}

	@Override
	public boolean variantPasses(Variant var) {
		String freq = var.getAnnotation("pop.freq");
		String arupStr = var.getAnnotation("ARUP.freq");
		
		if (freq == null && arupStr == null)
			return true;

		Double freqVal = 0.0;
		int arupTot = 0;
		try {
			Double val = Double.parseDouble(freq);
			freqVal = val;
		}
		catch (NumberFormatException nfe) {
		}

		if (freqVal > maxFreq) {
			return false;
		}

		try {

			int idx = arupStr.indexOf(" tot");
			if (idx > 0) {
				arupTot = Integer.parseInt( arupStr.substring(0, idx));
				if (arupTot > arupMax) {
					return false;
				}
			}
		}
		catch(NumberFormatException nfe) {

		}

		return true;
	}

}