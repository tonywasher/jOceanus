package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.StaticData;
import uk.co.tolcroft.finance.data.StaticClass.TaxRegClass;

public class TaxRegime extends StaticData<TaxRegime, TaxRegClass> {
	/**
	 * The name of the object
	 */
	public static final String objName = "TaxRegime";

	/**
	 * The name of the object
	 */
	public static final String listName = objName + "s";

	/**
	 * Return the TaxRegime class of the TaxRegime
	 * @return the class
	 */
	public TaxRegClass getRegime()         { return super.getStaticClass(); }
	
	/* Linking methods */
	public TaxRegime getBase() { return (TaxRegime)super.getBase(); }

	/**
	 * Obtain the type of the item
	 * @return the type of the item
	 */
	public String itemType() { return objName; }
	
	/**
	 * Construct a copy of a TaxRegime.
	 * @param pList	The list to associate the TaxRegime with
	 * @param pTaxRegime The TaxRegime to copy 
	 */
	protected TaxRegime(List  		pList,
			            TaxRegime   pTaxRegime) { 
		super(pList, pTaxRegime);
	}
	
	/**
	 * Construct a standard TaxRegime on load
	 * 
	 * @param pList	The list to associate the TaxRegime with
	 * @param sName Name of TaxRegime
	 */
	private TaxRegime(List 	 pList,
			          String sName) throws Exception {
		super(pList, sName);
	}
	
	/**
	 * Construct a tax regime on load
	 * @param pList	The list to associate the TaxRegime with
	 * @param uId the id of the new item
	 * @param isEnabled is the regime enabled
	 * @param pName Name of Tax Regime
	 * @param pDesc Description of Tax Regime
	 */
	private TaxRegime(List 		pList,
					  int		uId,
			          boolean	isEnabled,
			          String	pName,
			          String	pDesc) throws Exception {
		super(pList, uId, isEnabled, pName, pDesc);
	}
	
	/**
	 * Construct a standard TaxRegime on load
	 * @param pList	The list to associate the TaxRegime with
	 * @param uId   ID of TaxRegime
	 * @param uControlId the control id of the new item
	 * @param isEnabled is the regime enabled
	 * @param pName Encrypted Name of TaxRegime
	 * @param pDesc Encrypted Description of TaxRegime
	 */
	private TaxRegime(List 		pList,
			      	  int		uId,
			      	  int		uControlId,
			      	  boolean	isEnabled,
			      	  byte[]	pName,
			      	  byte[]	pDesc) throws Exception {
		super(pList, uId, uControlId, isEnabled, pName, pDesc);
	}

	/**
	 * Determine whether this tax regime supports a Low Salary Band
	 * @return <code>true/false</code>
	 */
	public  boolean         hasLoSalaryBand() {
		switch (getStaticClass()) {
			case ARCHIVE: 	return true;
			case STANDARD: 	return true;
			default: 		return false;
		}
	}
	
	/**
	 * Determine whether this tax regime treats capital gains as standard income
	 * @return <code>true/false</code>
	 */
	public  boolean         hasCapitalGainsAsIncome() {
		switch (getStaticClass()) {
			case STANDARD: 	return true;
			default: 		return false;
		}
	}
	
	/**
	 * Determine whether this tax regime supports an additional taxation band
	 * @return <code>true/false</code>
	 */
	public  boolean         hasAdditionalTaxBand() {
		switch (getStaticClass()) {
			case ADDITIONALBAND: 	return true;
			default: 				return false;
		}
	}

	/**
	 * Represents a list of {@link TaxRegime} objects. 
	 */
	public static class List  extends StaticList<List, TaxRegime, TaxRegClass> {
		protected Class<TaxRegClass> getEnumClass() { return TaxRegClass.class; }
		
	 	/** 
	 	 * Construct an empty CORE tax regime list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(FinanceData pData) { 
			super(List.class, TaxRegime.class, pData, ListStyle.CORE); }

		/**
		 * Constructor for a cloned List
		 * @param pSource the source List
		 */
		private List(List pSource) { 
			super(pSource);
		}
		
		/**
		 * Construct an update extract for the List.
		 * @return the update Extract
		 */
		private List getExtractList(ListStyle pStyle) {
			/* Build an empty Extract List */
			List myList = new List(this);
			
			/* Obtain underlying updates */
			myList.populateList(pStyle);
			
			/* Return the list */
			return myList;
		}

