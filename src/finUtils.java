package finance;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.RepaintManager;
import java.awt.print.*;
import java.util.Calendar;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.LayoutStyle;
import javax.swing.GroupLayout;
import javax.swing.Timer;

import finance.finSwing.financeTable;
import finance.finSwing.financeView;
import finance.finSwing.financePanel;
import finance.finObject.EditState;

public class finUtils {
	
	/* Properties */
	private static String theError = "Error";
	
	/* Access methods */
	public static String getError() { return theError; }
	
	/* String utilities */
	public static class StringUtil {
		/* String Cell Editor */
		public static class Editor extends AbstractCellEditor
	                               implements TableCellEditor {
			private static final long serialVersionUID = 2172483058466364800L;
			private JTextField theField;
		
			public Editor() {
				theField = new JTextField();
			}
		
			public JComponent getTableCellEditorComponent(JTable  table,
														  Object  value,
														  boolean isSelected,
														  int     row,
														  int     col) {
				theField.setText((java.lang.String)value);
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
	
		/* String Cell Renderer */
		public static class Renderer extends DefaultTableCellRenderer {

			private static final long serialVersionUID = -2004841981078780283L;
			private RenderData theData = null;
			
			public Renderer() {
				super();
				theData = new RenderData(false);
			}

			public void setValue(Object value) {
				String s;
				
				if (value == theError)  s = theError;
 				else if (value == null) s = "";
				else                    s = (String)value;
				
				super.setValue(s);
			}
		
			public JComponent getTableCellRendererComponent(JTable table, 
															Object value,
		                                                	boolean isSelected, 
		                                                	boolean hasFocus, 
		                                                	int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected,
		                                        	hasFocus, row, column);
				financeView myTable = (financeView)table;
				theData.setPosition(row, column, isSelected);
				myTable.getRenderData(theData);
				setForeground(theData.getForeGround());
				setBackground(theData.getBackGround());
				setFont(theData.getFont());
				setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
				setToolTipText(theData.getToolTip());
				return this;
			}
		}
	}	
	
	/* Integer utilities */
	public static class IntegerUtil {
		/* Integer Cell Editor */
		public static class Editor extends AbstractCellEditor
	                               implements TableCellEditor {
			private static final long serialVersionUID = 2172483058466364800L;
			private JTextField theField;
		
			public Editor() {
				theField = new JTextField();
			}
		
			public JComponent getTableCellEditorComponent(JTable  table,
														  Object  value,
														  boolean isSelected,
														  int     row,
														  int     col) {
				theField.setText(Integer.toString((Integer)value));
				return theField;
			}
		
			public Object getCellEditorValue() {
				String s = theField.getText();
				if (!s.equals("")) {
					try {
						Integer myInt = new Integer(s);
						return myInt;
					}
					catch (Exception e) {
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
	
		/* Integer Cell Renderer */
		public static class Renderer extends DefaultTableCellRenderer {

			private static final long serialVersionUID = -2004841981078780283L;
			private RenderData theData = null;
			
			public Renderer() {
				super();
				theData = new RenderData(false);
			}

			public void setValue(Object value) {
				String s;
				
				if (value == theError)  s = theError;
 				else if (value == null) s = "";
				else
				{
					Integer i = (Integer)value;
					if (i == 0) s = "";
					else 		s = i.toString();
				}
				
				super.setValue(s);
			}
		
			public JComponent getTableCellRendererComponent(JTable table, 
															Object value,
		                                                	boolean isSelected, 
		                                                	boolean hasFocus, 
		                                                	int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected,
		                                        	hasFocus, row, column);
				financeView myTable = (financeView)table;
				theData.setPosition(row, column, isSelected);
				myTable.getRenderData(theData);
				setForeground(theData.getForeGround());
				setBackground(theData.getBackGround());
				setFont(theData.getFont());
				setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
				setToolTipText(theData.getToolTip());
				return this;
			}
		}
	}	
	
	/* ComboBox utilities */
	public static class ComboUtil {
		/* String Cell Editor */
		public static class Editor extends AbstractCellEditor
	                               implements TableCellEditor {
			private static final long serialVersionUID = 6107290027015360230L;
			private JComboBox theCombo;
		
			public JComponent getTableCellEditorComponent(JTable  table,
														  Object  value,
														  boolean isSelected,
														  int     row,
														  int     col) {
				financeTable myTable = (financeTable)table;
				theCombo = myTable.getComboBox(row, col);
				theCombo.setSelectedItem((java.lang.String)value);
				return theCombo;
			}
		
			public Object getCellEditorValue() {
				String s = (String)theCombo.getSelectedItem();
				if (!s.equals("")) {
					return s;
				}
				return null;
			}
		
			public boolean stopCellEditing() {
				String s = (java.lang.String)getCellEditorValue();
				if (s == null) {
					fireEditingCanceled();
					return false;
				}
				return super.stopCellEditing();
			}
		}
	}	
	
	/* Date utilities */
	public static class DateUtil {
		/* Date Cell Editor */
		public static class Editor extends AbstractCellEditor
		                           implements TableCellEditor {
			private static final long serialVersionUID = -6886642204116115360L;
			private SpinnerDateModel  theModel       = null;
			private JSpinner          theSpinner     = null;
			private finObject.Range   theRange		 = null;
			
			public  void setRange(finObject.Range pRange) { theRange = pRange; }
			
			public Editor() {
				theModel   = new SpinnerDateModel();
				theSpinner = new JSpinner(theModel);
				theSpinner.setEditor(new JSpinner.DateEditor(theSpinner, "dd-MMM-yyyy"));
			}

			public void setNoYear() {
				java.util.Calendar myDate;
				finObject.Date myStart;
				finObject.Date myEnd;
				
				/* Create the start and end dates */
				myDate = Calendar.getInstance();
				myDate.set(1999, Calendar.APRIL, 6, 0, 0, 0);
				myStart = new finObject.Date(myDate.getTime());
				myDate.set(2000, Calendar.APRIL, 5, 0, 0, 0);
				myEnd = new finObject.Date(myDate.getTime());

				/* Create the range */
				theRange = new finObject.Range(myStart, myEnd);
			}
			
			public JComponent getTableCellEditorComponent(JTable  table,
					                                      Object  value,
					                                      boolean isSelected,
					                                      int     row,
					                                      int     col) {
				/* Access the range */
				finObject.Date myStart = theRange.getStart();
				finObject.Date myEnd   = theRange.getEnd();
				finObject.Date myCurr  = null;
				
				/* Start needs to be set back one day */
				if (myStart != null) {
					myStart = new finObject.Date(myStart);
					myStart.adjustDay(-1);
				}
				
				/* If the value is null */
				if ((value == null) || (value == theError)) {
					myCurr = new finObject.Date(Calendar.getInstance().getTime());
				}
				else {
					myCurr = (finObject.Date)value;
					if (myCurr.isNull())
						myCurr = new finObject.Date(Calendar.getInstance().getTime());
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
				finObject.Date myValue = new finObject.Date(theModel.getDate());
				return myValue;
			}
			
			public boolean stopCellEditing() {
				finObject.Date myDate = (finObject.Date)getCellEditorValue();
				if ((Object)myDate == null) {
					fireEditingCanceled();
					return false;
				}
				return super.stopCellEditing();
			}
		}
		
		/* Date Cell Renderer */
		public static class Renderer extends DefaultTableCellRenderer {
			private static final long serialVersionUID = -7384071058221166614L;
			private RenderData theData = null;
			
			public Renderer() {
				super();
				theData = new RenderData(true);
			}

			public void setValue(Object value) {
				finObject.Date myDate;
				String         s;
				
				if (value == theError)  s = theError;
				else if (value == null) s = "";
				else {
					myDate = (finObject.Date)value;
					s      = myDate.formatDate(false);
				}
				
				super.setValue(s);
			}
			public JComponent getTableCellRendererComponent(JTable table, Object value,
															boolean isSelected, 
															boolean hasFocus, 
															int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected,
													hasFocus, row, column);
				financeView myTable = (financeView)table;
				theData.setPosition(row, column, isSelected);
				myTable.getRenderData(theData);
				setForeground(theData.getForeGround());
				setBackground(theData.getBackGround());
				setFont(theData.getFont());
				setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			    setToolTipText(theData.getToolTip());
				return this;
			}
		}
	}
	
	/* Rate utilities */
	public static class RateUtil {
		/* Rates Cell Editor */
		public static class Editor extends AbstractCellEditor
		                           implements TableCellEditor {
			private static final long serialVersionUID = 2636603780411978911L;
			private JTextField theField;
			
			public Editor() {
				theField = new JTextField();
			}
			
			public JComponent getTableCellEditorComponent(JTable  table,
					                                      Object  value,
					                                      boolean isSelected,
					                                      int     row,
					                                      int     col) {
				theField.setText(((value == null) || (value == theError)) 
									? "" : ((finObject.Rate)value).format(false));
				return theField;
			}
			
			public Object getCellEditorValue() {
				String s = theField.getText();
				if (!s.equals("")) {
					try {
						finObject.Rate myRate = new finObject.Rate(s);
						return myRate;
					}
					catch (finObject.Exception e) {
						return null;
					}
				}
				return null;
			}
			
			public boolean stopCellEditing() {
				finObject.Rate myRate = (finObject.Rate)getCellEditorValue();
				if (myRate == null) {
					fireEditingCanceled();
					return false;
				}
				return super.stopCellEditing();
			}
		}
		
		/* Rate Cell Renderer */
		public static class Renderer extends DefaultTableCellRenderer {
			private static final long serialVersionUID = 6571410292897989673L;
			private RenderData theData = null;
			
			public Renderer() {
				super();
				theData = new RenderData(true);
			}

			public void setValue(Object value) {
				finObject.Rate myRate;
				String         s;
				
				if (value == theError)  s = theError;
				else if (value == null) s = "";
				else {
					myRate = (finObject.Rate)value;
					s      = myRate.format(true);
				}
				
				super.setValue(s);
			}
			public JComponent getTableCellRendererComponent(JTable table, Object value,
															boolean isSelected, 
															boolean hasFocus, 
															int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected,
													hasFocus, row, column);
				financeView myTable = (financeView)table;
				theData.setPosition(row, column, isSelected);
				myTable.getRenderData(theData);
				setForeground(theData.getForeGround());
				setBackground(theData.getBackGround());
				setFont(theData.getFont());
				setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			    setToolTipText(theData.getToolTip());
				return this;
			}
		}			
	}
	
	/* Money utilities */
	public static class MoneyUtil {
		/* Money Cell Editor */
		public static class Editor extends AbstractCellEditor
		                           implements TableCellEditor {
			private static final long serialVersionUID = 2748644075720076417L;
			private JTextField theField;
			
			public Editor() {
				theField = new JTextField();
			}
			
			public JComponent getTableCellEditorComponent(JTable  table,
					                                      Object  value,
					                                      boolean isSelected,
					                                      int     row,
					                                      int     col) {
				theField.setText(((value == null) || (value == theError))
									? "" : ((finObject.Money)value).format(false));
				return theField;
			}
			
			public Object getCellEditorValue() {
				String s = theField.getText();
				if (!s.equals("")) {
					try {
						finObject.Money myMoney = new finObject.Money(s);
						return myMoney;
					}
					catch (finObject.Exception e) {
						return null;
					}
				}
				return null;
			}
			
			public boolean stopCellEditing() {
				finObject.Money myMoney = (finObject.Money)getCellEditorValue();
				if (myMoney == null) {
					fireEditingCanceled();
					return false;
				}
				return super.stopCellEditing();
			}
		}
		
		/* Money Cell Renderer */
		public static class Renderer extends DefaultTableCellRenderer {
			private static final long serialVersionUID = 5222695627810930911L;
			private RenderData theData = null;
			
			public Renderer() {
				super();
				theData = new RenderData(true);
			}

			public void setValue(Object value) {
				finObject.Money myMoney;
				String          s;
				
				if (value == theError)  s = theError;
				else if (value == null) s = "";
				else {
					myMoney = (finObject.Money)value;
					s       = myMoney.format(true);
				}
				
				super.setValue(s);
			}
			public JComponent getTableCellRendererComponent(JTable table, Object value,
															boolean isSelected, 
															boolean hasFocus, 
															int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected,
													hasFocus, row, column);
				financeView myTable = (financeView)table;
				theData.setPosition(row, column, isSelected);
				myTable.getRenderData(theData);
				setForeground(theData.getForeGround());
				setBackground(theData.getBackGround());
				setFont(theData.getFont());
				setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			    setToolTipText(theData.getToolTip());
				return this;
			}
		}
	}
	
