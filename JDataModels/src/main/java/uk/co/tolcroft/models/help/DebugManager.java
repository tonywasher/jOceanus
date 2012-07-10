/*******************************************************************************
 * Copyright 2012 Tony Washer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package uk.co.tolcroft.models.help;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class DebugManager {
	/* Members */
	private DefaultTreeModel		theModel	= null;
	private DebugEntry				theRoot		= null;
	private DebugEntry				theFocus	= null;
	private String					theTitle	= "Debug Manager";
	private DebugWindow				theWindow	= null;
	private DebugManager			theManager	= this;

	/* Access methods */
	public TreeModel	getModel()		{ return theModel; }
	public String		getTitle()		{ return theTitle; }
	public DebugEntry	getFocus()		{ return theFocus; }
	
	/**
	 * Constructor
	 */
	public DebugManager() {
		/* Create the root node */
		theRoot = new DebugEntry(theTitle);
		
		/* Create the tree model */
		theModel = new DefaultTreeModel(theRoot.getNode());
	}
	
	/**
	 * Declare window object
	 * @param pWindow the window
	 */
	public void declareWindow(DebugWindow pWindow) {
		/* Store window */
		theWindow = pWindow;
	}
	
	/**
	 * Create a child entry for parent
	 * @param pParent the parent to add to
	 * @param pName the name of the new entry
	 * @return the new child entry
	 */
	public DebugEntry addChildEntry(DebugEntry	pParent,
							   		String		pName,
							   		DebugObject	pObject) {
		DebugEntry myEntry = new DebugEntry(pName);
		myEntry.addAsChildOf(pParent);
		myEntry.setObject(pObject);
		return myEntry;
	}	

	/* The Debug Entry class */
	public class DebugEntry {
		/* Members */
		private	String					theName		= null;
		private DebugObject				theObject	= null;
		private DefaultMutableTreeNode	theNode		= null;
		private DefaultMutableTreeNode	theParent	= null;
		private TreePath				thePath		= null;
		private boolean					isVisible	= false;
		private boolean					hasChildren	= false;
		
		/* Access methods */
		public String					getName() 		{ return theName; }
		public DebugObject				getObject()		{ return theObject; }
		public DefaultMutableTreeNode	getNode()		{ return theNode; }
		public TreePath					getPath()		{ return thePath; }
		public boolean					isVisible()		{ return isVisible; }
		public boolean					hasChildren()	{ return hasChildren; }
		
		/**
		 * Constructor
		 * @param pName the object name
		 */
		public DebugEntry(String 		pName) {
			/* Store name */
			theName 	= pName;
			
			/* Create node */
			theNode 	= new DefaultMutableTreeNode(this);
		}

		/**
		 * Add as a child into the tree
		 * @param pParent the parent object 
		 */
		public void addAsChildOf(DebugEntry	pParent) {
			/* Add as child of parent */
			theParent 	= pParent.getNode();
			theParent.add(theNode);

			/* Access the tree path */
			thePath	= new TreePath(theNode.getPath());
			
			/* Note that we are visible */
			isVisible = true;
			
			/* Note that parent has Children */
			pParent.hasChildren = true;
		}
		
		/**
		 * Add as a child into the tree
		 * @param pParent the parent object 
		 */
		public void addAsFirstChildOf(DebugEntry	pParent) {
			/* Add as child of parent */
			theParent 	= pParent.getNode();
			theParent.insert(theNode, 0);

			/* Access the tree path */
			thePath	= new TreePath(theNode.getPath());
			
			/* Note that we are visible */
			isVisible = true;
			
			/* Note that parent has Children */
			pParent.hasChildren = true;
		}
		
		/**
		 * Add as a root child into the tree
		 */
		public void addAsRootChild() {
			/* Add as child of root node */
			theParent 	= theRoot.getNode();
			theParent.add(theNode);
			
			/* Access the tree path */
			thePath	= new TreePath(theNode.getPath());
			
			/* Note that we are visible */
			isVisible = true;
		}
		
		@Override
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
		public void setObject(DebugObject pObject) {
			/* Set the new object */
			theObject = pObject;
			
			/* Remove all the children */
			removeChildren();
				
			/* Add all the new children */
			if (pObject != null)
				pObject.addChildEntries(theManager, this);

			/* Note that this entry and its children have changed */
			theModel.nodeStructureChanged(theNode);
			
			/* Ensure that display is updated if this is active */
			if (theWindow != null) theWindow.updateDebug(this);
		}

		/**
		 * Note that the object has been changed
		 */
		public void setChanged() {
			/* If the node is visible */
			if (isVisible) {
				/* Note the object has changed if it exists */
				if (theObject != null)  theModel.nodeChanged(theNode);

				/* Note that any children have changed */
				theModel.nodeStructureChanged(theNode);		
				
				/* Ensure that display is updated if this is active */
				if (theWindow != null) theWindow.updateDebug(this);
			}
		}

		/**
		 * Remove children of an object
		 */
		public void removeChildren() {
			/* Remove all the children */
			theNode.removeAllChildren();
			hasChildren = false;
		}
	}
}
