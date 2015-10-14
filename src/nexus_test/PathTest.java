package nexus_test;

import nexus_http.Path;

/**
 * This class tests the basic path parsing
 * @author Mikko Hilpinen
 * @since 7.10.2015
 */
public class PathTest
{
	// CONSTRUCTOR	---------------------
	
	private PathTest()
	{
		// Static interface
	}

	
	// MAIN METHOD	----------------------
	
	/**
	 * Tests path parsing
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		String[] testPaths = {"1/2/3/4", "1/2+/(3+4)", "1/2+3/4+(5/6)+7/8", "1/(2+3)/4", 
				"1+/2+/3+/4", "1+2+3+4", "(1/2)+(3/4)/5+(6/7)"};
		
		for (String testPath : testPaths)
		{
			System.out.println("\nPath before parsing: " + testPath);
			System.out.println("All included paths");
			for (Path parsed : Path.parseFromString(testPath))
			{
				System.out.println("Parsed path with size: " + parsed.size());
				for (Path included : parsed.getIncludedParts())
				{
					System.out.println(included);
				}
			}
		}
	}
}
