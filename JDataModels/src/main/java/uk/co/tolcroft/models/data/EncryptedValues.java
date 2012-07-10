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

import uk.co.tolcroft.models.Decimal.Dilution;
import uk.co.tolcroft.models.Decimal.Money;
import uk.co.tolcroft.models.Decimal.Price;
import uk.co.tolcroft.models.Decimal.Rate;
import uk.co.tolcroft.models.Decimal.Units;
import uk.co.tolcroft.models.Difference;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.data.EncryptedData.EncryptedCharArray;
import uk.co.tolcroft.models.data.EncryptedData.EncryptedDilution;
import uk.co.tolcroft.models.data.EncryptedData.EncryptedField;
import uk.co.tolcroft.models.data.EncryptedData.EncryptedMoney;
import uk.co.tolcroft.models.data.EncryptedData.EncryptedPrice;
import uk.co.tolcroft.models.data.EncryptedData.EncryptedRate;
import uk.co.tolcroft.models.data.EncryptedData.EncryptedString;
import uk.co.tolcroft.models.data.EncryptedData.EncryptedUnits;

public abstract class EncryptedValues<T extends DataItem<T>> extends HistoryValues<T> {
	private ControlKey		theControlKey   = null;
	private int				theControlId 	= -1;
	
	/* Access methods */
	public ControlKey  		getControlKey() { return theControlKey; }
	public int				getControlId()  { return theControlId; }
	
	/* Value setting */
	protected void setControlKey(ControlKey pControlKey) { 
		theControlKey   = pControlKey;
		theControlId 	= (pControlKey == null) ? -1 : pControlKey.getId();
	}
	protected void setControlId(int pValue) {
		theControlId	= pValue; }

	/* Constructor */
	public EncryptedValues() {}
	public EncryptedValues(EncryptedValues<T> pValues) { copyFrom(pValues); }
	
	@Override
	public Difference histEquals(HistoryValues<T> pValues) {
		EncryptedValues<T> myValues = (EncryptedValues<T>)pValues;
		return (ControlKey.differs(theControlKey,    myValues.theControlKey));
	}
	@Override
	public void    copyFrom(HistoryValues<?> pValues) {
		if (pValues instanceof EncryptedValues) {
			EncryptedValues<?> myValues = (EncryptedValues<?>)pValues;
			theControlKey	= myValues.getControlKey();
			theControlId 	= (theControlKey == null) ? -1 : theControlKey.getId();
		}
	}
	@Override
	public Difference	fieldChanged(int fieldNo, HistoryValues<T> pOriginal) {
		EncryptedValues<T> 	pValues = (EncryptedValues<T>)pOriginal;
		Difference	bResult = Difference.Identical;
		switch (fieldNo) {
			case EncryptedItem.FIELD_CONTROL:
				bResult = (ControlKey.differs(theControlKey,	pValues.theControlKey));
				break;
		}
		return bResult;
	}

	/* Apply Security */
	protected abstract void applySecurity() throws ModelException;
	protected abstract void updateSecurity() throws ModelException;
	protected abstract void adoptSecurity(EncryptedValues<T>	pBase) throws ModelException;
	
	/**
	 * Create a new encrypted string 
	 * @param pCurr the current value
	 * @param pValue the unencrypted value (or null)
	 * @return the EncryptedString or null
	 */
	protected EncryptedString createEncryptedString(EncryptedString pCurr,
													String 			pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* If Value and control is the same return current value */
		if ((pValue == EncryptedData.getValue(pCurr)) &&
			(ControlKey.differs(theControlKey, pCurr.getControlKey()).isIdentical())) return pCurr;
		
		/* Create the encrypted string */
		return new EncryptedString(theControlKey, pValue);
	}

	/**
	 * Create a new encrypted string 
	 * @param pValue the encrypted value (or null)
	 * @return the EncryptedString or null
	 */
	protected EncryptedString createEncryptedString(byte[] pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* Create the encrypted string */
		return new EncryptedString(theControlKey, pValue);
	}

