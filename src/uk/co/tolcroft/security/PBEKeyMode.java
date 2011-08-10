package uk.co.tolcroft.security;

import java.security.SecureRandom;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.security.SecurityControl.DigestType;
import uk.co.tolcroft.security.PasswordKey.PBEKeyType;

public class PBEKeyMode {
	/**
	 * The mode	
	 */
	private int		theMode		= 0;
	
	/**
	 * The type locations (in units of 4-bit shifts)
	 */
	private final static int 		placeDIGEST1	= 1;
	private final static int 		placeDIGEST2	= 2;
	private final static int 		placePBEKEY		= 3;
	
	/**
	 * The iteration locations (in units of 4-bit shifts)
	 */
	private final static int 		placeITER1		= 4;
	private final static int 		placeITER2		= 5;
	private final static int 		placeITER3		= 6;

	/**
	 * The Digest/Key types
	 */
	private DigestType	thePrimeDigest		= null;
	private DigestType	theSecondDigest		= null;
	private PBEKeyType	thePBEKeyType		= null;
	private	int			theFirstIteration	= 1024;
	private	int			theSecondIteration	= 1536;
	private	int			theThirdIteration	= 2048;

	/* The iteration counts */
	
	/* Access methods */
	protected	int		getMode() 			{ return theMode; }
	protected	byte[]	getByteMode() 		{ return Utils.BytesFromInteger(theMode); }
	public	DigestType	getFirstDigest()	{ return thePrimeDigest; }
	public	DigestType	getSecondDigest()	{ return theSecondDigest; }
	public	PBEKeyType	getPBEKeyType()		{ return thePBEKeyType; }
	public	int			getFirstIterate()	{ return theFirstIteration; }
	public	int			getSecondIterate()	{ return theSecondIteration; }
	public	int			getThirdIterate()	{ return theThirdIteration; }

	/**
	 * Constructor from node
	 * @param pMode the Control Mode
	 */
	protected PBEKeyMode(int pMode) throws Exception {
		/* Not allowed unless version is zero */
		if (getVersion(pMode) != 0)
			throw new Exception(ExceptionClass.LOGIC,
								"Invalid mode version: " + getVersion(pMode));
		
		/* Set the version */
		setVersion(0);
		
		/* Set Iterations */
		setIterations(getIteration(pMode, placeITER1),
					  getIteration(pMode, placeITER2),
					  getIteration(pMode, placeITER3));

		/* Set Digest values */
		setDigestTypes(DigestType.fromId(getId(pMode, placeDIGEST1)),
					   DigestType.fromId(getId(pMode, placeDIGEST2)));

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
	 * Construct a ControlMode
	 * @param pFirstDigest the first digest type
	 * @param pSecondDigest the second digest type
	 * @param pPBEKeyType the PBE key type
	 * @param pAsymKeyType the Asym Key type
	 * @param pRandom the random generator
	 */
	public static PBEKeyMode getMode(DigestType		pFirstDigest,
				   					 DigestType		pSecondDigest,
				   					 PBEKeyType		pPBEKeyType,
				   					 SecureRandom	pRandom) throws Exception {
		/* Create a new PBEKeyMode */
		PBEKeyMode myMode = new PBEKeyMode();

		/* Set digest options */
		myMode.setDigestTypes(pFirstDigest, pSecondDigest);

		/* Set PBE key option */
		myMode.setPBEKeyType(pPBEKeyType);
		
		/* Set iterations */
		myMode.setRandomIterations(pRandom);
		
		/* Return the mode */
		return myMode;
	}
	
	/**
	 * Construct a random KeyMode
	 * @param pRandom the random generator
	 */
	public static PBEKeyMode getMode(SecureRandom pRandom) throws Exception {
		/* Create a new PBEKeyMode */
		PBEKeyMode myMode = new PBEKeyMode();

		/* Access a random set of SymKeyTypes and DigestTypes */
		PBEKeyType[]  myPBE 	= PBEKeyType.getRandomTypes(1, pRandom);		
		DigestType[]  myDigest	= DigestType.getRandomTypes(2, pRandom);
		
		/* Set digest options */
		myMode.setDigestTypes(myDigest[0], myDigest[1]);

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
		while (iPlace-- > 0) { iId *= 16; iMask *= 16; }
		
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
		while (iPlace-- > 0) { iId /= 16; }
		
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
		while (iPlace-- > 0) { iId *= 16; iMask *= 16; }
		
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
		while (iPlace-- > 0) { iId /= 16; }
		
		/* Extract from the mode */
		iId &= iMask;
		return iId;
	}
	
	/**
	 * Set version into mode
	 */
	private void setVersion(int iVers) {
		/* Shift up place bytes */
		int iMask	 = 15;
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
		int iMask	= 15;
		iVers &= iMask;
		return iVers;
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
	 */
	private void setDigestTypes(DigestType pFirstDigest,
								DigestType pSecondDigest) throws Exception {
		/* Record the digests */
		thePrimeDigest 	= pFirstDigest;
		theSecondDigest = pSecondDigest;

		/* Set the values into the mode */
		setId(pFirstDigest.getId(), placeDIGEST1);
		setId(pSecondDigest.getId(), placeDIGEST2);
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
