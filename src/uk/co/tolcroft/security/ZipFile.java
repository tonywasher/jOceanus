package uk.co.tolcroft.security;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class ZipFile {
	/**
	 * The Header file name
	 */
	private final static String fileHeader			= "zipHeader";
	
	/**
	 * The Data file name
	 */
	public  final static String fileData			= "zipData";
	
	/**
	 * The encoding property name of a file
	 */
	private final static String propEncoding		= SecurityControl.ENCODING;
	
	/**
	 * ZipOutputFile Class represents a zip file that is in the process of being built 
	 */
	public static class Output {
		/**
		 * Security Control for this zip file
		 */
		private SecurityControl				theControl 		= null;
		
		/**
		 * The underlying zip output stream
		 */
		private ZipOutputStream 			theStream		= null;

		/**
		 *	The list of files contained in this ZipFile together with properties 
		 */
		private ZipFileEntry				theFiles		= null;
		
		/**
		 * The active zipEntry
		 */
		private ZipEntry 					theEntry		= null;

		/**
		 *	The mode of the current Zip file 
		 */
		private ZipEntryMode				theMode			= null;
		
		/**
		 * The active output stream
		 */
		private OutputStream				theOutput		= null;

		/**
		 * The compressed output stream
		 */
		private DigestStream.Output[] 		theDigests		= null;

		/**
		 * The encryption output stream
		 */
		private EncryptionStream.Output[] 	theEncrypts		= null;

		/**
		 *	Constructor for new output zip file
		 *  @param pControl the security control
		 *	@param pFile the file details for the new zip file 
		 */
		public Output(SecurityControl	pControl,
				 	  File 				pFile) throws Exception {
			FileOutputStream		myOutFile;
			BufferedOutputStream	myOutBuffer;
			
			/* Protect against exceptions */
			try {			
				/* record the security control */
				theControl 	= pControl;
				
				/* Create the output streams */
				myOutFile 	= new FileOutputStream(pFile);
				myOutBuffer	= new BufferedOutputStream(myOutFile);
				theStream 	= new ZipOutputStream(myOutBuffer);
			}
				
			/* Catch exceptions */
			catch (Throwable e) {
				throw new Exception(ExceptionClass.DATA,
									"Exception creating new Zip file",
									e);
			}			
		}
		
		/**
		 *	Obtain an output stream for an entry in the zip file
		 *	@param pFile the file details for the new zip entry
		 *	@param pMode the mode to store the new file in 
		 */
		public OutputStream getOutputStream(File pFile, ZipEntryMode pMode) throws Exception {
			GZIPOutputStream 		myZip;
			DigestStream.Output		myDigest;
			EncryptionStream.Output	myEncrypt;
			int						iDigest;
			int						iEncrypt;
		
			/* Reject call if we have closed the stream */
			if (theStream == null)
				throw new Exception(ExceptionClass.LOGIC,
						  			"ZipFile is closed");
			
			/* Reject call if we have an open stream */
			if (theOutput != null)
				throw new Exception(ExceptionClass.LOGIC,
						  			"Output stream already open");
			
			/* Reject call if the filename is fileHeader */
			if (pFile.getPath().equals(fileHeader))
				throw new Exception(ExceptionClass.LOGIC,
						  			"Cannot use reserved filename: " + fileHeader);
			
			/* Reject call if we have no security and encryption is requested */
			if ((theControl == null) &&
				(pMode.doEncrypt()))
				throw new Exception(ExceptionClass.LOGIC,
						  			"Encryption not allowed for this ZipFile. No security credentials were provided.");
			
			/* Protect against exceptions */
			try {
				/* Start the new entry */
				theEntry 	= new ZipEntry(pFile.getPath());
				theStream.putNextEntry(theEntry);

				/* Store the mode */
				theMode 	= pMode;
				
				/* Simply create a wrapper on the output stream */
				theOutput 	= new wrapOutputStream();

				/* If we are encrypting */
				if (pMode.doEncrypt()) {
					/* Create the arrays */
					theDigests 	= new DigestStream.Output[pMode.getNumDigests()];
					theEncrypts	= new EncryptionStream.Output[pMode.getNumEncrypts()];
					iDigest		= 0;
					
					/* Create an initial digest stream */
					myDigest 	= new DigestStream.Output(pMode.getDigestType(), theOutput);
					theOutput	= myDigest;
					theDigests[iDigest++] = myDigest;
					
					/* For each encryption stream */
					for (iEncrypt=0; iEncrypt<pMode.getNumEncrypts(); iEncrypt++) {
						/* Create the encryption stream */
						myEncrypt 	= new EncryptionStream.Output(theControl,
																  pMode.getKeyType(iEncrypt+1),
																  theOutput);
						theOutput	= myEncrypt;
						theEncrypts[iEncrypt] = myEncrypt;
					
						/* if we are debugging */
						if (pMode.doDebug()) {
							/* Create an extra digest stream */
							myDigest 	= new DigestStream.Output(pMode.getDigestType(), theOutput);
							theOutput	= myDigest;
							theDigests[iDigest++] = myDigest;
						}
					}
					
					/* Create a GZIP output stream onto the output */
					myZip		= new GZIPOutputStream(theOutput);
					theOutput	= myZip;

					/* Create a final digest stream */
					myDigest 	= new DigestStream.Output(pMode.getDigestType(), theOutput);
					theOutput	= myDigest;
					theDigests[iDigest++] = myDigest;
				}					
			}
			
			/* Catch exceptions */
			catch (Throwable e) {
				throw new Exception(ExceptionClass.DATA,
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
			ZipFileEntry 	myEntry;
			byte[]			mySign;
			
			/* Protect against exceptions */
			try {
				/* If we have an output stream */
				if (theOutput != null) {
					/* Close the active entry */
					theStream.closeEntry();
			
					/* Create a new zipFileEntry */
					myEntry = new ZipFileEntry();
					addToList(myEntry);
					
					/* Add the name and mode of the file entry */
					myEntry.setProperty(ZipFileEntry.propName, theEntry.getName().getBytes(propEncoding));
					myEntry.setProperty(ZipFileEntry.propName, theMode.getMode());
			
					/* If we have digest streams */
					if (theDigests != null) {
						/* Loop through the digests */
						for (int iDigest=0; iDigest < theDigests.length; iDigest++) {
							/* Set the digest properties */
							myEntry.setProperty(ZipFileEntry.propDigest, iDigest+1, theDigests[iDigest].getDigest());
							myEntry.setProperty(ZipFileEntry.propDigest, iDigest+1, theDigests[iDigest].getDataLen());							
						}
					}
					
					/* If we have encryption streams */
					if (theEncrypts != null) {
						/* Loop through the digests */
						for (int iEncrypt=0; iEncrypt < theEncrypts.length; iEncrypt++) {
							/* Set the encryption properties */
							myEntry.setProperty(ZipFileEntry.propSecretKey,  iEncrypt+1, theEncrypts[iEncrypt].getWrappedKey());
							myEntry.setProperty(ZipFileEntry.propInitVector, iEncrypt+1, theEncrypts[iEncrypt].getInitVector());
						}

						/* Calculate the signature and add it to properties */
						mySign = theControl.signFile(myEntry);
						myEntry.setProperty(ZipFileEntry.propSignature, mySign);
					}
					
					/* Release the entry */
					theEntry = null;
				}
				
				/* Reset streams */
				theOutput 	= null;
				theDigests 	= null;
				theEncrypts	= null;
				theMode		= null;
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

						/* Create the header entry */
						theEntry 	= new ZipEntry(fileHeader);
						
						/* If we have security control */
						if (theControl != null) {
							/* Access the asymmetric key */
							AsymmetricKey myKey = theControl.getAsymKey();
							
							/* Declare the security control and encrypt the header */
							theEntry.setExtra(theControl.getSecurityKey().getBytes(propEncoding));
							myBytes = myKey.encryptString(myHeader, myKey);
						}
						
						/* else just extract the bytes from the string */
						else myBytes  = myHeader.getBytes(propEncoding);
						
						/* Start the new entry */
						theStream.putNextEntry(theEntry);

						/* Write the bytes to the zip file and close the entry */
						theStream.write(myBytes);
						theStream.closeEntry();
					}
					
					/* close the stream */
					theStream.flush();
					theStream.close();
					theStream = null;
					
					/* reSeed the random number generator */
					theControl.reSeedRandom();
				}
			
				/* Catch exceptions */
				catch (IOException e) 	{ throw e; }
				catch (Throwable e) 	{ throw new IOException(e);	}
			}			
		}
		
		/**
		 * Add a zipFileEntry to the list
		 * @param pEntry the entry to add 
		 */
		private void addToList(ZipFileEntry pEntry) {
			ZipFileEntry myEntry;
			ZipFileEntry myLast;

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
		 * Wrapper class to catch close of output stream and prevent it from closing the ZipFile
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
	}
	
	/**
	 * ZipInputFile Class represents a zip file that can be read 
	 */
	public static class Input {
		/**
		 * The extension size for the buffer
		 */
		private static int 			BUFFERSIZE 		= 1024;
		
		/**
		 * Security Control for this zip file
		 */
		private SecurityControl		theControl 		= null;
		
		/**
		 * Security Key for this zip file
		 */
		private String				theSecurityKey	= null;
		
		/**
		 *	The list of files contained in this ZipFile together with properties 
		 */
		private ZipFileEntry		theFiles		= null;
		
		/** 
		 * The name of the Zip file
		 */
		private File				theZipFile		= null;
		
		/**
		 * The Header input stream
		 */
		private ZipInputStream		theHdrStream	= null;
		
		/**
		 * Obtain the next file entry 
		 * @return the next file entry
		 */
		public ZipFileEntry			getFiles() 		{ return theFiles; }
		
		/**
		 * Obtain the security key for the file 
		 * @return the next file entry
		 */
		public String				getSecurityKey(){ return theSecurityKey; }
		
		/**
		 * Constructor 
		 * @param pControl the security control
		 * @param pFile the file to read
		 */
		public Input(File	pFile) throws Exception {
			FileInputStream 	myInFile;
			BufferedInputStream myInBuffer;
			ZipEntry			myEntry;
		
			/* Protect against exceptions */
			try {
				/* Store the zipFile name */
				theZipFile = new File(pFile.getPath());
				
				/* Open the zip file for reading */
				myInFile   		= new FileInputStream(pFile);
				myInBuffer 		= new BufferedInputStream(myInFile);
				theHdrStream	= new ZipInputStream(myInBuffer);
		
				/* Loop through the Zip file entries */
				while((myEntry = theHdrStream.getNextEntry()) != null) {
					/* Break if we found the header entry */
					if (myEntry.getName().compareTo(fileHeader) == 0) break;
				}
				
				/* Pick up security key if it is present */
				if (myEntry.getExtra() != null) 
					theSecurityKey = new String(myEntry.getExtra());
			}
			
			/* Catch exceptions */
			catch (Throwable e) {
				throw new Exception(ExceptionClass.DATA,
									"Exception accessing Zip file",
									e);
			}
		}
		
		/**
		 * Set the security control
		 * @param pControl the security control
		 */
		public void setSecurityControl(SecurityControl pControl) throws Exception {
			byte[]			    myBuffer 	= new byte[BUFFERSIZE];
			int					myRead;
			int					myLen;
			int					mySpace;
			
			/* Protect against exceptions */
			try {
				/* If we have a security string */
				if (theSecurityKey != null) {
					/* Reject this is the wrong security control */
					if (!pControl.getSecurityKey().equals(theSecurityKey))
						throw new Exception(ExceptionClass.LOGIC,
			  								"Security control does not match ZipFile Security.");					
			
					/* Store the control */
					theControl = pControl;
				}

				/* Initialise variables */
				myLen   = 0;
				mySpace = BUFFERSIZE;
			
				/* Read the header entry */
				while ((myRead = theHdrStream.read(myBuffer, myLen, mySpace)) != -1) {
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
			
				/* If we have a security string */
				if (theSecurityKey != null) {
					/* Access the asymmetric key */
					AsymmetricKey myKey = theControl.getAsymKey();
					
					/* Parse the decrypted header */
					theFiles = new ZipFileEntry(myKey.decryptString(myBuffer, myKey));
				}
				
				/* else we have not encrypted */
				else {
					/* Parse the header details */
					theFiles = new ZipFileEntry(new String(myBuffer));
				}
			}
			
			/* Catch exceptions */
			catch (Exception e) { throw e; }
			catch (Throwable e) {				
				throw new Exception(ExceptionClass.DATA,
									"Exception reading header of Zip file",
									e);
			}
			
			finally { 
				/* Close the file */
				try { if (theHdrStream != null) theHdrStream.close(); } catch (Throwable e) {}
				theHdrStream = null;
			}
		}
		
		/**
		 *	Obtain an input stream for an entry in the zip file
		 *	@param pFile the file details for the new zip entry
		 */
		public InputStream getInputStream(ZipFileEntry pFile) throws Exception {
			FileInputStream 		myInFile;
			BufferedInputStream 	myInBuffer;
			ZipInputStream			myZipFile;
			ZipEntry				myEntry;
			String					myName;
			InputStream				myCurrent;
			DigestStream.Input		myDigest;
			EncryptionStream.Input	myDecrypt;
			ZipEntryMode			myMode;
			int						iDigest;
			int						iDecrypt;
		
			/* Protect against exceptions */
			try {
				/* Open the zip file for reading */
				myInFile   = new FileInputStream(theZipFile);
				myInBuffer = new BufferedInputStream(myInFile);
				myZipFile  = new ZipInputStream(myInBuffer);
		
				/* Access the name and mode of the file entry */
				myName = pFile.getFileName();
				myMode = pFile.getFileMode();
				
				/* Loop through the Zip file entries */
				while((myEntry = myZipFile.getNextEntry()) != null) {
					/* Break if we found the correct entry */
					if (myEntry.getName().compareTo(myName) == 0) break;
				}
				
				/* Note the current input stream */
				myCurrent = myZipFile;
				iDigest	  = 1;
				
				/* If the file is encrypted */
				if (myMode.doEncrypt()) {
					/* Reject call if we have no security provided */
					if (theControl == null) 
						throw new Exception(ExceptionClass.LOGIC,
								  			"Decryption required for this entry. No security credentials were provided.");					
					
					/* Verify Encryption details and set for decryption */
					theControl.verifyFile(pFile);
					
					/* Wrap a digest input stream around the zip file */
					myDigest  = new DigestStream.Input(myMode.getDigestType(), myCurrent);
					myCurrent = myDigest;
				
					/* Tell the digest stream about the expected digest and length */
					myDigest.setExpectedDetails("Final", 
												pFile.getDigestLen(iDigest),
												pFile.getDigest(iDigest++));

					/* For each decryption stream */
					for (iDecrypt=0; iDecrypt<myMode.getNumEncrypts(); iDecrypt++) {
						/* Create the encryption stream */
						myDecrypt 	= new EncryptionStream.Input(theControl,
																 pFile.getSecretKey(iDecrypt+1),
																 myMode.getKeyType(iDecrypt+1),
																 pFile.getInitVector(iDecrypt+1),
																 myCurrent);
						myCurrent	= myDecrypt;
						
						/* if we are debugging */
						if (myMode.doDebug()) {
							/* Create an extra digest stream */
							myDigest 	= new DigestStream.Input(myMode.getDigestType(), myCurrent);
							myCurrent	= myDigest;

							/* Tell the digest stream about the expected digest and length */
							myDigest.setExpectedDetails("Debug" + iDigest, 
														pFile.getDigestLen(iDigest),
														pFile.getDigest(iDigest++));
						}
					}					

					/* Wrap a GZIPInputStream around the stream */
					myCurrent = new GZIPInputStream(myCurrent);
					
					/* Wrap a digest input stream around the stream */
					myDigest  = new DigestStream.Input(myMode.getDigestType(), myCurrent);
					myCurrent = myDigest;
				
					/* Tell the digest stream about the expected digest and length */
					myDigest.setExpectedDetails("Raw", 
												pFile.getDigestLen(iDigest),
												pFile.getDigest(iDigest++));
				}
			}
			
			/* Catch exceptions */
			catch (Exception e) { throw e; }
			catch (Throwable e) {
				throw new Exception(ExceptionClass.DATA,
									"Exception creating new Output stream",
									e);
			}
			
			/* return the new stream */
			return myCurrent;
		}		
	}
}
