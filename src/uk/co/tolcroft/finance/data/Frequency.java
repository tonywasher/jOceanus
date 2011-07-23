package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.finance.data.StaticClass.FreqClass;

public class Frequency extends StaticData<Frequency, FreqClass> {
	/**
	 * The name of the object
	 */
	public static final String objName = "Frequency";

	/**
	 * The name of the object
	 */
	public static final String listName = "Frequencies";

	/**
	 * Return the Frequency class of the Frequency
	 * @return the class
	 */
	public FreqClass getFrequency()         { return super.getStaticClass(); }
	
	/* Linking methods */
	public Frequency getBase() { return (Frequency)super.getBase(); }

	/**
	 * Obtain the type of the item
	 * @return the type of the item
	 */
	public String itemType() { return objName; }
			
	/**
	 * Construct a copy of a Frequency.
	 * @param pList	The list to associate the Frequency with
	 * @param pFrequency The frequency to copy 
	 */
	protected Frequency(List		pList,
			            Frequency	pFrequency) { 
		super(pList, pFrequency);
	}
	
	/**
	 * Construct a standard Frequency on load
	 * @param pList	The list to associate the Frequency with
	 * @param sName Name of Frequency
	 */
	private Frequency(List 		pList,
			      	  String	sName) throws Exception {
		super(pList, sName);
		pList.setNewId(this);				
	}

	/**
	 * Construct a standard frequency on load
	 * @param pList	The list to associate the Frequency with
	 * @param uClassId the class id of the new item
	 * @param pName Name of Frequency
	 * @param pDesc Description of Frequency
	 */
	private Frequency(List 		pList,
			          int		uClassId,
			          String	pName,
			          String	pDesc) throws Exception {
		super(pList, uClassId, pName, pDesc);
		pList.setNewId(this);				
	}
	
	/**
	 * Construct a standard Frequency on load
	 * @param pList	The list to associate the Frequency with
	 * @param uId   ID of Frequency
	 * @param uControlId the control id of the new item
	 * @param uClassId the class id of the new item
	 * @param pName Encrypted Name of Frequency
	 * @param pDesc Encrypted Description of TaxRegime
	 */
	private Frequency(List 		pList,
			      	  int		uId,
			      	  int		uControlId,
			      	  int		uClassId,
			      	  byte[]	pName,
			      	  byte[]	pDesc) throws Exception {
		super(pList, uId, uControlId, uClassId, pName, pDesc);
		pList.setNewId(this);				
	}

	/**
	 * Represents a list of {@link Frequency} objects. 
	 */
	public static class List  extends StaticList<Frequency, FreqClass> {
		protected Class<FreqClass> getEnumClass() { return FreqClass.class; }

		/** 
	 	 * Construct an empty CORE frequency list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(DataSet pData) { 
			super(Frequency.class, pData, ListStyle.CORE); }
		
		/** 
	 	 * Construct a generic frequency data list
	 	 * @param pList the source Frequency list 
	 	 * @param pStyle the style of the list 
	 	 */
		public List(List pList, ListStyle pStyle) { super(Frequency.class, pList, pStyle); }

		/** 
	 	 * Construct a difference static data list
	 	 * @param pNew the new static data list 
	 	 * @param pOld the old static data list 
	 	 */
		protected List(List pNew, List pOld) { super(pNew, pOld); }
		
		/** 
	 	 * Clone a Frequency list
	 	 * @return the cloned list
	 	 */
		protected List cloneIt() { return new List(this, ListStyle.CORE); }
		
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }

		/**
		 * Add a new item to the list
		 * @param pItem item to be added
		 * @return the newly added item
		 */
		public Frequency addNewItem(DataItem<?> pItem) {
			Frequency myFreq = new Frequency(this, (Frequency)pItem);
			add(myFreq);
			return myFreq;
		}
	
		/**
		 * Create a new empty element in the edit list (null-operation)
		 * @param isCredit - is the item a credit or debit
		 * @return the newly added item
		 */
		public Frequency addNewItem(boolean isCredit) { return null; }			
		
		/**
		 * Add a Frequency
		 * @param pFrequency the Name of the frequency
		 * @throws Exception on error
		 */ 
		public void addItem(String 	pFrequency) throws Exception {
			Frequency myFrequency;
			
			/* Create a new Frequency */
			myFrequency = new Frequency(this, pFrequency);
				
			/* Check that this Frequency has not been previously added */
			if (searchFor(pFrequency) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myFrequency,
			                        "Duplicate Frequency");
				
			/* Check that this ClassId has not been previously added */
			if (searchForEnum(myFrequency.getStaticClassId()) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myFrequency,
			                        "Duplicate FrequencyClass");
				
			/* Add the Frequency to the list */
			add(myFrequency);		
		}
		
		/**
		 * Add a Frequency to the list
		 * @param uClassId the ClassId of the frequency
		 * @param pFrequency the Name of the frequency
		 * @param pDesc the Description of the frequency
		 */ 
		public void addItem(int	   uClassId,
				            String pFrequency,
				            String pDesc) throws Exception {
			Frequency myFreq;
				
			/* Create a new Frequency */
			myFreq = new Frequency(this, uClassId, pFrequency, pDesc);
				
			/* Check that this Frequency has not been previously added */
			if (searchFor(myFreq.getName()) != null) 
				throw new Exception(ExceptionClass.DATA,
	   					  			myFreq,
			  			            "Duplicate Frequency");
				 
			/* Check that this ClassId has not been previously added */
			if (searchForEnum(uClassId) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myFreq,
			                        "Duplicate FrequencyClass");
				
			/* Add the Frequency to the list */
			add(myFreq);
		}	

		/**
		 * Add a Frequency
		 * @param uId the Id of the frequency
		 * @param uControlId the control id of the new item
		 * @param uClassId the ClassId of the frequency
		 * @param pFrequency the Encrypted Name of the frequency
		 * @param pDesc the Encrypted Description of the frequency
		 * @throws Exception on error
		 */ 
		public void addItem(int		uId,
							int		uControlId,
							int		uClassId,
				            byte[] 	pFrequency,
				            byte[]	pDesc) throws Exception {
			Frequency myFrequency;
			
			/* Create a new Frequency */
			myFrequency = new Frequency(this, uId, uControlId, uClassId, pFrequency, pDesc);
				
			/* Check that this FrequencyId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myFrequency,
			  			            "Duplicate FrequencyId");
				 
			/* Check that this Frequency has not been previously added */
			if (searchFor(myFrequency.getName()) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myFrequency,
			                        "Duplicate Frequency");
				
			/* Check that this ClassId has not been previously added */
			if (searchForEnum(uClassId) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myFrequency,
			                        "Duplicate FrequencyClass");
				
			/* Add the Frequency to the list */
			add(myFrequency);		
		}		
	}
}
