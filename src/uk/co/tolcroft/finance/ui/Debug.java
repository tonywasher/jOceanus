package uk.co.tolcroft.finance.ui;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.StyleSheet;

public class Debug implements HyperlinkListener,
							  ItemListener {
	/* Properties */
	private MainTab							theWindow		= null;
	private JPanel                          thePanel  		= null;
	private JEditorPane                     theEditor     	= null;
	private JComboBox               		theDataBox 		= null;
		
	/* Access methods */
	public JPanel       getPanel()       { return thePanel; }
		
	/* Selection modes */
	private static final String selEvents 	= "Events";
	private static final String selRates  	= "Rates";
	private static final String selPrices	= "Prices";
	private static final String selPatterns	= "Patterns";
	private static final String selAccounts	= "Events";
	private static final String selTaxYears	= "TaxYears";
	private static final String selError	= "Error";
	
	/* Constructor */
	public Debug(MainTab pWindow) {
		JScrollPane   myScroll;
		HTMLEditorKit myKit;
		StyleSheet    myStyle;
		Document      myDoc;
			
		/* Store the window */
		theWindow 	= pWindow;

		/* Create the editor pane as non-editable */
		theEditor = new JEditorPane();
		theEditor.setEditable(false);
		theEditor.addHyperlinkListener(this);
			
		/* Add an editor kit to the editor */
		myKit = new HTMLEditorKit();
		theEditor.setEditorKit(myKit);
			
		/* Create a scroll-pane for the editor */
		myScroll = new JScrollPane(theEditor);
		myScroll.setSize(new Dimension(900, 200));
		
		/* Create the style-sheet for the window */
		myStyle = myKit.getStyleSheet();
		myStyle.addRule("body { color:#000; font-family:times; margins; 4px; }");
		myStyle.addRule("h1 { color: black; }");
		myStyle.addRule("h2 { color: black; }");
			
		/* Create the document for the window */
		myDoc = myKit.createDefaultDocument();
		theEditor.setDocument(myDoc);
			
		/* Create the selection box and add the items */
		theDataBox = new JComboBox();
		theDataBox.addItem(selEvents);
		theDataBox.addItem(selRates);
		theDataBox.addItem(selPrices);
		theDataBox.addItem(selPatterns);
		theDataBox.addItem(selAccounts);
		theDataBox.addItem(selTaxYears);
		theDataBox.addItem(selError);
		theDataBox.setSelectedIndex(0);
		theDataBox.addItemListener(this);
		
		/* Create the panel */
		thePanel = new JPanel();
			
		/* Create the layout for the panel */
		GroupLayout myLayout = new GroupLayout(thePanel);
		thePanel.setLayout(myLayout);
			    
		/* Set the layout */
		myLayout.setHorizontalGroup(
		  	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		       	.addGroup(myLayout.createSequentialGroup()
		       		.addContainerGap()
		            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
		                .addComponent(theDataBox, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                .addComponent(myScroll, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE))
		            .addContainerGap())
		);
	    myLayout.setVerticalGroup(
	      	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		       	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
		            .addComponent(theDataBox, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
		            .addComponent(myScroll)
		            .addContainerGap())
		);			
	}
		
	/* ItemStateChanged listener event */
	public void itemStateChanged(ItemEvent evt) {
		buildReport();
	}
			
	/* Print the report */
	public void    printReport() {
		/* Print the current report */
		try {
			theEditor.print();
		}
		catch (java.awt.print.PrinterException e) {}
	}
			
	/* Build Report */
	public void buildReport() {
		String             	myText = "<html><body>";

		/* Access the list from the events extract window */
		myText += theWindow.getDebugText();
		myText += "</body></html>";
		
		/* Set the report text */
		theEditor.setText(myText);
		theEditor.setCaretPosition(0);
		theEditor.requestFocusInWindow();
	}
		
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
					if ((url == null) && (desc.startsWith("#"))) {
						theEditor.scrollToReference(desc.substring(1));
					}
					else theEditor.setPage(e.getURL());
				}
				catch (Throwable t) {}
			}
		}
	}
	
	/* Debug Active states */
	protected enum DebugView {
		ERROR,
		EXTRACT,
		STATEMENT,
		UNITSTATEMENT,
		RATES,
		PATTERNS,
		PRICES,
		SPOTPRICES,
		ACCOUNT,
		TAXYEAR,
		NONE;
	}
}
