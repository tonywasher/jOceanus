package uk.co.tolcroft.models.sheets;

import uk.co.tolcroft.models.data.DataKey;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;

public class SheetDataKey extends SheetDataItem<DataKey> {
	/**
	 * SheetName for Keys
	 */
	private static final String Keys	   		= DataKey.listName;

	/**
	 * Is the spreadsheet a backup spreadsheet or an edit-able one
	 */
	private boolean 			isBackup		= false;
	
	/**
	 * DataKey list
	 */
	private DataKey.List 		theList			= null;

	/**
	 * DataSet
	 */
	private DataSet<?> 			theData			= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pInput the spreadsheet reader
	 */
	protected SheetDataKey(SheetReader<?>	pReader) {
		/* Call super constructor */
		super(pReader, Keys);
		
		/* Note whether this is a backup */
		isBackup = (pReader.getType() == SheetType.BACKUP);
		
		/* Access the Lists */
		theData	= pReader.getData();
		theList = theData.getDataKeys();
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pWriter the spreadsheet writer
	 */
	protected SheetDataKey(SheetWriter<?>	pWriter) {
		/* Call super constructor */
		super(pWriter, Keys);
		
		/* Note whether this is a backup */
		isBackup = (pWriter.getType() == SheetType.BACKUP);
				
		/* Access the Control list */
		theList = pWriter.getData().getDataKeys();
		setDataList(theList);
	}
	
	/**
	 * Load an item from the spreadsheet
	 */
	protected void loadItem() throws Throwable {
		/* Access the IDs */
		int	myID 		= loadInteger(0);
		int	myControl	= loadInteger(1);
		int	myKeyType	= loadInteger(2);
		
		/* Access the Binary values  */
		byte[]	myKey			= loadBytes(3);

		/* Add the DataKey */
		theList.addItem(myID, myControl, myKeyType, myKey);
	}

	/**
	 *  Insert a item into the spreadsheet
	 *  @param pItem the Item to insert
	 *  @param isBackup is the spreadsheet a backup, or else clear text
	 */
	protected void insertItem(DataKey	pItem) throws Throwable  {
		/* Set the fields */
		writeInteger(0, pItem.getId());
		writeInteger(1, pItem.getControlKey().getId());	
		writeInteger(2, pItem.getKeyType().getId());	
		writeBytes(3, pItem.getSecuredKeyDef());
	}

	/**
	 * PreProcess on write
	 */
	protected boolean preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return false;

		/* Write titles */
		writeString(0, DataKey.fieldName(DataKey.FIELD_ID));
		writeString(1, DataKey.fieldName(DataKey.FIELD_CONTROL));			
		writeString(2, DataKey.fieldName(DataKey.FIELD_KEYTYPE));			
		writeString(3, DataKey.fieldName(DataKey.FIELD_KEY));			
		return true;
	}	

	/**
	 * PostProcess on write
	 */
	protected void postProcessOnWrite() throws Throwable {		
		/* Set the four columns as the range */
		nameRange(4);
	}
}
