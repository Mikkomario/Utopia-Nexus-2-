package nexus_http;

/**
 * Link is simply a named path
 * @author Mikko Hilpinen
 * @since 14.10.2015
 */
public class Link
{
	// ATTRIBUTES	--------------------
	
	private Path target;
	private String name;
	
	
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new link
	 * @param name The name of the link. Should describe the targeted content.
	 * @param target The target path of the link
	 */
	public Link(String name, Path target)
	{
		this.name = name;
		this.target = target;
	}
	
	
	// IMPLEMENTED METHODS	------------
	
	@Override
	public String toString()
	{
		return getName() + " = " + getTargetPath();
	}

	
	// ACCESSORS	--------------------
	
	/**
	 * @return The path of the link's target
	 */
	public Path getTargetPath()
	{
		return this.target;
	}
	
	/**
	 * @return The name of the link. Should describe the content at the end of the target path.
	 */
	public String getName()
	{
		return this.name;
	}
}