	/**
	 * Update an encrypted string 
	 * @param pValue the encrypted value (or null)
	 * @return the EncryptedString or null
	 */
	protected EncryptedString updateEncryptedString(EncryptedString pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* If control is the same use the value */
		if (ControlKey.differs(theControlKey, pValue.getControlKey()).isIdentical()) return pValue;
		
		/* Create the encrypted string */
		return new EncryptedString(theControlKey, pValue.getValue());
	}

	/**
	 * Create a new encrypted string 
	 * @param pCurr the current value
	 * @param pValue the unencrypted value (or null)
	 * @return the EncryptedString or null
	 */
	protected EncryptedCharArray createEncryptedCharArray(EncryptedCharArray 	pCurr,
														  char[] 				pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* Value is the same return current value */
		if ((pValue == EncryptedData.getValue(pCurr)) &&
			(ControlKey.differs(theControlKey, pCurr.getControlKey()).isIdentical())) return pCurr;
		
		/* Create the encrypted string */
		return new EncryptedCharArray(theControlKey, pValue);
	}

	/**
	 * Create a new encrypted string 
	 * @param pValue the encrypted value (or null)
	 * @return the EncryptedString or null
	 */
	protected EncryptedCharArray createEncryptedCharArray(byte[] pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* Create the encrypted character array */
		return new EncryptedCharArray(theControlKey, pValue);
	}

	/**
	 * Update an encrypted charArray 
	 * @param pValue the encrypted value (or null)
	 * @return the EncryptedCharArray or null
	 */
	protected EncryptedCharArray updateEncryptedCharArray(EncryptedCharArray pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* If control is the same use the value */
		if (ControlKey.differs(theControlKey, pValue.getControlKey()).isIdentical()) return pValue;
		
		/* Create the encrypted character array */
		return new EncryptedCharArray(theControlKey, pValue.getValue());
	}

	/**
	 * Create a new encrypted money 
	 * @param pCurr the current value
	 * @param pValue the unencrypted value (or null)
	 * @return the EncryptedMoney or null
	 */
	protected EncryptedMoney createEncryptedMoney(EncryptedMoney	pCurr,
												  Money				pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* If Value and control is the same return current value */
		if ((pValue == EncryptedData.getValue(pCurr)) &&
			(ControlKey.differs(theControlKey, pCurr.getControlKey()).isIdentical())) return pCurr;
		
		/* Create the encrypted money */
		return new EncryptedMoney(theControlKey, pValue);
	}

	/**
	 * Create a new encrypted money 
	 * @param pCurr the current value
	 * @param pValue the unencrypted value (or null)
	 * @return the EncryptedMoney or null
	 */
	protected EncryptedMoney createEncryptedMoney(EncryptedMoney	pCurr,
												  String			pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* Parse the money and pass down */
		return createEncryptedMoney(pCurr, new Money(pValue));
	}

	/**
	 * Create a new encrypted money
	 * @param pValue the encrypted value (or null)
	 * @return the EncryptedMoney or null
	 */
	protected EncryptedMoney createEncryptedMoney(byte[] pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* Create the encrypted money */
		return new EncryptedMoney(theControlKey, pValue);
	}

	/**
	 * Update an encrypted money 
	 * @param pValue the encrypted value (or null)
	 * @return the EncryptedMoney or null
	 */
	protected EncryptedMoney updateEncryptedMoney(EncryptedMoney pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* If control is the same use the value */
		if (ControlKey.differs(theControlKey, pValue.getControlKey()).isIdentical()) return pValue;
		
		/* Create the encrypted money */
		return new EncryptedMoney(theControlKey, pValue.getValue());
	}

