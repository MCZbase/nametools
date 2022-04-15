package org.filteredpush.services;

import org.filteredpush.util.CurationException;

/**
 * Interface for curation services that support a cache file
 * 
 * @author Lei Dou
 *
 */
public interface ICurationWithFileService  extends ICurationService {
    public void setCacheFile(String file) throws CurationException;
  	public void flushCacheFile() throws CurationException;
}
