package uk.co.tolcroft.security;

import javax.crypto.Cipher;

import org.bouncycastle.util.Arrays;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class SecurityCipher {
	/**
	 * Buffer size for transfers
	 */
	protected final static int  BUFSIZE   		= 1024;	

	/**
	 * The cipher
	 */
	private Cipher 				theCipher 		= null;
		
	/**
	 * The transfer buffer
	 */
	private byte[]    			theBuffer		= null;
	
	/**
	 * The initialisation vector
	 */
	private byte[]    			theInitVector	= null;
	
	/**
	 * Obtain the output buffer
	 * @return the output buffer
	 */
	public byte[] getBuffer() { return theBuffer; }
	
	/**
	 * Constructor
	 * @param pCipher the cipher
	 * @param pVector the initialisation vector
	 */
	protected SecurityCipher(Cipher pCipher,
							 byte[]	pVector) {
		theCipher 		= pCipher;
		theInitVector	= pVector;
		theBuffer		= new byte[BUFSIZE];
	}
	
	/**
	 * Get Initialisation vector
	 * @return the initialisation vector
	 */
	public byte[] getInitVector() {
		return theInitVector;
	}
	
	/**
	 * Encrypt string
	 * @param pString string to encrypt
	 * @return Encrypted bytes
	 * @throws Exception 
	 */
	public byte[] encryptString(String pString) throws Exception {
		byte[] myBytes;
		
		/* Protect against exceptions */
		try {
			/* Convert the string to a byte array */
			myBytes = pString.getBytes(SecurityControl.ENCODING);
			
			/* Encrypt the byte array */
			myBytes = theCipher.doFinal(myBytes);
		}
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
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
	public byte[] encryptChars(char[] pChars) throws Exception {
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
			throw new Exception(ExceptionClass.CRYPTO,
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
	public String decryptString(byte[] pBytes) throws Exception {
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
			throw new Exception(ExceptionClass.CRYPTO,
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
	public char[] decryptChars(byte[] pBytes) throws Exception {
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
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to decrypt character array",
								e);
		}
		
		/* Return to caller */
		return myChars;
	}		

	/**
	 * Update Cipher
	 * @param pBytes Bytes to update cipher with
	 * @param pOffset offset within pBytes to read bytes from
	 * @param pLength length of data to update with
	 * @return number of bytes transferred to output buffer 
	 */
	public int update(byte[] pBytes, int pOffset, int pLength) throws Exception {
		int iNumBytes;
		
		/* Protect against exceptions */
		try {
			/* Check how long a buffer we need */
			iNumBytes = theCipher.getOutputSize(pLength);
		
			/* Extend the buffer if required */
			if (iNumBytes > theBuffer.length)
				theBuffer = new byte[iNumBytes];
		
			/* Update the data */
			iNumBytes = theCipher.update(pBytes, pOffset, pLength, theBuffer);					
		}
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to update cipher",
								e);
		}
		
		/* Return to caller */
		return iNumBytes;
	}
	
	/**
	 * Finish Cipher encrypting/decrypting any data buffered within the cipher
	 * @return number of bytes transferred to output buffer 
	 */
	public int finish() throws Exception {
		int iNumBytes;
		
		/* Protect against exceptions */
		try {
			/* Check how long a buffer we need to handle buffered data*/
			iNumBytes = theCipher.getOutputSize(0);
		
			/* Extend the buffer if required */
			if (iNumBytes > theBuffer.length)
				theBuffer = new byte[iNumBytes];
		
			/* Update the data */
			iNumBytes = theCipher.doFinal(theBuffer, 0);
		}
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to finish cipher operation",
								e);
		}
		
		/* Return to caller */
		return iNumBytes;
	}
}
