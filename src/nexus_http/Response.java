package nexus_http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Responses are sent by the server in response to a request
 * @author Mikko Hilpinen
 * @since 10.10.2015
 */
public class Response
{
	// ATTRIBUTES	--------------------
	
	private HttpStatus status;
	private Headers headers;
	private ByteArrayOutputStream body;
	
	
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new response. The status remains undefined until set.
	 */
	public Response()
	{
		this.status = null;
		this.headers = new Headers();
		this.body = null;
	}
	
	/**
	 * Creates a new response
	 * @param status The response status
	 * @param headers The response headers
	 * @param body The response body
	 */
	public Response(HttpStatus status, Headers headers, ByteArrayOutputStream body)
	{
		this.status = status;
		this.headers = headers;
		this.body = body;
	}
	
	/**
	 * Creates a new response based on the provided exception. The response body, if necessary, 
	 * should be added separately.
	 * @param e The exception this response is created from
	 */
	public Response(HttpException e)
	{
		this.status = e.getStatus();
		this.headers = new Headers();
		this.body = null;
		
		e.modifyHeaders(getHeaders());
	}
	
	
	// IMPLEMENTED METHODS	------------
	
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		
		if (getStatus() != null)
		{
			s.append("Status: ");
			s.append(getStatus());
		}
		s.append("\nHeaders:");
		s.append(getHeaders().toString());
		if (this.body != null)
		{
			s.append("\nBody:\n");
			s.append(this.body.toString());
		}
		
		return s.toString();
	}
	
	
	// ACCESSORS	--------------------
	
	/**
	 * @return The status of this reponse
	 */
	public HttpStatus getStatus()
	{
		return this.status;
	}
	
	/**
	 * Changes the status of this response
	 * @param status The new status of this response
	 */
	public void setStatus(HttpStatus status)
	{
		this.status = status;
	}
	
	/**
	 * @return The headers sent along with this response
	 */
	public Headers getHeaders()
	{
		return this.headers;
	}
	
	/**
	 * Returns the response body. If there is no set body, may create one
	 * @param createIfNotExists If there is no body yet should one be created.
	 * @return The body part of this response. Null if not initialized.
	 */
	public ByteArrayOutputStream getBody(boolean createIfNotExists)
	{
		if (this.body == null && createIfNotExists)
			this.body = new ByteArrayOutputStream();
		return this.body;
	}
	
	/**
	 * Adds a body to the response. If there was a body previously, closes it.
	 * @param body The body part of the response.
	 */
	public void setBody(ByteArrayOutputStream body)
	{
		if (this.body != null)
			closeBody();
		
		this.body = body;
	}
	
	/**
	 * Closes the response body
	 */
	public void closeBody()
	{
		try
		{
			if (this.body != null)
				this.body.close();
		}
		catch (IOException e1)
		{
			System.err.println("Failed to close the response body");
		}
	}
	
	
	// OTHER METHODS	------------------
	
	/**
	 * @return Does the response contain a body
	 */
	public boolean hasContent()
	{
		return this.body != null;
	}
}
