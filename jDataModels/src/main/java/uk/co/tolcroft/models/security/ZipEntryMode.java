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
package uk.co.tolcroft.models.security;

import java.security.SecureRandom;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.security.SecurityControl.DigestType;
import uk.co.tolcroft.models.security.SymmetricKey.SymKeyType;

public class ZipEntryMode {
	/**
	 * The mode	
	 */
	private long		theMode		= 0;
	
	/**
	 * The flags
	 */
	private final static long 		modeCOMPRESS	= 16;
	private final static long 		modeDIGEST		= 32;
	private final static long 		modeDEBUG		= 64;
	private final static long 		modeENCRYPT		= 128;
	private final static long 		modeDUALENC		= 256;
	private final static long 		modeTRIOENC		= 512;
	
	/**
	 * The type locations (in units of 8-bit shifts)
	 */
	private final static int 		placeDIGEST		= 3;
	private final static int 		placeENCRYPT1	= 4;
	private final static int 		placeENCRYPT2	= 5;
	private final static int 		placeENCRYPT3	= 6;
	
	/**
	 * The Digest/Key types
	 */
	private DigestType	theDigest			= null;
	private SymKeyType	thePrimeKeyType		= null;
	private SymKeyType	theSecondKeyType	= null;
	private SymKeyType	theThirdKeyType		= null;
	
	/* Access methods */
	protected	long	getMode() 			{ return theMode; }
	public	boolean		doCompress()		{ return ((theMode & modeCOMPRESS) != 0); }
	public	boolean		doDigest()			{ return ((theMode & modeDIGEST)   != 0); }
	public	boolean		doEncrypt()			{ return ((theMode & modeENCRYPT)  != 0); }
	public	boolean		doDualEncrypt()		{ return ((theMode & modeDUALENC)  != 0); }
	public	boolean		doTripleEncrypt()	{ return ((theMode & modeTRIOENC)  != 0); }
	public	boolean		doDebug()			{ return ((theMode & modeDEBUG)    != 0); }
	public	DigestType	getDigestType()		{ return theDigest; }
	public	SymKeyType	getKeyType()		{ return thePrimeKeyType; }
	public	SymKeyType	getSecondKeyType()	{ return theSecondKeyType; }
	public	SymKeyType	getThirdKeyType()	{ return theThirdKeyType; }

	/**
	 * Constructor from node
	 * @param pMode the Zip Mode
	 */
	protected ZipEntryMode(long pMode) throws ModelException {
		/* Not allowed unless version is zero */
		if (getVersion(pMode) != 0)
			throw new ModelException(ExceptionClass.LOGIC,
								"Invalid mode version: " + getVersion(pMode));
		
		/* Set the version */
		setVersion(0);
		
		/* Set encryption options if required */
		if ((pMode & modeENCRYPT) != 0) {
			/* if we are performing Dual Encryption */
			if ((pMode & modeDUALENC) != 0)
				setEncryption(SymKeyType.fromId(getId(pMode, placeENCRYPT1)),
							  SymKeyType.fromId(getId(pMode, placeENCRYPT2)));

			/* else if we are performing Triple Encryption */
			else if ((pMode & modeTRIOENC) != 0)
				setEncryption(SymKeyType.fromId(getId(pMode, placeENCRYPT1)), 
							  SymKeyType.fromId(getId(pMode, placeENCRYPT2)),
							  SymKeyType.fromId(getId(pMode, placeENCRYPT3)));

			/* else if we are performing Single Encryption */
			else 
				setEncryption(SymKeyType.fromId(getId(pMode, placeENCRYPT1)));
		}

		/* Set digest option if required */
		if ((pMode & modeDIGEST) != 0) {
			/* Mark digest */
			setDigest(DigestType.fromId(getId(pMode, placeDIGEST)));
			
			/* Set debug option if required */
			if ((pMode & modeDEBUG) != 0) 
				setDebug();
		}

		/* Set compression option if required */
		if ((pMode & modeCOMPRESS) != 0)
			setCompress();
	}

	/**
	 * Standard constructor
	 */
	protected ZipEntryMode() {
		setVersion(0);
	}
	
	/**
	 * Get number of Encryption stages 
	 * @return # of encryption stages
	 */
	public int getNumEncrypts() {
		int iNumEncrypts = 0;
		
		/* If we are encrypting */
		if (doEncrypt()) {
			/* Triple gives three encrypts */
			if (doTripleEncrypt())
				iNumEncrypts = 3;
			
			/* Dual gives two encrypts */
			else if (doDualEncrypt())
				iNumEncrypts = 2;
			
			/* else single encrypt */
			else iNumEncrypts = 1;
		}
		
		/* return to caller */
		return iNumEncrypts;
	}
	
	/**
	 * Get encryption type
	 * @param iIndex encryption stage 
	 * @return encryption key type
	 */
	public SymKeyType getKeyType(int iIndex) {
		/* Return the required index */
		if (iIndex == 1) return thePrimeKeyType;
		if (iIndex == 2) return theSecondKeyType;
		if (iIndex == 3) return theThirdKeyType;
		
		/* return to caller */
		return null;
	}
	
