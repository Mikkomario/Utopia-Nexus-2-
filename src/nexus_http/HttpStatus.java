package nexus_http;

/**
 * These are the different status codes used by the service. The documentation references 
 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 * @author Mikko Hilpinen
 * @since 10.10.2015
 */
public enum HttpStatus
{
	/**
	 * "The request has succeeded. The information returned with the response is dependent on 
	 * the method used in the request, for example:<br>
	 * GET an entity corresponding to the requested resource is sent in the response<br>
	 * HEAD the entity-header fields corresponding to the requested resource are sent in the response without any message-body<br>
	 * POST an entity describing or containing the result of the action"
	 */
	OK(200),
	/**
	 * "The request has been fulfilled and resulted in a new resource being created. 
	 * The newly created resource can be referenced by the URI(s) returned in the entity of 
	 * the response, with the most specific URI for the resource given by a Location header 
	 * field. The response SHOULD include an entity containing a list of resource 
	 * characteristics and location(s) from which the user or user agent can choose the one 
	 * most appropriate. The entity format is specified by the media type given in the 
	 * Content-Type header field. The origin server MUST create the resource before returning 
	 * the 201 status code. If the action cannot be carried out immediately, the server 
	 * SHOULD respond with 202 (Accepted) response instead."
	 */
	CREATED(201),
	/**
	 * "The request has been accepted for processing, but the processing has not been completed. 
	 * The request might or might not eventually be acted upon, as it might be disallowed when 
	 * processing actually takes place. There is no facility for re-sending a status code from 
	 * an asynchronous operation such as this.<p>
	 * The 202 response is intentionally non-committal. Its purpose is to allow a server to 
	 * accept a request for some other process (perhaps a batch-oriented process that is only 
	 * run once per day) without requiring that the user agent's connection to the server 
	 * persist until the process is completed. The entity returned with this response SHOULD 
	 * include an indication of the request's current status and either a pointer to a status 
	 * monitor or some estimate of when the user can expect the request to be fulfilled."
	 */
	ACCEPTED(202),
	/**
	 * "The server has fulfilled the request but does not need to return an entity-body, and 
	 * might want to return updated metainformation. The response MAY include new or updated 
	 * metainformation in the form of entity-headers, which if present SHOULD be associated 
	 * with the requested variant.<p>
	 * 
	 * If the client is a user agent, it SHOULD NOT change its document view from that which 
	 * caused the request to be sent. This response is primarily intended to allow input for 
	 * actions to take place without causing a change to the user agent's active document 
	 * view, although any new or updated metainformation SHOULD be applied to the document 
	 * currently in the user agent's active view.<p>
	 * 
	 * The 204 response MUST NOT include a message-body, and thus is always terminated by the 
	 * first empty line after the header fields."
	 */
	NO_CONTENT(204),
	/**
	 * "The requested resource has been assigned a new permanent URI and any future references 
	 * to this resource SHOULD use one of the returned URIs. Clients with link editing 
	 * capabilities ought to automatically re-link references to the Request-URI to one or 
	 * more of the new references returned by the server, where possible. This response is 
	 * cacheable unless indicated otherwise.<p>
	 * 
	 * The new permanent URI SHOULD be given by the Location field in the response. 
	 * Unless the request method was HEAD, the entity of the response SHOULD contain a short 
	 * hypertext note with a hyperlink to the new URI(s).<p>
	 * 
	 * If the 301 status code is received in response to a request other than GET or HEAD, 
	 * the user agent MUST NOT automatically redirect the request unless it can be confirmed 
	 * by the user, since this might change the conditions under which the request was issued."
	 */
	MOVED_PERMANENTLY(301),
	/**
	 * "The requested resource resides temporarily under a different URI. Since the 
	 * redirection MAY be altered on occasion, the client SHOULD continue to use the 
	 * Request-URI for future requests. This response is only cacheable if indicated by a 
	 * Cache-Control or Expires header field.<p>
	 * 
	 * The temporary URI SHOULD be given by the Location field in the response. Unless the 
	 * request method was HEAD, the entity of the response SHOULD contain a short hypertext 
	 * note with a hyperlink to the new URI(s) , since many pre-HTTP/1.1 user agents do not 
	 * understand the 307 status. Therefore, the note SHOULD contain the information necessary 
	 * for a user to repeat the original request on the new URI.<p>
	 * 
	 * If the 307 status code is received in response to a request other than GET or HEAD, 
	 * the user agent MUST NOT automatically redirect the request unless it can be confirmed 
	 * by the user, since this might change the conditions under which the request was issued."
	 */
	TEMPORARY_REDIRECT(307),
	/**
	 * "The request could not be understood by the server due to malformed syntax. The client 
	 * SHOULD NOT repeat the request without modifications."
	 */
	BAD_REQUEST(400),
	/**
	 * The request requires user authentication. The response MUST include a WWW-Authenticate 
	 * header field (section 14.47) containing a challenge applicable to the requested 
	 * resource. The client MAY repeat the request with a suitable Authorization header 
	 * field (section 14.8). If the request already included Authorization credentials, then 
	 * the 401 response indicates that authorization has been refused for those credentials. 
	 * If the 401 response contains the same challenge as the prior response, and the user 
	 * agent has already attempted authentication at least once, then the user SHOULD be 
	 * presented the entity that was given in the response, since that entity might include 
	 * relevant diagnostic information.".
	 */
	UNAUTHORIZED(401),
	/**
	 * "The server understood the request, but is refusing to fulfill it. Authorization will 
	 * not help and the request SHOULD NOT be repeated. If the request method was not HEAD 
	 * and the server wishes to make public why the request has not been fulfilled, it SHOULD 
	 * describe the reason for the refusal in the entity. If the server does not wish to make 
	 * this information available to the client, the status code 404 (Not Found) can be used 
	 * instead."
	 */
	FORBIDDEN(403),
	/**
	 * "The server has not found anything matching the Request-URI. No indication is given of 
	 * whether the condition is temporary or permanent. The 410 (Gone) status code SHOULD be 
	 * used if the server knows, through some internally configurable mechanism, that an old 
	 * resource is permanently unavailable and has no forwarding address. This status code is 
	 * commonly used when the server does not wish to reveal exactly why the request has been 
	 * refused, or when no other response is applicable."
	 */
	NOT_FOUND(404),
	/**
	 * "The method specified in the Request-Line is not allowed for the resource identified by 
	 * the Request-URI. The response MUST include an Allow header containing a list of valid 
	 * methods for the requested resource."
	 */
	METHOD_NOT_ALLOWED(405),
	/**
	 * The requested resource is no longer available at the server and no forwarding address 
	 * is known. This condition is expected to be considered permanent. Clients with link 
	 * editing capabilities SHOULD delete references to the Request-URI after user approval. 
	 * If the server does not know, or has no facility to determine, whether or not the 
	 * condition is permanent, the status code 404 (Not Found) SHOULD be used instead. This 
	 * response is cacheable unless indicated otherwise.<p>
	 * 
	 * The 410 response is primarily intended to assist the task of web maintenance by 
	 * notifying the recipient that the resource is intentionally unavailable and that the 
	 * server owners desire that remote links to that resource be removed. Such an event is 
	 * common for limited-time, promotional services and for resources belonging to 
	 * individuals no longer working at the server's site. It is not necessary to mark all 
	 * permanently unavailable resources as "gone" or to keep the mark for any length of 
	 * time -- that is left to the discretion of the server owner.
	 */
	GONE(410),
	/**
	 * The server failed to complete the request due to an error
	 */
	INTERNAL_SERVER_ERROR(500),
	/**
	 * The command / function is not implemented on server side
	 */
	NOT_IMPLEMENTED(501),
	/**
	 * The server is currently unable to handle the request due to a temporary overloading or 
	 * maintenance of the server. The implication is that this is a temporary condition which 
	 * will be alleviated after some delay. If known, the length of the delay MAY be indicated 
	 * in a Retry-After header. If no Retry-After is given, the client SHOULD handle the 
	 * response as it would for a 500 response.
	 */
	SERVICE_UNAVAILABLE(503);
	
	
	// ATTRIBUTES	-------------
	
