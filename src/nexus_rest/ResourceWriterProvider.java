package nexus_rest;

import java.io.OutputStream;

import nexus_http.ContentType;
import nexus_rest.ResourceWriter.ResourceWriterException;

/**
 * A resourceWriterProvider is able to produce resource writers for different content types
 * @author Mikko Hilpinen
 * @since 14.10.2015
 */
public interface ResourceWriterProvider
{
	/**
	 * In this method the provider creates a new writer for the target stream
	 * @param stream The target stream of the writer
	 * @param targetType The content type the writer should produce
	 * @return A resource writer that produces the requested content type
	 * @throws ResourceWriterException If the writer couldn't be created
	 */
	public ResourceWriter createWriter(OutputStream stream, ContentType targetType) throws 
			ResourceWriterException;
}
