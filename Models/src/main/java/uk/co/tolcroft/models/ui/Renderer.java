package uk.co.tolcroft.models.ui;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Decimal.*;
import uk.co.tolcroft.models.ui.DataTable.DataTableModel;
import uk.co.tolcroft.models.ui.DataTable.RowTableModel;
import uk.co.tolcroft.models.ui.DateButton.CellRenderer;

public class Renderer {
	/* Properties */
	protected static String theError = "Error";
	
	/* Access methods */
	public static String getError() { return theError; }
	
	/* String Cell Renderer */
	public static class StringCell extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -2004841981078780283L;
		private RenderData theData = null;
		
		public StringCell() {
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
			DataTableModel myModel = (DataTableModel)table.getModel();
			theData.setPosition(row, column, isSelected);
			myModel.getRenderData(theData);
			setForeground(theData.getForeGround());
			setBackground(theData.getBackGround());
			setFont(theData.getFont());
			setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			setToolTipText(theData.getToolTip());
			return this;
		}
	}
	
	/* Integer Cell Renderer */
	public static class IntegerCell extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -2004841981078780283L;
		private RenderData theData = null;
		
		public IntegerCell() {
			super();
			theData = new RenderData(false);
		}

		public void setValue(Object value) {
			String s;
			
			if (value == theError)  	s = theError;
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
			DataTableModel myModel = (DataTableModel)table.getModel();
			theData.setPosition(row, column, isSelected);
			myModel.getRenderData(theData);
			setForeground(theData.getForeGround());
			setBackground(theData.getBackGround());
			setFont(theData.getFont());
			setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			setToolTipText(theData.getToolTip());
			return this;
		}
	}

	/* Calendar Cell Renderer */
	public static class CalendarCell extends CellRenderer {
		private static final long serialVersionUID = 1947211408966548011L;
		private RenderData theData;
		
		public CalendarCell() {
			theData 	= new RenderData(true);
		}

		public void setValue(Object value) {
			DateDay 	myDate;
			
			/* If this is a Date value */
			if (value instanceof DateDay) {
				/* Extract the java date */
				myDate = (DateDay)value;
				value  = myDate.getDate();
			}
			
			/* Pass the value down */
			super.setValue(value);
		}
		
		public JComponent getTableCellRendererComponent(JTable table, Object value,
														boolean isSelected, 
														boolean hasFocus, 
														int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected,
												hasFocus, row, column);
			DataTableModel myModel = (DataTableModel)table.getModel();
			theData.setPosition(row, column, isSelected);
			myModel.getRenderData(theData);
			setForeground(theData.getForeGround());
			setBackground(theData.getBackGround());
			setFont(theData.getFont());
			setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		    setToolTipText(theData.getToolTip());
			return this;
		}
	}
	
	/* Rate Cell Renderer */
	public static class RateCell extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 6571410292897989673L;
		private RenderData theData = null;
		
		public RateCell() {
			super();
			theData = new RenderData(true);
		}

		public void setValue(Object value) {
			Rate 		myRate;
			String      s;
			
			if (value == theError)  s = theError;
			else if (value == null) s = "";
			else {
				myRate = (Rate)value;
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
			DataTableModel myModel = (DataTableModel)table.getModel();
			theData.setPosition(row, column, isSelected);
			myModel.getRenderData(theData);
			setForeground(theData.getForeGround());
			setBackground(theData.getBackGround());
			setFont(theData.getFont());
			setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		    setToolTipText(theData.getToolTip());
			return this;
		}
	}			

	/* Money Cell Renderer */
	public static class MoneyCell extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 5222695627810930911L;
		private RenderData theData = null;
		
		public MoneyCell() {
			super();
			theData = new RenderData(true);
		}

		public void setValue(Object value) {
			Money	myMoney;
			String  s;
			
			if (value == theError)  s = theError;
			else if (value == null) s = "";
			else {
				myMoney = (Money)value;
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
			DataTableModel myModel = (DataTableModel)table.getModel();
			theData.setPosition(row, column, isSelected);
			myModel.getRenderData(theData);
			setForeground(theData.getForeGround());
			setBackground(theData.getBackGround());
			setFont(theData.getFont());
			setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		    setToolTipText(theData.getToolTip());
			return this;
		}
	}

	/* Units Cell Renderer */
	public static class UnitCell extends DefaultTableCellRenderer {
		private static final long serialVersionUID = -4097886091647869702L;
		private RenderData theData = null;
		
		public UnitCell() {
			super();
			theData = new RenderData(true);
		}

		public void setValue(Object value) {
			Units 		myUnits;
			String      s;
			
			if (value == theError)  s = theError;
			else if (value == null) s = "";
			else {
				myUnits = (Units)value;
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
			DataTableModel myModel = (DataTableModel)table.getModel();
			theData.setPosition(row, column, isSelected);
			myModel.getRenderData(theData);
			setForeground(theData.getForeGround());
			setBackground(theData.getBackGround());
			setFont(theData.getFont());
			setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		    setToolTipText(theData.getToolTip());
			return this;
		}
	}			

	/* Dilution Cell Renderer */
	public static class DilutionCell extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 4073411390992240277L;
		private RenderData theData = null;
		
		public DilutionCell() {
			super();
			theData = new RenderData(true);
		}

		public void setValue(Object value) {
			Dilution		myDilution;
			String          s;
			
			if (value == theError)  s = theError;
			else if (value == null) s = "";
			else {
				myDilution = (Dilution)value;
				s          = myDilution.format(true);
			}
			
			super.setValue(s);
		}
		public JComponent getTableCellRendererComponent(JTable table, Object value,
														boolean isSelected, 
														boolean hasFocus, 
														int row, int column) {	
			super.getTableCellRendererComponent(table, value, isSelected,
												hasFocus, row, column);
			DataTableModel myModel = (DataTableModel)table.getModel();
			theData.setPosition(row, column, isSelected);
			myModel.getRenderData(theData);
			setForeground(theData.getForeGround());
			setBackground(theData.getBackGround());
			setFont(theData.getFont());
			setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		    setToolTipText(theData.getToolTip());
			return this;
		}
	}			

	/* Price Cell Renderer */
	public static class PriceCell extends DefaultTableCellRenderer {
		private static final long serialVersionUID = -2085384960415300006L;
		private RenderData theData = null;
		
		public PriceCell() {
			super();
			theData = new RenderData(true);
		}

		public void setValue(Object value) {
			Price 	myPrice;
			String 	s;
			
			if (value == theError)  s = theError;
			else if (value == null) s = "";
			else {
				myPrice = (Price)value;
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
			DataTableModel myModel = (DataTableModel)table.getModel();
			theData.setPosition(row, column, isSelected);
			myModel.getRenderData(theData);
			setForeground(theData.getForeGround());
			setBackground(theData.getBackGround());
			setFont(theData.getFont());
			setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		    setToolTipText(theData.getToolTip());
		    return this;
		}
	}	
	
	/* DilutedPrice Cell Renderer */
	public static class DilutedPriceCell extends DefaultTableCellRenderer {
		private static final long serialVersionUID = -1928861688649200235L;
		private RenderData theData = null;
		
		public DilutedPriceCell() {
			super();
			theData = new RenderData(true);
		}

		public void setValue(Object value) {
			DilutedPrice myPrice;
			String 				s;
			
			if (value == theError)  s = theError;
			else if (value == null) s = "";
			else {
				myPrice = (DilutedPrice)value;
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
			DataTableModel myModel = (DataTableModel)table.getModel();
			theData.setPosition(row, column, isSelected);
			myModel.getRenderData(theData);
			setForeground(theData.getForeGround());
			setBackground(theData.getBackGround());
			setFont(theData.getFont());
			setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		    setToolTipText(theData.getToolTip());
		    return this;
		}
	}	
	
	/* Row Cell Renderer */
	public static class RowCell extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 8710214547908947657L;
		private RenderData theData = null;
		
		public RowCell() {
			super();
			theData = new RenderData(false);
		}

		public void setValue(Object value) {
			String s;
			
			if (value == theError)  s = theError;
			else if (value == null) s = "";
			else
			{
				Integer myRow = (Integer)value;
				s = (myRow == 0) ? "" : myRow.toString();
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
			RowTableModel myModel = (RowTableModel)table.getModel();
			theData.setPosition(row, column, isSelected);
			myModel.getRenderData(theData);
			setForeground(theData.getForeGround());
			setBackground(theData.getBackGround());
			setFont(theData.getFont());
			setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			setToolTipText(theData.getToolTip());
			return this;
		}
	}	
}
