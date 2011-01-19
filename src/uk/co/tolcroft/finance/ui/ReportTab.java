package uk.co.tolcroft.finance.ui;

import java.net.URL;

import javax.swing.GroupLayout;
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

import uk.co.tolcroft.finance.ui.controls.*;
import uk.co.tolcroft.finance.ui.controls.FinanceInterfaces.*;
import uk.co.tolcroft.finance.ui.controls.ReportSelect.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.models.*;

/* Report Tab */
public class ReportTab implements HyperlinkListener,
								  financePanel {
	/* Properties */
	private View 				theView 	  = null;
	private JPanel              thePanel  	  = null;
	private JEditorPane         theEditor     = null;
	private ReportSelect 		theSelect     = null;
	private ReportType			theReportType = null;
	private Date				theDate       = null;
	private TaxYear				theYear       = null;
	private AnalysisYear.List	theList		  = null;
	private Properties			theProperties = null;
	
	/* Access methods */
	public JPanel       getPanel()       { return thePanel; }
	public boolean      hasUpdates()     { return false; }
	public boolean      isLocked()     	 { return false; }
	public EditState    getEditState()   { return EditState.CLEAN; }
	public void      	performCommand(financeCommand pCmd) { }
	
	/* Constructor */
	public ReportTab(MainTab pWindow) {
		JScrollPane   myScroll;
		HTMLEditorKit myKit;
		StyleSheet    myStyle;
		Document      myDoc;
		
		/* Store the view and properties */
		theView       = pWindow.getView();
		theProperties = pWindow.getProperties();
		
		/* Create the editor pane as non-editable */
		theEditor = new JEditorPane();
		theEditor.setEditable(false);
		theEditor.addHyperlinkListener(this);
		
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
		
		/* Create the Report Selection panel */
		theSelect = new ReportSelect(theView, this);
		
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
	                    .addComponent(theSelect.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(myScroll, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE))
	                .addContainerGap())
	    );
        myLayout.setVerticalGroup(
        	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
	                .addComponent(theSelect.getPanel())
	                .addComponent(myScroll)
	                .addContainerGap())
	    );			
	}
	
	/* refreshData */
	public void refreshData() {
		DataSet myData = theView.getData();
		
		/* Refresh the data */
		theList   = myData.getAnalyses();
		theSelect.refreshData(theList);
		buildReport();
	}
	
	/* Note that there has been a selection change */
	public void    notifySelection(Object obj)    {
		/* If this is a change from the report selection */
		if (obj == (Object) theSelect) {
			/* Build the report */
			buildReport();
		}			
	}
		
	/* Print the report */
	public void    printIt() {
		/* Print the current report */
		try {
			theEditor.print();
		}
		catch (java.awt.print.PrinterException e) {}
	}
		
	/* Build Report */
	private void buildReport() {
		AnalysisYear    mySet;
		AssetAnalysis	mySnapshot;
		AssetReport    	myAsset;
		IncomeReport	myIncome;
		TaxReport     	myTax;
		String          myText = "";
		
		/* Access the values from the selection */
		theReportType = theSelect.getReportType();
		theDate       = theSelect.getReportDate();
		theYear       = theSelect.getTaxYear();
		
		/* set lockdown of selection */
		theSelect.setLockDown();
		
		/* Skip if year is null */
		if (theYear == null) return;
		
		/* Switch on report type */
		switch (theReportType) {
			case ASSET:
				mySet   = theList.searchFor(theYear);
				myAsset = new AssetReport(mySet.getAssetAnalysis());
				myText  = myAsset.getYearReport(); 
				break;
				
			case INCOME:
				mySet    = theList.searchFor(theYear);
				myIncome = new IncomeReport(mySet.getIncomeAnalysis());
				myText   = myIncome.getReport();
				break;
				
			case TRANSACTION:
				mySet  = theList.searchFor(theYear);
				myTax  = new TaxReport(theProperties, mySet.getTaxAnalysis());
				myText = myTax.getTransReport();
				break;
				
			case TAX:
				mySet  = theList.searchFor(theYear);
				myTax  = new TaxReport(theProperties, mySet.getTaxAnalysis());
				myText = myTax.getTaxReport();
				break;
				
			case INSTANT:
				mySnapshot = new AssetAnalysis(theView.getData(), theDate);
				myAsset    = new AssetReport(mySnapshot);
				myText     = myAsset.getInstantReport();
				break;
				
			case MARKET:
				mySnapshot = new AssetAnalysis(theView.getData(), theDate);
				myAsset    = new AssetReport(mySnapshot);
				myText     = myAsset.getMarketReport();
				break;
		}

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
}