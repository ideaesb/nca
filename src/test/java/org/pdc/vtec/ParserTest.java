package org.pdc.vtec;

import junit.framework.TestCase;

public class ParserTest extends TestCase 
{

	public void testTwoPVtecsBackToBack()
	{
        String vtec = "/O.UPG.KPIH.FW.A.0007.130815T1800Z-130817T0300Z/ /O.NEW.KPIH.FW.W.0019.130815T1800Z-130817T0300Z/";
        Parser vtecParser = new Parser(vtec); 
        assertEquals(2, vtecParser.getMessages().size()); // recognize these are two P-VTECs
	}
	public void testPVtecHVtecCombo()
	{
        String vtec = "/O.NEW.KAMA.FA.Y.0053.130815T0056Z-130815T0300Z/" + System.getProperty("line.separator") +" /00000.N.ER.000000T0000Z.000000T0000Z.000000T0000Z.OO/";
        Parser vtecParser = new Parser(vtec); 
        assertEquals(1, vtecParser.getMessages().size()); // recognize this as a single VTEC with embedded hydrological 
	}
}
