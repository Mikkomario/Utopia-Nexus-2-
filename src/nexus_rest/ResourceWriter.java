package nexus_rest;

import java.io.OutputStream;

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
	 */
	public void writeResourceStart(String resourceName);
	
	/**
	 * The writer closes a currently open resource (element). If there is no resource open, 
	 * this method should do nothing.
	 */
	public void writeResourceEnd();
	
	/**
	 * The writer writes an attribute value pair under the currently open resource.
	 * @param attributeName The element name
	 * @param attributeValue The element value
	 */
	public void writeAttribute(String attributeName, String attributeValue);
	
	/**
	 * The writer writes a link element under the currently open resource.
	 * @param linkName The name of the link attribute
	 * @param targetPath The target path of the link
	 */
	public void writeLink(String linkName, Path targetPath);
	
	/**
	 * The writer writes the document end. No more resources or attributes can be written 
	 * under that document.
	 */
	public void writeDocumentEnd();
	
	/**
	 * @return The stream the writer writes to, in case some modifications need to be made 
	 * outside the writer itself.
	 */
	public OutputStream getStream();
	
	
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
