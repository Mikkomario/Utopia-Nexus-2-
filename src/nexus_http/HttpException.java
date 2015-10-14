package nexus_http;

/**
 * These exceptions are thrown on server side. Each exception has an associated http status 
 * code, eg. 404 "Not Found"
 * @author Mikko Hilpinen
 * @since 7.10.2015
 */
public class HttpException extends Exception
{
	// ATTRIBUTES	------------------
	
	private static final long serialVersionUID = -5185618679180460233L;
	
	private HttpStatus status;
	
	
	// CONSTRUCTOR	------------------
	
	/**
	 * Creates a new http exception
	 * @param status The status associated with the exception
	 */
	public HttpException(HttpStatus status)
	{
		this.status = status;
	}

	/**
	 * Creates a new http exception
	 * @param status The status associated with the exception
	 * @param message The message sent along with the exception. The message should be shown 
	 * to the client as well.
	 */
	public HttpException(HttpStatus status, String message)
	{
		super(message);
		
		this.status = status;
	}

	/**
	 * Creates a new http exception
	 * @param status The status associated with the exception
	 * @param cause The exception that caused this exception
	 */
	public HttpException(HttpStatus status, Throwable cause)
	{
		super(cause);
		
		this.status = status;
	}

	/**
	 * Creates a new http exception
	 * @param status The status associated with the exception
	 * @param message The message sent along with the exception. The message should be shown 
	 * to the client as well.
	 * @param cause The exception that caused this exception
	 */
	public HttpException(HttpStatus status, String message, Throwable cause)
	{
		super(message, cause);
		
		this.status = status;
	}
	
	
	// ACCESSORS	----------------
	
	/**
	 * @return The http status associated with this exception
	 */
	public HttpStatus getStatus()
	{
		return this.status;
	}
	
	
	// OTHER METHODS	------------
	
	/**
	 * This method makes the necessary modifications to the headers. The default 
	 * implementation does nothing, but the subclasses may wish to override this.
	 * @param headers The headers that may be modified
	 */
	public void modifyHeaders(Headers headers)
	{
		// The default implementation does nothing
	}
}
