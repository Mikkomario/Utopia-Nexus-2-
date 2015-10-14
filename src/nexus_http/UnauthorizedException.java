package nexus_http;

/**
 * These exceptions are thrown when user authentication is required
 * @author Mikko Hilpinen
 * @since 10.10.2015
 */
public class UnauthorizedException extends HttpException
{
	private static final long serialVersionUID = 761190209950270231L;
	
	// CONSTRUCTOR	-----------------

	/**
	 * Creates a new authorization exception
	 */
	public UnauthorizedException()
	{
		super(HttpStatus.UNAUTHORIZED);
	}

	
	// IMPLEMENTED METHODS	-------------
	
	/**
	 * Modifies the headers by adding a basic authentication challenge
	 * @param headers The headers that will get modified
	 */
	@Override
	public void modifyHeaders(Headers headers)
	{
		headers.setBasicAuthenticationChallenge();
	}
}
