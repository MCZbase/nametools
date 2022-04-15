package edu.harvard.mcz.nametools.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.harvard.mcz.nametools.ICNafpAuthorNameComparator;
import edu.harvard.mcz.nametools.ICZNAuthorNameComparator;
import edu.harvard.mcz.nametools.NameComparison;

public class TestICNafpAuthorNameComparator {

    private ICNafpAuthorNameComparator comparator;
	
	@Before
	public void setUp() throws Exception {
		comparator = new ICNafpAuthorNameComparator(.75d,.5d);
	}

	@Test
	public void testCompare() {
		assertEquals(NameComparison.MATCH_ERROR, comparator.compare(null, "Smith").getMatchType());
		assertEquals(NameComparison.MATCH_ERROR, comparator.compare("Smith", null).getMatchType());
		assertEquals(NameComparison.MATCH_ERROR, comparator.compare(null, null).getMatchType());
		
		assertEquals(NameComparison.MATCH_ADDSAUTHOR, comparator.compare("", "Conrad").getMatchType());
		
		assertEquals(NameComparison.MATCH_PARENTHESIESDIFFER, comparator.compare("Schilder","(Schilder)").getMatchType()); 
		assertEquals(NameComparison.MATCH_PARENTHESIESDIFFER, comparator.compare("(Schilder)","Schilder").getMatchType()); 
		assertEquals(NameComparison.MATCH_PARTSDIFFER, comparator.compare("Schilder","(Schilder) Jones").getMatchType()); 
		assertEquals(NameComparison.MATCH_PARTSDIFFER, comparator.compare("Fries; Fries","Fries: Fries").getMatchType()); 
		assertEquals(NameComparison.MATCH_PARTSDIFFER, comparator.compare("Fries, Fries","Fries: Fries").getMatchType()); 
		assertEquals(NameComparison.MATCH_PARTSDIFFER, comparator.compare("Fries ex Fries","Fries: Fries").getMatchType()); 
		assertEquals(NameComparison.MATCH_PARTSDIFFER, comparator.compare("(Persoon) P. Kummer: Fries","(Persoon) Kummer, Fries").getMatchType()); 
		
		assertEquals(NameComparison.MATCH_AUTHSIMILAR, comparator.compare("(Persoon) P. Kummer: Fries","(Persoon) Kummer: Fries").getMatchType());
		assertEquals(NameComparison.MATCH_AUTHSIMILAR, comparator.compare("(J. C. Schmidt) Coker & Beers ex Pouzar: Fries","(Schmidt) Coker & Beers ex Pouzar: Fries").getMatchType());
		
		assertEquals(NameComparison.MATCH_EXACT, comparator.compare("Conrad ", "Conrad").getMatchType());
		assertEquals(NameComparison.MATCH_EXACT, comparator.compare("J.C. Schmidt", "J. C. Schmidt").getMatchType());
	}

	@Test
	public void testTokenizeAuthorship() {
		assertEquals(0,ICNafpAuthorNameComparator.tokenizeAuthorship("").size());
		assertEquals(0,ICNafpAuthorNameComparator.tokenizeAuthorship(null).size());
		assertEquals(1,ICNafpAuthorNameComparator.tokenizeAuthorship("Smith").size());
		assertEquals(2,ICNafpAuthorNameComparator.tokenizeAuthorship("(Jones) Smith").size());
		assertEquals(3,ICNafpAuthorNameComparator.tokenizeAuthorship("(Jones ex George) Smith").size());
		assertEquals(4,ICNafpAuthorNameComparator.tokenizeAuthorship("(Jones ex George) Smith ex Tome").size());
		
		assertEquals(4,ICNafpAuthorNameComparator.tokenizeAuthorship("(Schultz Bipontinus ex Seemann) Bentham & Hooker f. ex Hemsley").size());
		assertEquals(4,ICNafpAuthorNameComparator.tokenizeAuthorship("(Schultz Bipontinus ex A. Richard) Bentham & Hooker f. ex Vatke").size());
		assertEquals(4,ICNafpAuthorNameComparator.tokenizeAuthorship("(Ehrenberg ex Fries) Léveillé: Fries").size());
		assertEquals(3,ICNafpAuthorNameComparator.tokenizeAuthorship("(Persoon) P. Kummer: Fries").size());
		assertEquals(3,ICNafpAuthorNameComparator.tokenizeAuthorship("(Persoon) Kummer: Fries").size());
		assertEquals(3,ICNafpAuthorNameComparator.tokenizeAuthorship("(Fries) Patouillard & Hariot: Fries").size());
		assertEquals(3,ICNafpAuthorNameComparator.tokenizeAuthorship("(Linnaeus: Fries) Redhead, Lutzoni, Moncalvo & Vilgalys").size());
		assertEquals(3,ICNafpAuthorNameComparator.tokenizeAuthorship("(Tode: Fries) De Notaris").size());
		assertEquals(2,ICNafpAuthorNameComparator.tokenizeAuthorship("Fries: Fries").size());
		assertEquals(2,ICNafpAuthorNameComparator.tokenizeAuthorship("(G. Don) Exell").size());
	}

}
