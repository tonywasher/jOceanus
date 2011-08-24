package uk.co.tolcroft.models.sheets;

import uk.co.tolcroft.finance.data.StaticClass;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.data.StaticData;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;

public abstract class SheetStaticData <T extends StaticData<T,?>> extends SheetDataItem<T> {

	/* Load the Static Data */
	protected  abstract void loadEncryptedItem(int pId, int pControlId, int pClassId, byte[] pName, byte[] pDesc) throws Exception;
	protected  abstract void loadClearTextItem(int pId, int pClassId, String pName, String pDesc) throws Exception;

	/**
	 * Is the spreadsheet a backup spreadsheet or an edit-able one
	 */
	private boolean isBackup	= false;
	
	/**
	 * The name of the items
	 */
	private String 	theNames	= null;
	
	/**
	 * Constructor for loading a spreadsheet
	 *  @param pReader the spreadsheet reader
	 *  @param pRange 	 the range to load
	 */
	protected SheetStaticData(SheetReader<?>	pReader,
							  String			pRange) {
		/* Call super constructor */
		super(pReader, pRange);
		
		/* Note whether this is a backup */
		isBackup = (pReader.getType() == SheetType.BACKUP);
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pOutput the spreadsheet writer
	 *  @param pRange  the range to create
	 *  @param pNames  the name range to create
	 */
	protected SheetStaticData(SheetWriter<?>	pWriter,
							  String			pRange,
							  String			pNames) {
		/* Call super constructor */
		super(pWriter, pRange);
		
		/* Record the names */
		theNames = pNames;
		
		/* Note whether this is a backup */
		isBackup = (pWriter.getType() == SheetType.BACKUP);
	}
	
	/**
	 * Load an item from the spreadsheet
	 */
	protected void loadItem() throws Throwable {
		/* If this is a backup load */
		if (isBackup) {
			/* Access the IDs */
			int myID 		= loadInteger(0);
			int myControlId	= loadInteger(1);
			int myClassId	= loadInteger(2);
		
			/* Access the name and description bytes */
			byte[] myNameBytes = loadBytes(3);
			byte[] myDescBytes = loadBytes(4);
		
			/* Load the item */
			loadEncryptedItem(myID, myControlId, myClassId, myNameBytes, myDescBytes);
		}
		
		/* else this is a load from an edit-able spreadsheet */
		else {
			/* Access the IDs */
			int myID 		= loadInteger(0);
			int myClassId	= loadInteger(1);
		
			/* Access the name and description bytes */
			String myName 	= loadString(2);
			String myDesc 	= loadString(3);
		
			/* Load the item */
			loadClearTextItem(myID, myClassId, myName, myDesc);
		}
	}

	/**
	 *  Insert a item into the spreadsheet
	 *  @param pItem the Item to insert
	 *  @param isBackup is the spreadsheet a backup, or else clear text
	 */
	protected void insertItem(T	pItem) throws Throwable  {
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the fields */
			writeInteger(0, pItem.getId());
			writeInteger(1, pItem.getControlKey().getId());				
			writeInteger(2, pItem.getStaticClassId());				
			writeBytes(3, pItem.getNameBytes());
			writeBytes(4, pItem.getDescBytes());
		}

		/* else we are creating an edit-able spreadsheet */
		else {
			/* Set the fields */
			writeInteger(0, pItem.getId());
			writeInteger(1, pItem.getStaticClassId());				
			writeString(2, pItem.getName());
			writeString(3, pItem.getDesc());			
		}
	}

	/**
	 * PreProcess on write 
	 */
	protected boolean preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return false;

		/* Write titles */
		writeString(0, StaticData.fieldName(StaticData.FIELD_ID));
		writeString(1, StaticData.fieldName(StaticData.FIELD_CLASSID));
		writeString(2, StaticData.fieldName(StaticData.FIELD_NAME));
		writeString(3, StaticData.fieldName(StaticData.FIELD_DESC));			
		return true;
	}	

	/**
	 * PostProcess on Write
	 */
	protected void postProcessOnWrite() throws Throwable {		
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the five columns as the range */
			nameRange(5);
		}

		/* else this is an edit-able spreadsheet */
		else {
			/* Set the four columns as the range */
			nameRange(4);

			/* Set the Id column as hidden */
			setHiddenColumn(0);

			/* Set the name column width and range */
			nameColumnRange(2, theNames);
			setColumnWidth(2, StaticClass.NAMELEN);
			
			/* Set description column width */
			setColumnWidth(3, StaticClass.DESCLEN);
		}
	}	
}
