/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.stream;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacParameters;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.digest.GordianCoreDigestFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIdManager;
import net.sourceforge.joceanus.jgordianknot.impl.core.mac.GordianCoreMacFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stream Factory.
 */
public final class GordianStreamManager {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(GordianStreamManager.class);

    /**
     * Close error.
     */
    private static final String ERROR_CLOSE = "Failed to close stream";

    /**
     * The keySet.
     */
    private final GordianCoreKeySet theKeySet;

    /**
     * Constructor.
     * @param pKeySet the keySet
     */
    public GordianStreamManager(final GordianCoreKeySet pKeySet) {
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
        GordianMacInputStream myMacStream = null;
        final Iterator<GordianStreamDefinition> myIterator = pStreamDefs.iterator();
        while (myIterator.hasNext()) {
            final GordianStreamDefinition myDef = myIterator.next();

            /* Build the stream */
            myCurrent = myDef.buildInputStream(theKeySet, myCurrent, myMacStream);
            if (myCurrent instanceof GordianMacInputStream) {
                myMacStream = (GordianMacInputStream) myCurrent;
            }
        }

        /* Return the stream */
        return myCurrent;
    }

    /**
     * Build output stream.
     * @param pBaseStream the base output stream
     * @param pCompress should we compress this file?
     * @return the new output stream
     * @throws OceanusException on error
     */
    public OutputStream buildOutputStream(final OutputStream pBaseStream,
                                          final boolean pCompress) throws OceanusException {
        /* Loop through the stream definitions */
        OutputStream myCurrent = pBaseStream;

        /* Access factory and bump the random engine */
        final GordianCoreFactory myFactory = theKeySet.getFactory();
        final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) myFactory.getCipherFactory();
        final GordianCoreMacFactory myMacs = (GordianCoreMacFactory) myFactory.getMacFactory();
        final GordianIdManager myIdMgr = myFactory.getIdManager();

        /* Create an initial MAC stream */
        final GordianMacSpec myMacSpec = myIdMgr.generateRandomMacSpec(theKeySet.getKeySetSpec().getKeyLength(), true);

        /* Determine a random key */
        final GordianKeyGenerator<GordianMacSpec> myGenerator = myMacs.getKeyGenerator(myMacSpec);
        final GordianKey<GordianMacSpec> myMacKey = myGenerator.generateKey();

        /* Create and initialise the MAC */
        final GordianMac myMac = myMacs.createMac(myMacSpec);
        myMac.init(GordianMacParameters.keyWithRandomNonce(myMacKey));
        final GordianMacOutputStream myMacStream = new GordianMacOutputStream(myMac, myCurrent);
        myCurrent = myMacStream;

        /* Generate a list of encryption types */
        final List<GordianKey<GordianSymKeySpec>> mySymKeys = generateRandomSymKeyList();
        boolean bFirst = true;

        /* For each encryption key */
        final Iterator<GordianKey<GordianSymKeySpec>> myIterator = mySymKeys.iterator();
        while (myIterator.hasNext()) {
            final GordianKey<GordianSymKeySpec> myKey = myIterator.next();
            final boolean bLast = !myIterator.hasNext();

            /* Determine mode and padding */
            GordianPadding myPadding = GordianPadding.NONE;
            if (!bFirst
                    && (bLast || myKey.getKeyType().getBlockLength() != GordianLength.LEN_128)) {
                myPadding = GordianPadding.ISO7816D4;
            }
            final GordianSymCipherSpec mySymSpec = bFirst
                                                    ? GordianSymCipherSpec.sic(myKey.getKeyType())
                                                    : GordianSymCipherSpec.ecb(myKey.getKeyType(), myPadding);

            /* Build the cipher stream */
            final GordianSymCipher mySymCipher = myCiphers.createSymKeyCipher(mySymSpec);
            mySymCipher.initForEncrypt(GordianCipherParameters.keyWithRandomNonce(myKey));
            myCurrent = new GordianCipherOutputStream<>(mySymCipher, myCurrent);

            /* Note that this is no longer the first */
            bFirst = false;
        }

