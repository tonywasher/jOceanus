package uk.co.tolcroft.help;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class HelpEntry {
	/* Members */
	private String 		theTitle	= null;
	private String 		theName 	= null;
	private String 		theFileName	= null;
	private HelpEntry[] theChildren	= null;
	private TreePath	thePath		= null;
	private HelpPage	thePage		= null;
	
	/* Access methods */
	public String 		getTitle() 		{ return theTitle; }
	public String 		getName() 		{ return theName; }
	public String 		getFileName() 	{ return theFileName; }
	public HelpEntry[]	getChildren() 	{ return theChildren; }
	public TreePath		getTreePath()	{ return thePath; }
	public HelpPage		getHelpPage()	{ return thePage; }
	
	/**
	 * Set the help page
	 * @param pPage the page to record
	 */
	protected void 		setHelpPage(HelpPage pPage) { thePage = pPage; }
	
	/**
	 * Constructor for an HTML leaf element (no children)
	 * @param pName the name by which this entry is referenced
	 * @param pTitle the title for this page in the table of contents
	 * @param pFileName the name of the file containing the HTML for this entry
	 */
	public HelpEntry(String pName, String pTitle, String pFileName) {
		theName 	= pName;
		theTitle 	= pTitle;
		theFileName = pFileName;
	}

	/**
	 * Constructor for a table of contents element
	 * @param pName the name by which this entry is referenced
	 * @param pTitle the title for this page in the table of contents
	 * @param pChildren the children for this element
	 */
	public HelpEntry(String pName, String pTitle, HelpEntry[] pChildren) {
		theName 	= pName;
		theTitle 	= pTitle;
		theChildren	= pChildren;
	}

	/**
	 * Constructor for a table of contents HTML element
	 * @param pName the name by which this entry is referenced
	 * @param pTitle the title for this page in the table of contents
	 * @param pFileName the name of the file containing the HTML for this entry
	 * @param pChildren the children for this element
	 */
	protected HelpEntry(String pName, String pTitle, String pFileName, HelpEntry[] pChildren) {
		theName 	= pName;
		theTitle 	= pTitle;
		theChildren	= pChildren;
	}
	
	/**
	 * Return the displayable form of this entry
	 * @return the title
	 */
	public String toString() { return getTitle(); }
	
	/**
	 * Find help entry by Id
	 * @param pName the name of the entry
	 * @return the matching entry of null
	 */
	protected HelpEntry searchFor(String pName) {
		HelpEntry myResult = null;
		
		/* If we are the required entry return ourselves */
		if (pName.equals(theName)) return this;
		
		/* If we have children */
		if (theChildren != null) {
			/* Loop through the entries */
			for (HelpEntry myEntry: theChildren) {
				/* Search this entry and return if found */
				myResult = myEntry.searchFor(pName);
				if (myResult != null) return myResult;
			}
		}
		
		/* Return the result */
		return myResult;
	}
	
	/**
	 * Construct a top level Tree Node from a set of help entries
	 */
	protected static DefaultMutableTreeNode createTree(String 		pTitle,
													   HelpEntry[]	pEntries) {
		/* Create an initial tree node */
		DefaultMutableTreeNode myTree = new DefaultMutableTreeNode(pTitle);
		
		/* Add the entries into the node */
		addHelpEntries(myTree, pEntries);
		
		/* Return the tree */
		return myTree;
	}
	
	/**
	 * Add array of Help entries 
	 */
	private static void addHelpEntries(DefaultMutableTreeNode 	pNode,
									   HelpEntry[]				pEntries) {
		DefaultMutableTreeNode myNode;
		
		/* Loop through the entries */
		for (HelpEntry myEntry: pEntries) {
			/* Create a new entry and add it to the node */
			myNode = new DefaultMutableTreeNode(myEntry);
			pNode.add(myNode);
	
			/* Access the tree path for this item */
			myEntry.thePath = new TreePath(myNode.getPath());
			
			/* If we have children */
			if (myEntry.getChildren() != null) {
				/* Add the children into the tree */
				addHelpEntries(myNode, myEntry.getChildren());
			}
		}
	}
}
