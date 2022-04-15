/** 
 * IPNIDataSource.java 
 * 
 * Copyright 2015 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.harvard.mcz.nametools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.services.IPNIService;
import org.filteredpush.util.CurationComment;
import org.filteredpush.util.CurationException;

/**
 * DataSource wrapper for IPNI's web service.
 * 
 * @author mole
 *
 */
public class IPNIDataSource implements Validator {

	private static final Log log = LogFactory.getLog(IPNIDataSource.class);
	
	protected AuthorNameComparator authorNameComparator;
	
	protected IPNIService service;
	
	public IPNIDataSource() throws CurationException {
		init();
	}
	
	protected void init() throws CurationException {
		service = new IPNIService();
		// service.setUseCache(false);
		log.debug(service.simplePlantNameSearch("Quercus alba", "L."));
		authorNameComparator = new ICNafpAuthorNameComparator(.70d,.5d);
	}
	
	/* (non-Javadoc)
	 * @see edu.harvard.mcz.nametools.Validator#validate(edu.harvard.mcz.nametools.NameUsage)
	 */
	@Override
	public NameUsage validate(NameUsage taxonToValidate) {
		NameUsage result = null;
		if (taxonToValidate.getScientificName()!=null) { 
			taxonToValidate.setAuthorComparator(authorNameComparator);
			String taxonName = taxonToValidate.getScientificName().trim();
			String authorship = taxonToValidate.getAuthorship().trim();
			try {
				log.debug("About to call simplePlantNameSearch");
				String id = service.simplePlantNameSearch(taxonName, authorship);
				log.debug(id);
				log.debug(taxonName);
				log.debug(authorship);
				
				service.validateScientificName(taxonName, authorship);
				if (service.getCurationStatus().equals(CurationComment.UNABLE_DETERMINE_VALIDITY) || service.getCurationStatus().equals(CurationComment.UNABLE_CURATED)) { 
					// not found or a handled error
					log.debug(service.getCurationStatus());
					log.debug(service.getComment());
				} else { 
					log.debug(service.getCurationStatus());
					log.debug(service.getCorrectedScientificName());
					log.debug(service.getCorrectedAuthor());
					log.debug(service.getLSID());
					result = new NameUsage();
					result.setAuthorComparator(authorNameComparator);
					result.setGuid(service.getLSID());
				    result.setScientificName(service.getCorrectedScientificName());
				    result.setAuthorship(service.getCorrectedAuthor());
				    result.setOriginalAuthorship(authorship);
				    result.setOriginalScientificName(taxonName);
				    result.setInputDbPK(taxonToValidate.getInputDbPK());
				    double nameSimilarity = ICNafpAuthorNameComparator.stringSimilarity(taxonName, service.getCorrectedScientificName());
				    NameComparison comparison = authorNameComparator.compare(authorship, result.getAuthorship()); 
				    //double authorSimilarity = taxonToValidate.calulateSimilarityOfAuthor(service.getCorrectedAuthor());
				    //String match = authorNameComparator.compare(authorship, result.getAuthorship());
				    double authorSimilarity = comparison.getSimilarity();
				    String match = comparison.getMatchType();
				    if (authorSimilarity==1d && nameSimilarity==1d) { 
				    	result.setMatchDescription(NameComparison.MATCH_EXACT);
				    } else { 
				    	result.setMatchDescription(match);
				    }
				    result.setAuthorshipStringEditDistance(authorSimilarity);
				    
				}
			} catch (CurationException e) {
				log.error(e.getMessage(),e);
			}
		}
		return result;
	}

}
