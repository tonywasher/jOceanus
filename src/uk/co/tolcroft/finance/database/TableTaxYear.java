package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.DatabaseTable;
import uk.co.tolcroft.models.database.TableDefinition;
import uk.co.tolcroft.models.database.TableDefinition.ColumnDefinition;
import uk.co.tolcroft.models.database.TableDefinition.SortOrder;

public class TableTaxYear extends DatabaseTable<TaxYear> {
	/**
	 * The name of the TaxYears table
	 */
	protected final static String TableName 	= TaxYear.listName;

	/**
	 * The table definition
	 */
	private TableDefinition theTableDef;	/* Set during load */

	/**
	 * The TaxYear list
	 */
	private TaxYear.List	theList 			= null;

	/**
	 * The DataSet
	 */
	private FinanceData		theData 			= null;

	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableTaxYear(Database<?>	pDatabase) {
		super(pDatabase, TableName);
	}
	
	/**
	 * Define the table columns (called from within super-constructor)
	 * @param pTableDef the table definition
	 */
	protected void defineTable(TableDefinition	pTableDef) {
		/* Define Standard table */
		super.defineTable(pTableDef);
		theTableDef = pTableDef;

		/* Define sort column variable */
		ColumnDefinition myDateCol;
		
		/* define the columns */
		myDateCol = theTableDef.addDateColumn(TaxYear.FIELD_YEAR, TaxYear.fieldName(TaxYear.FIELD_YEAR));
		theTableDef.addReferenceColumn(TaxYear.FIELD_REGIME, TaxYear.fieldName(TaxYear.FIELD_REGIME), TableTaxRegime.TableName);
		theTableDef.addMoneyColumn(TaxYear.FIELD_ALLOW, TaxYear.fieldName(TaxYear.FIELD_ALLOW));
		theTableDef.addMoneyColumn(TaxYear.FIELD_RENTAL, TaxYear.fieldName(TaxYear.FIELD_RENTAL));
		theTableDef.addMoneyColumn(TaxYear.FIELD_LOBAND, TaxYear.fieldName(TaxYear.FIELD_LOBAND));
		theTableDef.addMoneyColumn(TaxYear.FIELD_BSBAND, TaxYear.fieldName(TaxYear.FIELD_BSBAND));
		theTableDef.addMoneyColumn(TaxYear.FIELD_LOAGAL, TaxYear.fieldName(TaxYear.FIELD_LOAGAL));
		theTableDef.addMoneyColumn(TaxYear.FIELD_HIAGAL, TaxYear.fieldName(TaxYear.FIELD_HIAGAL));
		theTableDef.addMoneyColumn(TaxYear.FIELD_AGELMT, TaxYear.fieldName(TaxYear.FIELD_AGELMT));
		theTableDef.addNullMoneyColumn(TaxYear.FIELD_ADDLMT, TaxYear.fieldName(TaxYear.FIELD_ADDLMT));
		theTableDef.addRateColumn(TaxYear.FIELD_LOTAX, TaxYear.fieldName(TaxYear.FIELD_LOTAX));
		theTableDef.addRateColumn(TaxYear.FIELD_BASTAX, TaxYear.fieldName(TaxYear.FIELD_BASTAX));
		theTableDef.addRateColumn(TaxYear.FIELD_HITAX, TaxYear.fieldName(TaxYear.FIELD_HITAX));
		theTableDef.addRateColumn(TaxYear.FIELD_INTTAX, TaxYear.fieldName(TaxYear.FIELD_INTTAX));
		theTableDef.addRateColumn(TaxYear.FIELD_DIVTAX, TaxYear.fieldName(TaxYear.FIELD_DIVTAX));
		theTableDef.addRateColumn(TaxYear.FIELD_HDVTAX, TaxYear.fieldName(TaxYear.FIELD_HDVTAX));
		theTableDef.addNullRateColumn(TaxYear.FIELD_ADDTAX, TaxYear.fieldName(TaxYear.FIELD_ADDTAX));
		theTableDef.addNullRateColumn(TaxYear.FIELD_ADVTAX, TaxYear.fieldName(TaxYear.FIELD_ADVTAX));
		theTableDef.addNullMoneyColumn(TaxYear.FIELD_ADDBDY, TaxYear.fieldName(TaxYear.FIELD_ADDBDY));
		theTableDef.addNullMoneyColumn(TaxYear.FIELD_CAPALW, TaxYear.fieldName(TaxYear.FIELD_CAPALW));
		theTableDef.addNullRateColumn(TaxYear.FIELD_CAPTAX, TaxYear.fieldName(TaxYear.FIELD_CAPTAX));
		theTableDef.addNullRateColumn(TaxYear.FIELD_HCPTAX, TaxYear.fieldName(TaxYear.FIELD_HCPTAX));
		
		/* Declare the sort order */
		myDateCol.setSortOrder(SortOrder.ASCENDING);
	}
	
