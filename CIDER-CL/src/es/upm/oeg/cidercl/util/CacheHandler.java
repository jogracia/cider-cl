package es.upm.oeg.cidercl.util;

import org.apache.jcs.JCS;
import org.apache.log4j.Logger;

/** 
 * Class to manipulate cache (essentially initialize it) 
 * 
 * @author OEG group (Universidad Politécnica de Madrid) and SID group (Universidad de Zaragoza)
 *
 */
public class CacheHandler {
	
	private static Logger log = Logger.getLogger(CacheHandler.class);

	
	//Initialize the cache
	public static JCS initializeCache(String typeCache) {		
	
		JCS cache = null;
			try {
				cache = JCS.getInstance(typeCache);	           
			}catch (Exception e) {	       
				log.error("Problem initializing cache for region name [" + typeCache + "]." + e.toString() );
			}		 
		return cache;
	}
		

	

}
