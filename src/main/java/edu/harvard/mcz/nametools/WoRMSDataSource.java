/** 
 * WoRMSDataSource.java 
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

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gbif.api.model.checklistbank.ParsedName;
import org.gbif.nameparser.NameParser;
import org.gbif.nameparser.UnparsableException;
import org.marinespecies.aphia.v1_0.AphiaNameServicePortTypeProxy;
import org.marinespecies.aphia.v1_0.AphiaRecord;
import org.marinespecies.aphia.v1_0.Source;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;
import org.supercsv.quote.QuoteMode;

public class WoRMSDataSource implements Harvester, Validator {
	
	private static final Log log = LogFactory.getLog(WoRMSDataSource.class);
	
	private AphiaNameServicePortTypeProxy wormsService;
	protected AuthorNameComparator authorNameComparator;
	
	protected ICsvListWriter listWriter;
	protected int depth;
	
	public WoRMSDataSource() throws IOException { 
			init();
	}
	
	protected void init()  throws IOException { 
		wormsService = new AphiaNameServicePortTypeProxy();
		depth = 0;
		log.debug(wormsService.getEndpoint());
		URL test = new URL(wormsService.getEndpoint());
		URLConnection conn = test.openConnection();
		conn.connect();
		authorNameComparator = new ICZNAuthorNameComparator(.75d,.5d);
		
		// create a csv writer, configured to use a comma separator and to force all columns 
		// except valid_catalog_term_fg to be quoted.
		boolean[] cols = new boolean[] {
                    true,true,true,true,true,true,
                    true, true,
                    true, true, true,
                    false,true,
                    true,
                    true,
                    true };
		QuoteMode quoteMode = new ColumnQuoteMode(cols);
		CsvPreference prefs = new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(quoteMode).build();
		listWriter = new CsvListWriter(new FileWriter("wormharvest.csv"), prefs);
		String[] header = new String[] {
                "KINGDOM","PHYLUM","PHYLCLASS","PHYLORDER","FAMILY","GENUS",
                "SPECIES", "AUTHOR_TEXT",
                "SUBSPECIES", "INFRASPECIFIC_RANK", "INFRASPECIFIC_AUTHOR",
                "VALID_CATALOG_TERM_FG","SOURCE_AUTHORITY",
                "NOMENCLATURAL_CODE",
                "TAXON_STATUS",
                "TAXON_REMARKS",
                "SCIENTIFIC_NAME"
		};
		listWriter.writeHeader(header);		
	}
	
	public void harvestComplete() { 
		if (listWriter!=null) { 
		    try {
				listWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void getAllChildren(String taxon) { 
		boolean marineOnly = false;
			try {
				int taxonid = wormsService.getAphiaID(taxon, marineOnly);
				log.debug(taxonid);
				getAllChildren(taxonid, marineOnly);
			} catch (RemoteException e) {
				log.debug(e.getMessage());
			}
	}
	
	protected AphiaRecord[] populateChildrenArray(int taxonid, boolean marineOnly) throws RemoteException { 
		AphiaRecord[] childrenArray = wormsService.getAphiaChildrenByID(taxonid, 0, marineOnly);
		if (childrenArray!=null) { 
			int count = childrenArray.length;
			if (count==50) {
				// there might be more records
				ArrayList<AphiaRecord> children = new ArrayList<AphiaRecord>(Arrays.asList(childrenArray));
				AphiaRecord[] nextBit = null;
				int more = 50;
				while (more==50) { 
					nextBit = wormsService.getAphiaChildrenByID(taxonid, count+1, marineOnly);
					if (nextBit!=null) { 
						more = nextBit.length;
						count = count+more;
						children.addAll(new ArrayList<AphiaRecord>(Arrays.asList(nextBit)));
					} else { 
						more = 0;
					}
				} 
				try { 
				    childrenArray = (AphiaRecord[]) children.toArray();
				} catch (ClassCastException e) { 
					log.error(e.getMessage(),e);
				}
			}
		}
		return childrenArray;
	}
	
	protected void getAllChildren(int taxonid, boolean marineOnly) { 
		try {
			AphiaRecord record = wormsService.getAphiaRecordByID(taxonid);
			AphiaRecord[] childrenArray = populateChildrenArray(taxonid, marineOnly);
			if (childrenArray!=null) { 
				ArrayList<AphiaRecord> children = new ArrayList<AphiaRecord>(Arrays.asList(childrenArray));
				Iterator<AphiaRecord> i =  children.iterator();
				while (i.hasNext()) { 
					AphiaRecord child = i.next();
					log.debug(child.getScientificname() + " " + child.getRank()) ;
					if (child.getRank().equals("Genus")|| child.getRank().equals("Species") || child.getRank().equals("Subspecies")) { 
						ArrayList<String> output = new ArrayList<String>();

						NameParser parser = new NameParser();
						ParsedName nameBits = null;
						try {
							nameBits = parser.parse(child.getScientificname());
						} catch (UnparsableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						//"KINGDOM","PHYLUM","PHYLCLASS","PHYLORDER","FAMILY","GENUS",
						//"SPECIES", "AUTHOR_TEXT",
						//"SUBSPECIES", "INFRASPECIFIC_RANK", "INFRASPECIFIC_AUTHOR",
						//"VALID_CATALOG_TERM_FG","SOURCE_AUTHORITY",
						//"NOMENCLATURAL_CODE",
						//"TAXON_STATUS",
						//"TAXON_REMARKS"
						//"scientific_name"

						output.add(child.getKingdom());
						output.add(child.getPhylum());
						output.add(child.get_class());
						output.add(child.getOrder());
						output.add(child.getFamily());
						output.add(child.getGenus());
						if ((child.getRank().equals("Subspecies") || child.getRank().equals("Species")) && nameBits!=null ) {
							output.add(nameBits.getSpecificEpithet());
						} else { 
							output.add(child.getScientificname());
						}
						if (child.getRank().equals("Subspecies") && !child.getKingdom().equals("Animalia")) {
							output.add("");
						} else { 
							// for all animals, put authorship in author_text
						    output.add(child.getAuthority());  // authorship
						}
						if (child.getRank().equals("Subspecies") && nameBits !=null) { 
							output.add(nameBits.getInfraSpecificEpithet()); // infraspecific name
						} else { 
							output.add("");
						}
						
						if (child.getRank().equals("Subspecies") && nameBits !=null) { 
							if (nameBits.getRankMarker()==null) { 
							    output.add("");  // getting no "" for subspecies in worms.
							} else { 
							    output.add(nameBits.getRankMarker());  // infraspecific_rank
							}
						} else { 
							output.add("");
						}
						
						if (child.getRank().equals("Subspecies") && nameBits !=null && !child.getKingdom().equals("Animalia")) { 
							output.add(child.getAuthority()); // infraspecific author for non-animals.
						} else { 
							// for animals authorship for all ranks goes in author text
							output.add("");
						}
						if (child.getStatus().equals("accepted")) { 
							output.add("1");  // valid_catalog_term_fg
						} else { 
							output.add("0");
						}
						output.add("WoRMS (World Register of Marine Species)");  // source authority
						if (child.getKingdom().equals("Animalia")) { 
							output.add("ICZN");  //  nomenclatural code  
						} else { 
							//TODO: Determine kingdom to code mappings for other roots in WoRMS
							output.add("ICNafp");	
						}
						output.add(child.getUnacceptreason()); //  taxon_status
						output.add("urn:lsid:marinespecies.org:taxname:" + Integer.toString(child.getAphiaID()));  // put guid in taxon remarks

						output.add(child.getScientificname());
						
						listWriter.write(output);
					}
					if (!child.getRank().equals("Subspecies")) { 
						getAllChildren(child.getAphiaID(), marineOnly);
					}
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public NameUsage validate(NameUsage taxonNameToValidate) {
		log.debug("Checking: " + taxonNameToValidate.getScientificName() + " " + taxonNameToValidate.getAuthorship());
		NameUsage result = null;
		depth++;   
		try {
			String taxonName = taxonNameToValidate.getScientificName();
			String authorship = taxonNameToValidate.getAuthorship();
			taxonNameToValidate.setAuthorComparator(authorNameComparator);
			AphiaRecord[] resultsArr = wormsService.getAphiaRecords(taxonName, false, false, false, 1);
			if (resultsArr!=null && resultsArr.length>0) { 
				// We got at least one result
				List<AphiaRecord> results = Arrays.asList(resultsArr);
				Iterator<AphiaRecord> i = results.iterator();
				//Multiple matches indicate homonyms (or in WoRMS, deleted records).
				if (results.size()>1) {
				    log.debug("More than one match: " + resultsArr.length);
					boolean exactMatch = false;
					List<AphiaRecord> matches = new ArrayList<AphiaRecord>();
					while (i.hasNext() && !exactMatch) { 
					    AphiaRecord ar = i.next();
					    matches.add(ar);
					    log.debug(ar.getScientificname());
					    log.debug(ar.getAphiaID());
					    log.debug(ar.getAuthority());
					    log.debug(ar.getUnacceptreason());
					    log.debug(ar.getStatus());
					    if (ar !=null && ar.getScientificname()!=null && taxonName!=null && ar.getScientificname().equals(taxonName)) {
					    	if (ar.getAuthority()!=null && ar.getAuthority().equals(authorship)) {
					    		// If one of the results is an exact match on scientific name and authorship, pick that one. 
					    		result = new NameUsage(ar);
					    		result.setInputDbPK(taxonNameToValidate.getInputDbPK());
					    		result.setMatchDescription(NameComparison.MATCH_EXACT);
					    		result.setAuthorshipStringEditDistance(1d);
					    		result.setOriginalAuthorship(taxonNameToValidate.getAuthorship());
					    		result.setOriginalScientificName(taxonNameToValidate.getScientificName());
					    		result.setScientificNameStringEditDistance(1d);
					    		exactMatch = true;
					    	}
					    }
					}
					if (!exactMatch) {
						// If we didn't find an exact match on scientific name and authorship in the list, pick the 
						// closest authorship and list all of the potential matches.  
						Iterator<AphiaRecord> im = matches.iterator();
						AphiaRecord ar = im.next();
						NameUsage closest = new NameUsage(ar);
						StringBuffer names = new StringBuffer();
						names.append(closest.getScientificName()).append(" ").append(closest.getAuthorship()).append(" ").append(closest.getUnacceptReason()).append(" ").append(closest.getTaxonomicStatus());
						while (im.hasNext()) { 
							ar = im.next();
							NameUsage current = new NameUsage(ar);
						    names.append("; ").append(current.getScientificName()).append(" ").append(current.getAuthorship()).append(" ").append(current.getUnacceptReason()).append(" ").append(current.getTaxonomicStatus());
							if (ICZNAuthorNameComparator.calulateSimilarityOfAuthor(closest.getAuthorship(), authorship) < ICZNAuthorNameComparator.calulateSimilarityOfAuthor(current.getAuthorship(), authorship)) { 
								closest = current;
							}
						}
						result = closest;
					    result.setInputDbPK(taxonNameToValidate.getInputDbPK());
					    result.setMatchDescription(NameComparison.MATCH_MULTIPLE + " " + names.toString());
					    result.setOriginalAuthorship(taxonNameToValidate.getAuthorship());
					    result.setOriginalScientificName(taxonNameToValidate.getScientificName());
					    result.setScientificNameStringEditDistance(1d);
					    result.setAuthorshipStringEditDistance(ICZNAuthorNameComparator.calulateSimilarityOfAuthor(taxonNameToValidate.getAuthorship(), result.getAuthorship()));
					}
				} else { 
				  // we got exactly one result
				  while (i.hasNext()) { 
					AphiaRecord ar = i.next();
					if (ar !=null && ar.getScientificname()!=null && taxonName!=null && ar.getScientificname().equals(taxonName)) {
						if (ar.getAuthority()!=null && ar.getAuthority().equals(authorship)) { 
							// scientific name and authorship are an exact match 
							result = new NameUsage(ar);
							result.setInputDbPK(taxonNameToValidate.getInputDbPK());
							result.setMatchDescription(NameComparison.MATCH_EXACT);
							result.setAuthorshipStringEditDistance(1d);
							result.setOriginalAuthorship(taxonNameToValidate.getAuthorship());
							result.setOriginalScientificName(taxonNameToValidate.getScientificName());
							result.setScientificNameStringEditDistance(1d);
						} else {
							// find how 
							if (authorship!=null && ar!=null && ar.getAuthority()!=null) { 
								//double similarity = taxonNameToValidate.calulateSimilarityOfAuthor(ar.getAuthority());
								log.debug(authorship);
								log.debug(ar.getAuthority());
								NameComparison comparison = authorNameComparator.compare(authorship, ar.getAuthority());
								String match = comparison.getMatchType();
								double similarity = comparison.getSimilarity();
								log.debug(similarity);
								//if (match.equals(NameUsage.MATCH_DISSIMILAR) || match.equals(NameUsage.MATCH_ERROR)) {
									// result.setMatchDescription("Same name, authorship different");
								//} else { 
							        result = new NameUsage(ar);
							        result.setInputDbPK(taxonNameToValidate.getInputDbPK());
							        result.setAuthorshipStringEditDistance(similarity);
							        result.setOriginalAuthorship(taxonNameToValidate.getAuthorship());
							        result.setOriginalScientificName(taxonNameToValidate.getScientificName());
								    result.setMatchDescription(match);
								//}
							} else { 
								// no authorship was provided in the results, treat as no match
								log.error("Result with null authorship.");
							}
						}
					}
				  }
				}
			} else { 
				log.debug("No match.");
				// Try WoRMS fuzzy matching query
				String[] searchNames = { taxonName + " " + authorship };
				AphiaRecord[][] matchResultsArr = wormsService.matchAphiaRecordsByNames(searchNames, false);
				if (matchResultsArr!=null && matchResultsArr.length>0) {
					Iterator<AphiaRecord[]> i0 = (Arrays.asList(matchResultsArr)).iterator();
					while (i0.hasNext()) {
						// iterate through the inputs, there should be one and only one
						AphiaRecord[] matchResArr = i0.next();
						List<AphiaRecord> matches = Arrays.asList(matchResArr);
						Iterator<AphiaRecord> im = matches.iterator();
						List<NameUsage> potentialMatches = new ArrayList<NameUsage>();
						while (im.hasNext()) { 
							// iterate through the results, no match will have one result that is null
							AphiaRecord ar = im.next();
							if (ar!=null) { 
								NameUsage match = new NameUsage(ar);
								double similarity = ICZNAuthorNameComparator.calulateSimilarityOfAuthor(taxonNameToValidate.getAuthorship(), match.getAuthorship());
								match.setAuthorshipStringEditDistance(similarity);
								log.debug(match.getScientificName());
								log.debug(match.getAuthorship());
								log.debug(similarity);
								potentialMatches.add(match);
							} else {
								log.debug("im.next() was null");
							}
						} 
						log.debug("Fuzzy Matches: " + potentialMatches.size());
						if (potentialMatches.size()==1) { 
							result = potentialMatches.get(0);
							String authorComparison = authorNameComparator.compare(taxonNameToValidate.getAuthorship(), result.getAuthorship()).getMatchType();
							result.setMatchDescription(NameComparison.MATCH_FUZZY_SCINAME + "; authorship " + authorComparison);
							result.setOriginalAuthorship(taxonNameToValidate.getAuthorship());
							result.setOriginalScientificName(taxonNameToValidate.getScientificName());
							result.setInputDbPK(taxonNameToValidate.getInputDbPK());
						}
					} // iterator over input names, should be just one.
			    } else {
			    	log.error("Fuzzy match query returned null instead of a result set.");
			    }
			}
		} catch (RemoteException e) {
			if (e.getMessage().equals("Connection timed out")) { 
				log.error(e.getMessage() + " " + taxonNameToValidate.getScientificName() + " " + taxonNameToValidate.getInputDbPK());
			} else if (e.getCause()!=null && e.getCause().getClass().equals(UnknownHostException.class)) { 
				log.error("Connection Probably Lost.  UnknownHostException: "+ e.getMessage());
			} else {
				log.error(e.getMessage(), e);
			}
			if (depth<4) {
				// Try again, up to three times.
				result = this.validate(taxonNameToValidate);
			}
		}
		depth--;
		return result;
	}
	
	public void findCitations(NameUsage toCheck) {
		String taxonName = toCheck.getScientificName();
		String authorship = toCheck.getAuthorship();
		toCheck.setAuthorComparator(authorNameComparator);
		try {
			AphiaRecord[] resultsArr = wormsService.getAphiaRecords(taxonName, false, false, false, 1);
			List<AphiaRecord> results = Arrays.asList(resultsArr);
			log.debug(resultsArr.length);
			Iterator<AphiaRecord> i = results.iterator();
			while(i.hasNext()) { 
				AphiaRecord record = i.next();
				int id = record.getAphiaID();
				Source[] sourceArr = wormsService.getSourcesByAphiaID(id);
				List<Source> sources = Arrays.asList(sourceArr);
				Iterator<Source> is = sources.iterator();
				while (is.hasNext()) { 
					Source source = is.next();
					if (source.getUse().equals("original description")) { 
				        System.out.println(source.getUse());  // type of source, e.g. original description.
				        System.out.println(source.getReference()); // the citation for the work as a string
				        System.out.println(source.getPage());     // page in reference on which the original description is found
						System.out.println(source.getFulltext()); // full text of original citation 
						System.out.println(source.getUrl());   // uri for publication record in worms
						System.out.println(source.getLink());  // link to source document e.g. bhl
					}
				}
			}
		} catch (RemoteException e) {
			log.error(e.getMessage(),e);
		}		
		
	}

	public Map<String,String> getOriginalCitation(int aphiaId) { 
		HashMap<String,String> result = new HashMap<String,String>();
		Source[] sourceArr;
		try {
			sourceArr = wormsService.getSourcesByAphiaID(aphiaId);
			List<Source> sources = Arrays.asList(sourceArr);
			Iterator<Source> is = sources.iterator();
			while (is.hasNext()) { 
				Source source = is.next();
				if (source.getUse().equals("original description")) {
					result.put("work", source.getReference());
					result.put("page", source.getPage());
					result.put("link", source.getLink());
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