	/* Declare DataSet */
	protected void declareData(DataSet<?> pData) {
		FinanceData myData = (FinanceData)pData;
		theData = myData;
		theList = myData.getTaxYears();
		setList(theList);
	}

	/**
	 * postProcess on Load
	 */
	protected void postProcessOnLoad() throws Exception {
		theData.calculateDateRange();
	}
	
	/* Load the tax year */
	public void loadItem(int pId) throws Exception {
		java.util.Date  myYear;
		String  		myAllowance;
		String  		myRentalAllow;
		String  		myLoBand;
		String  		myBasicBand;
		String  		myLoAgeAllow;
		String  		myHiAgeAllow;
		String  		myCapitalAllow;
		String  		myAgeAllowLimit;
		String  		myAddAllowLimit;
		String  		myAddIncBound;
		String  		myLoTaxRate;
		String  		myBasicTaxRate;
		String  		myHiTaxRate;
		String  		myIntTaxRate;
		String  		myDivTaxRate;
		String  		myHiDivTaxRate;
		String  		myAddTaxRate;
		String  		myAddDivTaxRate;
		String  		myCapTaxRate;
		String  		myHiCapTaxRate;
		int		  		myRegime;
		
		/* Get the various fields */
		myYear          = theTableDef.getDateValue(TaxYear.FIELD_YEAR);
		myRegime        = theTableDef.getIntegerValue(TaxYear.FIELD_REGIME);
		myAllowance     = theTableDef.getStringValue(TaxYear.FIELD_ALLOW);
		myRentalAllow   = theTableDef.getStringValue(TaxYear.FIELD_RENTAL);
		myLoBand        = theTableDef.getStringValue(TaxYear.FIELD_LOBAND);
		myBasicBand     = theTableDef.getStringValue(TaxYear.FIELD_BSBAND);
		myLoAgeAllow    = theTableDef.getStringValue(TaxYear.FIELD_LOAGAL);
		myHiAgeAllow    = theTableDef.getStringValue(TaxYear.FIELD_HIAGAL);
		myAgeAllowLimit = theTableDef.getStringValue(TaxYear.FIELD_AGELMT);
		myAddAllowLimit = theTableDef.getStringValue(TaxYear.FIELD_ADDLMT);
		myLoTaxRate     = theTableDef.getStringValue(TaxYear.FIELD_LOTAX);
		myBasicTaxRate  = theTableDef.getStringValue(TaxYear.FIELD_BASTAX);
		myHiTaxRate     = theTableDef.getStringValue(TaxYear.FIELD_HITAX);
		myIntTaxRate    = theTableDef.getStringValue(TaxYear.FIELD_INTTAX);
		myDivTaxRate    = theTableDef.getStringValue(TaxYear.FIELD_DIVTAX);
		myHiDivTaxRate  = theTableDef.getStringValue(TaxYear.FIELD_HDVTAX);
		myAddTaxRate    = theTableDef.getStringValue(TaxYear.FIELD_ADDTAX);
		myAddDivTaxRate = theTableDef.getStringValue(TaxYear.FIELD_ADVTAX);
		myAddIncBound 	= theTableDef.getStringValue(TaxYear.FIELD_ADDBDY);
		myCapitalAllow  = theTableDef.getStringValue(TaxYear.FIELD_CAPALW);
		myCapTaxRate    = theTableDef.getStringValue(TaxYear.FIELD_CAPTAX);
		myHiCapTaxRate  = theTableDef.getStringValue(TaxYear.FIELD_HCPTAX);
	
		/* Add into the list */
		theList.addItem(pId, myRegime, myYear, 
		              	myAllowance, myRentalAllow,
		              	myLoAgeAllow, myHiAgeAllow,
		              	myCapitalAllow,
		              	myAgeAllowLimit, myAddAllowLimit,
		              	myLoBand, myBasicBand,
		              	myAddIncBound,
		              	myLoTaxRate, myBasicTaxRate, 
		              	myHiTaxRate, myIntTaxRate,
		              	myDivTaxRate, myHiDivTaxRate,
		              	myAddTaxRate, myAddDivTaxRate,
		              	myCapTaxRate, myHiCapTaxRate);
	}
	
