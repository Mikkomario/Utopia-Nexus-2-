package nexus_rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import flow_structure.TreeNode;
import nexus_http.HttpException;
import nexus_http.HttpStatus;
import nexus_http.InternalServerException;
import nexus_http.Link;
import nexus_http.Method;
import nexus_http.MethodNotAllowedException;
import nexus_http.Path;
import nexus_http.Request;
import nexus_http.RequestHandler;
import nexus_http.Response;
import nexus_rest.ResourceWriter.ResourceWriterException;

/**
 * The rest request handler operates on a hierarchical resource system, and performs the 
 * requested operations on it.
 * @author Mikko Hilpinen
 * @since 11.10.2015
 */
public class RestRequestHandler implements RequestHandler
{
	// ATTRIBUTES	---------------------
	
	private Collection<Resource> resources;
	private ResourceWriterProvider writerProvider;
	
	
	// CONSTRUCTOR	---------------------
	
	/**
	 * Creates a new rest handler.
	 * @param writerProvider The object that is able to provide resourceWriters for the handler
	 */
	public RestRequestHandler(ResourceWriterProvider writerProvider)
	{
		this.writerProvider = writerProvider;
		this.resources = new ArrayList<>();
	}
	
	
	// IMPLEMENTED METHODS	------------

	@Override
	public Response handle(Request request)
	{
		// Creates the response, which may be modified by the target operations
		Response response = new Response();
		try
		{
			// Finds the targeted resource(s)
			Collection<Path> targetPaths = request.getPaths();
			List<TreeNode<Resource>> targetResourceTrees = Resource.findIncludedResources(
					this.resources, targetPaths);
			
			// Lists all the targeted resources in a list form as well
			List<Resource> targetResources = 
					Resource.getResourcesFromTreeCollection(targetResourceTrees);
			
			// Checks that the request method is applicable for all the resources
			for (Resource resource : targetResources)
			{
				if (!Resource.resourceAllowsMethod(resource, request.getMethod()))
					throw new MethodNotAllowedException(resource.getPath().getContent() + 
							" doesn't allow " + request.getMethod(), resource.getAllowedMethods());
			}
			
			try
			{
				// Performs the operation on said resources
				// GET and POST are the only methods that return a body. Head works like GET but 
				// doesn't write anything
				if (request.getMethod() == Method.GET || request.getMethod() == Method.POST || 
						request.getMethod() == Method.HEAD)
				{
					ResourceWriter writer = null;
					Path lastLocation = null;
					try
					{
						writer = this.writerProvider.createWriter(response.getBody(true), 
								request.getHeaders());
						
						// Modifies the headers
						response.getHeaders().setContentType(writer.getContentType(), 
								writer.getCharset());
						response.getHeaders().setLinkWriteStyle(writer.getLinkWriteStyle());
						
						// Writing is only done for GET and POST
						if (request.getMethod() != Method.HEAD)
						{
							writer.writeDocumentStart("body");
							
							// With GET, writes all targeted resources
							if (request.getMethod() == Method.GET)
							{
								for (TreeNode<Resource> resourceTree : targetResourceTrees)
								{
									lastLocation = resourceTree.getContent().getPath();
									resourceTree.getContent().write(writer, 
											resourceTree.getChildren());
								}
							}
							// With POST, writes the link(s)
							else
							{
								for (Resource resource : targetResources)
								{
									lastLocation = resource.getPath();
									Link link = resource.post(request, response);
									if (link != null)
									{
										writer.writeLink(link);
										// Also adds a location header
										response.getHeaders().setLocation(link.getTargetPath());
									}
								}
							}
							
							writer.writeResourceEnd();
							writer.writeDocumentEnd();
						}
					}
					catch (ResourceWriterException e)
					{
						throw new InternalServerException("Resource writing failed", e, request, 
								lastLocation);
					}
					finally
					{
						if (writer != null)
							writer.close();
					}
				}
				// Other method types don't return a body (by default)
				else
				{
					// Put modifies all the resources
					if (request.getMethod() == Method.PUT)
					{
						for (Resource resource : targetResources)
						{
							resource.put(request, response);
						}
					}
					// Delete deletes the resources
					else
					{
						for (Resource resource : targetResources)
						{
							resource.delete(request, response);
						}
					}
				}
			}
			finally
			{
				/* TODO: Readd
				try
				{
					// Always closes the response body before sending it
					//OutputStream body = response.getBody(false);
					//if (body != null)
					//	body.close();
				}
				catch (IOException e1)
				{
					System.err.println("Failed to close the response body");
				}
				*/
			}
		}
		catch (InternalServerException e)
		{
			// TODO: Use events instead?
			System.err.println("Internal server error at: " + 
					(e.getSourceLocation() != null ? e.getSourceLocation() : "?"));
			e.printStackTrace();
			if (e.getRequest() != null)
				System.err.println(e.getRequest().toString());
			
			// Returns an error response
			return new Response(e);
		}
		catch (HttpException e)
		{
			// Returns an error response
			return new Response(e);
		}
		
		// Updates response http status, if it hasn't been set yet
		if (response.getStatus() == null)
			response.setStatus(HttpStatus.OK);
		
		// Sends the successful response
		return response;
	}
	
	
	// OTHER METHODS	----------------------
	
	/**
	 * Adds a new root resource to the handler
	 * @param root the new resource
	 */
	public void addRootResource(Resource root)
	{
		if (!this.resources.contains(root))
			this.resources.add(root);
	}
}
