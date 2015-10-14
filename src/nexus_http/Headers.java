package nexus_http;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Headers is a simple collection of request / response headers
 * @author Mikko Hilpinen
 * @since 8.10.2015
 */
public class Headers
{
	// ATTRIBUTES	---------------------
	
	/**
	 * The name of the header that contains the parameter encoding information
	 */
	public static String parameterEncodingHeaderName = "parameterEncoding";
	/**
	 * The name of the header that describes a (created) resource location
	 */
	public static final String LOCATION = "Location";
	/**
	 * This header is sent along with the 401 status
	 * https://en.wikipedia.org/wiki/Basic_access_authentication
	 */
	public static final String AUTHENTICATE = "WWW-Authenticate";
	/**
	 * This header contains the authorization information (basic is username:password)
	 * User RFC2045-MIME variant of Base64 when encoding / decoding
	 */
	public static final String AUTHORIZATION = "Authorization";
	/**
	 * This header describes, which methods are allowed for a resource
	 */
	public static final String ALLOW= "Allow";
	/**
	 * The type of content in the response body
	 */
	public static final String CONTENT_TYPE = "Content-Type";
	/**
	 * The content types accepted by the client
	 */
	public static final String ACCEPT = "Accept";
	
	private Map<String, String> headers;
	private Map<String, String> originalCasing;
	private AcceptHeader acceptHeader;
	
	
	// CONSTRUCTOR	---------------------
	
	/**
	 * Creates a new empty header set
	 */
	public Headers()
	{
		this.headers = new HashMap<>();
		this.originalCasing = new HashMap<>();
		this.acceptHeader = null;
	}
	
	/**
	 * Creates a copy of another headers entity
	 * @param other The headers that will be copied
	 */
	public Headers(Headers other)
	{
		this.headers = new HashMap<>();
		this.headers.putAll(other.headers);
		this.originalCasing = new HashMap<>();
		this.originalCasing.putAll(other.originalCasing);
		this.acceptHeader = other.acceptHeader;
	}
	
	
	// IMPLEMENTED METHODS	------------
	
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		for (String headerName : getHeaderNames())
		{
			s.append("\n" + headerName + " = ");
			s.append(getHeaderValue(headerName));
		}
		