	/* Units utilities */
	public static class UnitsUtil {
		/* Units Cell Editor */
		public static class Editor extends AbstractCellEditor
		                           implements TableCellEditor {
			private static final long serialVersionUID = -5924761972037405523L;
			private JTextField theField;
			
			public Editor() {
				theField = new JTextField();
			}
			
			public JComponent getTableCellEditorComponent(JTable  table,
					                                      Object  value,
					                                      boolean isSelected,
					                                      int     row,
					                                      int     col) {
				theField.setText(((value == null) || (value == theError))
										? "" : ((finObject.Units)value).format(false));
				return theField;
			}
			
			public Object getCellEditorValue() {
				String s = theField.getText();
				if (!s.equals("")) {
					try {
						finObject.Units myUnits = new finObject.Units(s);
						return myUnits;
					}
					catch (finObject.Exception e) {
						return this;
					}
				}
				return null;
			}
			
			public boolean stopCellEditing() {
				finObject.Units myUnits = (finObject.Units)getCellEditorValue();
				if ((Object)myUnits == this) {
					fireEditingCanceled();
					return false;
				}
				return super.stopCellEditing();
			}
		}
		
		/* Units Cell Renderer */
		public static class Renderer extends DefaultTableCellRenderer {
			private static final long serialVersionUID = -4097886091647869702L;
			private RenderData theData = null;
			
			public Renderer() {
				super();
				theData = new RenderData(true);
			}

			public void setValue(Object value) {
				finObject.Units myUnits;
				String          s;
				
				if (value == theError)  s = theError;
				else if (value == null) s = "";
				else {
					myUnits = (finObject.Units)value;
					s       = myUnits.format(true);
				}
				
				super.setValue(s);
			}
			public JComponent getTableCellRendererComponent(JTable table, Object value,
															boolean isSelected, 
															boolean hasFocus, 
															int row, int column) {	
				super.getTableCellRendererComponent(table, value, isSelected,
													hasFocus, row, column);
				financeView myTable = (financeView)table;
				theData.setPosition(row, column, isSelected);
				myTable.getRenderData(theData);
				setForeground(theData.getForeGround());
				setBackground(theData.getBackGround());
				setFont(theData.getFont());
				setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			    setToolTipText(theData.getToolTip());
				return this;
			}
		}			
	}
	
	/* Price utilities */
	public static class PriceUtil {
		/* Price Cell Editor */
		public static class Editor extends AbstractCellEditor
		                           implements TableCellEditor {
			private static final long serialVersionUID = 7215554993415708775L;
			private JTextField theField;
			
			public Editor() {
				theField = new JTextField();
			}
			
			public JComponent getTableCellEditorComponent(JTable  table,
					                                      Object  value,
					                                      boolean isSelected,
					                                      int     row,
					                                      int     col) {
				theField.setText(((value == null) || (value == theError))
									? "" : ((finObject.Price)value).format(false));
				return theField;
			}
			
			public Object getCellEditorValue() {
				String s = theField.getText();
				if (!s.equals("")) {
					try {
						finObject.Price myPrice = new finObject.Price(s);
						return myPrice;
					}
					catch (finObject.Exception e) {
						return null;
					}
				}
				return null;
			}
			
			public boolean stopCellEditing() {
				finObject.Price myPrice = (finObject.Price)getCellEditorValue();
				if (myPrice == null) {
					fireEditingCanceled();
					return false;
				}
				return super.stopCellEditing();
			}
		}
		
		/* Price Cell Renderer */
		public static class Renderer extends DefaultTableCellRenderer {
			private static final long serialVersionUID = -2085384960415300006L;
			private RenderData theData = null;
			
			public Renderer() {
				super();
				theData = new RenderData(true);
			}

			public void setValue(Object value) {
				finObject.Price myPrice;
				String s;
				
				if (value == theError)  s = theError;
				else if (value == null) s = "";
				else {
					myPrice = (finObject.Price)value;
					s       = myPrice.format(true);
				}
				
				super.setValue(s);
			}
			
			public JComponent getTableCellRendererComponent(JTable table, Object value,
			                                                boolean isSelected, 
			                                                boolean hasFocus, 
			                                                int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected,
			                                        hasFocus, row, column);
				financeView myTable = (financeView)table;
				theData.setPosition(row, column, isSelected);
				myTable.getRenderData(theData);
				setForeground(theData.getForeGround());
				setBackground(theData.getBackGround());
				setFont(theData.getFont());
				setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			    setToolTipText(theData.getToolTip());
			    return this;
			}
		}			
	}

	/* Class to determine combo box selector */
	public static class ComboSelect {
		/* Members */
		private Item    theFirst = null;
		
		/* Constructor */
		public ComboSelect(finView pView) {
			finData				  myData;
			finData.Account       myAcct;
			finData.AccountList   myAccounts;
			finStatic.TransType   myTran;
			finStatic.AccountType myType;
			finStatic.AccountType myCurr;
			ComboSelect.Item      myActList;
			ComboSelect.Item      myTranList;
			boolean				  isFirst;
			
			/* Access the data */
			myData     = pView.getData();
			myAccounts = myData.getAccounts();
			myActList  = null;
			myCurr     = null;
			
			/* Add the TransType values to the types box */
			for (myTran  = myData.getTransTypes().getFirst();
			     myTran != null;
			     myTran  = myTran.getNext()) {
				/* Ignore market adjust */
				if (myTran.isMarketAdjust()) continue;
				
				/* Access the list for this transaction */
				myTranList = searchFor(myTran);
				
				/* Loop through the accounts */
				for (myAcct = myAccounts.getFirst();
				     myAcct != null;
				     myAcct = myAcct.getNext()) {					
					/* Ignore deleted/closed accounts */
					if ((myAcct.isDeleted()) || (myAcct.isClosed()))
						continue;
					
					/* Access the account type */
					myType  = myAcct.getActType();
					isFirst = false;
					
					/* If this is a new account type */
					if (myType != myCurr) {
						/* Record it and get the correct list */
						myCurr     = myType;
						myActList  = searchFor(myType);
						isFirst    = true;
					}

					/* If this is OK for a credit account */
					if (finData.isValidEvent(myTran, myType, true)) {
						
						/* Add it to the list */
						if (isFirst) myActList.addCredit(myTran.getName());
						myTranList.addCredit(myAcct.getName());
					}
							
					/* If this is OK for a debit account */
					if (finData.isValidEvent(myTran, myType, false)) {
						/* Add it to the list */
						if (isFirst) myActList.addDebit(myTran.getName());
						myTranList.addDebit(myAcct.getName());
					}
				}	
			}
		}
		
