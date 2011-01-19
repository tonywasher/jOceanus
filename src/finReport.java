package finance;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.StyleSheet;

import finance.finAnalysis.BucketType;
import finance.finStatic.TaxClass;

public class finReport {
	/* Report Types */
	public enum ReportType {
		ASSET,
		INCOME,
		TAX,
		TRANSACTION,
		INSTANT,
		MARKET;
	}

	/* Report Tab */
	public static class ReportTab implements HyperlinkListener {
		/* Properties */
		private finView 			theView 	  = null;
		private JPanel              thePanel  	  = null;
		private JEditorPane         theEditor     = null;
		private ReportSelection 	theSelect     = null;
		private ReportType			theReportType = null;
		private finObject.Date		theDate       = null;
		private finData.TaxParms	theYear       = null;
		private finAnalysis			theReport     = null;
		private finAnalysis.List	theList		  = null;
		private finProperties		theProperties = null;
		
		/* Access methods */
		public JPanel       getPanel()       { return thePanel; }
		
		/* Constructor */
		public ReportTab(finSwing pWindow) {
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
			theSelect = new ReportSelection(theView, this);
			
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
			finData myData = theView.getData();
			
			/* Refresh the data */
			theSelect.refreshData();
			theReport = new finAnalysis(myData);
			theList   = myData.getTotals();
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
		public void    printReport() {
			/* Print the current report */
			try {
				theEditor.print();
			}
			catch (java.awt.print.PrinterException e) {}
		}
			
		/* Build Report */
		private void buildReport() {
			finAnalysis.Set    mySet;
			finAnalysis.Asset  mySnapshot;
			finReport.Asset    myAsset;
			finReport.Income   myIncome;
			finReport.Tax      myTax;
			String             myText = "";
			
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
					myAsset = new finReport.Asset(mySet.getAssetReport());
					myText  = myAsset.getYearReport(); 
					break;
					
				case INCOME:
					mySet    = theList.searchFor(theYear);
					myIncome = new finReport.Income(mySet.getIncomeReport());
					myText   = myIncome.getReport();
					break;
					
				case TRANSACTION:
					mySet  = theList.searchFor(theYear);
					myTax  = new finReport.Tax(theProperties, mySet.getTaxReport());
					myText = myTax.getTransReport();
					break;
					
				case TAX:
					mySet  = theList.searchFor(theYear);
					myTax  = new finReport.Tax(theProperties, mySet.getTaxReport());
					myText = myTax.getTaxReport();
					break;
					
				case INSTANT:
					mySnapshot = theReport.new Asset(theDate);
					myAsset    = new finReport.Asset(mySnapshot);
					myText     = myAsset.getInstantReport();
					break;
					
				case MARKET:
					mySnapshot = theReport.new Asset(theDate);
					myAsset    = new finReport.Asset(mySnapshot);
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
		
		/* Report Selection details */
		public static class ReportSelection implements ActionListener,
													   ItemListener,
													   ChangeListener {
			/* Members */
			private JPanel					thePanel		= null;
			private finReport.ReportTab     theControl		= null;
			private finView					theView			= null;
			private SpinnerDateModel        theModel        = null;
			private JSpinner                theDateBox      = null;
			private JComboBox               theReportBox 	= null;
			private JComboBox               theYearsBox 	= null;
			private JLabel					theRepLabel		= null;
			private JLabel					theYearLabel	= null;
			private JLabel					theDateLabel	= null;
			private JButton					thePrintButton	= null;
			private finObject.Date			theRepDate		= null;
			private finData.TaxParms	 	theYear			= null;
			private finReport.ReportType	theReport		= null;
			private finData.TaxParmList		theYears		= null;
			private boolean					yearsPopulated 	= false;
			private boolean					refreshingData  = false;
			
			/* Access methods */
			protected JPanel          	   	getPanel()      { return thePanel; }
			protected finReport.ReportType 	getReportType() { return theReport; }
			protected finData.TaxParms 	    getTaxYear()    { return theYear; }
			protected finObject.Date 	    getReportDate() { return theRepDate; }
						
			/* Report descriptions */
			private static final String Assets    	= "Asset";
			private static final String IncomeExp 	= "Income/Expense";
			private static final String Transaction	= "Transaction";
			private static final String Taxation  	= "Taxation";
			private static final String Instant   	= "Instant";
			private static final String Market    	= "Market";
		
			/* Constructor */
			public ReportSelection(finView pView, finReport.ReportTab pReport) {
				
				/* Store table and view details */
				theView 	  = pView;
				theControl	  = pReport;
				
				/* Create the boxes */
				theReportBox   = new JComboBox();
				theYearsBox    = new JComboBox();
				
				/* Create the DateSpinner Model and Box */
				theModel   = new SpinnerDateModel();
				theDateBox = new JSpinner(theModel);
				
				/* Initialise the data from the view */
				refreshData();
				
				/* Add the ReportTypes to the report box */
				theReportBox.addItem(Assets);
				theReportBox.addItem(IncomeExp);
				theReportBox.addItem(Transaction);
				theReportBox.addItem(Taxation);
				theReportBox.addItem(Instant);
				theReportBox.addItem(Market);
				theReportBox.setSelectedIndex(0);
				theReport = ReportType.ASSET;
				
				/* Create the labels */
				theRepLabel  = new JLabel("Report:");
				theYearLabel = new JLabel("Year:");
				theDateLabel = new JLabel("Date:");
				
				/* Create the print button */
				thePrintButton = new JButton("Print");
				thePrintButton.addActionListener(this);
				
				/* Limit the spinner to the Range */
				theModel.setValue(new java.util.Date());
				theRepDate = new finObject.Date(theModel.getDate());
			
				/* Set the format of the date */
				theDateBox.setEditor(new JSpinner.DateEditor(theDateBox, "dd-MMM-yyyy"));
			
				/* Add the listener for item changes */
				theReportBox.addItemListener(this);
				theYearsBox.addItemListener(this);
				theModel.addChangeListener(this);
				
				/* Create the panel */
				thePanel = new JPanel();
				thePanel.setBorder(javax.swing.BorderFactory
									.createTitledBorder("Report Selection"));

				/* Create the layout for the panel */
			    GroupLayout panelLayout = new GroupLayout(thePanel);
			    thePanel.setLayout(panelLayout);
			    
			    /* Set the layout */
			    panelLayout.setHorizontalGroup(
			    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			            .addGroup(panelLayout.createSequentialGroup()
			                .addContainerGap()
			                .addComponent(theRepLabel)
			                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			                .addComponent(theReportBox, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
			                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			                .addComponent(theYearLabel)
			                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			                .addComponent(theYearsBox)
			                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			                .addComponent(theDateLabel)
			                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			                .addComponent(theDateBox, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
			                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			                .addComponent(thePrintButton))
			    );
			    panelLayout.setVerticalGroup(
			        panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			            .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			                .addComponent(theRepLabel)
			                .addComponent(theReportBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			                .addComponent(theYearLabel)
			                .addComponent(theYearsBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			                .addComponent(theDateLabel)
			                .addComponent(theDateBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			                .addComponent(thePrintButton)
			    );

				/* Initiate lock-down mode */
				setLockDown();
			}
			
			/* refresh data */
			public void refreshData() {
				finData			  myData;
				finData.TaxParms  myYear;
				finObject.Range   myRange;
				
				/* Access the data */
				myData  = theView.getData();
				myRange = theView.getRange();
				
				/* Access tax Years */
				theYears    = myData.getTaxYears();
			
				/* Note that we are refreshing data */
				refreshingData = true;
				
				/* Set the range for the Date Spinner */
				setRange(myRange);
				
				/* If we have years already populated */
				if (yearsPopulated) {	
					/* If we have a selected year */
					if (theYear != null) {
						/* Find it in the new list */
						theYear = theYears.searchFor(theYear.getDate());
					}
					
					/* Remove the types */
					theYearsBox.removeAllItems();
					yearsPopulated = false;
				}
				
				/* Add the Year values to the years box */
				for (myYear  = theYears.getLast();
				     myYear != null;
				     myYear  = myYear.getPrev()) {
					/* Skip years that are not active */
					if (!myYear.isActive()) continue;
						
					/* Add the item to the list */
					theYearsBox.addItem(Integer.toString(myYear.getDate().getYear()));
					yearsPopulated = true;
				}
				
				/* If we have a selected year */
				if (theYear != null) {
					/* Select it in the new list */
					theYearsBox.setSelectedItem(theYear.getDate().getYear());
				}
				
				/* Else we have no year currently selected */
				else if (yearsPopulated) {
					/* Select the first year */
					theYearsBox.setSelectedIndex(0);
					theYear = theYears.getLast();
				}

				/* Note that we have finished refreshing data */
				refreshingData = false;
			}

			/* Set the range for the date box */
			public  void setRange(finObject.Range pRange) {
				finObject.Date myStart = null;
				finObject.Date myFirst;
				finObject.Date myLast;
				
				myFirst = (pRange == null) ? null : pRange.getStart();
				myLast = (pRange == null) ? null : pRange.getEnd();
				if (myFirst != null) {
					myStart = new finObject.Date(myFirst);
					myStart.adjustDay(-1);
				}
				theModel.setStart((myFirst == null) ? null : myStart.getDate());
				theModel.setEnd((myLast == null) ? null : myLast.getDate());
			}
			
			/* Lock/Unlock the selection */
			public void setLockDown() {
				boolean isDate    = ((theReport == ReportType.INSTANT) ||
						             (theReport == ReportType.MARKET));
				boolean isNull    = (theReport == null);
				boolean isYear    = (!isNull && !isDate);
				
				theDateBox.setEnabled(isDate);
				theDateLabel.setEnabled(isDate);
				theYearsBox.setEnabled(isYear);
				theYearLabel.setEnabled(isYear);				
			}
			
			/* actionPerformed listener event */
			public void actionPerformed(ActionEvent evt) {

				/* If this event relates to the Print button */
				if (evt.getSource() == (Object)thePrintButton) {
					/* Pass command to the table */
					theControl.printReport();
				}
			}
			
			/* ItemStateChanged listener event */
			public void itemStateChanged(ItemEvent evt) {
				String                myName;
				boolean               bChange = false;

				/* Ignore selection if refreshing data */
				if (refreshingData) return;
				
				/* If this event relates to the years box */
				if (evt.getSource() == (Object)theYearsBox) {
					myName = (String)evt.getItem();
					if (evt.getStateChange() == ItemEvent.SELECTED) {
						/* Select the new year */
						theYear = theYears.searchFor(myName);
						bChange = true;
					}
				}
								
				/* If this event relates to the report box */
				if (evt.getSource() == (Object)theReportBox) {
					myName = (String)evt.getItem();
					if (evt.getStateChange() == ItemEvent.SELECTED) {
						/* Determine the new report */
						bChange = true;
						if (myName == Assets)	      	theReport = ReportType.ASSET;
						else if (myName == IncomeExp) 	theReport = ReportType.INCOME;
						else if (myName == Transaction)	theReport = ReportType.TRANSACTION;
						else if (myName == Taxation)  	theReport = ReportType.TAX;
						else if (myName == Instant)   	theReport = ReportType.INSTANT;
						else if (myName == Market)    	theReport = ReportType.MARKET;
						else bChange = false;
					}
				}
				
				/* If we have a change, alert the table */
				if (bChange) { theControl.notifySelection(this); }
			}
			
			/* stateChanged listener event */
			public void stateChanged(ChangeEvent evt) {
				boolean bChange = false;
				
				/* If this event relates to the start box */
				if (evt.getSource() == (Object)theModel) {
					theRepDate = new finObject.Date(theModel.getDate());
					bChange    = true;
				}			
						
				/* If we have a change, notify the main program */
				if (bChange) { theControl.notifySelection(this); }
			}
		}		
	}
	
	private static StringBuilder makeMoneyItem(finObject.Money pAmount) {
		return makeMoneyCell(pAmount, false, 1);
	}
	
	private static StringBuilder makeMoneyTotal(finObject.Money pAmount) {
		return makeMoneyCell(pAmount, true, 1);
	}
	
	private static StringBuilder makeMoneyProfit(finObject.Money pAmount) {
		return makeMoneyCell(pAmount, true, 2);
	}
	
	private static StringBuilder makeMoneyCell(finObject.Money pAmount,
			                            	   boolean         isHighlighted,
			                            	   int             numCols) {
		StringBuilder 	myOutput = new StringBuilder(100);
		String 			myColour;
		String 			myHighlight = (isHighlighted) ? "h" : "d";
		
		/* Determine the colour of the cell */
		myColour = pAmount.isPositive() ? "blue" : "red";
		
		/* Build the cell */
		myOutput.append("<t");;
		myOutput.append(myHighlight);
		myOutput.append(" align=\"right\" color=\"");
		myOutput.append(myColour);
		myOutput.append("\"");
		if (numCols > 1) {
			myOutput.append(" colspan=\"");
			myOutput.append(numCols);
			myOutput.append("\"");
		}
		myOutput.append(">");
		if (pAmount.isNonZero())
			myOutput.append(pAmount.format(true));
		myOutput.append("</t");
		myOutput.append(myHighlight);
		myOutput.append(">");
		
		/* Return the detail */
		return myOutput;
	}
	
	private static StringBuilder makeUnitsItem(finObject.Units pUnits) {
		StringBuilder myOutput  = new StringBuilder(100);

		/* Build the cell */
		myOutput.append("<td align=\"right\" color=\"blue\">");
		if (pUnits.isNonZero())
			myOutput.append(pUnits.format(true));
		myOutput.append("</td>");

		/* Return the detail */
		return myOutput;
	}

	private static StringBuilder makePriceItem(finObject.Price pPrice) {
		StringBuilder myOutput = new StringBuilder(100);

		/* Build the cell */
		myOutput.append("<td align=\"right\" color=\"blue\">");
		if (pPrice.isNonZero())
			myOutput.append(pPrice.format(true));
		myOutput.append("</td>");

		/* Return the detail */
		return myOutput;
	}
	
	private static StringBuilder makeRateItem(finObject.Rate pRate) {
		StringBuilder myOutput = new StringBuilder(100);

		/* Build the cell */
		myOutput.append("<td align=\"right\" color=\"blue\">");
		if ((pRate != null) && (pRate.isNonZero()))
			myOutput.append(pRate.format(true));
		myOutput.append("</td>");

		/* Return the detail */
		return myOutput;
	}
	
	private static StringBuilder makeDateItem(finObject.Date pDate) {
		StringBuilder myOutput = new StringBuilder(100);
		
		/* Build the cell */
		myOutput.append("<td align=\"right\" color=\"blue\">");
		if ((pDate != null) && (!pDate.isNull()))
			myOutput.append(pDate.formatDate(false));
		myOutput.append("</td>");

		/* Return the detail */
		return myOutput;
	}
	
	/* Asset Report */
	public static class Asset {
		/* Properties */
		private finAnalysis.Asset 	theAsset	= null;
		private finObject.Date  	theDate 	= null;
		
		/* Constructor */
		public Asset(finAnalysis.Asset pAsset) {
			theAsset = pAsset;
			theDate  = pAsset.getDate();
		}
		
		/**
		 * Build a web output of the Year report
		 * @return Web output
		 */
		public String getYearReport() {
			finAnalysis.Asset.Bucket	myBucket;
			StringBuilder		   		myOutput = new StringBuilder(10000);
			StringBuilder          		myDetail = new StringBuilder(10000);	
			finObject.Money        		myProfit;
			finAnalysis.Asset.List 		myList;

			/* Endure that totals have been produced */
			theAsset.produceTotals();
			
			/* Access the bucket lists */
			myList = theAsset.getBuckets();
			
			/* Format the header */
			myOutput.append("<html><body><a name=\"Top\">");
			myOutput.append("<h1 align=\"center\">Asset Report for ");
			myOutput.append(theDate.getYear());
			myOutput.append("</h1></a>");
			myOutput.append("<table border=\"1\" width=\"75%\" align=\"center\">"); 
			myOutput.append("<thead><th rowspan=\"2\">Class</th><th colspan=\"2\">Value</th></thead>");
			myOutput.append("<thead><th>");
			myOutput.append(theDate.getYear());
			myOutput.append("</th><th>");
			myOutput.append(theDate.getYear()-1);
			myOutput.append("</th></thead>");
			myOutput.append("<tbody>");
			
			/* Loop through the Summary Buckets */
			for (myBucket = myList.getFirst();
			     myBucket != null;
			     myBucket = myBucket.getNext()) {
				
				/* Only process summary items */
				if (myBucket.getBucket() != BucketType.SUMMARY) continue;
				
				/* Format the Summary */
				myOutput.append("<tr><th align=\"center\">");
				myOutput.append("<a href=\"#Detail");
				myOutput.append(myBucket.getName());
				myOutput.append("\">");
				myOutput.append(myBucket.getName());
				myOutput.append("</a></th>");
				myOutput.append(makeMoneyItem(myBucket.getAmount()));
				myOutput.append(makeMoneyItem(myBucket.getOldAmount()));
				myOutput.append("</tr>");
					
				/* Format the detail */
				myDetail.append(makeStandardReport(myBucket));
			}
			
			/* Access the totals */
			myBucket = myList.getTotalsBucket();
			
			/* Format the totals */
			myOutput.append("<tr><th>Totals</th>");
			myOutput.append(makeMoneyTotal(myBucket.getAmount()));
			myOutput.append(makeMoneyTotal(myBucket.getOldAmount()));
			myOutput.append("</tr>");
			
			/* Format the profit */
			myOutput.append("<tr><th>Profit</th>");
			myProfit = new finObject.Money(myBucket.getAmount());
			myProfit.subtractAmount(myBucket.getOldAmount());
			myOutput.append(makeMoneyProfit(myProfit));
			myOutput.append("</tr></tbody></table>");
			
			/* Add the detail */
			myOutput.append(myDetail);
			
			/* Terminate the html */
			myOutput.append("</body></html>");
			
			/* Return the output */
			return myOutput.toString();
		}		
		
		/**
		 * Build a web output of the instant report
		 * @return Web output
		 */
		public String getInstantReport() {
			finAnalysis.Asset.Bucket	myBucket;
			StringBuilder		   		myOutput = new StringBuilder(10000);
			StringBuilder          		myDetail = new StringBuilder(10000);	
			finStatic.AccountType       myType;
			finAnalysis.Asset.List 		myList;

			/* Endure that totals have been produced */
			theAsset.produceTotals();
			
			/* Access the bucket lists */
			myList = theAsset.getBuckets();
			
			/* Format the header */
			myOutput.append("<html><body><a name=\"Top\">");
			myOutput.append("<h1 align=\"center\">Instant Asset Report for ");
			myOutput.append(theDate.formatDate(false));
			myOutput.append("</h1></a>");
			myOutput.append("<table border=\"1\" width=\"75%\" align=\"center\">"); 
			myOutput.append("<thead><th>Class</th><th>Value</th></thead>");
			myOutput.append("<tbody>");
			
			/* Loop through the Detail Buckets */
			for (myBucket = myList.getFirst();
			     myBucket != null;
			     myBucket = myBucket.getNext()) {
				
				/* Only process summary items */
				if (myBucket.getBucket() != BucketType.SUMMARY) continue;
				
				/* Format the Summary */
				myOutput.append("<tr><th align=\"center\">");
				myOutput.append("<a href=\"#Detail");
				myOutput.append(myBucket.getName());
				myOutput.append("\">");
				myOutput.append(myBucket.getName());
				myOutput.append("</a></th>");
				myOutput.append(makeMoneyItem(myBucket.getAmount()));
				myOutput.append("</tr>");
				
				/* Access the type */
				myType = myBucket.getType();
					
				/* Format the detail */
				if (myType.isMoney())
					myDetail.append(makeRatedReport(myBucket));
				else if (myType.isPriced())
					myDetail.append(makePricedReport(myBucket));
				else 
					myDetail.append(makeDebtReport(myBucket));
			}
			
			/* Access the totals */
			myBucket = myList.getTotalsBucket();
			
			/* Format the totals */
			myOutput.append("<tr><th>Totals</th>");
			myOutput.append(makeMoneyTotal(myBucket.getAmount()));
			myOutput.append("</tr></tbody></table>");
			
			/* Add the detail */
			myOutput.append(myDetail);
			
			/* Terminate the html */
			myOutput.append("</body></html>");
			
			/* Return the output */
			return myOutput.toString();
		}		
		
		/**
		 * Build a web output of the market report
		 * @return Web output
		 */
		public String getMarketReport() {
			finAnalysis.Asset.Bucket	myBucket;
			StringBuilder		   		myOutput = new StringBuilder(10000);
			finStatic.AccountType       myType; 
			finAnalysis.Asset.List 		myList;

			/* Endure that totals have been produced */
			theAsset.produceMarketTotals();
			
			/* Access the bucket lists */
			myList = theAsset.getBuckets();
			
			/* Format the header */
			myOutput.append("<html><body><a name=\"Top\">");
			myOutput.append("<h1 align=\"center\">Market Report for ");
			myOutput.append(theDate.formatDate(false));
			myOutput.append("</h1></a>");
			myOutput.append("<table border=\"1\" width=\"75%\" align=\"center\">"); 
			myOutput.append("<thead><th>Name</th><th>Value</th>");
			myOutput.append("<th>Invested</th><th>MarketGrowth</th></thead>");
			myOutput.append("<tbody>");
			
			/* Loop through the Detail Buckets */
			for (myBucket = myList.getFirst();
			     myBucket != null;
			     myBucket = myBucket.getNext()) {
				
				/* Only process detail items */
				if (myBucket.getBucket() != BucketType.DETAIL) break;
				
				/* Access the type */
				myType = myBucket.getType();
				
				/* Ignore non-priced items */
				if (!myType.isPriced()) continue;
				
				/* Format the Asset */
				myOutput.append("<tr><th align=\"center\">");
				myOutput.append(myBucket.getName());
				myOutput.append("</th>");
				myOutput.append(makeMoneyItem(myBucket.getAmount()));
				myOutput.append(makeMoneyItem(myBucket.getAdjust()));
				myOutput.append(makeMoneyItem(myBucket.getMarket()));
				myOutput.append("</tr>");
			}
			
			/* Access the totals */
			myBucket = myList.getTotalsBucket();
			
			/* Format the totals */
			myOutput.append("<tr><th>Totals</th>");
			myOutput.append(makeMoneyTotal(myBucket.getAmount()));
			myOutput.append(makeMoneyTotal(myBucket.getAdjust()));
			myOutput.append(makeMoneyTotal(myBucket.getMarket()));
			myOutput.append("</tr></tbody></table>");
			
			/* Terminate the html */
			myOutput.append("</body></html>");
			
			/* Return the output */
			return myOutput.toString();
		}		
		
		/**
		 * Build a standard yearly report element
		 * @param pSummary the class of the element
		 * @return Web output
		 */
		public StringBuilder makeStandardReport(finAnalysis.Asset.Bucket pSummary) {
			StringBuilder		   		myOutput = new StringBuilder(10000);
			finStatic.AccountType		myType;
			finAnalysis.Asset.Bucket	myBucket;
			finAnalysis.Asset.List 		myList;

			/* Access the bucket lists */
			myList = theAsset.getBuckets();
			
			/* Access the type */
			myType = pSummary.getType();
				
			/* Format the detail */
			myOutput.append("<a name=\"Detail");
			myOutput.append(pSummary.getName());
			myOutput.append("\">");
			myOutput.append("<h2 align=\"center\">");
			myOutput.append(pSummary.getName());
			myOutput.append("</h2></a>");
			myOutput.append("<table border=\"1\" width=\"75%\" align=\"center\">"); 
			myOutput.append("<thead><th rowspan=\"2\">Name</th><th colspan=\"2\">Value</th></thead>");
			myOutput.append("<thead><th>");
			myOutput.append(theDate.getYear());
			myOutput.append("</th><th>");
			myOutput.append(theDate.getYear()-1);
			myOutput.append("</th></thead>");
			myOutput.append("<tbody>");
			
			/* Loop through the Detail Buckets */
			for (myBucket = myList.getFirst();
			     myBucket != null;
			     myBucket = myBucket.getNext()) {
				
				/* Break loop if we have completed the details */
				if (myBucket.getBucket() != BucketType.DETAIL) break;
				
				/* Skip record if incorrect type */
				if (finObject.differs(myBucket.getType(), myType)) continue;

				/* Format the detail */
				myOutput.append("<tr><th align=\"center\">");
				myOutput.append(myBucket.getName());
				myOutput.append("</th>");
				myOutput.append(makeMoneyItem(myBucket.getAmount()));
				myOutput.append(makeMoneyItem(myBucket.getOldAmount()));
				myOutput.append("</tr>");
			}			 
			
			myOutput.append("<tr><th><a href=\"#Top\">Total</a></th>");
			myOutput.append(makeMoneyTotal(pSummary.getAmount()));
			myOutput.append(makeMoneyTotal(pSummary.getOldAmount()));
			myOutput.append("</tr></tbody></table>");
			
			/* Return the output */
			return myOutput;
		}
		
		/**
		 * Build a rated instant report element
		 * @param pSummary the class of the element
		 * @return Web output
		 */
		public StringBuilder makeRatedReport(finAnalysis.Asset.Bucket pSummary) {
			StringBuilder		   		myOutput = new StringBuilder(10000);
			finStatic.AccountType       myType;
			finAnalysis.Asset.Bucket	myBucket;
			finAnalysis.Asset.List 		myList;

			/* Access the bucket lists */
			myList = theAsset.getBuckets();
			
			/* Access the type */
			myType = pSummary.getType();
				
			/* Format the detail */
			myOutput.append("<a name=\"Detail");
			myOutput.append(pSummary.getName());
			myOutput.append("\">");
			myOutput.append("<h2 align=\"center\">");
			myOutput.append(pSummary.getName());
			myOutput.append("</h2></a>");
			myOutput.append("<table border=\"1\" width=\"75%\" align=\"center\">"); 
			myOutput.append("<thead><th>Name</th><th>Value</th>");
			myOutput.append("<th>Rate</th><th>Maturity</th></thead>");
			myOutput.append("<tbody>");
			
			/* Loop through the Detail Buckets */
			for (myBucket = myList.getFirst();
			     myBucket != null;
			     myBucket = myBucket.getNext()) {
				
				/* Break loop if we have completed the details */
				if (myBucket.getBucket() != BucketType.DETAIL) break;
				
				/* Skip record if incorrect type */
				if (finObject.differs(myBucket.getType(), myType)) continue;

				/* Format the detail */
				myOutput.append("<tr><th align=\"center\">");
				myOutput.append(myBucket.getName());
				myOutput.append("</th>");
				myOutput.append(makeMoneyItem(myBucket.getAmount()));
				myOutput.append(makeRateItem(myBucket.getRate()));
				myOutput.append(makeDateItem(myBucket.getDate()));
				myOutput.append("</tr>");
			}			 
			
			myOutput.append("<tr><th><a href=\"#Top\">Total</a></th>");
			myOutput.append(makeMoneyTotal(pSummary.getAmount()));
			myOutput.append("<td/><td/>");
			myOutput.append("</tr></tbody></table>");
			
			/* Return the output */
			return myOutput;
		}
		
		/**
		 * Build a debt instant report element
		 * @param pSummary the class of element
		 * @return Web output
		 */
		public StringBuilder makeDebtReport(finAnalysis.Asset.Bucket pSummary) {
			StringBuilder		   		myOutput = new StringBuilder(10000);
			finStatic.AccountType 		myType;
			finAnalysis.Asset.Bucket	myBucket;
			finAnalysis.Asset.List 		myList;

			/* Access the bucket lists */
			myList = theAsset.getBuckets();
			
			/* Access the type */
			myType = pSummary.getType();
				
			/* Format the detail */
			myOutput.append("<a name=\"Detail");
			myOutput.append(pSummary.getName());
			myOutput.append("\">");
			myOutput.append("<h2 align=\"center\">");
			myOutput.append(pSummary.getName());
			myOutput.append("</h2></a>");
			myOutput.append("<table border=\"1\" width=\"75%\" align=\"center\">"); 
			myOutput.append("<thead><th>Name</th><th>Value</th></thead>");
			myOutput.append("<tbody>");
			
			/* Loop through the Detail Buckets */
			for (myBucket = myList.getFirst();
			     myBucket != null;
			     myBucket = myBucket.getNext()) {
				
				/* Break loop if we have completed the details */
				if (myBucket.getBucket() != BucketType.DETAIL) break;
				
				/* Skip record if incorrect type */
				if (finObject.differs(myBucket.getType(), myType)) continue;

				/* Format the detail */
				myOutput.append("<tr><th align=\"center\">");
				myOutput.append(myBucket.getName());
				myOutput.append("</th>");
				myOutput.append(makeMoneyItem(myBucket.getAmount()));
				myOutput.append("</tr>");
			}			 
			
			myOutput.append("<tr><th><a href=\"#Top\">Total</a></th>");
			myOutput.append(makeMoneyTotal(pSummary.getAmount()));
			myOutput.append("</tr></tbody></table>");
			
			/* Return the output */
			return myOutput;
		}
		
		/**
		 * Build a priced instant report element
		 * @param pSummary the class of element
		 * @return Web output
		 */
		public StringBuilder makePricedReport(finAnalysis.Asset.Bucket pSummary) {
			StringBuilder		   		myOutput = new StringBuilder(10000);
			finStatic.AccountType		myType;
			finAnalysis.Asset.Bucket	myBucket;
			finAnalysis.Asset.List 		myList;

			/* Access the bucket lists */
			myList = theAsset.getBuckets();
			
			/* Access the type */
			myType = pSummary.getType();
				
			/* Format the detail */
			myOutput.append("<a name=\"Detail");
			myOutput.append(pSummary.getName());
			myOutput.append("\">");
			myOutput.append("<h2 align=\"center\">");
			myOutput.append(pSummary.getName());
			myOutput.append("</h2></a>");
			myOutput.append("<table border=\"1\" width=\"75%\" align=\"center\">"); 
			myOutput.append("<thead><th>Name</th><th>Units</th>");
			myOutput.append("<th>Price</th><th>Value</th></thead>");
			myOutput.append("<tbody>");
			
			/* Loop through the Detail Buckets */
			for (myBucket = myList.getFirst();
			     myBucket != null;
			     myBucket = myBucket.getNext()) {
				
				/* Break loop if we have completed the details */
				if (myBucket.getBucket() != BucketType.DETAIL) break;
				
				/* Skip record if incorrect type */
				if (finObject.differs(myBucket.getType(), myType)) continue;

				/* Format the detail */
				myOutput.append("<tr><th align=\"center\">");
				myOutput.append(myBucket.getName());
				myOutput.append("</th>");
				myOutput.append(makeUnitsItem(myBucket.getUnits()));
				myOutput.append(makePriceItem(myBucket.getPrice()));
				myOutput.append(makeMoneyItem(myBucket.getAmount()));
				myOutput.append("</tr>");
			}			 
			
			myOutput.append("<tr><th><a href=\"#Top\">Total</a></th>");
			myOutput.append("<td/><td/>");
			myOutput.append(makeMoneyTotal(pSummary.getAmount()));
			myOutput.append("</tr></tbody></table>");
			
			/* Return the output */
			return myOutput;
		}		
	}
	
	/* Income/Expense Report */
	public static class Income {
		/* Properties */
		private finAnalysis.Income 	theIncome	= null;
		private finObject.Date  	theDate 	= null;
		
		/* Constructor */
		public Income(finAnalysis.Income pIncome) {
			theIncome  = pIncome;
			theDate    = pIncome.getDate();
		}
		
		/**
		 * Build a web output of the report
		 * @return Web output
		 */
		public String getReport() {
			finAnalysis.Income.Bucket 	myBucket;
			StringBuilder		 		myOutput = new StringBuilder(10000);
			finObject.Money       		myProfit;
			finAnalysis.Income.List 	myList;

			/* Make sure that totals have been produced */
			theIncome.produceTotals();
			
			/* Access the bucket lists */
			myList = theIncome.getBuckets();
			
			/* Format the header */
			myOutput.append("<html><body><a name=\"Top\">");
			myOutput.append("<h1 align=\"center\">Income/Expense Report for ");
			myOutput.append(theDate.getYear());
			myOutput.append("</h1></a>");
			myOutput.append("<table border=\"1\" width=\"75%\" align=\"center\">"); 
			myOutput.append("<thead><th rowspan=\"2\">Name</th>");
			myOutput.append("<th colspan=\"2\">");
			myOutput.append(theDate.getYear());
			myOutput.append("</th>");
			myOutput.append("<th colspan=\"2\">");
			myOutput.append(theDate.getYear()-1);
			myOutput.append("</th></thead>");
			myOutput.append("<thead><th>Income</th><th>Expense</th>");
			myOutput.append("<th>Income</th><th>Expense</th></thead>");
			myOutput.append("<tbody>");
				
			/* Loop through the Detail Buckets */
			for (myBucket = myList.getFirst();
			     myBucket != null;
			     myBucket = myBucket.getNext()) {
				
				/* Break loop if we have completed the details */
				if (myBucket.getBucket() != BucketType.DETAIL) break;
								
				/* Format the detail */
				myOutput.append("<tr><th align=\"center\">");
				myOutput.append(myBucket.getName());
				myOutput.append("</th>");
				myOutput.append(makeMoneyItem(myBucket.getIncome()));
				myOutput.append(makeMoneyItem(myBucket.getExpense()));
				myOutput.append(makeMoneyItem(myBucket.getOldIncome()));
				myOutput.append(makeMoneyItem(myBucket.getOldExpense()));
				myOutput.append("</tr>");
			}			 
			
			/* Access the totals */
			myBucket = myList.getTotalsBucket();
			
			/* Format the totals */
			myOutput.append("<tr><th>Totals</th>");
			myOutput.append(makeMoneyTotal(myBucket.getIncome()));
			myOutput.append(makeMoneyTotal(myBucket.getExpense()));
			myOutput.append(makeMoneyTotal(myBucket.getOldIncome()));
			myOutput.append(makeMoneyTotal(myBucket.getOldExpense()));
			myOutput.append("</tr>");
			
			/* Format the profit */
			myOutput.append("<tr><th>Profit</th>");
			myProfit = new finObject.Money(myBucket.getIncome());
			myProfit.subtractAmount(myBucket.getExpense());
			myOutput.append(makeMoneyProfit(myProfit));
			myProfit = new finObject.Money(myBucket.getOldIncome());
			myProfit.subtractAmount(myBucket.getOldExpense());
			myOutput.append(makeMoneyProfit(myProfit));
			myOutput.append("</tr></tbody></table></body></html>");
			
			/* Return the output */
			return myOutput.toString();
		}		
	}
	
	/* Tax Report */
	public static class Tax {
		/* Properties */
		private finAnalysis.Tax theTax 		  = null;
		private finObject.Date  theDate 	  = null;
		private finProperties	theProperties = null;
		
		/* Constructor */
		public Tax(finProperties pProperties, finAnalysis.Tax pTax) {
			theTax  	  = pTax;
			theDate       = pTax.getDate();
			theProperties = pProperties;
		}
		
		/**
		 * Build a web output of the transaction report
		 * 
		 * @return Web output
		 */
		public String getTransReport() {
			finAnalysis.Tax.TranBucket  myBucket;
			StringBuilder		        myOutput = new StringBuilder(10000);
			finAnalysis.Tax.TranList    myTranList;

			/* Ensure that totals have been produced */
			theTax.produceTotals(theProperties);
			
			/* Access the bucket lists */
			myTranList = theTax.getTransBuckets();
			
			/* Format the header */
			myOutput.append("<html><body><a name=\"Top\">");
			myOutput.append("<h1 align=\"center\">Transaction Report for ");
			myOutput.append(theDate.getYear());
			myOutput.append("</h1></a>");
			myOutput.append("<a name=\"TransactionTotals\"><h2 align=\"center\">Transaction Totals</h2></a>");
			myOutput.append("<table border=\"1\" width=\"75%\" align=\"center\">"); 
			myOutput.append("<thead><th rowspan=\"2\">Class</th><th colspan=\"2\">Value</th></thead>");
			myOutput.append("<thead><th>");
			myOutput.append(theDate.getYear());
			myOutput.append("</th><th>");
			myOutput.append(theDate.getYear()-1);
			myOutput.append("</th></thead>");
			myOutput.append("<tbody>");
		
			/* Loop through the Transaction Summary Buckets */
			for (myBucket = myTranList.getFirst();
			     myBucket != null;
			     myBucket = myBucket.getNext()) {
				
				/* Skip the non-summary items */
				if (myBucket.getBucket() != BucketType.SUMMARY) continue;
								
				/* Format the detail */
				myOutput.append("<tr><th align=\"center\">" + myBucket.getName() + "</th>");
				myOutput.append(makeMoneyItem(myBucket.getAmount()));
				myOutput.append(makeMoneyItem(myBucket.getOldAmount()));
				myOutput.append("</tr>");
			}

			/* Format the next table */
			myOutput.append("</tbody></table>");
			myOutput.append("<a name=\"Trans\"><h2 align=\"center\">Transaction Breakdown</h2></a>");
			myOutput.append("<table border=\"1\" width=\"75%\" align=\"center\">"); 
			myOutput.append("<thead><th rowspan=\"2\">Class</th>");
			myOutput.append("<th colspan=\"2\">");
			myOutput.append(theDate.getYear());
			myOutput.append("</th><th  colspan=\"2\">");
			myOutput.append(theDate.getYear()-1);
			myOutput.append("</th></thead>");
			myOutput.append("<thead><th>Value</th><th>TaxCredit</th><th>Value</th><th>TaxCredit</th></thead>");
			myOutput.append("<tbody>");
				
			/* Loop through the Transaction Detail Buckets */
			for (myBucket = myTranList.getFirst();
			     myBucket != null;
			     myBucket = myBucket.getNext()) {
				
				/* Break loop if we have completed the details */
				if (myBucket.getBucket() != BucketType.DETAIL) break;
								
				/* Format the detail */
				myOutput.append("<tr><th align=\"center\">");
				myOutput.append(myBucket.getName());
				myOutput.append("</th>");
				myOutput.append(makeMoneyItem(myBucket.getAmount()));
				myOutput.append(makeMoneyItem(myBucket.getTaxCredit()));
				myOutput.append(makeMoneyItem(myBucket.getOldAmount()));
				myOutput.append(makeMoneyItem(myBucket.getOldTaxCred()));
				myOutput.append("</tr>");
			}			 
			
			/* Close the table */
			myOutput.append("</tbody></table></body></html>");
			
			/* Return the output */
			return myOutput.toString();
		}
		
		/**
		 * Build a web output of the taxation report
		 * 
		 * @return Web output
		 */
		public String getTaxReport() {
			finAnalysis.Tax.TaxBucket  	myTaxBucket;
			finAnalysis.Tax.TranBucket	myTranBucket;
			StringBuilder		       	myOutput = new StringBuilder(10000);
			StringBuilder			  	myDetail = new StringBuilder(10000);
			finAnalysis.Tax.TaxList    	myTaxList;
			finAnalysis.Tax.TranList    myTranList;
			finData.TaxParms		   	myYear;
			finStatic.TaxTypeList	   	myTaxTypes;

			/* Ensure that totals have been produced */
			theTax.produceTotals(theProperties);
			
			/* Access the bucket lists */
			myTaxList  = theTax.getTaxBuckets();
			myTranList = theTax.getTransBuckets();
			myYear	   = theTax.getYear();
			myTaxTypes = theTax.getTaxTypes();
			
			/* Initialise the detail */
			myDetail.append("<h1 align=\"center\">Taxation Breakdown</h1>");
			
			/* Format the header */
			myOutput.append("<html><body><a name=\"Top\">");
			myOutput.append("<h1 align=\"center\">Taxation Report for ");
			myOutput.append(theDate.getYear());
			myOutput.append("</h1></a>");
			myOutput.append("<a name=\"TaxSummary\"><h2 align=\"center\">Taxation Summary</h2></a>");
			myOutput.append("<table border=\"1\" width=\"75%\" align=\"center\">"); 
			myOutput.append("<thead><th>Class</th><th>Total Income</th><th>Taxation Due</th></thead>");
			myOutput.append("<tbody>");
		
			/* Loop through the Tax Summary Buckets */
			for (myTaxBucket = myTaxList.getFirst();
			     myTaxBucket != null;
			     myTaxBucket = myTaxBucket.getNext()) {
				
				/* Skip the detail elements */
				if (myTaxBucket.getBucket() == BucketType.DETAIL) continue;
								
				/* Break loop if we are on to the totals */
				if (myTaxBucket.getBucket() != BucketType.SUMMARY) break;
								
				/* Format the line */
				myOutput.append("<tr><th align=\"center\">");
				myOutput.append("<a href=\"#Detail");
				myOutput.append(myTaxBucket.getName());
				myOutput.append("\">");
				myOutput.append(myTaxBucket.getName());
				myOutput.append("</a></th>");
				myOutput.append(makeMoneyItem(myTaxBucket.getAmount()));
				myOutput.append(makeMoneyItem(myTaxBucket.getTaxation()));
				myOutput.append("</tr>");
				
				/* Format the detail */
				myDetail.append(makeTaxReport(myTaxBucket));
			}

			/* Access the Total taxation bucket */
			myTaxBucket = myTaxList.getTaxBucket(myTaxTypes.searchFor(TaxClass.TOTALTAX));
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append(myTaxBucket.getName());
			myOutput.append("</th>");
			myOutput.append(makeMoneyTotal(myTaxBucket.getAmount()));
			myOutput.append(makeMoneyTotal(myTaxBucket.getTaxation()));
			myOutput.append("</tr>");

			/* Access the Tax Paid bucket */
			myTranBucket = myTranList.getSummaryBucket(myTaxTypes.searchFor(TaxClass.TAXPAID));
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append(myTranBucket.getName());
			myOutput.append("</th>");
			myOutput.append(makeMoneyTotal(new finObject.Money(0)));
			myOutput.append(makeMoneyTotal(myTranBucket.getAmount()));
			myOutput.append("</tr>");

			/* Access the Tax Profit bucket */
			myTaxBucket = myTaxList.getTaxBucket(myTaxTypes.searchFor(TaxClass.TAXPROFIT));
			myOutput.append("<tr><th align=\"center\">");
			myOutput.append(myTaxBucket.getName());
			myOutput.append("</th>");
			myOutput.append(makeMoneyTotal(myTaxBucket.getAmount()));
			myOutput.append(makeMoneyTotal(myTaxBucket.getTaxation()));
			myOutput.append("</tr>");

			/* Finish the table */
			myOutput.append("</tbody></table>");

			/* Format the tax parameters */
			myOutput.append("<a name=\"TaxParms\"><h1 align=\"center\">Taxation Parameters</h1></a>");

			/* Format the allowances */
			myOutput.append("<a name=\"Allowances\"><h2 align=\"center\">Allowances</h2></a>");
			myOutput.append("<table border=\"1\" width=\"75%\" align=\"center\">");
			myOutput.append("<thead><th>Name</th><th>Value</th></thead>");
			myOutput.append("<tbody>");
			myOutput.append("<tr><th>PersonalAllowance</th>");
			myOutput.append(makeMoneyItem(myYear.getAllowance()));
			myOutput.append("</tr>");
			myOutput.append("<tr><th>Age 65-74 PersonalAllowance</th>");
			myOutput.append(makeMoneyItem(myYear.getLoAgeAllow()));
			myOutput.append("</tr>");
			myOutput.append("<tr><th>Age 75+ PersonalAllowance</th>");
			myOutput.append(makeMoneyItem(myYear.getHiAgeAllow()));
			myOutput.append("</tr>");
			myOutput.append("<tr><th>RentalAllowance</th>");
			myOutput.append(makeMoneyItem(myYear.getRentalAllowance()));
			myOutput.append("</tr>");
			myOutput.append("<tr><th>CapitalAllowance</th>");
			myOutput.append(makeMoneyItem(myYear.getCapitalAllow()));
			myOutput.append("</tr>");
			myOutput.append("<tr><th>Income Limit for AgeAllowance</th>");
			myOutput.append(makeMoneyItem(myYear.getAgeAllowLimit()));
			myOutput.append("</tr>");
			if (myYear.hasAdditionalTaxBand()) {
				myOutput.append("<tr><th>Income Limit for PersonalAllowance</th>");
				myOutput.append(makeMoneyItem(myYear.getAddAllowLimit()));
				myOutput.append("</tr>");
			}
			myOutput.append("</tbody></table>");
			
			/* Format the Rates */
			myOutput.append("<a name=\"Allowances\"><h2 align=\"center\">TaxRates</h2></a>");
			myOutput.append("<table border=\"1\" width=\"75%\" align=\"center\">");
			myOutput.append("<thead><th>IncomeType</th><th>LoRate</th>");
			myOutput.append("<th>BasicRate</th><th>HiRate</th>");
			if (myYear.hasAdditionalTaxBand()) 
				myOutput.append("<th>AdditionalRate</th>");
			myOutput.append("</thead><tbody>");
			myOutput.append("<tr><th>Salary/Rental</th>");
			myOutput.append(makeRateItem(myYear.hasLoSalaryBand()  
											? myYear.getLoTaxRate()
											: null));
			myOutput.append(makeRateItem(myYear.getBasicTaxRate()));
			myOutput.append(makeRateItem(myYear.getHiTaxRate()));
			if (myYear.hasAdditionalTaxBand()) 
				myOutput.append(makeRateItem(myYear.getAddTaxRate()));
			myOutput.append("</tr>");
			myOutput.append("<tr><th>Interest</th>");
			myOutput.append(makeRateItem(myYear.getLoTaxRate()));
			myOutput.append(makeRateItem(myYear.getIntTaxRate()));
			myOutput.append(makeRateItem(myYear.getHiTaxRate()));
			if (myYear.hasAdditionalTaxBand()) 
				myOutput.append(makeRateItem(myYear.getAddTaxRate()));
			myOutput.append("</tr>");
			myOutput.append("<tr><th>Dividends</th>");
			myOutput.append(makeRateItem(null));
			myOutput.append(makeRateItem(myYear.getDivTaxRate()));
			myOutput.append(makeRateItem(myYear.getHiDivTaxRate()));
			if (myYear.hasAdditionalTaxBand()) 
				myOutput.append(makeRateItem(myYear.getAddDivTaxRate()));
			myOutput.append("</tr>");
			myOutput.append("<tr><th>TaxableGains</th>");
			myOutput.append(makeRateItem(null));
			myOutput.append(makeRateItem(myYear.getBasicTaxRate()));
			myOutput.append(makeRateItem(myYear.getHiTaxRate()));
			if (myYear.hasAdditionalTaxBand()) 
				myOutput.append(makeRateItem(myYear.getAddTaxRate()));
			myOutput.append("</tr>");
			myOutput.append("<tr><th>CapitalGains</th>");
			myOutput.append(makeRateItem(null));
			myOutput.append(makeRateItem(myYear.getCapTaxRate()));
			myOutput.append(makeRateItem(myYear.getHiCapTaxRate()));
			if (myYear.hasAdditionalTaxBand()) 
				myOutput.append(makeRateItem(null));
			myOutput.append("</tr>");
			myOutput.append("</tbody></table>");
			
			/* Format the tax bands */
			myOutput.append("<a name=\"Allowances\"><h2 align=\"center\">TaxBands</h2></a>");
			myOutput.append("<table border=\"1\" width=\"75%\" align=\"center\">");
			myOutput.append("<thead><th>Name</th><th>Value</th></thead>");
			myOutput.append("<tbody>");
			myOutput.append("<tr><th>Age for Tax Year</th>");
			myOutput.append("<td align=\"right\" color=\"blue\">");
			myOutput.append(theTax.getAge());
			myOutput.append("</td></tr>");

			/* Access the original allowance */
			myTaxBucket = myTaxList.getTaxBucket(myTaxTypes
										.searchFor(TaxClass.ORIGALLOW));
			myOutput.append("<tr><th>Personal Allowance</th>");
			myOutput.append(makeMoneyItem(myTaxBucket.getAmount()));
			myOutput.append("</tr>");
 
			/* if we have adjusted the allowance */
			if (theTax.hasReducedAllow()) {
				/* Access the gross income */
				myTaxBucket = myTaxList.getTaxBucket(myTaxTypes
											.searchFor(TaxClass.GROSSINCOME));
				myOutput.append("<tr><th>Gross Taxable Income</th>");
				myOutput.append(makeMoneyItem(myTaxBucket.getAmount()));
				myOutput.append("</tr>");
 
				/* Access the gross income */
				myTaxBucket = myTaxList.getTaxBucket(myTaxTypes
											.searchFor(TaxClass.ADJALLOW));
				myOutput.append("<tr><th>Adjusted Allowance</th>");
				myOutput.append(makeMoneyItem(myTaxBucket.getAmount()));
				myOutput.append("</tr>");
			}
			
			/* Access the Low Tax Band */
			if (myYear.getLoBand() != null) {
				myOutput.append("<tr><th>Low Tax Band</th>");
				myOutput.append(makeMoneyItem(myYear.getLoBand()));
				myOutput.append("</tr>");
			}
 
			/* Access the Basic Tax Band */
			myOutput.append("<tr><th>Basic Tax Band</th>");
			myOutput.append(makeMoneyItem(myYear.getBasicBand()));
			myOutput.append("</tr>");
 
			/* If we have a high tax band */
			if (myYear.hasAdditionalTaxBand()) {
				/* Access the gross income */
				myTaxBucket = myTaxList.getTaxBucket(myTaxTypes
											.searchFor(TaxClass.HITAXBAND));
				myOutput.append("<tr><th>High Tax Band</th>");
				myOutput.append(makeMoneyItem(myTaxBucket.getAmount()));
				myOutput.append("</tr>");				
			}
			myOutput.append("</tbody></table>");
			
 			/* Add the detail */
			myOutput.append(myDetail);
			
			/* If we need a tax slice report */
			if (theTax.hasGainsSlices())
				myOutput.append(makeTaxSliceReport());
			
			/* Close the document */
			myOutput.append("</body></html>");
			
			/* Return the output */
			return myOutput.toString();
		}
		
		/**
		 * Build a standard tax report element
		 * 
		 * @return Web output
		 */
		public StringBuilder makeTaxReport(finAnalysis.Tax.TaxBucket pSummary) {
			StringBuilder		   		myOutput = new StringBuilder(1000);
			finAnalysis.Tax.TaxBucket	myBucket;
			finAnalysis.Tax.TaxList 	myList;

			/* Access the bucket lists */
			myList = theTax.getTaxBuckets();
			
			/* Format the detail */
			myOutput.append("<a name=\"Detail");
			myOutput.append(pSummary.getName());
			myOutput.append("\">");
			myOutput.append("<h2 align=\"center\">");
			myOutput.append(pSummary.getName());
			myOutput.append("</h2></a>");
			myOutput.append("<table border=\"1\" width=\"75%\" align=\"center\">"); 
			myOutput.append("<thead><th>Class</th><th>Income</th>");
			myOutput.append("<th>Rate</th><th>Taxation Due</th></thead>");
			myOutput.append("<tbody>");
			
			/* Loop through the Detail Buckets */
			for (myBucket = myList.getFirst();
			     myBucket != null;
			     myBucket = myBucket.getNext()) {
				
				/* Break loop if we have completed the details */
				if (myBucket.getBucket() != BucketType.DETAIL) break;
				
				/* Skip record if incorrect parent */
				if (myBucket.getParent() != pSummary) continue;

				/* Format the detail */
				myOutput.append("<tr><th align=\"center\">");
				myOutput.append(myBucket.getName());
				myOutput.append("</th>");
				myOutput.append(makeMoneyItem(myBucket.getAmount()));
				myOutput.append(makeRateItem(myBucket.getRate()));
				myOutput.append(makeMoneyItem(myBucket.getTaxation()));
				myOutput.append("</tr>");
			}			 
			
			myOutput.append("<tr><th><a href=\"#Top\">Total</a></th>");
			myOutput.append(makeMoneyTotal(pSummary.getAmount()));
			myOutput.append(makeRateItem(null));
			myOutput.append(makeMoneyTotal(pSummary.getTaxation()));
			myOutput.append("</tr></tbody></table>");
			
			/* Return the output */
			return myOutput;
		}
		
		/**
		 * Build a tax slice report
		 * 
		 * @return Web output
		 */
		public StringBuilder makeTaxSliceReport() {
			StringBuilder					myOutput = new StringBuilder(1000);
			finAnalysis.Tax.chargeableEvent	myCharge;
			finAnalysis.Tax.TaxBucket	    myBucket;
			finAnalysis.Tax.TaxList 	    myList;
			finStatic.TaxTypeList	   		myTaxTypes;

			/* Access the bucket lists */
			myList 		= theTax.getTaxBuckets();
			myTaxTypes 	= theTax.getTaxTypes();

			/* Format the detail */
			myOutput.append("<a name=\"DetailChargeableEvents>");
			myOutput.append("<h2 align=\"center\">Chargeable Events</h2></a>");
			myOutput.append("<table border=\"1\" width=\"75%\" align=\"center\">"); 
			myOutput.append("<thead><th>Date</th><th>Description</th>");
			myOutput.append("<th>Amount</th><th>TaxCredit</th><th>Years</th>");
			myOutput.append("<th>Slice</th><th>Taxation</th></thead>");
			myOutput.append("<tbody>");
			
			/* Loop through the Charges */
			for (myCharge  = theTax.getCharges();
			     myCharge != null;
			     myCharge  = myCharge.getNext()) {
				
				/* Format the detail */
				myOutput.append("<tr><td>");
				myOutput.append(myCharge.getDate().formatDate(false));
				myOutput.append("</td><td>");
				myOutput.append(myCharge.getDesc());
				myOutput.append("</td>");
				myOutput.append(makeMoneyItem(myCharge.getAmount()));
				myOutput.append(makeMoneyItem(myCharge.getTaxCredit()));
				myOutput.append("<td>");
				myOutput.append(myCharge.getYears());
				myOutput.append("</td>");
				myOutput.append(makeMoneyItem(myCharge.getSlice()));
				myOutput.append(makeMoneyItem(myCharge.getTaxation()));
				myOutput.append("</tr>");
			}			 
			myOutput.append("<tr><th>Totals</th><td/><td/>");
			myOutput.append(makeMoneyTotal(theTax.getCharges().getGainsTotal()));
			myOutput.append("<td/>");
			myOutput.append(makeMoneyTotal(theTax.getCharges().getSliceTotal()));
			myOutput.append(makeMoneyTotal(theTax.getCharges().getTaxTotal()));
			myOutput.append("</tr></tbody></table>");
				
			/* Access the Summary Tax Due Slice */
			myBucket = myList.getTaxBucket(myTaxTypes
									.searchFor(TaxClass.TAXDUESLICE));
			
			/* Add the Slice taxation details */
			myOutput.append(makeTaxReport(myBucket));
			
			/* Return the output */
			return myOutput;
		}		
	}
}
