package uk.co.tolcroft.models.ui;

import java.util.Calendar;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.table.TableCellEditor;

import uk.co.tolcroft.finance.ui.controls.FinanceTable;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Number.*;

public class Editor {
	/* String Cell Editor */
	public static class StringCell extends AbstractCellEditor
                               implements TableCellEditor {
		private static final long serialVersionUID = 2172483058466364800L;
		private JTextField theField;
	
		public StringCell() {
			theField = new JTextField();
		}
	
		public JComponent getTableCellEditorComponent(JTable  table,
													  Object  value,
													  boolean isSelected,
													  int     row,
													  int     col) {
			theField.setText(((value == null) || (value == Renderer.theError))
							 ? "" : (String)value);
			return theField;
		}
	
		public Object getCellEditorValue() {
			String s = theField.getText();
			if (!s.equals("")) {
				return s;
			}
			return null;
		}
	
		public boolean stopCellEditing() {
			String s = (String)getCellEditorValue();
			if (s == null) {
				fireEditingCanceled();
				return false;
			}
			return super.stopCellEditing();
		}
	}
	
	/* Integer Cell Editor */
	public static class IntegerCell extends AbstractCellEditor
                                    implements TableCellEditor {
		private static final long serialVersionUID = 2172483058466364800L;
		private JTextField theField;
	
		public IntegerCell() {
			theField = new JTextField();
		}
	
		public JComponent getTableCellEditorComponent(JTable  table,
													  Object  value,
													  boolean isSelected,
													  int     row,
													  int     col) {
			theField.setText(((value == null) || (value == Renderer.theError))
							 ? "" : Integer.toString((Integer)value));
			return theField;
		}
	
		public Object getCellEditorValue() {
			String s = theField.getText();
			if (!s.equals("")) {
				try {
					Integer myInt = new Integer(s);
					return myInt;
				}
				catch (Throwable e) {
					return null;
				}
			}
			return null;
		}
	
		public boolean stopCellEditing() {
			String s = (String)getCellEditorValue();
			if (s == null) {
				fireEditingCanceled();
				return false;
			}
			return super.stopCellEditing();
		}
	}

	/* String Cell Editor */
	public static class ComboBoxCell extends AbstractCellEditor
                                     implements TableCellEditor {
		private static final long serialVersionUID = 6107290027015360230L;
		private JComboBox theCombo;
	
		public JComponent getTableCellEditorComponent(JTable  table,
													  Object  value,
													  boolean isSelected,
													  int     row,
													  int     col) {
			FinanceTable<?> myTable = (FinanceTable<?>)table;
			theCombo = myTable.getComboBox(row, col);
			theCombo.setSelectedItem((java.lang.String)value);
			return theCombo;
		}
	
		public Object getCellEditorValue() {
			String s = (String)theCombo.getSelectedItem();
			if ((s != null) && (!s.equals(""))) {
				return s;
			}
			return null;
		}
	
		public boolean stopCellEditing() {
			String s = (String)getCellEditorValue();
			if (s == null) {
				fireEditingCanceled();
				return false;
			}
			return super.stopCellEditing();
		}
	}
	