	/**
	 * Create a new encrypted units 
	 * @param pCurr the current value
	 * @param pValue the unencrypted value (or null)
	 * @return the EncryptedUnits or null
	 */
	protected EncryptedUnits createEncryptedUnits(EncryptedUnits	pCurr,
												  Units				pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* If Value and control is the same return current value */
		if ((pValue == EncryptedData.getValue(pCurr)) &&
			(ControlKey.differs(theControlKey, pCurr.getControlKey()).isIdentical())) return pCurr;
		
		/* Create the encrypted units */
		return new EncryptedUnits(theControlKey, pValue);
	}

	/**
	 * Create a new encrypted units 
	 * @param pCurr the current value
	 * @param pValue the unencrypted value (or null)
	 * @return the EncryptedUnits or null
	 */
	protected EncryptedUnits createEncryptedUnits(EncryptedUnits	pCurr,
												  String			pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* Parse the units and pass down */
		return createEncryptedUnits(pCurr, new Units(pValue));
	}

	/**
	 * Create a new encrypted units
	 * @param pValue the encrypted value (or null)
	 * @return the EncryptedUnits or null
	 */
	protected EncryptedUnits createEncryptedUnits(byte[] pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* Create the encrypted units */
		return new EncryptedUnits(theControlKey, pValue);
	}

	/**
	 * Update an encrypted units 
	 * @param pValue the encrypted value (or null)
	 * @return the EncryptedUnits or null
	 */
	protected EncryptedUnits updateEncryptedUnits(EncryptedUnits pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* If control is the same use the value */
		if (ControlKey.differs(theControlKey, pValue.getControlKey()).isIdentical()) return pValue;
		
		/* Create the encrypted units */
		return new EncryptedUnits(theControlKey, pValue.getValue());
	}

	/**
	 * Create a new encrypted price 
	 * @param pCurr the current value
	 * @param pValue the unencrypted value (or null)
	 * @return the EncryptedPrice or null
	 */
	protected EncryptedPrice createEncryptedPrice(EncryptedPrice	pCurr,
												  Price				pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* If Value and control is the same return current value */
		if ((pValue == EncryptedData.getValue(pCurr)) &&
			(ControlKey.differs(theControlKey, pCurr.getControlKey()).isIdentical())) return pCurr;
		
		/* Create the encrypted price */
		return new EncryptedPrice(theControlKey, pValue);
	}

	/**
	 * Create a new encrypted price 
	 * @param pCurr the current value
	 * @param pValue the unencrypted value (or null)
	 * @return the EncryptedPrice or null
	 */
	protected EncryptedPrice createEncryptedPrice(EncryptedPrice	pCurr,
												  String			pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* Parse the price and pass down */
		return createEncryptedPrice(pCurr, new Price(pValue));
	}

	/**
	 * Create a new encrypted price
	 * @param pValue the encrypted value (or null)
	 * @return the EncryptedPrice or null
	 */
	protected EncryptedPrice createEncryptedPrice(byte[] pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* Create the encrypted price */
		return new EncryptedPrice(theControlKey, pValue);
	}

	/**
	 * Update an encrypted price 
	 * @param pValue the encrypted value (or null)
	 * @return the EncryptedPrice or null
	 */
	protected EncryptedPrice updateEncryptedPrice(EncryptedPrice pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* If control is the same use the value */
		if (ControlKey.differs(theControlKey, pValue.getControlKey()).isIdentical()) return pValue;
		
		/* Create the encrypted price */
		return new EncryptedPrice(theControlKey, pValue.getValue());
	}

	/**
	 * Create a new encrypted rate 
	 * @param pCurr the current value
	 * @param pValue the unencrypted value (or null)
	 * @return the EncryptedRate or null
	 */
	protected EncryptedRate createEncryptedRate(EncryptedRate	pCurr,
												Rate			pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* If Value and control is the same return current value */
		if ((pValue == EncryptedData.getValue(pCurr)) &&
			(ControlKey.differs(theControlKey, pCurr.getControlKey()).isIdentical())) return pCurr;
		
		/* Create the encrypted rate */
		return new EncryptedRate(theControlKey, pValue);
	}