		/* Obtain extract lists. */
		public List getUpdateList() { return getExtractList(ListStyle.UPDATE); }
		public List getEditList() 	{ return getExtractList(ListStyle.EDIT); }
		public List getShallowCopy() 	{ return getExtractList(ListStyle.COPY); }
		public List getDeepCopy(DataSet<?,?> pDataSet)	{ 
			/* Build an empty Extract List */
			List myList = new List(this);
			myList.setData(pDataSet);
			
			/* Obtain underlying clones */
			myList.populateList(ListStyle.CLONE);
			myList.setStyle(ListStyle.CORE);
			
			/* Return the list */
			return myList;
		}

		/** 
		 * Construct a difference ControlData list
		 * @param pNew the new ControlData list 
		 * @param pOld the old ControlData list 
		 */
		protected List getDifferences(List pOld) { 
			/* Build an empty Difference List */
			List myList = new List(this);
			
			/* Calculate the differences */
			myList.getDifferenceList(this, pOld);
			
			/* Return the list */
			return myList;
		}

		/**
		 * Add a new item to the list
		 * @param pItem item to be added
		 * @return the newly added item
		 */
		public TaxRegime addNewItem(DataItem<?> pItem) {
			TaxRegime myRegime = new TaxRegime(this, (TaxRegime)pItem);
			add(myRegime);
			return myRegime;
		}
	
		/**
		 * Create a new empty element in the edit list (null-operation)
		 * @return the newly added item
		 */
		public TaxRegime addNewItem() { return null; }
			
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }
				
		/**
		 * Add a TaxRegime
		 * @param pTaxRegime the Name of the tax regime
		 */ 
		public void addItem(String pTaxRegime) throws Exception {
			TaxRegime     myTaxRegime;
			
			/* Create a new tax regime */
			myTaxRegime = new TaxRegime(this, pTaxRegime);
				
			/* Check that this TaxRegimeId has not been previously added */
			if (!isIdUnique(myTaxRegime.getId())) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTaxRegime,
			  			            "Duplicate TaxRegimeId");
				 
			/* Check that this TaxRegime has not been previously added */
			if (searchFor(pTaxRegime) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTaxRegime,
			                        "Duplicate TaxRegime");
								
			/* Add the TaxRegime to the list */
			add(myTaxRegime);		
		}			

		/**
		 * Add a TaxRegime to the list
		 * @param uId the id of the new item
		 * @param isEnabled is the regime enabled
		 * @param pTaxRegime the Name of the tax regime
		 * @param pDesc the Description of the tax regime
		 */ 
		public void addItem(int	   	uId,
							boolean	isEnabled,
				            String 	pTaxRegime,
				            String 	pDesc) throws Exception {
			TaxRegime myTaxReg;
				
			/* Create a new Tax Regime */
			myTaxReg = new TaxRegime(this, uId, isEnabled, pTaxRegime, pDesc);
				
			/* Check that this TaxRegimeId has not been previously added */
			if (!isIdUnique(myTaxReg.getId())) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTaxReg,
			  			            "Duplicate TaxRegimeId");
				 
			/* Check that this Regime has not been previously added */
			if (searchFor(myTaxReg.getName()) != null) 
				throw new Exception(ExceptionClass.DATA,
	   					  			myTaxReg,
			  			            "Duplicate Tax Regime");
				
			/* Add the Tax Regime to the list */
			add(myTaxReg);
		}	

		/**
		 * Add a TaxRegime
		 * @param uId the Id of the tax regime
		 * @param uControlId the control id of the new item
		 * @param isEnabled is the regime enabled
		 * @param pTaxRegime the Encrypted Name of the tax regime
		 * @param pDesc the Encrypted Description of the tax regime
		 */ 
		public void addItem(int    	uId,
							int	   	uControlId,
							boolean	isEnabled,
				            byte[] 	pTaxRegime,
				            byte[] 	pDesc) throws Exception {
			TaxRegime     myTaxRegime;
			
			/* Create a new tax regime */
			myTaxRegime = new TaxRegime(this, uId, uControlId, isEnabled, pTaxRegime, pDesc);
				
			/* Check that this TaxRegimeId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTaxRegime,
			  			            "Duplicate TaxRegimeId");
				 
			/* Check that this TaxRegime has not been previously added */
			if (searchFor(myTaxRegime.getName()) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTaxRegime,
			                        "Duplicate TaxRegime");
				
			/* Add the TaxRegime to the list */
			add(myTaxRegime);		
		}			
	}
}
