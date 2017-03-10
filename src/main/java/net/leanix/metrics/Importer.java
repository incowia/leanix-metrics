package net.leanix.metrics;

import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;
import net.leanix.api.BusinessCapabilitiesApi;
import net.leanix.api.common.ApiClient;
import net.leanix.api.common.ApiClientBuilder;
import net.leanix.api.common.ApiException;
import net.leanix.api.models.BusinessCapability;

/**
 * import of leanIX to metrics
 */
public class Importer 
{
	private final static Logger LOGGER = Logger.getLogger(Importer.class.getName());
	
	private String host;

	private String getHost() {
		return host;
	}

	private void setHost(String host) {
		this.host = host;
	}
	
	private String workspace;

	private String getWorkspace() {
		return workspace;
	}

	private void setWorkspace(String workspace) {
		this.workspace = workspace;
	}
	
	private String token;

	private String getToken() {
		return token;
	}

	private void setToken(String token) {
		this.token = token;
	}
	
	private boolean debug = false;
	
	private boolean isDebug() {
		return debug;
	}

	private void setDebug(boolean debug) {
		this.debug = debug;
	}

	private ApiClient apiClient;
     
    
	public static void main( String[] args )
    {
		Hashtable<String,String> readedArgs = readArguments(args);
		Importer importer = new Importer();
		setProperties(readedArgs, importer);
		
		importer.apiClients();
		importer.readBusinessCapabilities();
    }

	private static Hashtable<String,String> readArguments(String[] args) {
		Hashtable<String,String> argsList = new Hashtable<String,String>();
		if(args.length != 4)
		{
			LOGGER.warning("no args or not enough args!");
			return null;
		}
		else
		{
			for(int i=0; i<args.length; i++)
			{
				String[] arg = args[i].split("=");
				argsList.put(arg[0],arg[1]);
			}
		}
		if(argsList.containsKey("HOST") && argsList.containsKey("WORKSPACE") && argsList.containsKey("TOKEN") && argsList.containsKey("DEBUG"))
		{
			return argsList;
		}
		else
		{
			LOGGER.warning("no the right args!");
			return null;
		}
	}
	
	/**
	 * @param readedArgs
	 * @param importer
	 */
	private static void setProperties(Hashtable<String, String> readedArgs, Importer importer) {
		if(readedArgs == null)
		{
			return;
		}
		else
		{
			importer.setHost(readedArgs.get("HOST"));
			importer.setWorkspace(readedArgs.get("WORKSPACE"));
			importer.setToken(readedArgs.get("TOKEN"));
			if(readedArgs.get("DEBUG").equals("true")){
				importer.setDebug(true);
			}
		}
	}
    
	/**
	 * create clients for access to leanIX and metrics
	 */
    private void apiClients()
    {
    	ApiClientBuilder apiClientBuilder = new ApiClientBuilder();
    	apiClient = apiClientBuilder
    				.withBasePath(String.format("https://%s/%s/api/v1", getHost(), getWorkspace()))
    				.withApiToken(getToken())
    			    .withTokenProviderHost(getHost())
    			    .withDebugging(isDebug())
    			    .build();
    }
    
    /**
     * read the businessCapabilities
     */
    private void readBusinessCapabilities()
    {
    	BusinessCapabilitiesApi businessCapabilitiesApi = new BusinessCapabilitiesApi(apiClient);
    	List<BusinessCapability> businessCapabilities;
		try {
			businessCapabilities = businessCapabilitiesApi.getBusinessCapabilities(false, null);
			for (BusinessCapability businessCapability : businessCapabilities) {
	    	    if(isDebug())
	    	    {
	    	    	LOGGER.info(businessCapability.toString());
	    	    }
	    	}
		} catch (ApiException e) {
			LOGGER.warning("The call to read the businesscapabilities is canceled, because it is a error: " + e.getMessage());
		}
    }
}