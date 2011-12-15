package uk.co.tolcroft.models.sheets;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.data.StaticData;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;

public abstract class SheetStaticData <T extends StaticData<T,?>> extends SheetDataItem<T> {

	/* Load the Static Data */
	protected  abstract void loadEncryptedItem(int pId, int pControlId, boolean isEnabled, int iOrder, byte[] pName, byte[] pDesc) throws Exception;
	protected  abstract void loadClearTextItem(int pId, boolean isEnabled, int iOrder, String pName, String pDesc) throws Exception;

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
			int myID 			= loadInteger(0);
			int myControlId		= loadInteger(1);
			int myOrder			= loadInteger(2);
			boolean myEnabled	= loadBoolean(3);
		
			/* Access the name and description bytes */
			byte[] myNameBytes = loadBytes(4);
			byte[] myDescBytes = loadBytes(5);
		
			/* Load the item */
			loadEncryptedItem(myID, myControlId, myEnabled, myOrder, myNameBytes, myDescBytes);
		}
		
		/* else this is a load from an edit-able spreadsheet */
		else {
			/* Access the IDs */
			int myID 			= loadInteger(0);
			int myOrder			= loadInteger(1);
			boolean myEnabled	= loadBoolean(2);
		
			/* Access the name and description bytes */
			String myName 	= loadString(3);
			String myDesc 	= loadString(4);
		
			/* Load the item */
			loadClearTextItem(myID, myEnabled, myOrder, myName, myDesc);
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
			writeInteger(2, pItem.getOrder());				
			writeBoolean(3, pItem.getEnabled());				
			writeBytes(4, pItem.getNameBytes());
			writeBytes(5, pItem.getDescBytes());
		}

		/* else we are creating an edit-able spreadsheet */
		else {
			/* Set the fields */
			writeInteger(0, pItem.getId());
			writeInteger(1, pItem.getOrder());				
			writeBoolean(2, pItem.getEnabled());				
			writeString(3, pItem.getName());
			writeString(4, pItem.getDesc());			
		}
	}

	@Override
	protected void preProcessOnWrite() throws Throwable {		
		/* Ignore if this is a backup */
		if (isBackup) return;

		/* Create a new row */
		newRow();
		
		/* Write titles */
		writeHeader(0, StaticData.fieldName(StaticData.FIELD_ID));
		writeHeader(1, StaticData.fieldName(StaticData.FIELD_ORDER));
		writeHeader(2, StaticData.fieldName(StaticData.FIELD_ENABLED));
		writeHeader(3, StaticData.fieldName(StaticData.FIELD_NAME));
		writeHeader(4, StaticData.fieldName(StaticData.FIELD_DESC));			
			
		/* Adjust for Header */
		adjustForHeader();
	}	

	@Override
	protected void postProcessOnWrite() throws Throwable {		
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the six columns as the range */
			nameRange(6);
		}

		/* else this is an edit-able spreadsheet */
		else {
			/* Set the five columns as the range */
			nameRange(5);

			/* Set the Id column as hidden */
			setHiddenColumn(0);
			
			/* Set default column types */
			setIntegerColumn(0);
			setIntegerColumn(1);
			setBooleanColumn(2);

			/* Set the name column width and range */
			nameColumnRange(3, theNames);
			setColumnWidth(3, StaticData.NAMELEN);
			
			/* Set description column width */
			setColumnWidth(4, StaticData.DESCLEN);
		}
	}	
}
