package nexus_rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nexus_http.HttpException;
import nexus_http.HttpStatus;
import nexus_http.Link;
import nexus_http.Method;
import nexus_http.Path;
import nexus_http.Request;
import nexus_http.Response;
import nexus_rest.ResourceWriter.ResourceWriterException;
import flow_structure.TreeNode;

/**
 * This is a simple implementation of the Resource interface. The resource stores string 
 * properties as well as links to other resources.
 * @author Mikko Hilpinen
 * @since 24.10.2015
 */
public class SimpleResource implements Resource
{
	// ATTRIBUTES	---------------------
	
	private static final String NAME_PROPERTY = "name";
	
	private Method[] allowedMethods;
	private Path path;
	private Map<String, Resource> links;
	private Map<String, String> properties;
	
	
	// CONSTRUCTOR	---------------------
	
	/**
	 * Creates a new simple resource
	 * @param path The path of the resource
	 * @param allowedMethods The methods the resource allows
	 */
	public SimpleResource(Path path, Method... allowedMethods)
	{
		this.allowedMethods = noDelete(allowedMethods);
		this.links = new HashMap<>();
		this.properties = new HashMap<>();
		this.path = path;
	}
	
	
	// IMPLEMENTED METHODS	-------------

	@Override
	public String toString()
	{
		return getPath().toString();
	}
	
	@Override
	public Path getPath()
	{
		return this.path;
	}

	@Override
	public Method[] getAllowedMethods()
	{
		return this.allowedMethods;
	}

	/**
	 * Creates a new simple resource (with similar restrictions) under this one. Requires 
	 * parameter 'name'. The other parameters will be added as properties to the lower element.
	 */
	@Override
	public Link post(Request request, Response response) throws HttpException
	{
		// Checks the parameters
		String name = request.getParameters().getParameterValue(NAME_PROPERTY);
		if (name == null)
			throw new HttpException(HttpStatus.BAD_REQUEST, "Parameter '" + NAME_PROPERTY + 
					"' required");
		if (this.links.containsKey(name.toLowerCase()))
			throw new HttpException(HttpStatus.FORBIDDEN, "Can't overwrite resource " + name);
		
		// Creates the new resource
		SimpleResource child = new SimpleResource(new Path(name, getPath(), false), 
				this.allowedMethods);
		for (String parameterName : request.getParameters().getParameterNames())
		{
			child.putPoperty(parameterName, request.getParameters().getParameterValue(parameterName));
		}
		this.links.put(name.toLowerCase(), child);
		
		// Returns a link to the resource
		response.setStatus(HttpStatus.CREATED);
		return new Link(name, child.getPath());
	}

	/**
	 * Modifies an existing property of a resource, or adds new ones
	 */
	@Override
	public void put(Request request, Response response) throws HttpException
	{
		for(String parameterName : request.getParameters().getParameterNames())
		{
			putPoperty(parameterName, request.getParameters().getParameterValue(parameterName));
		}
	}

	/**
	 * Doesn't support DELETE
	 */
	@Override
	public void delete(Request request, Response response) throws HttpException
	{
		// Ignored
	}

	@Override
	public Collection<TreeNode<Resource>> findConnectedResources(
			Collection<? extends Path> targetPaths) throws HttpException
	{
		return Resource.findIncludedResources(this.links.values(), targetPaths);
	}

	@Override
	public void write(ResourceWriter writer,
			Collection<? extends TreeNode<? extends Resource>> subResources, Path leadingPath)
			throws HttpException, ResourceWriterException
	{
		writer.writeResourceStart(Resource.parseResourceElementName(this, leadingPath), 
				getPath());
		
		// Writes all the properties
		for (String propertyName : this.properties.keySet())
		{
			writer.writeProperty(propertyName, this.properties.get(propertyName));
		}
		
		// Writes the included resources as children
		Resource.writeResourcesUnder(writer, this, subResources);
		
		writer.writeResourceEnd();
	}

	
	// OTHER METHODS	------------------
	
	/**
	 * Adds a new property to the resource
	 * @param propertyName The name of the property
	 * @param value The new value of the property
	 * @throws HttpException If the name property was being modified
	 */
	public void putPoperty(String propertyName, String value) throws HttpException
	{
		// Name can't be overwritten
		if (propertyName.equalsIgnoreCase(NAME_PROPERTY) && this.properties.containsKey(
				NAME_PROPERTY))
			throw new HttpException(HttpStatus.FORBIDDEN, NAME_PROPERTY + " can't be modified");
		
		this.properties.put(propertyName.toLowerCase(), value);
	}
	
	/**
	 * Links a new resource to this one
	 * @param linkName The name of the link
	 * @param resource The resource at the end of the link
	 */
	public void putLink(String linkName, Resource resource)
	{
		this.links.put(linkName, resource);
	}
	
	private static Method[] noDelete(Method[] methods)
	{
		boolean containedDelete = false;
		List<Method> methodsWithoutDelete = new ArrayList<>();
		for (Method method : methods)
		{
			if (method == Method.DELETE)
				containedDelete = true;
			else
				methodsWithoutDelete.add(method);
		}
		
		if (!containedDelete)
		{
			methodsWithoutDelete.clear();
			return methods;
		}
		else
			return methodsWithoutDelete.toArray(new Method[methodsWithoutDelete.size()]);
	}
}
