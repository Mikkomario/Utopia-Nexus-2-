package nexus_http;

/**
 * The request handlers are able to form a response to a client's request
 * @author Mikko Hilpinen
 * @since 10.10.2015
 */
public interface RequestHandler
{
	/**
	 * The handler receives the request and provides a response when ready. Usually this 
	 * method would be called in a separate thread.
	 * @param request The request sent by the client
	 * @return The handler's response to the request
	 */
	public Response handle(Request request);
}
