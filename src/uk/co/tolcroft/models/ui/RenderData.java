package uk.co.tolcroft.models.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.table.JTableHeader;

import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataState;

public class RenderData {
	/* static values */
	private	static Font 	theStdFont 		= new Font("Arial", Font.PLAIN, 12);
	private	static Font 	theNumFont 		= new Font("Courier", Font.PLAIN, 12);
	private	static Font 	theChgFont 		= new Font("Arial", Font.ITALIC, 12);
	private	static Font 	theChgNumFont 	= new Font("Courier", Font.ITALIC, 12);
	private static Color	theErrorColor	= Color.red;
	private static Color	theChangeColor	= Color.magenta;
	private static Color	theNewColor		= Color.blue;
	private static Color	theDelColor		= Color.lightGray;
	private static Color	theRecovColor	= Color.darkGray;
	private static Color	theStdColor		= Color.black;
	private static Color	theBackColor	= Color.white;

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
		myFore = theStdColor;
		myBack = theBackColor;

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
	 * Process Item
	 * @param pItem the item
	 * @param iField the field id 
	 * @param isNull is the item null 
	 */
	private void processComponent(DataItem<?> 	pItem,
								  int			iField,
		 					      boolean		isNull) {
		boolean 	isChanged;
		boolean 	isError;
		boolean 	isFlipped;
		DataState 	myState;
		Font		myFont;
		Color		myBack;
		Color		myFore;
		String		myTip = null;
		
		/* Access the data state */
		myState = pItem.getState();
		
		/* Determine the standard colour */
		myFore = theStdColor;
		myBack = theBackColor;
		if (myState == DataState.DELETED)
			myFore = theDelColor;
		else if (myState == DataState.NEW)
			myFore = theNewColor;
		else if (myState == DataState.RECOVERED)
			myFore = theRecovColor;
		
		/* Determine the render information */
		isChanged 	= pItem.fieldChanged(iField).isDifferent();
		myFont 		= (isChanged) ? (isFixed ? theChgNumFont : theChgFont) 
								  : (isFixed ? theNumFont : theStdFont);
		if (isError = pItem.hasErrors(iField)) 
			myTip  = pItem.getFieldErrors(iField);
		myFore	= ((isError) ? theErrorColor  
				 			 : (isChanged)	? theChangeColor
				 							: myFore);
		
		/* Determine whether to flip foreground and background */
		isFlipped = ((isError) && (isNull));
		
		theForeGround	= (isFlipped) ? myBack : myFore;
		theBackGround	= (isFlipped) ? myFore : myBack;
		theToolTipText 	= myTip;
		theFont			= myFont;		
	}
	
	/**
	 * Format the component for the field
	 * @param pComp the component
	 * @param iField the field number
	 * @param pItem the data item
	 * @param isNumeric is the field numeric
	 * @param isNull is the field null
	 */
	public static void formatComponent(JComponent	pComp,
								   	   int			iField,
								   	   DataItem<?>	pItem,
								   	   boolean 		isNumeric,
								   	   boolean		isNull) {
		/* Access RenderData */
		RenderData 	myRender	= new RenderData(isNumeric);
		myRender.processComponent(pItem, iField, isNull);

		/* Set component values */
		pComp.setForeground(myRender.getForeGround());
		pComp.setBackground(myRender.getBackGround());
		pComp.setToolTipText(myRender.getToolTip());
		pComp.setFont(myRender.getFont());
	}
}
