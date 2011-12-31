package uk.co.tolcroft.finance.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.data.EditState;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugManager.DebugEntry;
import uk.co.tolcroft.models.ui.StdInterfaces.stdCommand;
import uk.co.tolcroft.models.ui.StdInterfaces.stdPanel;
import uk.co.tolcroft.models.views.ViewList;

public class MaintStatic implements stdPanel,
									ItemListener {
	private MaintenanceTab			theParent		= null;
	private JPanel					thePanel		= null;
	private JPanel					theSelect		= null;
	private JComboBox				theSelectBox	= null;
	private MaintStaticData<?,?>	theActTypes		= null;
	private MaintStaticData<?,?>	theTranTypes	= null;
	private MaintStaticData<?,?>	theTaxTypes		= null;
	private MaintStaticData<?,?>	theTaxRegimes	= null;
	private MaintStaticData<?,?>	theFrequencys	= null;
	private MaintStaticData<?,?>	theInfoTypes	= null;
	private MaintStaticData<?,?>	theCurrent		= null;
	private ViewList				theViewSet		= null;
	private DebugEntry				theDebugData	= null;

	/* Get Top Window */
	protected JPanel	getPanel()		{ return thePanel; }
	protected MainTab	getTopWindow()	{ return theParent.getTopWindow(); }
	protected View		getView() 		{ return theParent.getView(); }
	protected ViewList	getViewSet() 	{ return theViewSet; }
	
	public MaintStatic(MaintenanceTab pParent) {
		/* Store parameters */
		theParent = pParent;
		
		/* Build the View set */
		theViewSet		= new ViewList(getView());

		/* Create the top level debug entry for this view  */
		View			myView		= getView();
		DebugManager 	myDebugMgr 	= myView.getDebugMgr();
		theDebugData = myDebugMgr.new DebugEntry("Static");
        theDebugData.addAsChildOf(pParent.getDebugEntry());

		/* Build the child windows */
		theActTypes 	= new MaintStaticData<AccountType.List, AccountType>(this, AccountType.List.class);
		theTranTypes 	= new MaintStaticData<TransactionType.List, TransactionType>(this, TransactionType.List.class);
		theTaxTypes 	= new MaintStaticData<TaxType.List, TaxType>(this, TaxType.List.class);
		theTaxRegimes 	= new MaintStaticData<TaxRegime.List, TaxRegime>(this, TaxRegime.List.class);
		theFrequencys	= new MaintStaticData<Frequency.List, Frequency>(this, Frequency.List.class);
		theInfoTypes	= new MaintStaticData<EventInfoType.List, EventInfoType>(this, EventInfoType.List.class);
		
		/* Build the Static box */
		theSelectBox = new JComboBox();
		theSelectBox.addItem(AccountType.listName);
		theSelectBox.addItem(TransactionType.listName);
		theSelectBox.addItem(TaxType.listName);
		theSelectBox.addItem(TaxRegime.listName);
		theSelectBox.addItem(Frequency.listName);
		theSelectBox.addItem(EventInfoType.listName);
		
		/* Add the listener for item changes */
		theSelectBox.addItemListener(this);
		
		/* Create the selection panel */
		theSelect = new JPanel();
		theSelect.setBorder(javax.swing.BorderFactory
							.createTitledBorder("Selection"));

		/* Create the layout for the panel */
	    GroupLayout panelLayout = new GroupLayout(theSelect);
	    theSelect.setLayout(panelLayout);
	    
	    /* Set the layout */
	    panelLayout.setHorizontalGroup(
	    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(panelLayout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(theSelectBox)
	                .addContainerGap())
	    );
	    panelLayout.setVerticalGroup(
	        panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                .addComponent(theSelectBox))
	    );

	    /* Create the full panel */
		thePanel = new JPanel();

		/* Create the layout for the panel */
	    panelLayout = new GroupLayout(thePanel);
	    thePanel.setLayout(panelLayout);
	    
	    /* Set the layout */
	    panelLayout.setHorizontalGroup(
	    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(panelLayout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
		                .addComponent(theSelect, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(theActTypes.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
	                    .addComponent(theTranTypes.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
	                    .addComponent(theTaxTypes.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
	                    .addComponent(theTaxRegimes.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
	                    .addComponent(theFrequencys.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
	                    .addComponent(theInfoTypes.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE))
	                .addContainerGap())
	    );
	    panelLayout.setVerticalGroup(
	        panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        		.addGroup(GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
	                .addComponent(theSelect)
	                .addComponent(theActTypes.getPanel())
	                .addComponent(theTranTypes.getPanel())
	                .addComponent(theTaxTypes.getPanel())
	                .addComponent(theTaxRegimes.getPanel())
	                .addComponent(theFrequencys.getPanel())
	                .addComponent(theInfoTypes.getPanel())
	                .addContainerGap())
	    );
	    
	    /* Select correct box */
		theSelectBox.setSelectedItem(AccountType.listName);
		setSelection(theActTypes);
	}

	/* Determine Focus */
	protected void determineFocus() {
		/* Set the required focus */
		theCurrent.getDebugEntry().setFocus();
	}
	
	/**
	 * Set Selection
	 * @param pClass the class that is selected
	 */
	private void setSelection(MaintStaticData<?,?> pClass) {
		/* Record the current class and set debug focus */
		theCurrent = pClass;
		determineFocus();
		
		/* Enable/Disable view */
		theActTypes.getPanel().setVisible(pClass == theActTypes);
		theTranTypes.getPanel().setVisible(pClass == theTranTypes);
		theTaxTypes.getPanel().setVisible(pClass == theTaxTypes);
		theTaxRegimes.getPanel().setVisible(pClass == theTaxRegimes);
		theFrequencys.getPanel().setVisible(pClass == theFrequencys);
		theInfoTypes.getPanel().setVisible(pClass == theInfoTypes);
	}
	
	/* ItemStateChanged listener event */
	public void itemStateChanged(ItemEvent evt) {
		String                myName;

		/* If this event relates to the Select box */
		if (evt.getSource() == (Object)theSelectBox) {
			myName = (String)evt.getItem();
			if (evt.getStateChange() == ItemEvent.SELECTED) {
				/* Determine the new table */
				if (myName == AccountType.listName)	    		setSelection(theActTypes);
				else if (myName == TransactionType.listName)	setSelection(theTranTypes);
				else if (myName == TaxType.listName)			setSelection(theTaxTypes);
				else if (myName == TaxRegime.listName)			setSelection(theTaxRegimes);
				else if (myName == Frequency.listName)			setSelection(theFrequencys);
				else if (myName == EventInfoType.listName)		setSelection(theInfoTypes);
			}
		}
	}
	
	@Override
	public void notifySelection(Object o) {}

	@Override
	public boolean hasUpdates() {
		/* Return to caller */
		return theViewSet.hasUpdates();
	}

	/**
	 * Has this set of tables got errors
	 */
	public boolean hasErrors() {
		/* Return to caller */
		return theViewSet.hasErrors();
	}
		
	@Override
	public void printIt() {}

	@Override
	public boolean isLocked() {	return false; }

	@Override
	public void performCommand(stdCommand pCmd) {}

	@Override
	public EditState getEditState() {
		/* Return to caller */
		return theViewSet.getEditState();
	}

	@Override
	public DebugManager getDebugManager() { return theParent.getDebugManager(); }

	@Override
	public DebugEntry getDebugEntry() { return theDebugData; }

	@Override
	public void lockOnError(boolean isError) {}
	
	/**
	 * Refresh views/controls after a load/update of underlying data
	 */
	public void refreshData() throws ModelException {
		/* Refresh the underlying children */
		theActTypes.refreshData();
		theTranTypes.refreshData();
		theTaxTypes.refreshData();
		theTaxRegimes.refreshData();
		theFrequencys.refreshData();
		theInfoTypes.refreshData();
	}
	
	/**
	 * Set Visibility 
	 */
	protected void setVisibility() {
		/* Lock down Selection if required */
		theSelectBox.setEnabled(!hasUpdates());
		
		/* Pass call on to parent */
		theParent.setVisibility();
	}
}
