package uk.co.tolcroft.help;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import uk.co.tolcroft.help.DebugManager.DebugEntry;
import uk.co.tolcroft.models.DataItem;
import uk.co.tolcroft.models.DataList;

public class DebugItem implements ActionListener,
								  ItemListener {
	/**
	 * The members 
	 */
	private JPanel						thePanel		= null;
	private JPanel						theListPanel	= null;
	private DataList<?>					theList 		= null;
	private DataItem<?>					theItem 		= null;
	private DataList<?>.ListIterator	theIterator		= null;
	private DebugObject					theObject		= null;
	private	JButton						theNext			= null;
	private	JButton						theNextTen		= null;
	private	JButton						theNextHun		= null;
	private	JButton						theNextThou		= null;
	private	JButton						thePrev			= null;
	private	JButton						thePrevTen		= null;
	private	JButton						thePrevHun		= null;
	private	JButton						thePrevThou		= null;
	private JToggleButton				theToggle		= null;
	private JLabel						theLabel		= null;
	private JEditorPane     			theEditor   	= null;
	private boolean						isListMode		= false;
	
	/* Access methods */
	protected	JPanel	getPanel()		{ return thePanel; } 
	
	/**
	 * Constructor
	 */
	protected DebugItem() {
		JScrollPane   myScroll;
		HTMLEditorKit myKit;
		StyleSheet    myStyle;
		Document      myDoc;
		
		/* Create the Buttons */
		theNext 	= new JButton("+1");
		theNextTen 	= new JButton("+10");
		theNextHun 	= new JButton("+100");
		theNextThou = new JButton("+1000");
		thePrev 	= new JButton("-1");
		thePrevTen 	= new JButton("-10");
		thePrevHun 	= new JButton("-100");
		thePrevThou	= new JButton("-1000");

		/* Create the toggle button */
		theToggle	= new JToggleButton("Show items");
		
		/* Create the label */
		theLabel	= new JLabel();
		
		/* Create the editor pane as non-editable */
		theEditor = new JEditorPane();
		theEditor.setEditable(false);
			
		/* Add an editor kit to the editor */
		myKit = new HTMLEditorKit();
		theEditor.setEditorKit(myKit);
			
		/* Create a scroll-pane for the editor */
		myScroll = new JScrollPane(theEditor);	

		/* Create the style-sheet for the window */
		myStyle = myKit.getStyleSheet();
		myStyle.addRule("body { color:#000; font-family:times; margins; 4px; }");
		myStyle.addRule("h1 { color: black; }");
		myStyle.addRule("h2 { color: black; }");
			
		/* Create the document for the window */
		myDoc = myKit.createDefaultDocument();
		theEditor.setDocument(myDoc);
			
		/* Create the list panel */
		theListPanel = new JPanel();	
		theListPanel.setBorder(javax.swing.BorderFactory
								.createTitledBorder("List Control"));

		/* Create the layout for the panel */
		GroupLayout myLayout = new GroupLayout(theListPanel);
		theListPanel.setLayout(myLayout);
			    
	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(myLayout.createSequentialGroup()
              	.addContainerGap()
              	.addComponent(theToggle)
          		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
                .addComponent(thePrevThou)
          		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(thePrevHun)
          		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(thePrevTen)
          		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(thePrev)
           		.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(theLabel)
          		.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(theNext)
          		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(theNextTen)
          		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(theNextHun)
          		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(theNextThou)
                .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(theToggle)
                .addComponent(thePrevThou)
                .addComponent(thePrevHun)
                .addComponent(thePrevTen)
                .addComponent(thePrev)
                .addComponent(theLabel)
                .addComponent(theNext)
                .addComponent(theNextTen)
                .addComponent(theNextHun)
                .addComponent(theNextThou)
        );
            
		/* Create the complete panel */
		thePanel = new JPanel();
			
		/* Create the layout for the panel */
		myLayout = new GroupLayout(thePanel);
		thePanel.setLayout(myLayout);    

		/* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(theListPanel)
            .addComponent(myScroll)
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
           	.addGroup(myLayout.createSequentialGroup()
           		.addContainerGap()
                .addComponent(theListPanel)
           		.addContainerGap()
                .addComponent(myScroll)
                .addContainerGap())
        );
        
        /* Add action Listeners */
        theNext.addActionListener(this);
        thePrev.addActionListener(this);
        theNextTen.addActionListener(this);
        thePrevTen.addActionListener(this);
        theNextHun.addActionListener(this);
        thePrevHun.addActionListener(this);
        theNextThou.addActionListener(this);
        thePrevThou.addActionListener(this);        

        /* Add item Listener */
        theToggle.addItemListener(this);
	}

	/* Display debug detail */
	protected void displayDebug(DebugEntry pEntry) {
		/* Record the object */
		theObject = pEntry.getObject();
		
		/* If we should use the DataList window */
		if ((theObject != null) &&
			(theObject instanceof DataList) && 
			(!pEntry.hasChildren())) {
			/* Show the list window */
			theListPanel.setVisible(true);

			/* Declare the list to the list window */
			DataList<?> myList = (DataList<?>) theObject;
			setList(myList);
		}
			
		/* Else hide the list window */
		else {
			/* Hide the list window */
			theListPanel.setVisible(false);
			
			/* Display the object */
			displayObject(theObject);
		}
	}		

	/* Display debug object */
	private void displayObject(DebugObject pObject) {
		StringBuilder	myValue;

		/* Set default value */
		myValue = null;
			
		/* Ignore if no debug object */
		if (pObject != null) myValue = pObject.toHTMLString();
			
		/* Build the HTML page */
		String	myText = "<html><body>";

		/* Add the value to the output */
		if (myValue != null) myText += myValue;
		myText += "</body></html>";
			
		/* Set the report text */
		theEditor.setText(myText);
		theEditor.setCaretPosition(0);
		theEditor.requestFocusInWindow();			
	}		

	/**
	 * Set List
	 */
	private void setList(DataList<?> pList) {
		/* Record list */
		theList 	= pList;
		
		/* If the list has items */
		if (theList.sizeAll() > 0) {
			/* Create iterator and obtain first item */
			theIterator = theList.listIterator(true);
			theItem 	= theIterator.next();
		
			/* Display header initially */
			theToggle.setSelected(false);
		}
		
		/* else hide the list control */
		else theListPanel.setVisible(false);
		
		/* Note that we are not in list mode */
		isListMode = false;
		
		/* display the header */
		displayHeader();
	}
	
	/**
	 * Display Item
	 */
	private void displayItem() {
		/* Access the list size */
		int mySize = theList.size();
		int myPos  = theList.indexOf(theItem);
		
		/* Show/hide movement buttons */
		theNextThou.setVisible(mySize >= 1000);
		thePrevThou.setVisible(mySize >= 1000);
		theNextHun.setVisible(mySize >= 100);
		thePrevHun.setVisible(mySize >= 100);
		theNextTen.setVisible(mySize >= 10);
		thePrevTen.setVisible(mySize >= 10);
		theNext.setVisible(true);
		thePrev.setVisible(true);
		theLabel.setVisible(true);
		
		/* Enable movement buttons */
		theNextThou.setEnabled(myPos < mySize - 1000);
		thePrevThou.setEnabled(myPos >= 1000);
		theNextHun.setEnabled(myPos >= 100);
		thePrevHun.setEnabled(myPos >= 100);
		theNextTen.setEnabled(myPos < mySize - 10);
		thePrevTen.setEnabled(myPos >= 10);
		theNext.setEnabled(myPos < mySize - 1);
		thePrev.setEnabled(myPos >= 1);
		
		/* Set the text detail */
		theLabel.setText("Item " + (myPos+1) + " of " + mySize);

		/* Set the text detail */
		theToggle.setText("Show header");

		/* Display the item */
		displayObject(theItem);
	}

	/**
	 * Display Header
	 */
	private void displayHeader() {
		/* Hide movement buttons */
		theNextThou.setVisible(false);
		thePrevThou.setVisible(false);
		theNextHun.setVisible(false);
		thePrevHun.setVisible(false);
		theNextTen.setVisible(false);
		thePrevTen.setVisible(false);
		theNext.setVisible(false);
		thePrev.setVisible(false);
		theLabel.setVisible(false);
		
		/* Set the text detail */
		theToggle.setText("Show items");

		/* Display the header */
		displayObject(theObject);
	}

	/**
	 * Perform the requested action 
	 * @param pEvent the event
	 */
	public void actionPerformed(ActionEvent pEvent) {
		/* If we asked for the next item */
		if 		(pEvent.getSource() == theNext) 	shiftIterator(1);
		else if (pEvent.getSource() == theNextTen)	shiftIterator(10);
		else if (pEvent.getSource() == theNextHun)	shiftIterator(100);
		else if (pEvent.getSource() == theNextThou)	shiftIterator(100);
		else if (pEvent.getSource() == thePrev)		shiftIterator(-1);
		else if (pEvent.getSource() == thePrevTen)	shiftIterator(-10);
		else if (pEvent.getSource() == thePrevHun)	shiftIterator(-100);
		else if (pEvent.getSource() == thePrevThou)	shiftIterator(-1000);
	}

	/**
	 * Shift the iterator a number of steps 
	 * @param iNumSteps the number of steps to shift (positive or negative)
	 */
	private void shiftIterator(int iNumSteps) {
		DataItem<?>	myNext = null;
		
		/* If we are stepping forwards */
		if (iNumSteps > 0) {
			/* Loop through the steps */
			while (iNumSteps-- > 0) {
				/* Shift to next element */
				myNext = theIterator.next();
				
				/* If we have reached the end of the list (should never happen) */
				if (myNext == null) {
					/* Set next element to the last in the list and break loop */
					myNext = theIterator.peekLast();
					break;
				}
			}
			
			/* Record the next entry */
			theItem = myNext;
		}
		
		/* else we are stepping backwards */
		else if (iNumSteps < 0) {
			/* Shift back one step */
			theIterator.previous();
			
			/* Loop through the steps */
			while (iNumSteps++ < 0) {
				/* Shift to previous element */
				myNext = theIterator.previous();
				
				/* If we have reached the end of the list (should never happen) */
				if (myNext == null) {
					/* Set next element to the last in the list and break loop */
					myNext = theIterator.peekFirst();
					break;
				}
			}
			
			/* Shift forward one step */
			theIterator.next();
			
			/* Record the next entry */
			theItem = myNext;
		}
		
		/* display the item */
		displayItem();
	}

	/**
	 * Handle item change events 
	 * @param pEvent the event
	 */
	public void itemStateChanged(ItemEvent pEvent) {
		/* If the event was the toggle button */
		if (pEvent.getSource() == theToggle) {
			/* If we are selecting list view */
			if (pEvent.getStateChange() == ItemEvent.SELECTED) {
				/* If we need to switch to item view */
				if (!isListMode) {
					/* Set list mode and display item */
					isListMode = true;
					displayItem();
				}
			}

			/* else if we are deselecting list view */
			else if (pEvent.getStateChange() == ItemEvent.DESELECTED) {
				/* If we need to switch to header view */
				if (isListMode) {
					/* Clear list mode and display header */
					isListMode = false;
					displayHeader();
				}
			}
		}
	}
}
