package nexus_http;

/**
 * This is a static collection of attributes that may be used in the project(s)
 * @author Mikko Hilpinen
 * @since 10.10.2015
 */
public class HttpSettings
{
	// ATTRIBUTES	-------------------
	
	private static String ip = "";
	private static int port = 8080;
	
	
	// CONSTRUCTOR	-------------------
	
	private HttpSettings()
	{
		// Static interface
	}
	
	
	// ACCESSORS	-------------------
	
	/**
	 * Updates the server ip and port values
	 * @param ip The server ip
	 * @param port The server port
	 */
	public static void setServerStatus(String ip, int port)
	{
		HttpSettings.ip = ip;
		HttpSettings.port = port;
	}
	
	/**
	 * @return the ip of the server
	 */
	public static String getIp()
	{
		return ip;
	}

	/**
	 * @return the port of the server
	 */
	public static int getPort()
	{
		return port;
	}
	
	
	// OTHER METHODS	--------------
	
	/**
	 * @return A string with both server ip and port
	 */
	public static String getServerString()
	{
		return getIp() + ":" + getPort();
	}
}
