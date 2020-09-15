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
package net.sourceforge.joceanus.jgordianknot.impl.core.keypairset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianConsumer;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignature;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyPairSet signer.
 */
public class GordianKeyPairSetSigner
        implements GordianConsumer {
    /**
     * The keyPairSetSpec.
     */
    private final GordianKeyPairSetSpec theSpec;

    /**
     * The signers.
     */
    private final List<GordianSignature> theSigners;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pKeyPairSetSpec the keyPairSetSpec
     * @throws OceanusException on error
     */
    GordianKeyPairSetSigner(final GordianKeyPairFactory pFactory,
                            final GordianKeyPairSetSpec pKeyPairSetSpec) throws OceanusException {
        /* Store parameters */
        theSpec = pKeyPairSetSpec;
        theSigners = new ArrayList<>();

        /* Create the signers */
        final GordianSignatureFactory myFactory = pFactory.getSignatureFactory();
        final Iterator<GordianKeyPairSpec> myIterator = theSpec.iterator();
        while (myIterator.hasNext()) {
            final GordianKeyPairSpec mySpec = myIterator.next();
            final GordianSignatureSpec mySignSpec = GordianSignatureSpec.defaultForKey(mySpec);
            theSigners.add(myFactory.createSigner(mySignSpec));
        }
    }

    /**
     * Obtain the keyPairSetSpec.
     * @return the Spec
     */
    public GordianKeyPairSetSpec getSpec() {
        return theSpec;
    }

    /**
     * Initialise for signature.
     * @param pKeyPairSet the keyPairSet
     * @throws OceanusException on error
     */
    public void initForSigning(final GordianKeyPairSet pKeyPairSet) throws OceanusException {
        /* Check the keyPairSet */
        checkKeySpec(pKeyPairSet);

        /* Initialise the signers */
        final GordianCoreKeyPairSet mySet = (GordianCoreKeyPairSet) pKeyPairSet;
        final Iterator<GordianKeyPair> myIterator = mySet.iterator();
        for (GordianSignature mySigner : theSigners) {
            final GordianKeyPair myPair = myIterator.next();
            mySigner.initForSigning(myPair);
        }
    }

    /**
     * Initialise for verify.
     * @param pKeyPairSet the keyPairSet
     * @throws OceanusException on error
     */
    public void initForVerify(final GordianKeyPairSet pKeyPairSet) throws OceanusException {
        /* Check the keyPairSet */
        checkKeySpec(pKeyPairSet);

        /* Initialise the signers */
        final GordianCoreKeyPairSet mySet = (GordianCoreKeyPairSet) pKeyPairSet;
        final Iterator<GordianKeyPair> myIterator = mySet.iterator();
        for (GordianSignature mySigner : theSigners) {
            final GordianKeyPair myPair = myIterator.next();
            mySigner.initForVerify(myPair);
        }
    }

    /**
     * check the keyPairSet.
     * @param pKeyPairSet the keyPairSet
     * @throws OceanusException on error
     */
    private void checkKeySpec(final GordianKeyPairSet pKeyPairSet) throws OceanusException {
        if (!theSpec.equals(pKeyPairSet.getKeyPairSetSpec())) {
            throw new GordianLogicException("Invalid keyPairSet for signer");
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

    /**
     * Complete the signature operation and return the signature bytes.
     * @return the signature
     * @throws OceanusException on error
     */
    public final byte[] sign() throws OceanusException {
        /* Create the signature */
        final GordianKeyPairSetSignASN1 myASN1 = new GordianKeyPairSetSignASN1(theSpec);

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
            myASN1.addSignature(mySign);
        }

        /* Return the signature */
        return myASN1.getEncodedSignature();
    }

    /**
     * Verify the signature against the supplied signature bytes.
     * @param pSignature the supplied signature
     * @return the signature
     * @throws OceanusException on error
     */
    public boolean verify(final byte[] pSignature) throws OceanusException {
        /* Parse the signature */
        final GordianKeyPairSetSignASN1 myASN1 = GordianKeyPairSetSignASN1.getInstance(pSignature);

        /* Loop through the signers */
        byte[] mySign = null;
        boolean isValid = true;
        final Iterator<byte[]> myIterator = myASN1.signIterator();
        for (GordianSignature mySigner : theSigners) {
            /* If we have a previous signature */
            if (mySign != null) {
                /* Process previous signature */
                mySigner.update(mySign);
            }

            /* Access next signature */
            mySign = myIterator.next();

            /* Verify using this signature */
            isValid = mySigner.verify(mySign) && isValid;
        }

        /* Return validity */
        return isValid;
    }
}
