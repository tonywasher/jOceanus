package uk.co.tolcroft.finance.ui;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import uk.co.tolcroft.finance.views.DebugManager.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.models.*;

public class DebugWindow extends JFrame implements TreeSelectionListener {
	private static final long serialVersionUID = 3055614623371854422L;

	/* Properties */
	private DebugManager	theDebugMgr		= null;
	private JTree			theTree			= null;
	private JEditorPane     theEditor     	= null;
			
	/* Constructor */
	public DebugWindow(JFrame 		pParent,
					   DebugManager	pManager) {
		JSplitPane	  mySplit;
		JScrollPane   myDocScroll;
		JScrollPane   myTreeScroll;
		HTMLEditorKit myKit;
		StyleSheet    myStyle;
		Document      myDoc;
			
		/* Store the parameters */
		theDebugMgr	= pManager;
		theTree 	= new JTree(theDebugMgr.getModel());

		/* Notify debug manager */
		theDebugMgr.declareWindow(this);

		/* Set the title */
		setTitle(theDebugMgr.getTitle());
		
		/* Create the editor pane as non-editable */
		theEditor = new JEditorPane();
		theEditor.setEditable(false);
			
		/* Add an editor kit to the editor */
		myKit = new HTMLEditorKit();
		theEditor.setEditorKit(myKit);
			
		/* Create a scroll-pane for the editor */
		myDocScroll = new JScrollPane(theEditor);
		
		/* Create the style-sheet for the window */
		myStyle = myKit.getStyleSheet();
		myStyle.addRule("body { color:#000; font-family:times; margins; 4px; }");
		myStyle.addRule("h1 { color: black; }");
		myStyle.addRule("h2 { color: black; }");
			
		/* Create the document for the window */
		myDoc = myKit.createDefaultDocument();
		theEditor.setDocument(myDoc);
			
		/* Make sure that we have single selection model */
		theTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		/* Hide the root selection */
		theTree.setRootVisible(false);
		theTree.setShowsRootHandles(true);
		theTree.setScrollsOnExpand(true);
		theTree.setExpandsSelectedPaths(true);
		
		/* Access the initial id */
		DebugEntry	myEntry = theDebugMgr.getFocus();
		displayDebug(myEntry);

		/* Add the listener for the tree */
		theTree.addTreeSelectionListener(this);

		/* Create a scroll-pane for the tree */
		myTreeScroll = new JScrollPane(theTree);
		
		/* Create the split pane */
		mySplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, myTreeScroll, myDocScroll);
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
	}
		
	/* display the dialog */
	public void showDialog() {
		/* Display the window */
		setVisible(true);		
	}
	
	/* Build Report */
	public void displayDebug(DebugEntry pEntry) {
		htmlDumpable pObject = pEntry.getObject();
		
		/* Ignore if no debug object */
		if (pObject == null) return;
		
		/* Build the HTML page */
		String	myText = "<html><body>";

		/* Convert the object to an HTML string */
		myText += pObject.toHTMLString();
		myText += "</body></html>";
		
		/* Set the report text */
		theEditor.setText(myText);
		theEditor.setCaretPosition(0);
		theEditor.requestFocusInWindow();
		
		/* Sort out the tree */
		TreePath myPath = pEntry.getPath();
		theTree.setSelectionPath(myPath);
		theTree.scrollPathToVisible(myPath);
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
