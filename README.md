# nametools
Old, mostly obsoleted by filteredpush/sci_name_qc library for working with scientific names data.

Build with

	mvn clean package

Run with

	$ java -jar target/nameTools-0.1.6-jar-with-dependencies.jar -h
	usage: CheckNames version 0.1.6 -action <arg> [-help] [-infile <arg>]
	       [-service <arg>] [-taxon <arg>]
	Validate taxon names from a specified source authority or
	harvest a snapshot of them from that authority.
	Errors are written out to nametools.log.
	Harvest output to {gbif|worm}harvest.csv.
	 -action <arg>    Action to take, validate or harvest.
	 -help            Help message.
	 -infile <arg>    File containing taxon names to validate.  Assumes a csv
	                  file, first three columns being dbpk, scientificname,
	                  authorship, columns after the third are ignored.  Used
	                  only in validation.
	 -service <arg>   Webservice to invoke, WoRMS, IPNI, IF, ZooBank,
	                  WoRMS+ZooBank, GBIF, or COL, default is WoRMS+ZooBank.
	                  GBIF uses the GBIF backbone taxonomy, COL uses the
	                  annual snapshot of COL in GBIF.  Not all services have
	                  both validation and harvest available.
	 -taxon <arg>     Taxon to validate or to harvest children of.
	Specify -action and -taxon to validate or harvest
