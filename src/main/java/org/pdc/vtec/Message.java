package org.pdc.vtec;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 *  VTEC Message - Object Model
 */
public class Message implements Serializable
{
  private static Logger logger = LoggerFactory.getLogger(Message.class);
	


private String vtec = "";
	
  private String pVtecProductClass = "";
  private String pVtecActions = "";
  private String pVtecOfficeId = "";
  private String pVtecPhenomena = "";
  private String pVtecSignificance = "";
  private String pVtecEventTrackingNumber="";
  private String pVtecBeginDate="";
  private String pVtecEndDate="";
  
  private String hVtecNwsLocationId="";
  private String hVtecFloodSeverity="";
  private String hVtecBeginDate="";
  private String hVtecFloodCrestDate="";
  private String hVtecEndDate="";
  private String hVtecImmediateCause="";
  private String hVtecFloodRecordStatus="";

  private boolean havePvtec = false;
  private boolean haveHvtec = false;

	
	public Message(String rawPVTECoptionalHTVEC)
	{
		vtec =rawPVTECoptionalHTVEC; 
		digest();
	}
	
	private void digest()
	{
		  if (sanityCheckVtec(vtec))
		  {
			  logger.info("Parsing VTEC " + vtec + ", number of tokens when split by forward slash = " + StringUtils.split(vtec, "/").length);

			  String [] vtecArr = StringUtils.split(vtec, "/");
			  
			  
			  // count non-empty
			  List<String> nonEmpty= new ArrayList <String>();
			  for (int i = 0; i < vtecArr.length; i++)
			  {
				  if (StringUtils.isNotBlank(vtecArr[i])) nonEmpty.add(vtecArr[i]);
			  }
			  
			  
			  if (nonEmpty.size() > 0)
			  {
				  // P-VTEC
				  logger.info("P-VTEC...parse BEGIN");
				  // clean-up vtec 
				  vtec = "/"+StringUtils.trimToEmpty(nonEmpty.get(0))+"/";
				  
				  String [] pelements = StringUtils.split(nonEmpty.get(0), ".");
				  
				  pVtecProductClass = getVtec("vtec.product_class.properties",pelements[0]); logger.info("pVtec.Product.Class = " + pVtecProductClass);
				  pVtecActions      = getVtec("vtec.actions.properties",pelements[1]); logger.info("pVtec.Actions = " + pVtecActions);
				  pVtecOfficeId     = pelements[2]; logger.info("pVtec.Office ID = " + pVtecOfficeId);
				  pVtecPhenomena    = getVtec("vtec.phenomena.properties",pelements[3]); logger.info("pVtec.Phenomena = " + pVtecPhenomena);
				  pVtecSignificance = getVtec("vtec.significance.properties",pelements[4]); logger.info("pVtec.Significance= " + pVtecSignificance);
				  pVtecEventTrackingNumber = pelements[5]; logger.info("pVtec.Event Tracking # = " + pVtecEventTrackingNumber);
				  
				  String [] dateElements = StringUtils.split(pelements[6], "-");
				  
				  pVtecBeginDate    = dateElements[0]; logger.info("pVTEC Begin Date = " + pVtecBeginDate);
				  pVtecEndDate      = dateElements[1]; logger.info("pVTEC End  Date = " + pVtecEndDate);
				  
				  havePvtec = true;
			      if (nonEmpty.size() > 1)
			      {
					  // H-VTEC

			    	  logger.info("Hydro-VTEC...parse BEGIN");
					  // clean-up vtec - add hydro after space
					  vtec += " /" + StringUtils.trimToEmpty(nonEmpty.get(1)) + "/";

			    	  String [] helements = StringUtils.split(nonEmpty.get(1), ".");

					  
					  hVtecNwsLocationId  =  helements[0]; logger.info("Location ID " + hVtecNwsLocationId);
					  hVtecFloodSeverity  =  getVtec("vtec.flood_severity.properties",helements[1]); logger.info("Flood Severity " + hVtecFloodSeverity);
					  hVtecImmediateCause =  getVtec("vtec.immediate_cause.properties",helements[2]); logger.info("Immediate Cause " + hVtecImmediateCause);
					  
					  hVtecBeginDate      = helements[3];  logger.info("Begin Date " + hVtecBeginDate);
					  hVtecFloodCrestDate = helements[4];  logger.info("Crest Date " + hVtecFloodCrestDate);
					  hVtecEndDate        = helements[5];  logger.info("End Date " + hVtecEndDate);
					  hVtecFloodRecordStatus = getVtec("vtec.flood_record_status.properties",helements[6]); logger.info("Flood Record Status " + hVtecFloodRecordStatus);
					  haveHvtec = true;
			     }
			  }
			  
		  }
		  else
		  {
			  logger.info("VTEC codes are empty or possibly corrupted " + vtec + ".... skipped parse ");
		  }

	}
	
	
	  private boolean sanityCheckVtec(String str)
	  {
		  boolean pass = true;
		  
		  // empty 
		  if (StringUtils.isBlank(str))
		  {
			  logger.info("VTEC value Empty - nothing to parse");
			  return false;
		  }
		  
		  // must have slash or dash delimiters 
		  if (StringUtils.contains(str, "/") && StringUtils.contains(str, "-"))
		  {
			  //ok
		  }
		  else
		  {
			  logger.info("VTEC value may be corrupt - no expected delimiters - forward slashes or dashes.");
			  return false;
		  }
		  
		  // split by slash should result in exactly two tokens
		  
		  // count non-empty
		  String [] vtecArr = StringUtils.split(str, "/");
		  List<String> nonEmpty= new ArrayList <String>();
		  
		  for (int i = 0; i < vtecArr.length; i++)
		  {
			  if (StringUtils.isNotBlank(vtecArr[i])) nonEmpty.add(vtecArr[i]);
		  }
		  
		  if (nonEmpty.size() == 1 || nonEmpty.size() == 2)
		  {
			  // ok
		  }
		  else
		  {
			  logger.info("VTEC does not split by forward slash delimiter into one or two non-empty fragments - will not parse");
			  return false; 
		  }
		  
		  String [] pelements = StringUtils.split(nonEmpty.get(0), ".");
		  if (pelements.length != 7)
		  {
            logger.info("P-VTEC does not have exactly 7 tokens when split by dot");			  
			  return false;
		  }
		  
		  String [] dateElements = StringUtils.split(pelements[6], "-");
		  if (dateElements.length != 2)
		  {
            logger.info("P-VTEC date elements does not have exactly 2 tokens when split by dash");			  
			  return false;
		  }
		  
		  if (nonEmpty.size() == 2)
		  {
		    String [] helements = StringUtils.split(nonEmpty.get(1), ".");
		    if (helements.length != 7)
		    {
            logger.info("Hydro H-VTEC does not have exactly 7 tokens when split by dot");			  
			  return false;
		    }
		  }
		  
		  // more sophsticated hurdles can be put here to ensure dates
		  
		  
		  return pass;
	  }

	  
	  
