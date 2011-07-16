package uk.co.tolcroft.finance.sheets;

import uk.co.tolcroft.finance.data.ControlKey;
import uk.co.tolcroft.finance.data.DataSet;
import uk.co.tolcroft.finance.sheets.SpreadSheet.InputSheet;
import uk.co.tolcroft.finance.sheets.SpreadSheet.OutputSheet;
import uk.co.tolcroft.finance.sheets.SpreadSheet.SheetType;

public class SheetControlKey extends SheetDataItem<ControlKey> {
	/**
	 * SheetName for Keys
	 */
	private static final String Keys	   		= ControlKey.listName;

	
	/**
	 * Is the spreadsheet a backup spreadsheet or an edit-able one
	 */
	private boolean isBackup					= false;
	
	/**
	 * ControlKey data list
	 */
	private ControlKey.List theList				= null;

	/**
	 * DataSet
	 */
	private DataSet theData						= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pInput the input spreadsheet
	 */
	protected SheetControlKey(InputSheet	pInput) {
		/* Call super constructor */
		super(pInput, Keys);
		
		/* Access the Lists */
		theData	= pInput.getData();
		theList = theData.getControlKeys();
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pOutput the output spreadsheet
	 */
	protected SheetControlKey(OutputSheet	pOutput) {
		/* Call super constructor */
		super(pOutput, Keys);
		
		/* Note whether this is a backup */
		isBackup = (pOutput.getType() == SheetType.BACKUP);
				
		/* Access the Control list */
		theList = pOutput.getData().getControlKeys();
		setDataList(theList);
	}
	
	/**
	 * Load an item from the spreadsheet
	 */
	protected void loadItem() throws Throwable {
		/* Access the IDs */
		int	myID 		= loadInteger(0);
		
		/* Access the String values  */
		String 	myControl		= loadString(1);

		/* Add the Control */
		theList.addItem(myID, myControl);
	}

	/**
	 *  Insert a item into the spreadsheet
	 *  @param pItem the Item to insert
	 *  @param isBackup is the spreadsheet a backup, or else clear text
	 */
	protected void insertItem(ControlKey	pItem) throws Throwable  {
		/* Set the fields */
		writeInteger(0, pItem.getId());
		writeString(1, pItem.getSecurityKey());
	}

	/**
	 * PreProcess on write
	 */
	protected boolean preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return false;

		/* Write titles */
		writeString(0, ControlKey.fieldName(ControlKey.FIELD_ID));
		writeString(1, ControlKey.fieldName(ControlKey.FIELD_KEY));			
		return true;
	}	

	/**
	 * PostProcess on write
	 */
	protected void postProcessOnWrite() throws Throwable {		
		/* Set the two columns as the range */
		nameRange(2);
	}
}
