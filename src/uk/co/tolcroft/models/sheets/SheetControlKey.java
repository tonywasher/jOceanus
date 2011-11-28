package uk.co.tolcroft.models.sheets;

import uk.co.tolcroft.models.data.ControlKey;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;

public class SheetControlKey extends SheetDataItem<ControlKey> {
	/**
	 * SheetName for Keys
	 */
	private static final String Keys	   		= ControlKey.listName;

	/**
	 * Is the spreadsheet a backup spreadsheet or an edit-able one
	 */
	private boolean 			isBackup		= false;
	
	/**
	 * ControlKey data list
	 */
	private ControlKey.List 	theList			= null;

	/**
	 * DataSet
	 */
	private DataSet<?> 			theData			= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pReader the spreadsheet reader
	 */
	protected SheetControlKey(SheetReader<?>	pReader) {
		/* Call super constructor */
		super(pReader, Keys);
		
		/* Note whether this is a backup */
		isBackup = (pReader.getType() == SheetType.BACKUP);
		
		/* Access the Lists */
		theData	= pReader.getData();
		theList = theData.getControlKeys();
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pWriter the Spreadsheet writer
	 */
	protected SheetControlKey(SheetWriter<?>	pWriter) {
		/* Call super constructor */
		super(pWriter, Keys);
		
		/* Note whether this is a backup */
		isBackup = (pWriter.getType() == SheetType.BACKUP);
				
		/* Access the Control list */
		theList = pWriter.getData().getControlKeys();
		setDataList(theList);
	}
	
	/**
	 * Load an item from the spreadsheet
	 */
	protected void loadItem() throws Throwable {
		/* Access the IDs */
		int	myID 		= loadInteger(0);
		int	myTypeID 	= loadInteger(1);
		int	mySteps 	= loadInteger(2);
		
		/* Access the binary values  */
		byte[] 	myHash		= loadBytes(3);
		byte[] 	myPublic	= loadBytes(4);
		byte[] 	myPrivate	= loadBytes(5);

		/* Add the Control */
		theList.addItem(myID, myTypeID, mySteps, myHash, myPublic, myPrivate);
	}

	/**
	 *  Insert a item into the spreadsheet
	 *  @param pItem the Item to insert
	 *  @param isBackup is the spreadsheet a backup, or else clear text
	 */
	protected void insertItem(ControlKey	pItem) throws Throwable  {
		/* Set the fields */
		writeInteger(0, pItem.getId());
		writeInteger(1, pItem.getKeyMode().getMode());
		writeInteger(2, pItem.getNumSteps());
		writeBytes(3, pItem.getPasswordHash());
		writeBytes(4, pItem.getPublicKey());
		writeBytes(5, pItem.getPrivateKey());
	}

	/**
	 * PreProcess on write
	 */
	protected boolean preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return false;

		/* Write titles */
		writeString(0, ControlKey.fieldName(ControlKey.FIELD_ID));
		writeString(1, ControlKey.fieldName(ControlKey.FIELD_KEYMODE));			
		writeString(2, ControlKey.fieldName(ControlKey.FIELD_NUMSTEPS));			
		writeString(3, ControlKey.fieldName(ControlKey.FIELD_PASSHASH));			
		writeString(4, ControlKey.fieldName(ControlKey.FIELD_PUBLICKEY));			
		writeString(5, ControlKey.fieldName(ControlKey.FIELD_PRIVATEKEY));			
		return true;
	}	

	/**
	 * PostProcess on write
	 */
	protected void postProcessOnWrite() throws Throwable {		
		/* Set the six columns as the range */
		nameRange(6);
	}
}
