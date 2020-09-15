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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementStatus;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairHandshakeAgreement;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetHandshakeAgreement;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyPairSet signedAgreement.
 */
public class GordianCoreKeyPairSetHandshakeAgreement
        extends GordianCoreAgreement
        implements GordianKeyPairSetHandshakeAgreement {
    /**
     * The list of agreements.
     */
    private final List<GordianKeyPairHandshakeAgreement> theAgreements;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     * @throws OceanusException on error
     */
    GordianCoreKeyPairSetHandshakeAgreement(final GordianCoreFactory pFactory,
                                            final GordianAgreementSpec pSpec) throws OceanusException {
        /* Initialise underlying class */
        super(pFactory, pSpec);
        theAgreements = new ArrayList<>();

        /* Create the agreements */
        final GordianAgreementFactory myFactory = pFactory.getKeyPairFactory().getAgreementFactory();
        final GordianKDFType myKDFType = pSpec.getKDFType();
        final Boolean withConfirm = pSpec.withConfirm();
        theAgreements.add((GordianKeyPairHandshakeAgreement) myFactory.createAgreement(
                GordianAgreementSpec.dhUnifiedConfirm(myKDFType, withConfirm)));
        theAgreements.add((GordianKeyPairHandshakeAgreement) myFactory.createAgreement(
                GordianAgreementSpec.ecdhUnifiedConfirm(GordianKeyPairType.EC, myKDFType, withConfirm)));
        theAgreements.add((GordianKeyPairHandshakeAgreement) myFactory.createAgreement(
                GordianAgreementSpec.xdhUnifiedConfirm(myKDFType, withConfirm)));
    }

    @Override
    public byte[] createClientHello(final GordianKeyPairSet pKeyPairSet) throws OceanusException {
        /* Must be in clean state */
        checkStatus(GordianAgreementStatus.CLEAN);

        /* Check valid spec */
        final GordianKeyPairSetSpec mySpec = pKeyPairSet.getKeyPairSetSpec();
        if (!mySpec.canAgree()) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(mySpec));
        }

        /* Create the message */
        final AlgorithmIdentifier myResId = getIdentifierForResult();
        final GordianKeyPairSetAgreeASN1 myASN1 = new GordianKeyPairSetAgreeASN1(mySpec, myResId);

        /* Loop through the agreements */
        final Iterator<GordianKeyPair> myIterator = ((GordianCoreKeyPairSet) pKeyPairSet).iterator();
        for (GordianKeyPairHandshakeAgreement myAgreement : theAgreements) {
            /* create the clientHello and add to message */
            myASN1.addMessage(myAgreement.createClientHello(myIterator.next()));
        }

        /* Set status */
        setStatus(GordianAgreementStatus.AWAITING_SERVERHELLO);

        /* Return the combined clientHello */
        return myASN1.getEncodedMessage();
    }

    @Override
    public byte[] acceptClientHello(final GordianKeyPairSet pClient,
                                    final GordianKeyPairSet pServer,
                                    final byte[] pClientHello)  throws OceanusException {
        /* Must be in clean state */
        checkStatus(GordianAgreementStatus.CLEAN);

        /* Parse the clientHello */
        final GordianKeyPairSetAgreeASN1 myHello = GordianKeyPairSetAgreeASN1.getInstance(pClientHello);
        final GordianKeyPairSetSpec mySpec = myHello.getSpec();
        final AlgorithmIdentifier myResId = myHello.getResultId();
        final boolean noConfirm = !getAgreementSpec().withConfirm();

        /* Process result identifier */
        processResultIdentifier(myResId);

        /* Create the result if needed */
        final int myPartLen = GordianLength.LEN_512.getByteLength();
        final byte[] myResult = noConfirm
                ? new byte[mySpec.numKeyPairs() * myPartLen]
                : null;
        int myOffset = 0;

        /* Loop through the agreements */
        final GordianCoreKeyPairSet myClient = (GordianCoreKeyPairSet) pClient;
        final GordianCoreKeyPairSet myServer = (GordianCoreKeyPairSet) pServer;
        final GordianKeyPairSetAgreeASN1 myASN1 = new GordianKeyPairSetAgreeASN1(mySpec, myResId);
        final Iterator<GordianKeyPair> myClientIterator = myClient.iterator();
        final Iterator<GordianKeyPair> myServerIterator = myServer.iterator();
        final Iterator<byte[]> myHelloIterator = myHello.msgIterator();
        for (GordianKeyPairHandshakeAgreement myAgreement : theAgreements) {
            /* Accept the clientHello and add response to serverHello */
            myASN1.addMessage(myAgreement.acceptClientHello(myClientIterator.next(), myServerIterator.next(), myHelloIterator.next()));

            /* Don't process result if we are with confirm */
            if (!noConfirm) {
                continue;
            }

            /* build secret part */
            final byte[] myPart = (byte[]) myAgreement.getResult();
            System.arraycopy(myPart, 0, myResult, myOffset, myPartLen);
            myOffset += myPartLen;
            Arrays.fill(myPart, (byte) 0);
        }

        /* Store the secret if not with confirm */
        if (noConfirm) {
            storeSecret(myResult);
        } else {
            /* set status */
            setStatus(GordianAgreementStatus.AWAITING_CLIENTCONFIRM);
        }

        /* Return the message */
        return myASN1.getEncodedMessage();
    }

    @Override
    public byte[] acceptServerHello(final GordianKeyPairSet pServer,
                                    final byte[] pServerHello) throws OceanusException {
        /* Must be waiting for serverHello */
        checkStatus(GordianAgreementStatus.AWAITING_SERVERHELLO);

        /* Parse the serverHello */
        final GordianKeyPairSetAgreeASN1 myHello = GordianKeyPairSetAgreeASN1.getInstance(pServerHello);
        final AlgorithmIdentifier myResId = myHello.getResultId();
        final boolean withConfirm = getAgreementSpec().withConfirm();

        /* Create the confirm message if needed */
        final GordianKeyPairSetAgreeASN1 myConfirm = withConfirm
                ? new GordianKeyPairSetAgreeASN1(myHello.getSpec(), myResId)
                : null;

        /* Create the result */
        final GordianKeyPairSetSpec mySpec = myHello.getSpec();
        final int myPartLen = GordianLength.LEN_512.getByteLength();
        final byte[] myResult = new byte[mySpec.numKeyPairs() * myPartLen];
        int myOffset = 0;

        /* Loop through the agreements */
        final GordianCoreKeyPairSet mySet = (GordianCoreKeyPairSet) pServer;
        final Iterator<GordianKeyPair> myPairIterator = mySet.iterator();
        final Iterator<byte[]> myHelloIterator = myHello.msgIterator();
        for (GordianKeyPairHandshakeAgreement myAgreement : theAgreements) {
            /* Accept the serverHello */
            final byte[] myMsg = myAgreement.acceptServerHello(myPairIterator.next(), myHelloIterator.next());

            /* Process confirm message if required */
            if (withConfirm) {
                myConfirm.addMessage(myMsg);
            }

            /* build secret part */
            final byte[] myPart = (byte[]) myAgreement.getResult();
            System.arraycopy(myPart, 0, myResult, myOffset, myPartLen);
            myOffset += myPartLen;
            Arrays.fill(myPart, (byte) 0);
        }

        /* Store the secret */
        storeSecret(myResult);

        /* Return confirm message or null */
        return withConfirm ? myConfirm.getEncodedMessage() : null;
    }

    @Override
    public void acceptClientConfirm(final byte[] pClientConfirm) throws OceanusException {
        /* Must be waiting for clientConfirm */
        checkStatus(GordianAgreementStatus.AWAITING_CLIENTCONFIRM);

        /* Parse the clientConfirm */
        final GordianKeyPairSetAgreeASN1 myConfirm = GordianKeyPairSetAgreeASN1.getInstance(pClientConfirm);

        /* Create the result */
        final GordianKeyPairSetSpec mySpec = myConfirm.getSpec();
        final int myPartLen = GordianLength.LEN_512.getByteLength();
        final byte[] myResult = new byte[mySpec.numKeyPairs() * myPartLen];
        int myOffset = 0;

        /* Loop through the agreements */
        final Iterator<byte[]> myConfirmIterator = myConfirm.msgIterator();
        for (GordianKeyPairHandshakeAgreement myAgreement : theAgreements) {
            /* Accept the clientConfirm */
            myAgreement.acceptClientConfirm(myConfirmIterator.next());

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