	  private String getVtec(String propertiesFile, String key)
	  {
		  PropertiesConfiguration config = null;
		  try
		  {
			   config = new PropertiesConfiguration(propertiesFile); 
		  }
		  catch (org.apache.commons.configuration.ConfigurationException e)
		  {
			  
		  }
		  
		  
		  if (config == null)  return key;
		  
		  
		  return config.getString(key, key);
		  
		  
	  }

	  public String getVtec() {
			return vtec;
		}

		public void setVtec(String vtec) {
			this.vtec = vtec;
		}

		public String getpVtecProductClass() {
			return pVtecProductClass;
		}

		public void setpVtecProductClass(String pVtecProductClass) {
			this.pVtecProductClass = pVtecProductClass;
		}

		public String getpVtecActions() {
			return pVtecActions;
		}

		public void setpVtecActions(String pVtecActions) {
			this.pVtecActions = pVtecActions;
		}

		public String getpVtecOfficeId() {
			return pVtecOfficeId;
		}

		public void setpVtecOfficeId(String pVtecOfficeId) {
			this.pVtecOfficeId = pVtecOfficeId;
		}

		public String getpVtecPhenomena() {
			return pVtecPhenomena;
		}

		public void setpVtecPhenomena(String pVtecPhenomena) {
			this.pVtecPhenomena = pVtecPhenomena;
		}

		public String getpVtecSignificance() {
			return pVtecSignificance;
		}

		public void setpVtecSignificance(String pVtecSignificance) {
			this.pVtecSignificance = pVtecSignificance;
		}

		public String getpVtecEventTrackingNumber() {
			return pVtecEventTrackingNumber;
		}

		public void setpVtecEventTrackingNumber(String pVtecEventTrackingNumber) {
			this.pVtecEventTrackingNumber = pVtecEventTrackingNumber;
		}

		public String getpVtecBeginDate() {
			return pVtecBeginDate;
		}

		public void setpVtecBeginDate(String pVtecBeginDate) {
			this.pVtecBeginDate = pVtecBeginDate;
		}

		public String getpVtecEndDate() {
			return pVtecEndDate;
		}

		public void setpVtecEndDate(String pVtecEndDate) {
			this.pVtecEndDate = pVtecEndDate;
		}

		public String gethVtecNwsLocationId() {
			return hVtecNwsLocationId;
		}

		public void sethVtecNwsLocationId(String hVtecNwsLocationId) {
			this.hVtecNwsLocationId = hVtecNwsLocationId;
		}

		public String gethVtecFloodSeverity() {
			return hVtecFloodSeverity;
		}

		public void sethVtecFloodSeverity(String hVtecFloodSeverity) {
			this.hVtecFloodSeverity = hVtecFloodSeverity;
		}

		public String gethVtecBeginDate() {
			return hVtecBeginDate;
		}

		public void sethVtecBeginDate(String hVtecBeginDate) {
			this.hVtecBeginDate = hVtecBeginDate;
		}

		public String gethVtecFloodCrestDate() {
			return hVtecFloodCrestDate;
		}

		public void sethVtecFloodCrestDate(String hVtecFloodCrestDate) {
			this.hVtecFloodCrestDate = hVtecFloodCrestDate;
		}

		public String gethVtecEndDate() {
			return hVtecEndDate;
		}

		public void sethVtecEndDate(String hVtecEndDate) {
			this.hVtecEndDate = hVtecEndDate;
		}

		public String gethVtecImmediateCause() {
			return hVtecImmediateCause;
		}

		public void sethVtecImmediateCause(String hVtecImmediateCause) {
			this.hVtecImmediateCause = hVtecImmediateCause;
		}

		public String gethVtecFloodRecordStatus() {
			return hVtecFloodRecordStatus;
		}

		public void sethVtecFloodRecordStatus(String hVtecFloodRecordStatus) {
			this.hVtecFloodRecordStatus = hVtecFloodRecordStatus;
		}

		public boolean isHavePvtec() {
			return havePvtec;
		}

		public void setHavePvtec(boolean havePvtec) {
			this.havePvtec = havePvtec;
		}

		public boolean isHaveHvtec() {
			return haveHvtec;
		}

		public void setHaveHvtec(boolean haveHvtec) {
			this.haveHvtec = haveHvtec;
		}


}
