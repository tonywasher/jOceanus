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
	 * The encoding property name of a file
	 */
	private final static String propEncoding		= "UTF-8";
	
	/**
	 * ZipOutputFile Class represents a zip file that is in the process of being built 
	 */
	public static class Output {
		/**
		 * Security Control for this zip file
		 */
		private SecurityControl			theControl 		= null;
		
		/**
		 *	The list of files contained in this ZipFile together with properties 
		 */
		private ZipFileEntry			theFiles		= null;
		
		/**
		 * The underlying zip output stream
		 */
		private ZipOutputStream 		theStream		= null;

		/**
		 * The active output stream
		 */
		private DigestStream.Output		theOutput		= null;

		/**
		 * The compressed output stream
		 */
		private DigestStream.Output 	theCompDigest	= null;

		/**
		 * The encrypted output stream
		 */
		private DigestStream.Output 	theEncDigest	= null;

		/**
		 * The encryption output stream
		 */
		private EncryptionStream.Output theEncrypted	= null;

		/**
		 * The active zipEntry
		 */
		private ZipEntry 				theEntry		= null;

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
		public OutputStream getOutputStream(File pFile, zipMode pMode) throws Exception {
			GZIPOutputStream myZip;
		
			/* Reject call if we have an closed the stream */
			if (theStream == null)
				throw new Exception(ExceptionClass.DATA,
						  			"ZipFile is closed");
			
			/* Reject call if we have an open stream */
			if (theOutput != null)
				throw new Exception(ExceptionClass.DATA,
						  			"Output stream already open");
			
			/* Protect against exceptions */
			try {
				/* Start the new entry */
				theEntry 	= new ZipEntry(pFile.getPath());
				theStream.putNextEntry(theEntry);
			}
			
			/* Catch exceptions */
			catch (Throwable e) {
				throw new Exception(ExceptionClass.DATA,
									"Exception writing to Zip file",
									e);
			}			
			
			/* Protect against exceptions */
			try {
				/* Switch on the mode */
				switch (pMode) {
					case RAW:
						/* Create the digest output stream directly onto the Zip stream */
						theOutput 		= new DigestStream.Output(new wrapOutputStream());
						break;
					case COMPRESS:
						/* Create the compressed digest stream directly onto the Zip stream */
						theCompDigest	= new DigestStream.Output(new wrapOutputStream());
					
						/* Create a GZIP output stream onto the compressed digest stream */
						myZip			= new GZIPOutputStream(theCompDigest);
					
						/* Create the digest output stream directly onto the GZIP stream */
						theOutput		= new DigestStream.Output(myZip);
						break;					
					case ENCRYPT:
						/* Create the encrypted digest stream directly onto the Zip stream */
						theEncDigest	= new DigestStream.Output(new wrapOutputStream());
					
						/* Create the encryption stream directly onto the encryption digest stream */
						theEncrypted	= new EncryptionStream.Output(theControl, theEncDigest);
					
						/* Create the digest output stream directly onto the encryption stream */
						theOutput		= new DigestStream.Output(theEncrypted);
						break;					
					case COMPRESS_AND_ENCRYPT:
						/* Create the encrypted digest stream directly onto the Zip stream */
						theEncDigest	= new DigestStream.Output(new wrapOutputStream());
					
						/* Create the encryption stream directly onto the encryption digest stream */
						theEncrypted	= new EncryptionStream.Output(theControl, theEncDigest);
					
						/* Create the compressed output stream directly onto the Encryption stream */
						theCompDigest	= new DigestStream.Output(theEncrypted);
					
						/* Create a GZIP output stream onto the compressed digest stream */
						myZip			= new GZIPOutputStream(theCompDigest);					
						
						/* Create the raw output stream directly onto the GZIP stream */
						theOutput		= new DigestStream.Output(myZip);
						break;					
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
			byte[]			myDigest;
			byte[]			myKey;
			byte[]			myVector;
			byte[]			mySign;
			long			myLength;
			
			/* Protect against exceptions */
			try {
				/* If we have an output stream */
				if (theOutput != null) {
					/* Close the active entry */
					theStream.closeEntry();
			
					/* Create a new zipFileEntry */
					myEntry = new ZipFileEntry();
					addToList(myEntry);
					
					/* Add the name of the file entry */
					myEntry.setProperty(ZipFileEntry.propName, theEntry.getName().getBytes(propEncoding));
			
					/* Access raw details */
					myDigest = theOutput.getDigest();
					myLength = theOutput.getDataLen();
					
					/* Set the properties */
					myEntry.setProperty(ZipFileEntry.propRawData, myDigest);
					myEntry.setProperty(ZipFileEntry.propRawData, myLength);

					/* If we have a compression digest */
					if (theCompDigest != null) {
						/* Access compressed details */
						myDigest = theCompDigest.getDigest();
						myLength = theCompDigest.getDataLen();
						
						/* Set the properties */
						myEntry.setProperty(ZipFileEntry.propCompData, myDigest);
						myEntry.setProperty(ZipFileEntry.propCompData, myLength);
					}
					
					/* If we have an encrypted digest */
					if (theEncrypted != null) {
						/* Access encrypted details */
						myDigest = theEncDigest.getDigest();
						myLength = theEncDigest.getDataLen();
						myKey 	 = theEncrypted.getWrappedKey();
						myVector = theEncrypted.getInitVector();
						
						/* Set the properties */
						myEntry.setProperty(ZipFileEntry.propEncData, 	myDigest);
						myEntry.setProperty(ZipFileEntry.propEncData, 	myLength);
						myEntry.setProperty(ZipFileEntry.propSecretKey,  myKey);
						myEntry.setProperty(ZipFileEntry.propInitVector, myVector);
						
						/* Calculate the signature and add it to properties */
						mySign = theControl.signFile(myEntry);
						myEntry.setProperty(ZipFileEntry.propSignature, mySign);
					}
					
					/* Release the entry */
					theEntry = null;
				}
				
				/* Reset streams */
				theOutput 		= null;
				theCompDigest 	= null;
				theEncDigest 	= null;
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
		 *	The list of files contained in this ZipFile together with properties 
		 */
		private ZipFileEntry		theFiles		= null;
		
		/** 
		 * The name of the Zip file
		 */
		private File				theZipFile		= null;
		
		/**
		 * Obtain the next file entry 
		 * @return the next file entry
		 */
		public ZipFileEntry	getFiles() 				{ return theFiles; }
		
		/**
		 * Constructor 
		 * @param pControl the security control
		 * @param pFile the file to read
		 */
		public Input(SecurityControl	pControl,
				 	 File 				pFile) throws Exception {
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
				/* record the security control */
				theControl 	= pControl;
				
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
				theFiles = new ZipFileEntry(new String(myBuffer));
				
				/* Close the file */
				myZipFile.close();
			}
			
			/* Catch exceptions */
			catch (Throwable e) {
				throw new Exception(ExceptionClass.DATA,
									"Exception writing to Zip file",
									e);
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
					/* Verify Encryption details and set for decryption */
					theControl.verifyFile(pFile);
					
					/* Wrap a digest input stream around the zip file */
					myDigest = new DigestStream.Input(myCurrent);
					
					/* Tell the digest about the expected compression digest and length */
					myDigest.setExpectedDetails("Compressed", 
												pFile.getEncryptedDataLen(),
												pFile.getEncryptedDigest());

					/* Wrap the zip file with the decrypt stream */
					myDecrypt = new EncryptionStream.Input(theControl, myDigest);
					
					/* Tell the encryption about the expected encryption details */
					myDecrypt.setExpectedDetails("Encrypted", 
												 pFile.getSecretKey(),
												 pFile.getInitVector());

					/* Record the current stream */
					myCurrent = myDecrypt;
				}
				
				/* If the file is compressed */
				if (pFile.isCompressed()) {
					/* Wrap a digest input stream around the zip file */
					myDigest = new DigestStream.Input(myCurrent);
					
					/* Tell the digest about the expected compression digest and length */
					myDigest.setExpectedDetails("Compressed", 
												pFile.getCompressedDataLen(),
												pFile.getCompressedDigest());

					/* Wrap a GZIPInputStream around the digest */
					myCurrent = new GZIPInputStream(myDigest);
				}
				
				/* Wrap a digest input stream around the zip file */
				myDigest = new DigestStream.Input(myCurrent);
				
				/* Tell the digest about the expected compression digest and length */
				myDigest.setExpectedDetails("Raw", 
											pFile.getRawDataLen(),
											pFile.getRawDigest());

				/* Record the current stream */
				myCurrent = myDigest;
			}
			
			/* Catch exceptions */
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
