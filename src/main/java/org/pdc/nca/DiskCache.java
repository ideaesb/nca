package org.pdc.nca;


import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * Warpper for disk cache - Enterprise edition of Ehcache.org 
 * @author Uday
 *
 */
public class DiskCache 
{

	  private static final CacheManager  cacheManager  = new CacheManager();
	  
	  int m_tti = 60*60*24*7;
	  int m_ttl = 60*60*24*30;
	  
	  /**
	   * construct with class cache parameters time to Idle (tti) and time to Live (ttl) 
	   * @param tti
	   * @param ttl
	   */
	  public DiskCache(int tti, int ttl) 
	  {
	    m_tti = tti; m_ttl = ttl;  
	  }

	  public Entry get(String key) 
	  {
	    Element elem = getCache().get(key);
	    return (Entry) elem.getObjectValue();
	  }
	  
	  
	  /**
	   * This will insert feed entry into cache 
	   * @param entry to be inserted into cache
	   * @return true if accepted into cache, false if already there
	   */
	  public boolean put(Entry entry)
	  {
		  if (getCache().isKeyInCache(entry.getKey()))
		  {
			  return false;
		  }
		  else
		  {
			  // actually no need to actually put element, just keys will do...
			  Element elem = new Element(entry.getKey(), null, m_tti, m_ttl);
			  getCache().put(elem);
			  return true;
		  }
	  }


	  public long getTTL() {
	    return getCache().getCacheConfiguration().getTimeToLiveSeconds();
	  }

	  public long getTTI() {
	    return getCache().getCacheConfiguration().getTimeToIdleSeconds();
	  }

	  public int getSize() {
	    return getCache().getSize();
	  }

	  private Ehcache getCache() {
	    return cacheManager.getEhcache("nwscapatom");
	  }

}