	/**
	 * Get number of Digest stages 
	 * @return # of digest stages
	 */
	public int getNumDigests() {
		int iNumDigests = 0;
		
		/* If we are encrypting */
		if (doEncrypt()) {
			/* Digests are start and finish */
			iNumDigests = 2;
			
			/* If we are debugging */
			if (doDebug()) {
				/* One in front of each encrypt (allowing for compress as well) */
				iNumDigests += getNumEncrypts();
			}
		}
		
		/* return to caller */
		return iNumDigests;
	}
	
	/**
	 * Construct an Encryption zipMode
	 * @param pKeyType the key type
	 * @param pDigestType the digest type
	 */
	public static ZipEntryMode getEncryptionMode(SymKeyType	pKeyType,
				   								 DigestType	pDigestType) throws ModelException {
		/* Create a new ZipEntryMode */
		ZipEntryMode myMode = new ZipEntryMode();

		/* Set encryption option */
		myMode.setEncryption(pKeyType);

		/* Set digest option */
		myMode.setDigest(pDigestType);
		
		/* Set compress option */
		myMode.setCompress();
		
		/* Return the mode */
		return myMode;
	}
	
	/**
	 * Construct a dual encryption zipMode
	 * @param pPrimeKeyType the primary key type
	 * @param pSecondKeyType the second key type
	 * @param pDigestType the digest type
	 */
	public static ZipEntryMode getDualMode(SymKeyType	pPrimeKeyType,
										   SymKeyType	pSecondKeyType,
				   						   DigestType	pDigestType) throws ModelException {
		/* Create a new ZipEntryMode */
		ZipEntryMode myMode = new ZipEntryMode();
		
		/* Set encryption options */
		myMode.setEncryption(pPrimeKeyType, pSecondKeyType);

		/* Set digest option */
		myMode.setDigest(pDigestType);
		
		/* Set compress option */
		myMode.setCompress();
		
		/* Return the mode */
		return myMode;
	}
	
	/**
	 * Construct a triple encryption zipMode
	 * @param pPrimeKeyType the primary key type
	 * @param pSecondKeyType the second key type
	 * @param pThirdKeyType the third key type
	 * @param pDigestType the digest type
	 */
	public static ZipEntryMode getTrioMode(SymKeyType	pPrimeKeyType,
										   SymKeyType	pSecondKeyType,
										   SymKeyType	pThirdKeyType,
				   						   DigestType	pDigestType) throws ModelException {
		/* Create a new ZipEntryMode */
		ZipEntryMode myMode = new ZipEntryMode();
		
		/* Set encryption options */
		myMode.setEncryption(pPrimeKeyType, pSecondKeyType, pThirdKeyType);

		/* Set digest option */
		myMode.setDigest(pDigestType);
		
		/* Set compress option */
		myMode.setCompress();
		
		/* Return the mode */
		return myMode;
	}

	/**
	 * Construct a random encryption zipMode
	 * @param pRandom Random generator
	 */
	public static ZipEntryMode getRandomEncryptionMode(SecureRandom pRandom) throws ModelException {
		/* Access a random set of SymKeyTypes and DigestTypes */
		SymKeyType[] myTypes 	= SymKeyType.getRandomTypes(1, pRandom);		
		DigestType[] myDigest	= DigestType.getRandomTypes(1, pRandom);		

		/* Return the mode */
		return getEncryptionMode(myTypes[0], 
						   		 myDigest[0]);
	}

	/**
	 * Construct a random dual encryption zipMode
	 * @param pRandom Random generator
	 */
	public static ZipEntryMode getRandomDualMode(SecureRandom 	pRandom) throws ModelException {
		/* Access a random set of SymKeyTypes and DigestTypes */
		SymKeyType[] myTypes 	= SymKeyType.getRandomTypes(1, pRandom);		
		DigestType[] myDigest	= DigestType.getRandomTypes(1, pRandom);		

		/* Return the mode */
		return getDualMode(myTypes[0], 
						   myTypes[1],
						   myDigest[0]);
	}

	/**
	 * Construct a random triple encryption zipMode
	 * @param pRandom Random generator
	 */
	public static ZipEntryMode getRandomTrioMode(SecureRandom 	pRandom) throws ModelException {
		/* Access a random set of SymKeyTypes and DigestTypes */
		SymKeyType[] myTypes 	= SymKeyType.getRandomTypes(3, pRandom);		
		DigestType[] myDigest	= DigestType.getRandomTypes(1, pRandom);		

		/* Return the mode */
		return getTrioMode(myTypes[0], 
						   myTypes[1],
						   myTypes[2],
						   myDigest[0]);
	}

