package finance;

import java.util.Arrays;
import java.util.zip.*;
import java.io.*;

import finance.finEncryption.digestOutputStream;
import finance.finEncryption.encryptOutputStream;
import finance.finEncryption.digestInputStream;
import finance.finEncryption.decryptInputStream;
import finance.finObject.ExceptionClass;

/**
 * Class providing management facilities for creating and reading ZipFiles
 * 
 * @author Tony Washer
 */
public class finZipFile {
	/**
	 * The Header file name
	 */
	private final static String fileHeader			= "zipHeader";
	
	/**
	 * The property name of a file
	 */
	private final static String propName			= "Name";
	
	/**
	 * The RawData property name of a file
	 */
	private final static String propRawData			= "RawData";
	
	/**
	 * The CompData property name of a file
	 */
	private final static String propCompData		= "CompressedData";
	
	/**
	 * The EncData property name of a file
	 */
	private final static String propEncData			= "EncryptedData";
	
	/**
	 * The Signature property name of a file
	 */
	private final static String propSignature		= "Signature";
	
	/**
	 * The SecretKey property name of a file
	 */
	private final static String propSecretKey		= "SecretKey";
	
	/**
	 * The InitVector property name of a file
	 */
	private final static String propInitVector		= "InitVector";
	
	/**
	 * The encoding property name of a file
	 */
	private final static String propEncoding		= "UTF-8";
	
	/**
	 * ZipOutputFile Class represents a zip file that is in the process of being built 
	 */
	protected static class zipOutputFile {
		/**
		 *	The list of files contained in this ZipFile together with properties 
		 */
		private zipFileEntry		theFiles		= null;
		
		/**
		 * The underlying zip output stream
		 */
		private ZipOutputStream 	theStream		= null;

		/**
		 * The active output stream
		 */
		private digestOutputStream 	theOutput		= null;

		/**
		 * The compressed output stream
		 */
		private digestOutputStream 	theCompressed	= null;

		/**
		 * The encrypted output stream
		 */
		private encryptOutputStream theEncrypted	= null;

		/**
		 * The active zipEntry
		 */
		private ZipEntry 			theEntry		= null;