	/* Set a field value */
	protected void setFieldValue(TaxYear	pItem, int iField) throws Exception  {
		/* Switch on field id */
		switch (iField) {
			case TaxYear.FIELD_YEAR:	theTableDef.setDateValue(iField, pItem.getDate());					break;
			case TaxYear.FIELD_REGIME:	theTableDef.setIntegerValue(iField, pItem.getTaxRegime().getId());	break;
			case TaxYear.FIELD_ALLOW:	theTableDef.setMoneyValue(iField, pItem.getAllowance());			break;
			case TaxYear.FIELD_RENTAL:	theTableDef.setMoneyValue(iField, pItem.getRentalAllowance());		break;
			case TaxYear.FIELD_LOBAND:	theTableDef.setMoneyValue(iField, pItem.getLoBand());				break;
			case TaxYear.FIELD_BSBAND:	theTableDef.setMoneyValue(iField, pItem.getBasicBand());			break;
			case TaxYear.FIELD_LOAGAL:	theTableDef.setMoneyValue(iField, pItem.getLoAgeAllow());			break;
			case TaxYear.FIELD_HIAGAL:	theTableDef.setMoneyValue(iField, pItem.getHiAgeAllow());			break;
			case TaxYear.FIELD_AGELMT:	theTableDef.setMoneyValue(iField, pItem.getAgeAllowLimit());		break;
			case TaxYear.FIELD_ADDLMT:	theTableDef.setMoneyValue(iField, pItem.getAddAllowLimit());		break;
			case TaxYear.FIELD_LOTAX:	theTableDef.setRateValue(iField, pItem.getLoTaxRate());				break;
			case TaxYear.FIELD_BASTAX:	theTableDef.setRateValue(iField, pItem.getBasicTaxRate());			break;
			case TaxYear.FIELD_HITAX:	theTableDef.setRateValue(iField, pItem.getHiTaxRate());				break;
			case TaxYear.FIELD_INTTAX:	theTableDef.setRateValue(iField, pItem.getIntTaxRate());			break;
			case TaxYear.FIELD_DIVTAX:	theTableDef.setRateValue(iField, pItem.getDivTaxRate());			break;
			case TaxYear.FIELD_HDVTAX:	theTableDef.setRateValue(iField, pItem.getHiDivTaxRate());			break;
			case TaxYear.FIELD_ADDTAX:	theTableDef.setRateValue(iField, pItem.getAddTaxRate());			break;
			case TaxYear.FIELD_ADVTAX:	theTableDef.setRateValue(iField, pItem.getAddDivTaxRate());			break;
			case TaxYear.FIELD_ADDBDY:	theTableDef.setMoneyValue(iField, pItem.getAddIncBound());			break;
			case TaxYear.FIELD_CAPALW:	theTableDef.setMoneyValue(iField, pItem.getCapitalAllow());			break;
			case TaxYear.FIELD_CAPTAX:	theTableDef.setRateValue(iField, pItem.getCapTaxRate());			break;
			case TaxYear.FIELD_HCPTAX:	theTableDef.setRateValue(iField, pItem.getHiCapTaxRate());			break;
			default:					super.setFieldValue(pItem, iField);									break;
		}
	}
}
