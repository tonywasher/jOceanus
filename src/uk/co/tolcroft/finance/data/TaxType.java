package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.data.StaticData;
import uk.co.tolcroft.finance.data.StaticClass.TaxClass;

public class TaxType extends StaticData<TaxType, TaxClass> {
	/**
	 * The name of the object
	 */
	public static final String objName = "TaxClass";

	/**
	 * The name of the object
	 */
	public static final String listName = objName + "es";

	/**
	 * Return the Tax class of the Tax Type
	 * @return the class
	 */
	public TaxClass 	getTaxClass()     { return super.getStaticClass(); }

	/* Linking methods */
	public TaxType getBase() { return (TaxType)super.getBase(); }

	/**
	 * Obtain the type of the item
	 * @return the type of the item
	 */
	public String itemType() { return objName; }
	
	/**
	 * Obtain the number of fields for an item
	 * @return the number of fields
	 */
	public int	numFields() {return NUMFIELDS; }
	
	/**
	 * Construct a copy of a Tax Type.
	 * 
	 * @param pList	The list to associate the Tax Type with
	 * @param pTaxType The Tax Type to copy 
	 */
	protected TaxType(List pList, TaxType pTaxType) { 
		super(pList, pTaxType);
	}
	
	/**
	 * Construct a standard Tax type on load
	 * 
	 * @param pList	The list to associate the Tax Type with
	 * @param sName Name of Tax Type
	 */
	private TaxType(List 	pList,
			        String  sName) throws Exception {
		super(pList, sName);
	}
	
	/**
	 * Construct a standard tax type on load
	 * @param pList	The list to associate the Tax Type with
	 * @param uId   ID of TaxType
	 * @param uClassId the class id of the new item
	 * @param pName Name of Tax Type
	 * @param pDesc Description of Tax Type
	 */
	private TaxType(List 	pList,
					int		uId,
			        int		uClassId,
			        String	pName,
			        String	pDesc) throws Exception {
		super(pList, uId, uClassId, pName, pDesc);
	}
	
	/**
	 * Construct a standard TaxType on load
	 * @param pList	The list to associate the TaxType with
	 * @param uId   ID of TaxType
	 * @param uControlId the control id of the new item
	 * @param uClassId the class id of the new item
	 * @param sName Encrypted Name of TaxType
	 * @param pDesc Encrypted Description of TaxType
	 */
	private TaxType(List 	pList,
			      	int		uId,
			      	int		uControlId,
			      	int		uClassId,
			      	byte[]	sName,
			      	byte[]	pDesc) throws Exception {
		super(pList, uId, uControlId, uClassId, sName, pDesc);
	}

	/**
	 * Determine whether we should add tax credits to the total
	 * 
	 * @return <code>true</code> if we should add tax credits to the total, <code>false</code> otherwise.
	 */
	public boolean hasTaxCredits() { 
		switch (getTaxClass()) {
			case GROSSSALARY:
			case GROSSINTEREST:
			case GROSSDIVIDEND:
			case GROSSUTDIVS:
			case GROSSTAXGAINS:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Determine whether we this is the tax paid bucket
	 * 
	 * @return <code>true</code> if we should add tax credits to the total, <code>false</code> otherwise.
	 */
	public boolean isTaxPaid() { 
		switch (getTaxClass()) {
			case TAXPAID:
				return true;
			default:
				return false;
		}
	}

	/**
	 * Represents a list of {@link TaxType} objects. 
	 */
	public static class List extends StaticList<TaxType, TaxClass> {
		protected Class<TaxClass> getEnumClass() { return TaxClass.class; }

		/** 
	 	 * Construct an empty CORE tax type list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(FinanceData pData) { 
			super(TaxType.class, pData, ListStyle.CORE); }

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
		public List getClonedList() { return getExtractList(ListStyle.CORE); }

		/** 
		 * Construct a difference ControlData list
		 * @param pNew the new ControlData list 
		 * @param pOld the old ControlData list 
		 */
		protected List getDifferences(DataList<TaxType> pOld) { 
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
		public TaxType addNewItem(DataItem<?> pItem) {
			TaxType myType = new TaxType(this, (TaxType)pItem);
			add(myType);
			return myType;
		}
	
		/**
		 * Create a new empty element in the edit list (null-operation)
		 * @return the newly added item
		 */
		public TaxType addNewItem() { return null; }
			
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }
		
		/**
		 * Add a TaxType
		 * @param pTaxType the Name of the tax type
		 */ 
		public void addItem(String 	pTaxType) throws Exception {
			TaxType      myTaxType;
			
			/* Create a new Tax Type */
			myTaxType = new TaxType(this, pTaxType);
			
			/* Check that this TaxType has not been previously added */
			if (searchFor(pTaxType) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTaxType,
			                        "Duplicate Tax Type");
				
			/* Check that this ClassId has not been previously added */
			if (searchForEnum(myTaxType.getStaticClassId()) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTaxType,
			                        "Duplicate TaxClass");
				
			/* Add the Tax Type to the list */
			add(myTaxType);
		}			

		/**
		 * Add a TaxType to the list
		 * @param uId   ID of TaxType
		 * @param uClassId the ClassId of the tax type
		 * @param pTaxType the Name of the tax type
		 * @param pDesc the Description of the tax type
		 */ 
		public void addItem(int    uId,
							int	   uClassId,
				            String pTaxType,
				            String pDesc) throws Exception {
			TaxType myTaxType;
				
			/* Create a new Tax Type */
			myTaxType = new TaxType(this, uId, uClassId, pTaxType, pDesc);
				
			/* Check that this TaxTypeId has not been previously added */
			if (!isIdUnique(myTaxType.getId())) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTaxType,
			  			            "Duplicate TaxTypeId");
				 
			/* Check that this TaxType has not been previously added */
			if (searchFor(myTaxType.getName()) != null) 
				throw new Exception(ExceptionClass.DATA,
	   					  			myTaxType,
			  			            "Duplicate Tax Type");
				 
			/* Check that this ClassId has not been previously added */
			if (searchForEnum(uClassId) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTaxType,
			                        "Duplicate TaxClass");
				
			/* Add the Tax Type to the list */
			add(myTaxType);
		}	

		/**
		 * Add a TaxType
		 * @param uId the Id of the tax type
		 * @param uControlId the control id of the new item
		 * @param uClassId the ClassId of the tax type
		 * @param pTaxType the Encrypted Name of the tax type
		 * @param pDesc the Encrypted Description of the tax type
		 */ 
		public void addItem(int		uId,
							int		uControlId,
							int		uClassId,
				            byte[] 	pTaxType,
				            byte[]	pDesc) throws Exception {
			TaxType      myTaxType;
			
			/* Create a new Tax Type */
			myTaxType = new TaxType(this, uId, uControlId, uClassId, pTaxType, pDesc);
			
			/* Check that this TaxTypeId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTaxType,
			  			            "Duplicate TaxTypeId");
				 
			/* Check that this TaxType has not been previously added */
			if (searchFor(myTaxType.getName()) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTaxType,
			                        "Duplicate Tax Type");
				
			/* Check that this ClassId has not been previously added */
			if (searchForEnum(uClassId) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myTaxType,
			                        "Duplicate TaxClass");
				
			/* Add the Tax Type to the list */
			add(myTaxType);
		}			
	}		
}
