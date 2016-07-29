package nexus_rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import flow_structure.TreeNode;
import nexus_http.HttpException;
import nexus_http.HttpStatus;
import nexus_http.Link;
import nexus_http.Method;
import nexus_http.Path;
import nexus_http.Request;
import nexus_http.Response;
import nexus_rest.ResourceWriter.ResourceWriterException;

/**
 * Resources are elements that can be connected to each other with links. The resources can 
 * also be manipulated with requests and oftentimes sent via responses. Resources reside on 
 * a (virtual) path.
 * @author Mikko Hilpinen
 * @since 10.10.2015
 */
public interface Resource
{	
	// TODO: Make an abstract class version?
	
	/**
	 * @return The path leading to the resource. The resource should be accessible through 
	 * the provided path.
	 */
	public Path getPath();
	
	/**
	 * @return All the methods this resource allows on itself
	 */
	public Method[] getAllowedMethods();
	
	/**
	 * Creates a new resource, element, attribute, etc. Under the resource. In case a new 
	 * resource was created, it should be returned as well. The resource shouldn't expect this 
	 * method call, if it doesn't allow POST.
	 * @param request The request for the operation
	 * @param response The response that will be sent to the client on success. The resource 
	 * may modify the response status and headers, etc. if it needs to.
	 * @return A link to the newly created resource
	 * @throws HttpException If the operation wasn't completed successfully
	 */
	public Link post(Request request, Response response) throws HttpException;
	
	/**
	 * Modifies the resource somehow.  The resource shouldn't expect this 
	 * method call, if it doesn't allow PUT.
	 * @param request The request for the operation
	 * @param response The response that will be sent to the client on success. The resource 
	 * may modify the response status and headers, etc. if it needs to.
	 * @throws HttpException If the operation wasn't carried out
	 */
	public void put(Request request, Response response) throws HttpException;
	
	/**
	 * Deletes the resource.  The resource shouldn't expect this 
	 * method call, if it doesn't allow DELETE.
	 * @param request The request for the operation
	 * @param response The response that will be sent to the client on success. The resource 
	 * may modify the response status and headers, etc. if it needs to.
	 * @throws HttpException If the operation wasn't carried out
	 */
	public void delete(Request request, Response response) throws HttpException;
	
	/**
	 * This method is used for finding targeted resources under / connected to a certain 
	 * resource. If one or more of the targeted resources can't be found, the operation fails. 
	 * The resources should should be ordered hierarchically, so that resource found under 
	 * other returned resources are returned below them.
	 * @param targetPaths The target resource paths. The resources that should be returned by 
	 * this method are the "included" nodes of the path.
	 * @return The resources in the target path(s) that are marked as included. The returned 
	 * collection(s) need to be hierarchically structured.
	 * @throws HttpException If the resource can't find a requested resource or another 
	 * error occurs
	 */
	public Collection<TreeNode<Resource>> findConnectedResources(Collection<? extends Path> 
			targetPaths) throws HttpException;
	
	/**
	 * In this method the resource should write itself with the provided writer. The resource 
	 * should also write the provided resources that should reside under it. The resource 
	 * shouldn't expect this method call, if it doesn't allow GET.
	 * @param writer The writer that is used in the process.
	 * @param subResources The resources under this one that should be written inside this 
	 * resource.
	 * @throws HttpException If the operation wasn't carried out
	 * @throws ResourceWriterException If the writing failed
	 * @see #getPathBetween(Resource, Resource)
	 */
	public void write(ResourceWriter writer, 
			Collection<? extends TreeNode<? extends Resource>> subResources) 
			throws HttpException, ResourceWriterException;
	
	/**
	 * This method writes each of the subresources, presumably residing under the parent 
	 * resource, consecutively, using the provided writer.
	 * @param writer The writer that writes the resources.
	 * @param subResources The resources that should be written
	 * @throws HttpException If the operation failed
	 * @throws ResourceWriterException If the writing failed
	 */
	public static void writeResourcesUnder(ResourceWriter writer, 
			Collection<? extends TreeNode<? extends Resource>> subResources) throws 
			HttpException, ResourceWriterException
	{
		// Writes the included resources as children
		for (TreeNode<? extends Resource> childNode : subResources)
		{
			childNode.getContent().write(writer, childNode.getChildren());
		}
	}
	
	/**
	 * Finds the path separating the two resources
	 * @param upper The presumably upper resource
	 * @param lower The presumably lower resource
	 * @return The path between the two resources. Null if there is no path between or 
	 * connecting the resources
	 */
	public static Path getPathBetween(Resource upper, Resource lower)
	{
		return Path.getPathBetween(upper.getPath(), lower.getPath(), false);
	}
	
