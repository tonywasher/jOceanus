package uk.co.tolcroft.models.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.table.JTableHeader;

import uk.co.tolcroft.models.PropertySet.Property;
import uk.co.tolcroft.models.PropertySet.PropertyType;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataState;

public class RenderData {
	/* static values */
	private	static Font 	theStdFont 		= new Font("Arial", Font.PLAIN, 12);
	private	static Font 	theNumFont 		= new Font("Courier", Font.PLAIN, 12);
	private	static Font 	theChgFont 		= new Font("Arial", Font.ITALIC, 12);
	private	static Font 	theChgNumFont 	= new Font("Courier", Font.ITALIC, 12);
	protected static Color	theErrorColor	= Color.red;
	protected static Color	theChangeColor	= Color.magenta.darker();
	protected static Color	theNewColor		= Color.blue;
	protected static Color	theDelColor		= Color.lightGray;
	protected static Color	theRecovColor	= Color.darkGray;
	protected static Color	theStdColor		= Color.black;
	protected static Color	theBackColor	= Color.white;

	/* Properties */
	private String			theToolTipText 	= null;
	private Font			theFont			= theStdFont;
	private Color           theForeGround  	= theStdColor;
	private Color           theBackGround  	= theBackColor;
	private int				theRow			= 0;
	private int				theCol			= 0;
	private boolean			isSelected		= false;
	private boolean			isFixed			= false;
	
	/* Access methods */
	public  Color           getForeGround() { return theForeGround; }
	public  Color           getBackGround() { return theBackGround; }
	public  Font            getFont() 		{ return theFont; }
	public  String 			getToolTip()   	{ return theToolTipText; }
	public  int				getRow()   		{ return theRow; }
	public  int				getCol()		{ return theCol; }
	public  boolean			isSelected() 	{ return isSelected; }
	public  boolean			isFixed() 		{ return isFixed; }
	
	/* Constructor */
	protected	RenderData(boolean isFixed) 	{ this.isFixed = isFixed; }
	
	/* Set the position */
	protected	void setPosition(int row, int col, boolean isSelected) {
		theRow 			= row;
		theCol 			= col;
		this.isSelected = isSelected; }
	
	/**
	 * Process Table Row
	 */
	protected void setDefaults() {
		/* Set the data */
		theForeGround 	= theStdColor;
		theBackGround	= theBackColor;
		theFont			= (isFixed) ? theNumFont : theStdFont;
		theToolTipText	= null;
	}
	
	/**
	 * Process Table Row
	 * @param pRow the Table row
	 * @param iField the field id 
	 */
	protected void processTableRow(DataItem<?> 	pRow,
								   int			iField) {
		String     	myTip = null;
		Color		myFore;
		Color		myBack;
		Font		myFont;
		boolean 	isChanged = false;
		
		/* Default is black on white */
		myFore = getForeground(pRow, iField);
		myBack = getBackground();

		/* Has the field changed */
		isChanged = pRow.fieldChanged(iField).isDifferent();
			
		/* Determine the colour */
		if (pRow.isDeleted()) {
			myFore = theDelColor;
		}
		else if ((pRow.hasErrors()) &&
				 (pRow.hasErrors(iField))) {
			myFore 	= theErrorColor;
			myTip 	= pRow.getFieldErrors(iField);
		}
		else if (isChanged)
			myFore 	= theChangeColor;
		else if (pRow.getState() == DataState.NEW)
			myFore 	= theNewColor;
		else if (pRow.getState() == DataState.RECOVERED)
			myFore 	= theRecovColor;
			
		/* For selected items flip the foreground/background */
		if (isSelected()){
			Color myTemp = myFore;
			myFore = myBack;
			myBack = myTemp;
		}
			
		/* Select the font */
		if (isFixed)
			myFont = isChanged	? theChgNumFont 
								: theNumFont;
		else
			myFont = isChanged	? theChgFont 
								: theStdFont;
		
		/* Set the data */
		theForeGround 	= myFore;
		theBackGround	= myBack;
		theFont			= myFont;
		theToolTipText	= myTip;
	}		

