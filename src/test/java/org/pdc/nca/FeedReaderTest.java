package org.pdc.nca;

import java.io.*;

import junit.framework.TestCase; 

public class FeedReaderTest extends TestCase 
{
   /**
    * rudimentary blackbox test - look at two mock feeds, feed1 has one new and one duplicate
    */
   public void testReader()
   {
	   String args [] = {"c:/jwork/nca/testData/feed0.xml"}; 
	   FeedReader.main(args);
	   
	   int n = (new File("c:/jwork/nca/candidateHazards")).listFiles().length;
	   
	   // now process feed1.xml
	   args [0] = "c:/jwork/nca/testData/feed1.xml";
	   FeedReader.main(args);
	   
	   int m = (new File("c:/jwork/nca/candidateHazards")).listFiles().length;
	   
	   assertEquals(1, m-n);
   }
}