	/**
	 * Set id into mode
	 */
	private void setId(int iId, int iPlace) throws ModelException {
		/* Ensure that ordinal is store-able within a byte */
		if ((iId < 1) || (iId > 255))
			throw new ModelException(ExceptionClass.LOGIC,
								"Invalid id: " + iId);
		
		/* Access as a long value and shift up place bytes */
		long lId 	= iId;
		long lMask	= 255;
		while (iPlace-- > 0) { lId <<= 8; lMask <<= 8; }
		
		/* Add into the mode */
		theMode &= ~lMask;
		theMode |= lId;
	}
	
	/**
	 * Obtain id from mode
	 * @param pMode the mode
	 * @param iPlace the pace of the id
	 */
	private int getId(long pMode, int iPlace) {
		/* Access as a long value and shift down place bytes */
		long lId 	= pMode;
		long lMask	= 255;
		while (iPlace-- > 0) { lId >>= 8; }
		
		/* Extract from the mode */
		lId &= lMask;
		return (int)lId;
	}
	
	/**
	 * Set version into mode
	 */
	private void setVersion(int iVers) {
		/* Access as a long value and shift up place bytes */
		long lVers 	= iVers;
		long lMask	= 15;
		iVers &= lMask;
		
		/* Add into the mode */
		theMode &= ~lMask;
		theMode |= lVers;
	}
	
	/**
	 * Obtain version from mode
	 * @param pMode the mode
	 */
	private int getVersion(long pMode) {
		/* Access as a long value and isolate */
		long lVers	= pMode;
		long lMask	= 15;
		lVers &= lMask;
		return (int)lVers;
	}
	
	/**
	 * Set Compress flag
	 */
	private void setCompress() throws ModelException {
		/* Not allowed unless encryption has been specified */
		if (!doEncrypt())
			throw new ModelException(ExceptionClass.LOGIC,
								"Compress requested without Encryption");
		
		/* Set the value into the mode and the flag */
		theMode 	|= modeCOMPRESS;
	}
	
	/**
	 * Set Debug flag
	 */
	protected void setDebug() throws ModelException {
		/* Not allowed unless digest has been specified */
		if (!doDigest())
			throw new ModelException(ExceptionClass.LOGIC,
								"Debug requested without Digest");
		
		/* Set the value into the mode and the flag */
		theMode |= modeDEBUG;
	}
	
	/**
	 * Set Digest type
	 * @param pDigestType the digest type
	 */
	private void setDigest(DigestType pDigestType) throws ModelException {
		/* Not allowed unless encryption has been specified */
		if (!doEncrypt())
			throw new ModelException(ExceptionClass.LOGIC,
								"Digest requested without Encryption");
		
		/* Record the digest */
		theDigest 	 = pDigestType;

		/* Set the value into the mode and the flag */
		theMode 	|= modeDIGEST;
		setId(pDigestType.getId(), placeDIGEST);
	}
	
	/**
	 * Set Encryption type
	 * @param pKeyType the key type
	 */
	private void setEncryption(SymKeyType pKeyType) throws ModelException {
		/* Record the key type */
		thePrimeKeyType 	= pKeyType;
		theSecondKeyType 	= null;
		theThirdKeyType 	= null;

		/* Set the value into the mode and the flag */
		theMode 	|= modeENCRYPT;
		theMode		&= ~(modeDUALENC|modeTRIOENC);
		setId(pKeyType.getId(), placeENCRYPT1);
	}
	
	/**
	 * Set Dual Encryption type
	 * @param pPrimeKeyType the first key type
	 * @param pSecondKeyType the second key type
	 */
	private void setEncryption(SymKeyType pPrimeKeyType,
							   SymKeyType pSecondKeyType) throws ModelException {
		/* Record the key type */
		thePrimeKeyType 	= pPrimeKeyType;
		theSecondKeyType 	= pSecondKeyType;
		theThirdKeyType 	= null;

		/* Set the value into the mode and the flag */
		theMode 	|= (modeENCRYPT|modeDUALENC);
		theMode		&= ~(modeTRIOENC);
		setId(pPrimeKeyType.getId(), placeENCRYPT1);
		setId(pSecondKeyType.getId(), placeENCRYPT2);
	}

	/**
	 * Set Triple Encryption type
	 * @param pPrimeKeyType the first key type
	 * @param pSecondKeyType the second key type
	 * @param pThirdKeyType the second key type
	 */
	private void setEncryption(SymKeyType pPrimeKeyType,
							   SymKeyType pSecondKeyType,
							   SymKeyType pThirdKeyType) throws ModelException {
		/* Record the key type */
		thePrimeKeyType 	= pPrimeKeyType;
		theSecondKeyType 	= pSecondKeyType;
		theThirdKeyType 	= pThirdKeyType;

		/* Set the value into the mode and the flag */
		theMode 	|= (modeENCRYPT|modeTRIOENC);
		theMode		&= ~(modeDUALENC);
		setId(pPrimeKeyType.getId(), placeENCRYPT1);
		setId(pSecondKeyType.getId(), placeENCRYPT2);
		setId(pThirdKeyType.getId(), placeENCRYPT3);
	}
}
