package nexus_rest;

import java.io.OutputStream;

import nexus_http.ContentType;
import nexus_http.Link;
import nexus_http.Path;

/**
 * Resource writers are used when forming a body for a response. The writers should be 
 * able to write resource elements, attributes and links alike.
 * @author Mikko Hilpinen
 * @since 10.10.2015
 */
public interface ResourceWriter
{
	/**
	 * The writer writes a resource start (element). If the document start hasn't been 
	 * written, the writer should write that as well.
	 * @param resourceName The name of the resource (element)
	 * @param resourceLinkPath The path to the resource, in case the writer wants to 
	 * write it as well
	 * @throws ResourceWriterException If the writing fails
	 */
	public void writeResourceStart(String resourceName, Path resourceLinkPath) 
			throws ResourceWriterException;
	
	/**
	 * The writer closes a currently open resource (element). If there is no resource open, 
	 * this method should do nothing.
	 * @throws ResourceWriterException If the writing fails
	 */
	public void writeResourceEnd() throws ResourceWriterException;
	
	/**
	 * The writer starts an array (if applicable) that may contain repeated elements
	 * @param arrayName The name of the array
	 * @throws ResourceWriterException If the writing fails
	 */
	public void writeArrayStart(String arrayName) throws ResourceWriterException;
	
	/**
	 * The writer ends array writing and writes necessary close elements
	 * @throws ResourceWriterException If the writing fails
	 */
	public void writeArrayEnd() throws ResourceWriterException;
	
	/**
	 * The writer writes an attribute value pair under the currently open resource.
	 * @param propertyName The element name
	 * @param propertyValue The element value
	 * @throws ResourceWriterException If the writing fails
	 */
	public void writeProperty(String propertyName, String propertyValue) throws ResourceWriterException;
	
	/**
	 * The writer writes a link element under the currently open resource.
	 * @param link the link to write
	 * @throws ResourceWriterException If the writing fails
	 */
	public void writeLink(Link link) throws ResourceWriterException;
	
	/**
	 * The writer writes the document start, including any possible root resource. The writer 
	 * can expect the document to be opened when writing other content.
	 * @param rootName The name of the document's root element, where applicable
	 * @throws ResourceWriterException If the writing fails
	 */
	public void writeDocumentStart(String rootName) throws ResourceWriterException;
	
	/**
	 * The writer writes the document end. No more resources or attributes should be written 
	 * under that document anymore.
	 * @throws ResourceWriterException If the writing fails
	 */
	public void writeDocumentEnd() throws ResourceWriterException;
	
	/**
	 * @return The stream the writer writes to, in case some modifications need to be made 
	 * outside the writer itself.
	 */
	public OutputStream getStream();
	
	/**
	 * Closes the writer and releases any resource attached to it. This must not close the 
	 * output stream. The writer shouldn't be used after calling this method.
	 */
	public void close();
	
	/**
	 * @return The link write style used by this writer
	 */
	public LinkWriteStyle getLinkWriteStyle();
	
	/**
	 * @return The character set used by this writer
	 */
	public String getCharset();
	
	/**
	 * @return The content type produced by this writer
	 */
	public ContentType getContentType();
	
	
	// ENUMERATIONS	---------------------
	
	/**
	 * These are the different styles a link can be written with
	 * @author Mikko Hilpinen
	 * @since 18.10.2015
	 */
	public static enum LinkWriteStyle
	{
		/**
		 * The link will be written in full, with server path included
		 */
		FULL,
		/**
		 * The written link will only contain the link's path and no server path
		 */
		SIMPLE,
		/**
		 * Links won't be written at all
		 */
		NONE;
		
		
		// OTHER METHODS	--------------
		
		/**
		 * Parses a link write style from a string
		 * @param s A string that represents a link write style
		 * @return The style represented by the string or null if the string didn't represent 
		 * a link write style
		 */
		public static LinkWriteStyle parseFromString(String s)
		{
			for (LinkWriteStyle style : values())
			{
				if (style.toString().equalsIgnoreCase(s))
					return style;
			}
			
			return null;
		}
	}
	
	
	// SUBCLASSES	----------------------
	
	/**
	 * These exception can occur when a resource writer fails to performs a task it was given.
	 * @author Mikko Hilpinen
	 * @since 10.10.2015
	 */
	public static class ResourceWriterException extends Exception
	{
		private static final long serialVersionUID = -6348445624485862532L;
		
		// CONSTRUCTOR	------------------

		/**
		 * Creates a new exception
		 */
		public ResourceWriterException()
		{
			// Simple constructor
		}
		
		/**
		 * Creates a new exception
		 * @param message The message sent along with the exception
		 */
		public ResourceWriterException(String message)
		{
			super(message);
		}
		
		/**
		 * Creates a new exception
		 * @param cause The cause of the exception
		 */
		public ResourceWriterException(Throwable cause)
		{
			super(cause);
		}
		
		/**
		 * Creates a new exception
		 * @param message The message sent with the exception
		 * @param cause The cause of the exception
		 */
		public ResourceWriterException(String message, Throwable cause)
		{
			super(message, cause);
		}
	}
}
