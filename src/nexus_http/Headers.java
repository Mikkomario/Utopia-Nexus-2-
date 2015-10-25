package nexus_http;

import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nexus_rest.ResourceWriter.LinkWriteStyle;

/**
 * Headers is a simple collection of request / response headers
 * @author Mikko Hilpinen
 * @since 8.10.2015
 */
public class Headers
{
	// http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.23
	
	// ATTRIBUTES	---------------------
	
	/**
	 * The name of the header that contains the parameter encoding information
	 */
	public static String parameterEncodingHeaderName = "ParameterEncoding";
	/**
	 * The name of the header that affects the link write style
	 */
	public static String linkWriteStyleHeaderName = "LinkStyle";
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
	/**
	 * The character sets / encodings the client accepts
	 */
	public static final String ACCEPT_CHARSET = "Accept-Charset";
	/**
	 * The name of the cookie header (cookies are provided by the client (browser))
	 */
	public static final String COOKIE = "Cookie";
	/**
	 * The server's set-cookie header that is used when a new cookie is set
	 */
	public static final String SET_COOKIE = "Set-Cookie";
	
	private Map<String, List<String>> headers;
	private Map<String, String> originalCasing;
	private Map<String, AcceptHeader> acceptHeaders;
	private Map<String, Cookie> cookies;
	
	
	// CONSTRUCTOR	---------------------
	
	/**
	 * Creates a new empty header set
	 */
	public Headers()
	{
		this.headers = new HashMap<>();
		this.originalCasing = new HashMap<>();
		this.acceptHeaders = new HashMap<>();
	}
	
	/**
	 * Creates a copy of another headers entity
	 * @param other The headers that will be copied
	 */
	public Headers(Headers other)
	{
		this.headers = new HashMap<>();
		for (String headerName : other.headers.keySet())
		{
			List<String> values = new ArrayList<>();
			values.addAll(other.headers.get(headerName));
			this.headers.put(headerName, values);
		}
		
		this.originalCasing = new HashMap<>();
		this.originalCasing.putAll(other.originalCasing);
		
		this.acceptHeaders = new HashMap<>();
		for (AcceptHeader acceptHeader : other.acceptHeaders.values())
		{
			setAcceptHeader(new AcceptHeader(acceptHeader));
		}
	}
	
	
	// IMPLEMENTED METHODS	------------
	
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		for (String headerName : getHeaderNames())
		{
			for (String headerValue : getHeaderValues(headerName))
			{
				s.append("\n" + headerName + " = ");
				s.append(headerValue);
			}
		}
		
