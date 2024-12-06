/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.sign;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignature;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianCompositeKeyPair;
import net.sourceforge.joceanus.oceanus.OceanusException;

/**
 * KeyPairSet signer.
 */
public class GordianCompositeSigner
        implements GordianSignature {
    /**
     * The factory.
     */
    private final GordianSignatureFactory theFactory;

    /**
     * The SignatureSpec.
     */
    private final GordianSignatureSpec theSpec;

    /**
     * The signers.
     */
    private final List<GordianSignature> theSigners;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSignatureSpec the signatureSpec
     * @throws OceanusException on error
     */
    public GordianCompositeSigner(final GordianFactory pFactory,
                                  final GordianSignatureSpec pSignatureSpec) throws OceanusException {
        /* Store parameters */
        theFactory = pFactory.getKeyPairFactory().getSignatureFactory();
        theSpec = pSignatureSpec;
        theSigners = new ArrayList<>();

        /* Create the signers */
        final Iterator<GordianSignatureSpec> myIterator = theSpec.signatureSpecIterator();
        while (myIterator.hasNext()) {
            final GordianSignatureSpec mySpec = myIterator.next();
            theSigners.add(theFactory.createSigner(mySpec));
        }
    }

    @Override
    public GordianSignatureSpec getSignatureSpec() {
        return theSpec;
    }

    @Override
    public void initForSigning(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Check the keyPairSet */
        checkKeySpec(pKeyPair);

        /* Initialise the signers */
        final GordianCompositeKeyPair myCompositePair = (GordianCompositeKeyPair) pKeyPair;
        final Iterator<GordianKeyPair> myIterator = myCompositePair.iterator();
        for (GordianSignature mySigner : theSigners) {
            final GordianKeyPair myPair = myIterator.next();
            mySigner.initForSigning(myPair);
        }
    }

    @Override
    public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Check the keyPairSet */
        checkKeySpec(pKeyPair);

        /* Initialise the signers */
        final GordianCompositeKeyPair myCompositePair = (GordianCompositeKeyPair) pKeyPair;
        final Iterator<GordianKeyPair> myIterator = myCompositePair.iterator();
        for (GordianSignature mySigner : theSigners) {
            final GordianKeyPair myPair = myIterator.next();
            mySigner.initForVerify(myPair);
        }
    }

    /**
     * check the keyPair.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    private void checkKeySpec(final GordianKeyPair pKeyPair) throws OceanusException {
        if (!theFactory.validSignatureSpecForKeyPairSpec(pKeyPair.getKeyPairSpec(), theSpec)) {
            throw new GordianLogicException("Invalid keyPair for signer");
        }
    }

    @Override
    public void update(final byte[] pBytes,
                       final int pOffset,
                       final int pLength) {
        /* Loop through the signers */
        for (GordianSignature mySigner : theSigners) {
            mySigner.update(pBytes, pOffset, pLength);
        }
    }

    @Override
    public void update(final byte pByte) {
        /* Loop through the signers */
        for (GordianSignature mySigner : theSigners) {
            mySigner.update(pByte);
        }
    }

    @Override
    public void reset() {
        /* Loop through the signers */
        for (GordianSignature mySigner : theSigners) {
            mySigner.reset();
        }
    }

    @Override
    public final byte[] sign() throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Create the signature */
            final ASN1EncodableVector ks = new ASN1EncodableVector();

            /* Loop through the signers */
            byte[] mySign = null;
            for (GordianSignature mySigner : theSigners) {
                /* If we have a previous signature */
                if (mySign != null) {
                    /* Process previous signature */
                    mySigner.update(mySign);
                }

                /* Sign using this signature */
                mySign = mySigner.sign();

                /* Add the signature */
                ks.add(new DEROctetString(mySign));
            }

            /* Return the signature */
            return new DERSequence(ks).getEncoded();

        } catch (IOException e) {
            throw new GordianIOException("Failed to encode signature", e);
        }
    }

    @Override
    public boolean verify(final byte[] pSignature) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Parse the signature */
            final ASN1Sequence mySequence = ASN1Sequence.getInstance(pSignature);

            /* Loop through the signers */
            byte[] mySign = null;
            int numFailed = 0;
            final Enumeration<?> en = mySequence.getObjects();
            for (GordianSignature mySigner : theSigners) {
                /* If we have a previous signature */
                if (mySign != null) {
                    /* Process previous signature */
                    mySigner.update(mySign);
                }

                /* Access next signature */
                mySign = ASN1OctetString.getInstance(en.nextElement()).getOctets();

                /* Verify using this signature */
                numFailed += mySigner.verify(mySign) ? 0 : 1;
            }

            /* Return validity */
            return numFailed == 0;

        } catch (IllegalArgumentException e) {
            throw new GordianIOException("Invalid encoded signature", e);
        }
    }
}
