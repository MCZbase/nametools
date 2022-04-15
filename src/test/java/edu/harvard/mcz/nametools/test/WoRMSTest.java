/**
 * WoRMSTest.java
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

import java.io.IOException;

import org.junit.Test;

import edu.harvard.mcz.nametools.NameUsage;
import edu.harvard.mcz.nametools.WoRMSDataSource;

/**
 * @author mole
 *
 */
public class WoRMSTest {

	/**
	 * Test method for {@link edu.harvard.mcz.nametools.WoRMSDataSource#findCitations(edu.harvard.mcz.nametools.NameUsage)}.
	 */
	@Test
	public void testFindCitations() {
		WoRMSDataSource service;
		try {
			service = new WoRMSDataSource();
		    NameUsage toCheck = new NameUsage();
		    toCheck.setScientificName("Agaricia rugosa");
		    toCheck.setAuthorship("Lamarck, 1801");
		    service.findCitations(toCheck);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
