package nexus_http;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import flow_structure.TreeNode;

/**
 * Path is constructed from client uri. The path may lead to one or more nodes
 * @author Mikko Hilpinen
 * @since 6.10.2015
 */
public class Path extends TreeNode<String>
{
	// ATTRIBUTES	-------------------
	
	/**
	 * The directory / resource separator (/).
	 * Used for traversing down the path
	 */
	public static final char DS = '/';
	/**
	 * The directory / resource inclusion (+).
	 * Used for adding new children / branches
	 */
	public static final char INCLUSION = '+';
	
	private boolean included;
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new path piece
	 * @param partName The name of the current path part
	 * @param parent The last part of the path
	 * @param included Is this path part marked as included (final branch nodes don't count)
	 */
	public Path(String partName, Path parent, boolean included)
	{
		super(partName, parent);
		
		this.included = included;
	}
	
	
	// IMPLEMENTED METHODS	-----------
	
	@Override
	public Path getParent()
	{
		TreeNode<String> parent = super.getParent();
		if (parent instanceof Path)
			return (Path) parent;
		return null;
	}
	
	/**
	 * Parses the path into an url line
	 */
	@Override
	public String toString()
	{
		return getLeadingPath() + getContent();
	}
	
	@Override
	public Path getChild(int index)
	{
		TreeNode<String> child = super.getChild(index);
		if (child instanceof Path)
			return (Path) child;
		return null;
	}
	
	
	// ACCESSORS	-------------------
	
	/**
	 * @return Is this path part marked as included. Also, the nodes at the end of the 
	 * branches are always included
	 */
	public boolean isIncluded()
	{
		return this.included || getChildAmount() == 0;
	}
	
	/**
	 * Marks this node as included, even if it weren't a branch end node
	 */
	public void markAsIncluded()
	{
		this.included = true;
	}

	
	// OTHER METHODS	---------------
	
	/**
	 * @return A list of all the paths under this path
	 */
	public List<Path> getChildPaths()
	{
		List<Path> paths = new ArrayList<>();
		
		for (int i = 0; i < getChildAmount(); i++)
		{
			paths.add(getChild(i));
		}
		
		return paths;
	}
	
	/**
	 * @return Parses the path leading up to this point. If this is the root node, an empty 
	 * string is returned. Otherwise the returned string will end in a directory separator.
	 */
	public String getLeadingPath()
	{
		List<Path> parents = new ArrayList<>();
		Path nextNode = getParent();
		while (nextNode != null)
		{
			parents.add(nextNode);
			nextNode = nextNode.getParent();
		}
		
		StringBuilder s = new StringBuilder();
		for (int i = parents.size() - 1; i >= 0; i --)
		{
			s.append(parents.get(i).getContent());
			s.append(DS);
		}
		
		return s.toString();
	}
	
	/**
	 * @return An url to the path, including the server address
	 */
	public String getAbsoluteUrl()
	{
		return HttpSettings.getServerString() + DS + toString();
	}
	
	/**
	 * @return All the included path parts below, and possibly including, this part node. The end 
	 * nodes of each branch are always included, as well as any part specifically marked as 
	 * included.
	 */
	public List<Path> getIncludedParts()
	{
		List<Path> included = new ArrayList<>();
		if (isIncluded())
			included.add(this);
		for (int i = 0; i < getChildAmount(); i++)
		{
			included.addAll(getChild(i).getIncludedParts());
		}
		
		return included;
	}
	
	// TODO: Should be moved to treeNode class
	/**
	 * Checks whether the path is below a node with the provided content (case-insensitive)
	 * @param content The content the parent (or grandparent, etc.) node should have
	 * @return Is this path node below another node that has the given content. This node 
	 * doesn't have to be directly under the other node for the result to be true.
	 */
	public boolean isBelowNodeWithContent(String content)
	{
		Path parent = getParent();
		
		if (parent == null)
			return false;
		else if (parent.getContent().equalsIgnoreCase(content))
			return true;
		else
			return parent.isBelowNodeWithContent(content);
	}
	
	/**
	 * Finds the path that divides the two nodes from each other. One of the nodes needs to 
	 * be above the other for this to work
	 * @param upperNode The path node that is (presumably) above the other path node
	 * @param lowerNode The path node that is (presumably) below the other path node
	 * @return A path between (not including) the two nodes or null if there are no nodes 
	 * between the two path nodes (also if they can't be connected). The returned path will be 
	 * completely separate from the original path, and changes made to one don't affect the 
	 * other.
	 */
	public static Path getPathBetween(Path upperNode, Path lowerNode)
	{
		Path upper = upperNode;
		Path lower = lowerNode;
		
		if (!lowerNode.isBelowNodeWithContent(upperNode.getContent()))
		{
			if (upperNode.isBelowNodeWithContent(lowerNode.getContent()))
			{
				upper = lowerNode;
				lower = upperNode;
			}
			else
				return null;
		}
		
		// Lists all the dividing path parts (from down to up)
		Stack<Path> betweenParts = new Stack<>();
		Path parent = lower.getParent();
		while (!parent.getContent().equalsIgnoreCase(upper.getContent()))
		{
			betweenParts.push(parent);
			parent = parent.getParent();
		}
		
		if (betweenParts.isEmpty())
			return null;
		
		// Creates a path from the dividing parts
		Path topNode = betweenParts.pop();
		Path dividingPath = new Path(topNode.getContent(), null, topNode.included);
		Path lastNode = dividingPath;
		while (!betweenParts.isEmpty())
		{
			Path nextNode = betweenParts.pop();
			lastNode = new Path(nextNode.getContent(), lastNode, nextNode.included);
		}
		
		return dividingPath;
	}
	
	/**
	 * Parses a path from the provided uri. The path supports inclusion (+), which allows 
	 * branching paths
	 * @param uri The uri (not including any parameters, the host, etc.)
	 * @return The path(s) parsed from the uri
	 */
	public static List<Path> parseFromString(String uri)
	{
		return parseFromString(uri, null);
	}
	
	private static List<Path> parseFromString(String uri, Path lastNode)
	{	
		List<Path> created = new ArrayList<>();
		
		// Finds the next breaker (resource separator or similar)
		int nextBreakerIndex = indexOf(uri, DS, INCLUSION, '(', ')');
		
		// If there are no breakers left, this is the final element
		if (nextBreakerIndex < 0)
		{
			created.add(new Path(uri, null, false));
			return created;
		}
		
		char breaker = uri.charAt(nextBreakerIndex);
		String content = uri.substring(0, nextBreakerIndex);
		
		// The latest addition is parsed from the content between the breakers
		Path newPart = null;
		Path latestNode = lastNode;
		if (!content.isEmpty())
		{
			newPart = new Path(content, null, false);
			latestNode = newPart;
			created.add(newPart);
		}
		
		switch (breaker)
		{
			// On closing parenthesis, the rest of the uri is skipped for the parent element
			case ')': return created;
			// On opening parenthesis, appends the next operation(s) and children for the all 
			// included elements of the closed path (content should be empty on this one)
			case '(':
				int closedPathEndsAt = uri.indexOf(')');
				List<Path> closedPath = parseFromString(uri.substring(nextBreakerIndex + 1, 
						closedPathEndsAt), latestNode);
				String remainingUri = uri.substring(closedPathEndsAt + 1);
				List<Path> createdFromRemaining = new ArrayList<>();
				if (!remainingUri.isEmpty())
				{
					for (Path path : closedPath)
					{
						List<Path> included = path.getIncludedParts();
						for (Path includedPart : included)
						{
							createdFromRemaining.addAll(parseFromString(remainingUri, includedPart));
						}
					}
				}
				created.addAll(closedPath);
				created.addAll(createdFromRemaining);
				return created;
			// On inclusion, adds the results of the next iteration(s) to this one
			// If the separator is introduced as well, marks the new parent node as included
			case INCLUSION:
				String remaining = uri.substring(nextBreakerIndex + 1);
				if (!remaining.isEmpty() && remaining.charAt(0) == DS)
					latestNode.markAsIncluded();
				created.addAll(parseFromString(remaining, latestNode));
				return created;
			// On directory separator, adds the following path(s) under the latest node
			case DS:
				List<Path> remainingPaths = parseFromString(uri.substring(nextBreakerIndex + 1), 
						latestNode);
				for (Path path : remainingPaths)
				{
					latestNode.addChild(path);
				}
				return created;
			default:
				System.err.println("Logic error. Unknown breaker: '" + breaker + "'");
				return created;
		}
	}
	
	private static int indexOf(String from, char... regex)
	{
		int smallest = -1;
		for (char r : regex)
		{
			int index = from.indexOf(r);
			if (smallest == -1 || (index >= 0 && index < smallest))
			{
				smallest = index;
			}
		}
		
		return smallest;
	}
}