        /* Create the encryption stream for a stream key */
        final GordianLength myKeyLen = theKeySet.getKeySetSpec().getKeyLength();
        final GordianStreamKeySpec myStreamKeySpec = myIdMgr.generateRandomStreamKeySpec(myKeyLen, true);
        final GordianKeyGenerator<GordianStreamKeySpec> myStreamGenerator = myCiphers.getKeyGenerator(myStreamKeySpec);
        final GordianKey<GordianStreamKeySpec> myStreamKey = myStreamGenerator.generateKey();
        final GordianStreamCipher myStreamCipher = myCiphers.createStreamKeyCipher(GordianStreamCipherSpec.stream(myStreamKey.getKeyType()));
        myStreamCipher.initForEncrypt(GordianCipherParameters.keyWithRandomNonce(myStreamKey));
        myCurrent = new GordianCipherOutputStream<>(myStreamCipher, myCurrent);

        /* If we are compressing */
        if (pCompress) {
            /* Attach an LZMA output stream onto the output */
            myCurrent = new GordianLZMAOutputStream(myCurrent);
        }

        /* Create a digest stream */
        final GordianDigest myDigest = generateRandomDigest();
        myCurrent = new GordianDigestOutputStream(myDigest, myCurrent, myMacStream);

        /* Return the stream */
        return myCurrent;
    }

    /**
     * generate random GordianDigest.
     * @return the new Digest
     * @throws OceanusException on error
     */
    private GordianDigest generateRandomDigest() throws OceanusException {
        /* Access factory */
        final GordianCoreFactory myFactory = theKeySet.getFactory();
        final GordianCoreDigestFactory myDigests = (GordianCoreDigestFactory) myFactory.getDigestFactory();
        final GordianIdManager myIdMgr = myFactory.getIdManager();

        /* Generate the random digest */
        final GordianDigestSpec mySpec = myIdMgr.generateRandomDigestSpec(true);
        return myDigests.createDigest(mySpec);
    }

    /**
     * generate random SymKeyList.
     * @return the list of keys
     * @throws OceanusException on error
     */
    public List<GordianKey<GordianSymKeySpec>> generateRandomSymKeyList() throws OceanusException {
        /* Access factory */
        final GordianCoreFactory myFactory = theKeySet.getFactory();
        final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) myFactory.getCipherFactory();
        final GordianIdManager myIdMgr = myFactory.getIdManager();

        /* Determine a random set of keyType */
        final int myCount = theKeySet.getKeySetSpec().getCipherSteps() - 1;
        final GordianLength myKeyLen = theKeySet.getKeySetSpec().getKeyLength();
        final GordianSymKeySpec[] mySpecs = myIdMgr.generateRandomKeySetSymKeySpecs(myKeyLen, myCount);

        /* Loop through the keys */
        final List<GordianKey<GordianSymKeySpec>> myKeyList = new ArrayList<>();
        for (int i = 0; i < myCount; i++) {
            /* Generate a random key */
            final GordianKeyGenerator<GordianSymKeySpec> myGenerator = myCiphers.getKeyGenerator(mySpecs[0]);
            myKeyList.add(myGenerator.generateKey());
        }

        /* Return the list */
        return myKeyList;
    }

    /**
     * Close an inputStream on error exit.
     * @param pStream the file to delete
     */
    public static void cleanUpInputStream(final InputStream pStream) {
        try {
            pStream.close();
        } catch (IOException e) {
            LOGGER.error(ERROR_CLOSE, e);
        }
    }

    /**
     * Close an outputStream on error exit.
     * @param pStream the file to delete
     */
    public static void cleanUpOutputStream(final OutputStream pStream) {
        try {
            pStream.close();
        } catch (IOException e) {
            LOGGER.error(ERROR_CLOSE, e);
        }
    }
}
