package uk.co.tolcroft.models.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;

import net.sourceforge.JDateButton.JDateButton.CellEditor;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Decimal.*;

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
		private JComboBox 	theCombo;
		private ComboAction	theActionListener = new ComboAction();
		private ComboPopup	thePopupListener  = new ComboPopup();
	
		public JComponent getTableCellEditorComponent(JTable  table,
													  Object  value,
													  boolean isSelected,
													  int     row,
													  int     col) {
			DataTable<?> myTable = (DataTable<?>)table;
			theCombo = myTable.getComboBox(row, col);
			theCombo.setSelectedIndex(-1);
			if (value != null) theCombo.setSelectedItem((String)value);
			theCombo.addActionListener(theActionListener);
			theCombo.addPopupMenuListener(thePopupListener);
			return theCombo;
		}

		private class ComboAction implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				stopCellEditing();
			}
		}
		
		private class ComboPopup implements PopupMenuListener {
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
			public void popupMenuCanceled(PopupMenuEvent e) {
				cancelCellEditing();
			}
		}
		
		public Object getCellEditorValue() {
			String s = (String)theCombo.getSelectedItem();
			if ((s != null) && (s.equals(""))) s=null;
			return s;
		}
	
		public void cancelCellEditing() {
			theCombo.removePopupMenuListener(thePopupListener);
			theCombo.removeActionListener(theActionListener);
			super.cancelCellEditing();
		}
		
		public boolean stopCellEditing() {
			theCombo.removePopupMenuListener(thePopupListener);
			theCombo.removeActionListener(theActionListener);
			return super.stopCellEditing();
		}
	}
	
	/* Calendar Cell Editor */
	public static class CalendarCell extends CellEditor {
		private static final long serialVersionUID = -5463480186940634327L;
		private DateDay.Range   			theRange	= null;
		
		public  void setRange(DateDay.Range pRange) { theRange = pRange; }
		
		public JComponent getTableCellEditorComponent(JTable  table,
				                                      Object  value,
				                                      boolean isSelected,
				                                      int     row,
				                                      int     col) {
			/* Access the range */
			DateDay myStart = (theRange == null) ? null : theRange.getStart();
			DateDay myEnd   = (theRange == null) ? null : theRange.getEnd();
			DateDay myCurr;
			
			/* If the value is null */
			if ((value == null) || (value == Renderer.theError)) {
				myCurr = new DateDay(Calendar.getInstance().getTime());
			}
			else {
				myCurr = (DateDay)value;
				if (myCurr.isNull())
					myCurr = new DateDay(Calendar.getInstance().getTime());
			}
				
			/* Set up initial values and range */
			setSelectableRange((myStart == null) ? null : myStart.getDate(),
							   (myEnd == null)   ? null : myEnd.getDate());
			
			/* Pass onwards */
			return super.getTableCellEditorComponent(table, myCurr.getDate(), isSelected, row, col);
		}
		
		public Object getCellEditorValue() {
			DateDay myValue = new DateDay(getSelectedDate());
			return myValue;
		}
		
		public boolean stopCellEditing() {
			DateDay myDate = (DateDay)getCellEditorValue();
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