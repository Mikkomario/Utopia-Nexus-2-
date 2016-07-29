package nexus_http;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is a collection of parameters. Supports encoding. The keys are case-insensitive.
 * @author Mikko Hilpinen
 * @since 7.10.2015
 */
public class Parameters
{
	// ATTRIBUTES	------------------------
	
	// TODO: Use model here. Stream must be made a new data type
	private Map<String, String> parameters;
	private Map<String, InputStream> streamParameters;
	
	
	// CONSTRUCTOR	------------------------
	
	/**
	 * Creates a new parameter set
	 * @see #addParameter(String, String)
	 */
	public Parameters()
	{
		this.parameters = new HashMap<>();
		this.streamParameters = new HashMap<>();
	}
	
	/**
	 * Copies another parameter set
	 * @param other The other parameter set
	 */
	public Parameters(Parameters other)
	{
		this.parameters = new HashMap<>();
		this.parameters.putAll(other.parameters);
		this.streamParameters = new HashMap<>();
		this.streamParameters.putAll(other.streamParameters);
	}
	
	/**
	 * Parses a parameter set from a parameter string (the part that comes after the '?')
	 * @param parameterString The parameter string. Key value pairs are separated with '&' while 
	 * values are separated from the keys with '='
	 * @param encoding The encoding that is used for the parameter <b>values</b>. Null if raw 
	 * values should be used
	 * @throws UnsupportedEncodingException If the decoding failed
	 */
	public Parameters(String parameterString, String encoding) throws UnsupportedEncodingException
	{
		this.parameters = new HashMap<>();
		this.streamParameters = new HashMap<>();
		
		String[] keyValuePairs = parameterString.split("\\&");
		for (String keyValuePair : keyValuePairs)
		{
			int indexOfEquals = keyValuePair.indexOf('=');
			String key, value;
			if (indexOfEquals < 0)
			{
				key = keyValuePair;
				value = "";
			}
			else
			{
				key = keyValuePair.substring(0, indexOfEquals);
				value = keyValuePair.substring(indexOfEquals + 1);
			}
			
			// Decodes parameter values if necessary
			if (encoding != null && value != null)
				value = URLDecoder.decode(value, encoding);
			
			addParameter(key, value);
		}
	}
	
	
	// IMPLEMENTED METHODS	----------------
	
	@Override
	public String toString()
	{
		try
		{
			return getParameterString(null);
		}
		catch (UnsupportedEncodingException e)
		{
			// Ignored, besides, no encoding is used
			return null;
		}
	}

	
	// OTHER METHODS	--------------------
	
	/**
	 * Parses the parameters into a form that can be put to an uri
	 * @param encoding Encoding used for the parameter values. Null if raw values should be 
	 * added.
	 * @return A string parsed from the parameters
	 * @throws UnsupportedEncodingException If the encoding failed
	 */
	public String getParameterString(String encoding) throws UnsupportedEncodingException
	{
		StringBuilder s = new StringBuilder();
		
		boolean isFirst = true;
		for (String parameterName : getParameterNames())
		{
			if (!isFirst)
				s.append("&");
			isFirst = false;
			
			s.append(parameterName);
			String value = getParameterValue(parameterName);
			if (value != null)
			{
				s.append("=");
				if (encoding != null)
					s.append(URLEncoder.encode(value, encoding));
				else
					s.append(value);
			}
		}
		
		return s.toString();
	}
	
	/**
	 * Finds the value of a single parameter (case-insensitive)
	 * @param parameterName The name of the parameter
	 * @return The value of that parameter. Null if there is no such parameter.
	 */
	public String getParameterValue(String parameterName)
	{
		return this.parameters.get(parameterName.toLowerCase());
	}
	
	/**
	 * Finds a value of a single stream / post parameter (case-insensitive)
	 * @param parameterName The name of the parameter
	 * @return The value of the parameter as an input stream. Remember to close the stream 
	 * after use.
	 */
	public InputStream getParameterStream(String parameterName)
	{
		return this.streamParameters.get(parameterName.toLowerCase());
	}
	
	/**
	 * Adds a new parameter to the set or replaces an existing parameter
	 * @param parameterName The name of the parameter
	 * @param parameterValue The value associated with the parameter
	 */
	public void addParameter(String parameterName, String parameterValue)
	{
		this.parameters.put(parameterName.toLowerCase(), parameterValue);
	}
	
	/**
	 * Adds a new post / stream parameter to the set
	 * @param parameterName The name of the parameter
	 * @param parameterValue The parameter value
	 */
	public void addParameter(String parameterName, InputStream parameterValue)
	{
		this.streamParameters.put(parameterName.toLowerCase(), parameterValue);
	}
	
	/**
	 * Checks if there is a parameter or a post / stream parameter with the given name in 
	 * the set (case-insensitive)
	 * @param parameterName The name of the parameter
	 * @return Is there a parameter with the given name in the set
	 */
	public boolean containsParameter(String parameterName)
	{
		return this.parameters.containsKey(parameterName.toLowerCase()) || 
				this.streamParameters.containsKey(parameterName.toLowerCase());
	}
	
	/**
	 * @return The names of all the basic parameters in this set
	 */
	public Set<String> getParameterNames()
	{
		return this.parameters.keySet();
	}
	
	/**
	 * @return The names of all the stream / post parameters in this set
	 */
	public Set<String> getStreamParameterNames()
	{
		return this.streamParameters.keySet();
	}
}
