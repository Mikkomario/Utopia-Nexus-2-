package nexus_http;

/**
 * These exceptions are thrown when something goes wrong and the server can't respond 
 * properly. These exceptions should always be logged for debugging purposes.
 * @author Mikko Hilpinen
 * @since 10.10.2015
 */
public class InternalServerException extends HttpException
{
	// ATTRIBUTES	--------------------
	
	private static final long serialVersionUID = 1599132731480839483L;
	
	private Request request;
	private Path sourceLocation;
	
	
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new exception
	 * @param message The message describing the exception context
	 * @param cause The cause of the exception
	 * @param request The request that was being handled
	 * @param sourceLocation The location of the resource that encountered the exception
	 */
	public InternalServerException(String message,
			Throwable cause, Request request, Path sourceLocation)
	{
		super(HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
		
		this.request = request;
		this.sourceLocation = sourceLocation;
	}
	
	/**
	 * Creates a new exception
	 * @param message The message describing the exception context
	 * @param request The request that was being handled
	 * @param sourceLocation The location of the resource that encountered the exception
	 */
	public InternalServerException(String message, Request request, Path sourceLocation)
	{
		super(HttpStatus.INTERNAL_SERVER_ERROR, message);
		
		this.request = request;
		this.sourceLocation = sourceLocation;
	}
	
	
	// ACCESSORS	--------------------
	
	/**
	 * @return The request that was being handled while the exception occurred
	 */
	public Request getRequest()
	{
		return this.request;
	}
	
	/**
	 * @return The location of the resource that encountered the exception
	 */
	public Path getSourceLocation()
	{
		return this.sourceLocation;
	}
}
