package uk.co.tolcroft.models.security;

import java.security.SecureRandom;

import uk.co.tolcroft.models.Difference;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.security.AsymmetricKey.AsymKeyType;
import uk.co.tolcroft.models.security.SecurityControl.DigestType;
import uk.co.tolcroft.models.Utils;

public class SecurityMode {
	/**
	 * Mode length in bytes 
	 */
	protected static final int 	MODELENGTH	 		= 4;
	
	/**
	 * The mode	
	 */
	private int		theMode			= 0;
	
	/**
	 * Use restricted security	
	 */
	private boolean	useRestricted	= false;
	
	/**
	 * Password Hash Mode (or AsymmetricKey)
	 */
	private boolean	isPasswordHash	= true;
	
	/**
	 * The locations (in units of 4-bit shifts)
	 */
	private final static int 		placeDIGESTPRIME	= 1;
	private final static int 		placeDIGESTALT		= 2;
	private final static int 		placeDIGESTCIPHER	= 3;
	private final static int 		placeDIGESTSECRET	= 4;
	private final static int 		placeASYMKEYTYPE	= 1;
	private final static int 		placeITERSWITCH		= 5;
	private final static int 		placeITERFINAL		= 6;
	private final static int 		placeFLAGS			= 7;

	/**
	 * The various masks
	 */
	private final static int		maskHASH			= 1;
	private final static int		maskRESTRICT		= 4;
	private final static int		maskVALUE			= 15;
	
	/**
	 * The Digest/Key types
	 */
	private DigestType	thePrimeDigest		= null;
	private DigestType	theAltDigest		= null;
	private DigestType	theSecretDigest		= null;
	private DigestType	theCipherDigest		= null;
	private AsymKeyType	theAsymKeyType		= null;
	private	int			theSwitchIteration	= 1024;
	private	int			theFinalIteration	= 2048;

	/* The iteration counts */
	
	/* Access methods */
	public	int			getMode() 			{ return theMode; }
	protected	byte[]	getByteMode() 		{ return Utils.BytesFromInteger(theMode); }
	public	boolean		useRestricted() 	{ return useRestricted; }
	public	DigestType	getPrimeDigest()	{ return thePrimeDigest; }
	public	DigestType	getAltDigest()		{ return theAltDigest; }
	public	DigestType	getSecretDigest()	{ return theSecretDigest; }
	public	DigestType	getCipherDigest()	{ return theCipherDigest; }
	public	AsymKeyType	getAsymKeyType()	{ return theAsymKeyType; }
	public	int			getSwitchIterate()	{ return theSwitchIteration; }
	public	int			getFinalIterate()	{ return theFinalIteration; }

	/**
	 * Constructor from mode
	 * @param pMode the Control Mode
	 */
	public SecurityMode(int pMode) throws ModelException {
		/* Not allowed unless version is zero */
		if (getVersion(pMode) != 0)
			throw new ModelException(ExceptionClass.LOGIC,
								"Invalid mode version: " + getVersion(pMode));
		
		/* Set the version */
		setVersion(0);
		
		/* Set flags */
		setFlags(getRestricted(pMode),
				 getIsPasswordHash(pMode));
		
		/* Set Cipher Digest values */
		setCipherDigest(getDigestType(pMode, placeDIGESTCIPHER));
		
		/* If this is a Password Hash */
		if (isPasswordHash) {
			/* Set Iterations */
			setIterations(getValue(pMode, placeITERSWITCH),
					  	  getValue(pMode, placeITERFINAL));

			/* Set Password Digest values */
			setPasswordDigests(getDigestType(pMode, placeDIGESTPRIME),
						   	   getDigestType(pMode, placeDIGESTALT),
						   	   getDigestType(pMode, placeDIGESTSECRET));
		}

		/* else it is an Asymmetric Key Mode */
		else {
			/* Set AsymKeyValue */
			setAsymKeyType(getAsymKeyType(pMode, placeASYMKEYTYPE));
		}
	}

	/**
	 * Standard constructor
	 */
	private SecurityMode() {
		setVersion(0);
	}
	
	/**
	 * Construct a random Security KeyMode
	 * @param useRestricted use restricted keys
	 * @param pRandom the random generator
	 */
	public static SecurityMode getSecurityMode(boolean 		useRestricted,
									   		   SecureRandom pRandom) throws ModelException {
		/* Create a new SecurityMode */
		SecurityMode myMode = new SecurityMode();

		/* Set flags */
		myMode.setFlags(useRestricted, true);
		
		/* Set iterations */
		myMode.setRandomIterations(pRandom);
		
		/* Access a random set of DigestTypes */
		DigestType[]  myDigest		= DigestType.getRandomTypes(3, pRandom);
		DigestType[]  mySetDigest	= DigestType.getRandomTypes(1, pRandom);
		
		/* Set Password digests */
		myMode.setPasswordDigests(myDigest[0], myDigest[1], myDigest[2]);

		/* Set Cipher digest */
		myMode.setCipherDigest(mySetDigest[0]);

		/* Return the mode */
		return myMode;
	}
	
