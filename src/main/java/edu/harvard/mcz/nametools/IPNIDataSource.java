/**
 * 
 */
package edu.harvard.mcz.nametools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.services.IPNIService;
import org.filteredpush.util.CurationComment;
import org.filteredpush.util.CurationException;

/**
 * @author mole
 *
 */
public class IPNIDataSource implements Validator {

	private static final Log log = LogFactory.getLog(IPNIDataSource.class);
	
	protected IPNIService service;
	
	public IPNIDataSource() throws CurationException {
		init();
	}
	
	protected void init() throws CurationException { 
		service = new IPNIService();
		// service.setUseCache(false);
		log.debug(service.simplePlantNameSearch("Quercus alba", "L."));
	}
	
	/* (non-Javadoc)
	 * @see edu.harvard.mcz.nametools.Validator#validate(edu.harvard.mcz.nametools.NameUsage)
	 */
	@Override
	public NameUsage validate(NameUsage taxonToValidate) {
		NameUsage result = null;
		if (taxonToValidate.getScientificName()!=null) { 
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
					result.setGuid(service.getLSID());
				    result.setScientificName(service.getCorrectedScientificName());
				    result.setAuthorship(service.getCorrectedAuthor());
				    result.setOriginalAuthorship(authorship);
				    result.setOriginalScientificName(taxonName);
				    result.setInputDbPK(taxonToValidate.getInputDbPK());
				    double authorSimilarity = taxonToValidate.calulateSimilarityOfAuthor(service.getCorrectedAuthor());
				    double nameSimilarity = NameUsage.stringSimilarity(taxonName, service.getCorrectedScientificName());
				    String match = NameUsage.compare(authorship, result.getAuthorship());
				    if (authorSimilarity==1d && nameSimilarity==1d) { 
				    	result.setMatchDescription(NameUsage.MATCH_EXACT);
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
