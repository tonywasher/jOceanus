package uk.co.tolcroft.models.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;

import uk.co.tolcroft.models.Utils;

public class ValueField extends JTextField {
	/* Serial Id */
	private static final long serialVersionUID = -1865387282196348594L;

	/**
	 * Self reference 
	 */
	private ValueField	theSelf		= this;
	
	/**
	 * The Data Model
	 */
	private DataModel	theModel	= null;
	
	/**
	 * Value property
	 */
	public final static String valueName = "value";
	
	/**
	 * Get the DataModel
	 */
	public DataModel getModel() 	{ return theModel; }
	
	/**
	 * Constructor
	 */
	public ValueField() {
		/* Create the model */
		theModel = new DataModel();
		
		/* Add Action Listener */
		addActionListener(new TextAction());
		
		/* Add Focus Listener */
		addFocusListener(new TextFocus());
	}

	/**
	 * The Data Model class
	 */
	public class DataModel {
		/**
		 * The value of the Data
		 */
		private String 		theValue 	= null;
		
		/**
		 * Get Value
		 */
		public	String		getValue() 	{ return theValue; }
		
		/**
		 * Set Value
		 * @param pValue the value
		 */
		public void setValue(String pValue) {
			/* Set the value */
			theValue = pValue;
			
			/* Determine value to display */
			String s = (pValue == null) ? "" : pValue;
			
			/* Set text value */
			setText(s);
		}
	}
	
	/**
	 * finishEdit
	 */
	private void finishEdit() {
		/* Obtain the text value */
		String s = getText();
		
		/* Determine whether the value has changed */
		if (Utils.differs(s, theModel.theValue).isDifferent()) {
			/* Fire a Property change */
			firePropertyChange(valueName, theModel.theValue, s);

			/* Store the Model value */
			theModel.theValue = s;
		}
	}
	
	/**
	 * Handle loss of focus 
	 */
	private class TextFocus extends FocusAdapter {
        @Override
        public void focusGained(final FocusEvent e) { }

        @Override
        public void focusLost(final FocusEvent e) { finishEdit();	}
	}	

	/**
	 * Handle actions 
	 */
	private class TextAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			/* If this relates to the value */
			if (e.getSource() == theSelf) {
				/* Check for finish of edit */
				finishEdit();
			}
		}
	}	
}