	/* Date Cell Editor */
	public static class DateCell extends AbstractCellEditor
	                             implements TableCellEditor {
		private static final long 	serialVersionUID = -6886642204116115360L;
		private SpinnerDateModel  	theModel       = null;
		private JSpinner          	theSpinner     = null;
		private Date.Range   		theRange		 = null;
		
		public  void setRange(Date.Range pRange) { theRange = pRange; }
		
		public DateCell() {
			theModel   = new SpinnerDateModel();
			theSpinner = new JSpinner(theModel);
			theSpinner.setEditor(new JSpinner.DateEditor(theSpinner, "dd-MMM-yyyy"));
		}

		public void setNoYear() {
			java.util.Calendar 	myDate;
			Date 				myStart;
			Date 				myEnd;
			
			/* Create the start and end dates */
			myDate = Calendar.getInstance();
			myDate.set(1999, Calendar.APRIL, 6, 0, 0, 0);
			myStart = new Date(myDate.getTime());
			myDate.set(2000, Calendar.APRIL, 5, 0, 0, 0);
			myEnd = new Date(myDate.getTime());

			/* Create the range */
			theRange = new Date.Range(myStart, myEnd);
		}
		
		public JComponent getTableCellEditorComponent(JTable  table,
				                                      Object  value,
				                                      boolean isSelected,
				                                      int     row,
				                                      int     col) {
			/* Access the range */
			Date myStart = theRange.getStart();
			Date myEnd   = theRange.getEnd();
			Date myCurr  = null;
			
			/* Start needs to be set back one day */
			if (myStart != null) {
				myStart = new Date(myStart);
				myStart.adjustDay(-1);
			}
			
			/* If the value is null */
			if ((value == null) || (value == Renderer.theError)) {
				myCurr = new Date(Calendar.getInstance().getTime());
			}
			else {
				myCurr = (Date)value;
				if (myCurr.isNull())
					myCurr = new Date(Calendar.getInstance().getTime());
			}
				
			if (theRange.compareTo(myCurr) != 0) {
				if (myEnd != null) myCurr = myEnd;
				else myCurr = myStart;
			}
			
			/* Set up spinner values */
			theModel.setStart((myStart == null) ? null : myStart.getDate());
			theModel.setEnd((myEnd == null) ? null : myEnd.getDate());
			theModel.setValue(myCurr.getDate());
			return theSpinner;
		}
		
		public Object getCellEditorValue() {
			Date myValue = new Date(theModel.getDate());
			return myValue;
		}
		
		public boolean stopCellEditing() {
			Date myDate = (Date)getCellEditorValue();
			if ((Object)myDate == null) {
				fireEditingCanceled();
				return false;
			}
			return super.stopCellEditing();
		}
	}
	
	/* Rates Cell Editor */
	public static class RateCell extends AbstractCellEditor
	                             implements TableCellEditor {
		private static final long serialVersionUID = 2636603780411978911L;
		private JTextField theField;
		
		public RateCell() {
			theField = new JTextField();
		}
		
		public JComponent getTableCellEditorComponent(JTable  table,
				                                      Object  value,
				                                      boolean isSelected,
				                                      int     row,
				                                      int     col) {
			theField.setText(((value == null) || (value == Renderer.theError)) 
								? "" : ((Rate)value).format(false));
			return theField;
		}
		
		public Object getCellEditorValue() {
			String s = theField.getText();
			if (!s.equals("")) {
				try {
					Rate myRate = new Rate(s);
					return myRate;
				}
				catch (Throwable e) {
					return null;
				}
			}
			return null;
		}
		
		public boolean stopCellEditing() {
			Rate myRate = (Rate)getCellEditorValue();
			if (myRate == null) {
				fireEditingCanceled();
				return false;
			}
			return super.stopCellEditing();
		}
	}
	
	/* Money Cell Editor */
	public static class MoneyCell extends AbstractCellEditor
	                              implements TableCellEditor {
		private static final long serialVersionUID = 2748644075720076417L;
		private JTextField theField;
		
		public MoneyCell() {
			theField = new JTextField();
		}
		
		public JComponent getTableCellEditorComponent(JTable  table,
				                                      Object  value,
				                                      boolean isSelected,
				                                      int     row,
				                                      int     col) {
			theField.setText(((value == null) || (value == Renderer.theError))
								? "" : ((Money)value).format(false));
			return theField;
		}
		
		public Object getCellEditorValue() {
			String s = theField.getText();
			if (!s.equals("")) {
				try {
					Money myMoney = new Money(s);
					return myMoney;
				}
				catch (Throwable e) {
					return null;
				}
			}
			return null;
		}
		
		public boolean stopCellEditing() {
			Money myMoney = (Money)getCellEditorValue();
			if (myMoney == null) {
				fireEditingCanceled();
				return false;
			}
			return super.stopCellEditing();
		}
	}

