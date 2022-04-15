/**
 * Harvester.java
 *
 */
package edu.harvard.mcz.nametools;

/**
 * Interface for data sources that can harvest taxon names.
 * The implementation is responsible for serialization of the
 * harvest to some output.
 * 
 * @author mole
 *
 */
public interface Harvester {
	
	public abstract void getAllChildren(String taxon);
	
	public abstract void harvestComplete();
}