		return s.toString();
	}

	
	// OTHER METHODS	----------------
	
	/**
	 * Finds the value given to a header (case-insensitive)
	 * @param headerName The name of the header
	 * @return The value of the header or null if there is no such header
	 */
	public String getHeaderValue(String headerName)
	{
		return this.headers.get(headerName.toLowerCase());
	}
	
	/**
	 * Adds a new header to the set
	 * @param headerName The name of the header
	 * @param headerValue The value of the header
	 */
	public void addHeader(String headerName, String headerValue)
	{
		this.headers.put(headerName.toLowerCase(), headerValue);
		this.originalCasing.put(headerName.toLowerCase(), headerName);
	}
	
	/**
	 * Checks if there is a header with the given name (case-insensitive)
	 * @param headerName The name of the header
	 * @return Is there a value for the provided header
	 */
	public boolean containsHeader(String headerName)
	{
		return this.headers.containsKey(headerName.toLowerCase());
	}
	
	/**
	 * @return The names of the headers in this set
	 */
	public Collection<String> getHeaderNames()
	{
		return this.originalCasing.values();
	}
	
	/**
	 * @return The desired parameter encoding or null if not specified
	 */
	public String getParameterEncoding()
	{
		return getHeaderValue(parameterEncodingHeaderName);
	}
	
	/**
	 * Updates the location header
	 * @param location The header value
	 */
	public void setLocation(Path location)
	{
		addHeader(LOCATION, location.toString());
	}
	
	/**
	 * Parses the basic authorization header
	 * @return A table containing the userName and password or null if there was no header or 
	 * if the authorization isn't basic.
	 */
	public String[] getBasicAuthorization()
	{
		String authHeaderValue = getHeaderValue(AUTHORIZATION);
		
		if (authHeaderValue != null)
		{
			int cutLenght = "Basic".length();
			if (authHeaderValue.length() < cutLenght || !authHeaderValue.substring(0, 
					cutLenght).equalsIgnoreCase("Basic"))
				return null;
			
			// Basic authorization uses base64 encoding
			String base64Credentials = authHeaderValue.substring(cutLenght).trim();
	        String credentials = new String(Base64.getDecoder().decode(base64Credentials),
	                Charset.forName("UTF-8"));
	        
	        // The user name and password are separated with ':'
	        return credentials.split("\\:", 2);
		}
		
		return null;
	}
	
	/**
	 * Adds a new basic authorization header
	 * @param userName The username
	 * @param password The password
	 */
	public void setBasicAuthorization(String userName, String password)
	{
		String credentials = userName + ":" + password;
		String base64Credentials = new String(Base64.getEncoder().encode(
				credentials.getBytes()));
		addHeader(AUTHORIZATION, "Basic " + base64Credentials);
	}
	
	/**
	 * Adds an authentication challenge to the headers
	 */
	public void setBasicAuthenticationChallenge()
	{
		addHeader(AUTHENTICATE, "Basic realm='myRealm'");
	}
	
	/**
	 * Adds an allow parameter with the provided list of methods
	 * @param allowed The allowed methods
	 */
	public void setAllowedMethods(Method... allowed)
	{
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < allowed.length; i++)
		{
			if (i != 0)
				s.append(", ");
			s.append(allowed[i]);
		}
		
		addHeader(ALLOW, s.toString());
	}
	
	/**
	 * Adds the content type header
	 * @param type The type of the response content
	 */
	public void setContentType(ContentType type)
	{
		addHeader(CONTENT_TYPE, type.toString());
	}
	
	/**
	 * @return The accept header provided by the client
	 */
	public AcceptHeader getAcceptHeader()
	{
		if (this.acceptHeader == null)
			this.acceptHeader = new AcceptHeader();
		
		return this.acceptHeader;
	}
	
	
	// SUBCLASSES	----------------------
	
	/**
	 * The accept -header provided by the client. The header can be used for narrowing down 
	 * the possible content-types
	 * @author Mikko Hilpinen
	 * @since 14.10.2015
	 */
	public class AcceptHeader
	{
		// ATTRIBUTES	------------------
		
		private Map<String, Double> acceptedTypes;
		
		
		// CONSTRUCTOR	------------------
		
		/**
		 * Sets a new accept header that accepts the provided content types
		 * @param acceptedTypes The content types accepted, each mapped to a priority ]0, 1]
		 */
		public AcceptHeader(Map<ContentType, Double> acceptedTypes)
		{
			this.acceptedTypes = new HashMap<>();
			StringBuilder headerValue = new StringBuilder();
			
			boolean isFirst = true;
			for (ContentType type : acceptedTypes.keySet())
			{
				double priority = acceptedTypes.get(type);
				this.acceptedTypes.put(type.toString(), priority);
				
				if (!isFirst)
					headerValue.append(", ");
				headerValue.append(type.toString());
				headerValue.append("; q=");
				headerValue.append(priority);
			}
			
			addHeader(ACCEPT, headerValue.toString());
		}
		
		/**
		 * Creates a new set of acceptHeaders
		 */
		private AcceptHeader()
		{
			this.acceptedTypes = new HashMap<>();
			
			// Parses the headers (if possible)
			String headerValue = getHeaderValue(ACCEPT);
			if (headerValue != null)
			{
				String[] contentTypes = headerValue.split("\\,");
				for (String contentTypeString : contentTypes)
				{
					String[] typeAndArgs = contentTypeString.split("\\;");
					String contentType = typeAndArgs[0].trim();
					if (typeAndArgs.length < 2)
						this.acceptedTypes.put(contentType, 1.0);
					else
					{
						for (int i = 1; i < typeAndArgs.length; i++)
						{
							String[] argumentAndValue = typeAndArgs[i].split("\\=");
							String argument = argumentAndValue[0].trim();
							if (argument.equals("q") && argumentAndValue.length > 1)
							{
								this.acceptedTypes.put(contentType, Double.parseDouble(
										argumentAndValue[1].trim()));
								break;
							}
						}
					}
				}
			}
		}
		
		
		// OTHER METHODS	----------------------
		
		/**
		 * Finds the content type most preferred by the client
		 * @param availableTypes The content types the service is willing to provide
		 * @return The content type the client prefers. Null if the client doesn't accept 
		 * any of the content types.
		 */
		public ContentType getPrefferedContentType(Collection<ContentType> availableTypes)
		{
			ContentType best = null;
			double bestPriority = -1;
			
			for (ContentType type : availableTypes)
			{
				String typeString = type.toString();
				if (this.acceptedTypes.containsKey(typeString))
				{
					double priority = this.acceptedTypes.get(typeString);
					if (priority == 1)
						return type;
					else if (priority > bestPriority)
					{
						best = type;
						bestPriority = priority;
					}
				}
			}
			
			return best;
		}
	}
}
