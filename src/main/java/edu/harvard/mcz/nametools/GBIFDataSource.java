/**
 * GBIFDataSource.java 
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
 * 
 */
package edu.harvard.mcz.nametools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gbif.api.model.checklistbank.ParsedName;
import org.gbif.nameparser.NameParser;
import org.gbif.nameparser.UnparsableException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.marinespecies.aphia.v1_0.AphiaRecord;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;
import org.supercsv.quote.QuoteMode;

/**
 * See service documentation at: http://dev.gbif.org/wiki/display/POR/Webservice+API#WebserviceAPI-ChecklistBankServices:Nameusage
 * 
 * @author mole
 *
 */
public class GBIFDataSource implements Harvester, Validator {
	
	private static final Log log = LogFactory.getLog(GBIFDataSource.class);
	
	
	public static final String GBIF_SERVICE = "http://api.gbif.org/v1";
	
	public static final String KEY_GBIFBACKBONE = "d7dddbf4-2cf0-4f39-9b2a-bb099caae36c";
	public static final String KEY_IPNI = "046bbc50-cae2-47ff-aa43-729fbf53f7c5";
	public static final String KEY_INDEXFUNGORUM = "bf3db7c9-5e5d-4fd0-bd5b-94539eaf9598";
	public static final String KEY_COL = "7ddf754f-d193-4cc9-b351-99906754a03b";
	
	protected String targetKey;
	protected String targetDataSetName; 
	protected boolean fetchSynonymsAboveSpecies;
	protected ICsvListWriter listWriter;

	public GBIFDataSource()  throws IOException  { 
		targetKey = GBIFDataSource.KEY_GBIFBACKBONE;
		targetDataSetName = "GBIF Backbone Taxonomy";
		fetchSynonymsAboveSpecies = true;
		init();
	}
	
	public GBIFDataSource(String targetKey)  throws IOException  { 
		this.targetKey = targetKey;
		fetchSynonymsAboveSpecies = true;
		//TODO: Lookup title for dataset from targetKey (and check that datset exists).
		targetDataSetName = "GBIF Dataset" + targetKey; 
		init();
	}
	
