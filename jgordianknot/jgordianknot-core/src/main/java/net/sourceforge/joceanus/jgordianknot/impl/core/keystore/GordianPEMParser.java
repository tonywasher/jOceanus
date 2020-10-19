package net.sourceforge.joceanus.jgordianknot.impl.core.keystore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairSetCertificate;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

public class GordianPEMParser {
    /**
     * The bracket sequence.
     */
    private static final String BRACKET = "-----";

    /**
     * The begin header.
     */
    private static final String BEGIN = "BEGIN ";

    /**
     * The end header.
     */
    private static final String END = "END ";

    /**
     * The certificateId.
     */
    private static final String PAIRCERTIFICATE = "CERTIFICATE";

    /**
     * The setCertificateId.
     */
    private static final String SETCERTIFICATE = "SETCERTIFICATE";

    /**
     * The PEM line length.
     */
    private static final int PEMLEN = 64;

    /**
     * is this a keySet?
     */
    private boolean isKeySet;

    /**
     * are we parsing a certificate?
     */
    private boolean inParsing;

    /**
     * Write certificate File.
     * @param pFile the writer
     * @param pCertificates the list of certificates
     * @throws OceanusException on error
     */
    public void writeKeyPairCertificates(final File pFile,
                                         final GordianKeyPairCertificate[] pCertificates) throws OceanusException {
        /* Write to file */
        isKeySet = false;
        writeCertificates(pFile, pCertificates);
    }

    /**
     * Write certificate File.
     * @param pFile the writer
     * @param pCertificates the list of certificates
     * @throws OceanusException on error
     */
    public void writeKeyPairSetCertificates(final File pFile,
                                            final GordianKeyPairSetCertificate[] pCertificates) throws OceanusException {
        /* Write to file */
        isKeySet = true;
        writeCertificates(pFile, pCertificates);
    }

    /**
     * Parse certificate File.
     * @param pFile the writer
     * @return the array of certificates
     * @throws OceanusException on error
     */
    GordianCertificate<?>[] parseCertificates(final File pFile) throws OceanusException {
        /* Parse the certificates */
        final List<byte[]> myCertBytes = parseCertificateList(pFile);

        /* Write to file */
        return isKeySet
                ? parseKeyPairSetCertificates(myCertBytes)
                : parseKeyPairCertificates(myCertBytes);
    }

    /**
     * Write certificate File.
     * @param pFile the writer
     * @param pCertificates the array of certificates
     * @throws OceanusException on error
     */
    private void writeCertificates(final File pFile,
                                   final GordianCertificate<?>[] pCertificates) throws OceanusException {
        /* Create the list */
        final List<byte[]> myList = new ArrayList<>();
        for (GordianCertificate<?> myCert : pCertificates) {
            myList.add(myCert.getEncoded());
        }

        /* Write to file */
        writeCertificates(pFile, myList);
    }

    /**
     * Parse keyPairCertificates.
     * @param pCertificates the certificats
     * @return the array of certificates
     * @throws OceanusException on error
     */
    GordianKeyPairCertificate[] parseKeyPairCertificates(final List<byte[]> pCertificates) throws OceanusException {
        /* Parse the certificates */
        final int mySize = pCertificates.size();
        final GordianKeyPairCertificate[] myCerts = new GordianKeyPairCertificate[mySize];

        /* Create the list */
        for (int i = 0; i < mySize; i++) {
            myCerts[i] = new GordianCoreKeyPairCertificate(null, pCertificates.get(i));
        }

        /* Return the certificates */
        return myCerts;
    }

    /**
     * Parse keyPairCertificates.
     * @param pCertificates the certificats
     * @return the array of certificates
     * @throws OceanusException on error
     */
    GordianKeyPairSetCertificate[] parseKeyPairSetCertificates(final List<byte[]> pCertificates) throws OceanusException {
        /* Parse the certificates */
        final int mySize = pCertificates.size();
        final GordianKeyPairSetCertificate[] myCerts = new GordianKeyPairSetCertificate[mySize];

        /* Create the list */
        for (int i = 0; i < mySize; i++) {
            myCerts[i] = new GordianCoreKeyPairSetCertificate(null, pCertificates.get(i));
        }

        /* Return the certificates */
        return myCerts;
    }

