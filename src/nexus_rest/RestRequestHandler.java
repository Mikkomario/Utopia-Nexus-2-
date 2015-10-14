package nexus_rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import flow_structure.TreeNode;
import nexus_http.HttpException;
import nexus_http.HttpStatus;
import nexus_http.InternalServerExeption;
import nexus_http.Path;
import nexus_http.Request;
import nexus_http.RequestHandler;
import nexus_http.Response;

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
	
	
	// CONSTRUCTOR	---------------------
	
	/**
	 * Creates a new rest handlers.
	 */
	public RestRequestHandler()
	{
		this.resources = new ArrayList<>();
	}
	
	
	// IMPLEMENTED METHODS	------------

	@Override
	public Response handle(Request request)
	{
		try
		{
			// Finds the targeted resource(s)
			Collection<Path> targetPaths = request.getPaths();
			List<TreeNode<Resource>> targetResources = new ArrayList<>();
			
			for (Path target : targetPaths)
			{
				Resource rootResource = getResourceWithName(target.getContent());
				
				Collection<TreeNode<Resource>> targets = rootResource.findConnectedResources(
						target.getChildPaths());
				
				if (target.isIncluded())
				{
					TreeNode<Resource> root = new TreeNode<>(rootResource, null);
					for (TreeNode<Resource> child : targets)
					{
						root.addChild(child);
					}
					
					targetResources.add(root);
				}
				else
					targetResources.addAll(targets);
			}
			
			// Checks that the request method is applicable for all the resources
			
			// Performs the operation on said resources
			
			
		}
		catch (InternalServerExeption e)
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
		
		
		return null;
	}

	
	// OTHER METHODS	-------------------
	
	private Resource getResourceWithName(String name) throws HttpException
	{
		for (Resource resource : this.resources)
		{
			if (Resource.getResourceName(resource).equalsIgnoreCase(name))
				return resource;
		}
		
		throw new HttpException(HttpStatus.NOT_FOUND);
	}
}