	protected void init()  throws IOException  { 
		// check that the service is up.
		GBIFDataSource.searchForGenus("Murex", GBIFDataSource.KEY_GBIFBACKBONE,1);
		
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

		listWriter = new CsvListWriter(new FileWriter("gbifharvest.csv"), prefs);
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
	

	public static String fetchTaxon(String taxon, String targetChecklist) { 
		StringBuilder result = new StringBuilder();
		String datasetKey = "";
		if (targetChecklist!=null) { 
			datasetKey = "datasetKey=" + targetChecklist;
		}
		URL url;
		try {
			//url = new URL(GBIF_SERVICE + "/name_usage/" + taxon + "?limit=100&" + datasetKey);
			url = new URL(GBIF_SERVICE + "/species/?name=" + taxon + "?limit=100&" + datasetKey);
			URLConnection connection = url.openConnection();
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while((line = reader.readLine()) != null) {
				result.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return result.toString();
	}
	
	public static String searchForTaxon(String name, String targetChecklist) { 
		StringBuilder result = new StringBuilder();
		String datasetKey = "";
		if (targetChecklist!=null) { 
			datasetKey = "datasetKey=" + targetChecklist;
		}
		URL url;
		try {
			//url = new URL(GBIF_SERVICE + "/name_usage/search?q=" + name + "&limit=100&" + datasetKey);
			url = new URL(GBIF_SERVICE + "/species/?name=" + URLEncoder.encode(name,"UTF-8") + "&limit=100&" + datasetKey);
			URLConnection connection = url.openConnection();
			log.debug(url.toString());
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while((line = reader.readLine()) != null) {
				result.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result.toString();
	}
	
	public static String searchForSpecies(String name, String targetChecklist) throws IOException { 
		StringBuilder result = new StringBuilder();
		String datasetKey = "";
		if (targetChecklist!=null) { 
			datasetKey = "datasetKey=" + targetChecklist;
		}
		URL url;
			//url = new URL(GBIF_SERVICE + "/name_usage/search?q=" + URLEncoder.encode(name,"UTF-8") + "&rank=SPECIES&limit=100&" + datasetKey);
			url = new URL(GBIF_SERVICE + "/species/search?q=" + URLEncoder.encode(name,"UTF-8") + "&rank=SPECIES&limit=100&" + datasetKey);
			log.debug(url.toString());
			URLConnection connection = url.openConnection();
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while((line = reader.readLine()) != null) {
				result.append(line);
				log.debug(line);
			}
		return result.toString();
	}	
	
	public static String searchForGenus(String name, String targetChecklist, int limit) throws IOException { 
		StringBuilder result = new StringBuilder();
		String datasetKey = "";
		if (targetChecklist!=null) { 
			datasetKey = "datasetKey=" + targetChecklist;
		}
		URL url;
			url = new URL(GBIF_SERVICE + "/species/search?q=" + URLEncoder.encode(name,"UTF-8") + "&rank=GENUS&limit=" + Integer.toString(limit) + "&" + datasetKey);
			log.debug(url.toString());
			URLConnection connection = url.openConnection();
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while((line = reader.readLine()) != null) {
				result.append(line);
				log.debug(line);
			}
		return result.toString();
	}	
	
	public static String fetchChildren(int taxonId, String targetChecklist, String taxonName) throws IOException { 
		StringBuilder result = new StringBuilder();
		String datasetKey = "";
		if (targetChecklist!=null) { 
			datasetKey = "datasetKey=" + targetChecklist;
		}
		URL url;
		try {
			//url = new URL(GBIF_SERVICE + "/name_usage/" + taxonId + "/children?limit=1000&" + datasetKey);
			url = new URL(GBIF_SERVICE + "/species/" + taxonId + "/children?limit=1000&" + datasetKey);
			URLConnection connection = url.openConnection();
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while((line = reader.readLine()) != null) {
				result.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (result.toString().equals("{\"offset\":0,\"limit\":1000,\"endOfRecords\":true,\"results\":[]}") &&
				targetChecklist.equals("bf3db7c9-5e5d-4fd0-bd5b-94539eaf9598"))
		{ 
			return searchForSpecies(taxonName, targetChecklist);
		}

		return result.toString();
	}	
	
	public static String fetchSynonyms(int taxonId, String targetChecklist) { 
		StringBuilder result = new StringBuilder();
		String datasetKey = "";
		if (targetChecklist!=null) { 
			datasetKey = "datasetKey=" + targetChecklist;
		}
		URL url;
		try {
			//url = new URL(GBIF_SERVICE + "/name_usage/" + taxonId + "/synonyms?limit=1000&" + datasetKey);
			url = new URL(GBIF_SERVICE + "/species/" + taxonId + "/synonyms?limit=1000&" + datasetKey);
			URLConnection connection = url.openConnection();
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while((line = reader.readLine()) != null) {
				result.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return result.toString();
	}		
	
	public static List<NameUsage> parseAllNameUsagesFromJSON(String json) {
		boolean gotAll = true;
		ArrayList<NameUsage> result = new ArrayList<NameUsage>(); 
        JSONParser parser=new JSONParser();

        try {
        	JSONArray array = new JSONArray();
        	try {
        	    JSONObject o = (JSONObject)parser.parse(json);
			    array = (JSONArray)o.get("results");
			    //System.out.println(o.get("offset"));
			    //System.out.println(o.get("limit"));
			    //System.out.println(o.get("endOfRecords"));
			    //System.out.println(o.get("count"));
			    if (o.get("endOfRecords").equals("false")) { 
			    	gotAll = false;
			    }
        	} catch (ClassCastException e) { 
    			array = (JSONArray)parser.parse(json);
        	}
    			 
			Iterator i = array.iterator();
			while (i.hasNext()) { 
				JSONObject obj = (JSONObject)i.next();
				NameUsage name = new NameUsage(obj);
				result.add(name);
			}
 
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO: Report not getting all records.
		if (!gotAll) { 
			System.out.println("Incomplete Harvest");
		}
		
		return result;
	}	
	
	public static void  fetchAllChildren(NameUsage match, String targetChecklist, boolean fetchSynonymsAboveSpecies) throws IOException {  
		if (match!=null) { 
			String json = GBIFDataSource.fetchChildren(match.getKey(), targetChecklist, match.getCanonicalName());

			Iterator<NameUsage> i = parseAllNameUsagesFromJSON(json).iterator();
			while (i.hasNext()) { 
				NameUsage usage = i.next();
				// Prevent infinite loop if search is invoked
				if (!usage.getCanonicalName().equals(match.getCanonicalName())) { 
				   System.out.print(usage.toCSVLine());
				   fetchAllChildren(usage,targetChecklist,fetchSynonymsAboveSpecies);
				}
			}

			if (fetchSynonymsAboveSpecies || 
					match.getRank().equals("SPECIES") ||
					match.getRank().equals("SUBSPECIES") ||
					match.getRank().equals("INFRASPECIFIC_NAME") ||
					match.getRank().equals("VARIETY") ||
					match.getRank().equals("FORM") ||
					match.getRank().equals("INFRASUBSPECIFIC_NAME") 
			) 
			{ 
				// look up synonyms.
				json = GBIFDataSource.fetchSynonyms(match.getKey(), targetChecklist);

				i = parseAllNameUsagesFromJSON(json).iterator();
				while (i.hasNext()) { 
					NameUsage usage = i.next();
					System.out.print(usage.toCSVLine());
					// don't retrieve children of synonyms.
					// fetchAllChildren(usage,targetChecklist);
				}
			}
		}
	}
	
	public static NameUsage parseNameUsageFromJSON(String targetName, String json) { 
        JSONParser parser=new JSONParser();

        try {
        	JSONArray array = new JSONArray();
        	try {
        	    JSONObject o = (JSONObject)parser.parse(json);
        	    if (o.get("results")!=null) {  
			        array = (JSONArray)o.get("results");
        	    } else { 
        	    	// array = (JSONArray)parser.parse(json);
        	    }
        	} catch (ClassCastException e) { 
        		log.debug(e.getMessage(),e);
    			array = (JSONArray)parser.parse(json);
        	}
    			 
			Iterator<JSONObject> i = array.iterator();
			while (i.hasNext()) { 
				JSONObject obj = i.next();
				// System.out.println(obj.toJSONString());
				NameUsage name = new NameUsage(obj);
				if (name.getCanonicalName().equals(targetName)) { 
					return name;
				}
			}
 
		} catch (ParseException e) {
        	log.debug(e.getMessage(),e);
		}
		return null;
	}

	@Override
	public void getAllChildren(String taxon) {
		log.debug(taxon);
		try { 
			NameUsage match = parseNameUsageFromJSON(taxon, GBIFDataSource.searchForTaxon(taxon, targetKey));
			if (match!=null) { 
				log.debug(match.getCanonicalName());
				String json = GBIFDataSource.fetchChildren(match.getKey(), targetKey, match.getCanonicalName());

				Iterator<NameUsage> i = parseAllNameUsagesFromJSON(json).iterator();
				while (i.hasNext()) { 
					NameUsage usage = i.next();
					// Prevent infinite loop if search is invoked
					if (!usage.getCanonicalName().equals(match.getCanonicalName())) {
						
						ArrayList<String> output = new ArrayList<String>();
						
						NameParser parser = new NameParser();
						ParsedName nameBits = null;
						try {
							nameBits = parser.parse(usage.getScientificName());
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

						output.add(usage.getKingdom());
						output.add(usage.getPhylum());
						output.add(usage.getTclass());
						output.add(usage.getOrder());
						output.add(usage.getFamily());
						output.add(usage.getGenus());
						if ((usage.getRank().equals("Subspecies") || usage.getRank().equals("Species")) && nameBits!=null ) {
							output.add(nameBits.getSpecificEpithet());
						} else { 
							output.add(usage.getScientificName());
						}
						if (usage.getRank().equals("Subspecies") && !usage.getKingdom().equals("Animalia")) {
							output.add("");
						} else { 
							// for all animals, put authorship in author_text
						    output.add(usage.getAuthorship());  // authorship
						}
						if (usage.getRank().equals("Subspecies") && nameBits !=null) { 
							output.add(nameBits.getInfraSpecificEpithet()); // infraspecific name
						} else { 
							output.add("");
						}
						
						if (usage.getRank().equals("Subspecies") && nameBits !=null) { 
							if (nameBits.getRankMarker()==null) { 
							    output.add("");  // getting no "" for subspecies in worms.
							} else { 
							    output.add(nameBits.getRankMarker());  // infraspecific_rank
							}
						} else { 
							output.add("");
						}
						
						if (usage.getRank().equals("Subspecies") && nameBits !=null && !usage.getKingdom().equals("Animalia")) { 
							output.add(usage.getAuthorship()); // infraspecific author for non-animals.
						} else { 
							// for animals authorship for all ranks goes in author text
							output.add("");
						}
						if (usage.getAcceptedName().equals(usage.getScientificName())) { 
							output.add("1");  // valid_catalog_term_fg
						} else { 
							output.add("0");
						}
						output.add(targetDataSetName);  // source authority
						if (usage.getKingdom().equals("Animalia")) { 
							output.add("ICZN");  //  nomenclatural code  
						} else { 
							//TODO: Determine kingdom to code mappings for other roots in WoRMS
							output.add("ICNafp");	
						}
						output.add(usage.getTaxonomicStatus()); //  taxon_status
						
						output.add("http://api.gbif.org/v1/species/" + Integer.toString(usage.getKey()));  // put guid in taxon remarks

						output.add(usage.getScientificName());
						
						listWriter.write(output);						
						
						
						System.out.print(usage.toCSVLine());
						fetchAllChildren(usage,targetKey,fetchSynonymsAboveSpecies);
					}
				}

				if (fetchSynonymsAboveSpecies || 
						match.getRank().equals("SPECIES") ||
						match.getRank().equals("SUBSPECIES") ||
						match.getRank().equals("INFRASPECIFIC_NAME") ||
						match.getRank().equals("VARIETY") ||
						match.getRank().equals("FORM") ||
						match.getRank().equals("INFRASUBSPECIFIC_NAME") 
						) 
				{ 
					// look up synonyms.
					json = GBIFDataSource.fetchSynonyms(match.getKey(), targetKey);

					i = parseAllNameUsagesFromJSON(json).iterator();
					while (i.hasNext()) { 
						NameUsage usage = i.next();
						System.out.print(usage.toCSVLine());
						// don't retrieve children of synonyms.
						// fetchAllChildren(usage,targetChecklist);
					}
				}
			} else { 
				log.debug("match is null, failed to parse a name usage");
			}
		} catch (IOException e) { 
			log.error(e.getMessage());
		}
	}

	@Override
	public void harvestComplete() {
		try {
			this.listWriter.close();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public NameUsage validate(NameUsage taxonNameToValidate) {
		NameUsage result = null;
		if (taxonNameToValidate!=null) {
			String taxonName = taxonNameToValidate.getScientificName();
			String authorship = taxonNameToValidate.getAuthorship();
			List<NameUsage> hits = GBIFDataSource.parseAllNameUsagesFromJSON(GBIFDataSource.searchForTaxon(taxonName, targetKey));
			if (hits==null || hits.size()==0) { 
				// no matches
			} else if (hits.size()==1) { 
			    Iterator<NameUsage> i = hits.iterator();
				// One possible match
				NameUsage potentialMatch = i.next();
				AuthorNameComparator authorNameComparator = new ICNafpAuthorNameComparator(.75d,.5d);
				if (potentialMatch.getKingdom().equals("Animalia")) { 
				    authorNameComparator = new ICZNAuthorNameComparator(.75d,.5d);
				}
				if (potentialMatch.getScientificName().equals(taxonName) && potentialMatch.getAuthorship().equals(authorship)) { 
					potentialMatch.setMatchDescription(NameComparison.MATCH_EXACT);
					result = potentialMatch;
				    result.setAuthorshipStringEditDistance(1d);
				} else { 
					NameComparison authorComparison = authorNameComparator.compare(authorship, potentialMatch.getAuthorship());
					double similarity = authorComparison.getSimilarity();
					String match = authorComparison.getMatchType();
					log.debug(authorship);
					log.debug(potentialMatch.getAuthorship());
					log.debug(similarity);
					potentialMatch.setMatchDescription(match);
					result = potentialMatch;
				    result.setAuthorshipStringEditDistance(similarity);
				}
				result.setInputDbPK(taxonNameToValidate.getInputDbPK());
				result.setScientificNameStringEditDistance(1d);
				result.setOriginalAuthorship(taxonNameToValidate.getAuthorship());
				result.setOriginalScientificName(taxonNameToValidate.getScientificName());
			} else { 
				// multiple possible matches
			    Iterator<NameUsage> i = hits.iterator();
			    log.debug("More than one match: " + hits.size());
				boolean exactMatch = false;
				List<NameUsage> matches = new ArrayList<NameUsage>();
			    while (i.hasNext() && !exactMatch) { 
			        NameUsage potentialMatch = i.next();
				    matches.add(potentialMatch);
				    log.debug(potentialMatch.getScientificName());
				    log.debug(potentialMatch.getKey());
				    log.debug(potentialMatch.getAuthorship());
				    log.debug(potentialMatch.getTaxonomicStatus());
				    if (potentialMatch.getScientificName().equals(taxonName)) {
				    	if (potentialMatch.getAuthorship().equals(authorship)) {
				    		// If one of the results is an exact match on scientific name and authorship, pick that one. 
				    		result = potentialMatch;
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
					Iterator<NameUsage> im = matches.iterator();
					if (im.hasNext()) { 
					NameUsage closest = im.next();
					StringBuffer names = new StringBuffer();
					names.append(closest.getScientificName()).append(" ").append(closest.getAuthorship()).append(" ").append(closest.getUnacceptReason()).append(" ").append(closest.getTaxonomicStatus());
					while (im.hasNext()) { 
						NameUsage current = im.next();
					    names.append("; ").append(current.getScientificName()).append(" ").append(current.getAuthorship()).append(" ").append(current.getUnacceptReason()).append(" ").append(current.getTaxonomicStatus());
						if (taxonNameToValidate.getAuthorComparator().calulateSimilarityOfAuthor(closest.getAuthorship(), authorship) < taxonNameToValidate.getAuthorComparator().calulateSimilarityOfAuthor(current.getAuthorship(), authorship)) { 
							closest = current;
						}
					}
					result = closest;
				    result.setInputDbPK(taxonNameToValidate.getInputDbPK());
				    result.setMatchDescription(NameComparison.MATCH_MULTIPLE + " " + names.toString());
				    result.setOriginalAuthorship(taxonNameToValidate.getAuthorship());
				    result.setOriginalScientificName(taxonNameToValidate.getScientificName());
				    result.setScientificNameStringEditDistance(1d);
				    result.setAuthorshipStringEditDistance(taxonNameToValidate.getAuthorComparator().calulateSimilarityOfAuthor(taxonNameToValidate.getAuthorship(), result.getAuthorship()));
					}
				}
			}
		}
		if (result!=null) { 
			// GBIF includes the authorship in the scientific name.
			result.setScientificName(result.getScientificName().replaceAll(result.getAuthorship() + "$", ""));
			// set a guid for the gbif records
			result.setGuid("http://api.gbif.org/v1/species/" + Integer.toString(result.getKey()));
		}
		return result;
	}
	
}
