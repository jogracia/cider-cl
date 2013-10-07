--------------------
 Requirements
--------------------
- Java version 1.6 

--------------------
 Install
--------------------
- Just unzip the installation file into a convenient directory

----------------------------------------------------
 Example of SIMILARITY computation between URIs
----------------------------------------------------
- You can find an example of usage in the class SimilarityBetweenURIsTest.java (in the 'test' folder) 

-----------------------------------------------------------------
 Example of ALIGNMENT between ontologies (command line)
 (without alignment between instances, and with threshold=0.0025) 
-----------------------------------------------------------------
java -jar cidercl.jar -i es.upm.oeg.cidercl.aligner.Aligner -o ./test/CIDER_test_output.rdf file:./test/onto1.owl file:./test/onto2.owl -Dnoinst=1 -t 0.0025

---------------
Final Remarks
---------------
1. It can operate in two modes: 
    a) SIMILARITY computation between two ontology terms (using SoftTFIDFBetweenOntologyEntities.java or CLESABetweenOntologyEntities.java classes, for the monolingual/crosslingual case respectively)
    b) ALIGNMENT between two ontologies (using Aligner.java class) 
2. It aligns classes and properties but not instances
3. This aligner is not intended to operate with large ontologies
4. Minumum recommended threshold is 0.0025 