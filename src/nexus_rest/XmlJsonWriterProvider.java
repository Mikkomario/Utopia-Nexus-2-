package nexus_rest;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import nexus_http.ContentType;
import nexus_http.Headers;
import nexus_http.Headers.AcceptHeader;
import nexus_rest.ResourceWriter.LinkWriteStyle;
import nexus_rest.ResourceWriter.ResourceWriterException;

/**
 * This writer provider is able to create xml and json writers
 * @author Mikko Hilpinen
 * @since 24.10.2015
 */
public class XmlJsonWriterProvider implements ResourceWriterProvider
{
	// ATTRIBUTES	-------------------------
	
	private List<ContentType> contentTypes;
	private List<Charset> charsets;
	
	// CONSTRUCTOR	-------------------------
	
	/**
	 * Creates a new writer provider
	 */
	public XmlJsonWriterProvider()
	{
		this.contentTypes = new ArrayList<>();
		this.contentTypes.add(ContentType.XML);
		this.charsets = ResourceWriterProvider.defaultCharsets();
	}
	
	// IMPLEMENTED METHODS	-----------------------
	
	@Override
	public ResourceWriter createWriter(OutputStream stream, Headers headers) throws 
			ResourceWriterException
	{
		LinkWriteStyle linkStyle = headers.getLinkWriteStyle();
		
		ContentType contentType = ContentType.XML;
		AcceptHeader acceptContentType = headers.getAcceptContentTypeHeader();
		if (acceptContentType != null)
		{
			ContentType preferred = acceptContentType.getPrefferedContentType(
					getSupportedContentTypes());
			if (preferred != null)
				contentType = preferred;
		}
		
		Charset charset = null;
		AcceptHeader acceptCharset = headers.getAcceptCharsetHeader();
		if (acceptCharset != null)
		{
			Charset preferred = acceptCharset.getPrefferedCharset(this.charsets);
			if (preferred != null)
				charset = preferred;
		}
		
		// TODO: Add support for json
		switch (contentType)
		{
			default: return new XmlResourceWriter(stream, linkStyle, charset);
		}
	}

	@Override
	public List<? extends ContentType> getSupportedContentTypes()
	{
		return this.contentTypes;
	}
}
