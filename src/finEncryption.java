package finance;

import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;
import java.util.Arrays;
import finance.finObject.ExceptionClass;

/**
 * Provides encryption and compression services for backups. 
 * @author 	Tony Washer
 * @version 1.0
 */
public class finEncryption {
	/**
	 * Buffer size for transfers
	 */
	private final static int    BUFSIZE   		= 1024;
	
	/**
	 * Key size for symmetric algorithm
	 */
	private final static int    SYMKEYSIZE   	= 256;
	
	/**
	 * Key size for asymmetric algorithm
	 */
	private final static int    ASYMKEYSIZE   	= 2048;
	
	/**
	 * Symmetric algorithm
	 */
	private final static String SYMALGORITHM 	= "AES";
	
	/**
	 * Password Based Encryption algorithm
	 */
	private final static String PBEALGORITHM 	= "PBEWithSHA1AndDESede";
	
	/**
	 * Symmetric full algorithm
	 */
	private final static String FULLSYMALGORITHM= "AES/CBC/PKCS5PADDING";
	
	/**
	 * Public/Private key algorithm
	 */
	private final static String RSAALGORITHM 	= "RSA";
	
	/**
	 * Message Digest algorithm
	 */
	private final static String DIGEST 			= "SHA-256";
	
	/**
	 * Signature algorithm
	 */
	private final static String SIGNATURE		= "SHA256withRSA";
	
	/**
	 * Byte encoding
	 */
	private final static String ENCODING		= "UTF-8";
	
	/**
	 * The Public/Private Key Pair
	 */
	private static KeyPair				theKeyPair		= null;
	
	/**
	 * The Database Key 
	 */
	private static SecretKey			theDatabaseKey	= null;
	
	/**
	 * The secure random generator
	 */
	private static 	SecureRandom		theRandom		= null;
	
	/**
	 * The secret key generator
	 */
	private static	KeyGenerator		theKeyGen;

	/**
	 * The properties
	 */
	private static finProperties		theProperties	= null;
	
	/**
	 * The password utilities
	 */
	private static passwordUtil			thePasswordUtil	= null;
	
	/**
	 * Have we initialised successfully 
	 */
	private static boolean				hasInitialised	= false;

	/**
	 * Provides signature creation verification for a file
	 */
	public static class finSignature {
		
		/**
		 * Obtain the signature for the file entry
		 * @param pEntry the ZipFile properties
		 * @return the signature 
		 * @throws finObject.Exception if there are any errors
		 */
		public static byte[] signFile(finZipFile.zipFileEntry pEntry) throws finObject.Exception {
			byte[]			 	myKeyEnc;
			byte[]			 	myEncDigest;
			byte[]			 	myCompDigest;
			byte[]			 	myRawDigest;
			byte[]				myInitVector;
			byte[]			 	mySignEnc;	
			Signature		 	mySignature;
			
			/* Protect against exceptions */
			try { 
				/* Access the parameters for the entry */
				myEncDigest 	= pEntry.getEncryptedDigest();
				myCompDigest 	= pEntry.getCompressedDigest();
				myRawDigest 	= pEntry.getRawDigest();
				myInitVector 	= pEntry.getInitVector();
				myKeyEnc 		= pEntry.getSecretKey();

				/* Create a signature */
				mySignature = Signature.getInstance(SIGNATURE);
				
				/* Sign the sender key and digest using the private key */
				mySignature.initSign(theKeyPair.getPrivate());
				if (myKeyEnc 	 != null) mySignature.update(myKeyEnc);
				if (myInitVector != null) mySignature.update(myInitVector);
				if (myEncDigest  != null) mySignature.update(myEncDigest);
				if (myCompDigest != null) mySignature.update(myCompDigest);
				if (myRawDigest  != null) mySignature.update(myRawDigest);
				
				/* Complete the signature */
				mySignEnc = mySignature.sign();
			} 
		
			/* Catch exceptions */
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.ENCRYPT,
											  "Exception calculating signature",
											  e);
			}
			
