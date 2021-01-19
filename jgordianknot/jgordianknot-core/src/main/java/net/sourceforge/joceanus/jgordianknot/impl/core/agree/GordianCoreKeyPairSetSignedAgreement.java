/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.agree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementStatus;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairSetAgreement;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairSetAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairSetSignedAgreement;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairSignedAgreement;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypairset.GordianCoreKeyPairSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyPairSet signedAgreement.
 */
public class GordianCoreKeyPairSetSignedAgreement
    extends GordianCoreAgreement<GordianKeyPairSetAgreementSpec>
    implements GordianKeyPairSetSignedAgreement, GordianKeyPairSetAgreement {
    /**
     * The list of agreements.
     */
    private final List<GordianKeyPairSignedAgreement> theAgreements;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     * @throws OceanusException on error
     */
    GordianCoreKeyPairSetSignedAgreement(final GordianCoreFactory pFactory,
                                         final GordianKeyPairSetAgreementSpec pSpec) throws OceanusException {
        /* Initialise underlying class */
        super(pFactory, pSpec);
        theAgreements = new ArrayList<>();

        /* Create the agreements */
        final GordianAgreementFactory myFactory = pFactory.getKeyPairFactory().getAgreementFactory();
        final GordianKDFType myKDFType = pSpec.getKDFType();
        theAgreements.add((GordianKeyPairSignedAgreement) myFactory.createKeyPairAgreement(
                GordianKeyPairAgreementSpec.dhSigned(myKDFType)));
        theAgreements.add((GordianKeyPairSignedAgreement) myFactory.createKeyPairAgreement(
                GordianKeyPairAgreementSpec.ecdhSigned(GordianKeyPairType.EC, myKDFType)));
        theAgreements.add((GordianKeyPairSignedAgreement) myFactory.createKeyPairAgreement(
                GordianKeyPairAgreementSpec.xdhSigned(myKDFType)));
    }

    @Override
    public byte[] createClientHello(final GordianKeyPairSetSpec pKeyPairSetSpec) throws OceanusException {
        /* Must be in clean state */
        checkStatus(GordianAgreementStatus.CLEAN);

        /* Check valid spec */
        if (!getAgreementSpec().getKeyPairSetSpec().equals(pKeyPairSetSpec)) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeyPairSetSpec));
        }

        /* Create the message */
        final AlgorithmIdentifier myResId = getIdentifierForResult();
        final GordianKeyPairSetAgreeASN1 myASN1 = new GordianKeyPairSetAgreeASN1(getAgreementSpec(), myResId);

        /* Loop through the agreements */
        final Iterator<GordianKeyPairSpec> myIterator = pKeyPairSetSpec.iterator();
        for (GordianKeyPairSignedAgreement myAgreement : theAgreements) {
            /* create the clientHello and add to message */
            myASN1.addMessage(myAgreement.createClientHello(myIterator.next()));
        }

        /* Set status */
        setStatus(GordianAgreementStatus.AWAITING_SERVERHELLO);

        /* Return the combined clientHello */
        return myASN1.getEncodedMessage();
    }

    @Override
    public byte[] acceptClientHello(final GordianKeyPairSet pServer,
                                    final byte[] pClientHello)  throws OceanusException {
        /* Must be in clean state */
        checkStatus(GordianAgreementStatus.CLEAN);

        /* Check valid spec */
        if (!pServer.getKeyPairSetSpec().canSign()) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pServer.getKeyPairSetSpec()));
        }

        /* Parse the clientHello */
        final GordianKeyPairSetAgreeASN1 myHello = GordianKeyPairSetAgreeASN1.getInstance(pClientHello);
        final AlgorithmIdentifier myResId = myHello.getResultId();
        if (!Objects.equals(getAgreementSpec(), myHello.getSpec())) {
            throw new GordianDataException(ERROR_INVSPEC);
        }

        /* Process result identifier */
        processResultIdentifier(myResId);

        /* Create the result */
        final GordianKeyPairSetAgreementSpec mySpec = myHello.getSpec();
        final int myPartLen = GordianLength.LEN_512.getByteLength();
        final byte[] myResult = new byte[mySpec.getKeyPairSetSpec().numKeyPairs() * myPartLen];
        int myOffset = 0;

        /* Loop through the agreements */
        final GordianCoreKeyPairSet mySet = (GordianCoreKeyPairSet) pServer;
        final GordianKeyPairSetAgreeASN1 myASN1 = new GordianKeyPairSetAgreeASN1(mySpec, myResId);
        final Iterator<GordianKeyPair> myPairIterator = mySet.iterator();
        final Iterator<byte[]> myHelloIterator = myHello.msgIterator();
        for (GordianKeyPairSignedAgreement myAgreement : theAgreements) {
            /* process clientHello and add serverHello to response */
            myASN1.addMessage(myAgreement.acceptClientHello(myPairIterator.next(), myHelloIterator.next()));

            /* build secret part */
            final byte[] myPart = (byte[]) myAgreement.getResult();
            System.arraycopy(myPart, 0, myResult, myOffset, myPartLen);
            myOffset += myPartLen;
            Arrays.fill(myPart, (byte) 0);
        }

        /* Store the secret */
        storeSecret(myResult);

        /* Return the message */
        return myASN1.getEncodedMessage();
    }

    @Override
    public void acceptServerHello(final GordianKeyPairSet pServer,
                                  final byte[] pServerHello) throws OceanusException {
        /* Must be waiting for serverHello */
        checkStatus(GordianAgreementStatus.AWAITING_SERVERHELLO);

        /* Check valid spec */
        if (!pServer.getKeyPairSetSpec().canSign()) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pServer.getKeyPairSetSpec()));
        }

        /* Parse the serverHello */
        final GordianKeyPairSetAgreeASN1 myHello = GordianKeyPairSetAgreeASN1.getInstance(pServerHello);
        if (!Objects.equals(getAgreementSpec(), myHello.getSpec())) {
            throw new GordianDataException(ERROR_INVSPEC);
        }

        /* Create the result */
        final GordianKeyPairSetAgreementSpec mySpec = myHello.getSpec();
        final int myPartLen = GordianLength.LEN_512.getByteLength();
        final byte[] myResult = new byte[mySpec.getKeyPairSetSpec().numKeyPairs() * myPartLen];
        int myOffset = 0;

        /* Loop through the agreements */
        final GordianCoreKeyPairSet mySet = (GordianCoreKeyPairSet) pServer;
        final Iterator<GordianKeyPair> myPairIterator = mySet.iterator();
        final Iterator<byte[]> myHelloIterator = myHello.msgIterator();
        for (GordianKeyPairSignedAgreement myAgreement : theAgreements) {
            /* process serverHello */
            myAgreement.acceptServerHello(myPairIterator.next(), myHelloIterator.next());

            /* build secret part */
            final byte[] myPart = (byte[]) myAgreement.getResult();
            System.arraycopy(myPart, 0, myResult, myOffset, myPartLen);
            myOffset += myPartLen;
            Arrays.fill(myPart, (byte) 0);
        }

        /* Store the secret */
        storeSecret(myResult);
    }
}
