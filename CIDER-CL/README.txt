    CIDER-CL, a system for monolingual and cross-lingual ontology alignment
   	
   	Copyright (C) 2011-2013  OEG group Universidad Politécnica de Madrid
 	Copyright (C) 2008-2011  SID group University of Zaragoza
	
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see [http://www.gnu.org/licenses/].

---------------
Features
---------------
1. It can operate in two modes: 
    a) SIMILARITY computation between two ontology terms (using SoftTFIDFBetweenOntologyEntities.java or CLESABetweenOntologyEntities.java classes, for the monolingual/crosslingual case respectively)
    b) ALIGNMENT between two ontologies (using Aligner.java class) 
2. It aligns classes and properties but not instances
3. This aligner is not intended to operate with large ontologies
4. Minumum recommended threshold is 0.0025 

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
- You can find an example of usage in the class SimilarityBetweenURIsTest.java

-----------------------------------------------------------------
 Example of ALIGNMENT between ontologies (command line)
 (without alignment between instances, and with threshold=0.0025) 
-----------------------------------------------------------------
java -jar cidercl.jar -i es.upm.oeg.cidercl.aligner.Aligner -o ./test/CIDER_test_output.rdf file:./test/onto1.owl file:./test/onto2.owl -Dnoinst=1 -t 0.0025

