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

import java.awt.Dimension;
import java.net.URL;

import javax.swing.GroupLayout;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.StyleSheet;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class HelpWindow extends JFrame implements HyperlinkListener, TreeSelectionListener {
    private static final long serialVersionUID = 3908377793788072474L;

    /* Members */
    private JEditorPane theEditor = null;
    private JTree theTree = null;
    private HelpEntry[] theEntries = null;
    private HelpPage.List theList = null;
    private DefaultMutableTreeNode theRoot = null;

    /**
     * Constructor
     * @param pParent the parent frame
     * @param pModule the help module to display
     */
    public HelpWindow(JFrame pParent, HelpModule pModule) {
        /* Local variables */
        JSplitPane mySplit;
        JScrollPane myDocScroll;
        JScrollPane myTreeScroll;
        HTMLEditorKit myKit;
        StyleSheet myStyle;
        Document myDoc;

        /* Set the title */
        setTitle("Help Manager");

        /* Access the Help entries and list */
        theEntries = pModule.getHelpEntries();
        theList = pModule.getHelpPages();

        /* Access the initial id */
        String myName = pModule.getInitialName();

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
        theRoot = HelpEntry.createTree(pModule.getTitle(), theEntries);

        /* Create the JTree object */
        theTree = new JTree(theRoot);

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
        mySplit.setPreferredSize(new Dimension(900, 600));

        /* Create the error panel */
        JPanel myPanel = new JPanel();

        /* Create the layout for the panel */
        GroupLayout myLayout = new GroupLayout(myPanel);
        myPanel.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                myLayout.createSequentialGroup().addContainerGap().addComponent(mySplit).addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                mySplit));

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
        String myInternal = null;
        String myName = pName;

        /* If the name has a # in it */
        if (myName.contains("#")) {
            /* Split on the # */
            String[] myTokens = myName.split("#");

            /* Allocate the values */
            myName = myTokens[0];
            myInternal = myTokens[1];

            /* Handle an internal reference */
            if (myName.length() == 0)
                myName = null;
        }

        /* If we are switching pages */
        if (myName != null) {
            /* Access the help page */
            HelpPage myPage = theList.searchFor(myName);

            /* If we have a page */
            if (myPage != null) {
                /* Set the help text */
                theEditor.setText(myPage.getHtml());
                theEditor.setCaretPosition(0);
                theEditor.requestFocusInWindow();

                /* Sort out the tree */
                TreePath myPath = myPage.getEntry().getTreePath();
                theTree.setSelectionPath(myPath);
                theTree.scrollPathToVisible(myPath);
            }
        }

        /* If we have an internal reference */
        if (myInternal != null) {
            /* Scroll to the reference */
            theEditor.scrollToReference(myInternal);
        }
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        /* If this is an activated event */
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            if (e instanceof HTMLFrameHyperlinkEvent) {
                HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
                HTMLDocument doc = (HTMLDocument) theEditor.getDocument();
                doc.processHTMLFrameHyperlinkEvent(evt);
            } else {
                try {
                    URL url = e.getURL();
                    String desc = e.getDescription();
                    if (url == null) {
                        /* display the new page */
                        displayPage(desc);
                    } else
                        theEditor.setPage(e.getURL());
                } catch (Throwable t) {
                }
            }
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode myNode = (DefaultMutableTreeNode) theTree.getLastSelectedPathComponent();

        /* Ignore if there is no selection or if this is the root */
        if (myNode == null)
            return;
        if (myNode == theRoot)
            return;

        /* Access the Help Entry */
        HelpEntry myEntry = (HelpEntry) myNode.getUserObject();

        /* display the node */
        displayPage(myEntry.getName());
    }
}
