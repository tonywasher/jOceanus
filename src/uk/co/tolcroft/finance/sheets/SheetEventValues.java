package uk.co.tolcroft.finance.sheets;

import uk.co.tolcroft.finance.data.EventValue;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.models.sheets.SheetDataItem;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;

public class SheetEventValues extends SheetDataItem<EventValue> {
	/**
	 * NamedArea for EventValues
	 */
	private static final String EventValues	   	= EventValue.listName;
	
	/**
	 * Is the spreadsheet a backup spreadsheet or an edit-able one
	 */
	private boolean 			isBackup		= false;
	
	/**
	 * EventValue data list
	 */
	private EventValue.List 	theList			= null;

	/**
	 * DataSet
	 */
	private FinanceData			theData			= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pReader the spreadsheet reader
	 */
	protected SheetEventValues(FinanceReader	pReader) {
		/* Call super constructor */
		super(pReader, EventValues);
		
		/* Note whether this is a backup */
		isBackup = (pReader.getType() == SheetType.BACKUP);
				
		/* Access the Lists */
		theData	= pReader.getData();
		theList = theData.getEventValues();
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pWriter the spreadsheet writer
	 */
	protected SheetEventValues(FinanceWriter	pWriter) {
		/* Call super constructor */
		super(pWriter, EventValues);
		
		/* Note whether this is a backup */
		isBackup = (pWriter.getType() == SheetType.BACKUP);
				
		/* Access the TaxYears list */
		theList = pWriter.getData().getEventValues();
		setDataList(theList);
	}
	
	/**
	 * Load an item from the spreadsheet
	 */
	protected void loadItem() throws Throwable {

		/* If this is a backup load */
		if (isBackup) {
			/* Access the IDs */
			int	myID 		= loadInteger(0);
			int	myInfoId	= loadInteger(1);
			int	myEventId	= loadInteger(2);
			int	myValue		= loadInteger(3);
		
			/* Add the Value */
			theList.addItem(myID, myInfoId, myEventId, myValue);
		}
		
		/* else this is a load from an edit-able spreadsheet */
		else {
			/* Access the ID */
			//int	myID 			= loadInteger(0);
			//int	myEventId		= loadInteger(2);
			
			/* Access the Strings */
			//String myInfoType	= loadString(1);
		
			/* Access the value */
			//int 	myValue		= loadInteger(3);
		
			/* Add the Tax Year */
			//theList.addItem(myID,
		}
	}

	/**
	 *  Insert a item into the spreadsheet
	 *  @param pItem the Item to insert
	 *  @param isBackup is the spreadsheet a backup, or else clear text
	 */
	protected void insertItem(EventValue	pItem) throws Throwable  {
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the fields */
			writeInteger(0, pItem.getId());
			writeInteger(1, pItem.getInfoType().getId());
			writeInteger(2, pItem.getEvent().getId());	
			writeInteger(3, pItem.getValue());
		}

		/* else we are creating an edit-able spreadsheet */
		else {
			/* Set the fields */
			writeInteger(0, pItem.getId());
		}
	}

	/**
	 * PreProcess on write
	 */
	protected boolean preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return false;

		/* Write titles */
		//writeString(0, TaxYear.fieldName(TaxYear.FIELD_ID));
		return true;
	}	

	/**
	 * PostProcess on write
	 */
	protected void postProcessOnWrite() throws Throwable {		
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the four columns as the range */
			nameRange(4);
		}

		/* else this is an edit-able spreadsheet */
		else {
			/* Set the four columns as the range */
			//nameRange(4);

			/* Set the Id column as hidden */
			//setHiddenColumn(0);

			/* Set the String column width */
			//setColumnWidth(2, StaticData.NAMELEN);
		}
	}
}