	/**
	 * Construct a random Asymmetric KeyMode
	 * @param useRestricted use restricted keys
	 * @param pRandom the random generator
	 */
	public static SecurityMode getAsymmetricMode(boolean 		useRestricted,
									   		   	 SecureRandom 	pRandom) throws ModelException {
		/* Create a new SecurityMode */
		SecurityMode myMode = new SecurityMode();

		/* Set flags */
		myMode.setFlags(useRestricted, false);
		
		/* Access a random set of Key/DigestTypes */
		AsymKeyType[] myKeyType		= AsymKeyType.getRandomTypes(1, pRandom);
		DigestType[]  mySetDigest	= DigestType.getRandomTypes(1, pRandom);
		
		/* Set AsymKeyTypes */
		myMode.setAsymKeyType(myKeyType[0]);

		/* Set Cipher digest */
		myMode.setCipherDigest(mySetDigest[0]);

		/* Return the mode */
		return myMode;
	}
	
	/**
	 * Construct a random set of iterations
	 * @param pRandom the random generator
	 */
	protected void setRandomIterations(SecureRandom pRandom) throws ModelException {
		/* Access a random set of Iterations */
		int iSwitch = 1 + pRandom.nextInt(15);		
		int iFinal 	= 1 + pRandom.nextInt(15);		

		/* Set the iterations */
		setIterations(iSwitch, iFinal);
	}

	/**
	 * Set Value into mode
	 * @param iValue the value to set 
	 * @param iPlace the position to set the value
	 */
	private void setValue(int iValue, int iPlace) throws ModelException {
		/* Ensure that ordinal is store-able within a nibble */
		if (((iValue < 1) || (iValue > 15)) &&
			(iPlace != placeFLAGS))
			throw new ModelException(ExceptionClass.LOGIC,
								"Invalid Value: " + iValue + " at position: " + iPlace);
		
		/* Shift up place bytes */
		int iMask	= maskVALUE;
		while (iPlace-- > 0) { iValue <<= 4; iMask <<= 4; }
		
		/* Add into the mode */
		theMode &= ~iMask;
		theMode |= iValue;
	}
	
	/**
	 * Obtain value from mode
	 * @param pMode the mode
	 * @param iPlace the position from which to receive the value
	 * @return the retrieved value
	 */
	private int getValue(int pMode, int iPlace) {
		/* Access as a long value and shift down place bytes */
		int  iValue	= pMode;
		int	 iMask	= maskVALUE;
		while (iPlace-- > 0) { iValue >>= 4; }
		
		/* Extract from the mode */
		iValue &= iMask;
		return iValue;
	}
	
	/**
	 * Set version into mode
	 */
	private void setVersion(int iVers) {
		/* Isolate the version */
		int iMask	 = maskVALUE;
		iVers 		&= iMask;
		
		/* Add into the mode */
		theMode &= ~iMask;
		theMode |= iVers;
	}
	
	/**
	 * Obtain version from mode
	 * @param pMode the mode
	 */
	private int getVersion(int pMode) {
		/* Access as a long value and isolate */
		int	iVers	= pMode;
		int iMask	= maskVALUE;
		iVers &= iMask;
		return iVers;
	}
	
	/**
	 * Set flags into mode
	 * @param useRestricted use restricted keys
	 * @param isPasswordHash is this a password hash
	 */
	private void setFlags(boolean useRestricted,
						  boolean isPasswordHash) throws ModelException {
		/* Initialise the value */
		int iValue = 0;
		
		/* Record flags */
		this.useRestricted 	= useRestricted;
		this.isPasswordHash	= isPasswordHash;
		
		/* Set the value appropriately */
		if (useRestricted) 	iValue |= maskRESTRICT;
		if (isPasswordHash)	iValue |= maskHASH;
		
		/* Add into the mode */
		setValue(iValue, placeFLAGS);
	}
	
	/**
	 * Obtain useRestricted flag from mode
	 * @return should restricted keys be used
	 */
	private boolean getRestricted(int pMode) {
		int iValue = getValue(pMode, placeFLAGS);
		return ((iValue & maskRESTRICT) != 0);
	}
	