	/**
	 * Initialise data from Table Header
	 * @param pHeader the Table Header
	 */
	public void initFromHeader(JTableHeader pHeader) {
		theBackGround 	= pHeader.getBackground();
		theForeGround 	= pHeader.getForeground();
		theFont 		= pHeader.getFont();				
	}
	
	/**
	 * Process Table Row
	 * @param pRow the Table row
	 * @param iField the field id 
	 */
	protected void processRowHeader(DataItem<?> pRow,
								 	int[]		iFields) {
		boolean 	isChanged;

		/* Has the row changed */
		isChanged = pRow.hasHistory();
		
		/* Determine the colour */
		if (pRow.isDeleted()) {
			theForeGround = theDelColor;
		}
		else if (pRow.hasErrors()) {
			theForeGround 	= theStdColor;
			theBackGround 	= theErrorColor;
			theToolTipText	= pRow.getFieldErrors(iFields);
		}
		else if (isChanged)
			theForeGround = theChangeColor;
		else if (pRow.getState() == DataState.NEW)
			theForeGround = theNewColor;
		else if (pRow.getState() == DataState.RECOVERED)
			theForeGround = theRecovColor;
	}
	
	/**
	 * Determine Standard foreground
	 * @param pItem the Item
	 * @param iField the Field number
	 * @return the standard foreground for the item
	 */
	protected static Color getForeground(DataItem<?> pItem,
										 int		 iField) {
		/* Handle deleted items */
		if (pItem.isDeleted()) return theDelColor;

		/* Handle error items */
		if ((pItem.hasErrors()) && (pItem.hasErrors(iField))) 
			return theErrorColor;
			
		/* Handle changed items */
		if (pItem.fieldChanged(iField).isDifferent())
			return theChangeColor;
		
		/* Switch on Status */
		switch (pItem.getState()) {
			case NEW:		return theNewColor;
			case RECOVERED:	return theRecovColor;
			default: 		return theStdColor;
		}
	}

	/**
	 * Determine Standard foreground
	 * @param pProperty the property
	 * @return the standard foreground for the item
	 */
	protected static Color getForeground(Property pProperty) {
		/* Handle changed items */
		return (pProperty.isChanged()) 	? theChangeColor
										: theStdColor;
	}

	/**
	 * Determine Standard background
	 * @return the standard background
	 */
	protected static Color getBackground() { return theBackColor; }

	/**
	 * Determine Standard Font
	 * @param pItem the Item
	 * @param iField the Field number
	 * @return the standard Font for the item
	 */
	protected static Font getFont(DataItem<?> 	pItem,
								  int			iField,
								  boolean		isFixed) {
		if (pItem.fieldChanged(iField).isDifferent())
			return (isFixed ? theChgNumFont : theChgFont); 
		else 
			return (isFixed ? theNumFont : theStdFont);
	}
	
	/**
	 * Determine Standard Font
	 * @param pProperty the Item
	 * @return the standard Font for the item
	 */
	protected static Font getFont(Property 	pProperty) {
		boolean isFixed = pProperty.getType() == PropertyType.Integer; 
		if (pProperty.isChanged())
			return (isFixed ? theChgNumFont : theChgFont); 
		else 
			return (isFixed ? theNumFont : theStdFont);
	}
	
	/**
	 * Determine Standard ToolTip
	 * @param pItem the Item
	 * @param iField the Field number
	 * @return the standard ToolTip for the item
	 */
	protected static String getToolTip(DataItem<?> 	pItem,
									   int			iField) {
		/* Handle deleted items */
		if (pItem.isDeleted()) return null;

		/* Handle error items */
		if ((pItem.hasErrors()) && (pItem.hasErrors(iField))) 
			return pItem.getFieldErrors(iField);
			
		/* Return no ToolTip */
		return null;
	}
}
