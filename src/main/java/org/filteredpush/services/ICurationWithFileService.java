package org.filteredpush.services;

import org.filteredpush.util.CurationException;

public interface ICurationWithFileService  extends ICurationService {
    public void setCacheFile(String file) throws CurationException;
  	public void flushCacheFile() throws CurationException;
}
