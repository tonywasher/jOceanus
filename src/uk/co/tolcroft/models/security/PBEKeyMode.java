package uk.co.tolcroft.models.security;

import java.security.SecureRandom;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.security.PasswordKey.PBEKeyType;
import uk.co.tolcroft.models.security.SecurityControl.DigestType;
import uk.co.tolcroft.models.Utils;

public class PBEKeyMode {
	/**
	 * The mode	
	 */
	private int		theMode			= 0;
	
	/**
	 * Use restricted security	
	 */
	private boolean	useRestricted	= false;
	
	/**
	 * The type locations (in units of 4-bit shifts)
	 */
	private final static int 		placeDIGEST1	= 1;
	private final static int 		placeDIGEST2	= 2;
	private final static int 		placeDIGEST3	= 3;
	private final static int 		placePBEKEY		= 4;
	
	/**
	 * The iteration locations (in units of 4-bit shifts)
	 */
	private final static int 		placeITER1		= 5;
	private final static int 		placeITER2		= 6;
	private final static int 		placeITER3		= 7;

	/**
	 * The mask for restricted security 
	 */
	private final static int		maskRESTRICT	= 128;
	
	/**
	 * The Digest/Key types
	 */
	private DigestType	thePrimeDigest		= null;
	private DigestType	theSecondDigest		= null;
	private DigestType	theThirdDigest		= null;
	private PBEKeyType	thePBEKeyType		= null;
	private	int			theFirstIteration	= 1024;
	private	int			theSecondIteration	= 1536;
	private	int			theThirdIteration	= 2048;

	/* The iteration counts */
	
	/* Access methods */
	protected	int		getMode() 			{ return theMode; }
	protected	byte[]	getByteMode() 		{ return Utils.BytesFromInteger(theMode); }
	public	boolean		useRestricted() 	{ return useRestricted; }
	public	DigestType	getFirstDigest()	{ return thePrimeDigest; }
	public	DigestType	getSecondDigest()	{ return theSecondDigest; }
	public	DigestType	getThirdDigest()	{ return theThirdDigest; }
	public	PBEKeyType	getPBEKeyType()		{ return thePBEKeyType; }
	public	int			getFirstIterate()	{ return theFirstIteration; }
	public	int			getSecondIterate()	{ return theSecondIteration; }
	public	int			getThirdIterate()	{ return theThirdIteration; }

	/**
	 * Constructor from mode
	 * @param pMode the Control Mode
	 */
	protected PBEKeyMode(int pMode) throws Exception {
		/* Not allowed unless version is zero */
		if (getVersion(pMode) != 0)
			throw new Exception(ExceptionClass.LOGIC,
								"Invalid mode version: " + getVersion(pMode));
		
		/* Set the version */
		setVersion(0);
		
		/* Set restricted */
		setRestricted(getRestricted(pMode));
		
		/* Set Iterations */
		setIterations(getIteration(pMode, placeITER1),
					  getIteration(pMode, placeITER2),
					  getIteration(pMode, placeITER3));

		/* Set Digest values */
		setDigestTypes(DigestType.fromId(getId(pMode, placeDIGEST1)),
					   DigestType.fromId(getId(pMode, placeDIGEST2)),
		   			   DigestType.fromId(getId(pMode, placeDIGEST3)));

		/* Set PBEKey value */
		setPBEKeyType(PBEKeyType.fromId(getId(pMode, placePBEKEY)));
	}

	/**
	 * Standard constructor
	 */
	private PBEKeyMode() {
		setVersion(0);
	}
	
	/**
	 * Construct a random KeyMode
	 * @param useRestricted use restricted keys
	 * @param pRandom the random generator
	 */
	public static PBEKeyMode getMode(boolean 		useRestricted,
									 SecureRandom 	pRandom) throws Exception {
		/* Create a new PBEKeyMode */
		PBEKeyMode myMode = new PBEKeyMode();

		/* Set restricted flag */
		myMode.setRestricted(useRestricted);
		
		/* Access a random set of SymKeyTypes and DigestTypes */
		PBEKeyType[]  myPBE 	= PBEKeyType.getRandomTypes(1, pRandom);		
		DigestType[]  myDigest	= DigestType.getRandomTypes(3, pRandom);
		
		/* Set digest options */
		myMode.setDigestTypes(myDigest[0], myDigest[1], myDigest[2]);

		/* Set PBE key option */
		myMode.setPBEKeyType(myPBE[0]);
		
		/* Set iterations */
		myMode.setRandomIterations(pRandom);
		
		/* Return the mode */
		return myMode;
	}
	
	/**
	 * Construct a random set of iterations
	 * @param pRandom the random generator
	 */
	protected void setRandomIterations(SecureRandom pRandom) throws Exception {
		/* Access a random set of Iterations */
		int iFirst  = 1 + pRandom.nextInt(15);		
		int iSecond = 1 + pRandom.nextInt(15);		
		int iThird  = 1 + pRandom.nextInt(15);		

		/* Set the iterations */
		setIterations(iFirst, iSecond, iThird);
	}

