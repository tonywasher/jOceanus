package uk.co.tolcroft.finance.ui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugManager.*;

public class MaintenanceTab implements ChangeListener {
	/* properties */
	private View				theView 	  	= null;
	private MainTab				theParent	  	= null;
	private JPanel            	thePanel      	= null;
	private JTabbedPane       	theTabs       	= null;
	private MaintAccount		theAccountTab	= null;
	private MaintTaxYear		theTaxYearTab	= null;
	private MaintProperties		theProperties	= null;
	private MaintNewYear  		thePatternYear	= null;
	private MaintStatic  		theStatic		= null;
	private DebugEntry			theDebugEntry	= null;

	/* Access methods */
	protected JPanel    getPanel()		{ return thePanel; }
	protected View		getView() 		{ return theView; }
	protected Font	getStdFont(boolean isFixed) { return null; }
	protected Font	getChgFont(boolean isFixed) { return null; }
	protected MainTab	getTopWindow()		{ return theParent; }
	public DebugEntry	getDebugEntry()		{ return theDebugEntry; }
	public DebugManager getDebugManager() 	{ return theParent.getDebugMgr(); }
	
	/* Tab titles */
	private static final String titleAccounts 	= "Accounts";
	private static final String titleTaxYear  	= "TaxYears";
	private static final String titleProperties	= "Properties";
	private static final String titlePattern  	= "PatternYear";
	private static final String titleStatic  	= "Static";
	
	/* Constructor */
	public MaintenanceTab(MainTab pTop) {
		/* Store details */
		theView 	= pTop.getView();
		theParent 	= pTop;
		
		/* Create the top level debug entry for this view  */
		DebugManager myDebugMgr = theView.getDebugMgr();
		DebugEntry   mySection  = theView.getDebugEntry(View.DebugViews);
        theDebugEntry = myDebugMgr.new DebugEntry("Maintenance");
        theDebugEntry.addAsChildOf(mySection);
		
		/* Create the Tabbed Pane */
		theTabs = new JTabbedPane();
		theTabs.addChangeListener(this);
			
		/* Create the account Tab and add it */
		theAccountTab = new MaintAccount(this);
		theTabs.addTab(titleAccounts, theAccountTab.getPanel());
		
		/* Create the TaxYears Tab */
		theTaxYearTab = new MaintTaxYear(this);
		theTabs.addTab(titleTaxYear, theTaxYearTab.getPanel());
		
		/* Create the Properties Tab */
		theProperties = new MaintProperties(this);
		theTabs.addTab(titleProperties, theProperties.getPanel());
		
		/* Create the PatternYear Tab */
		thePatternYear = new MaintNewYear(this);
		theTabs.addTab(titlePattern, thePatternYear.getPanel());
		
		/* Create the Static Tab */
		theStatic = new MaintStatic(this);
		theTabs.addTab(titleStatic, theStatic.getPanel());
		
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
	                    .addComponent(theTabs, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addContainerGap())
	    );
        myLayout.setVerticalGroup(
        	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
	                .addComponent(theTabs))
	    );
	}
	
	/* refresh data */
	public void refreshData() throws Exception {
		/* Refresh sub-panels */
		theAccountTab.refreshData();
		theTaxYearTab.refreshData();
		theProperties.refreshData();
		thePatternYear.refreshData();
		theStatic.refreshData();
	}
	
	/* Has this set of tables got updates */
	public boolean hasUpdates() {
		boolean hasUpdates = false;
		
		/* Determine whether we have updates */
		hasUpdates = theAccountTab.hasUpdates();
        if (!hasUpdates) hasUpdates = theTaxYearTab.hasUpdates();
        if (!hasUpdates) hasUpdates = theProperties.hasUpdates();
        if (!hasUpdates) hasUpdates = theStatic.hasUpdates();
 			
		/* Return to caller */
		return hasUpdates;
	}
		
	/* Select an explicit account for maintenance */
	public void selectAccount(Account  pAccount) {
		/* Pass through to the Account control */
		theAccountTab.selectAccount(pAccount);
		
		/* Goto the Accounts tab */
		gotoNamedTab(titleAccounts);
	}
	
	/* Goto the specific tab */
	private void gotoNamedTab(String pTabName) {
		/* Access the Named index */
		int iIndex = theTabs.indexOfTab(pTabName);
	
		/* Select the required tab */
		theTabs.setSelectedIndex(iIndex);
	}
	
	/* Set visibility */
	protected void setVisibility() {
		int         iIndex;
		boolean     hasUpdates;
		
		/* Determine whether we have any updates */
		hasUpdates = hasUpdates();
		
		/* Access the Accounts index */
		iIndex = theTabs.indexOfTab(titleAccounts);
		
		/* Enable/Disable the Accounts tab */
		if (iIndex != -1) 
			theTabs.setEnabledAt(iIndex, !hasUpdates || theAccountTab.hasUpdates());
		
		/* Access the TaxYear index */
		iIndex = theTabs.indexOfTab(titleTaxYear);
		
		/* Enable/Disable the TaxYear tab */
		if (iIndex != -1) 
			theTabs.setEnabledAt(iIndex, !hasUpdates || theTaxYearTab.hasUpdates());
		
		/* Access the Properties panel */
		iIndex = theTabs.indexOfTab(titleProperties);
		
		/* Enable/Disable the Properties tab */
		if (iIndex != -1) 
			theTabs.setEnabledAt(iIndex, !hasUpdates || theProperties.hasUpdates());
		
		/* Access the PatternYear panel */
		iIndex = theTabs.indexOfTab(titlePattern);
		
		/* Enable/Disable the patternYear tab */
		if (iIndex != -1) 
			theTabs.setEnabledAt(iIndex, !hasUpdates);
		
		/* Access the Static panel */
		iIndex = theTabs.indexOfTab(titleStatic);
		
		/* Enable/Disable the static tab */
		if (iIndex != -1) 
			theTabs.setEnabledAt(iIndex, !hasUpdates  || theStatic.hasUpdates());
		
		/* Update the top level tabs */
		theParent.setVisibility();
	}
	
	/* Change listener */
	public void stateChanged(ChangeEvent e) {
		/* Ignore if it is not the tabs */
		if (e.getSource() != theTabs) return;
		
		/* Determine the focus */
		determineFocus();
	}

	/* Determine Focus */
	protected void determineFocus() {
		/* Access the selected component */
		Component myComponent = theTabs.getSelectedComponent();
		
		/* If the selected component is Accounts */
		if (myComponent == (Component)theAccountTab.getPanel()) {
			/* Set the debug focus */
			theAccountTab.getDebugEntry().setFocus();
		}
		
		/* If the selected component is TaxYear */
		else if (myComponent == (Component)theTaxYearTab.getPanel()) {
			/* Set the debug focus */
			theTaxYearTab.getDebugEntry().setFocus();			
		}

		/* If the selected component is NewYear */
		else if (myComponent == (Component)thePatternYear.getPanel()) {
			/* Set the debug focus */
			thePatternYear.getDebugEntry().setFocus();
			thePatternYear.requestFocusInWindow();
		}

		/* If the selected component is Static */
		else if (myComponent == (Component)theStatic.getPanel()) {
			/* Set the debug focus */
			theStatic.getDebugEntry().setFocus();			
		}
	}
}