		/**
		 * Search for the Item for this TransType
		 * @param pTransType - the TransType
		 * @return theItem
		 */
		public Item searchFor(finStatic.TransType pTransType) {
			Item myCurr;
			for (myCurr = theFirst;
			     myCurr != null;
			     myCurr = myCurr.theNext) {
				if (!finObject.differs(pTransType, myCurr.theTransType))
					break;
			}
			if (myCurr == null) myCurr = new Item(pTransType);
			return myCurr;
		}
		
		/**
		 * Search for the item for this Account Type
		 * @param pActType - the Account type
		 * @return the item
		 */
		public Item searchFor(finStatic.AccountType pActType) {
			Item myCurr;
			for (myCurr = theFirst;
			     myCurr != null;
			     myCurr = myCurr.theNext) {
				if (!finObject.differs(pActType, myCurr.theActType))
					break;
			}
			if (myCurr == null) myCurr = new Item(pActType);
			return myCurr;
		}		
		
		/* The item class */
		public class Item {
			private finStatic.TransType   theTransType = null;
			private finStatic.AccountType theActType   = null;
			private JComboBox             theCredit    = null;
			private JComboBox             theDebit     = null;
			private Item             	  theNext      = null;
			
			public JComboBox getCredit() { return theCredit; }
			public JComboBox getDebit()  { return theDebit; }
		
			public Item (finStatic.TransType pTransType) {
				theCredit    = new JComboBox();
				theDebit     = new JComboBox();
				theTransType = pTransType;
				theNext      = theFirst;
				theFirst     = this;
			}
			public Item (finStatic.AccountType pActType) {
				theCredit    = new JComboBox();
				theDebit     = new JComboBox();
				theActType   = pActType;
				theNext      = theFirst;
				theFirst     = this;
			}
			public void addCredit(java.lang.String pName) {
				theCredit.addItem(pName);
			}
			public void addDebit(java.lang.String pName) {
				theDebit.addItem(pName);
			}
		}
	}
	
	/* Interface to allow cell colours and tool-tips in JTable */
	public static class RenderData {
		private java.lang.String theToolTipText = null;
		private Font			 theFont		= null;
		private Color            theForeGround  = null;
		private Color            theBackGround  = null;
		private int				 theRow			= 0;
		private int				 theCol			= 0;
		private boolean			 isSelected		= false;
		private boolean			 isFixed		= false;
		public  Color            getForeGround() { return theForeGround; }
		public  Color            getBackGround() { return theBackGround; }
		public  Font             getFont() 		{ return theFont; }
		public  java.lang.String getToolTip()   { return theToolTipText; }
		public  int				 getRow()   	{ return theRow; }
		public  int				 getCol()		{ return theCol; }
		public  boolean			 isSelected() 	{ return isSelected; }
		public  boolean			 isFixed() 		{ return isFixed; }
		public 	RenderData(boolean isFixed) { this.isFixed = isFixed;}
		public  void setData(Color pFore, Color pBack, 
							 Font pFont, java.lang.String pTooltip) { 
			theForeGround 	= pFore;
			theBackGround 	= pBack;
			theFont		  	= pFont;
			theToolTipText 	= pTooltip; }
		public void setPosition(int row, int col, boolean isSelected) {
			theRow 			= row;
			theCol 			= col;
			this.isSelected = isSelected; }
	}

	/* Controls details */
	public static class Controls {
		/* DatePeriod values */
		private enum DatePeriod {
			ONEMONTH,
			QUARTERYEAR,
			HALFYEAR,
			ONEYEAR,
			UNLIMITED;
		}
		