	/**
	 * Parses the name of the resource that contains the leading path information, if present
	 * @param resource The resource who's name is parsed
	 * @param leadingPath The path leading to the resource. May be null.
	 * @return The name of the resource, including the leading path (where applicable).
	 */
	public static String parseResourceElementName(Resource resource, Path leadingPath)
	{
		String resourceName = getResourceName(resource);
		
		if (leadingPath == null)
			return resourceName;
		else
			return leadingPath.toString() + Path.DS + resourceName;
	}
	
	/**
	 * Finds the name of the resource from its path.
	 * @param resource The resource
	 * @return The name of the resource
	 */
	public static String getResourceName(Resource resource)
	{
		return resource.getPath().getContent();
	}
	
	/**
	 * Checks whether the provided resource allows use of the given method
	 * @param resource The resource that is targeted with the method
	 * @param method The method that would be used on the resource
	 * @return Would the resource allow the use of the method
	 */
	public static boolean resourceAllowsMethod(Resource resource, Method method)
	{
		for (Method allowed : resource.getAllowedMethods())
		{
			if (allowed == method)
				return true;
		}
		
		return false;
	}
	
	/**
	 * Finds all the resources directly below the provided resource node in a hierarchical 
	 * tree structure.
	 * @param node The node who's children are collected
	 * @return All the direct children of the provided node
	 */
	public static List<Resource> getDirectNodeChildren(TreeNode<? extends Resource> node)
	{
		List<Resource> resources = new ArrayList<>();
		
		for (TreeNode<? extends Resource> child : node.getChildren())
		{
			resources.add(child.getContent());
		}
		
		return resources;
	}
	
	/**
	 * Finds all the resources stored in a hierarchical tree structure
	 * @param resourceTree A resource tree
	 * @return The root node resource and each resource under it.
	 */
	public static List<Resource> getResourcesFromTree(TreeNode<? extends Resource> resourceTree)
	{
		List<Resource> resources = new ArrayList<>();
		
		resources.add(resourceTree.getContent());
		for (TreeNode<? extends Resource> child : resourceTree.getChildren())
		{
			resources.addAll(getResourcesFromTree(child));
		}
		
		return resources;
	}
	
	/**
	 * Finds all the resources stored in a set of hierarchical tree structures
	 * @param resourceTrees A collection of resource trees
	 * @return All resources included in the provided nodes and those under them
	 */
	public static List<Resource> getResourcesFromTreeCollection(
			Collection<? extends TreeNode<? extends Resource>> resourceTrees)
	{
		List<Resource> resources = new ArrayList<>();
		for (TreeNode<? extends Resource> resourceTree : resourceTrees)
		{
			resources.addAll(getResourcesFromTree(resourceTree));
		}
		
		return resources;
	}
	
	/**
	 * Searches through a collection for a resource with a specific name
	 * @param resources The resources that are searched through
	 * @param targetName The name of the target resource
	 * @return A resource with the given name or null if the collection didn't contain a 
	 * resource with the given name
	 */
	public static Resource findResourceWithName(Collection<? extends Resource> resources, 
			String targetName)
	{
		for (Resource resource : resources)
		{
			if (getResourceName(resource).equalsIgnoreCase(targetName))
				return resource;
		}
		
		return null;
	}
	
	/**
	 * Creates a hierarchical resource collection from a set of resources and their children. 
	 * Only the resources who's path is marked as included are included in this collection.
	 * @param resources The resources included in the search. The resources should be parents 
	 * or children for each other. Usually this would be a set of siblings under a resource.
	 * @param targetPaths A collection of paths that should be represented in the returned 
	 * collection. Each path node should represent a resource in the provided collection
	 * @return A hierarchical resource collection that contains the resources (and their 
	 * children) that are marked as included in the target paths.
	 * @throws HttpException If all of the target paths weren't represented in the provided 
	 * resource collection or if one of the resources couldn't find the correct resources 
	 * under it
	 */
	public static List<TreeNode<Resource>> findIncludedResources(
			Collection<? extends Resource> resources, Collection<? extends Path> targetPaths) 
			throws HttpException
	{
		// TODO: Add support for *, somewhere
		List<TreeNode<Resource>> includedTrees = new ArrayList<>();
		
		for (Path targetPath : targetPaths)
		{
			Resource rootResource = findResourceWithName(resources, targetPath.getContent());
			
			if (rootResource == null)
				throw new HttpException(HttpStatus.NOT_FOUND, "Can't find the resource at " + 
						targetPath);
			
			Collection<TreeNode<Resource>> includedChildren = rootResource.findConnectedResources(
					targetPath.getChildPaths());
			
			if (targetPath.isIncluded())
			{
				TreeNode<Resource> root = new TreeNode<>(rootResource, null);
				for (TreeNode<Resource> child : includedChildren)
				{
					root.addChild(child);
				}
				
				includedTrees.add(root);
			}
			else
				includedTrees.addAll(includedChildren);
		}
		
		return includedTrees;
	}
}
