package uk.co.tolcroft.security;

import java.security.*;

import javax.crypto.*;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class SecurityControl {
	/**
	 * Message Digest algorithm
	 */
	protected final static String 	DIGEST 		= "SHA-256";
	
	/**
	 * Byte encoding
	 */
	public final static String 		ENCODING	= "UTF-8";
	
	/**
	 * The public/private separator 
	 */
	protected final static char		KEYSEP 		= '!';
	
	/**
	 * The secure random generator
	 */
	private SecureRandom			theRandom		= null;
	
	/**
	 * The secret key generator
	 */
	private KeyGenerator			theKeyGen		= null;

	/**
	 * The key pair generator
	 */
	private KeyPairGenerator		thePairGen		= null;

	/**
	 * The password key 
	 */
	private AsymmetricKey			theAsymKey		= null;

	/**
	 * The password key 
	 */
	private PasswordKey				thePassKey		= null;

	/**
	 * Constructor
	 * @param pSecurityKey the Encoded Security Bytes (or null if first initialisation)  
	 * @param pPassword the password (cleared after usage)
	 */
	public SecurityControl(String	pSecurityKey,
						   char[] 	pPassword) throws WrongPasswordException, Exception {
		/* Protect against exceptions */
		try { 
			/* Create a new secure random generator */
			theRandom 	= new SecureRandom();

			/* Create the secret key generator */
			theKeyGen 	= KeyGenerator.getInstance(SymmetricKey.ALGORITHM);
			theKeyGen.init(SymmetricKey.KEYSIZE, theRandom);

			/* Create an instance of the asymmetric key generator */
			thePairGen  = KeyPairGenerator.getInstance(AsymmetricKey.ALGORITHM);
			thePairGen.initialize(AsymmetricKey.KEYSIZE, theRandom);
		
			/* If the security key is null */
			if (pSecurityKey == null) {
				/* Generate the password key */
				thePassKey 	= new PasswordKey(pPassword, theRandom);
							
				/* Create the asymmetric key */
				theAsymKey  = new AsymmetricKey(thePairGen.generateKeyPair(), thePassKey);			
			}
			
			/* Else we need to decode the keys */
			else {
				/* Generate the password key */
				thePassKey 	= new PasswordKey(pSecurityKey, pPassword, theRandom);
						
				/* Create the asymmetric key */
				theAsymKey  = new AsymmetricKey(thePassKey.getKeyPair(), pSecurityKey, thePassKey);
			}
		}
		
		catch (WrongPasswordException e) { throw e; }

		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Failed to initialise security control",
								e);
		}
	}
	
	/**
	 * Obtain the security key 
	 * @return the security key
	 */
	public String		getSecurityKey() throws Exception {
		/* Access the Asymmetric keys security key */
		return theAsymKey.getSecurityKey();
	}
	
	/**
	 * Obtain the public security key 
	 * @return the public security key
	 */
	public String		getPublicKey() throws Exception {
		/* Access the Asymmetric keys security key */
		return theAsymKey.getPublicKey();
	}
	
	/**
	 * Obtain the signature for the file entry
	 * @param pEntry the ZipFile properties
	 * @return the signature 
	 * @throws finObject.Exception if there are any errors
	 */
	public byte[] signFile(ZipFileEntry pEntry) throws Exception {
		/* Sign the file */
		return theAsymKey.signFile(pEntry);		
	}
	
	/**
	 * Verify the signature for the zipFileEntry
	 * @param pEntry the ZipFile properties
	 */
	public void verifyFile(ZipFileEntry pEntry) throws Exception {
		/* verify the file */
		theAsymKey.verifyFile(pEntry);		
	}
	
	/**
	 * Generate a new PasswordKey 
	 * @param pPassword the password (cleared after usage)
	 * @return the Password key
	 */
	public PasswordKey	getPasswordKey(char[]	pPassword) throws Exception {
		PasswordKey 	myPassKey;
		
		/* Generate the password key class */
		myPassKey = new PasswordKey(pPassword, theRandom);
		
		/* Return the new key */
		return myPassKey;
	}
	
	/**
	 * Generate a new PasswordKey for an existing salt
	 * @param pPassword the password (cleared after usage)
	 * @param pSaltAndHash the Salt And Hash array for the password (null if password not set) 
	 * @return the Password key
	 */
	public PasswordKey	getPasswordKey(char[]	pPassword,
									   byte[]	pSaltAndHash) throws WrongPasswordException, Exception {
		PasswordKey 	myPassKey;
		
		/* Generate the password key class */
		myPassKey = new PasswordKey(pSaltAndHash, pPassword, theRandom);
		
		/* Return the new key */
		return myPassKey;
	}
	
	/**
	 * Generate a new AsymmetricKey 
	 * @return the Asymmetric key
	 */
	public AsymmetricKey	getAsymmetricKey() throws Exception {
		AsymmetricKey 	myAsymKey;
		
		/* Generate the asymmetric key class */
		myAsymKey = new AsymmetricKey(thePairGen.generateKeyPair(), thePassKey);
		
		/* Return the new key */
		return myAsymKey;
	}
	
	/**
	 * Generate a new AsymmetricKey 
	 * @param pSecurityKey the SecurityKey for the key 
	 * @return the Asymmetric key
	 */
	public AsymmetricKey	getAsymmetricKey(String pSecurityKey) throws Exception {
		AsymmetricKey 	myAsymKey;
		KeyPair			myKeyPair;
		
		/* Access the KeyPair */
		myKeyPair = thePassKey.getKeyPair(pSecurityKey);
		
		/* Generate the asymmetric key class */
		myAsymKey = new AsymmetricKey(myKeyPair, pSecurityKey, thePassKey);
		
		/* Return the new key */
		return myAsymKey;
	}
	
	/**
	 * Generate a new SymmetricKey 
	 * @return the Symmetric key
	 */
	public SymmetricKey	getSymmetricKey() throws Exception {
		SecretKey 		myKey;
		SymmetricKey 	mySymKey;
		
		/* Generate the Secret key */
		myKey = theKeyGen.generateKey();
		
		/* Generate the symmetric key class */
		mySymKey = new SymmetricKey(this, myKey, theRandom);
		
		/* Return the new key */
		return mySymKey;
	}
	
	/**
	 * Generate a SymmetricKey from wrapped key
	 * @param pWrappedKey the wrapped key
	 * @return the Symmetric key
	 */
	public SymmetricKey	getSymmetricKey(byte[] pWrappedKey) throws Exception {
		SecretKey 		myKey;
		SymmetricKey 	mySymKey;
		
		/* Protect against exceptions */
		try {			
			/* unwrap the key */
			myKey = theAsymKey.unwrapSecretKey(pWrappedKey);

			/* Generate the symmetric key class */
			mySymKey = new SymmetricKey(this, myKey, pWrappedKey, theRandom);
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Failed to unwrap key",
								e);
		}
		
		/* Return the new key */
		return mySymKey;
	}
	
	/**
	 * Wrap secret key
	 * @param pKey the Key to wrap  
	 * @return the wrapped secret key
	 */
	protected byte[] getWrappedKey(SymmetricKey pKey) throws Exception {
		byte[] 				myWrappedKey;
		
		/* Protect against exceptions */
		try {			
			/* wrap the key */
			myWrappedKey = theAsymKey.wrapSecretKey(pKey.getSecretKey());
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Failed to wrap key",
								e);
		}
		
		/* Return to caller */
		return myWrappedKey;
	}	
}