	/**
	 * Obtain useRestricted flag from mode
	 * @return should restricted keys be used
	 */
	private boolean getIsPasswordHash(int pMode) {
		int iValue = getValue(pMode, placeFLAGS);
		return ((iValue & maskHASH) != 0);
	}
	
	/**
	 * Set Iterations
	 * @param pSwitchIteration the switch iteration
	 * @param pFinalIteration the final iteration
	 */
	private void setIterations(int pSwitchIteration,
							   int pFinalIteration) throws ModelException {
		/* Record the iterations */
		theSwitchIteration 	+= pSwitchIteration;
		theFinalIteration 	+= pFinalIteration;

		/* Set the values into the mode */
		setValue(pSwitchIteration,  placeITERSWITCH);
		setValue(pFinalIteration, 	placeITERFINAL);
	}

	/**
	 * Set Password Digests
	 * @param pPrimeDigest the prime digest type
	 * @param pAltDigest the alternate digest type
	 * @param pSecretDigest the secret digest type
	 */
	private void setPasswordDigests(DigestType pPrimeDigest,
									DigestType pAltDigest,
									DigestType pSecretDigest) throws ModelException {
		/* Record the digests */
		thePrimeDigest 	= pPrimeDigest;
		theAltDigest 	= pAltDigest;
		theSecretDigest = pSecretDigest;

		/* Set the values into the mode */
		setValue(pPrimeDigest.getId(), 	placeDIGESTPRIME);
		setValue(pAltDigest.getId(), 	placeDIGESTALT);
		setValue(pSecretDigest.getId(), placeDIGESTSECRET);
	}
	
	/**
	 * Set CipherSet Digest
	 * @param pCipherDigest the cipher set digest type
	 */
	private void setCipherDigest(DigestType pCipherDigest) throws ModelException {
		/* Record the digests */
		theCipherDigest = pCipherDigest;

		/* Set the values into the mode */
		setValue(pCipherDigest.getId(), placeDIGESTCIPHER);
	}
	
	/**
	 * Get DigestType from mode
	 * @param pMode the mode
	 * @param iPlace the position from which to receive the digestType
	 * @return the retrieved DigestType
	 */
	private DigestType getDigestType(int pMode, int iPlace) throws ModelException {
		/* Obtain the value */
		int iValue = getValue(pMode, iPlace);
		
		/* Return the value */
		return DigestType.fromId(iValue);
	}
	
	/**
	 * Set AsymKeyType
	 * @param pKeyType the asymmetric keyType
	 */
	private void setAsymKeyType(AsymKeyType pKeyType) throws ModelException {
		/* Record the key type */
		theAsymKeyType 	= pKeyType;

		/* Set the value into the mode */
		setValue(pKeyType.getId(), 	placeASYMKEYTYPE);
	}
	
	/**
	 * Get AsymKeyType from mode
	 * @param pMode the mode
	 * @param iPlace the position from which to receive the keyType
	 * @return the retrieved keyType
	 */
	private AsymKeyType getAsymKeyType(int pMode, int iPlace) throws ModelException {
		/* Obtain the value */
		int iValue = getValue(pMode, iPlace);
		
		/* Return the value */
		return AsymKeyType.fromId(iValue);
	}
	
	@Override
	public String toString() {
		StringBuilder myString = new StringBuilder(1000);
		if (theAsymKeyType != null) { 
			myString.append("AsymKeyType="); myString.append(theAsymKeyType.toString()); myString.append(','); } 
		if (thePrimeDigest != null) { 
			myString.append("PrimeDigest="); myString.append(thePrimeDigest.toString()); myString.append(','); } 
		if (theAltDigest != null) { 
			myString.append("AltDigest="); myString.append(theAltDigest.toString()); myString.append(','); } 
		if (theSecretDigest != null) { 
			myString.append("SecretDigest="); myString.append(theSecretDigest.toString()); myString.append(','); } 
		if (theCipherDigest != null) { 
			myString.append("CipherDigest="); myString.append(theCipherDigest.toString()); myString.append(','); } 
		if (useRestricted) { 
			myString.append("Restricted Security,"); } 
		return myString.toString();
	}
	
	/**
	 * Difference function
	 */
	public static Difference differs(SecurityMode pFirst, SecurityMode pSecond) {
		/* Handle nulls */
		if (pFirst == null) return (pSecond == null) ? Difference.Identical
													 : Difference.Different;
		if (pSecond == null) return Difference.Different;
		
		/* Return difference in modes */
		return (pFirst.theMode != pSecond.theMode) ? Difference.Different 
												   : Difference.Identical;
	}
}