	private final int code;
	
	
	// CONSTRUCTOR	-------------
	
	private HttpStatus(int code)
	{
		this.code = code;
	}
	
	
	// ACCESSORS	-------------
	
	/**
	 * @return The integer status code for this status
	 */
	public int getStatusCode()
	{
		return this.code;
	}
	
	
	// OTHER METHODS	---------
	
	/**
	 * @return The category this status code lies in
	 */
	public StatusCategory getCategory()
	{
		if (this.code < 300)
			return StatusCategory.OK;
		else if (this.code < 400)
			return StatusCategory.REDIRECT;
		else if (this.code < 500)
			return StatusCategory.REQUEST_FAILURE;
		else
			return StatusCategory.SERVER_FAILURE;
	}
	
	/**
	 * Finds a http status for the provided status code
	 * @param statusCode The status code
	 * @return The http status for that code or null if no http status has been introduced for 
	 * the code
	 */
	public static HttpStatus parseFromInt(int statusCode)
	{
		for (HttpStatus status : values())
		{
			if (status.getStatusCode() == statusCode)
				return status;
		}
		
		return null;
	}
	
	
	// ENUMS	-----------------
	
	/**
	 * These are the more broad categories that each enclose multiple http statuses
	 * @author Mikko Hilpinen
	 * @since 10.10.2015
	 */
	public static enum StatusCategory
	{
		/**
		 * Use when the http status is ok
		 */
		OK,
		/**
		 * Used on temporary and permanent redirects
		 */
		REDIRECT,
		/**
		 * Used when the client's request was not accepted
		 */
		REQUEST_FAILURE,
		/**
		 * Used when the server couldn't respond to a request
		 */
		SERVER_FAILURE;
	}
}
