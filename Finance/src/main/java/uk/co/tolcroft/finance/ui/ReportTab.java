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
import uk.co.tolcroft.finance.ui.controls.ReportSelect.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.EventAnalysis.AnalysisYear;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.data.EditState;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugManager.*;
import uk.co.tolcroft.models.ui.ErrorPanel;
import uk.co.tolcroft.models.ui.StdInterfaces.*;

/* Report Tab */
public class ReportTab implements HyperlinkListener,
								  stdPanel {
	/* Properties */
	private View 				theView 	  	= null;
	private MainTab				theParent		= null;
	private JPanel              thePanel  	  	= null;
	private JScrollPane			theScroll		= null;
	private JEditorPane         theEditor     	= null;
	private ReportSelect 		theSelect     	= null;
	private ReportType			theReportType 	= null;
	private DateDay				theDate       	= null;
	private TaxYear				theYear       	= null;
	private EventAnalysis		theAnalysis	  	= null;
	private DebugEntry			theDebugReport	= null;
	private DebugEntry			theSpotEntry	= null;

	private ErrorPanel			theError		= null;
	
	/* Access methods */
	public JPanel       getPanel()       { return thePanel; }
	public boolean      hasUpdates()     { return false; }
	public boolean      isLocked()     	 { return false; }
	public EditState    getEditState()   { return EditState.CLEAN; }
	public void      	performCommand(stdCommand pCmd) { }
	
	/* Access the debug entry */
	public DebugEntry 	getDebugEntry()		{ return theDebugReport; }
	public DebugManager getDebugManager() 	{ return theParent.getDebugMgr(); }
	
	/**
	 * Constructor for Report Window
	 * @param pWindow the parent window
	 */
	public ReportTab(MainTab pWindow) {
		HTMLEditorKit myKit;
		StyleSheet    myStyle;
		Document      myDoc;
		DebugEntry	  mySection;
		
		/* Store the view and properties */
		theParent	  = pWindow;
		theView       = pWindow.getView();
		
		/* Create the top level debug entry for this view  */
		DebugManager myDebugMgr = theView.getDebugMgr();
		mySection = theView.getDebugEntry(View.DebugViews);
        theDebugReport 	= myDebugMgr.new DebugEntry("Report");
		theSpotEntry	= myDebugMgr.new DebugEntry("SpotAnalysis");
        theDebugReport.addAsChildOf(mySection);
        theSpotEntry.addAsChildOf(theDebugReport);
		theSpotEntry.hideEntry();
		
		/* Create the editor pane as non-editable */
		theEditor = new JEditorPane();
		theEditor.setEditable(false);
		theEditor.addHyperlinkListener(this);
		
		/* Add an editor kit to the editor */
		myKit = new HTMLEditorKit();
		theEditor.setEditorKit(myKit);
		
		/* Create a scroll-pane for the editor */
		theScroll = new JScrollPane(theEditor);
		
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
		
        /* Create the error panel for this view */
        theError = new ErrorPanel(this);
        
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
	                    .addComponent(theError.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(theSelect.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(theScroll, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE))
	                .addContainerGap())
	    );
        myLayout.setVerticalGroup(
        	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
	                .addComponent(theError.getPanel())
	                .addComponent(theSelect.getPanel())
	                .addComponent(theScroll)
	                .addContainerGap())
	    );			
	}
	
	/**
	 * Refresh views/controls after a load/update of underlying data
	 */
	public void refreshData() throws ModelException {		
		/* Hide the instant debug since it is now invalid */
		theSpotEntry.hideEntry();

		/* Refresh the data */
		theAnalysis	= theView.getAnalysis();
		theSelect.refreshData(theAnalysis);
		buildReport();
		
		/* Create SavePoint */
		theSelect.createSavePoint();
	}
	
	/**
	 * Lock on error
	 * @param isError is there an error (True/False)
	 */
	public void lockOnError(boolean isError) {
		/* Hide selection panel */
		theSelect.getPanel().setVisible(!isError);

		/* Lock scroll-able area */
		theScroll.setEnabled(!isError);
	}
	
	/**
	 *  Notify window that there has been a change in selection by an underlying control
	 *  @param obj the underlying control that has changed selection
	 */
	public void    notifySelection(Object obj)    {
		/* If this is a change from the report selection */
		if (obj == (Object) theSelect) {
			/* Protect against exceptions */
			try {
				/* Build the report */
				buildReport();
				
				/* Create SavePoint */
				theSelect.createSavePoint();
			}
			
			/* Catch Exceptions */
			catch (ModelException e) {
				/* Build the error */
				ModelException myError = new ModelException(ExceptionClass.DATA,
										          "Failed to change selection",
										          e);
				
				/* Show the error */
				theError.setError(myError);
				
				/* Restore SavePoint */
				theSelect.restoreSavePoint();
			}
		}			
	}
		
	/**
	 *  Print the report
	 */
	public void    printIt() {
		/* Print the current report */
		try {
			theEditor.print();
		}
		catch (java.awt.print.PrinterException e) {}
	}
		
	/**
	 *  Build the report
	 */
	private void buildReport() throws ModelException {
		AnalysisYear    myYear;
		EventAnalysis	mySnapshot;
		AnalysisReport	myReport;
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
				myYear  	= theAnalysis.getAnalysisYear(theYear);
				myReport	= new AnalysisReport(myYear);
				myText  	= myReport.getYearReport(); 
				break;
				
			case INCOME:
				myYear  	= theAnalysis.getAnalysisYear(theYear);
				myReport	= new AnalysisReport(myYear);
				myText   	= myReport.getIncomeReport();
				break;
				
			case TRANSACTION:
				myYear  	= theAnalysis.getAnalysisYear(theYear);
				myReport	= new AnalysisReport(myYear);
				myText 		= myReport.getTransReport();
				break;
				
			case TAX:
				myYear  	= theAnalysis.getAnalysisYear(theYear);
				myReport	= new AnalysisReport(myYear);
				myText 		= myReport.getTaxReport();
				break;
				
			case BREAKDOWN:
				myYear  	= theAnalysis.getAnalysisYear(theYear);
				myReport	= new AnalysisReport(myYear);
				myText		= myReport.getBreakdownReport();
				break;
				
			case INSTANT:
				mySnapshot 	= new EventAnalysis(theView.getData(),
												theDate);
				myReport   	= new AnalysisReport(mySnapshot);
				myText     	= myReport.getInstantReport();
				theSpotEntry.setObject(mySnapshot);
				theSpotEntry.showEntry();
				break;
				
			case MARKET:
				mySnapshot 	= new EventAnalysis(theView.getData(),
												theDate);
				myReport   	= new AnalysisReport(mySnapshot);
				myText     	= myReport.getMarketReport();
				theSpotEntry.setObject(mySnapshot);
				theSpotEntry.showEntry();
				break;
		}

		/* Set the report text */
		theEditor.setText(myText);
		theEditor.setCaretPosition(0);
		theEditor.requestFocusInWindow();
	}
	
	/**
	 *  Handle a HyperLink event
	 *  @param e the event
	 */
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