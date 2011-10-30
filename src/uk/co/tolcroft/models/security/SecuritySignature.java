package uk.co.tolcroft.models.security;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.security.AsymmetricKey.AsymKeyType;
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
	 * The AsymKeyType
	 */
	private AsymKeyType					theKeyType				= null;
	
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
	public AsymKeyType			getKeyType()			{ return theKeyType; }
	public byte[]				getPublicKey()			{ return thePublicKey; }
	public byte[]				getSecuredKeyDef()		{ return theSecuredKeyDef; }
	
	protected void setPasswordHash(byte[] pHash)		{ thePasswordHash = pHash; }
	protected void setSecuredKeyDef(byte[] pKeyDef)		{ theSecuredKeyDef = pKeyDef; }
	
	/**
	 * Constructor
	 * @param pPasswordHash the password hash
	 * @param pKeyType the Asymmetric Key type
	 * @param pPublicKey the public key (in X509 format)
	 * @param pSecuredKeyDef the secured private key
	 */
	public SecuritySignature(byte[] 			pPasswordHash,
							 AsymKeyType 		pKeyType,
							 byte[]				pPublicKey,
							 byte[]				pSecuredKeyDef) {
		/* Store the values */
		thePasswordHash 	= pPasswordHash;
		theKeyType			= pKeyType;
		thePublicKey		= pPublicKey;
		theSecuredKeyDef	= pSecuredKeyDef;
	}
	
	/**
	 * Constructor
	 * @param pSignature the signature
	 */
	public SecuritySignature(String	pSignature) throws Exception {
		/* Split the signature */
		String[] myTokens = pSignature.split(KEYSEP);
		
		/* Must have four parts */
		if (myTokens.length != 4)
			throw new Exception(ExceptionClass.DATA,
								"Invalid Signature - " + pSignature);
		
		/* Store the values */
		thePasswordHash 	= Utils.BytesFromHexString(myTokens[0]);
		thePublicKey		= Utils.BytesFromHexString(myTokens[1]);
		theSecuredKeyDef	= Utils.BytesFromHexString(myTokens[2]);
		theKeyType			= AsymKeyType.fromId(Integer.parseInt(myTokens[3]));
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
		myBuilder.append(Integer.toString(theKeyType.getId()));
		
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
		hashCode += theKeyType.getId();
		return hashCode;
	}
	
	/**
	 * Compare this signature to another for equality 
	 * @param pThat the key to compare to
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
		if (myThat.theKeyType != theKeyType) return false;
		
		/* Ensure that the private/public keys and password are identical */
		if (Utils.differs(myThat.thePasswordHash,   thePasswordHash).isDifferent()) 	return false;
		if (Utils.differs(myThat.theSecuredKeyDef,  theSecuredKeyDef).isDifferent()) 	return false;
		if (Utils.differs(myThat.thePublicKey, 		thePublicKey).isDifferent()) 		return false;
		
		/* Identical if those tests succeed */
		return true;
	}
}
