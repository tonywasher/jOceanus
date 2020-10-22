/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.impl.core.keystore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianPEMObject.GordianPEMObjectType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * PEM Parser.
 */
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
     * The newLine.
     */
    private static final char NEWLINE = '\n';

    /**
     * The PEM line length.
     */
    private static final int PEMLEN = 64;

    /**
     * the active ObjectType.
     */
    private GordianPEMObjectType theObjectType;

    /**
     * Write PEM objects to stream.
     * @param pStream the stream
     * @param pObjects the list of objects
     * @throws OceanusException on error
     */
    void writePEMFile(final OutputStream pStream,
                      final List<GordianPEMObject> pObjects) throws OceanusException {
        /* Protect against exceptions */
        try (OutputStreamWriter myOutputWriter = new OutputStreamWriter(pStream, StandardCharsets.UTF_8);
             BufferedWriter myWriter = new BufferedWriter(myOutputWriter)) {
            /* Write the objects to the file */
            writeObjects(myWriter, pObjects);

            /* Catch exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to write to stream", e);
        }
    }

    /**
     * Parse PEM Object stream.
     * @param pStream the stream
     * @return the list of parsed objects
     * @throws OceanusException on error
     */
    List<GordianPEMObject> parsePEMFile(final InputStream pStream) throws OceanusException {
        /* Protect against exceptions */
        try (InputStreamReader myInputReader = new InputStreamReader(pStream, StandardCharsets.UTF_8);
             BufferedReader myReader = new BufferedReader(myInputReader)) {
            /* Parse the objects from the file */
            return parseObjects(myReader);

            /* Catch exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to process stream", e);
        }
    }

    /**
     * Write PEM Objects.
     * @param pWriter the writer
     * @param pObjects the list of objects
     * @throws OceanusException on error
     */
    private void writeObjects(final BufferedWriter pWriter,
                              final List<GordianPEMObject> pObjects) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Loop through the objects */
            for (GordianPEMObject myObject : pObjects) {
                /* Determine header type */
                final String myType = myObject.getObjectType().getId();

                /* Write the object header */
                pWriter.write(BRACKET);
                pWriter.write(BEGIN);
                pWriter.write(myType);
                pWriter.write(BRACKET);
                pWriter.write(NEWLINE);

                /* Access base64 data */
                final String myBase64 = TethysDataConverter.byteArrayToBase64(myObject.getEncoded());
                int myLen = myBase64.length();
                for (int i = 0; myLen > 0; i += PEMLEN, myLen -= PEMLEN) {
                    pWriter.write(myBase64, i, Math.min(myLen, PEMLEN));
                    pWriter.write(NEWLINE);
                }

                /* Write the object trailer */
                pWriter.write(BRACKET);
                pWriter.write(END);
                pWriter.write(myType);
                pWriter.write(BRACKET);
                pWriter.write(NEWLINE);
                pWriter.write(NEWLINE);
            }

            /* Catch exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to write to stream", e);
        }
   }

    /**
     * Parse certificates.
     * @param pReader the reader
     * @return the list of objects
     * @throws OceanusException on error
     */
    private List<GordianPEMObject> parseObjects(final BufferedReader pReader) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Create variables */
            final List<GordianPEMObject> myObjects = new ArrayList<>();
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
                    if (theObjectType != null) {
                        processEndBoundary(myLine, myCurrent, myObjects);
                    } else {
                        processStartBoundary(myLine);
                    }

                    /* else if we are parsing, add line to buffer */
                } else if (theObjectType != null) {
                    myCurrent.append(myLine);
                }

                /* Ignore other lines */
            }

            /* Return the objects */
            return myObjects;

            /* Catch exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to parse stream", e);
        }
    }

    /**
     * Process the start boundary.
     * @param pBoundary the boundary
     * @throws OceanusException on error
     */
    private void processStartBoundary(final String pBoundary) throws OceanusException {
        /* If this is not a begin boundary */
        if (!pBoundary.startsWith(BEGIN)) {
            throw new GordianDataException("Sequencing error");
        }

        /* Check object type */
        final String myLine = pBoundary.substring(BEGIN.length());
        theObjectType = GordianPEMObjectType.getObjectType(myLine);
    }

    /**
     * Process the end boundary.
     * @param pBoundary the boundary
     * @param pCurrent the current item
     * @param pList the list of parsed objects
     * @throws OceanusException on error
     */
    private void processEndBoundary(final String pBoundary,
                                    final StringBuilder pCurrent,
                                    final List<GordianPEMObject> pList) throws OceanusException {
        /* If this is not an end boundary */
        if (!pBoundary.startsWith(END)) {
            throw new GordianDataException("Sequencing error");
        }

        /* Check object type */
        final String myLine = pBoundary.substring(END.length());
        final GordianPEMObjectType myType = GordianPEMObjectType.getObjectType(myLine);
        if (theObjectType != myType) {
            throw new GordianDataException("Mixed dataTypes");
        }

        /* Parse the data and add certificate to list */
        final String myData = pCurrent.toString();
        final byte[] myBytes = TethysDataConverter.base64ToByteArray(myData);
        pList.add(new GordianPEMObject(theObjectType, myBytes));
        theObjectType = null;
        pCurrent.setLength(0);
    }
}
