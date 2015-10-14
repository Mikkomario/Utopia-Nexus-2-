package nexus_http;

import java.io.IOException;
import java.io.OutputStream;

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
	private OutputStream body;
	
	
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new response
	 */
	public Response()
	{
		this.status = HttpStatus.OK;
		this.headers = new Headers();
		this.body = null;
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
	 * @return The body part of this response. Null if not initialized.
	 */
	public OutputStream getBody()
	{
		return this.body;
	}
	
	/**
	 * Adds a body to the response. If there was a body previously, closes it.
	 * @param body The body part of the response.
	 */
	public void setBody(OutputStream body)
	{
		if (this.body != null)
		{
			try
			{
				this.body.close();
			}
			catch (IOException e)
			{
				System.err.println("Couldn't close previous response body");
				e.printStackTrace();
			}
		}
		
		this.body = body;
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