    /**
     * Write certificate File.
     * @param pFile the writer
     * @param pCertificates the list of certificates
     * @throws OceanusException on error
     */
    void writeCertificates(final File pFile,
                           final List<byte[]> pCertificates) throws OceanusException {
        /* Protect against exceptions */
        try (OutputStream myStream = new FileOutputStream(pFile);
             OutputStreamWriter myOutputWriter = new OutputStreamWriter(myStream, StandardCharsets.UTF_8);
             BufferedWriter myWriter = new BufferedWriter(myOutputWriter)) {
            /* Write the certificates to the file */
            writeCertificates(myWriter, pCertificates);

            /* Catch exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to write file", e);
        }
    }

    /**
     * Parse certificate file.
     * @param pFile the writer
     * @return the list of certificates
     * @throws OceanusException on error
     */
    List<byte[]> parseCertificateList(final File pFile) throws OceanusException {
        /* Protect against exceptions */
        try (InputStream myStream = new FileInputStream(pFile);
             InputStreamReader myInputReader = new InputStreamReader(myStream, StandardCharsets.UTF_8);
             BufferedReader myReader = new BufferedReader(myInputReader)) {
            /* Parse the certificates from the file */
            return parseCertificates(myReader);

            /* Catch exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to parse file", e);
        }
    }

    /**
     * Write certificates.
     * @param pWriter the writer
     * @param pCertificates the list of certificates
     * @throws OceanusException on error
     */
    private void writeCertificates(final BufferedWriter pWriter,
                                   final List<byte[]> pCertificates) throws OceanusException {
        /* Determine header type */
        final String myType = isKeySet ? SETCERTIFICATE : PAIRCERTIFICATE;

        /* Protect against exceptions */
        try {
            /* Loop through the certificates */
            for (byte[] myCert : pCertificates) {
                /* Write the certificate header */
                pWriter.write(BRACKET);
                pWriter.write(BEGIN);
                pWriter.write(myType);
                pWriter.write(BRACKET);
                pWriter.write('\n');

                /* Access base64 data */
                final String myBase64 = TethysDataConverter.byteArrayToBase64(myCert);
                int myLen = myBase64.length();
                for (int i = 0; myLen > 0; i += PEMLEN, myLen -= PEMLEN) {
                    pWriter.write(myBase64, i, Math.min(myLen, PEMLEN));
                    pWriter.write('\n');
                }

                /* Write the certificate trailer */
                pWriter.write(BRACKET);
                pWriter.write(END);
                pWriter.write(myType);
                pWriter.write(BRACKET);
                pWriter.write('\n');
                pWriter.write('\n');
            }

            /* Catch exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to parse file", e);
        }
   }

    /**
     * Parse certificates.
     * @param pReader the reader
     * @return the list of certificates
     * @throws OceanusException on error
     */
    private List<byte[]> parseCertificates(final BufferedReader pReader) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Create variables */
            final List<byte[]> myCerts = new ArrayList<>();
            final StringBuilder myCurrent = new StringBuilder();

            /* Read the lines */
            for (;;) {
                /* Read next line */
                String myLine = pReader.readLine();
                if (myLine == null) {
                    break;
                }

                /* If the line is a start/end element */
                if (myLine.startsWith(BRACKET)) {
                    /* Process the boundary */
                    myLine = myLine.substring(BRACKET.length());
                    if (inParsing) {
                        processEndBoundary(myLine, myCurrent, myCerts);
                    } else {
                        processStartBoundary(myLine, myCerts.isEmpty());
                    }

                    /* else if we are parsing, add line to buffer */
                } else if (inParsing) {
                    myCurrent.append(myLine);
                }

                /* Ignore other lines */
            }

            /* Return the certificates */
            return myCerts;

            /* Catch exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to parse file", e);
        }
    }

    /**
     * Process the start boundary.
     * @param pBoundary the boundary
     * @param pFirst is this the first certificate
     * @throws OceanusException on error
     */
    private void processStartBoundary(final String pBoundary,
                                      final boolean pFirst) throws OceanusException {
        /* If this is not a begin boundary */
        if (!pBoundary.startsWith(BEGIN)) {
            throw new GordianDataException("Sequencing error");
        }

        /* Check object type */
        final String myLine = pBoundary.substring(BEGIN.length());
        final boolean isSet = getBoundaryType(myLine);
        if (pFirst) {
            isKeySet = isSet;
        } else if (isKeySet != isSet) {
            throw new GordianDataException("Mixed dataTypes");
        }

        /* Note the we are parsing */
        inParsing = true;
    }

    /**
     * Process the end boundary.
     * @param pBoundary the boundary
     * @param pCurrent the current item
     * @param pList the list of certificates
     * @throws OceanusException on error
     */
    private void processEndBoundary(final String pBoundary,
                                    final StringBuilder pCurrent,
                                    final List<byte[]> pList) throws OceanusException {
        /* If this is not an end boundary */
        if (!pBoundary.startsWith(END)) {
            throw new GordianDataException("Sequencing error");
        }

        /* Check object type */
        final String myLine = pBoundary.substring(END.length());
        final boolean isSet = getBoundaryType(myLine);
        if (isKeySet != isSet) {
            throw new GordianDataException("Mixed dataTypes");
        }

        /* Parse the data and add certificate to list */
        final String myData = pCurrent.toString();
        final byte[] myBytes = TethysDataConverter.base64ToByteArray(myData);
        pList.add(myBytes);
        inParsing = false;
    }

    /**
     * Obtain the boundary type.
     * @param pBoundary the boundary
     * @return keySetCertificate true/false
     * @throws OceanusException on error
     */
    private static boolean getBoundaryType(final String pBoundary) throws OceanusException {
        /* If this is a keyPairCertificate */
        if (pBoundary.equals(PAIRCERTIFICATE + BRACKET)) {
            return false;
        }

        /* If this is a keyPairSetCertificate */
        if (pBoundary.equals(SETCERTIFICATE + BRACKET)) {
            return true;
        }

        /* Unsupported object type */
        throw new GordianDataException("Unsupported object type");
    }
}
