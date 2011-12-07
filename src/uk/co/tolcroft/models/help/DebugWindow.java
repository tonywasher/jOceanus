package uk.co.tolcroft.models.help;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import uk.co.tolcroft.models.help.DebugManager.*;

public class DebugWindow extends JFrame implements TreeSelectionListener {
	private static final long serialVersionUID = 3055614623371854422L;

	/* Properties */
	private DebugManager	theDebugMgr		= null;
	private JTree			theTree			= null;
	private DebugItem		theItemPane		= null;
			
	/* Constructor */
	public DebugWindow(JFrame 		pParent,
					   DebugManager	pManager) {
		JSplitPane	  mySplit;
		JScrollPane   myTreeScroll;
			
		/* Store the parameters */
		theDebugMgr	= pManager;
		theTree 	= new JTree(theDebugMgr.getModel());

		/* Notify debug manager */
		theDebugMgr.declareWindow(this);

		/* Set the title */
		setTitle(theDebugMgr.getTitle());
		
		/* Make sure that we have single selection model */
		theTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		/* Hide the root selection */
		theTree.setRootVisible(false);
		theTree.setShowsRootHandles(true);
		theTree.setScrollsOnExpand(true);
		theTree.setExpandsSelectedPaths(true);
		
		/* Access the initial id */
		DebugEntry	myEntry = theDebugMgr.getFocus();

		/* Add the listener for the tree */
		theTree.addTreeSelectionListener(this);

		/* Create a scroll-pane for the tree */
		myTreeScroll = new JScrollPane(theTree);

		/* Create the item panel */
		theItemPane = new DebugItem();
		
		/* Create the split pane */
		mySplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, myTreeScroll, theItemPane.getPanel());
		mySplit.setOneTouchExpandable(true);
		mySplit.setPreferredSize(new Dimension(900, 600));

		/* Create the panel */
		JPanel myPanel = new JPanel();
			
		/* Create the layout for the panel */
		GroupLayout myLayout = new GroupLayout(myPanel);
		myPanel.setLayout(myLayout);
			    
	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(myLayout.createSequentialGroup()
           		.addContainerGap()
                .addComponent(mySplit)
                .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(mySplit)
        );
            
		/* Set this to be the main panel */
		getContentPane().add(myPanel);
		pack();
		
		/* Set the relative location */
		setLocationRelativeTo(pParent);
		displayDebug(myEntry);
	}
		
	/* display the dialog */
	public void showDialog() {
		/* Display the window */
		setVisible(true);		
	}
	
	/* Display the debug */
	protected void displayDebug(DebugEntry pEntry) {
		/* Sort out the tree */
		TreePath myPath = pEntry.getPath();
		theTree.setSelectionPath(myPath);
		theTree.scrollPathToVisible(myPath);
		
		/* display the debug in the panel */
		theItemPane.displayDebug(pEntry);
	}
	
	/* Update debug */
	protected void updateDebug(DebugEntry pEntry) {
		/* update debug if required */
		theItemPane.updateDebug(pEntry);
	}
	
	/* Handle tree selection */
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode myNode = 
			(DefaultMutableTreeNode) theTree.getLastSelectedPathComponent();
		
		/* Ignore if there is no selection */
		if (myNode == null) return;
		
		/* Access the Debug Entry */
		DebugEntry myDebug = (DebugEntry)myNode.getUserObject();
		
		/* display the node */
		displayDebug(myDebug);
	}
}