	/**
	 * Set iteration into mode
	 */
	private void setIteration(int iId, int iPlace) throws Exception {
		/* Ensure that ordinal is store-able within a nibble */
		if ((iId < 1) || (iId > 15))
			throw new Exception(ExceptionClass.LOGIC,
								"Invalid iteration: " + iId);
		
		/* Shift up place nibbles */
		int  iMask	= 15;
		while (iPlace-- > 0) { iId <<= 4; iMask <<= 4; }
		
		/* Add into the mode */
		theMode &= ~iMask;
		theMode |= iId;
	}
	
	/**
	 * Obtain iteration from mode
	 * @param pMode the mode
	 * @param iPlace the place of the id
	 */
	private int getIteration(int pMode, int iPlace) {
		/* Shift down place nibbles */
		int  iId 	= pMode;
		int  iMask	= 15;
		while (iPlace-- > 0) { iId >>= 4; }
		
		/* Extract from the mode */
		iId &= iMask;
		return iId;
	}
	
	/**
	 * Set id into mode
	 */
	private void setId(int iId, int iPlace) throws Exception {
		/* Ensure that ordinal is store-able within a nibble */
		if ((iId < 1) || (iId > 15))
			throw new Exception(ExceptionClass.LOGIC,
								"Invalid id: " + iId);
		
		/* Shift up place bytes */
		int iMask	= 15;
		while (iPlace-- > 0) { iId <<= 4; iMask <<= 4; }
		
		/* Add into the mode */
		theMode &= ~iMask;
		theMode |= iId;
	}
	
	/**
	 * Obtain id from mode
	 * @param pMode the mode
	 * @param iPlace the place of the id
	 */
	private int getId(int pMode, int iPlace) {
		/* Access as a long value and shift down place bytes */
		int  iId 	= pMode;
		int	 iMask	= 15;
		while (iPlace-- > 0) { iId >>= 4; }
		
		/* Extract from the mode */
		iId &= iMask;
		return iId;
	}
	
	/**
	 * Set version into mode
	 */
	private void setVersion(int iVers) {
		/* Shift up place bytes */
		int iMask	 = 7;
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
		int iMask	= 7;
		iVers &= iMask;
		return iVers;
	}
	
	/**
	 * Set restricted into mode
	 * @param useRestricted use restricted keys
	 */
	private void setRestricted(boolean useRestricted) {
		/* Access the mask */
		int iMask	 = maskRESTRICT;
		
		/* Record flag */
		this.useRestricted = useRestricted;
		
		/* Add into the mode */
		theMode &= ~iMask;
		if (useRestricted) theMode |= iMask;
	}
	
	/**
	 * Obtain useRestricted flag from mode
	 * @return should restricted keys be used
	 */
	private boolean getRestricted(int pMode) {
		return ((pMode & maskRESTRICT) != 0);
	}
	
	/**
	 * Set Iterations
	 * @param pFirstIteration the first iteration
	 * @param pSecondIteration the second iteration
	 * @param pThirdIteration the third iteration
	 */
	private void setIterations(int pFirstIteration,
							   int pSecondIteration,
							   int pThirdIteration) throws Exception {
		/* Record the iterations */
		theFirstIteration 	+= pFirstIteration;
		theSecondIteration 	+= pSecondIteration;
		theThirdIteration 	+= pThirdIteration;

		/* Set the values into the mode */
		setIteration(pFirstIteration,  placeITER1);
		setIteration(pSecondIteration, placeITER2);
		setIteration(pThirdIteration,  placeITER3);
	}

	/**
	 * Set Digest types
	 * @param pFirstDigest the first digest type
	 * @param pSecondDigest the second digest type
	 * @param pThirdDigest the third digest type
	 */
	private void setDigestTypes(DigestType pFirstDigest,
								DigestType pSecondDigest,
								DigestType pThirdDigest) throws Exception {
		/* Record the digests */
		thePrimeDigest 	= pFirstDigest;
		theSecondDigest = pSecondDigest;
		theThirdDigest  = pThirdDigest;

		/* Set the values into the mode */
		setId(pFirstDigest.getId(), placeDIGEST1);
		setId(pSecondDigest.getId(), placeDIGEST2);
		setId(pThirdDigest.getId(), placeDIGEST3);
	}

	/**
	 * Set PBEKeyType type
	 * @param pPBEKeyType the PBE key type
	 */
	private void setPBEKeyType(PBEKeyType pPBEKeyType) throws Exception {
		/* Record the key type */
		thePBEKeyType 	= pPBEKeyType;

		/* Set the value into the mode */
		setId(pPBEKeyType.getId(), placePBEKEY);
	}
}
