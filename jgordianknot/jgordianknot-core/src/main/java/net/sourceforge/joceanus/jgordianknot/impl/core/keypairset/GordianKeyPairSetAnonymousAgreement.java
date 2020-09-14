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

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyPairSet signedAgreement.
 */
public class GordianKeyPairSetAnonymousAgreement
        extends GordianCoreAgreement {
    /**
     * The list of agreements.
     */
    private final List<GordianAnonymousAgreement> theAgreements;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     * @throws OceanusException on error
     */
    GordianKeyPairSetAnonymousAgreement(final GordianCoreFactory pFactory,
                                        final GordianAgreementSpec pSpec) throws OceanusException {
        /* Initialise underlying class */
        super(pFactory, pSpec);
        theAgreements = new ArrayList<>();

        /* Create the agreements */
        final GordianAgreementFactory myFactory = pFactory.getAsymmetricFactory().getAgreementFactory();
        final GordianKDFType myKDFType = pSpec.getKDFType();
        theAgreements.add((GordianAnonymousAgreement) myFactory.createAgreement(
                GordianAgreementSpec.dhAnon(myKDFType)));
        theAgreements.add((GordianAnonymousAgreement) myFactory.createAgreement(
                GordianAgreementSpec.ecdhAnon(GordianAsymKeyType.EC, myKDFType)));
        theAgreements.add((GordianAnonymousAgreement) myFactory.createAgreement(
                GordianAgreementSpec.xdhAnon(myKDFType)));
    }

    /**
     * Create the clientHello message.
     * @param pKeyPairSet the target keyPairSet
     * @return the clientHello message
     * @throws OceanusException on error
     */
    public byte[] createClientHello(final GordianKeyPairSet pKeyPairSet) throws OceanusException {
        /* Check valid spec */
        final GordianKeyPairSetSpec mySpec = pKeyPairSet.getKeyPairSetSpec();
        if (!mySpec.canAgree()) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(mySpec));
        }

        /* Create the message */
        final AlgorithmIdentifier myResId = getIdentifierForResult();
        final GordianKeyPairSetAgreeASN1 myASN1 = new GordianKeyPairSetAgreeASN1(mySpec, myResId);

        /* Create the result */
        final int myPartLen = GordianLength.LEN_512.getByteLength();
        final byte[] myResult = new byte[mySpec.numKeyPairs() * myPartLen];
        int myOffset = 0;

        /* Loop through the agreements */
        final Iterator<GordianKeyPair> myIterator = ((GordianCoreKeyPairSet) pKeyPairSet).iterator();
        for (GordianAnonymousAgreement myAgreement : theAgreements) {
            myASN1.addMessage(myAgreement.createClientHello(myIterator.next()));
            final byte[] myPart = (byte[]) myAgreement.getResult();
            System.arraycopy(myPart, 0, myResult, myOffset, myPartLen);
            myOffset += myPartLen;
        }

        /* Store the secret */
        storeSecret(myResult);

        /* Return the combined clientHello */
        return myASN1.getEncodedMessage();
    }

    /**
     * Accept the clientHello.
     * @param pServer the server keyPairSet
     * @param pClientHello the incoming clientHello message
     * @throws OceanusException on error
     */
    public void acceptClientHello(final GordianKeyPairSet pServer,
                                  final byte[] pClientHello)  throws OceanusException {
        /* Parse the clientHello */
        final GordianKeyPairSetAgreeASN1 myHello = GordianKeyPairSetAgreeASN1.getInstance(pClientHello);
        final AlgorithmIdentifier myResId = myHello.getResultId();

        /* Process result identifier */
        processResultIdentifier(myResId);

        /* Create the result */
        final GordianKeyPairSetSpec mySpec = myHello.getSpec();
        final int myPartLen = GordianLength.LEN_512.getByteLength();
        final byte[] myResult = new byte[mySpec.numKeyPairs() * myPartLen];
        int myOffset = 0;

        /* Loop through the agreements */
        final GordianCoreKeyPairSet mySet = (GordianCoreKeyPairSet) pServer;
        final Iterator<GordianKeyPair> myPairIterator = mySet.iterator();
        final Iterator<byte[]> myHelloIterator = myHello.msgIterator();
        for (GordianAnonymousAgreement myAgreement : theAgreements) {
            myAgreement.acceptClientHello(myPairIterator.next(), myHelloIterator.next());
            final byte[] myPart = (byte[]) myAgreement.getResult();
            System.arraycopy(myPart, 0, myResult, myOffset, myPartLen);
            myOffset += myPartLen;
        }

        /* Store the secret */
        storeSecret(myResult);
    }
}
