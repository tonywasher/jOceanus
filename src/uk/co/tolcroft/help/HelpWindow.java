package uk.co.tolcroft.help;

import java.net.URL;

import javax.swing.GroupLayout;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.JSplitPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.StyleSheet;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class HelpWindow extends JFrame implements HyperlinkListener,
												  TreeSelectionListener {
	private static final long serialVersionUID = 3908377793788072474L;

	/* Members */
	private JEditorPane     theEditor   = null;
	private JTree			theTree		= null;
	private HelpEntry[]		theEntries	= null;
	private HelpPage.List	theList		= null;
	
	/**
	 * Constructor
	 */
	public HelpWindow(JFrame 		pParent,
			  		  HelpModule 	pModule)  {
		/* Local variables */
		JSplitPane	  mySplit;
		JScrollPane   myDocScroll;
		JScrollPane   myTreeScroll;
		HTMLEditorKit myKit;
		StyleSheet    myStyle;
		Document      myDoc;

		/* Set the title */
		setTitle("Help Manager");
		
		/* Access the Help entries and list */
		theEntries  = pModule.getHelpEntries();
		theList		= pModule.getHelpPages();
		
		/* Access the initial id */
		String 		myName = pModule.getInitialName();

		/* Create the editor pane as non-editable */
		theEditor = new JEditorPane();
		theEditor.setEditable(false);
		theEditor.addHyperlinkListener(this);

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
		
		/* Get the tree node from the help entries */
		DefaultMutableTreeNode myNode = HelpEntry.createTree(pModule.getTitle(), 
															 theEntries);
		
		/* Create the JTree object */
		theTree = new JTree(myNode);
		
		/* Make sure that we have single selection model */
		theTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		/* Add the listener for the tree */
		theTree.addTreeSelectionListener(this);
		
		/* Create a scroll-pane for the tree */
		myTreeScroll = new JScrollPane(theTree);
		
		/* display the page */
		displayPage(myName);

		/* Create the split pane */
		mySplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, myTreeScroll, myDocScroll);
		mySplit.setOneTouchExpandable(true);
		
		/* Create the error panel */
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
	
	/* display the page */
	private void displayPage(String pName) {
		/* Access the help page */
		HelpPage myPage = theList.searchFor(pName);
		
		/* If we have no page just skip */
		if (myPage == null) return;
		
		/* Set the help text */
		theEditor.setText(myPage.getHtml());
		theEditor.setCaretPosition(0);
		theEditor.requestFocusInWindow();
		
		/* Sort out the tree */
		TreePath myPath = myPage.getEntry().getTreePath();
		theTree.setSelectionPath(myPath);
		theTree.scrollPathToVisible(myPath);
	}
	
	/* Handle link movement */
	public void hyperlinkUpdate(HyperlinkEvent e ){
		/* If this is an activated event */
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			if (e instanceof HTMLFrameHyperlinkEvent) {
				HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
				HTMLDocument doc = (HTMLDocument)theEditor.getDocument();
				doc.processHTMLFrameHyperlinkEvent(evt);
			}
			else {
				try {
					URL    url  = e.getURL();
					String desc = e.getDescription();
					if (url == null) {
						/* If this is an internal reference */
						if (desc.startsWith("#")) {
							/* Scroll to the reference */
							theEditor.scrollToReference(desc.substring(1));
						}
						
						/* else it is a reference to a new page */
						else {
							/* display the new page */
							displayPage(desc);
						}
					}
					else theEditor.setPage(e.getURL());
				}
				catch (Throwable t) {}
			}
		}
	}
	
	/* Handle tree selection */
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode myNode = 
			(DefaultMutableTreeNode) theTree.getLastSelectedPathComponent();
		
		/* Ignore if there is no selection */
		if (myNode == null) return;
		
		/* Access the Help Entry */
		HelpEntry myEntry = (HelpEntry)myNode.getUserObject();
		
		/* display the node */
		displayPage(myEntry.getName());
	}
}
