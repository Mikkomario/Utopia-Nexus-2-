package nexus_rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import flow_structure.TreeNode;
import nexus_http.HttpException;
import nexus_http.Link;
import nexus_http.Method;
import nexus_http.Path;
import nexus_http.Request;

/**
 * Resources are elements that can be connected to each other with links. The resources can 
 * also be manipulated with requests and oftentimes sent via responses. Resources reside on 
 * a (virtual) path.
 * @author Mikko Hilpinen
 * @since 10.10.2015
 */
public interface Resource
{	
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
	 * @return A link to the newly created resource
	 * @throws HttpException If the operation wasn't completed
	 */
	public Link post(Request request) throws HttpException;
	
	/**
	 * Modifies the resource somehow.  The resource shouldn't expect this 
	 * method call, if it doesn't allow PUT.
	 * @param request The request for the operation
	 * @throws HttpException If the operation wasn't carried out
	 */
	public void put(Request request) throws HttpException;
	
	/**
	 * Deletes the resource.  The resource shouldn't expect this 
	 * method call, if it doesn't allow DELETE.
	 * @param request The request for the operation
	 * @throws HttpException If the operation wasn't carried out
	 */
	public void delete(Request request) throws HttpException;
	
	/**
	 * This method is used for finding targeted resources under / connected to a certain 
	 * resource. If one or more of the targeted resources can't be found, the operation fails. 
	 * The resources should should be ordered hierarchically, so that resource found under 
	 * other returned resources are returned below them.
	 * @param targetPaths The target resource paths. The resources that should be returned by 
	 * this method are the "included" nodes of the path.
	 * @return The resources in the target path that are marked as included. The returned 
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
	 * @throws HttpException If the writing wasn't carried out
	 */
	public void writeContents(ResourceWriter writer, 
			Collection<? extends TreeNode<? extends Resource>> subResources) throws HttpException;
	
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
	public static List<Resource> getDirectNodeChilder(TreeNode<Resource> node)
	{
		List<Resource> resources = new ArrayList<>();
		
		for (TreeNode<Resource> child : node.getChildren())
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
	public static List<Resource> getResourcesFromTree(TreeNode<Resource> resourceTree)
	{
		List<Resource> resources = new ArrayList<>();
		
		resources.add(resourceTree.getContent());
		for (TreeNode<Resource> child : resourceTree.getChildren())
		{
			resources.addAll(getResourcesFromTree(child));
		}
		
		return resources;
	}
}