	/**
	 * Create a new encrypted rate 
	 * @param pCurr the current value
	 * @param pValue the unencrypted value (or null)
	 * @return the EncryptedRate or null
	 */
	protected EncryptedRate createEncryptedRate(EncryptedRate	pCurr,
												String			pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* Parse the rate and pass down */
		return createEncryptedRate(pCurr, new Rate(pValue));
	}

	/**
	 * Create a new encrypted rate
	 * @param pValue the encrypted value (or null)
	 * @return the EncryptedRate or null
	 */
	protected EncryptedRate createEncryptedRate(byte[] pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* Create the encrypted rate */
		return new EncryptedRate(theControlKey, pValue);
	}

	/**
	 * Update an encrypted rate
	 * @param pValue the encrypted value (or null)
	 * @return the EncryptedRate or null
	 */
	protected EncryptedRate updateEncryptedRate(EncryptedRate pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* If control is the same use the value */
		if (ControlKey.differs(theControlKey, pValue.getControlKey()).isIdentical()) return pValue;
		
		/* Create the encrypted rate */
		return new EncryptedRate(theControlKey, pValue.getValue());
	}

	/**
	 * Create a new encrypted dilution 
	 * @param pCurr the current value
	 * @param pValue the unencrypted value (or null)
	 * @return the EncryptedDilution or null
	 */
	protected EncryptedDilution createEncryptedDilution(EncryptedDilution	pCurr,
												 		Dilution			pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* If Value and control is the same return current value */
		if ((pValue == EncryptedData.getValue(pCurr)) &&
			(ControlKey.differs(theControlKey, pCurr.getControlKey()).isIdentical())) return pCurr;
		
		/* Create the encrypted dilution */
		return new EncryptedDilution(theControlKey, pValue);
	}

	/**
	 * Create a new encrypted dilution 
	 * @param pCurr the current value
	 * @param pValue the unencrypted value (or null)
	 * @return the EncryptedRate or null
	 */
	protected EncryptedDilution createEncryptedDilution(EncryptedDilution	pCurr,
														String				pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* Parse the dilution and pass down */
		return createEncryptedDilution(pCurr, new Dilution(pValue));
	}

	/**
	 * Create a new encrypted dilution
	 * @param pValue the encrypted value (or null)
	 * @return the EncryptedDilution or null
	 */
	protected EncryptedDilution createEncryptedDilution(byte[] pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* Create the encrypted dilution */
		return new EncryptedDilution(theControlKey, pValue);
	}

	/**
	 * Update an encrypted dilution 
	 * @param pValue the encrypted value (or null)
	 * @return the EncryptedDilution or null
	 */
	protected EncryptedDilution updateEncryptedDilution(EncryptedDilution pValue) throws ModelException {
		/* If value is null, just return null */
		if (pValue == null) return null;
		
		/* If control is the same use the value */
		if (ControlKey.differs(theControlKey, pValue.getControlKey()).isIdentical()) return pValue;
		
		/* Create the encrypted dilution */
		return new EncryptedDilution(theControlKey, pValue.getValue());
	}

	/**
	 * Apply encryption for an encrypted field 
	 * @param pValue the EncryptedField
	 */
	protected void applyEncryption(EncryptedField<?> pValue) throws ModelException {
		/* Renew the encryption */
		if (pValue != null) pValue.applyEncryption(theControlKey);
	}

	/**
	 * Adopt encryption for an encrypted field 
	 * @param pValue the EncryptedField
	 * @param pBase the EncryptedField to adopt from 
	 */
	protected <X> void adoptEncryption(EncryptedField<X> pValue,
									   EncryptedField<X> pBase) throws ModelException {
		/* Adopt the encryption */
		if (pValue != null) pValue.adoptEncryption(theControlKey, pBase);
	}
}
