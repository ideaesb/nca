package org.pdc.nca;

import org.pdc.nca.dao.*;

public class DatabaseCache 
{

	// for hibernate use true
	EntryDao dao = new EntryDao(false);
	
	  
    ///////////////
	  // Plain JDBC callers 
	  public boolean put(Entry entry)
	  {
		  return dao.tryAdd(entry);
	  }


	  public int getSize() 
	  {
	    return dao.getCount();
	  }

	  
	  /**
	   * This will insert feed entry into cache - use with Hibernate 
	   * @param entry to be inserted into cache
	   * @return true if accepted into cache, false if already there
	   */
	  /*
	  public Entry get(String key) 
	  {
		  return dao.getEntryById(key);
	  }
	  public boolean put(Entry entry)
	  {
		  if (get(entry.getKey()) == null)
		  {
			 dao.addEntry(entry);
			 return true;
		  }
		  else
		  {
			  return false;
		  }
	  }


	  public int getSize() {
	    return dao.getAllEntrys().size();
	  }
      */
	
}
