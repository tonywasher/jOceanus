package uk.co.tolcroft.models.sheets;

import uk.co.tolcroft.models.data.ControlData;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;

public class SheetControl extends SheetDataItem<ControlData> {	
	/**
	 * SheetName for Static
	 */
	private static final String Control	   		= ControlData.listName;
	
	/**
	 * Is the spreadsheet a backup spreadsheet or an edit-able one
	 */
	private boolean 			isBackup		= false;
	
	/**
	 * ControlData data list
	 */
	private ControlData.List 	theList			= null;

	/**
	 * DataSet
	 */
	private DataSet<?,?> 			theData			= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pReader the spreadsheet reader
	 */
	protected SheetControl(SheetReader<?>	pReader) {
		/* Call super constructor */
		super(pReader, Control);
		
		/* Note whether this is a backup */
		isBackup = (pReader.getType() == SheetType.BACKUP);
		
		/* Access the Lists */
		theData	= pReader.getData();
		theList = theData.getControlData();
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pWriter the spreadsheet writer
	 */
	protected SheetControl(SheetWriter<?> pWriter) {
		/* Call super constructor */
		super(pWriter, Control);
		
		/* Note whether this is a backup */
		isBackup = (pWriter.getType() == SheetType.BACKUP);
				
		/* Access the Control list */
		theList = pWriter.getData().getControlData();
		setDataList(theList);
	}
	
	/**
	 * Load an item from the spreadsheet
	 */
	protected void loadItem() throws Throwable {
		/* If this is a backup */
		if (isBackup) {
			/* Access the IDs */
			int	myID 		= loadInteger(0);
			int	myVersion	= loadInteger(1);

			/* Access the Control Key  */
			int 	myControl		= loadInteger(2);

			/* Add the Control */
			theList.addItem(myID, myVersion, myControl);
		}
		
		/* else this is plain text */
		else {
			/* Access the Version */
			int	myID 		= loadInteger(0);
			int	myVersion	= loadInteger(1);

			/* Add the Control */
			theList.addItem(myID, myVersion);			
		}
	}

	/**
	 *  Insert a item into the spreadsheet
	 *  @param pItem the Item to insert
	 *  @param isBackup is the spreadsheet a backup, or else clear text
	 */
	protected void insertItem(ControlData	pItem) throws Throwable  {
		/* If this is a backup */
		if (isBackup) {
			/* Set the fields */
			writeInteger(0, pItem.getId());
			writeInteger(1, pItem.getDataVersion());
			writeInteger(2, pItem.getControlKey().getId());
		}
		
		/* else just write the data version */
		else {
			writeInteger(0, pItem.getId());
			writeInteger(1, pItem.getDataVersion());
		}
	}

	/**
	 * PreProcess on write
	 */
	protected boolean preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return false;

		/* Write titles */
		writeString(0, ControlData.fieldName(ControlData.FIELD_ID));			
		writeString(1, ControlData.fieldName(ControlData.FIELD_VERS));			
		return true;
	}	

	/**
	 * PostProcess on write
	 */
	protected void postProcessOnWrite() throws Throwable {		
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the three columns as the range */
			nameRange(3);
		}
		
		/* else */
		else {
			/* Set the two columns as the range */
			nameRange(2);

			/* Set the Id column as hidden */
			setHiddenColumn(0);

			/* Set the Version column width */
			setColumnWidth(1, 8);
		}
	}
}
