package uk.co.tolcroft.models.sheets;

import uk.co.tolcroft.models.data.DataKey;
import uk.co.tolcroft.models.data.DataSet;

public class SheetDataKey extends SheetDataItem<DataKey> {
	/**
	 * SheetName for Keys
	 */
	private static final String Keys	   		= DataKey.listName;

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
		byte[]	myKey	= loadBytes(3);

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

	@Override
	protected void preProcessOnWrite() throws Throwable {}		

	@Override
	protected void postProcessOnWrite() throws Throwable {		
		/* Set the four columns as the range */
		nameRange(4);
	}
}
