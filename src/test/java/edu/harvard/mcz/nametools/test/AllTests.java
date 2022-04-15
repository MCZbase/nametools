package edu.harvard.mcz.nametools.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	GBIFHarvesterTest.class, 
	NameUsageTest.class,
	TestICZNAuthorNameComparator.class, 
	TestICNafpAuthorNameComparator.class 
	})
public class AllTests {

}
