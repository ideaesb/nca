package org.pdc.vtec;

import java.util.regex.*;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * VTEC Parser
 *
 */
public class Parser 
{
	private static Logger logger = LoggerFactory.getLogger(Parser.class);
	
	private String bulletin = "";
	private String vtecPattern = "[/][OTEX][.](NEW|CON|EXT|EXA|EXB|UPG|CAN|EXP|COR|ROU)[.][\\w]{4}[.][A-Z][A-Z][.][WAYSFON][.][0-9]{4}[.][0-9]{6}[T][0-9]{4}[Z][-][0-9]{6}[T][0-9]{4}[Z][/]([^/]*[/][\\w]{5}[.][[N0-3U]][.][A-Z][A-Z][.][0-9]{6}[T][0-9]{4}[Z][.][0-9]{6}[T][0-9]{4}[Z][.][0-9]{6}[T][0-9]{4}[Z][.](NO|NR|UU|OO)[/])?";
	List<Message> messages =  new ArrayList<Message>();
	
	
	
	public Parser(String stringContainingVTEC)
	{
		bulletin = stringContainingVTEC;
		digest();
	}
	
	
	
	
	public List<Message> getMessages()
	{
		return messages;
	}
	
	
	private void digest()
	{
		logger.info("Digesting VTEC " + bulletin);
		
		Pattern pattern = Pattern.compile(vtecPattern);
		logger.info("Applying pattern " + pattern.pattern());

		Matcher matcher = pattern.matcher(bulletin);
		
		while (matcher.find())
		{
			String matchedText = matcher.group();
			logger.info("Found VTEC " + matchedText);
			messages.add(new Message(matchedText));
		}

		if(matcher.hitEnd()) logger.info("Regex reached end of string to be parsed ");
	}
	
    public static void main( String[] args )
    {
    	String vtec = "/O.UPG.KPIH.FW.A.0007.130815T1800Z-130817T0300Z/ /O.NEW.KPIH.FW.W.0019.130815T1800Z-130817T0300Z/";
        Parser vtecParser = new Parser(vtec); 
    }
}