	/* Units Cell Editor */
	public static class UnitCell extends AbstractCellEditor
	                             implements TableCellEditor {
		private static final long serialVersionUID = -5924761972037405523L;
		private JTextField theField;
		
		public UnitCell() {
			theField = new JTextField();
		}
		
		public JComponent getTableCellEditorComponent(JTable  table,
				                                      Object  value,
				                                      boolean isSelected,
				                                      int     row,
				                                      int     col) {
			theField.setText(((value == null) || (value == Renderer.theError))
									? "" : ((Units)value).format(false));
			return theField;
		}
		
		public Object getCellEditorValue() {
			String s = theField.getText();
			if (!s.equals("")) {
				try {
					Units myUnits = new Units(s);
					return myUnits;
				}
				catch (Throwable e) {
					return this;
				}
			}
			return null;
		}
		
		public boolean stopCellEditing() {
			Units myUnits = (Units)getCellEditorValue();
			if ((Object)myUnits == this) {
				fireEditingCanceled();
				return false;
			}
			return super.stopCellEditing();
		}
	}

	/* Dilutions Cell Editor */
	public static class DilutionCell extends AbstractCellEditor
	                                 implements TableCellEditor {
		private static final long serialVersionUID = -4764410922782962134L;
		private JTextField theField;
		
		public DilutionCell() {
			theField = new JTextField();
		}
		
		public JComponent getTableCellEditorComponent(JTable  table,
				                                      Object  value,
				                                      boolean isSelected,
				                                      int     row,
				                                      int     col) {
			theField.setText(((value == null) || (value == Renderer.theError))
									? "" : ((Dilution)value).format(false));
			return theField;
		}
		
		public Object getCellEditorValue() {
			String s = theField.getText();
			if (!s.equals("")) {
				try {
					Dilution myDilution = new Dilution(s);
					return myDilution;
				}
				catch (Throwable e) {
					return this;
				}
			}
			return null;
		}
		
		public boolean stopCellEditing() {
			Units myUnits = (Units)getCellEditorValue();
			if ((Object)myUnits == this) {
				fireEditingCanceled();
				return false;
			}
			return super.stopCellEditing();
		}
	}

	/* Price Cell Editor */
	public static class PriceCell extends AbstractCellEditor
	                              implements TableCellEditor {
		private static final long serialVersionUID = 7215554993415708775L;
		private JTextField theField;
		
		public PriceCell() {
			theField = new JTextField();
		}
		
		public JComponent getTableCellEditorComponent(JTable  table,
				                                      Object  value,
				                                      boolean isSelected,
				                                      int     row,
				                                      int     col) {
			theField.setText(((value == null) || (value == Renderer.theError))
								? "" : ((Price)value).format(false));
			return theField;
		}
		
		public Object getCellEditorValue() {
			String s = theField.getText();
			if (!s.equals("")) {
				try {
					Price myPrice = new Price(s);
					return myPrice;
				}
				catch (Throwable e) {
					return null;
				}
			}
			return null;
		}
		
		public boolean stopCellEditing() {
			Price myPrice = (Price)getCellEditorValue();
			if (myPrice == null) {
				fireEditingCanceled();
				return false;
			}
			return super.stopCellEditing();
		}
	}

	/* DilutedPrice Cell Editor */
	public static class DilutedPriceCell extends AbstractCellEditor
	                                     implements TableCellEditor {
		private static final long serialVersionUID = 3930787232807465136L;
		private JTextField theField;
		
		public DilutedPriceCell() {
			theField = new JTextField();
		}
		
		public JComponent getTableCellEditorComponent(JTable  table,
				                                      Object  value,
				                                      boolean isSelected,
				                                      int     row,
				                                      int     col) {
			theField.setText(((value == null) || (value == Renderer.theError))
								? "" : ((DilutedPrice)value).format(false));
			return theField;
		}
		
		public Object getCellEditorValue() {
			String s = theField.getText();
			if (!s.equals("")) {
				try {
					DilutedPrice myPrice = new DilutedPrice(s);
					return myPrice;
				}
				catch (Throwable e) {
					return null;
				}
			}
			return null;
		}
		
		public boolean stopCellEditing() {
			Price myPrice = (Price)getCellEditorValue();
			if (myPrice == null) {
				fireEditingCanceled();
				return false;
			}
			return super.stopCellEditing();
		}
	}
}
