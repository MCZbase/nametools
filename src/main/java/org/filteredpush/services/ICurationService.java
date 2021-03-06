package org.filteredpush.services;

import java.util.List;

import org.filteredpush.util.CurationStatus;

/**
 * Interface For Curation Services.
 * 
 * @author Lei Dou
 *
 */
public interface ICurationService {
    public List<List> getLog();
    public void setUseCache(boolean use);
	public String getComment();		
	public CurationStatus getCurationStatus();
	public String getServiceName();	
}
