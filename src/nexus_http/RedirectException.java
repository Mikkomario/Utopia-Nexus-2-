package nexus_http;

/**
 * These exceptions are thrown when a targeted resource has been moved elswhere, either 
 * temporarily or permanently.
 * @author Mikko Hilpinen
 * @since 10.10.2015
 */
public class RedirectException extends HttpException
{
	// ATTRIBUTES	-------------------
	
	private static final long serialVersionUID = 7111514489423119778L;
	
	private Path targetLocation, newLocation;
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new exception
	 * @param targetLocation The location that was requested
	 * @param newLocation The location the resource was moved to
	 * @param isTemporary Is the change temporary (true) or permanent (false)
	 */
	public RedirectException(Path targetLocation, Path newLocation, boolean isTemporary)
	{
		super(isTemporary ? HttpStatus.TEMPORARY_REDIRECT : HttpStatus.MOVED_PERMANENTLY, 
				newLocation.getAbsoluteUrl());
		
		this.targetLocation = targetLocation;
		this.newLocation = newLocation;
	}
	
	
	// IMPLEMENTED METHODS	-----------
	
	/**
	 * Adds the location header
	 * @param headers The headers that will be modified
	 */
	@Override
	public void modifyHeaders(Headers headers)
	{
		headers.setLocation(getNewLocation());
	}
	
	
	// ACCESSORS	-------------------
	
	/**
	 * @return The resource's original location
	 */
	public Path getOriginalLocation()
	{
		return this.targetLocation;
	}
	
	/**
	 * @return The resource's new location
	 */
	public Path getNewLocation()
	{
		return this.newLocation;
	}
}
