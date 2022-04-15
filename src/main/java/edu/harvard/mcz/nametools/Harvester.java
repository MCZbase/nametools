/**
 * Harvester.java
 *
 */
package edu.harvard.mcz.nametools;

/**
 * @author mole
 *
 */
public interface Harvester {
	
	public abstract void getAllChildren(String taxon);
	
	public abstract void harvestComplete();
}
