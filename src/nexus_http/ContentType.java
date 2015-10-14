package nexus_http;

/**
 * The different content types that can be returned in the responses
 * @author Mikko Hilpinen
 * @since 10.10.2015
 */
public enum ContentType
{
	/**
	 * Application/xml
	 */
	XML("text/xml"),
	/**
	 * Application/json
	 */
	JSON("application/json"),
	/**
	 * text/html
	 */
	HTML("text/html"),
	/**
	 * text/plain
	 */
	PLAINTEXT("text/plain"),
	/**
	 * Image/png
	 */
	PNG("image/png"),
	/**
	 * Audio/mp3
	 */
	MP3("audio/mp3"),
	/**
	 * Audio/wav
	 */
	WAV("audio/wav");
	
	
	// ATTRIBUTES	-----------------
	
	private final String type;
	
	
	// CONSTRUCTOR	-----------------
	
	private ContentType(String type)
	{
		this.type = type;
	}
	
	
	// IMPLEMENTED METHODS	---------
	
	@Override
	public String toString()
	{
		return this.type;
	}
}
