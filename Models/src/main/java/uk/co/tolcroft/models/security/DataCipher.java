package uk.co.tolcroft.models.security;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.util.Arrays;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.security.SymmetricKey.SymKeyType;

public class DataCipher {
	/**
	 * The cipher
	 */
	private Cipher 				theCipher 		= null;
		
	/**
	 * The SymmetricKey (if used)
	 */
	private SymmetricKey 		theSymKey 		= null;
		
	/**
	 * Get Symmetric Key Type
	 */
	protected SymKeyType getSymKeyType() { return (theSymKey == null) ? null : theSymKey.getKeyType(); }
	
	/**
	 * Constructor
	 * @param pCipher the initialised cipher
	 */
	protected DataCipher(Cipher 		pCipher) {
		theCipher 		= pCipher;
	}
	
	/**
	 * Constructor
	 * @param pCipher the uninitialised cipher
	 * @param pKey the Symmetric Key
	 */
	protected DataCipher(Cipher 		pCipher,
						 SymmetricKey 	pKey) {
		theCipher 		= pCipher;
		theSymKey		= pKey;
	}
	
	/**
	 * Encrypt bytes
	 * @param pBytes bytes to encrypt
	 * @param pVector initialisation vector
	 * @return Encrypted bytes
	 * @throws ModelException 
	 */
	public byte[] encryptBytes(byte[] pBytes,
							   byte[] pVector) throws ModelException {
		byte[]					myBytes;
		
		/* Protect against exceptions */
		try {
			/* Initialise the cipher using the vector */
			initialiseEncryption(pVector);
			
			/* Encrypt the byte array */
			myBytes = theCipher.doFinal(pBytes);
		}
		catch (Throwable e) {
			throw new ModelException(ExceptionClass.CRYPTO,
								"Failed to encrypt bytes",
								e);
		}
		
		/* Return to caller */
		return myBytes;
	}		
	
	/**
	 * Decrypt bytes
	 * @param pBytes bytes to decrypt
	 * @param pVector initialisation vector
	 * @return Decrypted bytes
	 * @throws ModelException 
	 */
	public byte[] decryptBytes(byte[] pBytes,
							   byte[] pVector) throws ModelException {
		byte[]					myBytes;
		
		/* Protect against exceptions */
		try {
			/* Initialise the cipher using the vector */
			initialiseDecryption(pVector);
			
			/* Encrypt the byte array */
			myBytes = theCipher.doFinal(pBytes);
		}
		catch (Throwable e) {
			throw new ModelException(ExceptionClass.CRYPTO,
								"Failed to decrypt bytes",
								e);
		}
		
		/* Return to caller */
		return myBytes;
	}		
	
	/**
	 * Encrypt string
	 * @param pString string to encrypt
	 * @return Encrypted bytes
	 * @throws ModelException 
	 */
	public byte[] encryptString(String pString) throws ModelException {
		byte[] myBytes;
		
		/* Protect against exceptions */
		try {
			/* Convert the string to a byte array */
			myBytes = pString.getBytes(SecurityControl.ENCODING);
			
			/* Encrypt the byte array */
			myBytes = theCipher.doFinal(myBytes);
		}
		catch (Throwable e) {
			throw new ModelException(ExceptionClass.CRYPTO,
								"Failed to encrypt string",
								e);
		}
		
		/* Return to caller */
		return myBytes;
	}		
	
	/**
	 * Encrypt character array
	 * @param pChars Characters to encrypt
	 * @return Encrypted bytes
	 */
	public byte[] encryptChars(char[] pChars) throws ModelException {
		byte[] myBytes;
		byte[] myRawBytes;
		
		/* Protect against exceptions */
		try {
			/* Convert the characters to a byte array */
			myRawBytes = Utils.charToByteArray(pChars);
			
			/* Encrypt the characters */
			myBytes = theCipher.doFinal(myRawBytes);
						
			/* Clear out the bytes */
			Arrays.fill(myRawBytes, (byte)0);
		}
		catch (Throwable e) {
			throw new ModelException(ExceptionClass.CRYPTO,
								"Failed to encrypt character array",
								e);
		}
		
		/* Return to caller */
		return myBytes;
	}		
	
	/**
	 * Decrypt bytes into a string
	 * @param pBytes bytes to decrypt
	 * @return Decrypted string
	 */
	public String decryptString(byte[] pBytes) throws ModelException {
		byte[] 	myBytes;
		String	myString;
		
		/* Protect against exceptions */
		try {
			/* Decrypt the bytes */
			myBytes  = theCipher.doFinal(pBytes);
			
			/* Convert the bytes to a string */ 
			myString = new String(myBytes, SecurityControl.ENCODING);
		}
		catch (Throwable e) {
			throw new ModelException(ExceptionClass.CRYPTO,
								"Failed to decrypt string",
								e);
		}
		
		/* Return to caller */
		return myString;
	}		

	/**
	 * Decrypt bytes into a character array
	 * @param pBytes Bytes to decrypt
	 * @return Decrypted character array
	 */
	public char[] decryptChars(byte[] pBytes) throws ModelException {
		byte[] 	myBytes;
		char[]	myChars;
		
		/* Protect against exceptions */
		try {
			/* Decrypt the bytes */
			myBytes  = theCipher.doFinal(pBytes);
			
			/* Convert the bytes to characters */ 
			myChars = Utils.byteToCharArray(myBytes);
			
			/* Clear out the bytes */
			Arrays.fill(myBytes, (byte)0);
		}
		catch (Throwable e) {
			throw new ModelException(ExceptionClass.CRYPTO,
								"Failed to decrypt character array",
								e);
		}
		
		/* Return to caller */
		return myChars;
	}		

	/**
	 * Initialise encryption
	 * @param pVector initialisation vector
	 */
	private void initialiseEncryption(byte[] pVector) throws Throwable {
		AlgorithmParameterSpec 	myParms;

		/* If we have a symmetric key */
		if (theSymKey != null) {
			/* Initialise the cipher using the vector */
			myParms = new IvParameterSpec(pVector);
			theCipher.init(Cipher.ENCRYPT_MODE, theSymKey.getSecretKey(), myParms);
		}
	}			

	/**
	 * Initialise decryption
	 * @param pVector initialisation vector
	 */
	private void initialiseDecryption(byte[] pVector) throws Throwable {
		AlgorithmParameterSpec 	myParms;

		/* If we have a symmetric key */
		if (theSymKey != null) {
			/* Initialise the cipher using the vector */
			myParms = new IvParameterSpec(pVector);
			theCipher.init(Cipher.DECRYPT_MODE, theSymKey.getSecretKey(), myParms);
		}
	}			
}
