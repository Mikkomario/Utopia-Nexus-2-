package nexus_http;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Requests are sent by clients and handled by the server
 * @author Mikko Hilpinen
 * @since 27.12.2014
 */
public class Request
{
	// ATTRIBUTES	------------------------------
	
	private Method method;
	private String targetPathString;
	private Collection<Path> paths;
	private Headers headers;
	private Parameters parameters;
	
	
	// CONSTRUCTOR	------------------------------
	
	/**
	 * Parses a new request from the provided uri line
	 * @param method The method used with the request
	 * @param uriLine The uri line. The path part and the parameter part should be separated with '?'
	 * @param headers The headers given with this request
	 * @param encoding The encoding used for the parameter values. Null if parameter values 
	 * are read as raw.
	 * @throws UnsupportedEncodingException If the parameter value decoding failed
	 */
	public Request(Method method, String uriLine, Headers headers, 
			String encoding) throws UnsupportedEncodingException
	{
		this.method = method;
		this.headers = headers;
		if (this.headers == null)
			this.headers = new Headers();
		
		int parametersStartAfter = uriLine.indexOf('?');
		String pathPart, parameterPart;
		if (parametersStartAfter < 0)
		{
			pathPart = uriLine;
			parameterPart = "";
		}
		else
		{
			pathPart = uriLine.substring(0, parametersStartAfter);
			parameterPart = uriLine.substring(parametersStartAfter + 1);
		}
		
		this.parameters = new Parameters(parameterPart, encoding);
		this.targetPathString = pathPart;
		this.paths = Path.parseFromString(pathPart);
	}
	
	/**
	 * Creates a new request with the given data
	 * @param method The method that describes this request
	 * @param path The target path of this request
	 * @param parameters The parameters used in this request
	 * @param headers The headers provided with the request
	 */
	public Request(Method method, String path, Parameters parameters, 
			Headers headers)
	{
		// Initializes attributes
		this.method = method;
		this.paths = Path.parseFromString(path);
		this.targetPathString = path;
		this.parameters = parameters;
		this.headers = headers;
		
		if (this.parameters == null)
			this.parameters = new Parameters();
		if (this.headers == null)
			this.headers = new Headers();
	}
	
	/**
	 * Creates a copy of another request
	 * @param another The request that will be copied
	 */
	public Request(Request another)
	{
		this.method = another.method;
		this.paths = new ArrayList<>();
		this.paths.addAll(another.paths);
		this.parameters = new Parameters(another.parameters);
		this.headers = new Headers(another.getHeaders());
	}
	
	
	// IMPLEMENTED METHODS	-----------------------
	
	@Override
	public String toString()
	{
		try
		{
			StringBuilder s = new StringBuilder(getMethod() + " ");
			s.append(getUriLine(null));
			Set<String> streamParamNames = getParameters().getStreamParameterNames();
			if (!streamParamNames.isEmpty())
			{
				s.append("\nStream parameters:");
				for (String paramName : streamParamNames)
				{
					s.append("\n" + paramName);
				}
			}
			s.append("\nHeaders:");
			s.append(getHeaders().toString());
			
			return s.toString();
		}
		catch (UnsupportedEncodingException e)
		{
			// Ignored, no encoding used
			return null;
		}
	}
	
	
	// GETTERS & SETTERS	-----------------------
	
	/**
	 * @return The method used by this request
	 */
	public Method getMethod()
	{
		return this.method;
	}
	
	/**
	 * @return The target path(s) of this request
	 */
	public Collection<Path> getPaths()
	{
		return this.paths;
	}
	
	/**
	 * @return The request's target path(s) in string format
	 */
	public String getPath()
	{
		return this.targetPathString;
	}
	
	/**
	 * @return The parameters given with this request
	 */
	public Parameters getParameters()
	{
		return this.parameters;
	}
	
	/**
	 * @return The headers given with this request
	 */
	public Headers getHeaders()
	{
		return this.headers;
	}
	
	
	// OTHER METHODS	-------------------------
	
	/**
	 * Parses the request into an uri line that contains the path and the parameters
	 * @param encoding the encoding used for the parameter values. Null if raw parameter 
	 * values should be added.
	 * @return The path and the parameters of the request in a single string
	 * @throws UnsupportedEncodingException If the encoding failed
	 */
	public String getUriLine(String encoding) throws UnsupportedEncodingException
	{
		String parameterString = getParameters().getParameterString(encoding);
		if (!parameterString.isEmpty())
		{
			return getPath() + "?" + parameterString;
		}
		return getPath();
	}
}