		/**
		 *	Constructor for new output zip file
		 *	@param pFile the file details for the new zip file 
		 */
		public zipOutputFile(File pFile) throws finObject.Exception {
			FileOutputStream		myOutFile;
			BufferedOutputStream	myOutBuffer;
			
			/* Protect against exceptions */
			try {			
				/* Create the output streams */
				myOutFile 	= new FileOutputStream(pFile);
				myOutBuffer	= new BufferedOutputStream(myOutFile);
				theStream 	= new ZipOutputStream(myOutBuffer);
			}
				
			/* Catch exceptions */
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.DATA,
											  "Exception creating new Zip file",
											  e);
			}			
		}
		
		/**
		 *	Obtain an output stream for an entry in the zip file
		 *	@param pFile the file details for the new zip entry
		 *	@param pMode the mode to store the new file in 
		 */
		public OutputStream getOutputStream(File pFile, zipMode pMode) throws finObject.Exception {
			GZIPOutputStream myZip;
		
			/* Reject call if we have an closed the stream */
			if (theStream == null)
				throw new finObject.Exception(ExceptionClass.DATA,
						  					  "ZipFile is closed");
			
			/* Reject call if we have an open stream */
			if (theOutput != null)
				throw new finObject.Exception(ExceptionClass.DATA,
						  					  "Output stream already open");
			
			/* Protect against exceptions */
			try {
				/* Start the new entry */
				theEntry 	= new ZipEntry(pFile.getPath());
				theStream.putNextEntry(theEntry);
			}
			
			/* Catch exceptions */
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.DATA,
											  "Exception writing to Zip file",
											  e);
			}			
			
			/* Protect against exceptions */
			try {
				/* Switch on the mode */
				switch (pMode) {
					case RAW:
						/* Create the digest output stream directly onto the Zip stream */
						theOutput 		= new digestOutputStream(new wrapOutputStream());
						break;
					case COMPRESS:
						/* Create the compressed output stream directly onto the Zip stream */
						theCompressed	= new digestOutputStream(new wrapOutputStream());
					
						/* Create a GZIP output stream onto the compressed digest stream */
						myZip			= new GZIPOutputStream(theCompressed);
					
						/* Create the raw output stream directly onto the GZIP stream */
						theOutput		= new digestOutputStream(myZip);
						break;					
					case ENCRYPT:
						/* Create the encrypted output stream directly onto the Zip stream */
						theEncrypted	= new encryptOutputStream(new wrapOutputStream());
					
						/* Create the raw output stream directly onto the encryption stream */
						theOutput		= new digestOutputStream(theEncrypted);
						break;					
					case COMPRESS_AND_ENCRYPT:
						/* Create the encrypted output stream directly onto the Zip stream */
						theEncrypted	= new encryptOutputStream(new wrapOutputStream());
					
						/* Create the compressed output stream directly onto the Encrypt stream */
						theCompressed	= new digestOutputStream(theEncrypted);
					
						/* Create a GZIP output stream onto the compressed digest stream */
						myZip			= new GZIPOutputStream(theCompressed);					
						
						/* Create the raw output stream directly onto the GZIP stream */
						theOutput		= new digestOutputStream(myZip);
						break;					
				}
			}
			
			/* Catch exceptions */
			catch (finObject.Exception e) {	throw e; }
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.DATA,
											  "Exception creating new Output stream",
											  e);
			}
			
			/* return the new stream */
			return theOutput;
		}
		
		/**
		 *	Close any active output stream and record digest values
		 */
		private void closeOutputStream() throws IOException {
			zipFileEntry myEntry;
			byte[]		 myDigest;
			byte[]		 myKey;
			byte[]		 myVector;
			byte[]		 mySign;
			long		 myLength;
			
			/* Protect against exceptions */
			try {
				/* If we have an output stream */
				if (theOutput != null) {
					/* Close the active entry */
					theStream.closeEntry();
			
					/* Create a new zipFileEntry */
					myEntry = new zipFileEntry();
					addToList(myEntry);
					
					/* Add the name of the file entry */
					myEntry.setProperty(propName, theEntry.getName().getBytes(propEncoding));
			
					/* Access raw details */
					myDigest = theOutput.getDigest();
					myLength = theOutput.getDataLen();
					
					/* Set the properties */
					myEntry.setProperty(propRawData, myDigest);
					myEntry.setProperty(propRawData, myLength);

					/* If we have a compression digest */
					if (theCompressed != null) {
						/* Access compressed details */
						myDigest = theCompressed.getDigest();
						myLength = theCompressed.getDataLen();
						
						/* Set the properties */
						myEntry.setProperty(propCompData, myDigest);
						myEntry.setProperty(propCompData, myLength);
					}
					
					/* If we have an encrypted digest */
					if (theEncrypted != null) {
						/* Access encrypted details */
						myDigest = theEncrypted.getDigest();
						myLength = theEncrypted.getDataLen();
						myKey 	 = theEncrypted.getSecretKeyEncoded();
						myVector = theEncrypted.getInitVector();
						
						/* Set the properties */
						myEntry.setProperty(propEncData, 	myDigest);
						myEntry.setProperty(propEncData, 	myLength);
						myEntry.setProperty(propSecretKey,  myKey);
						myEntry.setProperty(propInitVector, myVector);
						
						/* Calculate the signature and add it to properties */
						mySign = finEncryption.finSignature.signFile(myEntry);
						myEntry.setProperty(propSignature, mySign);
					}
					
					/* Release the entry */
					theEntry = null;
				}
				
				/* Reset streams */
				theOutput 		= null;
				theCompressed 	= null;
				theEncrypted 	= null;
			}
			
			/* Catch exceptions */
			catch (IOException e) 	{ throw e; }
			catch (Exception e) 	{ throw new IOException(e);	}
		}
		
		/**
		 *	Close the Zip file and write the header
		 */
		public void close() throws IOException {
			String		 myHeader;
			byte[]		 myBytes;
			
			/* Close any open output stream */
			closeOutputStream();
			
			/* If the stream is open */
			if (theStream != null) {
				/* Protect against exceptions */
				try {
					/* If we have stored files */
					if (theFiles != null) {
						/* Access the encoded file string */
						myHeader = theFiles.getEncodedString();
						myBytes  = myHeader.getBytes(propEncoding);

						/* Start the new entry */
						theEntry 	= new ZipEntry(fileHeader);
						theStream.putNextEntry(theEntry);

						/* Write the bytes to the zip file and close the entry */
						theStream.write(myBytes);
						theStream.closeEntry();
					}
					
					/* close the stream */
					theStream.flush();
					theStream.close();
					theStream = null;
				}
			
				/* Catch exceptions */
				catch (IOException e) 	{ throw e; }
				catch (Exception e) 	{ throw new IOException(e);	}
			}			
		}
		
		/**
		 * Add a zipFileEntry to the list
		 * @param pEntry the entry to add 
		 */
		private void addToList(zipFileEntry pEntry) {
			zipFileEntry myEntry;
			zipFileEntry myLast;

			/* Loop through to the end of the list */
			for (myEntry  = theFiles, myLast = null;
				 myEntry != null;
				 myLast   = myEntry, myEntry  = myEntry.getNext()) {}

			/* If we do not have an insert point */
			if (myLast == null) {
				/* Add the value to the head of the list */
				theFiles = pEntry;
			}
			
			/* Else we have an insert point */
			else {
				/* Add the value to the end of the list */
				myLast.setNext(pEntry);
			}
		}
		
		/**
		 * Wrapper class to catch close of output stream and prevent it from closing the zipfile
		 */
		private class wrapOutputStream extends java.io.OutputStream {
			/**
			 * Flush stream
			 */
			public void flush() throws IOException { theStream.flush(); } 

			/**
			 * Write byte
			 */
			public void write(int b) throws IOException { theStream.write(b); } 

			/**
			 * Write byte array
			 */
			public void write(byte[] b) throws IOException { theStream.write(b); } 

			/**
			 * Write byte array section
			 */
			public void write(byte[] b, int offset, int length) throws IOException { theStream.write(b, offset, length); } 

			/**
			 * Close file
			 */
			public void close() throws IOException { 
				closeOutputStream();
			} 
		}
		
		/**
		 * Enumeration class for Zip modes 
		 */
		public enum zipMode {
			/**
			 * Raw Data
			 */
			RAW,
			
			/**
			 * Compressed data
			 */
			COMPRESS,
			
			/**
			 * Encrypted data
			 */
			ENCRYPT,
			
			/**
			 * Compressed and Encrypted data
			 */
			COMPRESS_AND_ENCRYPT;
		}
	}
	
	/**
	 * ZipInputFile Class represents a zip file that can be read 
	 */
	protected static class zipInputFile {
		/* The extension size of the buffer */
		private static int 			BUFFERSIZE 		= 1024;
		
		/**
		 *	The list of files contained in this ZipFile together with properties 
		 */
		private zipFileEntry		theFiles		= null;
		
		/** 
		 * The name of the Zip file
		 */
		private File				theZipFile		= null;
		
		/**
		 * Obtain the next file entry 
		 * @return the next file entry
		 */
		public zipFileEntry	getFiles() 				{ return theFiles; }
		
		/**
		 * Constructor 
		 * @param pFile the file to read
		 */
		public zipInputFile(File pFile) throws finObject.Exception {
			FileInputStream 	myInFile;
			BufferedInputStream myInBuffer;
			ZipInputStream		myZipFile;
			ZipEntry			myEntry;
			byte[]			    myBuffer = new byte[BUFFERSIZE];
			int					myRead;
			int					myLen;
			int					mySpace;
		
			/* Protect against exceptions */
			try {
				/* Store the zipFile name */
				theZipFile = new File(pFile.getPath());
				
				/* Open the zip file for reading */
				myInFile   = new FileInputStream(pFile);
				myInBuffer = new BufferedInputStream(myInFile);
				myZipFile  = new ZipInputStream(myInBuffer);
		
				/* Loop through the Zip file entries */
				while((myEntry = myZipFile.getNextEntry()) != null) {
					/* Break if we found the header entry */
					if (myEntry.getName().compareTo(fileHeader) == 0) break;
				}
				
				/* Initialise variables */
				myLen   = 0;
				mySpace = BUFFERSIZE;
				
				/* Read the header entry */
				while ((myRead = myZipFile.read(myBuffer, myLen, mySpace)) != -1) {
					/* Adjust buffer */
					myLen   += myRead;
					mySpace -= myRead;
					
					/* If we have finished up the buffer */
					if (mySpace == 0) {
						/* Increase the buffer */
						myBuffer = Arrays.copyOf(myBuffer, myLen+BUFFERSIZE);
						mySpace += BUFFERSIZE;
					}
				}
				
				/* Cut down the buffer to size */
				myBuffer = Arrays.copyOf(myBuffer, myLen);
				
				/* Parse the header details */
				theFiles = new zipFileEntry(new String(myBuffer));
				
				/* Close the file */
				myZipFile.close();
			}
			
			/* Catch exceptions */
			catch (finObject.Exception e) {	throw e; }
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.DATA,
									  	      "Exception writing to Zip file",
										  	  e);
			}
		}
		
		/**
		 *	Obtain an input stream for an entry in the zip file
		 *	@param pFile the file details for the new zip entry
		 *	@param pMode the mode to store the new file in 
		 */
		public InputStream getInputStream(zipFileEntry pFile) throws finObject.Exception {
			FileInputStream 	myInFile;
			BufferedInputStream myInBuffer;
			ZipInputStream		myZipFile;
			ZipEntry			myEntry;
			String				myName;
			InputStream			myCurrent;
			digestInputStream	myDigest;
			decryptInputStream	myDecrypt;
		
			/* Protect against exceptions */
			try {
				/* Open the zip file for reading */
				myInFile   = new FileInputStream(theZipFile);
				myInBuffer = new BufferedInputStream(myInFile);
				myZipFile  = new ZipInputStream(myInBuffer);
		
				/* Access the name of the file entry */
				myName = pFile.getFileName();
				
				/* Loop through the Zip file entries */
				while((myEntry = myZipFile.getNextEntry()) != null) {
					/* Break if we found the correct entry */
					if (myEntry.getName().compareTo(myName) == 0) break;
				}
				
				/* Note the current input stream */
				myCurrent = myZipFile;
				
				/* If the file is encrypted */
				if (pFile.isEncrypted()) {
					/* Wrap the zip file with the decrypt stream */
					myDecrypt = new decryptInputStream(myCurrent);
					
					/* Verify Encryption details and set for decryption */
					finEncryption.finSignature.verifyFile(pFile);
					
					/* Tell the encryption about the expected encryption details */
					myDecrypt.setExpectedDetails("Encrypted", 
												 pFile.getEncryptedDataLen(),
												 pFile.getEncryptedDigest(),
												 pFile.getSecretKey(),
												 pFile.getInitVector());

					/* Record the current stream */
					myCurrent = myDecrypt;
				}
				
				/* If the file is compressed */
				if (pFile.isCompressed()) {
					/* Wrap a digest input stream around the zip file */
					myDigest = new digestInputStream(myCurrent);
					
					/* Tell the digest about the expected compression digest and length */
					myDigest.setExpectedDetails("Compressed", 
												pFile.getCompressedDataLen(),
												pFile.getCompressedDigest());

					/* Wrap a GZIPInputStream around the digest */
					myCurrent = new GZIPInputStream(myDigest);
				}
				
				/* Wrap a digest input stream around the zip file */
				myDigest = new digestInputStream(myCurrent);
				
				/* Tell the digest about the expected compression digest and length */
				myDigest.setExpectedDetails("Raw", 
											pFile.getRawDataLen(),
											pFile.getRawDigest());

				/* Record the current stream */
				myCurrent = myDigest;
			}
			
			/* Catch exceptions */
			catch (finObject.Exception e) {	throw e; }
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.DATA,
											  "Exception creating new Output stream",
											  e);
			}
			
			/* return the new stream */
			return myCurrent;
		}		
	}

	/**
	 * ZipFileEntry Class representing an entry in the zip file and its properties 
	 */
	protected static class zipFileEntry {
		/**
		 * The file separator 
		 */
		private final static char 	theFileSeparator 	= ';';
		
		/**
		 * The property separator 
		 */
		private final static char	thePropSeparator 	= '/';
		
		/**
		 * The value separator 
		 */
		private final static char	theValuSeparator 	= '=';
		
		/**
		 * The value separator 
		 */
		private final static char	theLongSeparator 	= '!';
		
		/**
		 * The next file in the list
		 */
		private zipFileEntry 		theNext 			= null;

		/**
		 * The property list
		 */
		private property 			theProperties		= null;

		/**
		 * Is the file encrypted
		 */
		private boolean 			isEncrypted			= false;

		/**
		 * Is the file compressed
		 */
		private boolean 			isCompressed		= false;

		/**
		 * Is the file encrypted in the zip file
		 * @return is the file encrypted?   
		 */
		public boolean 		isEncrypted() 			{ return isEncrypted; }
		
		/**
		 * Is the file compressed in the zip file 
		 * @return is the file compressed?
		 */
		public boolean 		isCompressed() 			{ return isCompressed; }
		
		/**
		 * Obtain the next file entry 
		 * @return the next file entry
		 */
		public zipFileEntry	getNext() 				{ return theNext; }
		
		/**
		 * Obtain the name of the file 
		 * @return the name of the file
		 */
		public String 		getFileName() 			{ return new String(getByteProperty(propName)); }
		
		/**
		 * Obtain the raw data length of the file 
		 * @return the raw data length of the file
		 */
		public long 		getRawDataLen() 		{ return getLongProperty(propRawData); }
		
		/**
		 * Obtain the compressed data length of the file 
		 * @return the compressed data length of the file
		 */
		public long 		getCompressedDataLen() 	{ return getLongProperty(propCompData); }
		
		/**
		 * Obtain the encrypted data length of the file 
		 * @return the encrypted data length of the file
		 */
		public long 		getEncryptedDataLen() 	{ return getLongProperty(propEncData); }
		
		/**
		 * Obtain the raw data digest of the file 
		 * @return the raw data digest of the file
		 */
		public byte[] 		getRawDigest() 			{ return getByteProperty(propRawData); }
		
		/**
		 * Obtain the compressed data digest of the file 
		 * @return the compressed data digest of the file
		 */
		public byte[] 		getCompressedDigest() 	{ return getByteProperty(propCompData); }
		
		/**
		 * Obtain the encrypted data digest of the file 
		 * @return the encrypted data digest of the file
		 */
		public byte[] 		getEncryptedDigest() 	{ return getByteProperty(propEncData); }
		
		/**
		 * Obtain the initialisation vector for the file 
		 * @return the initialisation vector for the file
		 */
		public byte[] 		getInitVector() 		{ return getByteProperty(propInitVector); }
		
		/**
		 * Obtain the secret key for the file 
		 * @return the secret key for the file
		 */
		public byte[] 		getSecretKey() 			{ return getByteProperty(propSecretKey); }
		
		/**
		 * Obtain the signature for the file 
		 * @return the signature for the file
		 */
		public byte[] 		getSignature() 			{ return getByteProperty(propSignature); }
		
		/**
		 * Standard constructor 
		 */
		private zipFileEntry() {}
		
		/**
		 * Construct encryption properties from encoded string
		 * @param pCodedString the encoded properties 
		 */
		private zipFileEntry(String pCodedString) throws finObject.Exception {
			StringBuilder	myString = new StringBuilder(pCodedString);
			String			myPropSep	= Character.toString(thePropSeparator);
			String			myFileSep	= Character.toString(theFileSeparator);
			int				myLoc;
			
			/* If there is a file separator in the string */
			if ((myLoc = myString.indexOf(myFileSep)) != -1) {
				/* Parse the trailing data and remove it */
				theNext = new zipFileEntry(myString.substring(myLoc+1));
				myString.setLength(myLoc-1);
			}
			
			/* while we have separators in the string */
			while ((myLoc = myString.indexOf(myPropSep)) != -1) {
				/* Parse the encoded property and remove it from the buffer */
				parseEncodedProperty(myString.substring(0, myLoc));
				myString.delete(0, myLoc+1);
			}
			
			/* Parse the remaining property */
			parseEncodedProperty(myString.toString());
			
			/* Determine whether the file is encrypted */
			if (getProperty(propEncData) != null) 	isEncrypted 	= true;
			
			/* Determine whether the file is compressed */
			if (getProperty(propCompData) != null) 	isCompressed 	= true;
		}
		
		/**
		 * Obtain the bytes value of the named property
		 * @return the value of the property or <code>null</code> if the property does not exist
		 */
		public byte[] getByteProperty(String pName) {
			property myProperty;
			
			/* Access the property */
			myProperty  = getProperty(pName);
			
			/* Return the value */
			return (myProperty == null) ? null : myProperty.getByteValue();
		}
		
		/**
		 * Obtain the long value of the named property
		 * @return the value of the property or <code>-1</code> if the property does not exist
		 */
		private long getLongProperty(String pName) {
			property myProperty;
			
			/* Access the property */
			myProperty  = getProperty(pName);
			
			/* Return the value */
			return (myProperty == null) ? -1 : myProperty.getLongValue();
		}
		
		/**
		 * Set the next file entry
		 * @param pEntry the next entry
		 */
		private void setNext(zipFileEntry pEntry) { theNext = pEntry; }

		/**
		 * Obtain the named property
		 * @return the value of the property or <code>null</code> if the property does not exist
		 */
		private property getProperty(String pName) {
			property myProperty;
			
			/* Loop through the properties */
			for (myProperty  = theProperties;
				 myProperty != null;
				 myProperty  = myProperty.getNext()) {
				/* Break loop if this is the desired property */
				if (myProperty.getName().compareTo(pName) == 0) break;
			}
			
			/* Return the value */
			return myProperty;
		}
		
		/**
		 * Set the named property
		 * @param pName the name of the property
		 * @param pValue the Value of the property
		 */
		private void setProperty(String pName, byte[] pValue) {
			property myProperty;
			
			/* Access any existing property */
			myProperty = getProperty(pName);
			
			/* If the property already exists */
			if (myProperty != null) {
				/* Set the new value */
				myProperty.setByteValue(pValue);
			}
			
			/* else this is a new property */
			else {
				/* Create the new property */
				myProperty = new property(pName, pValue, -1);
								
				/* Add it to the list */
				addToList(myProperty);
			}
		}
		
		/**
		 * Set the named property
		 * @param pName the name of the property
		 * @param pValue the Value of the property
		 */
		private void setProperty(String pName, long pValue) {
			property myProperty;
			
			/* Access any existing property */
			myProperty = getProperty(pName);
			
			/* If the property already exists */
			if (myProperty != null) {
				/* Set the new value */
				myProperty.setLongValue(pValue);
			}
			
			/* else this is a new property */
			else {
				/* Create the new property */
				myProperty = new property(pName, null, pValue);
								
				/* Add it to the list */
				addToList(myProperty);
			}
		}
		
		/**
		 * Obtain the encoded string representing the properties
		 * @return the encoded string
		 */
		private String getEncodedString() {
			property 		myProperty;
			StringBuilder 	myString = new StringBuilder(1000);
			StringBuilder	myValue  = new StringBuilder(200);
			char	 		myChar;
			int		 		myDigit;
			int		 		myInt;
			
			/* Loop through the list */
			for (myProperty  = theProperties;
				 myProperty != null;
				 myProperty  = myProperty.getNext()) {
				/* Build the value string */
				myValue.setLength(0);
				myValue.append(myProperty.getName());
				myValue.append(theValuSeparator);
				
				/* If we have a byte value */
				if (myProperty.getByteValue() != null) {
					/* For each byte in the value */
					for (Byte b : myProperty.getByteValue()) {
						/* Access the byte as an unsigned integer */
						myInt = (int) b;
						if (myInt<0) myInt+=256;
					
						/* Access the high digit */
						myDigit = myInt / 16;
						myChar = (char)((myDigit > 9) ? ('a' + (myDigit-10)) : ('0' + myDigit));
					
						/* Add it to the value string */
						myValue.append(myChar);
						
						/* Access the low digit */
						myDigit = myInt % 16;
						myChar = (char)((myDigit > 9) ? ('a' + (myDigit-10)) : ('0' + myDigit));
					
						/* Add it to the value string */
						myValue.append(myChar);
					}
				}
				
				/* Add the value separator */
				myValue.append(theLongSeparator);
				
				/* Add the long value if it exists */
				if (myProperty.getLongValue() != -1) {
					/* Access the value */
					long value = myProperty.getLongValue();
					
					/* handle negative values */
					boolean isNegative = (value < 0);
					if (isNegative) value = -value;
					
					/* Special case for zero */
					if (value == 0) myValue.append('0');
					
					/* else need to loop through the digits */
					else {
						/* Create a new string Builder variable */
						StringBuilder myLong = new StringBuilder();
						
						/* While we have digits to format */
						while (value > 0) {
							/* Access the digit and move to next one */
							myDigit = (int)(value % 16);
							myChar  = (char)((myDigit > 9) ? ('a' + (myDigit-10)) : ('0' + myDigit));
							myLong.insert(0, myChar);
							value  /= 16;
						}
						
						/* Reinstate negative sign and append to value */
						if (isNegative) myLong.insert(0, '-');
						myValue.append(myLong);
					}	
				}
				
				/* Add the value to the string */
				if (myString.length() > 0) myString.append(thePropSeparator);
				myString.append(myValue);
			}
			
			/* If we have further files */
			if (theNext != null) {
				/* Add the encoding of the further files */
				myString.append(theFileSeparator);
				myString.append(theNext.getEncodedString());
			}
			
			/* Return the encoded string */
			return myString.toString();
		}
		
		/**
		 * Parse the encoded string representation to obtain the property
		 * @param pProperty the encoded property
		 */
		private void parseEncodedProperty(String pValue) throws finObject.Exception {
			property	myProperty;
			String  	myName;
			String		myBytes;
			String		myLong;
			byte[]		myByteValue;
			long		myLongValue;
			char		myChar;
			int			myInt;
			int			myLen;
			int 		myLoc;
			
			/* Locate the Value separator in the string */
			myLoc = pValue.indexOf(theValuSeparator);
			
			/* Check that we found the value separator */
			if (myLoc == -1) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  "Missing value separator: " + pValue);

			/* Split the values and name */
			myName 	= pValue.substring(0, myLoc);
			myBytes = pValue.substring(myLoc+1);
			myLen   = myBytes.length();
			
			/* If the name is already present reject it */
			if (getProperty(myName) != null) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  "Duplicate name: " + pValue);
			
			/* Locate the Long separator in the string */
			myLoc = myBytes.indexOf(theLongSeparator);
			
			/* Check that we found the long separator */
			if (myLoc == -1) 
				throw new finObject.Exception(ExceptionClass.DATA,
											  "Missing long separator: " + pValue);

			/* Access the separate byte and long values */
			myLong 	= (myLoc < myLen-1) ? myBytes.substring(myLoc+1) : null;
			myBytes = (myLoc > 0) ? myBytes.substring(0, myLoc) : null;
			
			/* Initialise the values */
			myByteValue = null;
			myLongValue = -1;

			/* If we have a bytes array */
			if (myBytes != null) {
				/* Access the length of the bytes section */
				myLen = myBytes.length();
				
				/* Check that it has an even length */
				if ((myLen % 2) != 0) 
					throw new finObject.Exception(ExceptionClass.DATA,
												  "Invalid Bytes Encoded Value Length: " + pValue);
				
				/* Allocate the new bytes array */
				myByteValue = new byte[myLen / 2];
			
				/* Loop through the string */
				for (int i=0; i < myLen; i+=2) {
					/* Access the top level byte */
					myChar = myBytes.charAt(i);
					myInt  = 16 * ((myChar >= 'a') ? 10 + (myChar - 'a') : (myChar - '0'));
			
					/* Check that the char is a valid hex digit */
					if (!Character.isDigit(myChar) && ((myChar < 'a') || (myChar > 'f'))) 
						throw new finObject.Exception(ExceptionClass.DATA,
													  "Non Hexadecimal Bytes Value: " + pValue);
					
					/* Access the second byte */
					myChar = myBytes.charAt(i+1);
					myInt  += ((myChar >= 'a') ? 10 + (myChar - 'a') : (myChar - '0'));
						
					/* Check that the char is a valid hex digit */
					if (!Character.isDigit(myChar) && ((myChar < 'a') || (myChar > 'f'))) 
						throw new finObject.Exception(ExceptionClass.DATA,
													  "Non Hexadecimal Bytes Value: " + pValue);
					
					/* Convert to byte and store */
					if (myInt > 127) myInt -= 256;
					myByteValue[i/2] = (byte)myInt;
				}
			}
			
			/* If we have a long value */
			if (myLong != null) {				
	 			/* Initialise values */
		 		myLen 		= myLong.length();
		 		myLongValue = 0;

		 		/* Loop through the characters of the integer part of the value */
		 		for (int i=0; i<myLen; i++) {
					/* Access the next character */
					myChar = myLong.charAt(i);
		 				
					/* Check that the char is a valid hex digit */
					if (!Character.isDigit(myChar) && ((myChar < 'a') || (myChar > 'f'))) 
						throw new finObject.Exception(ExceptionClass.DATA,
										    		  "Non Hexadecimal Numeric Value: " + myLong);
						
					/* Add into the value */
					myLongValue *= 16;
					myLongValue += ((myChar >= 'a') ? 10 + (myChar - 'a') : (myChar - '0'));
		 		}
			}
			
			/* Create a new property */
			myProperty = new property(myName, myByteValue, myLongValue);
			
			/* Add it to the list */
			addToList(myProperty);
		}

		/**
		 * Add a property to the list
		 * @param pProperty the property to add 
		 */
		private void addToList(property pProperty) {
			property myProperty;
			property myLast;
			String	 myName;
			
			/* Access the property name */
			myName = pProperty.getName();
			
			/* Loop through the list */
			for (myProperty  = theProperties, myLast = null;
				 myProperty != null;
				 myLast = myProperty, myProperty  = myProperty.getNext()) {
				/* Break if this property should be later than the passed property */
				if (myProperty.getName().compareTo(myName) > 0) break;
			}

			/* If we do not have an insert point */
			if (myLast == null) {
				/* Add the value to the head of the list */
				pProperty.setNext(theProperties);
				theProperties = pProperty;
			}
			
			/* Else we have an insert point */
			else {
				/* Add the value in the middle of the list */
				pProperty.setNext(myProperty);
				myLast.setNext(pProperty);
			}
		}
		
		/**
		 * compare this property set to another
		 * @param pThat the properties to compare against
		 * @return (-1,0,1) depending on order 
		 */
		 public int compareTo(zipFileEntry pThat) {
			 property myThis;
			 property myThat;
			 int 	  iDiff;
		 
			 /* If that does not exist return 1 */
			 if (pThat == null) return 1;
			 
			 /* Loop through the list */
			 for (myThis  = theProperties, myThat = pThat.theProperties;
				  myThis != null;
				  myThis  = myThis.getNext(), myThat = myThat.getNext()) {
				 /* If we have finished "that's" list return 1 */
				 if (myThat == null) return 1;
				 
				 /* If there is a difference return it */
				 if ((iDiff = myThis.compareTo(myThat)) != 0) return iDiff;
			 }
			 
			 /* If we have not finished "that's" list return -1 */
			 if (myThat != null) return -1;

			 /* If we have another entry compare that */
			 if (theNext != null) return theNext.compareTo(pThat.theNext);
			 
			 /* If there are further files for that */
			 if (pThat.theNext != null) return -1;
			 
			 /* Return no difference */
			 return 0;
		 }
		 
		/**
		 * Inner Property class
		 */
		private class property {
			/**
			 * Name of property
			 */
			private String 		theName 		= null;
			
			/**
			 * Value of property
			 */
			private byte[]		theByteValue	= null;
			
			/**
			 * Value of property
			 */
			private long		theLongValue	= -1;
			
			/** 
			 * Link to next property
			 */
			private property	theNext			= null;
			
			/**
			 * Standard Constructor
			 * @param pName the name of the property
			 * @param pBytes the Bytes value of the property
			 * @param pLong the Long value of the property
			 */
			private property(String pName, byte[] pBytes, long pLong) {
				/* Store name and value */
				theName 		= pName;
				theByteValue 	= pBytes;
				theLongValue	= pLong;
			}
			
			/**
			 * Obtain the name of the property
			 * @return the name of the property
			 */
			private String getName() { return theName; }
			
			/**
			 * Obtain the byte value of the property
			 * @return the value of the property
			 */
			private byte[] 	getByteValue() { return theByteValue; }
						
			/**
			 * Obtain the byte value of the property
			 * @return the value of the property
			 */
			private long 	getLongValue() { return theLongValue; }
						
			/**
			 * Obtain the next property
			 * @return the next property
			 */
			private property getNext() { return theNext; }
			
			/**
			 * Set the byte value
			 * @param pValue the new value
			 */
			private void setByteValue(byte[] pValue) { 
				theByteValue = Arrays.copyOf(pValue, pValue.length); }
			
			/**
			 * Set the long value
			 * @param pValue the new value
			 */
			private void setLongValue(long pValue) { 
				theLongValue = pValue; }
			
			/**
			 * Set the next property
			 * @param pProperty the next property
			 */
			private void setNext(property pProperty) { theNext = pProperty; }

			/**
			 * compare this property to another
			 * @param pThat the property to compare against
			 * @return (-1,0,1) depending on order 
			 */
			private int compareTo(property pThat) {
				 int 	  iDiff;
			 
				 /* Handle differences in name */
				 if ((iDiff = theName.compareTo(pThat.getName())) != 0) return iDiff;
				 
				 /* Handle differences in long value */
				 if (theLongValue != pThat.getLongValue()) 
					 return (theLongValue < pThat.getLongValue()) ? -1 : 1;
				 
				 /* If we are identical return 0 */
				 if (theByteValue == pThat.getByteValue()) 	return 0;
				 if (theByteValue == null) 					return -1;
				 if (pThat.getByteValue() == null) 			return 1;

				 /* Handle non-equal arrays can't get order he-hum */
				 if (!Arrays.equals(theByteValue, pThat.getByteValue())) return 1;
				 
				 /* Return OK */
				 return 0;
			 }
		}
	}
}
