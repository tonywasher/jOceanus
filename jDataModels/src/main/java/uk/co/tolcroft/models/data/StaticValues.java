/*******************************************************************************
 * Copyright 2012 Tony Washer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package uk.co.tolcroft.models.data;

import uk.co.tolcroft.models.Difference;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.data.EncryptedData.EncryptedString;

public class StaticValues<T extends StaticData<T,E>, E extends Enum<E>> extends EncryptedValues<T> {
	private EncryptedString	theName     	= null;
	private EncryptedString	theDesc     	= null;
	private boolean    		isEnabled		= true;
	private Class<E>		theEnumClass 	= null;
	private E				theClass 		= null;
	private int     		theOrder		= -1;
	
	/* Access methods */
	public 		EncryptedString 	getName()      	{ return theName; }
	public 		EncryptedString 	getDesc()      	{ return theDesc; }
	public 		boolean  			getEnabled()    { return isEnabled; }
	protected	Class<E>   			getEnumClass()	{ return theEnumClass; }
	protected	E   				getStaticClass(){ return theClass; }
	protected	int 				getOrder()		{ return theOrder; }
	public 		String  			getNameValue()  { return EncryptedData.getValue(theName); }
	public 		String  			getDescValue()  { return EncryptedData.getValue(theDesc); }
	public 		byte[]  			getNameBytes()  { return EncryptedData.getBytes(theName); }
	public 		byte[]  			getDescBytes()  { return EncryptedData.getBytes(theDesc); }
	
	/* Value setting */
	protected 	void setName(EncryptedString pName) { theName = pName; }
	protected	void setDesc(EncryptedString pDesc) { theDesc = pDesc; }
	protected	void setEnabled(boolean isEnabled) 	{ this.isEnabled = isEnabled; }
	protected	void setEnumClass(Class<E> pClass)  { theEnumClass = pClass; }
	protected	void setStaticClass(Enum<E> pClass) { theClass = theEnumClass.cast(pClass); }
	protected	void setOrder(int pOrder) 			{ theOrder = pOrder; }

	/* Set encrypted values */
	protected 	void setName(String pName) throws ModelException	{ theName = createEncryptedString(theName, pName); }
	protected	void setDesc(String pDesc) throws ModelException	{ theDesc = createEncryptedString(theDesc, pDesc); }
	protected 	void setName(byte[] pName) throws ModelException	{ theName = createEncryptedString(pName); }
	protected	void setDesc(byte[] pDesc) throws ModelException	{ theDesc = createEncryptedString(pDesc); }

	/* Constructor */
	public StaticValues() {}
	public StaticValues(StaticValues<T,E> pValues) { copyFrom(pValues); }
	
	@Override
	public Difference histEquals(HistoryValues<T> pCompare) {
		/* Make sure that the object is the same class */
		if (pCompare.getClass() != this.getClass()) return Difference.Different;
		
		/* Cast correctly */
		StaticValues<?,?> myValues = (StaticValues<?,?>)pCompare;
		
		/* Make sure that the object is the same enumeration class */
		if (myValues.getEnumClass() != theEnumClass) return Difference.Different;
		
		/* Handle integer differences */
		if ((isEnabled != myValues.isEnabled) ||
		    (theOrder  != myValues.theOrder))	return Difference.Different;
		
		/* Determine underlying differences */
		Difference myDifference = super.histEquals(pCompare);
		
		/* Handle remaining differences */
		myDifference = myDifference.combine(Utils.differs(theName,	myValues.theName));
		myDifference = myDifference.combine(Utils.differs(theDesc,	myValues.theDesc));
		
		/* Return differences */
		return myDifference;
	}
	
	@Override
	public HistoryValues<T> copySelf() {
		return new StaticValues<T,E>(this);
	}
	@Override
	public void    copyFrom(HistoryValues<?> pSource) {
		@SuppressWarnings("unchecked")
		StaticValues<T,E> myValues = (StaticValues<T,E>)pSource;
		copyFrom(myValues);
	}
	public void    copyFrom(StaticValues<T,E> pValues) {
		super.copyFrom(pValues);
		theEnumClass	= pValues.getEnumClass();
		theClass		= pValues.getStaticClass();
		theOrder		= pValues.getOrder();
		isEnabled		= pValues.getEnabled();
		theName     	= pValues.getName();
		theDesc     	= pValues.getDesc();
	}
	@Override
	public Difference	fieldChanged(int fieldNo, HistoryValues<T> pOriginal) {
		StaticValues<?,?> 	pValues = (StaticValues<?,?>)pOriginal;
		Difference	bResult = Difference.Identical;
		switch (fieldNo) {
			case StaticData.FIELD_NAME:
				bResult = (EncryptedItem.differs(theName,      pValues.theName));
				break;
			case StaticData.FIELD_DESC:
				bResult = (EncryptedItem.differs(theDesc,      pValues.theDesc));
				break;
			case StaticData.FIELD_ENABLED:
				bResult = (isEnabled != pValues.isEnabled) ? Difference.Different
														   : Difference.Identical;
				break;
			case StaticData.FIELD_ORDER:
				bResult = (theOrder  != pValues.theOrder)  ? Difference.Different
														   : Difference.Identical;
				break;
			default:
				bResult = super.fieldChanged(fieldNo, pOriginal);
				break;
		}
		return bResult;
	}

	@Override
	protected void updateSecurity() throws ModelException {
		/* Update the encryption */
		theName = updateEncryptedString(theName);
		theDesc = updateEncryptedString(theDesc);
	}		
	
	@Override
	protected void applySecurity() throws ModelException {
		/* Apply the encryption */
		applyEncryption(theName);
		applyEncryption(theDesc);
	}		
	
	@Override
	protected void adoptSecurity(EncryptedValues<T> pBase) throws ModelException {
		@SuppressWarnings("unchecked")
		StaticValues<T,E> myBase = (StaticValues<T,E>)pBase;
		
		/* Adopt the encryption */
		adoptEncryption(theName, myBase.getName());
		adoptEncryption(theDesc, myBase.getDesc());
	}		
}