		public static class DateSelection implements ItemListener,
													 ActionListener,
        											 ChangeListener {
			/* Members */
			private financeTable			theTable  	   = null;
			private SpinnerDateModel        theModel       = null;
			private JSpinner                theStartBox    = null;
			private JComboBox               thePeriodBox   = null;
			private JPanel                  thePanel       = null;
			private JButton                 theNextButton  = null;
			private JButton                 thePrevButton  = null;
			private JLabel                  theStartLabel  = null;
			private JLabel                  thePeriodLabel = null;
			private finObject.Date          theStartDate   = null;
			private finObject.Date          theFirstDate   = null;
			private finObject.Date          theFinalDate   = null;
			private DatePeriod           	thePeriod      = DatePeriod.ONEMONTH;
			private finObject.Range         theRange       = null;
			private boolean        			isPrevOK	   = true;
			private boolean					isNextOK	   = true;
			private boolean					refreshingData = false;
		
			/* Access methods */
			protected JPanel               getPanel()   { return thePanel; }
			protected finObject.Range      getRange()   { return theRange; }
		
			/* Period descriptions */
			private static final String OneMonth    = "One Month";
			private static final String QuarterYear = "Quarter Year";
			private static final String HalfYear    = "Half Year";
			private static final String OneYear     = "One Year";
			private static final String Unlimited   = "Unlimited";
		
			/* Constructor */
			public DateSelection(financeTable pTable) {
				
				/* Create the DateSpinner Model */
				theModel = new SpinnerDateModel();
				theTable = pTable;
			
				/* Create the boxes */
				theStartBox   = new JSpinner(theModel);
				thePeriodBox  = new JComboBox();
			
				/* Limit the spinner to the Range */
				theModel.setValue(new java.util.Date());
				theStartDate = new finObject.Date(theModel.getDate());
				setRange(null);
			
				/* Set the format of the date */
				theStartBox.setEditor(new JSpinner.DateEditor(theStartBox, "dd-MMM-yyyy"));
			
				/* Add the PeriodTypes to the period box */
				thePeriodBox.addItem(OneMonth);
				thePeriodBox.addItem(QuarterYear);
				thePeriodBox.addItem(HalfYear);
				thePeriodBox.addItem(OneYear);
				thePeriodBox.addItem(Unlimited);
				thePeriodBox.setSelectedIndex(0);
				
				/* Create the labels */
				theStartLabel = new JLabel("Start Date:");
				thePeriodLabel = new JLabel("Period:");
			
				/* Create the buttons */
				theNextButton = new JButton("Next");
				thePrevButton = new JButton("Prev");
			
				/* Add the listener for item changes */
				thePeriodBox.addItemListener(this);
				theNextButton.addActionListener(this);
				thePrevButton.addActionListener(this);
				theModel.addChangeListener(this);
			
				/* Create the panel */
				thePanel = new JPanel();
				thePanel.setBorder(javax.swing.BorderFactory
									.createTitledBorder("Date Range Selection"));

				/* Create the layout for the panel */
			    GroupLayout panelLayout = new GroupLayout(thePanel);
			    thePanel.setLayout(panelLayout);
			    
			    /* Set the layout */
			    panelLayout.setHorizontalGroup(
			    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			            .addGroup(panelLayout.createSequentialGroup()
			                .addContainerGap()
			                .addComponent(theStartLabel)
			                .addGap(18, 18, 18)
			                .addComponent(theStartBox, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
			                .addGap(29, 29, 29)
			                .addComponent(theNextButton)
			                .addGap(26, 26, 26)
			                .addComponent(thePrevButton)
			                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
			                .addComponent(thePeriodLabel)
			                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			                .addComponent(thePeriodBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			                .addGap(25, 25, 25))
			    );
			    panelLayout.setVerticalGroup(
			        panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			            .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			                .addComponent(theStartLabel)
			                .addComponent(theStartBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			                .addComponent(theNextButton)
			                .addComponent(thePrevButton)
			                .addComponent(thePeriodLabel)
			                .addComponent(thePeriodBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			    );

				/* Initiate lock-down mode */
				setLockDown();
			}
		
			public  void setRange(finObject.Range pRange) {
				finObject.Date myStart = null;
				
				theFirstDate = (pRange == null) ? null : pRange.getStart();
				theFinalDate = (pRange == null) ? null : pRange.getEnd();
				if (theFirstDate != null) {
					myStart = new finObject.Date(theFirstDate);
					myStart.adjustDay(-1);
				}
				theModel.setStart((theFirstDate == null) ? null : myStart.getDate());
				theModel.setEnd((theFinalDate == null) ? null : theFinalDate.getDate());
			}
			
		
			/* Copy date selection from other box */
			public void setSelection(DateSelection pSource) {
				DatePeriod myPeriod = pSource.thePeriod;
				
				/* Set the refreshing data flag */
				refreshingData = true;
				
				/* Set the new date */
				theModel.setValue(pSource.theStartDate.getDate());
				theStartDate = new finObject.Date(pSource.theStartDate);
				
				/* Select the correct period */
				thePeriodBox.setSelectedItem(getPeriodName(myPeriod));
				thePeriod = myPeriod;
				
				/* Build the range */
				theRange = buildRange();
				
				/* Reset the refreshing data flag */
				refreshingData = false;
			}
		
			/* ItemStateChanged listener event */
			public String getPeriodName(DatePeriod pPeriod) {
				String myName = null;
				
				/* Switch on period to select name */
				switch(pPeriod) {
					case ONEMONTH:    myName = OneMonth;    break;
					case QUARTERYEAR: myName = QuarterYear; break;
					case HALFYEAR:    myName = HalfYear;    break;
					case ONEYEAR:     myName = OneYear;     break;
					case UNLIMITED:   myName = Unlimited;   break;
				}
				
				/* Return the name */
				return myName;
			}
			
			/* Lock/Unlock the selection */
			public void setLockDown() {
				boolean bLock = theTable.hasUpdates();
				
				/* Lock/Unlock the selection */
				theStartBox.setEnabled(!bLock);
				thePeriodBox.setEnabled(!bLock);
				theNextButton.setEnabled((!bLock) && isNextOK);
				thePrevButton.setEnabled((!bLock) && isPrevOK);
			}
		
			/* adjust a date by a period */
			private finObject.Date adjustDate(finObject.Date pDate,
					                          boolean        bForward) {
				finObject.Date myDate;
			
				/* Initialise the date */
				myDate = new finObject.Date(pDate);
			
				/* Switch on the period */
				switch (thePeriod) {
					case ONEMONTH:
						myDate.adjustMonth((bForward) ? 1 : -1);
						break;
					case QUARTERYEAR:
						myDate.adjustMonth((bForward) ? 3 : -3);
						break;
					case HALFYEAR:
						myDate.adjustMonth((bForward) ? 6 : -6);
						break;
					case ONEYEAR:
						myDate.adjustYear((bForward) ? 1 : -1);
						break;
				}
				
				/* Make sure that we do not go beyond the date range */
				if ((theFirstDate != null) &&
					(myDate.compareTo(theFirstDate) < 0)) myDate = theFirstDate;
				if ((theFinalDate != null) &&
					(myDate.compareTo(theFinalDate) > 0)) myDate = theFinalDate;
			
				/* Return the date */
				return myDate;
			}
		
			/* build the range represented by the selection */
			private finObject.Range buildRange() {
				finObject.Date myEnd;
			
				/* If we are unlimited */
				if (thePeriod == DatePeriod.UNLIMITED) {
					/* Set end date as last possible date*/
					myEnd = theFinalDate;
					
					/* Note that previous and next are not allowed */
					isNextOK = isPrevOK = false;
				}
				
				/* else we have to calculate the date */
				else{
					/* Initialise the end date */
					myEnd = new finObject.Date(theStartDate);
			
					/* Adjust the date */
					myEnd = adjustDate(myEnd, true);
					
					/* Assume that both next and prev are OK */
					isNextOK = isPrevOK = true;
					
					/* If we have not hit the start date disable prev */
					if (theStartDate.compareTo(theFirstDate) == 0)
						isPrevOK = false;
					
					/* If we have not hit the final date shift back one day */
					if (myEnd.compareTo(theFinalDate) != 0)
						myEnd.adjustDay(-1);
					else
						isNextOK = false;
				}
				
				/* Adjust the lock-down */
				setLockDown();
				theModel.setValue(theStartDate.getDate());
				
				/* Return the date range */
				return new finObject.Range(theStartDate, myEnd);
			}
		
			/* ItemStateChanged listener event */
			public void itemStateChanged(ItemEvent evt) {
				String                myName;
				boolean               bChange = false;

				/* Ignore selection if refreshing data */
				if (refreshingData) return;
				
				/* If this event relates to the period box */
				if (evt.getSource() == (Object)thePeriodBox) {
					myName = (String)evt.getItem();
					if (evt.getStateChange() == ItemEvent.SELECTED) {
						/* Determine the new period */
						bChange = true;
						if (myName == OneMonth)	        thePeriod = DatePeriod.ONEMONTH;
						else if (myName == QuarterYear) thePeriod = DatePeriod.QUARTERYEAR;
						else if (myName == HalfYear)    thePeriod = DatePeriod.HALFYEAR;
						else if (myName == OneYear)     thePeriod = DatePeriod.ONEYEAR;
						else if (myName == Unlimited)   thePeriod = DatePeriod.UNLIMITED;
						else bChange = false;
					
						/* Build the new range */
						if (bChange) theRange = buildRange();
					}
				}
			
				/* If we have a change, alert the tab group */
				if (bChange) { theTable.notifySelection(this); }
			}
		
			/* ActionPerformed listener event */
			public void actionPerformed(ActionEvent evt) {
				boolean               bChange = false;

				/* If this event relates to the next button */
				if (evt.getSource() == (Object)theNextButton) {
					/* Calculate the new start date */
					theStartDate = adjustDate(theStartDate, true);
					bChange      = true;
					
					/* Build the new range */
					if (bChange) theRange = buildRange();
				}
				
				/* If this event relates to the previous button */
				else if (evt.getSource() == (Object)thePrevButton) {
					/* Calculate the new start date */
					theStartDate = adjustDate(theStartDate, false);
					bChange      = true;
					
					/* Build the new range */
					if (bChange) theRange = buildRange();
				}
				
				/* If we have a change, alert the tab group */
				if (bChange) { theTable.notifySelection(this); }
			}
		
			/* stateChanged listener event */
			public void stateChanged(ChangeEvent evt) {
				boolean bChange = false;

				/* Ignore selection if refreshing data */
				if (refreshingData) return;
				
				/* If this event relates to the start box */
				if (evt.getSource() == (Object)theModel) {
					theStartDate = new finObject.Date(theModel.getDate());
					bChange      = true;
				}			
						
				/* Build the new range */
				if (bChange) theRange = buildRange();
			
				/* If we have a change, notify the main program */
				if (bChange) { theTable.notifySelection(this); }
			}
		}
		
		/* Account Selection details */
		public static class AccountSelection implements ItemListener {
			/* Members */
			private JPanel					thePanel		= null;
			private financePanel			theTable		= null;
			private finView					theView			= null;
			private JComboBox               theTypesBox 	= null;
			private JComboBox               theAccountBox	= null;
			private JLabel                  theTypeLabel    = null;
			private JLabel                  theAccountLabel = null;
			private JCheckBox				theShowClosed   = null;
			private JCheckBox				theShowDeleted  = null;
			private finStatic.ActTypeList   theTypes		= null;
			private finData.AccountList     theAccounts     = null;
			private finStatic.AccountType   theType			= null;
			private finData.Account         theSelected 	= null;
			private boolean					doShowClosed	= false;
			private boolean					doShowDeleted	= false;
			private boolean					acctsPopulated 	= false;
			private boolean					typesPopulated 	= false;
			private boolean					refreshingData  = false;
			
			/* Access methods */
			protected JPanel           	   	getPanel()      { return thePanel; }
			protected finData.Account      	getSelected()   { return theSelected; }
			protected finStatic.AccountType	getType()	   	{ return theType; }
			protected boolean				doShowClosed()	{ return doShowClosed; }
			protected boolean				doShowDeleted()	{ return doShowDeleted; }
						
			/* Constructor */
			public AccountSelection(finView      pView, 
									financePanel pTable,
									boolean	     showDeleted) {
				
				/* Store table and view details */
				theTable	  = pTable;
				theView 	  = pView;
				
				/* Create the boxes */
				theTypesBox    = new JComboBox();
				theAccountBox  = new JComboBox();
				theShowClosed  = new JCheckBox();
				theShowDeleted = new JCheckBox();
									
				/* Initialise the data from the view */
				refreshData();
				
				/* Set the text for the check-box */
				theShowClosed.setText("Show Closed");
				theShowClosed.setSelected(doShowClosed);
				
				/* Set the text for the check-box */
				theShowDeleted.setText("Show Deleted");
				theShowDeleted.setSelected(doShowDeleted);
				
				/* Create the labels */
				theTypeLabel = new JLabel("Account Type:");
				theAccountLabel = new JLabel("Account:");
				
				/* Add the listener for item changes */
				theTypesBox.addItemListener(this);
				theAccountBox.addItemListener(this);
				theShowClosed.addItemListener(this);
				theShowDeleted.addItemListener(this);
				
				/* Create the panel */
				thePanel = new JPanel();
				thePanel.setBorder(javax.swing.BorderFactory
									.createTitledBorder("Account Selection"));

				/* Create the layout for the panel */
			    GroupLayout panelLayout = new GroupLayout(thePanel);
			    thePanel.setLayout(panelLayout);
			    
			    /* If we are showing deleted */
			    if (showDeleted) {
			    	/* Set the layout */
			    	panelLayout.setHorizontalGroup(
			    		panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			            	.addGroup(panelLayout.createSequentialGroup()
			            		.addContainerGap()
			            		.addComponent(theTypeLabel)
			            		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			            		.addComponent(theTypesBox, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
			            		.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			            		.addComponent(theAccountLabel)
			            		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			            		.addComponent(theAccountBox)
			            		.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			            		.addComponent(theShowClosed, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			            		.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			            		.addComponent(theShowDeleted, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			            		.addContainerGap())
			    	);
			    	panelLayout.setVerticalGroup(
			    		panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			            	.addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			            		.addComponent(theTypeLabel)
			            		.addComponent(theTypesBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			            		.addComponent(theAccountLabel)
			            		.addComponent(theAccountBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			            		.addComponent(theShowClosed)
			            		.addComponent(theShowDeleted)
			    	);
			    }

			    else {
			    	/* Set the layout */
			    	panelLayout.setHorizontalGroup(
			    		panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			            	.addGroup(panelLayout.createSequentialGroup()
			            		.addContainerGap()
			            		.addComponent(theTypeLabel)
			            		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			            		.addComponent(theTypesBox, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
			            		.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			            		.addComponent(theAccountLabel)
			            		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			            		.addComponent(theAccountBox)
			            		.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			            		.addComponent(theShowClosed, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			            		.addContainerGap())
			    	);
			    	panelLayout.setVerticalGroup(
			    		panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			            	.addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			            		.addComponent(theTypeLabel)
			            		.addComponent(theTypesBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			            		.addComponent(theAccountLabel)
			            		.addComponent(theAccountBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			            		.addComponent(theShowClosed)
			    	);
			    }

			    /* Initiate lock-down mode */
				setLockDown();
			}
			
			/* refresh data */
			public void refreshData() {
				finData				  myData;
				finStatic.AccountType myType;
				
				/* Access the data */
				myData = theView.getData();
				
				/* Access types and accounts */
				theTypes    = myData.getActTypes();
				theAccounts = myData.getAccounts();
			
				/* Note that we are refreshing data */
				refreshingData = true;
				
				/* If we have types already populated */
				if (typesPopulated) {	
					/* If we have a selected type */
					if (theType != null) {
						/* Find it in the new list */
						theType = theTypes.searchFor(theType.getName());
					}
					
					/* Remove the types */
					theTypesBox.removeAllItems();
					typesPopulated = false;
				}
				
				/* Add the AccountType values to the types box */
				for (myType  = theTypes.getFirst();
				     myType != null;
				     myType  = myType.getNext()) {
					/* Add the item to the list */
					theTypesBox.addItem(myType.getName());
					typesPopulated = true;
				}
				
				/* If we have a selected type */
				if (theType != null) {
					/* Select it in the new list */
					theTypesBox.setSelectedItem(theType.getName());
				}
				
				/* Else we have no type currently selected */
				else if (typesPopulated) {
					/* Select the first account type */
					theTypesBox.setSelectedIndex(0);
					theType = theTypes.getFirst();
				}

				/* Note that we have finished refreshing data */
				refreshingData = false;

				/* Build the account list for the type */ 
				buildAccounts();	
			}
			
			/* build the accounts comboBox */
			private boolean buildAccounts() {
				finData.Account       myAcct;
				finData.Account       myFirst = null;
				finData.Account       myOld   = theSelected;
				
				/* Note that we are refreshing data */
				refreshingData = true;
				
				/* If we have accounts already populated */
				if (acctsPopulated) {	
					/* If we have a selected account */
					if (theSelected != null) {
						/* Find it in the new list */
						theSelected = theAccounts.searchFor(theSelected.getName());
					}
					
					/* Remove the accounts from the box */
					theAccountBox.removeAllItems();
					acctsPopulated = false;
				}
				
				/* If the selected item is no longer valid */
				if ((theSelected != null) &&
					(((!doShowDeleted) &&
					  (theSelected.isDeleted())) ||
					 ((!doShowClosed) &&
					  (theSelected.isClosed())) ||
					 (theType.compareTo(theSelected.getActType()) != 0))) {
					/* Remove selection */
					theSelected = null;
				}
				
				/* Add the Account values to the types box */
				for (myAcct  = theAccounts.getFirst();
				     myAcct != null;
				     myAcct  = myAcct.getNext()) {
					/* Skip deleted items */
					if ((!doShowDeleted) &&
						(myAcct.isDeleted())) continue;
					
					/* Skip closed items if required */
					if ((!doShowClosed) && 
						(myAcct.isClosed())) continue;
					
					/* Skip items that are the wrong type */
					if (theType.compareTo(myAcct.getActType()) != 0)
					  continue;
					
					/* Note the first in the list */
					if (myFirst == null) myFirst = myAcct;
					
					/* Add the item to the list */
					theAccountBox.addItem(myAcct.getName());
					acctsPopulated = true;
				}
							
				/* If we have a selected account */
				if (theSelected != null) {
					/* Select it in the new list */
					theAccountBox.setSelectedItem(theSelected.getName());
				}
				
				/* Else we have no account currently selected */
				else if (acctsPopulated) {
					/* Select the first account */
					theAccountBox.setSelectedIndex(0);
					theSelected = myFirst;
				}

				/* Note that we have finished refreshing data */
				refreshingData = false;
				
				/* Return whether we have changed selection */
				return finObject.differs(theSelected, myOld);
			}
			
			/* Copy date selection from other box */
			public void setSelection(finData.Account pAccount) {
				finData.Account myAccount;
				
				/* Set the refreshing data flag */
				refreshingData = true;
				
				/* Access the editable account */
				myAccount = theAccounts.searchFor(pAccount.getName());
				
				/* Select the correct account type */
				theType = pAccount.getActType();
				theTypesBox.setSelectedItem(theType.getName());
				
				/* If we need to show closed items */
				if ((!doShowClosed) && (myAccount != null) && (myAccount.isClosed())) {
					/* Set the flag correctly */
					doShowClosed = true;
					theShowClosed.setSelected(doShowClosed);
				}
				
				/* If we need to show deleted items */
				if ((!doShowDeleted) && (myAccount != null) && (myAccount.isDeleted())) {
					/* Set the flag correctly */
					doShowDeleted = true;
					theShowDeleted.setSelected(doShowDeleted);
				}
				
				/* Select the account */
				theSelected = myAccount;
				
				/* Reset the refreshing data flag */
				refreshingData = false;

				/* Build the accounts */
				buildAccounts();
			}
		
			/* Lock/Unlock the selection */
			public void setLockDown() {
				boolean bLock = theTable.hasUpdates();
				
				/* Lock/Unlock the selection */
				theTypesBox.setEnabled(!bLock);
				theAccountBox.setEnabled(!bLock);
				
				/* Can't switch off show closed if account is closed */
				if ((theSelected != null) &&
					(theSelected.isClosed()))
					bLock = true;
				
				/* Lock Show Closed */
				theShowClosed.setEnabled(!bLock);
				
				/* Reset the lock */
				bLock = theTable.hasUpdates();
				
				/* Can't switch off show deleted if account is deleted */
				if ((theSelected != null) &&
					(theSelected.isDeleted()))
					bLock = true;
				
				/* Lock Show Deleted */
				theShowDeleted.setEnabled(!bLock);
			}
			
			/* ItemStateChanged listener event */
			public void itemStateChanged(ItemEvent evt) {
				String                myName;
				boolean               bChange = false;

				/* Ignore selection if refreshing data */
				if (refreshingData) return;
				
				/* If this event relates to the types box */
				if (evt.getSource() == (Object)theTypesBox) {
					myName = (String)evt.getItem();
					if (evt.getStateChange() == ItemEvent.SELECTED) {
						/* Select the new type and rebuild account list */
						theType = theTypes.searchFor(myName);
						bChange = buildAccounts();
					}
				}
				
				/* If this event relates to the account box */
				if (evt.getSource() == (Object)theAccountBox) {
					myName = (String)evt.getItem();
					if (evt.getStateChange() == ItemEvent.SELECTED) {
						/* Select the new account */						
						theSelected = theAccounts.searchFor(myName);
						bChange     = true;
					}
				}
				
				/* If this event relates to the showClosed box */
				if (evt.getSource() == (Object)theShowClosed) {
					/* Note the new criteria and re-build lists */
					doShowClosed = theShowClosed.isSelected();
					bChange = buildAccounts();
				}
				
				/* If this event relates to the showDeleted box */
				if (evt.getSource() == (Object)theShowDeleted) {
					/* Note the new criteria and re-build lists */
					doShowDeleted = theShowDeleted.isSelected();
					bChange = buildAccounts();
				}
				
				/* If we have a change, alert the table */
				if (bChange) { theTable.notifySelection(this); }
			}
		}
		
		/* Spot Selection details */
		public static class SpotSelection implements ItemListener,
													 ChangeListener {
			/* Members */
			private JPanel					thePanel		= null;
			private finSwing.SpotViewTable  theControl		= null;
			private finView					theView			= null;
			private SpinnerDateModel        theModel        = null;
			private JSpinner                theDateBox      = null;
			private JCheckBox				theShowClosed   = null;
			private finObject.Date			theSpotDate		= null;
			private boolean					doShowClosed	= false;
			
			/* Access methods */
			protected JPanel          	   	getPanel()  	{ return thePanel; }
			protected finObject.Date		getDate()		{ return theSpotDate; }
			protected boolean				getShowClosed() { return doShowClosed; }
						
			/* Constructor */
			public SpotSelection(finView pView, finSwing.SpotViewTable pTable) {
				
				/* Store table and view details */
				theView 	  = pView;
				theControl	  = pTable;
				
				/* Create the check box */
				theShowClosed = new JCheckBox("Show Closed");
				theShowClosed.setSelected(doShowClosed);
				
				/* Create the DateSpinner Model and Box */
				theModel   = new SpinnerDateModel();
				theDateBox = new JSpinner(theModel);
				
				/* Initialise the data from the view */
				refreshData();
				
				/* Limit the spinner to the Range */
				theModel.setValue(new java.util.Date());
				theSpotDate = new finObject.Date(theModel.getDate());
			
				/* Set the format of the date */
				theDateBox.setEditor(new JSpinner.DateEditor(theDateBox, "dd-MMM-yyyy"));
			
				/* Add the listener for item changes */
				theModel.addChangeListener(this);
				theShowClosed.addItemListener(this);
				
				/* Create the panel */
				thePanel = new JPanel();
				thePanel.setBorder(javax.swing.BorderFactory
									.createTitledBorder("Spot Selection"));

				/* Create the layout for the panel */
			    GroupLayout panelLayout = new GroupLayout(thePanel);
			    thePanel.setLayout(panelLayout);
			    
			    /* Set the layout */
			    panelLayout.setHorizontalGroup(
			    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			            .addGroup(panelLayout.createSequentialGroup()
			                .addContainerGap()
			                .addComponent(theDateBox, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
			                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			                .addComponent(theShowClosed))
			    );
			    panelLayout.setVerticalGroup(
			        panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			            .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			                .addComponent(theDateBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			                .addComponent(theShowClosed))
			    );

				/* Initiate lock-down mode */
				setLockDown();
			}
			
			/* refresh data */
			public void refreshData() {
				finObject.Range   myRange;
				
				/* Access the data */
				myRange = theView.getRange();
				
				/* Set the range for the Date Spinner */
				setRange(myRange);				
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
				boolean bLock = theControl.hasUpdates();
				
				theDateBox.setEnabled(!bLock);
			}
			
			/* ItemStateChanged listener event */
			public void itemStateChanged(ItemEvent evt) {
				boolean               bChange = false;

				/* If this event relates to the showClosed box */
				if (evt.getSource() == (Object)theShowClosed) {
					/* Note the new criteria and re-build lists */
					doShowClosed = theShowClosed.isSelected();
					bChange      = true;
				}
				
				/* If we have a change, alert the table */
				if (bChange) { theControl.notifySelection(this); }
			}
			
			/* stateChanged listener event */
			public void stateChanged(ChangeEvent evt) {
				boolean bChange = false;
				
				/* If this event relates to the start box */
				if (evt.getSource() == (Object)theModel) {
					theSpotDate = new finObject.Date(theModel.getDate());
					bChange    = true;
				}			
						
				/* If we have a change, notify the main program */
				if (bChange) { theControl.notifySelection(this); }
			}
		}
		
		/* tableCommands */
		public enum tableCommand {
			OK,
			VALIDATEALL,
			RESETALL,
			VALIDATE,
			RESET,
			INSERTCR,
			INSERTDB,
			DELETE,
			RECOVER,
			UNDO,
			NEXT,
			PREV;
		}
	
		public enum InsertStyle {
			INSERT,
			CREDITDEBIT,
			NONE;
		}
		
		/* RowButton Commands details */
		public static class RowButtons implements ItemListener,
												  ActionListener {
			/* Members */
			private JPanel					thePanel		= null;
			private JPanel					theHistPanel	= null;
			private JPanel					theEditPanel	= null;
			private JPanel					theDelPanel		= null;
			private JPanel					theInsPanel		= null;
			private financeTable			theTable		= null;
			private JButton               	theInsCrButton 	= null;
			private JButton               	theInsDbButton	= null;
			private JButton               	theDelButton 	= null;
			private JButton               	theRecovButton	= null;
			private JButton               	theUndoButton 	= null;
			private JButton               	theValButton 	= null;
			private JButton               	theResetButton 	= null;
			private JButton               	theNextButton	= null;
			private JButton               	thePrevButton	= null;
			private JCheckBox				theShowDeleted  = null;
			private boolean					doShowDeleted	= false;
			
			/* Access methods */
			protected JPanel           	   	getPanel()      { return thePanel; }
			protected boolean				getShowDel()	{ return doShowDeleted; }
						
			/* Constructor */
			public RowButtons(financeTable pTable, 
					          InsertStyle  pStyle) {
				GroupLayout panelLayout;
				
				/* Create the buttons */
				switch (pStyle)	{
					case CREDITDEBIT:
						theInsCrButton = new JButton("Insert Credit");
						theInsDbButton = new JButton("Insert Debit");
						break;
					case INSERT:	
						theInsCrButton = new JButton("Insert");
						break;
				}
				theDelButton   = new JButton("Delete");
				theRecovButton = new JButton("Recover");
				theUndoButton  = new JButton("Undo");
				theResetButton = new JButton("Reset");
				theValButton   = new JButton("Validate");
				theNextButton  = new JButton("Next");
				thePrevButton  = new JButton("Prev");
				theShowDeleted = new JCheckBox();
				theTable	   = pTable;
				
				/* Set the text for the check-box */
				theShowDeleted.setText("Show Deleted");
				theShowDeleted.setSelected(doShowDeleted);
				
				/* Add the listener for item changes */
				if (theInsCrButton != null)
					theInsCrButton.addActionListener(this);
				if (theInsDbButton != null)
					theInsDbButton.addActionListener(this);
				theDelButton.addActionListener(this);
				theRecovButton.addActionListener(this);
				theUndoButton.addActionListener(this);
				theResetButton.addActionListener(this);
				theValButton.addActionListener(this);
				theNextButton.addActionListener(this);
				thePrevButton.addActionListener(this);
				theShowDeleted.addItemListener(this);
				
			    /* If we have a credit option */
			    if (pStyle != InsertStyle.NONE) {
			    	/* Create the insert panel */
			    	theInsPanel = new JPanel();
			    	theInsPanel.setBorder(javax.swing.BorderFactory
			    								.createTitledBorder("Insert"));

			    	/* Create the layout for the insert panel */
			    	panelLayout = new GroupLayout(theInsPanel);
			    	theInsPanel.setLayout(panelLayout);
			    
			    	/* If we have a credit option */
			    	if (pStyle == InsertStyle.CREDITDEBIT) {
			    		/* Set the layout */
			    		panelLayout.setHorizontalGroup(
			    			panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			    				.addGroup(panelLayout.createSequentialGroup()
			    					.addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
			    						.addComponent(theInsCrButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			    						.addComponent(theInsDbButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
			    		);
			    		panelLayout.setVerticalGroup(
			    			panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			        			.addGroup(GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
			        				.addComponent(theInsCrButton)
			        				.addComponent(theInsDbButton))
			    		);
			    	}
	            
			    	/* else we have no credit option */
			    	else if (pStyle == InsertStyle.INSERT) {
			    		/* Set the layout */
			    		panelLayout.setHorizontalGroup(
			    			panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			        			.addGroup(panelLayout.createSequentialGroup()
			        				.addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
			        					.addComponent(theInsCrButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
			    		);
			    		panelLayout.setVerticalGroup(
			    			panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			        			.addGroup(GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
			        				.addComponent(theInsCrButton))
			    		);
			    	}
			    }
			    
				/* Create the edit panel */
				theEditPanel = new JPanel();
				theEditPanel.setBorder(javax.swing.BorderFactory
										.createTitledBorder("Edit"));

				/* Create the layout for the edit panel */
			    panelLayout = new GroupLayout(theEditPanel);
			    theEditPanel.setLayout(panelLayout);
			    
			    /* Set the layout */
			    panelLayout.setHorizontalGroup(
			    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			        	.addGroup(panelLayout.createSequentialGroup()
			        		.addContainerGap()
			                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
			                	.addComponent(theUndoButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			                    .addComponent(theValButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			                    .addComponent(theResetButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
			                .addContainerGap())
			    );
	            panelLayout.setVerticalGroup(
	            	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			        	.addGroup(GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
			        		.addComponent(theUndoButton)
			                .addComponent(theValButton)
			                .addComponent(theResetButton))
			    );
			    
				/* Create the history panel */
				theHistPanel = new JPanel();
				theHistPanel.setBorder(javax.swing.BorderFactory
										.createTitledBorder("History"));

				/* Create the layout for the history panel */
			    panelLayout = new GroupLayout(theHistPanel);
			    theHistPanel.setLayout(panelLayout);
			    
			    /* Set the layout */
			    panelLayout.setHorizontalGroup(
			    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			        	.addGroup(panelLayout.createSequentialGroup()
			                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
			                	.addComponent(theNextButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			                    .addComponent(thePrevButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
			    );
	            panelLayout.setVerticalGroup(
	            	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			        	.addGroup(GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
			        		.addComponent(theNextButton)
			                .addComponent(thePrevButton))
			    );
			    
				/* Create the delete panel */
				theDelPanel = new JPanel();
				theDelPanel.setBorder(javax.swing.BorderFactory
										.createTitledBorder("Delete"));

				/* Create the layout for the delete panel */
			    panelLayout = new GroupLayout(theDelPanel);
			    theDelPanel.setLayout(panelLayout);
			    
			    /* Set the layout */
			    panelLayout.setHorizontalGroup(
			    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			        	.addGroup(panelLayout.createSequentialGroup()
			                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
			                	.addComponent(theDelButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			                    .addComponent(theRecovButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			                    .addComponent(theShowDeleted, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
			    );
	            panelLayout.setVerticalGroup(
	            	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			        	.addGroup(GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
			        		.addComponent(theDelButton)
			                .addComponent(theRecovButton)
			                .addComponent(theShowDeleted))
			    );

		    	/* Create the main panel */
				thePanel = new JPanel();
				thePanel.setBorder(javax.swing.BorderFactory
						.createTitledBorder("Row Options"));

				/* Create the layout for the main panel */
			    panelLayout = new GroupLayout(thePanel);
			    thePanel.setLayout(panelLayout);
			    
			    /* If we have a credit option */
			    if (pStyle != InsertStyle.NONE) {
			    	/* Set the layout */
			    	panelLayout.setHorizontalGroup(
			    		panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			            	.addGroup(panelLayout.createSequentialGroup()
			            		.addComponent(theInsPanel)
			            		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			            		.addComponent(theDelPanel)
			            		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			            		.addComponent(theEditPanel)
			            		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			            		.addComponent(theHistPanel))
			    	);
			    	panelLayout.setVerticalGroup(
			    		panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			            	.addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			            		.addComponent(theInsPanel)
			            		.addComponent(theDelPanel)
			            		.addComponent(theEditPanel)
			            		.addComponent(theHistPanel))
			    	);
			    }
			    
			    /* else no insert option */
			    else {
			    	/* Set the layout */
			    	panelLayout.setHorizontalGroup(
			    		panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			            	.addGroup(panelLayout.createSequentialGroup()
			            		.addComponent(theDelPanel)
			            		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			            		.addComponent(theEditPanel)
			            		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			            		.addComponent(theHistPanel))
			    	);
			    	panelLayout.setVerticalGroup(
			    		panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			            	.addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			            		.addComponent(theDelPanel)
			            		.addComponent(theEditPanel)
			            		.addComponent(theHistPanel))
			    	);			    	
			    }

				/* Initiate lock-down mode */
				setLockDown();
			}
						
			/* Lock/Unlock the selection */
			public void setLockDown() {
				boolean 			  enableRecov 	= false;
				boolean 			  enableDel		= false;
				boolean 			  enableUndo	= false;
				boolean 			  enableReset	= false;
				boolean 			  enableValid	= false;
				boolean 			  enableNext	= false;
				boolean 			  enablePrev	= false;
				boolean				  enableIns		= true;
				boolean				  enableShow	= true;
				finSwing.tableElement myRow;
				
				/* If the table is locked */
				if (theTable.isLocked()) {
					/* Disable all buttons */
					enableUndo 	= false;
					enableReset	= false;
					enableValid	= false;
					enableNext	= false;
					enablePrev	= false;
					enableRecov	= false;
					enableDel	= false;
					enableIns	= false;
					enableShow	= false;
				}

				/* else not locked */
				else {
					/* Loop through the selected rows */
					for (int row : theTable.getSelectedRows()) {
						/* If we have a header decrement the row */
						if (theTable.hasHeader()) row--;
						
						/* Access the row */
						myRow = theTable.extractItemAt(row);
						
						/* Ignore locked rows */
						if ((myRow == null) || (myRow.isLocked())) continue;
						
						/* Determine which options are allowed */
						if (myRow.hasHistory())  { 
							enableUndo 	= true;
						  	enableReset	= true;
						  	if (myRow.getEditState() != EditState.VALID)
						  		enableValid  = true;
						}
						if (myRow.hasFurther(theTable)) enableNext 	= true;
						if (myRow.hasPrevious()) 		enablePrev 	= true;
						if (myRow.isDeleted())   {
							if (theTable.isValidObj((finLink.itemElement)myRow, myRow.getObj()))
								enableRecov	= true;
							enableShow  = false;
						}
						else							enableDel	= true;
					}
				}
				
				/* Enable/Disable the buttons */
				theRecovButton.setEnabled(enableRecov);
				theDelButton.setEnabled(enableDel);
				theUndoButton.setEnabled(enableUndo);
				theResetButton.setEnabled(enableReset);
				theValButton.setEnabled(enableValid);
				theNextButton.setEnabled(enableNext);
				thePrevButton.setEnabled(enablePrev);
				theShowDeleted.setEnabled(enableShow);
				if (theInsCrButton != null)
					theInsCrButton.setEnabled(enableIns);
				if (theInsDbButton != null)
					theInsDbButton.setEnabled(enableIns);
			}
			
			/* ItemStateChanged listener event */
			public void itemStateChanged(ItemEvent evt) {

				/* If this event relates to the showDeleted box */
				if (evt.getSource() == (Object)theShowDeleted) {
					/* Note the new criteria and re-build lists */
					doShowDeleted = theShowDeleted.isSelected();
					theTable.notifySelection(this);
				}
			}
			
			/* ActionPerformed listener event */
			public void actionPerformed(ActionEvent evt) {

				/* If this event relates to the InsCr box */
				if (evt.getSource() == (Object)theInsCrButton) {
					/* Pass command to the table */
					theTable.performCommand(tableCommand.INSERTCR);
				}
				
				/* If this event relates to the InsDb box */
				else if (evt.getSource() == (Object)theInsDbButton) {
					/* Pass command to the table */
					theTable.performCommand(tableCommand.INSERTDB);
				}
				
				/* If this event relates to the Del box */
				else if (evt.getSource() == (Object)theDelButton) {
					/* Pass command to the table */
					theTable.performCommand(tableCommand.DELETE);
				}
				
				/* If this event relates to the Recover box */
				else if (evt.getSource() == (Object)theRecovButton) {
					/* Pass command to the table */
					theTable.performCommand(tableCommand.RECOVER);
				}
				
				/* If this event relates to the Undo box */
				else if (evt.getSource() == (Object)theUndoButton) {
					/* Pass command to the table */
					theTable.performCommand(tableCommand.UNDO);
				}
				
				/* If this event relates to the Reset box */
				else if (evt.getSource() == (Object)theResetButton) {
					/* Pass command to the table */
					theTable.performCommand(tableCommand.RESET);
				
				}
				
				/* If this event relates to the Validate box */
				else if (evt.getSource() == (Object)theValButton) {
					/* Pass command to the table */
					theTable.performCommand(tableCommand.VALIDATE);
				}
				
				/* If this event relates to the Next box */
				else if (evt.getSource() == (Object)theNextButton) {
					/* Pass command to the table */
					theTable.performCommand(tableCommand.NEXT);
				}
				
				/* If this event relates to the Previous box */
				else if (evt.getSource() == (Object)thePrevButton) {
					/* Pass command to the table */
					theTable.performCommand(tableCommand.PREV);
				}
			}
		}
		
		/* Table Button Commands details */
		public static class TableButtons implements ActionListener {
			/* Members */
			private JPanel					thePanel		= null;
			private financePanel			theTable		= null;
			private JButton               	theOKButton 	= null;
			private JButton               	theValidButton	= null;
			private JButton                 theResetButton  = null;
			
			/* Access methods */
			protected JPanel           	   	getPanel()      { return thePanel; }
						
			/* Constructor */
			public TableButtons(financePanel pTable) {
				GroupLayout panelLayout;
				
				/* Create the boxes */
				theOKButton    = new JButton("OK");
				theValidButton = new JButton("Validate");
				theResetButton = new JButton("Reset");
				theTable	   = pTable;
				
				/* Add the listener for item changes */
				theOKButton.addActionListener(this);
				theValidButton.addActionListener(this);
				theResetButton.addActionListener(this);
				
				/* Create the save panel */
				thePanel = new JPanel();
				thePanel.setBorder(javax.swing.BorderFactory
									.createTitledBorder("Save Options"));

				/* Create the layout for the save panel */
			    panelLayout = new GroupLayout(thePanel);
			    thePanel.setLayout(panelLayout);
			    
			    /* Set the layout */
	            panelLayout.setHorizontalGroup(
	            	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			        	.addGroup(GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
			                .addContainerGap()
			        		.addComponent(theOKButton)
			                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			                .addComponent(theValidButton)
			                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			                .addComponent(theResetButton)
			                .addContainerGap())
			    );
			    panelLayout.setVerticalGroup(
				    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				        	.addGroup(panelLayout.createSequentialGroup()
				                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
				                	.addComponent(theOKButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				                    .addComponent(theValidButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				                    .addComponent(theResetButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
			    );

				/* Initiate lock-down mode */
				setLockDown();
			}
						
			/* Lock/Unlock the selection */
			public void setLockDown() {
				/* If the table is locked clear the buttons */
				if (theTable.isLocked()) {
					theOKButton.setEnabled(false);
					theValidButton.setEnabled(false);
					theResetButton.setEnabled(false);
					
				/* Else look at the edit state */
				} else {
					switch (theTable.getEditState()) {
						case CLEAN:
							theOKButton.setEnabled(false);
							theValidButton.setEnabled(false);
							theResetButton.setEnabled(false);
							break;
						case DIRTY:
						case ERROR:
							theOKButton.setEnabled(false);
							theValidButton.setEnabled(true);
							theResetButton.setEnabled(true);
							break;
						case VALID:
							theOKButton.setEnabled(true);
							theValidButton.setEnabled(false);
							theResetButton.setEnabled(true);
							break;
					}
				}
			}
			
			/* actionPerformed listener event */
			public void actionPerformed(ActionEvent evt) {

				/* If this event relates to the OK box */
				if (evt.getSource() == (Object)theOKButton) {
					/* Pass command to the table */
					theTable.performCommand(tableCommand.OK);
				}
				
				/* If this event relates to the Reset box */
				else if (evt.getSource() == (Object)theResetButton) {
					/* Pass command to the table */
					theTable.performCommand(tableCommand.RESETALL);
				}
				
				/* If this event relates to the OK box */
				else if (evt.getSource() == (Object)theValidButton) {
					/* Pass command to the table */
					theTable.performCommand(tableCommand.VALIDATEALL);
				}
				
				/* Set the lockDown Status */
				setLockDown();
			}
		}

		/* Status Bar details */
		public static class StatusBar implements ActionListener {
			/* Members */
			private JPanel					theProgPanel	= null;
			private JPanel					theStatPanel	= null;
			private JProgressBar			theSteps		= null;
			private JProgressBar			theStages		= null;
			private JButton               	theCancel	 	= null;
			private JButton               	theClear	 	= null;
			private JLabel               	theStageLabel	= null;
			private JLabel                  theOpnLabel  	= null;
			private JLabel                  theStatusLabel  = null;
			private finSwing				theSwing		= null;
			private finObject.Exception		theError		= null;
			private Timer					theTimer		= null;
			
			/* Access methods */
			public  JPanel           	   	getProgressPanel()  { return theProgPanel; }
			public  JPanel           	   	getStatusPanel()	{ return theStatPanel; }
			public  finObject.Exception	   	getError()			{ return theError; }
						
			/* Constructor */
			public StatusBar(finSwing pSwing) {
				GroupLayout panelLayout;
			
				/* Record passed parameters */
				theSwing = pSwing;
				
				/* Create the boxes */
				theCancel      = new JButton("Cancel");
				theClear       = new JButton("Clear");
				theOpnLabel    = new JLabel();
				theStageLabel  = new JLabel();
				theStatusLabel = new JLabel();
				theStages	   = new JProgressBar();
				theSteps	   = new JProgressBar();
				
				/* Initialise progress bars */
				theStages.setMaximum(100);
				theStages.setMinimum(0);
				theStages.setValue(0);
				theStages.setStringPainted(true);
				theSteps.setMaximum(100);
				theSteps.setMinimum(0);
				theSteps.setValue(0);
				theSteps.setStringPainted(true);
				
				/* Add the listener for item changes */
				theCancel.addActionListener(this);
				theClear.addActionListener(this);
				
				/* Create the progress panel */
				theProgPanel = new JPanel();
				theProgPanel.setBorder(javax.swing.BorderFactory
									.createTitledBorder("Progress"));

				/* Create the layout for the save panel */
			    panelLayout = new GroupLayout(theProgPanel);
			    theProgPanel.setLayout(panelLayout);
			    
			    /* Set the layout */
			    panelLayout.setHorizontalGroup(
				    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				            .addGroup(panelLayout.createSequentialGroup()
				                .addContainerGap()
				                .addComponent(theOpnLabel)
				                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				                .addComponent(theStages)
				                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				                .addComponent(theStageLabel, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
				                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				                .addComponent(theSteps)
				                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				                .addComponent(theCancel)
				                .addContainerGap())
			    );
			    panelLayout.setVerticalGroup(
				    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				        	.addGroup(panelLayout.createSequentialGroup()
				                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
				                	.addComponent(theOpnLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				                    .addComponent(theStages, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				                	.addComponent(theStageLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				                    .addComponent(theSteps, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				                    .addComponent(theCancel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
			    );
			    
				/* Create the status panel */
				theStatPanel = new JPanel();
				theStatPanel.setBorder(javax.swing.BorderFactory
									.createTitledBorder("Status"));

				/* Create the layout for the save panel */
			    panelLayout = new GroupLayout(theStatPanel);
			    theStatPanel.setLayout(panelLayout);
			    
			    /* Set the layout */
			    panelLayout.setHorizontalGroup(
				    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				        	.addGroup(GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
				                .addContainerGap()
				                .addComponent(theClear)
				                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				                .addComponent(theStatusLabel))
			    );
			    panelLayout.setVerticalGroup(
				    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				        	.addGroup(panelLayout.createSequentialGroup()
				                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
				                	.addComponent(theStatusLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				                    .addComponent(theClear, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
			    );
			}
			
			/* actionPerformed listener event */
			public void actionPerformed(ActionEvent evt) {

				/* If this event relates to the Cancel box */
				if (evt.getSource() == (Object)theCancel) {
					/* Pass command to the table */
					theSwing.performCancel();
				}
				
				/* If this event relates to the Clear box */
				if (evt.getSource() == (Object)theClear) {
					/* Stop any timer */
					if (theTimer != null) theTimer.stop();
					
					/* Make the Status window invisible */
					theStatPanel.setVisible(false);
					theError = null;
					
					/* Finish the thread */
					theSwing.finishThread();
				}
				
				/* If this event relates to the Clear or the timer box */
				if (evt.getSource() == (Object)theTimer) {
					/* Make the Status window invisible */
					theStatPanel.setVisible(false);
					theError = null;
					
					/* Finish the thread */
					theSwing.finishThread();
				}
			}
			
			/* Set Operation string */
			public void setOperation(String pStatus) {
				/* Set the label field */
				theOpnLabel.setText(pStatus);
			}
			
			/* Set Stage */
			public void setStage(String pStage,
					             int    pStagesDone,
					             int    pNumStages) {
				/* Expand stage text to 20 */
				String myStage = pStage + "                              ";
				myStage = myStage.substring(0, 20);
				
				/* Set the Stage progress */
				theStageLabel.setText(pStage);
				theStages.setMaximum(pNumStages);
				theStages.setValue(pStagesDone);
			}
			
			/* Set Steps */
			public void setSteps(int    pStepsDone,
					             int    pNumSteps) {
				/* Set the steps progress */
				theSteps.setMaximum(pNumSteps);
				theSteps.setValue(pStepsDone);
			}
			
			/* Set Success string */
			public void setSuccess(String pOperation) {
				/* Set the status text field */
				theStatusLabel.setText(pOperation + " succeeded");
				
				/* Show the status window rather than the progress window */
				theStatPanel.setVisible(true);
				theProgPanel.setVisible(false);
				
				/* Set up a timer for 5 seconds and no repeats */
				if (theTimer == null) theTimer = new Timer(5000, this);
				theTimer.setRepeats(false);
				theTimer.start();
			}
						
			/* Set Failure string */
			public void setFailure(String              pOperation,
					               finObject.Exception pError) {
				/* Initialise the message */
				String myText = pOperation + " failed";
				
				/* If there is an error detail */
				if (pError != null) {
					/* Add the error detail */
					myText += ". " + pError.getMessage();
				}
				
				/* else no failure - must have cancelled */
				else myText += ". Operation cancelled";
				
				/* Store the error */
				theError = pError;
				
				/* Set the status text field */
				theStatusLabel.setText(myText);
				
				/* Show the status window rather than the progress window */
				theStatPanel.setVisible(true);
				theProgPanel.setVisible(false);
			}
		}
	}
	
	public static class PrintUtilities implements Printable {
		  private JComponent componentToBePrinted;

		  public static void printComponent(JComponent c) {
		    new PrintUtilities(c).print();
		  }
		  
		  public PrintUtilities(JComponent componentToBePrinted) {
		    this.componentToBePrinted = componentToBePrinted;
		  }
		  
		  public void print() {
		    PrinterJob printJob = PrinterJob.getPrinterJob();
		    printJob.setPrintable(this);
		    if (printJob.printDialog())
		      try {
		        printJob.print();
		      } catch(PrinterException pe) {
		        System.out.println("Error printing: " + pe);
		      }
		  }

		  public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
		    if (pageIndex > 0) {
		      return(NO_SUCH_PAGE);
		    } else {
		      Graphics2D g2d = (Graphics2D)g;
		      g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		      disableDoubleBuffering(componentToBePrinted);
		      componentToBePrinted.paint(g2d);
		      enableDoubleBuffering(componentToBePrinted);
		      return(PAGE_EXISTS);
		    }
		  }

		  public static void disableDoubleBuffering(JComponent c) {
		    RepaintManager currentManager = RepaintManager.currentManager(c);
		    currentManager.setDoubleBufferingEnabled(false);
		  }

		  public static void enableDoubleBuffering(JComponent c) {
		    RepaintManager currentManager = RepaintManager.currentManager(c);
		    currentManager.setDoubleBufferingEnabled(true);
		  }
	}
	
	/**
	 * Password dialog class
	 */
	public static class passwordDialog extends JDialog 
								implements ActionListener {
		/**
		 * Serial version ID	
		 */
		private static final long serialVersionUID = 5867685302365849587L;

		/**
		 *	Obtained password
		 */
		private char[] 			thePassword		= null;

		/**
		 * OK Button
		 */
		private JButton 		theOKButton;

		/**
		 * Password field
		 */
		private JPasswordField 	thePassField;

		/**
		 * Is the password set
		 */
		private boolean 		isPasswordSet	= false;

		/**
		 * Obtain the password
		 */
		public char[] getPassword() { return thePassword; }

		/**
		 * Constructor
		 * @param pParent the parent frame for the dialog
		 */
		public passwordDialog(JFrame pParent) {
			/* Initialise the dialog (this calls dialogInit) */
			super(pParent, "Enter Password", true);
			setLocationRelativeTo(pParent);
		}

		/**
		 * Initialise dialog
		 */
		public void dialogInit() {
			JLabel 			myLabel;
			JPanel			myPanel;

			/* Create the components */
			myLabel 		= new JLabel("Password:");
			thePassField	= new JPasswordField("", 30);
			theOKButton		= new JButton("OK");

			/* Add the listener for item changes */
			theOKButton.addActionListener(this);
			thePassField.addActionListener(this);

			/* Initialise dialog */
			super.dialogInit();

			/* Create the panel */
			myPanel = new JPanel();

			/* Create the layout for the panel */
			GroupLayout myLayout = new GroupLayout(myPanel);
			myPanel.setLayout(myLayout);

			/* Set the layout */
			myLayout.setHorizontalGroup(
				myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(myLabel)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(thePassField)
						.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(theOKButton)
						.addContainerGap())
			);
			myLayout.setVerticalGroup(
				myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
							.addComponent(myLabel)
							.addComponent(thePassField)
							.addComponent(theOKButton))
							.addContainerGap())
			);

			/* Set this to be the main panel */
			getContentPane().add(myPanel);
			pack();
		}

		/**
		 *  Perform a requested action
		 * @param evt the action event
		 */
		public void actionPerformed(ActionEvent evt) {

			/* If this event relates to the OK box or the password field */
			if ((evt.getSource() == (Object)theOKButton) ||
				(evt.getSource() == (Object)thePassField)) {
				/* Access the password */
				thePassword = thePassField.getPassword();

			/* Note that we have set the password */
			isPasswordSet = true;

			/* Close the dialog */
			setVisible(false);
			}			
		}

		/**
		 * show the dialog
		 */
		public boolean showDialog() {
			/* Show the dialog */
			setVisible(true);

			/* Return whether the password is set */
			return isPasswordSet;
		}
	}
}
	