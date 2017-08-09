/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto.stream;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipher;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMac;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianPadding;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeyType;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
     * @throws OceanusException on error
     */
    public List<GordianStreamDefinition> analyseStreams(final OutputStream pStream) throws OceanusException {
        /* Allocate the list */
        final List<GordianStreamDefinition> myStreams = new ArrayList<>();

        /* Loop through the streams */
        OutputStream myStream = pStream;
        for (;;) {
            /* If this is a Digest Output Stream */
            if (myStream instanceof GordianDigestOutputStream) {
                /* Process stream */
                final GordianDigestOutputStream myDigest = (GordianDigestOutputStream) myStream;
                myStreams.add(0, new GordianStreamDefinition(theKeySet, myDigest));
                myStream = myDigest.getNextStream();

                /* If this is a MAC Output Stream */
            } else if (myStream instanceof GordianMacOutputStream) {
                /* Process stream */
                final GordianMacOutputStream myMac = (GordianMacOutputStream) myStream;
                myStreams.add(0, new GordianStreamDefinition(theKeySet, myMac));
                myStream = myMac.getNextStream();

                /* If this is a LZMA Output Stream */
            } else if (myStream instanceof GordianLZMAOutputStream) {
                /* Process stream */
                final GordianLZMAOutputStream myLZMA = (GordianLZMAOutputStream) myStream;
                myStreams.add(0, new GordianStreamDefinition(myLZMA));
                myStream = myLZMA.getNextStream();

                /* If this is a Encryption Output Stream */
            } else if (myStream instanceof GordianCipherOutputStream) {
                /* Process stream */
                final GordianCipherOutputStream<?> myEnc = (GordianCipherOutputStream<?>) myStream;
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
     * @throws OceanusException on error
     */
    public InputStream buildInputStream(final List<GordianStreamDefinition> pStreamDefs,
                                        final InputStream pBaseStream) throws OceanusException {
        /* Loop through the stream definitions */
        InputStream myCurrent = pBaseStream;
        final Iterator<GordianStreamDefinition> myIterator = pStreamDefs.iterator();
        while (myIterator.hasNext()) {
            final GordianStreamDefinition myDef = myIterator.next();

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
     * @throws OceanusException on error
     */
    public OutputStream buildOutputStream(final OutputStream pBaseStream) throws OceanusException {
        /* Loop through the stream definitions */
        OutputStream myCurrent = pBaseStream;

        /* Access factory and bump the random engine */
        final GordianFactory myFactory = theKeySet.getFactory();

        /* Create an initial MAC stream */
        final GordianMac myMac = myFactory.generateRandomMac();
        myCurrent = new GordianMacOutputStream(myMac, myCurrent);

        /* Generate a list of encryption types */
        final List<GordianKey<GordianSymKeyType>> mySymKeys = myFactory.generateRandomSymKeyList();
        boolean bFirst = true;

        /* For each encryption key */
        final Iterator<GordianKey<GordianSymKeyType>> myIterator = mySymKeys.iterator();
        while (myIterator.hasNext()) {
            final GordianKey<GordianSymKeyType> myKey = myIterator.next();
            final boolean bLast = !myIterator.hasNext();

            /* Determine mode and padding */
            GordianPadding myPadding = GordianPadding.NONE;
            if (!bFirst) {
                if (bLast) {
                    myPadding = GordianPadding.ISO7816D4;
                } else if (!myKey.getKeyType().isStdBlock()) {
                    myPadding = GordianPadding.CTS;
                }
            }
            final GordianSymCipherSpec mySpec = bFirst
                                                       ? GordianSymCipherSpec.sic(myKey.getKeyType())
                                                       : GordianSymCipherSpec.ecb(myKey.getKeyType(), myPadding);

            /* Build the cipher stream */
            final GordianCipher<GordianSymKeyType> mySymCipher = myFactory.createSymKeyCipher(mySpec);
            mySymCipher.initCipher(myKey);
            myCurrent = new GordianCipherOutputStream<GordianSymKeyType>(mySymCipher, myCurrent);

            /* Note that this is no longer the first */
            bFirst = false;
        }

        /* Create the encryption stream for a stream key */
        final GordianKey<GordianStreamKeyType> myStreamKey = myFactory.generateRandomStreamKey();
        final GordianCipher<GordianStreamKeyType> myStreamCipher = myFactory.createStreamKeyCipher(GordianStreamCipherSpec.stream(myStreamKey.getKeyType()));
        myStreamCipher.initCipher(myStreamKey);
        myCurrent = new GordianCipherOutputStream<GordianStreamKeyType>(myStreamCipher, myCurrent);

        /* Attach an LZMA output stream onto the output */
        myCurrent = new GordianLZMAOutputStream(myCurrent);

        /* Create a digest stream */
        final GordianDigest myDigest = myFactory.generateRandomDigest();
        myCurrent = new GordianDigestOutputStream(myDigest, myCurrent);

        /* Return the stream */
        return myCurrent;
    }
}
