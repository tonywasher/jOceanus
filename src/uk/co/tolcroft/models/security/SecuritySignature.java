package uk.co.tolcroft.models.security;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.Utils;

public class SecuritySignature {
	/**
	 * The public/private separator 
	 */
	protected final static String		KEYSEP 					= "!";
	
	/**
	 * The password hash
	 */
	private byte[]						thePasswordHash			= null;

	/**
	 * The Security Mode
	 */
	private SecurityMode				theKeyMode				= null;
	
	/**
	 * The public key
	 */
	private byte[]						thePublicKey			= null;

	/**
	 * The secured private key
	 */
	private byte[]						theSecuredKeyDef		= null;
	
	/* Access methods */
	public byte[]				getPasswordHash()		{ return thePasswordHash; }
	public SecurityMode			getKeyMode()			{ return theKeyMode; }
	public byte[]				getPublicKey()			{ return thePublicKey; }
	public byte[]				getSecuredKeyDef()		{ return theSecuredKeyDef; }
	
	protected void setPasswordHash(byte[] pHash)		{ thePasswordHash = pHash; }
	protected void setSecuredKeyDef(byte[] pKeyDef)		{ theSecuredKeyDef = pKeyDef; }
	
	/**
	 * Constructor
	 * @param pPasswordHash the password hash
	 * @param pKeyMode the Asymmetric Key mode
	 * @param pPublicKey the public key (in X509 format)
	 * @param pSecuredKeyDef the secured private key
	 */
	public SecuritySignature(byte[] 		pPasswordHash,
							 SecurityMode	pKeyMode,
							 byte[]			pPublicKey,
							 byte[]			pSecuredKeyDef) {
		/* Store the values */
		thePasswordHash 	= pPasswordHash;
		theKeyMode			= pKeyMode;
		thePublicKey		= pPublicKey;
		theSecuredKeyDef	= pSecuredKeyDef;
	}
	
	/**
	 * Constructor
	 * @param pSignature the signature
	 */
	public SecuritySignature(String	pSignature) throws ModelException {
		/* Split the signature */
		String[] myTokens = pSignature.split(KEYSEP);
		
		/* Must have four parts */
		if (myTokens.length != 4)
			throw new ModelException(ExceptionClass.DATA,
								"Invalid Signature - " + pSignature);
		
		/* Store the values */
		thePasswordHash 	= Utils.BytesFromHexString(myTokens[0]);
		thePublicKey		= Utils.BytesFromHexString(myTokens[1]);
		theSecuredKeyDef	= Utils.BytesFromHexString(myTokens[2]);
		theKeyMode			= new SecurityMode(Integer.parseInt(myTokens[3]));
	}
	
	/**
	 * Get Signature for Security
	 * @return the signature
	 */
	public String getSignature() {
		StringBuilder myBuilder = new StringBuilder(10000);
		
		/* Add the Password Hash */
		myBuilder.append(Utils.HexStringFromBytes(thePasswordHash));
		myBuilder.append(KEYSEP);

		/* Add the Public Key */
		myBuilder.append(Utils.HexStringFromBytes(thePublicKey));
		myBuilder.append(KEYSEP);

		/* Add the PrivateKey */
		myBuilder.append(Utils.HexStringFromBytes(theSecuredKeyDef));
		myBuilder.append(KEYSEP);

		/* Add the KeyType */
		myBuilder.append(Integer.toString(theKeyMode.getMode()));
		
		/* Return the String */
		return myBuilder.toString();
	}
	
	/**
	 * Hash for the Asymmetric Key
	 * @return the hash value
	 */
	public int hashCode() {
		/* Calculate and return the hashCode for this signature */
		int hashCode = 19 * thePublicKey.hashCode();
		hashCode += theSecuredKeyDef.hashCode();
		hashCode *= 19;
		hashCode += thePasswordHash.hashCode();
		hashCode *= 19;
		hashCode += theKeyMode.getMode();
		return hashCode;
	}
	
	/**
	 * Compare this signature to another for equality 
	 * @param pThat the signature to compare to
	 * @return <code>true/false</code> 
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a SecuritySignature */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the target signature */
		SecuritySignature myThat = (SecuritySignature)pThat;
	
		/* Not equal if different key-types */
		if (myThat.theKeyMode.getMode() != theKeyMode.getMode()) return false;
		
		/* Ensure that the private/public keys and password are identical */
		if (Utils.differs(myThat.thePasswordHash,   thePasswordHash).isDifferent()) 	return false;
		if (Utils.differs(myThat.theSecuredKeyDef,  theSecuredKeyDef).isDifferent()) 	return false;
		if (Utils.differs(myThat.thePublicKey, 		thePublicKey).isDifferent()) 		return false;
		
		/* Identical if those tests succeed */
		return true;
	}

	/**
	 * Compare this signature to another for sortOrder 
	 * @param pThat the signature to compare to
	 * @return <code>true/false</code> 
	 */
	public int compareTo(Object pThat) {
		int iDiff;
		
		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is a SecuritySignature */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the target signature */
		SecuritySignature myThat = (SecuritySignature)pThat;
	
		/* Not equal if different modes */
		Integer myMode 		= theKeyMode.getMode();
		Integer myCompMode 	= myThat.theKeyMode.getMode();
		if ((iDiff = myCompMode.compareTo(myMode)) != 0) return iDiff;
		
		/* Compare the private/public keys and password */
		if ((iDiff = Utils.compareTo(thePasswordHash,  myThat.thePasswordHash)) != 0) 	return iDiff;
		if ((iDiff = Utils.compareTo(theSecuredKeyDef, myThat.theSecuredKeyDef)) != 0) 	return iDiff;
		if ((iDiff = Utils.compareTo(thePublicKey,     myThat.thePublicKey)) != 0) 		return iDiff;
		
		/* Identical if those tests succeed */
		return 0;
	}
}
