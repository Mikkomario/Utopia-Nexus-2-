package nexus_rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import flow_structure.TreeNode;
import nexus_http.ContentType;
import nexus_http.HttpException;
import nexus_http.InternalServerException;
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
	private List<ContentType> contentTypes;
	
	
	// CONSTRUCTOR	---------------------
	
	/**
	 * Creates a new rest handler.
	 * @param writerProvider The object that is able to provide resourceWriters for the handler
	 * @param defaultContentType The content type that is used by default
	 * @param supportedTypes The other content types that can be produced
	 */
	public RestRequestHandler(ResourceWriterProvider writerProvider, 
			ContentType defaultContentType, ContentType... supportedTypes)
	{
		this.writerProvider = writerProvider;
		this.resources = new ArrayList<>();
		this.contentTypes = new ArrayList<>();
		
		this.contentTypes.add(defaultContentType);
		for (ContentType type : supportedTypes)
		{
			this.contentTypes.add(type);
		}
	}
	
	
	// IMPLEMENTED METHODS	------------

	@Override
	public Response handle(Request request)
	{
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
			
			// Performs the operation on said resources
			// GET and POST are the only methods that return a body. Head works like GET but 
			// doesn't write anything
			if (request.getMethod() == Method.GET || request.getMethod() == Method.POST || 
					request.getMethod() == Method.HEAD)
			{
				// Creates the response and adds the content-type header
				ContentType contentType = getContentTypeFor(request);
				Response response = new Response();
				response.getHeaders().setContentType(contentType);
				
				if (request.getMethod() == Method.HEAD)
					return response;
				else
				{
					OutputStream body = new ByteArrayOutputStream();
					ResourceWriter writer = null;
					Path lastLocation = null;
					try
					{
						writer = this.writerProvider.createWriter(body, contentType);
						writer.writeResourceStart("body");
						
						// With GET, writes all targeted resources
						if (request.getMethod() == Method.GET)
						{
							for (TreeNode<Resource> resourceTree : targetResourceTrees)
							{
								lastLocation = resourceTree.getContent().getPath();
								resourceTree.getContent().writeContents(writer, 
										resourceTree.getChildren());
							}
						}
						// With POST, writes the link(s)
						else
						{
							for (Resource resource : targetResources)
							{
								lastLocation = resource.getPath();
								writer.writeLink(resource.post(request));
							}
						}
						
						writer.writeResourceEnd();
						writer.writeDocumentEnd();
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
						try
						{
							body.close();
						}
						catch (IOException e1)
						{
							System.err.println("Failed to close the response body");
						}
					}
				}
				
				return response;
			}
			// Other method types don't return a body
			else
			{
				// Put modifies all the resources
				if (request.getMethod() == Method.PUT)
				{
					for (Resource resource : targetResources)
					{
						resource.put(request);
					}
				}
				// Delete deletes the resources
				else
				{
					for (Resource resource : targetResources)
					{
						resource.delete(request);
					}
				}
				
				return new Response();
			}
		}
		catch (InternalServerException e)
		{
			// TODO: Use events instead?
			System.err.println("Internal server error at: " + 
					(e.getSourceLocation() != null ? e.getSourceLocation() : "?"));
			e.printStackTrace();
			if (e.getRequest() != null)
				e.getRequest().toString();
			
			return new Response(e);
		}
		catch (HttpException e)
		{
			return new Response(e);
		}
	}

	
	// OTHER METHODS	-------------------
	
	private ContentType getContentTypeFor(Request request)
	{
		ContentType bestType = request.getHeaders().getAcceptHeader().getPrefferedContentType(
				this.contentTypes);
		if (bestType == null)
			bestType = this.contentTypes.get(0);
		
		return bestType;
	}
}
