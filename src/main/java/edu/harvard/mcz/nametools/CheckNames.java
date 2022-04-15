/**
 * CheckNames.java
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
 * Author: Paul J. Morris
 */
package edu.harvard.mcz.nametools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gbif.api.model.checklistbank.ParsedName;
import org.gbif.nameparser.NameParser;
import org.gbif.nameparser.UnparsableException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.util.CurationException;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

/**
 * Launch the nameTools scientific name string validation and harvesting toolkit.
 * 
 * @author mole
 *
 * $Id: CheckNames.java 459 2015-03-26 14:12:49Z mole $
 */
public class CheckNames {
	
	private static final Log log = LogFactory.getLog(CheckNames.class);
	
	public static final String VERSION = "0.1.0";
	
	public static final String ACTION_HARVEST = "harvest";
	public static final String ACTION_VALIDATE = "validate";
	
	public static void main(String[] args) {

        log.debug("Harvest: version " + VERSION + " starting.");
        
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        options.addOption("taxon",true,"Taxon to validate or to harvest children of.");
        options.addOption("infile",true,"File containing taxon names to validate.  Assumes a csv file, first three columns being dbpk, scientificname, authorship, columns after the third are ignored.  Used only in validation.");
        options.addOption("action",true,"Action to take, validate or harvest.");
        options.getOption("action").setRequired(true);
        options.addOption("service",true,"Webservice to invoke, WoRMS, IPNI, IF, GBIF, or COL, default is WoRMS.  GBIF uses the GBIF backbone taxonomy, COL uses the annual snapshot of COL in GBIF.  Not all services have both validation and harvest available.");
        options.addOption("help",false,"Help message.");
        
        String start = "Plumatellida";
        String service = "WoRMS";
        String action = CheckNames.ACTION_VALIDATE;
        String inputFileName = null;
        
        int resultCode = 0;
        
        try {
        	CommandLine cmd = parser.parse(options, args);
        	if (cmd.hasOption("help")) { 
        		throw new ParseException("help specified");
        	}
        	if (cmd.hasOption("taxon")) { 
        		start = cmd.getOptionValue("taxon");
        	}
        	if (cmd.hasOption("infile")) { 
        		inputFileName = cmd.getOptionValue("infile");
        	}
        	if (cmd.hasOption("service")) { 
        		service = cmd.getOptionValue("service");
        	}
        	if (cmd.hasOption("action")) { 
        		if (cmd.getOptionValue("action").equalsIgnoreCase("harvest")) { 
        			action = CheckNames.ACTION_HARVEST;
        		} else if (cmd.getOptionValue("action").equalsIgnoreCase("validate")) { 
        			action = CheckNames.ACTION_VALIDATE;
        	        if (cmd.hasOption("infile")) {
        	        	log.error("-action harvest does not use infile, -taxon specifies starting point for harvest.");
        	        	log.error("-infile  " + cmd.getOptionValue("infile") + " will be ignored." );
        	        }
        		} else { 
        			throw new ParseException("unrecognized option to -action given: -action {validate|harvest}.");
        		}
        	} else { 
        		throw new ParseException("Option -action {validate|harvest} is required.");
        	}
        } catch (ParseException e) {
        	log.error(e.getMessage());
        	HelpFormatter formatter = new HelpFormatter();
        	formatter.printHelp( "CheckNames version " + VERSION, "Validate taxon names from a specified source authority or\nharvest a snapshot of them from that authority.\nErrors are written out to nametools.log.\nHarvest output to {gbif|worm}harvest.csv.\n", options, "Specify -action and -taxon to validate or harvest.", true );
        	System.exit(2);    
        }

        log.debug("Service: " + service);
        log.debug("Taxon: " + start);
        log.debug("Action: " + action);
        String gbif_checklist = GBIFDataSource.KEY_GBIFBACKBONE;

        if (action.equals(CheckNames.ACTION_VALIDATE)) {
        	// Validate
        	// Step 1: Load names to validate (one from command line option, or list from file).
        	ArrayList<NameUsage> targets = new ArrayList<NameUsage>();
    		if (inputFileName!=null) { 
    			File infile = new File(inputFileName);
    			if (infile.exists() && infile.canRead())  { 
    				log.debug("Reading from " + inputFileName);
    				try {
    					
    					BufferedReader reader = new BufferedReader(new FileReader(infile));
    					CsvListReader csvReader = new CsvListReader(reader,CsvPreference.STANDARD_PREFERENCE);
    					
    					List<String> line;
    					while ((line=csvReader.read())!=null) {
    						if (line.get(1)!=null && line.get(1).length()>0) {
    							try { 
    							NameUsage usage = new NameUsage();
    							usage.setScientificName(line.get(1));
    							usage.setInputDbPK(Integer.parseInt(line.get(0)));
    							usage.setAuthorship(line.get(2));
    							targets.add(usage);
    							} catch (NumberFormatException e) { 
    								// probably header line 
    								log.debug(e.getMessage());
    							}
    						}
    					}
    					csvReader.close();
    					reader.close();
    				} catch (FileNotFoundException e) {
    					log.error(e.getMessage());
    				} catch (IOException e) {
    					log.error(e.getMessage());
    				} 
    			}
    		}  else { 
    			NameUsage usage = new NameUsage();
    			org.gbif.nameparser.NameParser nameParser = new NameParser();
    			try {
					ParsedName parse = nameParser.parse(start);
					usage.setAuthorship(parse.getAuthorship());
					usage.setScientificName(parse.getScientificName().replaceAll(parse.getAuthorship()+"$", ""));
				} catch (UnparsableException e) {
    			    usage.setScientificName(start);
				}
    			targets.add(usage);
    		}
    		// Validate
    		// Step 2: Validate name(s) in list against services
    		Iterator<NameUsage> validationIterator = targets.iterator(); 
        	switch (service.toLowerCase()) 
        	{ 
        	case "ipni":
        		IPNIDataSource ipni;
        		try {
        			ipni = new IPNIDataSource();
        			System.out.println(NameUsage.briefCsvHeaderLine());
        			while (validationIterator.hasNext()) { 
        				NameUsage toValidate = validationIterator.next();
        				NameUsage result = ipni.validate(toValidate);
        				if (result!=null) {
        					if (result.getScientificName().equals(toValidate)) {
        						System.out.println(result.toBriefCsvLine());
        						log.debug(result.getGuid());
        					} else { 
        						System.out.println(result.toBriefCsvLine());
        						log.debug("Not matched");
        					}
        				} else { 
        					toValidate.setMatchDescription("Not Found");
        					log.debug("Not found");
        					System.out.println(toValidate.toBriefCsvLine());
        				}
        			}
        		} catch (CurationException e2) {
        			log.error(e2.getMessage());
        		}
        		break;
        	case "if":
        		IFDataSource ifds;
				try {
					ifds = new IFDataSource();
					System.out.println(NameUsage.briefCsvHeaderLine());
					while (validationIterator.hasNext())  { 
						NameUsage toValidate = validationIterator.next();
						NameUsage result = ifds.validate(toValidate);
						if (result!=null) {
							System.out.println(result.toBriefCsvLine());
							if (result.getScientificName().equals(toValidate)) {
								log.debug(result.getGuid());
							} else { 
								log.debug("Not matched");
							}
						} else { 
							toValidate.setMatchDescription("Not Found");
							log.debug("Not found");
							System.out.println(toValidate.toBriefCsvLine());
						}        		       
					}
				} catch (IOException e1) {
					log.error(e1.getMessage(),e1);
				}
        		break;
        	case "col":	
                gbif_checklist = GBIFDataSource.KEY_COL;
        	case "gbif":
        		GBIFDataSource gbif;
        		try {
        			gbif = new GBIFDataSource(gbif_checklist);
        			System.out.println(NameUsage.briefCsvHeaderLine());
        			while (validationIterator.hasNext()) { 
        				NameUsage toValidate = validationIterator.next();
        				NameUsage result = gbif.validate(toValidate);
        				if (result!=null) {
        					if (result.getScientificName().equals(toValidate)) {
        						System.out.println(result.toBriefCsvLine());
        						log.debug(result.getGuid());
        					} else { 
        						System.out.println(result.toBriefCsvLine());
        						log.debug("Not matched");
        					}
        				} else { 
        					toValidate.setMatchDescription("Not Found");
        					log.debug("Not found");
        					System.out.println(toValidate.toBriefCsvLine());
        				}
        			}
        		} catch (IOException e) {
        			log.error("Unable to connect to WoRMS service.");
        			log.debug(e.getClass().toString() + ":" + e.getMessage());
        			resultCode = 1;
        		}           
        		break;
        	case "worms":
        	default:
        		WoRMSDataSource worms;
        		try {
        			worms = new WoRMSDataSource();
        			System.out.println(NameUsage.briefCsvHeaderLine());
        			while (validationIterator.hasNext()) { 
        				NameUsage toValidate = validationIterator.next();
        				NameUsage result = worms.validate(toValidate);
        				if (result!=null) {
        					if (result.getScientificName().equals(toValidate)) {
        						System.out.println(result.toBriefCsvLine());
        						log.debug(result.getGuid());
        					} else { 
        						System.out.println(result.toBriefCsvLine());
        						log.debug("Not matched");
        					}
        				} else { 
        					toValidate.setMatchDescription("Not Found");
        					log.debug("Not found");
        					System.out.println(toValidate.toBriefCsvLine());
        				}
        			}
        		} catch (IOException e) {
        			log.error("Unable to connect to WoRMS service.");
        			log.debug(e.getClass().toString() + ":" + e.getMessage());
        			resultCode = 1;
        		}           
        		break;
        	}
        } else { 
        	// Harvest
        	switch (service.toLowerCase()) 
        	{ 
        	case "if":
        		System.out.println("Not implemented.");
        		break;
        	case "ipni":
        		System.out.println("Not implemented.");
        		break;
        	case "col":	
                gbif_checklist = GBIFDataSource.KEY_COL;
        	case "gbif":
        		GBIFDataSource cb;
        		try {
        			cb = new GBIFDataSource(gbif_checklist);
        			cb.getAllChildren(start);
        			cb.harvestComplete();
        		} catch (IOException e1) {
        			log.error("Unable to connect to GBIF service.");
        			log.debug(e1.getMessage(),e1);
        			resultCode = 1;
        		}
        		break;
        	case "worms": 
        	default:
        		WoRMSDataSource worms;
        		try {
        			worms = new WoRMSDataSource();
        			worms.getAllChildren(start);
        			worms.harvestComplete();
        		} catch (IOException e) {
        			log.error("Unable to connect to WoRMS service.");
        			log.debug(e.getMessage(),e);
        			resultCode = 1;
        		}           
        	}

        }
		
		log.debug("Done.");
		System.exit(resultCode);
	}

}
