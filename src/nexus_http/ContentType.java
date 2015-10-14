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
	XML("text", "xml"),
	/**
	 * Application/json
	 */
	JSON("application", "json"),
	/**
	 * text/html
	 */
	HTML("text", "html"),
	/**
	 * text/plain
	 */
	PLAINTEXT("text", "plain"),
	/**
	 * Image/png
	 */
	PNG("image", "png"),
	/**
	 * Image/jpg
	 */
	JPG("image", "jpg"),
	/**
	 * Audio/mp3
	 */
	MP3("audio", "mp3"),
	/**
	 * Audio/wav
	 */
	WAV("audio", "wav"),
	/**
	 * Text/*
	 */
	ALL_TEXT("text", "*"),
	/**
	 * Image/*
	 */
	ALL_IMAGE("image", "*"),
	/**
	 * Audio/*
	 */
	ALL_AUDIO("audio", "*");
	
	
	// ATTRIBUTES	-----------------
	
	private final String category, type;
	
	
	// CONSTRUCTOR	-----------------
	
	private ContentType(String category, String type)
	{
		this.type = type;
		this.category = category;
	}
	
	
	// IMPLEMENTED METHODS	---------
	
	@Override
	public String toString()
	{
		return this.category + "/" + this.type;
	}
	
	
	// OTHER METHODS	-------------
	
	/**
	 * Checks if this content type is in the provided category.
	 * @param category A content type category like 'text', 'audio' or 'image'
	 * @return Does this content type fit into the category
	 */
	public boolean isInCategory(String category)
	{
		return this.category.equalsIgnoreCase(category);
	}
	
	/**
	 * Checks if this contentType is of the provided type.
	 * @param contentType The content-type string, like 'image/jpg' or 'application/json'. 
	 * '/*' is allowed. For example, 'text/*' 
	 * could mean both xml and html.
	 * @return Does this content type fit into the provided type
	 */
	public boolean isOfType(String contentType)
	{
		if (contentType.endsWith("*"))
			return isInCategory(contentType.split("\\/")[0]);
		else
			return toString().equalsIgnoreCase(contentType);
	}
}
