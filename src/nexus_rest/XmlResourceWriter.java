package nexus_rest;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;

import flow_io.XMLIOAccessor;
import nexus_http.ContentType;
import nexus_http.Link;
import nexus_http.Path;
import utopia.flow.generics.Value;

/**
 * These writers are able to write resources in xml format
 * @author Mikko Hilpinen
 * @since 15.10.2015
 */
public class XmlResourceWriter implements ResourceWriter
{
	// ATTRIBUTES	-------------------
	
	private OutputStream stream;
	private XMLStreamWriter writer;
	private boolean isOpen;
	private LinkWriteStyle linkStyle;
	private Charset charset;
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new writer that will write into the target stream
	 * @param stream The stream the writer will operate on
	 * @param linkStyle The style the links are written in
	 * @param charset The character set used for encoding the output contents, null means utf-8
	 */
	public XmlResourceWriter(OutputStream stream, LinkWriteStyle linkStyle, Charset charset)
	{
		this.stream = stream;
		this.writer = null;
		this.isOpen = false;
		this.linkStyle = linkStyle;
		this.charset = charset;
		
		if (this.linkStyle == null)
			this.linkStyle = LinkWriteStyle.NONE;
	}
	
	
	// IMPLEMENTED METHODS	----------

	@Override
	public void writeResourceStart(String resourceName, Path resourceLinkPath)
			throws ResourceWriterException
	{
		if (resourceName != null)
		{
			try
			{	
				getWriter().writeStartElement(resourceName);
				if (resourceLinkPath != null && this.linkStyle != LinkWriteStyle.NONE)
				{
					String url;
					if (this.linkStyle == LinkWriteStyle.FULL)
						url = resourceLinkPath.getAbsoluteUrl();
					else
						url = resourceLinkPath.toString();
					XMLIOAccessor.writeLinkAsAttribute(url, getWriter(), false);
				}
			}
			catch (XMLStreamException e)
			{
				throw new ResourceWriterException("Failed to write resource start for " + 
						resourceName, e);
			}
		}
	}

	@Override
	public void writeArrayStart(String arrayName)
			throws ResourceWriterException
	{
		writeResourceStart(arrayName, null);
	}

	@Override
	public void writeArrayEnd() throws ResourceWriterException
	{
		writeResourceEnd();
	}

	@Override
	public void writeResourceEnd() throws ResourceWriterException
	{
		try
		{
			getWriter().writeEndElement();
		}
		catch (XMLStreamException e)
		{
			throw new ResourceWriterException("Failed to write resource end", e);
		}
	}

	@Override
	public void writeProperty(String attributeName, Value attributeValue) throws 
			ResourceWriterException
	{
		// TODO: Use xml element writer to write the value(s)
		// The element writer needs encoding options, however
		if (attributeName != null)
		{
			try
			{
				if (attributeValue != null)
				{
					writeResourceStart(attributeName, null);
					getWriter().writeCharacters(attributeValue.toString());
					writeResourceEnd();
				}
				else
					getWriter().writeEmptyElement(attributeName);
			}
			catch (XMLStreamException e)
			{
				throw new ResourceWriterException("Failed to write resource attribute " + 
						attributeName, e);
			}
		}
	}

	@Override
	public void writeLink(Link link) throws ResourceWriterException
	{
		if (link != null && this.linkStyle != LinkWriteStyle.NONE)
		{
			writeResourceStart(link.getName(), link.getTargetPath());
			writeResourceEnd();
		}
	}
	
	@Override
	public void writeDocumentStart(String rootName) throws ResourceWriterException
	{
		try
		{
			getWriter().writeStartDocument(this.charset.name());
			getWriter().writeStartElement(rootName);
			XMLIOAccessor.writeXLinkNamespaceIntroduction(getWriter());
		}
		catch (XMLStreamException e)
		{
			throw new ResourceWriterException("Failed to write document start", e);
		}
	}

	@Override
	public void writeDocumentEnd() throws ResourceWriterException
	{
		try
		{
			getWriter().writeEndDocument();
		}
		catch (XMLStreamException e)
		{
			throw new ResourceWriterException("Failed to write document end", e);
		}
	}

	@Override
	public OutputStream getStream()
	{
		if (this.stream == null)
			this.stream = new ByteArrayOutputStream();
		return this.stream;
	}

	@Override
	public void close()
	{
		if (isOpen())
		{
			try
			{
				this.writer.close();
				this.isOpen = false;
			}
			catch (XMLStreamException e)
			{
				// Ignored
			}
		}
	}
	
	@Override
	public LinkWriteStyle getLinkWriteStyle()
	{
		return this.linkStyle;
	}

	@Override
	public String getCharset()
	{
		return this.charset.name();
	}

	@Override
	public ContentType getContentType()
	{
		return ContentType.XML;
	}
	
	
	// ACCESSORS	--------------------
	
	/**
	 * @return Is the writer currently open. Open writers should be closed eventually.
	 */
	public boolean isOpen()
	{
		return this.isOpen;
	}
	
	private XMLStreamWriter getWriter() throws ResourceWriterException
	{
		if (this.writer == null)
		{
			if (this.charset == null)
				this.writer = XMLStreamWriterFactory.create(getStream());
			else
				this.writer = XMLStreamWriterFactory.create(getStream(), this.charset.name());
			
			if (this.writer == null)
				throw new ResourceWriterException("Couldn't create an xml stream writer");
		}
		
		return this.writer;
	}
	
	/*
	private String encoded(String original)
	{
		if (original == null)
			return null;
		
		if (this.charset == null)
			return original;
		else
			return this.charset.encode(original).toString();
	}
	*/
}