		return s.toString();
	}

	
	// OTHER METHODS	----------------
	
	/**
	 * Finds the first value of a header (case-insensitive)
	 * @param headerName The name of the header
	 * @return The first value of the header or null if there is no such header
	 * @see #getHeaderValues(String)
	 */
	public String getHeaderValue(String headerName)
	{
		List<String> values = this.headers.get(headerName.toLowerCase());
		if (values == null)
			return null;
		else
			return values.get(0);
	}
	
	/**
	 * Finds all the values assigned to a header name (case-insensitive)
	 * @param headerName The name of the header
	 * @return The values assigned to the header / headers. Null if no such header exists. 
	 * The returned collection is just a copy. Changes made to it won't affect the header 
	 * values.
	 * @see #getHeaderValue(String)
	 */
	public List<String> getHeaderValues(String headerName)
	{
		List<String> values = this.headers.get(headerName.toLowerCase());
		if (values == null)
			return null;
		else
		{
			List<String> copy = new ArrayList<>();
			copy.addAll(values);
			return copy;
		}
	}
	
	/**
	 * Adds a new header to the set. This doesn't overwrite any existing headers, a new header 
	 * with the same name is added instead
	 * @param headerName The name of the header
	 * @param headerValue The value of the header
	 * @see #setHeader(String, String)
	 */
	public void addHeader(String headerName, String headerValue)
	{
		List<String> existingValues = this.headers.get(headerName.toLowerCase());
		
		if (existingValues == null)
			setHeader(headerName, headerValue);
		else
			existingValues.add(headerValue);
	}
	
	/**
	 * Adds a new header to the set. If there already exists a header with the given name, 
	 * it is overwritten with this one.
	 * @param headerName The name of the added header
	 * @param headerValue The value of the added header
	 * @see #addHeader(String, String)
	 */
	public void setHeader(String headerName, String headerValue)
	{
		List<String> values = new ArrayList<>();
		values.add(headerValue);
		
		this.headers.put(headerName.toLowerCase(), values);
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
	 * @return The link write style specified in the headers
	 */
	public LinkWriteStyle getLinkWriteStyle()
	{
		return LinkWriteStyle.parseFromString(getHeaderValue(linkWriteStyleHeaderName));
	}
	
	/**
	 * Sets the link write style header
	 * @param style The style of the links in the response
	 */
	public void setLinkWriteStyle(LinkWriteStyle style)
	{
		if (style != null)
			setHeader(linkWriteStyleHeaderName, style.toString());
	}
	
	/**
	 * Updates the location header
	 * @param location The header value
	 */
	public void setLocation(Path location)
	{
		setHeader(LOCATION, location.toString());
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
		setHeader(AUTHORIZATION, "Basic " + base64Credentials);
	}
	
	/**
	 * Adds an authentication challenge to the headers
	 */
	public void setBasicAuthenticationChallenge()
	{
		setHeader(AUTHENTICATE, "Basic realm='myRealm'");
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
		
		setHeader(ALLOW, s.toString());
	}
	
	/**
	 * @return A list of methods set as allowed by the server. Null if no such header exists.
	 */
	public List<Method> getAllowedMethods()
	{
		String headerValue = getHeaderValue(ALLOW);
		if (headerValue == null)
			return null;
		
		List<Method> methods = new ArrayList<>();
		for (String methodPart : headerValue.split("\\,"))
		{
			Method method = Method.parseFromString(methodPart.trim());
			if (method != null && !methods.contains(method))
				methods.add(method);
		}
		
		return methods;
	}
	
	/**
	 * Adds the content type header
	 * @param type The type of the response content
	 */
	public void setContentType(ContentType type)
	{
		setHeader(CONTENT_TYPE, type.toString());
	}
	
	/**
	 * Adds the content type header
	 * @param type The type of the response content
	 * @param charset The character set used with the content type
	 */
	public void setContentType(ContentType type, String charset)
	{
		if (charset == null)
			setContentType(type);
		else
			setHeader(CONTENT_TYPE, type.toString() + "; " + charset);
	}
	
	/**
	 * Finds all the cookies. The cookies -header will be parsed if it hasn't been already.
	 * @return All the cookies in the headers. Null if there was no cookie header.
	 */
	public Collection<Cookie> getCookies()
	{
		parseCookiesIfNecessary();
		if (this.cookies != null)
			return this.cookies.values();
		else
			return null;
	}
	
	/**
	 * Finds a cookie with a specific name. The cookies -header will be parsed if 
	 * it hasn't been already.
	 * @param cookieName The name of the cookie.
	 * @return The cookie with the given name (case-insensitive). Null if there was no 
	 * cookie with the given name.
	 */
	public Cookie getCookie(String cookieName)
	{
		parseCookiesIfNecessary();
		if (this.cookies != null)
			return this.cookies.get(cookieName.toLowerCase());
		else
			return null;
	}
	
	/**
	 * Finds the first value of a header / cookie. If the value can't be found from the 
	 * headers, it is searched from the cookies.
	 * @param headerOrCookieName The name of the header / cookie
	 * @return The first value of a header / cookie with the given name. Null if no such 
	 * header or cookie exists.
	 */
	public String getHeaderOrCookieValue(String headerOrCookieName)
	{
		String value = getHeaderValue(headerOrCookieName);
		if (value == null)
		{
			Cookie cookie = getCookie(headerOrCookieName);
			if (cookie != null)
				value = cookie.getValue();
		}
		
		return value;
	}
	
	/**
	 * Adds a new set-cookie header so that the browser will create a new cookie.
	 * @param cookie The cookie to be added, not null
	 * @param expires The time when the cookie expires. Null if the cookie is a session cookie 
	 * and expires when the user closes the browser.
	 * @param path The path under which the cookie is in effect. Null if the cookie should 
	 * be in effect under the whole domain.
	 * @param httpOnly If a HttpOnly -attribute should be added to disable javascript, etc. 
	 * being able to access the cookie.
	 * @param secure Should the cookie require a secure https connection to be used
	 */
	public void addSetCookie(Cookie cookie, ZonedDateTime expires, Path path, 
			boolean httpOnly, boolean secure)
	{
		if (cookie == null)
			return;
		
		StringBuilder s = new StringBuilder(cookie.toString());
		
		if (expires != null)
			s.append("; " + expires.format(DateTimeFormatter.RFC_1123_DATE_TIME));
		if (path != null)
			s.append("; /" + path.toString());
		if (httpOnly)
			s.append("; HttpOnly");
		if (secure)
			s.append("; Secure");
		
		addHeader(SET_COOKIE, s.toString());
	}
	
	/**
	 * Retrieves an accept header from the headers. Parses the value if necessary.
	 * @param headerName The name of the accept header
	 * @return An accept header provided by the client. The header is parsed when it is 
	 * first requested. Null if no such header exists.
	 */
	public AcceptHeader getAcceptHeader(String headerName)
	{
		String lowerName = headerName.toLowerCase();
		AcceptHeader header = this.acceptHeaders.get(lowerName);
		if (header == null)
		{
			String headerValue = getHeaderValue(headerName);
			if (headerValue != null)
			{
				header = new AcceptHeader(headerName, headerValue);
				this.acceptHeaders.put(lowerName, header);
			}
		}
		
		return header;
	}
	
	/**
	 * @return The content type accept header. The value is parsed when first requested. 
	 * Null if no accept (content type) header exists.
	 */
	public AcceptHeader getAcceptContentTypeHeader()
	{
		return getAcceptHeader(ACCEPT);
	}
	
	/**
	 * @return The charSet accept header. The value is parsed when first requested. 
	 * Null if no accept-charset -header exists.
	 */
	public AcceptHeader getAcceptCharsetHeader()
	{
		return getAcceptHeader(ACCEPT_CHARSET);
	}
	
	/**
	 * Adds a new accept header to the headers
	 * @param header The header to add
	 */
	public void setAcceptHeader(AcceptHeader header)
	{
		if (header == null)
			return;
		
		String headerName = header.getName();
		String lowerName = headerName.toLowerCase();
		
		this.acceptHeaders.put(lowerName, header);
		setHeader(headerName, header.parseHeaderValue());
	}
	
	private void parseCookiesIfNecessary()
	{
		if (this.cookies == null)
		{
			List<String> headerValues = getHeaderValues(COOKIE);
			if (headerValues == null)
				return;
			
			this.cookies = new HashMap<>();
			for (String headerValue : headerValues)
			{
				for (String cookieString : headerValue.split("\\,"))
				{
					Cookie cookie = new Cookie(cookieString);
					this.cookies.put(cookie.getName().toLowerCase(), cookie);
				}
			}
		}
	}
	
	
	// SUBCLASSES	----------------------
	
	/**
	 * Cookies are created by the server and stored by the client browser. Each cookie 
	 * contains a name value pair. (In some cases the value can be omitted though).
	 * @author Mikko Hilpinen
	 * @since 18.10.2015
	 */
	public static class Cookie
	{
		// ATTRIBUTES	------------------
		
		private String name, value;
		
		
		// CONSTRUCTOR	------------------
		
		/**
		 * Creates a new cookie
		 * @param name The name of the cookie
		 * @param value The value assigned to the cookie. Null if no value should be assigned.
		 */
		public Cookie(String name, String value)
		{
			this.name = name;
			this.value = value;
		}
		
		private Cookie(String cookieString)
		{
			String[] parts = cookieString.split("\\=", 1);
			if (parts.length > 0)
				this.name = parts[0].trim();
			else
				this.name = null;
			if (parts.length > 1)
				this.value = parts[1].trim();
			else
				this.value = null;
		}
		
		
		// IMPLEMENTED METHODS	----------
		
		@Override
		public String toString()
		{
			if (getName() == null)
				return "";
			
			StringBuilder s = new StringBuilder(getName());
			if (getValue() != null)
			{
				s.append("=");
				s.append(getValue());
			}
			
			return s.toString();
		}
		
		
		// ACCESSORS	-------------------
		
		/**
		 * @return The name of the cookie
		 */
		public String getName()
		{
			return this.name;
		}
		
		/**
		 * @return The value assigned to the cookie
		 */
		public String getValue()
		{
			return this.value;
		}
	}
	
	/**
	 * The accept -header provided by the client. The header can be used for narrowing down 
	 * the possible content-types
	 * @author Mikko Hilpinen
	 * @since 14.10.2015
	 */
	public static class AcceptHeader
	{
		// ATTRIBUTES	------------------
		
		private Map<String, Double> acceptedTypes;
		private String headerName;
		
		
		// CONSTRUCTOR	------------------
		
		/**
		 * Sets a new accept header that accepts the provided content types
		 * @param headerName The name of the accept header. May be 'Accept', 'Accept-CharSet', 
		 * etc.
		 * @param acceptedTypes The content types accepted, each mapped to a priority ]0, 1]
		 */
		public AcceptHeader(String headerName, Map<String, Double> acceptedTypes)
		{
			this.headerName = headerName;
			this.acceptedTypes = new HashMap<>();
			this.acceptedTypes.putAll(acceptedTypes);
		}
		
		/**
		 * Creates a new accept header by copying the value from another
		 * @param other The accept header the values are copied from
		 */
		public AcceptHeader(AcceptHeader other)
		{
			this.acceptedTypes = new HashMap<>();
			this.acceptedTypes.putAll(other.acceptedTypes);
			this.headerName = other.headerName;
		}
		
		/**
		 * Parses a new set of acceptHeaders from the existing header value
		 */
		private AcceptHeader(String headerName, String headerValue)
		{
			this.acceptedTypes = new HashMap<>();
			this.headerName = headerName;
			
			// Parses the headers (if possible)
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
		
		
		// IMPLEMENTED METHODS	------------------
		
		@Override
		public String toString()
		{
			return getName() + " = " + parseHeaderValue();
		}
		
		
		// ACCESSORS	--------------------------
		
		/**
		 * @return The name of the header
		 */
		public String getName()
		{
			return this.headerName;
		}
		
		
		// OTHER METHODS	----------------------
		
		/**
		 * @return Parses a header value string from the accept header
		 */
		public String parseHeaderValue()
		{
			StringBuilder headerValue = new StringBuilder();
			
			boolean isFirst = true;
			for (String type : this.acceptedTypes.keySet())
			{
				double priority = this.acceptedTypes.get(type);
				
				if (!isFirst)
					headerValue.append(", ");
				else
					isFirst = false;
				headerValue.append(type.toString());
				headerValue.append("; q=");
				headerValue.append(priority);
			}
			
			return headerValue.toString();
		}
		
		/**
		 * Finds the accepted type most preferred by the client
		 * @param availableTypes The types the service is willing to provide
		 * @return The index of the type the client prefers. -1 if the client doesn't accept 
		 * any of the types.
		 */
		public int getPrefferedTypeIndex(List<String> availableTypes)
		{
			// TODO: Remove case-sensitivity?
			int best = -1;
			double bestPriority = -1;
			
			for (int i = 0; i < availableTypes.size(); i++)
			{
				String type = availableTypes.get(i);
				if (this.acceptedTypes.containsKey(type))
				{
					double priority = this.acceptedTypes.get(type);
					if (priority == 1)
						return i;
					else if (priority > bestPriority && priority != 0)
					{
						best = i;
						bestPriority = priority;
					}
				}
			}
			
			return best;
		}
		
		/**
		 * Finds the content type most preferred by the client
		 * @param types The content types the server is able to provide
		 * @return The most desirable content type or null if none of the types is accepted
		 */
		public ContentType getPrefferedContentType(List<? extends ContentType> types)
		{
			List<String> typeStrings = new ArrayList<>();
			for (ContentType type : types)
			{
				typeStrings.add(type.toString());
			}
			
			int bestIndex = getPrefferedTypeIndex(typeStrings);
			if (bestIndex < 0)
				return null;
			else
				return types.get(bestIndex);
		}
		
		/**
		 * Finds the charset most preferred by the client
		 * @param charsets A list of possible character sets
		 * @return The most desirable character set or null if none of the sets is accepted
		 */
		public Charset getPrefferedCharset(List<? extends Charset> charsets)
		{
			List<String> charsetStrings = new ArrayList<>();
			for (Charset charset : charsets)
			{
				charsetStrings.add(charset.name());
			}
			
			int bestIndex = getPrefferedTypeIndex(charsetStrings);
			if (bestIndex < 0)
				return null;
			else
				return charsets.get(bestIndex);
		}
	}
}
