/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jgordianknot/jgordianknot-core/src/main/java/net/sourceforge/joceanus/jgordianknot/crypto/MacType.java $
 * $Revision: 647 $
 * $Author: Tony $
 * $Date: 2015-11-04 08:58:02 +0000 (Wed, 04 Nov 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto.stream;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipher;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherMode;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMac;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeyType;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Stream Manager.
 */
public final class GordianStreamManager {
    /**
     * The keySet.
     */
    private GordianKeySet theKeySet;

    /**
     * Constructor.
     * @param pKeySet the keySet
     */
    public GordianStreamManager(final GordianKeySet pKeySet) {
        theKeySet = pKeySet;
    }

    /**
     * Analyse output stream.
     * @param pStream the output stream
     * @return the Stream definition list
     * @throws JOceanusException on error
     */
    public List<GordianStreamDefinition> analyseStreams(final OutputStream pStream) throws JOceanusException {
        /* Allocate the list */
        List<GordianStreamDefinition> myStreams = new ArrayList<GordianStreamDefinition>();

        /* Loop through the streams */
        OutputStream myStream = pStream;
        for (;;) {
            /* If this is a Digest Output Stream */
            if (myStream instanceof GordianDigestOutputStream) {
                /* Process stream */
                GordianDigestOutputStream myDigest = (GordianDigestOutputStream) myStream;
                myStreams.add(0, new GordianStreamDefinition(theKeySet, myDigest));
                myStream = myDigest.getNextStream();

                /* If this is a MAC Output Stream */
            } else if (myStream instanceof GordianMacOutputStream) {
                /* Process stream */
                GordianMacOutputStream myMac = (GordianMacOutputStream) myStream;
                myStreams.add(0, new GordianStreamDefinition(theKeySet, myMac));
                myStream = myMac.getNextStream();

                /* If this is a LZMA Output Stream */
            } else if (myStream instanceof GordianLZMAOutputStream) {
                /* Process stream */
                GordianLZMAOutputStream myLZMA = (GordianLZMAOutputStream) myStream;
                myStreams.add(0, new GordianStreamDefinition(myLZMA));
                myStream = myLZMA.getNextStream();

                /* If this is a Encryption Output Stream */
            } else if (myStream instanceof GordianCipherOutputStream) {
                /* Process stream */
                GordianCipherOutputStream<?> myEnc = (GordianCipherOutputStream<?>) myStream;
                myStreams.add(0, new GordianStreamDefinition(theKeySet, myEnc));
                myStream = myEnc.getNextStream();

                /* Else stop loop */
            } else {
                break;
            }
        }

        /* Return the list */
        return myStreams;
    }

    /**
     * Build input stream.
     * @param pStreamDefs the list of stream definitions
     * @param pBaseStream the base input stream
     * @return the new input stream
     * @throws JOceanusException on error
     */
    public InputStream buildInputStream(final List<GordianStreamDefinition> pStreamDefs,
                                        final InputStream pBaseStream) throws JOceanusException {
        /* Loop through the stream definitions */
        InputStream myCurrent = pBaseStream;
        Iterator<GordianStreamDefinition> myIterator = pStreamDefs.iterator();
        while (myIterator.hasNext()) {
            GordianStreamDefinition myDef = myIterator.next();

            /* Build the stream */
            myCurrent = myDef.buildInputStream(theKeySet, myCurrent);
        }

        /* Return the stream */
        return myCurrent;
    }

    /**
     * Build output stream.
     * @param pBaseStream the base output stream
     * @return the new output stream
     * @throws JOceanusException on error
     */
    public OutputStream buildOutputStream(final OutputStream pBaseStream) throws JOceanusException {
        /* Loop through the stream definitions */
        OutputStream myCurrent = pBaseStream;

        /* Access factory and bump the random engine */
        GordianFactory myFactory = theKeySet.getFactory();

        /* Create an initial MAC stream */
        GordianMac myMac = myFactory.generateRandomMac();
        myCurrent = new GordianMacOutputStream(myMac, myCurrent);

        /* Generate a list of encryption types */
        List<GordianKey<GordianSymKeyType>> mySymKeys = myFactory.generateRandomSymKeyList();
        int myEncryption = mySymKeys.size();
        boolean doCBC = myEncryption > 1;
        boolean doCFB = myEncryption > 2;

        /* For each encryption key */
        Iterator<GordianKey<GordianSymKeyType>> myIterator = mySymKeys.iterator();
        while (myIterator.hasNext()) {
            GordianKey<GordianSymKeyType> myKey = myIterator.next();

            /* Determine Mode */
            myEncryption--;
            GordianCipherMode myMode = GordianCipherMode.SIC;
            if (doCBC && myEncryption == 0) {
                myMode = GordianCipherMode.CBC;
            } else if (doCFB && myEncryption == 1) {
                myMode = GordianCipherMode.CFB;
            }

            /* Determine padding */
            boolean isPadded = myMode.equals(GordianCipherMode.CBC);

            /* Build the cipher stream */
            GordianCipher<GordianSymKeyType> mySymCipher = myFactory.createSymKeyCipher(myKey.getKeyType(), myMode, isPadded);
            mySymCipher.initCipher(myKey);
            myCurrent = new GordianCipherOutputStream<GordianSymKeyType>(mySymCipher, myCurrent);
        }

        /* Create the encryption stream for a stream key */
        GordianKey<GordianStreamKeyType> myStreamKey = myFactory.generateRandomStreamKey();
        GordianCipher<GordianStreamKeyType> myStreamCipher = myFactory.createStreamKeyCipher(myStreamKey.getKeyType());
        myStreamCipher.initCipher(myStreamKey);
        myCurrent = new GordianCipherOutputStream<GordianStreamKeyType>(myStreamCipher, myCurrent);

        /* Attach an LZMA output stream onto the output */
        myCurrent = new GordianLZMAOutputStream(myCurrent);

        /* Create a digest stream */
        GordianDigest myDigest = myFactory.generateRandomDigest();
        myCurrent = new GordianDigestOutputStream(myDigest, myCurrent);

        /* Return the stream */
        return myCurrent;
    }
}
