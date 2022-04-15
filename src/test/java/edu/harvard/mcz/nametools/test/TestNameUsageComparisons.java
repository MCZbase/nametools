package edu.harvard.mcz.nametools.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.harvard.mcz.nametools.NameUsage;

public class TestNameUsageComparisons {

	@Test
	public void testCalculateHasYear() {
		assertTrue(NameUsage.calculateHasYear("Smith, 1882"));
		assertTrue(NameUsage.calculateHasYear("(Smith, 1882)"));
		assertTrue(NameUsage.calculateHasYear("1882"));
		
		assertFalse(NameUsage.calculateHasYear("Smith"));
		assertFalse(NameUsage.calculateHasYear("Smith, 188"));
		assertFalse(NameUsage.calculateHasYear("(Smith, 188)"));
		assertFalse(NameUsage.calculateHasYear("188"));
		assertFalse(NameUsage.calculateHasYear("188414314"));
		assertFalse(NameUsage.calculateHasYear(Integer.toString(Integer.MAX_VALUE)));
		assertFalse(NameUsage.calculateHasYear(Integer.toString(Integer.MIN_VALUE)));
	}
	
	@Test
	public void testCalculateHasParen() { 
		assertTrue(NameUsage.calculateHasParen("(Smith, 1882)"));
		assertTrue(NameUsage.calculateHasParen("(1882)"));
		assertTrue(NameUsage.calculateHasParen("()"));
		assertFalse(NameUsage.calculateHasParen("Smith, 1888"));
		assertFalse(NameUsage.calculateHasParen("(Smith, 1888"));
		assertFalse(NameUsage.calculateHasParen("(Smith, 1888))"));
	}
	
	@Test
	public void testCompare() { 
		assertEquals(NameUsage.MATCH_ERROR, NameUsage.compare(null, "Smith"));
		assertEquals(NameUsage.MATCH_ERROR, NameUsage.compare("Smith", null));
		assertEquals(NameUsage.MATCH_ERROR, NameUsage.compare(null, null));
		
		assertEquals(NameUsage.MATCH_EXACT, NameUsage.compare("Smith, 1880","Smith, 1880"));
		assertEquals(NameUsage.MATCH_EXACT, NameUsage.compare("(Smith, 1880)","(Smith, 1880)"));
		
		assertEquals(NameUsage.MATCH_EXACT, NameUsage.compare("C.B. Adams, 1852","C. B. Adams, 1852"));
		
		assertEquals(NameUsage.MATCH_EXACTADDSYEAR, NameUsage.compare("(Smith)","(Smith, 1880)"));
		assertEquals(NameUsage.MATCH_EXACTADDSYEAR, NameUsage.compare("Reeve","Reeve, 1843"));
		assertEquals(NameUsage.MATCH_EXACTMISSINGYEAR, NameUsage.compare("(Smith, 1880)","(Smith)"));
		
		assertEquals(NameUsage.MATCH_WEAKEXACTYEAR, NameUsage.compare("(Von Born, 1778)", "(Born, 1778)"));
		
		assertEquals(NameUsage.MATCH_SIMILAREXACTYEAR, NameUsage.compare("(v Born, 1778)", "(Born, 1778)"));
		assertEquals(NameUsage.MATCH_SIMILAREXACTYEAR, NameUsage.compare("Ferussac, 1822", "Férussac, 1822"));
		
		assertEquals(NameUsage.MATCH_EXACTDIFFERENTYEAR, NameUsage.compare("(Carpenter, 1864)", "(Carpenter, 1857)"));
		
		assertEquals(NameUsage.MATCH_ADDSAUTHOR, NameUsage.compare("", "(Conrad, 1833)"));
		
		assertEquals(NameUsage.MATCH_PARENTHESIESDIFFER, NameUsage.compare("Schilder, 1922","(Schilder, 1922)")); 
		
		NameUsage.compare("Myers and D'Attilio","Myers & D'Attilio, 1990"); 
		NameUsage.compare("Watson","(Watson, 1881)"); 
		NameUsage.compare("L.","Linnaeus, 1758"); 
		NameUsage.compare("Linné","Linnaeus, 1758"); 
		NameUsage.compare("L.","Lamarck"); 
		NameUsage.compare("(Hornung & Mermod, 1925)", "Hornung & Mermod, 1924");
		assertEquals(NameUsage.MATCH_SOWERBYEXACTYEAR, NameUsage.compare("(Sowerby, 1833)", "(G. B. Sowerby I, 1833)"));
		assertEquals(NameUsage.MATCH_SOWERBYEXACTYEAR, NameUsage.compare("Sowerby, 1860", "G. B. Sowerby II, 1860"));
		assertEquals(NameUsage.MATCH_SOWERBYEXACTYEAR, NameUsage.compare("Sowerby, 1892", "G. B. Sowerby III, 1892"));
	}

}