			/* Return the signature */
			return mySignEnc;
		}		
		
		/**
		 * Verify the signature for the zipFileEntry
		 * @param pEntry the ZipFile properties
		 */
		public static void verifyFile(finZipFile.zipFileEntry pEntry) throws finObject.Exception {
			byte[]			 		myKeyEnc;
			byte[]			 		myEncDigest;
			byte[]			 		myCompDigest;
			byte[]			 		myRawDigest;
			byte[]					myInitVector;
			byte[]			 		mySignEnc;	
			Signature		 		mySignature;
			
			/* Protect against exceptions */
			try { 
				/* Access the parameters for the entry */
				myEncDigest 	= pEntry.getEncryptedDigest();
				myCompDigest 	= pEntry.getCompressedDigest();
				myRawDigest 	= pEntry.getRawDigest();
				myInitVector 	= pEntry.getInitVector();
				myKeyEnc 		= pEntry.getSecretKey();
				mySignEnc 		= pEntry.getSignature();

				/* Create a signature */
				mySignature = Signature.getInstance(SIGNATURE);
				
				/* Verify the signature */
				mySignature.initVerify(theKeyPair.getPublic());
				if (myKeyEnc 	 != null) mySignature.update(myKeyEnc);
				if (myInitVector != null) mySignature.update(myInitVector);
				if (myEncDigest  != null) mySignature.update(myEncDigest);
				if (myCompDigest != null) mySignature.update(myCompDigest);
				if (myRawDigest  != null) mySignature.update(myRawDigest);

				/* Check the signature */
				if (!mySignature.verify(mySignEnc)) {
					/* Throw an invalid file exception */
					throw new finObject.Exception(ExceptionClass.ENCRYPT, 
												  "Signature does not match");
				}
			} 
		
			/* Catch exceptions */
			catch (finObject.Exception e) 	{ throw e; }
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.ENCRYPT,
											  "Exception occurred verifying signature",
											  e);
			}
		}				
	}
	
	/**
	 * Provides a digest OutputStream. This class simply calculates a digest of the data in the stream at this 
	 * point and passes the data onto the next Output Stream in the chain.
	 */
	public static class digestOutputStream extends java.io.OutputStream {
		/**
		 * The underlying output stream
		 */
		private java.io.OutputStream	theStream	= null;

		/**
		 * has this stream been closed
		 */
		private boolean					isClosed 	= false;

		/**
		 * The Message Digest of the data written 
		 */
		private MessageDigest			theDigest	= null;
		
		/** 
		 * The length of the data written 
		 */
		private long					theDataLen	= 0;
		
		/** 
		 * Access the length of the data written
		 * @return the length of data written 
		 */
		public long		getDataLen()	{ return theDataLen; }
		
		/** 
		 * Access the digest of the data written
		 * @return the digest of data written 
		 */
		public byte[]	getDigest()		{ return theDigest.digest(); }
		
		/**
		 * Construct the output stream
		 * @param pFile the file to write encrypted data to
		 * @throws finObject.Exception
		 */
		public digestOutputStream(java.io.OutputStream pStream) throws finObject.Exception {		
			/* Protect against exceptions */
			try {
				/* Create the message digest */
				theDigest = MessageDigest.getInstance(DIGEST);

				/* Store the stream */
				theStream = pStream;
			}
			
			/* Catch exceptions */
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.ENCRYPT,
											  "Exception creating digest output stream",
											  e);
			}			
		}
		
		/**
		 * Close the output stream
		 * @throws IOException
		 */
		public void close() throws IOException {
			/* Null operation if we are already closed */
			if (!isClosed) {
				/* Flush the output stream */
				theStream.flush();
				
				/* Close the output stream */
				theStream.close();
				isClosed = true;
			}
		}
		
		/**
		 * Flush the output stream
		 * @throws IOException
		 */
		public void flush() throws IOException {
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");

			/* Flush the output stream */
			theStream.flush();
		}
		
		/**
		 * Write an array of bytes to the Output stream
		 * @param pBytes the bytes to write
		 * @param pOffset the offset from which to start writing
		 * @param pLength the length of data to write
		 * @throws IOException
		 */
		public void write(byte[] pBytes, int pOffset, int pLength) throws IOException {
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* Update the data digest */
			theDigest.update(pBytes, pOffset, pLength);
		
			/* Adjust the data length */
			theDataLen += pLength;
			
			/* Write the bytes to the stream */
			theStream.write(pBytes, pOffset, pLength);
		}
		
		/**
		 * Write an array of bytes to the Output stream
		 * @param pBytes the bytes to write
		 * @throws IOException
		 */
		public void write(byte[] pBytes) throws IOException {
			/* Write the bytes to the stream */
			write(pBytes, 0, pBytes.length);
		}
		
		/**
		 * Write a byte to the Output stream
		 * @param pByte the byte to write
		 * @throws IOException
		 */
		public void write(int pByte) throws IOException {
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* Update the data digest */
			theDigest.update((byte)pByte);
			
			/* Adjust the data length */
			theDataLen++;
			
			/* Write the byte to the stream */
			theStream.write(pByte);
		}
	}
	
	/**
	 * Provide an encryptOutputStream wrapper. This class simply wraps an output buffer
	 * and encrypts the data before passing it on. It will record a digest of the data after encryption.
	 */
	public static class encryptOutputStream extends java.io.OutputStream {
		/**
		 * The underlying output stream
		 */
		private digestOutputStream 		theStream		= null;

		/**
		 * has this stream been closed
		 */
		private boolean					isClosed 		= false;

		/**
		 * The encryption cipher
		 */
		private Cipher 			 		theCipher 		= null;
		
		/**
		 * The Secret Key
		 */
		private SecretKey 				theKey 			= null;
		
		/**
		 * The Secret Key Encoded
		 */
		private byte[]    				theKeyEncoded	= null;
		
		/**
		 * The initialisation vector
		 */
		private byte[]    				theInitVector	= null;
		
		/**
		 *  A buffer for single byte writes 
		 */
		private byte[] 					theByte 		= new byte[1];
		
		/**
		 * The transfer buffer
		 */
		private byte[]    				theBuffer		= new byte[BUFSIZE];
		
		/** 
		 * Access the length of the data written
		 * @return the length of data written 
		 */
		public long		getDataLen()			{ return theStream.getDataLen(); }
		
		/** 
		 * Access the digest of the data written
		 * @return the digest of data written 
		 */
		public byte[]	getDigest()				{ return theStream.getDigest(); }
		
		/** 
		 * Access the initialisation vector
		 * @return the initialisation vector
		 */
		public byte[]	getInitVector()			{ return theInitVector; }
		
		/** 
		 * Access the initialisation vector
		 * @return the initialisation vector
		 */
		public byte[]	getSecretKeyEncoded()	{ return theKeyEncoded; }
		
		/**
		 * Construct the encrypt output stream
		 * @param pStream the stream to encrypt to
		 * @throws finObject.Exception
		 */
		public encryptOutputStream(java.io.OutputStream pStream) throws finObject.Exception {
			Cipher 	myRSACipher;
			
			/* If we have not yet initialised */
			if (!hasInitialised)
				throw new finObject.Exception(ExceptionClass.LOGIC,
											  "Must initialise encryption first");
			
			/* Protect against exceptions */
			try {
				/* record the output stream */
				theStream 	= new digestOutputStream(pStream);
				
				/* Generate the Secret key */
				theKey = theKeyGen.generateKey();
				
				/* Access the new ciphers */
				theCipher = Cipher.getInstance(FULLSYMALGORITHM);
				
				/* Initialise the cipher generating a random Initialisation vector */
				theCipher.init(Cipher.ENCRYPT_MODE, theKey, theRandom);
				
				/* Access the initialisation vector */
				theInitVector	= theCipher.getIV();
					
				/* Access the new ciphers */
				myRSACipher = Cipher.getInstance(RSAALGORITHM);
				
				/* Initialise the ciphers for encryption */
				myRSACipher.init(Cipher.WRAP_MODE, theKeyPair.getPublic());
				
				/* Encrypt the sender key using the public key */
				theKeyEncoded = myRSACipher.wrap(theKey);
				
				/* Obscure the key */
				theKeyEncoded = obscureArray(theKeyEncoded, true);
			}
			
			/* Catch exceptions */
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.ENCRYPT,
											  "Exception creating encryption output stream",
											  e);
			}			
		}
		
		/**
		 * Close the output stream
		 * @throws IOException
		 */
		public void close() throws IOException {
			
			/* Protect against exceptions */
			try { 
				/* Null operation if we are already closed */
				if (!isClosed) {
					/* Finish the cipher operation */
					int iNumBytes = theCipher.doFinal(theBuffer, 0);
					
					/* If we have data to write then write it */
					if (iNumBytes > 0) theStream.write(theBuffer, 0, iNumBytes);
					
					/* Close the output stream */
					theStream.close();
					isClosed = true;
					
					/* Release allocated buffers */
					theByte   = null;
					theBuffer = null;
					
					/* Release allocated encryption resources */
					theCipher = null;
				}
			}
			
			/* Catch exceptions */
			catch (IOException e) 	{ throw e; }
			catch (Exception e) 	{ throw new IOException(e);	}
		}
		
		/**
		 * Flush the output stream
		 * @throws IOException
		 */
		public void flush() throws IOException {
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");

			/* Flush the output stream */
			theStream.flush();
		}
		
		/**
		 * Write an array of bytes to the Output stream
		 * @param pBytes the bytes to write
		 * @param pOffset the offset from which to start writing
		 * @param pLength the length of data to write
		 * @throws IOException
		 */
		public void write(byte[] pBytes, int pOffset, int pLength)	throws IOException {
			int		iNumBytes;

			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* Ignore a null write */
			if (pLength == 0) return;
		
			/* Protect against exceptions */
			try { 
				/* Check how long a buffer we need */
				iNumBytes = theCipher.getOutputSize(pLength);
			
				/* Extend the buffer if required */
				if (iNumBytes > theBuffer.length)
					theBuffer = new byte[iNumBytes];
			
				/* Encrypt the data */
				iNumBytes = theCipher.update(pBytes, pOffset, pLength, theBuffer);
			
				/* Write the bytes to the stream */
				theStream.write(theBuffer, 0, iNumBytes);
			}
			
			/* Catch exceptions */
			catch (IOException e) 	{ throw e; }
			catch (Exception e) 	{ throw new IOException(e);	}
		}
		
		/**
		 * Write an array of bytes to the Output stream
		 * @param pBytes the bytes to write
		 * @throws IOException
		 */
		public void write(byte[] pBytes) throws IOException {
			/* Write the bytes to the stream */
			write(pBytes, 0, pBytes.length);
		}
		
		/**
		 * Write a byte to the Output stream
		 * @param pByte the byte to write
		 * @throws IOException
		 */
		public void write(int pByte) throws IOException {
			/* Copy the byte to the buffer */
			theByte[0] = (byte)pByte;
			
			/* Write the byte to the stream */
			write(theByte, 0, 1);
		}
	}
	
	/**
	 * Provides a digest InputStream. This class simply calculates a digest of the data in the stream at this 
	 * point as it is read. On close of the file, the digest is validated. 
	 */
	public static class digestInputStream extends java.io.InputStream {
		/**
		 * The underlying input stream
		 */
		private java.io.InputStream	theStream				= null;

		/**
		 * has this stream been closed
		 */
		private boolean				isClosed 				= false;

		/**
		 * The Message Digest of the data read 
		 */
		private MessageDigest 	 	theDigest				= null;
		
		/** 
		 * The length of the data read 
		 */
		private long				theDataLen				= 0;
		
		/** 
		 * The expected length of the data read 
		 */
		private long				theExpectedDataLen		= 0;
		
		/**
		 * The Expected Message Digest Bytes of the data 
		 */
		private byte[] 	 			theExpectedDigestBytes	= null;
		
		/**
		 * The Skip buffer 
		 */
		private byte[] 	 			theSkipBuffer			= new byte[BUFSIZE];
		
		/**
		 * Has EOF been reached
		 */
		private boolean				hasEOFbeenSeen			= false;
		
		/**
		 * The name of the digest
		 */
		private String				theName					= null;
		
		/**
		 * Set the expected digest details
		 * @param pName the name of the digest
		 * @param pDataLen the expected length of data  
		 * @param pDigest the expected digest value  
		 */
		public	void setExpectedDetails(String  pName,
										long 	pDataLen,
										byte[] 	pDigest) {
			/* Store the name of the digest */
			theName 				= pName;
			
			/* Store the expected details */
			theExpectedDataLen 		= pDataLen;
			theExpectedDigestBytes 	= pDigest;
		}
		
		/**
		 * Construct the input stream
		 * @param pStream the Stream to read data from
		 * @throws IOException
		 */
		public digestInputStream(java.io.InputStream pStream) throws finObject.Exception {
			/* Protect against exceptions */
			try {
				/* Create a message digest */
				theDigest = MessageDigest.getInstance(DIGEST);

				/* Store the stream details */
				theStream = pStream;
			}
			
			/* Catch exceptions */
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.ENCRYPT,
											  "Exception creating digest input stream",
											  e);
			}			
		}
		
		/**
		 * Close the input stream
		 * @throws IOException
		 */
		public void close() throws IOException {
			/* Null operation if we are already closed */
			if (!isClosed) {
				/* Close the input stream */
				theStream.close();
				isClosed = true;
				
				/* If we have seen EOF and have a digest to compare */
				if ((hasEOFbeenSeen) && (theExpectedDigestBytes != null)) {
					/* If the data lengths do not match */
					if (theDataLen != theExpectedDataLen) {
						/* Throw an exception */
						throw new IOException("Mismatch on Data lengths for " + theName);
					}
					
					/* If the digest does not match */
					if (!MessageDigest.isEqual(theDigest.digest(),
										   	   theExpectedDigestBytes)) {
						/* Throw an exception */
						throw new IOException("Mismatch on Digest for " + theName);
					}
				}
					
				/* Release allocated encryption resources */
				theDigest				= null;
				theExpectedDigestBytes 	= null;
				theSkipBuffer			= null;
				theName					= null;
			}
		}
		
		/**
		 * Skip a number of bytes in the input stream
		 * @param iNumToSkip the number of bytes to skip
		 * @return the actual number of bytes skipped
		 * @throws IOException
		 */
		public long skip(long iNumToSkip) throws IOException {
			long iNumSkipped = 0;
			int iNumToRead;
			int iNumRead;
			
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
		
			/* while we have data left to skip */
			while (iNumToSkip > 0) {
				/* Determine size of next read */
				iNumToRead = BUFSIZE;
				if (iNumToRead > iNumToSkip) iNumToRead = (int)iNumToSkip;
			
				/* Read the next set of data */
				iNumRead = read(theSkipBuffer, 0, iNumToRead);
				
				/* Break loop on EOF */
				if (iNumRead < 0) break;
				
				/* Adjust count */
				iNumToSkip  -= iNumRead;
				iNumSkipped	+= iNumRead;
			}
			
			return iNumSkipped;
		}
		
		/**
		 * Determine the number of bytes available without blocking
		 * @return the number of bytes available
		 * @throws IOException
		 */
		public int available() throws IOException {
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* Determine the number of bytes available */
			return theStream.available();
		}
		
		/**
		 * Determines whether the mark and reset methods are available
		 * @return <code>false</code>
		 */
		public boolean markSupported() {
			/* Always return false */
			return false;
		}
		
		/**
		 * Marks the current position in the input stream
		 * @param readLimit the number of bytes to read before mark is invalidated
		 */
		public void mark(int readLimit) {
			/* Just ignore the call */
			return;
		}
		
		/**
		 * Resets the current position in the input stream to that which was last marked
		 * @throws IOException
		 */
		public void reset() throws IOException {
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* Set the mark */
			throw new IOException("Mark not supported");
		}
		
		/**
		 * Read an array of bytes from the Input stream
		 * @param pBuffer the buffer to read into to write
		 * @param pOffset the offset from which to start reading
		 * @param pLength the maximum length of data to read
		 * @return the actual length of data read or -1 if EOF
		 * @throws IOException
		 */
		public int read(byte[] pBuffer, int pOffset, int pLength) throws IOException {			
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* Read the bytes from the stream */
			int iNumRead = theStream.read(pBuffer, pOffset, pLength);
			
			/* If we have read some data */
			if (iNumRead > 0) {
				/* Update the message digest */
				theDigest.update(pBuffer, pOffset, iNumRead);
		
				/* Adjust the data length */
				theDataLen += iNumRead;
			}
			
			/* Note if EOF has been seen */
			if (iNumRead == -1)
				hasEOFbeenSeen = true;
			
			/* Return the amount of data read */
			return iNumRead;
		}
		
		/**
		 * Read an array of bytes from the Input stream
		 * @param pBytes the buffer to read into to write
		 * @return the actual length of data read or -1 if EOF
		 * @throws IOException
		 */
		public int read(byte[] pBytes) throws IOException {
			/* Read the bytes from the stream */
			return read(pBytes, 0, pBytes.length);
		}
		
		/**
		 * Read a byte from the Input stream
		 * @return the byte read or -1 if EOF
		 * @throws IOException
		 */
		public int read() throws IOException {
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* read the next byte */
			int iByte = theStream.read();
			
			/* If we read data */
			if (iByte > -1) {
				/* Update the message digest */
				theDigest.update((byte)iByte);
			
				/* Adjust the data length */
				theDataLen++;
			}
			
			/* Note if EOF has been seen */
			if (iByte == -1) hasEOFbeenSeen = true;
			
			/* Return to the caller */
			return iByte;
		}
	}
	
	/**
	 * Provide an decryptInputStream wrapper. This class simply wraps an input buffer and processes
	 * it as a Zip file. It will read control information from the HEADER zip entry and will use this
	 * information to decrypts the data from the DATA Zip Entry
	 */
	public static class decryptInputStream extends java.io.InputStream {
		/**
		 * The underlying input stream
		 */
		private digestInputStream 	theStream				= null;

		/**
		 * has this stream been closed
		 */
		private boolean				isClosed 				= false;

		/**
		 * The decryption cipher
		 */
		private Cipher 			 	theCipher 				= null;
		
		/**
		 *  A buffer for single byte reads 
		 */
		private byte[] 				theByte 				= new byte[1];
		
		/**
		 * The transfer buffer
		 */
		private byte[]    			theBuffer				= new byte[BUFSIZE];
		
		/**
		 * The Skip buffer used by the skip function
		 */
		private byte[] 	 			theSkipBuffer			= new byte[BUFSIZE];
		
		/**
		 * The holding buffer for data that has been decrypted but not read
		 */
		private decryptBuffer		theDecrypted			= new decryptBuffer();
		
		/**
		 * Construct the decrypt input stream
		 * @param pStream the stream to decrypt from
		 * @throws IOException
		 */
		public decryptInputStream(java.io.InputStream pStream) throws finObject.Exception {
			/* Protect against exceptions */
			try {
				/* record the input stream */
				theStream 	= new digestInputStream(pStream);

				/* Access a new cipher */
				theCipher	= Cipher.getInstance(FULLSYMALGORITHM);
			}
			
			/* Catch exceptions */
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.ENCRYPT,
											  "Exception occurred creating input stream",
											  e);
			}			
		}
		
		/**
		 * Set the expected digest details
		 * @param pName the name of the digest
		 * @param pDataLen the expected length of data  
		 * @param pDigest the expected digest value  
		 * @param pSecretKey the encoded secret key  
		 * @param pInitVector the initialisation vector  
		 */
		public	void setExpectedDetails(String  pName,
										long 	pDataLen,
										byte[] 	pDigest,
										byte[]  pSecretKey,
										byte[]  pInitVector) throws finObject.Exception {
			AlgorithmParameterSpec	myParms;
			Cipher					myRSACipher;
			SecretKey 				myKey;
			
			/* Record the encrypted digest details */
			theStream.setExpectedDetails(pName, 
										 pDataLen,
										 pDigest);

			/* Protect from exceptions */
			try {
				/* Access the new cipher */
				myRSACipher = Cipher.getInstance(RSAALGORITHM);
				myRSACipher.init(Cipher.UNWRAP_MODE, theKeyPair.getPrivate());

				/* reverse the obscuring of the key */
				pSecretKey = obscureArray(pSecretKey, false);
				
				/* Decrypt the key */
				myKey = (SecretKey)myRSACipher.unwrap(pSecretKey, SYMALGORITHM, Cipher.SECRET_KEY);
				
				/* Initialise the cipher using initialisation vector */
				myParms = new IvParameterSpec(pInitVector);
					
				/* Initialise the cipher in standard format */
				theCipher.init(Cipher.DECRYPT_MODE, myKey, myParms);
			} 
		
			/* Catch exceptions */
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.ENCRYPT,
											  "Exception occurred verifying signature",
											  e);
			}
		}		
		
		/**
		 * Close the input stream
		 * @throws IOException
		 */
		public void close() throws IOException {
			/* Null operation if we are already closed */
			if (!isClosed) {
				/* Close the input stream */
				theStream.close();
				isClosed = true;
				
				/* Release allocated encryption resources */
				theCipher				= null;
				
				/* release buffers */
				theByte					= null;
				theBuffer				= null;
				theDecrypted			= null;
				theSkipBuffer			= null;
			}
		}
		
		/**
		 * Skip a number of bytes in the input stream
		 * @param iNumToSkip the number of bytes to skip
		 * @return the actual number of bytes skipped
		 * @throws IOException
		 */
		public long skip(long iNumToSkip) throws IOException {
			long 	iNumSkipped = 0;
			int 	iNumToRead;
			int 	iNumRead;
			
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
		
			/* while we have data left to skip */
			while (iNumToSkip > 0) {
				/* Determine size of next read */
				iNumToRead = BUFSIZE;
				if (iNumToRead > iNumToSkip) iNumToRead = (int)iNumToSkip;
			
				/* Read the next set of data */
				iNumRead = read(theSkipBuffer, 0, iNumToRead);
				
				/* Break loop on EOF */
				if (iNumRead < 0) break;
				
				/* Adjust count */
				iNumToSkip  -= iNumRead;
				iNumSkipped	+= iNumRead;
			}
			
			return iNumSkipped;
		}
					
		/**
		 * Determine the number of bytes available without blocking
		 * @return the number of bytes available
		 * @throws IOException
		 */
		public int available() throws IOException {
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* Determine the number of bytes available */
			return theDecrypted.available();
		}
		
		/**
		 * Determines whether the mark and reset methods are available
		 * @return <code>false</code>
		 */
		public boolean markSupported() {
			/* return false */
			return false;
		}
		
		/**
		 * Marks the current position in the input stream
		 * @param readLimit the number of bytes to read before mark is invalidated
		 */
		public void mark(int readLimit) {
			/* Just ignore */
			return;
		}
		
		/**
		 * Resets the current position in the input stream to that which was last marked
		 * @throws IOException
		 */
		public void reset() throws IOException {
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* Set the mark */
			throw new IOException("Mark not supported");
		}
		
		/**
		 * Read an array of bytes from the Input stream
		 * @param pBuffer the buffer to read into to write
		 * @param pOffset the offset from which to start reading
		 * @param pLength the maximum length of data to read
		 * @return the actual length of data read or -1 if EOF
		 * @throws IOException
		 */
		public int read(byte[] pBuffer, int pOffset, int pLength) throws IOException {
			int 	iNumRead;
		
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* Protect against exceptions */
			try {
				/* If there is no data in the decrypt buffer */
				if (theDecrypted.available() == 0) {
					/* If we have already exhausted the source return now */
					if (theDecrypted.hasEOFbeenSeen) return -1;
					
					/* Read more data from the input stream */
					iNumRead = theStream.read(theBuffer, 0, BUFSIZE);
					
					/* If we read no data just return details */
					if (iNumRead == 0) return iNumRead;
					
					/* Decrypt and store the decrypted bytes into the decrypt buffer */
					theDecrypted.storeBytes(theBuffer, iNumRead);
				}
				
				/* Read from the decrypted buffer */
				iNumRead  = theDecrypted.readBytes(pBuffer, pOffset, pLength);												
			}
						
			/* Catch exceptions */
			catch (IOException e) 	{ throw e; }
			catch (Exception e) 	{ throw new IOException(e);	}
			
			/* Return the amount of data read */
			return iNumRead;
		}
		
		/**
		 * Read an array of bytes from the Input stream
		 * @param pBytes the buffer to read into to write
		 * @return the actual length of data read or -1 if EOF
		 * @throws IOException
		 */
		public int read(byte[] pBytes) throws IOException {
			/* Write the byte to the stream */
			return read(pBytes, 0, pBytes.length);
		}
		
		/**
		 * Read a byte from the Input stream
		 * @return the byte read or -1 if EOF
		 * @throws IOException
		 */
		public int read() throws IOException {
			int iNumRead;
			
			/* Loop until we get a byte or EOF */
			while ((iNumRead = read(theByte, 0, 1)) == 0);
			
			/* Convert the byte read into an integer */
			if (iNumRead > 0) iNumRead = (theByte[0] & 0xff);
				
			/* Return to the caller */
			return iNumRead;
		}
		
		/**
		 * Buffer to hold the decrypted data prior to returning to returning to caller
		 */
		private class decryptBuffer {
			/**
			 * The buffer itself
			 */
			private byte[]	theStore 		= new byte[BUFSIZE];
			
			/**
			 * The length of data in the buffer
			 */
			private int		theDataLen		= 0;
			
			/**
			 * The read offset of data in the buffer
			 */
			private int		theReadOffset	= 0;
			
			/**
			 * have we seen EOF
			 */
			private boolean	hasEOFbeenSeen	= false;
			
			/**
			 * Determine the amount of data in the buffer
			 * @return the number of data bytes in the buffer
			 */
			public int		available() { return theDataLen - theReadOffset; }
			
			/**
			 * Read a number of bytes out from the buffer
			 * @param pBuffer the buffer to read bytes into
			 * @param pOffset the offset from which to start reading
			 * @param pLength the maximum length of data to read
			 * @return the actual length of data read or -1 if EOF
			 */
			public int		readBytes(byte[] pBuffer, int pOffset, int pLength) {
				/* Determine how much data we have available */
				int iNumRead  = theDataLen - theReadOffset;
				
				/* Determine how much data we can transfer */
				iNumRead = (iNumRead <= pLength) ? iNumRead
												 : pLength;
				
				/* If we have data to copy */
				if (iNumRead > 0) {
					/* Transfer the bytes */
					System.arraycopy(theStore, theReadOffset, pBuffer, pOffset, iNumRead);

					/* Adjust ReadOffset */
					theReadOffset += iNumRead;
				
					/* If we have finished with the data in the buffer */
					if (theReadOffset >= theDataLen) {
						/* Reset the values */
						theDataLen    = 0;
						theReadOffset = 0;
					}
				}
				
				/* else if we have no data check for EOF and report it if required */
				else if (hasEOFbeenSeen)
					iNumRead = -1;
				
				/* Return the number of bytes transferred */
				return iNumRead;
			}
			
			/**
			 * Decrypt bytes into the buffer and update the message digests
			 * @param pBuffer the buffer from which to store bytes 
			 * @param pLength the number of bytes read into the buffer (must not be zero)
			 */
			public void	storeBytes(byte[] pBuffer, int pLength) 
								throws 	ShortBufferException,
									 	BadPaddingException,
									 	IllegalBlockSizeException {
				int iNumBytes; 
			
				/* If we have EOF from the input stream */
				if (pLength == -1) {
					/* Record the fact and reset the read length to zero */
					hasEOFbeenSeen  = true;
					pLength			= 0;
				}

				/* Check how long a buffer we need */
				iNumBytes = theCipher.getOutputSize(pLength);
			
				/* Extend the store if required */
				if (iNumBytes > theStore.length)
					theStore = new byte[iNumBytes];
				
				/* If we have data that we read from the input stream */
				if (pLength > 0) {
					/* Decrypt the data */
					iNumBytes = theCipher.update(pBuffer, 0, pLength, theStore);
				}
				
				/* else we have EOF */
				else if (hasEOFbeenSeen) {
					/* Finish the cipher operation to pick up remaining bytes */
					iNumBytes = theCipher.doFinal(theStore, 0);
				}
			
				/* Set up holding variables */
				theDataLen    = iNumBytes;
				theReadOffset = 0;
				
				/* Return to caller */
				return;
			}
		}
	}
	
	/**
	 * Simple function to obscure/un-obscure and array. The obscure function is simply performs by an XOR
	 * operation with the value x55, together with an XOR against the low nanoseconds in the system clock 
	 * The system clock values used are embedded into the obscured output and may be easily used to 
	 * reverse the operation 
	 * @param pArray the array to obscure/clear
	 * @param isObscuring <code>true/false</code>
	 * @return the obscured or clear array 
	 */
	public static byte[] obscureArray(byte[] pArray, boolean isObscuring) {
		byte[] myNewArray;
		int	   myNewLen;
		int	   i;
		long   myNanos;
		
		/* If we are obscuring the key */
		if (isObscuring) {
			/* Allocate the new array */
			myNewLen   = (pArray.length * 2) + 1;
			myNewArray = new byte[myNewLen];
						
			/* Pad the alternate bytes */
			for (i=0; i<pArray.length+1; i++) {
				/* Access the current nanoseconds */
				myNanos = System.nanoTime();
				
				/* Store the nanoseconds into alternate bytes */
				myNewArray[2*i] = (byte) myNanos;
			}
			
			/* Load up the key into the array into alternate bytes */
			for (i=0; i<pArray.length; i++) {
				/* Obscure the key into alternate bytes */
				myNewArray[(2*i)+1]  = (byte) ((byte)pArray[i] ^ 85);
				myNewArray[(2*i)+1] ^= myNewArray[2*i];
			}
		}
		
		/* If we are reversing the obscure operation */
		else {
			/* Allocate the new array */
			myNewLen   = (pArray.length-1) / 2;
			myNewArray = new byte[myNewLen];
			
			/* Load up the key into the array */
			for (i=0; i<myNewLen; i++) {
				/* Access alternate bytes and reverse the obscure operation */
				myNewArray[i]  = (byte) ((byte)pArray[2*i] ^ (byte)pArray[(2*i)+1]);
				myNewArray[i] ^= (byte) 85;
			}
		}
		
		/* return the array */
		return myNewArray;
	}
	
	/**
	 * Simple function to encrypt/decrypt an initialisation vector using the message digest of a password
	 * Since it uses XOR functionality, repeating the call reverses the previous call
	 * @param pVector the vector to obscure/clear
	 * @param pPassword the password to utilise
	 * @return the obscured or clear array
	 * @throws Exception 
	 */
	public static byte[] obscureVector(byte[] pVector, byte[] pPassword) throws Exception {
		MessageDigest	myDigest;
		byte[]			myPassword;
		byte[] 			myNewArray;
		int	   			myLen;
		int	   			i;
		
		/* Create a digest of the password */
		myDigest = MessageDigest.getInstance(DIGEST);
		myDigest.update(pPassword);
		myPassword = myDigest.digest();

		/* Allocate the new array as a copy of the vector */
		myNewArray = Arrays.copyOf(pVector, pVector.length);
		
		/* Determine length of operation */
		myLen = pVector.length;
		if (myLen > myPassword.length)
			myLen = myPassword.length;
		
		/* Loop through the vector bytes */
		for (i=0; i<pVector.length; i++) {
			/* Obscure the byte */
			myNewArray[i] ^= myPassword[i];
		}
					
		/* return the array */
		return myNewArray;
	}
	
	/**
	 * generate a new set of keys
	 * @throws finObject.Exception if there are any errors
	 */
	private static void generateKeys() throws finObject.Exception {
		KeyPairGenerator 	myGenerator;
		byte[]				myPrivate;
		byte[]			 	myPublic;
		byte[]			 	myDatabase;
		
		/* Protect against exceptions */
		try { 
			/* Create an instance of the public/private generator */
			myGenerator = KeyPairGenerator.getInstance(RSAALGORITHM);
			
			/* Set the Key size */
			myGenerator.initialize(ASYMKEYSIZE, theRandom);
			
			/* Generate the Public/Private keys */
			theKeyPair = myGenerator.generateKeyPair();

			/* Generate the Database key */
			theDatabaseKey = theKeyGen.generateKey();
			
			/* Wrap the keys */
			myPublic  	= thePasswordUtil.wrapKey(theKeyPair.getPublic(),  	Cipher.PUBLIC_KEY);
			myPrivate 	= thePasswordUtil.wrapKey(theKeyPair.getPrivate(), 	Cipher.PRIVATE_KEY);
			myDatabase	= thePasswordUtil.wrapKey(theDatabaseKey, 			Cipher.PRIVATE_KEY);

			/* Store the values into preferences */
			theProperties.setPrivateKey(myPrivate);
			theProperties.setPublicKey(myPublic);
			theProperties.setDatabaseKey(myDatabase);
			
			/* Flush the preference changes */
			theProperties.flushChanges();
		} 
		
		/* Catch exceptions */
		catch (finObject.Exception e) {
			throw e;
		}
		
		/* Catch exceptions */
		catch (Exception e) {
			throw new finObject.Exception(ExceptionClass.ENCRYPT,
										  "Exception occurred generating key Pair",
										  e);
		}
	}

	/**
	 * Access the public/private key pairs
	 * @throws finObject.Exception
	 */
	private static void accessKeys() throws finObject.Exception {
		PrivateKey		   	myPrivateKey;
		PublicKey		   	myPublicKey;
		byte[]				myPrivate;
		byte[]				myPublic;
		byte[]				myDatabase;
		
		/* Protect against exceptions */
		try {
			/* Access the stored values */
			myPrivate  = theProperties.getPrivateKey();
			myPublic   = theProperties.getPublicKey();
			myDatabase = theProperties.getDatabaseKey();
			
			/* If we need to generate new keys */
			if ((myPrivate == null) || (myPublic == null) || (myDatabase == null)) {
				/* Generate a new set of keys */
				generateKeys();
				return;
			}
			
			/* Unwrap the keys */
			myPublicKey    = (PublicKey) thePasswordUtil.unwrapKey(myPublic,   Cipher.PUBLIC_KEY);
			myPrivateKey   = (PrivateKey)thePasswordUtil.unwrapKey(myPrivate,  Cipher.PRIVATE_KEY);
			theDatabaseKey = (SecretKey) thePasswordUtil.unwrapKey(myDatabase, Cipher.SECRET_KEY);

			/* Create the key pair */
			theKeyPair = new KeyPair(myPublicKey, myPrivateKey);			
		} 
		
		/* Catch exceptions */
		catch (finObject.Exception e) {
			throw e;
		}
		
		/* Catch exceptions */
		catch (Exception e) {
			throw new finObject.Exception(ExceptionClass.ENCRYPT,
										  "Exception occurred restoring key Pair",
										  e);
		}
	}
	
	/**
	 * Static initialisation for the class to access/initialise the Keys
	 * @param pParent the top level window
	 * @throws finObject.Exception
	 */
	public static synchronized void initialise(finSwing pParent) throws finObject.Exception {
		/* Protect against exceptions */
		try { 
			/* Ignore if already initialised */
			if (hasInitialised) return; 
				
			/* Store the properties */
			theProperties = pParent.getProperties();
			
			/* Create a new secure random generator */
			theRandom = new SecureRandom();
			
			/* Create the secret key generator */
			theKeyGen = KeyGenerator.getInstance(SYMALGORITHM);
			theKeyGen.init(SYMKEYSIZE, theRandom);
			
			/* Access the password utility initialised with the stored hash */
			thePasswordUtil = new passwordUtil(theProperties.getPasswordHash());
			
			/* Prompt for the password */
			finUtils.passwordDialog myPass = new finUtils.passwordDialog(pParent.getFrame());
			if (myPass.showDialog()) {
				/* Access the password */
				char[] myPassword = myPass.getPassword();
				
				/* Set/Check the password */
				thePasswordUtil.setPassword(myPassword);
			}
			
			/* Access the set of keys */
			accessKeys();
			
			/* Note successful initialisation */
			hasInitialised = true;
		} 
		catch (Exception e) {
			throw new finObject.Exception(ExceptionClass.ENCRYPT,
									      "Failed to initialise encryption class",
										  e);
		}
	}
	
	/**
	 * Encryption class for password based cryptography
	 */
	public static class passwordUtil {
		/**
		 * Iteration count for passwords 
		 */
		private static final int 	theIterations 	= 2048;
		
		/**
		 * Salt length for passwords 
		 */
		private static final int 	theSaltLength 	= 16;
		
		/**
		 * Secret key for wrapping 
		 */
		private SecretKey 			thePassKey		= null;
		
		/**
		 * Password salt and hash 
		 */
		private byte[] 				theSaltAndHash 	= null;
		
		/**
		 * Obtain the Salt and Hash array
		 * @return the Salt And Hash array 
		 */
		public byte[] getSaltAndHash() { return obscureArray(theSaltAndHash, true); }
		
		/**
		 * Constructor
		 * @param pSaltAndHash the Salt And Hash array 
		 */
		public passwordUtil(byte[] pSaltAndHash) {
			/* Store the salt and hash */
			theSaltAndHash = (pSaltAndHash == null) ? null : obscureArray(pSaltAndHash, false);
		}
		
		/**
		 * Seed the password utility with the password
		 * @param pPassword the password (cleared after usage) 
		 */
		public void setPassword(char[] pPassword) throws finObject.Exception {
			PBEKeySpec 			myKeySpec;
			SecretKeyFactory 	myKeyFactory;
			byte[]				mySalt;
			byte[]				mySaltAndHash;
			
			/* Protect against exceptions */
			try {
				/* If we already have a salt */
				if (theSaltAndHash != null) {
					/* Pick out the salt from the array */
					mySalt = Arrays.copyOf(theSaltAndHash, theSaltLength);
				}
				
				/* Else this is the initialisation phase */
				else {
					/* Generate a new salt */
					mySalt = new byte[theSaltLength];
					theRandom.nextBytes(mySalt);
				}
				
				/* Generate the saltAndHash */
				mySaltAndHash = generateSaltAndHash(mySalt, pPassword);
					
				/* If we already have a salt */
				if (theSaltAndHash != null) {
					/* Check that the arrays match */
					if (!Arrays.equals(theSaltAndHash, mySaltAndHash)) {
						/* Throw and exception */
						throw new finObject.Exception(ExceptionClass.ENCRYPT,
													  "Invalid Password");
					}
				}
					
				/* Else this is the initialisation phase */
				else {
					/* Record the Salt and Hash */
					theSaltAndHash = mySaltAndHash;
				}
				
				/* Generate the key */
				myKeySpec 		= new PBEKeySpec(pPassword, mySalt, theIterations);
				myKeyFactory 	= SecretKeyFactory.getInstance(PBEALGORITHM);
				thePassKey 		= myKeyFactory.generateSecret(myKeySpec);
				
				/* Clear out the password */
				for (int i=0; i<pPassword.length; i++) { pPassword[i] = 0; }
				myKeySpec.clearPassword();
			}
			catch (finObject.Exception e) { throw e; }
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.ENCRYPT,
										      "Failed to initialise using password",
											  e);
			}
		}
		
		/**
		 * Generate Salt And Hash array
		 * @param pSalt the salt for the password 
		 * @param pPassword the password for the keys
		 * @return the Salt and Hash array 
		 */
		private byte[] generateSaltAndHash(byte[] pSalt, char[] pPassword) throws finObject.Exception {
			byte[] 			mySaltAndHash;
			byte[] 			myHash;
			MessageDigest 	myDigest;
			
			/* Protect against exceptions */
			try {
				/* Create a new digest */
				myDigest = MessageDigest.getInstance(DIGEST); 
					
				/* Initialise the hash value as the UTF-8 version of the password */
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				OutputStreamWriter out = new OutputStreamWriter(baos, ENCODING);
				for(int ch : pPassword) { out.write(ch); }
				myHash = baos.toByteArray();
			
				/* Initialise the digest with the salt */
				myDigest.update(pSalt);
				
				/* Loop through the iterations */
				for (int i=0; i < theIterations; i++) {
					/* Update the digest and calculate it */
					myDigest.update(myHash);
					myHash = myDigest.digest();
					
					/* Reset the digest */
					myDigest.reset();
				}
				
				/* Combine the salt and hash */
				mySaltAndHash = new byte[pSalt.length+ myHash.length];
				System.arraycopy(pSalt, 0, mySaltAndHash, 0, pSalt.length);
				System.arraycopy(myHash, 0, mySaltAndHash, pSalt.length, myHash.length);
			}
			
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.ENCRYPT,
										      "Failed to generate salt and hash",
											  e);
			}
			
			/* Return to caller */
			return mySaltAndHash;
		}
		
		/**
		 * Wrap secret key
		 * @param pKey the Key to wrap  
		 * @return the wrapped secret key with random salt
		 */
		public byte[] wrapKey(Key pKey, int pType) throws finObject.Exception {
			byte[] 				mySalt;
			byte[] 				myKeyEnc;
			byte[] 				mySaltAndKey;
			PBEParameterSpec 	mySpec;
			Cipher				myCipher;
			
			/* Protect against exceptions */
			try {
				/* Create the new salt */
				mySalt = new byte[theSaltLength];
				theRandom.nextBytes(mySalt);
				
				/* Initialise the cipher */
				mySpec 		= new PBEParameterSpec(mySalt, theIterations);
				myCipher	= Cipher.getInstance(PBEALGORITHM);
				myCipher.init(Cipher.WRAP_MODE, thePassKey, mySpec);
			
				/* wrap the key */
				myKeyEnc = myCipher.wrap(pKey);

				/* Combine the salt and hash */
				mySaltAndKey = new byte[mySalt.length+ myKeyEnc.length];
				System.arraycopy(mySalt, 0, mySaltAndKey, 0, mySalt.length);
				System.arraycopy(myKeyEnc, 0, mySaltAndKey, mySalt.length, myKeyEnc.length);

				/* Obscure the array */
				mySaltAndKey = obscureArray(mySaltAndKey, true);
			}
			
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.ENCRYPT,
										      "Failed to wrap key",
											  e);
			}
			
			/* Return to caller */
			return mySaltAndKey;
		}
		
		/**
		 * Unwrap key
		 * @param pSaltAndKey the Salt and encrypted key  
		 * @return the unwrapped secret key 
		 */
		public Key unwrapKey(byte[] pSaltAndKey, int pType) throws finObject.Exception {
			byte[] 				mySalt;
			byte[] 				myKeyEnc;
			PBEParameterSpec 	mySpec;
			Cipher				myCipher;
			Key					myKey;
			String				myAlgorithm;
			
			/* Protect against exceptions */
			try {
				/* Determine algorithm */
				myAlgorithm = (pType == Cipher.SECRET_KEY) ? SYMALGORITHM : RSAALGORITHM;
				
				/* Un-obscure the array */
				pSaltAndKey = obscureArray(pSaltAndKey, false);
				
				/* Pick out the salt and key from the array */
				mySalt 		= Arrays.copyOf(pSaltAndKey, theSaltLength);
				myKeyEnc	= Arrays.copyOfRange(pSaltAndKey, theSaltLength, pSaltAndKey.length);
				
				/* Initialise the cipher */
				mySpec 		= new PBEParameterSpec(mySalt, theIterations);
				myCipher	= Cipher.getInstance(PBEALGORITHM);
				myCipher.init(Cipher.UNWRAP_MODE, thePassKey, mySpec);
			
				/* unwrap the key */
				myKey = myCipher.unwrap(myKeyEnc, myAlgorithm, pType);
			}
			
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.ENCRYPT,
										      "Failed to generate salt and hash",
											  e);
			}
			
			/* Return to caller */
			return myKey;
		}
	}
	
	/**
	 * Encryption class for database usage
	 */
	public class encryptDatabase {
		/**
		 * The cipher
		 */
		private Cipher 	theCipher 		= null;
		
		/**
		 * The initialisation vector
		 */
		private byte[] 	theInitVector 	= null;
		
		/**
		 * Obtain the initialisation vector
		 * @return the initialisation vector for cipher
		 */
		public byte[] getInitVector() { return theInitVector; }
		
		/**
		 * Constructor
		 */
		public encryptDatabase() throws finObject.Exception {
			/* If we have not yet initialised */
			if (!hasInitialised)
				throw new finObject.Exception(ExceptionClass.LOGIC,
										  	  "Must initialise encryption first");
		
			/* Protect against exceptions */
			try {
				/* Create a new cipher */
				theCipher = Cipher.getInstance(FULLSYMALGORITHM);
			}
			
			/* catch exceptions */
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.ENCRYPT,
										      "Failed to create cipher",
											  e);
			}
		}
		
		/**
		 * Initialise cipher for encryption with initialisation vector
		 * @param Initialisation vector for cipher
		 */
		public void initEncryption(byte[] pInitVector) throws finObject.Exception {
			AlgorithmParameterSpec 	myParms;

			/* Protect against exceptions */
			try {
				/* Initialise the cipher using the password */
				myParms = new IvParameterSpec(pInitVector);
				theCipher.init(Cipher.ENCRYPT_MODE, theDatabaseKey, myParms);
			}
			
			/* catch exceptions */
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.ENCRYPT,
										      "Failed to initialise cipher",
											  e);
			}
		}
		
		/**
		 * Initialise cipher for encryption with random initialisation vector
		 */
		public void initEncryption() throws finObject.Exception {

			/* Protect against exceptions */
			try {
				/* Initialise the cipher generating a random Initialisation vector */
				theCipher.init(Cipher.ENCRYPT_MODE, theDatabaseKey, theRandom);
				
				/* Access the initialisation vector */
				theInitVector	= theCipher.getIV();
			}
			
			/* catch exceptions */
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.ENCRYPT,
										      "Failed to initialise cipher",
											  e);
			}
		}
		
		/**
		 * Initialise cipher for decryption with initialisation vector
		 * @param Initialisation vector for cipher
		 */
		public void initDecryption(byte[] pInitVector) throws finObject.Exception {
			AlgorithmParameterSpec 	myParms;

			/* Protect against exceptions */
			try {
				/* Initialise the cipher using the password */
				myParms = new IvParameterSpec(pInitVector);
				theCipher.init(Cipher.DECRYPT_MODE, theDatabaseKey, myParms);
			}
			
			/* catch exceptions */
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.ENCRYPT,
										      "Failed to initialise cipher",
											  e);
			}
		}
		
		/**
		 * Encrypt string
		 * @param pString String to encrypt
		 * @return Encrypted bytes
		 * @throws finObject.Exception 
		 */
		public byte[] encryptString(String pString) throws finObject.Exception {
			byte[] myBytes;
			
			/* Protect against exceptions */
			try {
				/* Encrypt the string */
				myBytes = theCipher.doFinal(pString.getBytes(ENCODING));
			}
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.ENCRYPT,
										      "Failed to encrypt string",
											  e);
			}
			
			/* Return to caller */
			return myBytes;
		}		
		
		/**
		 * Decrypt string
		 * @param pString String to encrypt
		 * @return Encrypted bytes
		 * @throws finObject.Exception 
		 */
		public String decryptString(byte[] pBytes) throws finObject.Exception {
			byte[] 	myBytes;
			String	myString;
			
			/* Protect against exceptions */
			try {
				/* Encrypt the string */
				myBytes  = theCipher.doFinal(pBytes);
				myString = new String(myBytes);
			}
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.ENCRYPT,
										      "Failed to decrypt string",
											  e);
			}
			
			/* Return to caller */
			return myString;
		}		
	}
}
