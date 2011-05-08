package uk.co.tolcroft.finance.views;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import uk.co.tolcroft.finance.ui.*;
import uk.co.tolcroft.models.htmlDumpable;

public class DebugManager {
	/* Members */
	private DefaultTreeModel		theModel	= null;
	private DebugEntry				theRoot		= null;
	private DebugEntry				theFocus	= null;
	private String					theTitle	= "Debug Manager";
	private DebugEntry				theError	= null;
	private DebugEntry				theViews	= null;
	private DebugEntry				theUpdates	= null;
	private DebugEntry				theAnalysis	= null;
	private DebugEntry				theInstant	= null;
	private DebugEntry				theAccount	= null;
	private DebugWindow				theWindow	= null;

	/* Access methods */
	public TreeModel	getModel()		{ return theModel; }
	public String		getTitle()		{ return theTitle; }
	public DebugEntry	getError()		{ return theError; }
	public DebugEntry	getViews()		{ return theViews; }
	public DebugEntry	getUpdates()	{ return theUpdates; }
	public DebugEntry	getAnalysis()	{ return theAnalysis; }
	public DebugEntry	getInstant()	{ return theInstant; }
	public DebugEntry	getAccount()	{ return theAccount; }
	public DebugEntry	getFocus()		{ return theFocus; }
	
	/**
	 * Constructor
	 * @param pTitle the title of the debug window
	 */
	public DebugManager() {
		/* Create the root node */
		theRoot = new DebugEntry(theTitle);
		
		/* Create the tree model */
		theModel = new DefaultTreeModel(theRoot.getNode());
		
		/* Create the default entries */
		theViews 	= new DebugEntry("EditViews");
		theUpdates 	= new DebugEntry("Updates");
		theAnalysis	= new DebugEntry("Analysis");
		theInstant	= new DebugEntry("InstantAnalysis");
		theAccount	= new DebugEntry("AccountAnalysis");
		theError	= new DebugEntry("Error");
		
		/* Add them to the root */
		theViews.addAsRootChild();
		theUpdates.addAsRootChild();
		theAnalysis.addAsRootChild();
		theAccount.addAsRootChild();
		theInstant.addAsRootChild();
		theError.addAsRootChild();
		
		/* Hide the instant/error view */
		theInstant.hideEntry();
		theError.hideEntry();
	}
	
	/**
	 * Declare window object
	 * @param pWindow the window
	 */
	public void declareWindow(DebugWindow pWindow) {
		/* Store window */
		theWindow = pWindow;
	}
	
	/* The Debug Entry class */
	public class DebugEntry {
		/* Members */
		private	String					theName		= null;
		private htmlDumpable			theObject	= null;
		private DefaultMutableTreeNode	theNode		= null;
		private DefaultMutableTreeNode	theParent	= null;
		private TreePath				thePath		= null;
		private boolean					isVisible	= false;
		
		/* Access methods */
		public String					getName() 	{ return theName; }
		public htmlDumpable				getObject()	{ return theObject; }
		public DefaultMutableTreeNode	getNode()	{ return theNode; }
		public TreePath					getPath()	{ return thePath; }
		public boolean					isVisible()	{ return isVisible; }
		
		/**
		 * Constructor
		 * @param the object name
		 */
		public DebugEntry(String 		pName) {
			/* Store name */
			theName 	= pName;
			
			/* Create node */
			theNode 	= new DefaultMutableTreeNode(this);
		}

		/**
		 * Add as a child into the tree
		 * @param the parent object 
		 */
		public void addAsChildOf(DebugEntry	pParent) {
			/* Add as child of parent */
			theParent 	= pParent.getNode();
			theParent.add(theNode);

			/* Access the tree path */
			thePath	= new TreePath(theNode.getPath());
			
			/* Note that we are visible */
			isVisible = true;
		}
		
		/**
		 * Add as a child into the tree
		 * @param the parent object 
		 */
		public void addAsFirstChildOf(DebugEntry	pParent) {
			/* Add as child of parent */
			theParent 	= pParent.getNode();
			theParent.insert(theNode, 0);

			/* Access the tree path */
			thePath	= new TreePath(theNode.getPath());
			
			/* Note that we are visible */
			isVisible = true;
		}
		
		/**
		 * Add as a root child into the tree
		 */
		private void addAsRootChild() {
			/* Add as child of root node */
			theParent 	= theRoot.getNode();
			theParent.add(theNode);
			
			/* Access the tree path */
			thePath	= new TreePath(theNode.getPath());
			
			/* Note that we are visible */
			isVisible = true;
		}
		
		/**
		 * toString method
		 */
		public String toString() { return theName; }
		
		/**
		 * Set Focus onto this debug entry 
		 */
		public void setFocus() {
			/* Record the focus */
			theFocus = this;
			
			/* If we have a window */
			if (theWindow != null) {
				/* Set selection path and ensure visibility */
				theWindow.displayDebug(this);
			}
		}
		
		/**
		 * Hide the entry
		 */
		public void hideEntry() {
			/* If the node is visible */
			if (theNode.getParent() != null) {
				/* Remove it from the view using the model */
				theModel.removeNodeFromParent(theNode);
			}
			
			/* Note that this entry is hidden */
			isVisible = false;
		}

		/**
		 * Ensure that the entry is visible
		 */
		public void showEntry() {
			/* If the node is not visible */
			if (theNode.getParent() == null) {
				/* Insert it into the view using model */
				theModel.insertNodeInto(theNode, theParent, theParent.getChildCount());
			}
			
			/* Note that this entry is visible */
			isVisible = true;
		}

		/**
		 * Ensure that the entry is visible
		 */
		public void showPrimeEntry() {
			/* If the node is not visible */
			if (theNode.getParent() == null) {
				/* Insert it into the view using model */
				theModel.insertNodeInto(theNode, theParent, 0);
			}
			
			/* Note that this entry is visible */
			isVisible = true;
		}

		/**
		 * Set the object referred to by the entry
		 * @param pObject the new object
		 */
		public void setObject(htmlDumpable pObject) {
			/* Set the new object */
			theObject = pObject;
			
			/* Note that this entry has changed */
			if (isVisible) theModel.nodeChanged(theNode);
		}

		/**
		 * Note that the object has been changed
		 * @param pObject the new object
		 */
		public void setChanged() {
			/* If this entry has an object */
			if (theObject != null) {
				/* Note that this object has changed */
				if (isVisible) theModel.nodeChanged(theNode);
			}
			
			/* else its the children that have changed */
			else {
				/* Note that this entry has changed */
				if (isVisible) theModel.nodeStructureChanged(theNode);				
			}
		}

		/**
		 * Remove children of an object
		 * @param pObject the new object
		 */
		public void removeChildren() {
			/* Remove all the children */
			theNode.removeAllChildren();				
		}
	}
}