package uk.co.tolcroft.models.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;

import javax.swing.JTextField;

import uk.co.tolcroft.models.Number.Dilution;
import uk.co.tolcroft.models.Number.Money;
import uk.co.tolcroft.models.Number.Price;
import uk.co.tolcroft.models.Number.Rate;
import uk.co.tolcroft.models.Number.Units;
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
	 * The Cached foreground colour
	 */
	private Color		theCache	= null;
	
	/**
	 * Value property
	 */
	public final static String valueName = "value";
	
	/**
	 * Get the Value
	 */
	public Object getValue() 	{ return theModel.getValue(); }
	
	/**
	 * Constructor
	 */
	public ValueField() { this(ValueClass.String); }

	/**
	 * Constructor
	 * @param pClass the value class
	 */
	public ValueField(ValueClass pClass) {
		/* Switch on requested class */
		switch (pClass) {
			/* Create appropriate model class */
			case String:	theModel = new StringModel(); 		break;
			case Money:		theModel = new MoneyModel(); 		break;
			case Rate:		theModel = new RateModel(); 		break;
			case Units:		theModel = new UnitsModel(); 		break;
			case Price:		theModel = new PriceModel(); 		break;
			case Dilution:	theModel = new DilutionModel(); 	break;
			case CharArray:	theModel = new CharArrayModel(); 	break;
		}
		
		/* Add Action Listener */
		addActionListener(new TextAction());
		
		/* Add Focus Listener */
		addFocusListener(new TextFocus());
	}

	/**
	 * Set Display Value 
	 * @param pValue the value to set
	 */
	protected void setDisplay(String pValue) {
		/* Set the display value */
		theModel.setDisplay(pValue);
		setText(pValue);
	}
	
	/**
	 * Set Value 
	 * @param pValue the value to set
	 */
	public void setValue(Object pValue) {
		/* Reject invalid objects */
		if (pValue != null) theModel.validateObject(pValue);
		
		/* Determine whether this is a new value */
		boolean bNew = theModel.isNewValue(pValue);
		
		/* Access old value */
		Object myOld = getValue();
		
		/* Store the new value */
		theModel.setValue(pValue);
		
		/* Determine value to display */
		if (theCache != null) setForeground(theCache);
		setText(theModel.getDisplay());
		
		/* Fire a Property change if required */
		if (bNew) firePropertyChange(valueName, myOld, pValue);
		
		/* Clear old character array values */
		if (myOld instanceof char[]) Arrays.fill((char[])myOld, (char)0);
	}
	
	/**
	 * finishEdit
	 */
	private void finishEdit() {
		Object myValue 		= null;
		
		/* Obtain the text value */
		String myEditText 	= getText();
		
		/* Trim the string */
		myEditText.trim();
		
		/* Convert empty string to null */
		if (myEditText.length() == 0) 
			myEditText = null;
		
		/* If we have a parse-able object */
		if (myEditText != null) {
			/* Parse the value into the correct object */
			myValue = theModel.parseValue(myEditText);
		
			/* If we have an invalid value */
			if (myValue == null) {
				/* If the object is invalid */
				setToolTipText("Invalid Value");
				setForeground(Color.red);
			
				/* Store as the edit value for the model */
				theModel.setEdit(new String(myEditText));
			
				/* Re-acquire the focus */
				requestFocusInWindow();
			
				/* Return to caller */
				return;
			}
		}
		
		/* Store value */
		theSelf.setToolTipText(null);
		setValue(myValue);
		
		/* Reset the cache */
		theCache = null;
	}
	
	/**
	 * startEdit
	 */
	private void startEdit() {
		/* Save the current color */
		theCache = getForeground();
		
		/* Show the edit text */
		setText(theModel.getEdit());
	}
	
	/**
	 * Handle loss of focus 
	 */
	private class TextFocus extends FocusAdapter {
        @Override
        public void focusGained(final FocusEvent e) { 
        	startEdit(); }

        @Override
        public void focusLost(final FocusEvent e) { 
        	finishEdit(); }
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
	
	/**
	 * The Data Model class
	 */
	public abstract static class DataModel {
		/**
		 * The value of the Data
		 */
		private Object		theValue 	= null;
		
		/**
		 * The display value for the text 
		 */
		private String		theDisplay	= null;
		
		/**
		 * The edit value for the text 
		 */
		private String		theEdit		= null;
		
		/**
		 * Get Value
		 * @return the value associated with the field
		 */
		public	Object		getValue() 		{ return theValue; }
		
		/**
		 * Get Display Value
		 * @return the string to be shown when the field is not being edited
		 */
		protected	String	getDisplay()	{ return theDisplay; }
		
		/**
		 * Get Edit Display String
		 * @return the string to be shown at the start of an edit session
		 */
		protected	String	getEdit() 		{ return theEdit; }
		
		/**
		 * Set Edit display string
		 * @param pValue the value
		 */
		protected 	void	setEdit(String pValue)	{ theEdit = pValue; }
		
		/**
		 * Set Standard display string
		 * @param pValue the value
		 */
		protected 	void	setDisplay(String pValue)	{ 
			if ((pValue.length() > 0) || (theDisplay != Renderer.theError))
				theDisplay = pValue;
		}
		
		/**
		 * Set Value
		 * @param pValue the value
		 */
		protected void setValue(Object pValue) { theValue = pValue; }
		
		/**
		 * Parse value 
		 * @param pValue the non-Null text to parse
		 * @return the parsed object (or Null if the string is invalid)
		 */
		protected abstract Object parseValue(String pValue);		

		/**
		 * Validate object type 
		 * @param pValue the non-Null object to check
		 * @throws IllegalArgumentException if invalid argument
		 */
		protected abstract void validateObject(Object pObject);

		/**
		 * Determine whether the new value is different from the existing value 
		 * @param pValue the new value
		 * @return true if the new value is different
		 */
		protected abstract boolean isNewValue(Object pObject);
	}
	
	/**
	 * The String Data Model class
	 */
	public static class StringModel extends DataModel {
		@Override
		public		String	getValue() 		{ return (String)super.getValue(); }
		
		@Override
		protected	String	getDisplay() 	{ 
			String s = getValue();
			return (s == null) ? "" : s;
		}
		
		@Override
		public void setValue(Object pValue) {
			/* Store the new value */
			super.setValue((pValue == null) ? null : new String((String)pValue));
			
			/* Set edit and display values */
			String s = getValue();
			if (s == null) s = "";
			setDisplay(s);
			setEdit(s);
		}
		
		@Override
		protected Object parseValue(String pValue) {
			/* Return success */
			return pValue;
		}
		
		@Override
		protected void validateObject(Object pValue) {
			/* Reject non-string */
			if (!(pValue instanceof String))
				throw new IllegalArgumentException();
		}
		
		@Override
		protected boolean isNewValue(Object pValue) {
			/* Determine whether the value has changed */
			return (Utils.differs(getValue(), (String)pValue).isDifferent());
		}
	}
	
	/**
	 * The Money Data Model class
	 */
	public static class MoneyModel extends DataModel {
		@Override
		public		Money	getValue() 		{ return (Money)super.getValue(); }
		
		@Override
		public void setValue(Object pValue) {
			Money myNew = (Money)pValue;
			
			/* Store the new value */
			super.setValue((pValue == null) ? null : new Money(myNew));
			
			/* Set new edit and display values */
			setDisplay((myNew == null) ? "" : myNew.format(true));
			setEdit((myNew == null) ? "" : myNew.format(false));
		}
		
		@Override
		protected Object parseValue(String pValue) {
			/* Return the parsed value */
			return Money.Parse(pValue);
		}
		
		@Override
		protected void validateObject(Object pValue) {
			/* Reject non-money */
			if (!(pValue instanceof Money))
				throw new IllegalArgumentException();
		}
		
		@Override
		protected boolean isNewValue(Object pValue) {
			/* Determine whether the value has changed */
			return (Money.differs(getValue(), (Money)pValue).isDifferent());
		}
	}
	
	/**
	 * The Rate Data Model class
	 */
	public static class RateModel extends DataModel {
		@Override
		public		Rate	getValue() 		{ return (Rate)super.getValue(); }
		
		@Override
		protected	String	getDisplay() 	{ 
			Rate s = getValue();
			return (s == null) ? "" : s.format(true);
		}
		
		@Override
		public void setValue(Object pValue) {
			Rate myNew = (Rate)pValue;
			
			/* Store the new value */
			super.setValue((pValue == null) ? null : new Rate(myNew));
			
			/* Set new edit and display values */
			setDisplay((myNew == null) ? "" : myNew.format(true));
			setEdit((myNew == null) ? "" : myNew.format(false));
		}
		
		@Override
		protected Object parseValue(String pValue) {
			/* Return the parsed value */
			return Rate.Parse(pValue);
		}
		
		@Override
		protected void validateObject(Object pValue) {
			/* Reject non-rate */
			if (!(pValue instanceof Rate))
				throw new IllegalArgumentException();
		}
		
		@Override
		protected boolean isNewValue(Object pValue) {
			/* Determine whether the value has changed */
			return (Rate.differs(getValue(), (Rate)pValue).isDifferent());
		}
	}
	
	/**
	 * The Units Data Model class
	 */
	public static class UnitsModel extends DataModel {
		@Override
		public		Units	getValue() 		{ return (Units)super.getValue(); }
		
		@Override
		protected	String	getDisplay() 	{ 
			Units s = getValue();
			return (s == null) ? "" : s.format(true);
		}
		
		@Override
		public void setValue(Object pValue) {
			Units myNew = (Units)pValue;
			
			/* Store the new value */
			super.setValue((pValue == null) ? null : new Units(myNew));
			
			/* Set new edit value */
			setEdit((myNew == null) ? "" : myNew.format(false));
		}
		
		@Override
		protected Object parseValue(String pValue) {
			/* Return the parsed value */
			return Units.Parse(pValue);
		}
		
		@Override
		protected void validateObject(Object pValue) {
			/* Reject non-rate */
			if (!(pValue instanceof Units))
				throw new IllegalArgumentException();
		}
		
		@Override
		protected boolean isNewValue(Object pValue) {
			/* Determine whether the value has changed */
			return (Units.differs(getValue(), (Units)pValue).isDifferent());
		}
	}
	
	/**
	 * The Price Data Model class
	 */
	public static class PriceModel extends DataModel {
		@Override
		public		Price	getValue() 		{ return (Price)super.getValue(); }
		
		@Override
		protected	String	getDisplay() 	{ 
			Price s = getValue();
			return (s == null) ? "" : s.format(true);
		}
		
		@Override
		public void setValue(Object pValue) {
			Price myNew = (Price)pValue;
			
			/* Store the new value */
			super.setValue((pValue == null) ? null : new Price(myNew));
			
			/* Set new edit value */
			setEdit((myNew == null) ? "" : myNew.format(false));
		}
		
		@Override
		protected Object parseValue(String pValue) {
			/* Return the parsed value */
			return Price.Parse(pValue);
		}
		
		@Override
		protected void validateObject(Object pValue) {
			/* Reject non-rate */
			if (!(pValue instanceof Price))
				throw new IllegalArgumentException();
		}
		
		@Override
		protected boolean isNewValue(Object pValue) {
			/* Determine whether the value has changed */
			return (Price.differs(getValue(), (Price)pValue).isDifferent());
		}
	}
	
	/**
	 * The Dilution Data Model class
	 */
	public static class DilutionModel extends DataModel {
		@Override
		public		Dilution	getValue() 		{ return (Dilution)super.getValue(); }
		
		@Override
		protected	String	getDisplay() 	{ 
			Dilution s = getValue();
			return (s == null) ? "" : s.format(true);
		}
		
		@Override
		public void setValue(Object pValue) {
			Dilution myNew = (Dilution)pValue;
			
			/* Store the new value */
			super.setValue((pValue == null) ? null : new Dilution(myNew));
			
			/* Set new edit value */
			setEdit((myNew == null) ? "" : myNew.format(false));
		}
		
		@Override
		protected Object parseValue(String pValue) {
			/* Return the parsed value */
			return Dilution.Parse(pValue);
		}
		
		@Override
		protected void validateObject(Object pValue) {
			/* Reject non-rate */
			if (!(pValue instanceof Dilution))
				throw new IllegalArgumentException();
		}
		
		@Override
		protected boolean isNewValue(Object pValue) {
			/* Determine whether the value has changed */
			return (Dilution.differs(getValue(), (Dilution)pValue).isDifferent());
		}
	}
	
	/**
	 * The Char Array Model class
	 */
	public static class CharArrayModel extends DataModel {
		@Override
		public		char[]	getValue() 		{ return (char[])super.getValue(); }
		
		@Override
		protected	String	getDisplay() 	{ 
			char[] s = getValue();
			if (s == null) return "";
			char[] myMask = new char[s.length];
			Arrays.fill(myMask, '*');
			return new String(myMask);
		}
		
		@Override
		public void setValue(Object pValue) {
			char[] myNew = (char[])pValue;
			
			/* Store the new value */
			super.setValue((pValue == null) ? null : Arrays.copyOf(myNew, myNew.length));
			
			/* Set new edit value */
			setEdit((myNew == null) ? "" : new String(myNew));
		}
		
		@Override
		protected Object parseValue(String pValue) {
			/* Return the parsed value */
			return pValue.toCharArray();
		}
		
		@Override
		protected void validateObject(Object pValue) {
			/* Reject non-rate */
			if (!(pValue instanceof char[]))
				throw new IllegalArgumentException();
		}
		
		@Override
		protected boolean isNewValue(Object pValue) {
			/* Determine whether the value has changed */
			return (Utils.differs(getValue(), (char[])pValue).isDifferent());
		}
	}
	
	/**
	 * Classes of ValueField
	 */
	public enum ValueClass {
		String,
		Money,
		Rate,
		Units,
		Price,
		Dilution,
		CharArray;
	}
}
