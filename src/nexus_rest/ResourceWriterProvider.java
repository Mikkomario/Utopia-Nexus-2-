package nexus_rest;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import nexus_http.ContentType;
import nexus_http.Headers;
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
	 * @param headers The headers that may affect the writer properties
	 * @return A resource writer that produces the requested content type
	 * @throws ResourceWriterException If the writer couldn't be created
	 */
	public ResourceWriter createWriter(OutputStream stream, Headers headers) throws 
			ResourceWriterException;
	
	/**
	 * @return The content types supported by this writer
	 */
	public List<? extends ContentType> getSupportedContentTypes();
	
	
	// OTHER METHODS	----------------
	
	/**
	 * @return A list containing all the standard charcater sets
	 */
	public static List<Charset> defaultCharsets()
	{
		List<Charset> charsets = new ArrayList<>();
		charsets.add(StandardCharsets.ISO_8859_1);
		charsets.add(StandardCharsets.US_ASCII);
		charsets.add(StandardCharsets.UTF_16);
		charsets.add(StandardCharsets.UTF_16BE);
		charsets.add(StandardCharsets.UTF_16LE);
		charsets.add(StandardCharsets.UTF_8);
		
		return charsets;
	}
}
