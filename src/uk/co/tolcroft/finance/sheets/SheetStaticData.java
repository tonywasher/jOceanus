package uk.co.tolcroft.finance.sheets;

import uk.co.tolcroft.finance.data.StaticClass;
import uk.co.tolcroft.finance.sheets.SpreadSheet.InputSheet;
import uk.co.tolcroft.finance.sheets.SpreadSheet.OutputSheet;
import uk.co.tolcroft.finance.sheets.SpreadSheet.SheetType;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.StaticData;

public abstract class SheetStaticData <T extends StaticData<T,?>> extends SheetDataItem<T> {

	/* Load the Static Data */
	protected  abstract void loadEncryptedItem(int pId, int pControlId, int pClassId, byte[] pName, byte[] pDesc) throws Exception;
	protected  abstract void loadClearTextItem(int pClassId, String pName, String pDesc) throws Exception;

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
	 *  @param pInput the input spreadsheet
	 *  @param pRange 	 the range to load
	 */
	protected SheetStaticData(InputSheet	pInput,
							  String		pRange) {
		/* Call super constructor */
		super(pInput, pRange);
		
		/* Note whether this is a backup */
		isBackup = (pInput.getType() == SheetType.BACKUP);
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pOutput the output spreadsheet
	 *  @param pRange  the range to create
	 *  @param pNames  the name range to create
	 */
	protected SheetStaticData(OutputSheet	pOutput,
							  String		pRange,
							  String		pNames) {
		/* Call super constructor */
		super(pOutput, pRange);
		
		/* Record the names */
		theNames = pNames;
		
		/* Note whether this is a backup */
		isBackup = (pOutput.getType() == SheetType.BACKUP);
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
			int myClassId	= loadInteger(0);
		
			/* Access the name and description bytes */
			String myName 	= loadString(1);
			String myDesc 	= loadString(2);
		
			/* Load the item */
			loadClearTextItem(myClassId, myName, myDesc);
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
			writeInteger(0, pItem.getStaticClassId());				
			writeString(1, pItem.getName());
			writeString(2, pItem.getDesc());			
		}
	}

	/**
	 * PreProcess on write 
	 */
	protected boolean preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return false;

		/* Write titles */
		writeString(0, StaticData.fieldName(StaticData.FIELD_CLASSID));
		writeString(1, StaticData.fieldName(StaticData.FIELD_NAME));
		writeString(2, StaticData.fieldName(StaticData.FIELD_DESC));			
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
			/* Set the three columns as the range */
			nameRange(3);

			/* Set the name column width and range */
			nameColumnRange(1, theNames);
			setColumnWidth(1, StaticClass.NAMELEN);
			
			/* Set column 2 width */
			setColumnWidth(2, StaticClass.DESCLEN);
		}
	}	
}
