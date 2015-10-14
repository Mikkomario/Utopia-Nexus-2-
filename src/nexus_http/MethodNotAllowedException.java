package nexus_http;

/**
 * These exceptions are thrown when a resource doesn't support a method
 * @author Mikko Hilpinen
 * @since 10.10.2015
 */
public class MethodNotAllowedException extends HttpException
{
	// ATTRIBUTES	------------------
	
	private static final long serialVersionUID = 1016606882463640330L;
	private Method[] allowed;
	
	
	// CONSTRUCTOR	------------------
	
	/**
	 * Creates a new exception.
	 * @param allowedMethods The methods that are allowed for the targeted resource
	 */
	public MethodNotAllowedException(Method... allowedMethods)
	{
		super(HttpStatus.METHOD_NOT_ALLOWED);
		
		this.allowed = allowedMethods;
	}
	
	/**
	 * Creates a new exception.
	 * @param message The message sent along with the exception
	 * @param allowedMethods The methods that are allowed for the targeted resource
	 */
	public MethodNotAllowedException(String message, Method... allowedMethods)
	{
		super(HttpStatus.METHOD_NOT_ALLOWED, message);
		
		this.allowed = allowedMethods;
	}
	
	
	// IMPLEMENTED METHODS	----------
	
	/**
	 * Adds an allowed header to describe the allowed methods
	 */
	@Override
	public void modifyHeaders(Headers headers)
	{
		headers.setAllowedMethods(this.allowed);
	}
	
	
	// ACCESSORS	------------------
	
	/**
	 * @return The methods allowed by the resource
	 */
	public Method[] getAllowedMethods()
	{
		return this.allowed;
	}
}
