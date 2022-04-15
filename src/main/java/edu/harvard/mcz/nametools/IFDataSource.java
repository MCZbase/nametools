/** 
 * IFDataSource.java 
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

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.axis.message.MessageElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;

import org.indexfungorum.cabi.fungusserver.FungusSoapProxy;
import org.indexfungorum.cabi.fungusserver.NameSearchResponseNameSearchResult;

/**
 * @author mole
 *
 */
public class IFDataSource implements Validator, Harvester {
	
	private static final Log log = LogFactory.getLog(IFDataSource.class);

	private FungusSoapProxy ifService;
	protected AuthorNameComparator authorNameComparator;
	
	public IFDataSource() throws IOException { 
		init();
	}
	
	protected void init() throws IOException { 
		ifService = new FungusSoapProxy();
		log.debug(ifService.getEndpoint());
		URL test = new URL(ifService.getEndpoint());
		URLConnection conn = test.openConnection();
		conn.connect();
		authorNameComparator = new ICNafpAuthorNameComparator(.75d,.5d);
	}
	
	/* (non-Javadoc)
	 * @see edu.harvard.mcz.nametools.Harvester#getAllChildren(java.lang.String)
	 */
	@Override
	public void getAllChildren(String taxon) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.nametools.Harvester#harvestComplete()
	 */
	@Override
	public void harvestComplete() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.nametools.Validator#validate(java.lang.String)
	 */
	@Override
	public NameUsage validate(NameUsage taxonNameUsage) {
		NameUsage result = null;
		String taxonName = taxonNameUsage.getScientificName();
		try {
			NameSearchResponseNameSearchResult searchResult = ifService.nameSearch(taxonName, false, 2);
			if (searchResult!=null) { 
				List<MessageElement> mes = Arrays.asList(searchResult.get_any());
				log.debug(mes.size());
				Iterator<MessageElement> i = mes.iterator();
				while (i.hasNext()) { 
					MessageElement me = i.next();
					log.debug(me);
					Iterator<MessageElement> it = me.getChildElements();
					while (it.hasNext()) { 
						MessageElement mei = it.next();
						log.debug(mei.getChildElement(new QName("NAME_x0020_OF_x0020_FUNGUS")).getValue());
						String name = mei.getChildElement(new QName("NAME_x0020_OF_x0020_FUNGUS")).getValue();
						if (name.equals(taxonName)) {
							String authorship = mei.getChildElement(new QName("AUTHORS")).getValue();
							if (authorship!=null && authorship.equals(taxonNameUsage.getAuthorship())) { 
								String uuid = mei.getChildElement(new QName("UUID")).getValue();
								String recnum = mei.getChildElement(new QName("RECORD_x0020_NUMBER")).getValue();
								String lsid = "urn:lsid:indexfungorum.org:names:" + recnum;
								log.debug("\"" + taxonName + "\", \"urn:uuid:" + uuid + "\",\"" + lsid +  "\"");
								result = new NameUsage("IndexFungorum",authorNameComparator);
								result.setScientificName(taxonName);
								result.setKey(Integer.parseInt(recnum));
								result.setGuid(lsid);
								result.setSourceID("urn:uuid:"+ uuid);
								result.setAuthorship(authorship);
								result.setMatchDescription(NameComparison.MATCH_EXACT);
								result.setAuthorshipStringEditDistance(1d);
								result.setInputDbPK(taxonNameUsage.getInputDbPK());
								result.setOriginalScientificName(taxonNameUsage.getScientificName());
								result.setOriginalAuthorship(taxonNameUsage.getAuthorship());
							} else { 
								double similarity = AuthorNameComparator.calulateSimilarityOfAuthor(taxonNameUsage.getAuthorship(), authorship);
								log.debug(similarity);
								if (similarity>.75d) { 
									String uuid = mei.getChildElement(new QName("UUID")).getValue();
									String recnum = mei.getChildElement(new QName("RECORD_x0020_NUMBER")).getValue();
								    String lsid = "urn:lsid:indexfungorum.org:names:" + recnum;
									log.debug("\"" + taxonName + "\", \"urn:uuid:" + uuid + "\",\"" + lsid +  "\"");
									result = new NameUsage("IndexFungorum",authorNameComparator);
									result.setScientificName(taxonName);
									result.setKey(Integer.parseInt(recnum));
									result.setGuid(lsid);
									result.setSourceID("urn:uuid:"+ uuid);
									result.setAuthorship(authorship);
									result.setMatchDescription(NameComparison.MATCH_AUTHSIMILAR);
									result.setAuthorshipStringEditDistance(similarity);
								    result.setInputDbPK(taxonNameUsage.getInputDbPK());
								    result.setOriginalScientificName(taxonNameUsage.getScientificName());
								    result.setOriginalAuthorship(taxonNameUsage.getAuthorship());
								} 
							}
						}
					}
					
				}
			}
		} catch (RemoteException e) {
			log.error(e.getMessage(),e);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}

}
