/**
 * NameUsageTest.java
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
package edu.harvard.mcz.nametools.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import edu.harvard.mcz.nametools.GBIFDataSource;
import edu.harvard.mcz.nametools.NameUsage;

/**
 * @author mole
 *
 */
public class NameUsageTest {

	/**
	 * Test method for {@link edu.harvard.mcz.nametools.NameUsage#fixAuthorship(java.lang.String)}.
	 */
	@Test
	public void testFixAuthorship() {
		NameUsage nu = new NameUsage();
		nu.setKingdom("Animalia");
		nu.setAuthorship("Gibson (1882)");
		nu.fixAuthorship();
		assertEquals("(Gibson, 1882)",nu.getAuthorship());
		
		nu.setKingdom("Plantae");
		nu.setAuthorship("Gibson (1882)");
		nu.fixAuthorship();
		assertEquals("Gibson (1882)",nu.getAuthorship());
	}
	
	@Test
	public void testCreate() { 
		String json = "{\"offset\":0,\"limit\":20,\"endOfRecords\":true,\"results\":[{\"key\":2231030,\"nubKey\":2231030,\"taxonID\":\"119548319\",\"kingdom\":\"Animalia\",\"phylum\":\"Arthropoda\",\"order\":\"Lithobiomorpha\",\"family\":\"Henicopidae\",\"genus\":\"Zygethobius\",\"species\":\"Zygethobius dolichopus\",\"kingdomKey\":1,\"phylumKey\":54,\"classKey\":360,\"orderKey\":807,\"familyKey\":6182,\"genusKey\":2231025,\"speciesKey\":2231030,\"datasetKey\":\"d7dddbf4-2cf0-4f39-9b2a-bb099caae36c\",\"parentKey\":2231025,\"parent\":\"Zygethobius\",\"scientificName\":\"Zygethobius dolichopus (Chamberlin, 1902)\",\"canonicalName\":\"Zygethobius dolichopus\",\"authorship\":\" (Chamberlin, 1902)\",\"nameType\":\"WELLFORMED\",\"rank\":\"SPECIES\",\"origin\":\"SOURCE\",\"taxonomicStatus\":\"ACCEPTED\",\"nomenclaturalStatus\":[],\"accordingTo\":\"The Catalogue of Life, 3rd January 2011\",\"numDescendants\":0,\"modified\":\"2013-02-08T03:15:41.847+0000\",\"lastInterpreted\":\"2014-11-03T15:22:26.302+0000\",\"issues\":[],\"synonym\":false,\"class\":\"Chilopoda\"},{\"key\":119548319,\"nubKey\":2231030,\"taxonID\":\"3343505\",\"kingdom\":\"Animalia\",\"phylum\":\"Arthropoda\",\"order\":\"Lithobiomorpha\",\"family\":\"Henicopidae\",\"genus\":\"Zygethobius\",\"species\":\"Zygethobius dolichopus\",\"kingdomKey\":140821094,\"phylumKey\":140844030,\"classKey\":140888159,\"orderKey\":140888415,\"familyKey\":140888418,\"genusKey\":140888437,\"speciesKey\":119548319,\"datasetKey\":\"7ddf754f-d193-4cc9-b351-99906754a03b\",\"constituentKey\":\"7b3586a9-d9bc-46b5-844b-f12d8f15581f\",\"parentKey\":140888437,\"parent\":\"Zygethobius\",\"scientificName\":\"Zygethobius dolichopus Chamberlin (1902)\",\"canonicalName\":\"Zygethobius dolichopus\",\"authorship\":\" (Chamberlin, 1902)\",\"nameType\":\"SCINAME\",\"rank\":\"SPECIES\",\"origin\":\"SOURCE\",\"taxonomicStatus\":\"ACCEPTED\",\"nomenclaturalStatus\":[],\"accordingTo\":\"Edgecombe G.\",\"numDescendants\":0,\"references\":\"http://www.catalogueoflife.org/annual-checklist/details/species/id/3343505\",\"lastCrawled\":\"2015-02-27T16:48:49.660+0000\",\"lastInterpreted\":\"2015-02-28T02:42:04.303+0000\",\"issues\":[],\"synonym\":false,\"class\":\"Chilopoda\"}]}";
		List<NameUsage> hits = GBIFDataSource.parseAllNameUsagesFromJSON(json);
		assertEquals(2,hits.size());
		NameUsage nu = hits.get(0);
		assertEquals("Zygethobius dolichopus",nu.getCanonicalName());
		assertEquals("(Chamberlin, 1902)", nu.getAuthorship());
		nu = hits.get(1);
		assertEquals("Zygethobius dolichopus",nu.getCanonicalName());
		assertEquals("(Chamberlin, 1902)", nu.getAuthorship());
	}

}
