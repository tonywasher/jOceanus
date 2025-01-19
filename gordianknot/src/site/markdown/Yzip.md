# GordianKnot ZipFile Support

## Overview
ZipFiles are supported via the <strong>GordianZipFactory</strong> interface.

GordianKnot provides support for to creating and reading encrypted ZipFiles.
This is built on top of the Java ZipFile support and utilises the encryption facilities of GordianKnot to encrypt and
decrypt each file entry. LZMA compression is also provided (based on the 7-Zip LZMA SDK), which provides significantly
improved compression over the Deflation standard Java method.

Encryption is performed in a similar fashion to KeySet encryption, as follows (N = number of configured encryption steps),
although all keys and initialisationVectors are randomly generated per zipEntry. The selection of encryption algorithms is
also randomly done rather than from a seed value< 

1. Digest is calculated on the plain text
2. LZMA compression is performed
3. StreamKey encryption is performed
4. (N-1)*SymKey ECB encryption (first with ISO7816-4 padding, remainder with NONE)
5. SymKey SIC encryption
6. Mac calculated on encrypted data
            
All filenames in the public zipFile directory are written as File1, File2 etc to further hide information

The names of the files, and the (wrapped) encryption details are written as an encrypted final zipEntry

The keySetHash used to encrypt the final file entry is recorded as additionalData for the directory entry

Note that updating of ZipFiles is not supported, only creation and extraction.

##  ZipLocks
Each zip file is secured using a zipLock structure, which at its core is a lock. Any of the three supported lock types can be used

### Sample Creation of Encrypted ZipFile
The following sample reads a designated file directory and creates an encrypted zipFile of the contents
```
/* Access factory */
final GordianFactory myBaseFactory = GordianGenerator.createFactory();
final GordianLockFactory myLockFactory = myBaseFactory.getLockFactory();
final GordianZipFactory myZipFactory = myBaseFactory.getZipFactory();

/* Create new Lock */
final char[] myPassword = .....
final GordianPasswordLockSpec mySpec = new GordianKeySetHashSpec(new GordianKeySetSpec(pKeyLen));
final GordianLock myLock = myLockFactory.createFactoryLock(mySpec, myPassword);
final GoroanZipLock myZipLock = myZipFactory.createZipLock(myLock);

/* Initialise the Zip file */
final ByteArrayOutputStream myZipStream = new ByteArrayOutputStream();
final GordianZipWriteFile myZipFile = myZipFactory.createZipFile(myZipLock, myZipStream);

/* Obtain list of files */
file File myDirectory = ....
final File[] myFiles = pDirectory.listFiles();

/* Loop through the files in the directory */
for (File myFile : myFiles) {
    /* Skip directories */
    if (myFile.isDirectory()) {
        continue;
    }

    /* Open the file for reading */
    InputStream myInFile = new FileInputStream(myFile);
    InputStream myInBuffer = new BufferedInputStream(myInFile);

    /* Open the zipEntry stream */
    OutputStream myOutput = myZipFile.createOutputStream(new File(myFile.getName()), true);

    /* Copy file content to zip file */
    myInBuffer.transferTo(myOutput);

    /* Close files */
    myInBuffer.close();
    myOutput.close();
}

/* Close Zip file */
myZipFile.close();

/* ZipFile as byte array */
final byte[] myBytes = myZipStream.toByteArray();
```

### Sample Parsing of Encrypted ZipFile
The following sample reads the contents of an encrypted zipFile
```
/* Access factory */
final GordianFactory myBaseFactory = GordianGenerator.createFactory();
final GordianZipFactory myZipFactory = myBaseFactory.getZipFactory();

/* Access the file (bytes from previous sample) */
final ByteArrayInputStream myInputStream = new ByteArrayInputStream(myBytes);
final GordianZipReadFile myZipFile = myZipMgr.openZipFile(myInputStream);

/* Unlock the file */
final GordianLock myLock = myZipFile.getLock();
final char[] myPassword = ....
myLock.unlock(myPassword);

/* Access the contents */
final GordianZipFileContents myContents = myZipFile.getContents();

/* Loop through the entries */
final Iterator<GordianZipFileEntry> myIterator = myContents.iterator();
while (myIterator.hasNext()) {
    /* Access next entry */
    final GordianZipFileEntry myEntry = myIterator.next();
    final InputStream myZipInput = myZipFile.createInputStream(myEntry);

    /* Read the data */
    final byte[] myZipData = myZipInput.readAllBytes();
}

/* Close Zip file */
myZipFile.close();
```
