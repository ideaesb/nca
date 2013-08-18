package org.pdc.nca;

import org.pdc.nca.dao.*;

public class DatabaseCache 
{

	EntryDao dao = new EntryDao();
	
	  public Entry get(String key) 
	  {
		  return dao.getEntryById(key);
	  }
	  
	  /**
	   * This will insert feed entry into cache 
	   * @param entry to be inserted into cache
	   * @return true if accepted into cache, false if already there
	   */
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

	
}
