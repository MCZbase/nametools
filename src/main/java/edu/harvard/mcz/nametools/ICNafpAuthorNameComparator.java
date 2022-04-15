/**
 * ICZNAuthorNameComparator.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Make comparisons between pairs of scientificNameAuthor strings, under the assumption
 * that both strings are from names covered by the botanical code.
 * 
 * @author mole
 *
 */
public class ICNafpAuthorNameComparator extends AuthorNameComparator {
	
	private static final Log log = LogFactory.getLog(ICNafpAuthorNameComparator.class);
	
	/**
	 *  Threshold of similarity (0-1) over which strong similarity is asserted.
	 */
	protected double similarityThreshold = .75d;
	/**
	 *  Threshold of similarity (0-1) over which a weak similarity is asserted.
	 */
    protected double weakThreshold = .5d;
	
    /**
     * Constructor for an ICNapf author name comparator that specifies values for similarity
     * assertions.
     * 
     * @param similarityThreshold in range 0 to 1 for marking comparisons as similar
     * @param weakThreshold in range 0 to 1 for marking comparisons as weakly similar
     */
    public ICNafpAuthorNameComparator(double similarityThreshold, double weakThreshold) { 
    	this.similarityThreshold = similarityThreshold;
    	this.weakThreshold = weakThreshold;
    }
    
	/* (non-Javadoc)
	 * @see edu.harvard.mcz.nametools.AuthorNameComparator#compare(java.lang.String, java.lang.String)
	 */
	@Override
	public NameComparison compare(String anAuthor, String toOtherAuthor) {
		
		NameComparison result = new NameComparison(anAuthor, toOtherAuthor);
		
        result.setMatchType(NameComparison.MATCH_ERROR);
		if (anAuthor==null || toOtherAuthor==null) {
		    result.setMatchType(NameComparison.MATCH_ERROR);
		} else { 
			if (anAuthor.equals(toOtherAuthor) 
					|| anAuthor.toLowerCase().replaceAll("[ .,]", "").equals(toOtherAuthor.toLowerCase().replaceAll("[ .,]", ""))) 
			{ 
				result.setMatchType(NameComparison.MATCH_EXACT);
			} else {
				if (anAuthor.length()==0 && toOtherAuthor.length()> 0 ) { 
					result.setMatchType(NameComparison.MATCH_ADDSAUTHOR);
				} else { 
					NameUsage test = new NameUsage();
					test.setAuthorship(anAuthor);
					double similarity = AuthorNameComparator.calulateSimilarityOfAuthor(anAuthor, toOtherAuthor);
					result.setSimilarity(similarity);
					if (similarity > similarityThreshold) { 
						result.setMatchType(NameComparison.MATCH_AUTHSIMILAR);
					} else { 
						result.setMatchType(NameComparison.MATCH_DISSIMILAR);
					}
					double similarityAlpha = AuthorNameComparator.calulateSimilarityOfAuthorAlpha(anAuthor, toOtherAuthor);
					boolean parenSame = ICNafpAuthorNameComparator.calculateHasParen(anAuthor)==ICNafpAuthorNameComparator.calculateHasParen(toOtherAuthor);
					
					
					List<String> anAuthorBits = tokenizeAuthorship(anAuthor);
					List<String> toOtherAuthorBits = tokenizeAuthorship(toOtherAuthor);
					if (anAuthorBits.size() != toOtherAuthorBits.size()) { 
						result.setMatchType(NameComparison.MATCH_PARTSDIFFER);
					}
					
					if (anAuthor.contains(" ex ") && !toOtherAuthor.contains(" ex ")) { 
						result.setMatchType(NameComparison.MATCH_PARTSDIFFER);
					}
					if (anAuthor.contains(":") && !toOtherAuthor.contains(":")) { 
						result.setMatchType(NameComparison.MATCH_PARTSDIFFER);
					}
					if (anAuthor.contains("(") && !toOtherAuthor.contains("(")) { 
						result.setMatchType(NameComparison.MATCH_PARTSDIFFER);
					}
					
					if (!parenSame && (similarityAlpha==1d)) { 
						result.setMatchType(NameComparison.MATCH_PARENTHESIESDIFFER);
					}
				}
			}
		}
		return result;
	}	
	
	
	/**
	 * Given a botanical authorship string, split it into a list of component 
	 * authors on parenthetical authors, ex authors, and sanctioning authors.
	 * 
	 * @param authorship
	 * @return a list of authorship strings representing the components of the 
	 * authorship string.
	 */
     public static List<String> tokenizeAuthorship(String authorship) { 
		ArrayList<String> bits = new ArrayList<String>();
		ArrayList<String> subbits = new ArrayList<String>();
		ArrayList<String> result = new ArrayList<String>();
		
		if (authorship==null || authorship.length()==0) { 
			return result;
		}
		
		// separate out parenthetical author
		log.debug(authorship);
		if (authorship.matches("^\\(.*\\).+$")) { 
			String[] parBits = authorship.split("\\)");
			log.debug(parBits.length);
			log.debug(parBits[0]);
			bits.add(parBits[0].replaceFirst("^\\(", ""));
			bits.add(parBits[1]);
		} else { 
			bits.add(authorship);
		}

		// separate out ex author
		Iterator<String> i = bits.iterator();
		while (i.hasNext()) { 
			String bit = i.next();
			if (bit.contains(" ex ")) { 
				String[] exBits = bit.split(" ex ");
				log.debug(exBits.length);
				log.debug(exBits[0]);
				log.debug(exBits[1]);
				subbits.add(exBits[0]);
				subbits.add(exBits[1]);
			} else { 
				log.debug(bit);
				subbits.add(bit);
			}

		}

		// separate out sanctioning author
		Iterator<String> ir = subbits.iterator();
		while (ir.hasNext()) { 
			String bit = ir.next();
			if (bit.contains(":")) { 
				String[] exBits = bit.split(":");
				result.add(exBits[0]);
				result.add(exBits[1]);
			} else { 
				result.add(bit);
			}

		}		
		
		return result;
	}
}
