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
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianHandshakeAgreement;
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
public class GordianKeyPairSetHandshakeAgreement
        extends GordianCoreAgreement {
    /**
     * The list of agreements.
     */
    private final List<GordianHandshakeAgreement> theAgreements;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     * @throws OceanusException on error
     */
    GordianKeyPairSetHandshakeAgreement(final GordianCoreFactory pFactory,
                                        final GordianAgreementSpec pSpec) throws OceanusException {
        /* Initialise underlying class */
        super(pFactory, pSpec);
        theAgreements = new ArrayList<>();

        /* Create the agreements */
        final GordianAgreementFactory myFactory = pFactory.getAsymmetricFactory().getAgreementFactory();
        final GordianKDFType myKDFType = pSpec.getKDFType();
        theAgreements.add((GordianHandshakeAgreement) myFactory.createAgreement(
                GordianAgreementSpec.dhUnified(myKDFType)));
        theAgreements.add((GordianHandshakeAgreement) myFactory.createAgreement(
                GordianAgreementSpec.ecdhUnified(GordianAsymKeyType.EC, myKDFType)));
        theAgreements.add((GordianHandshakeAgreement) myFactory.createAgreement(
                GordianAgreementSpec.xdhUnified(myKDFType)));
    }

    /**
     * Create the clientHello message.
     * @param pKeyPairSet the client keyPairSet
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

        /* Loop through the agreements */
        final Iterator<GordianKeyPair> myIterator = ((GordianCoreKeyPairSet) pKeyPairSet).iterator();
        for (GordianHandshakeAgreement myAgreement : theAgreements) {
            myASN1.addMessage(myAgreement.createClientHello(myIterator.next()));
        }

        /* Return the combined clientHello */
        return myASN1.getEncodedMessage();
    }

    /**
     * Accept the clientHello.
     * @param pClient the server keyPairSet
     * @param pServer the server keyPairSet
     * @param pClientHello the incoming clientHello message
     * @return the serverHello message
     * @throws OceanusException on error
     */
    public byte[] acceptClientHello(final GordianKeyPairSet pClient,
                                    final GordianKeyPairSet pServer,
                                    final byte[] pClientHello)  throws OceanusException {
        /* Parse the clientHello */
        final GordianKeyPairSetAgreeASN1 myHello = GordianKeyPairSetAgreeASN1.getInstance(pClientHello);
        final AlgorithmIdentifier myResId = myHello.getResultId();

        /* Process result identifier */
        processResultIdentifier(myResId);

        /* Loop through the agreements */
        final GordianKeyPairSetSpec mySpec = myHello.getSpec();
        final GordianCoreKeyPairSet myClient = (GordianCoreKeyPairSet) pClient;
        final GordianCoreKeyPairSet myServer = (GordianCoreKeyPairSet) pServer;
        final GordianKeyPairSetAgreeASN1 myASN1 = new GordianKeyPairSetAgreeASN1(mySpec, myResId);
        final Iterator<GordianKeyPair> myClientIterator = myClient.iterator();
        final Iterator<GordianKeyPair> myServerIterator = myServer.iterator();
        final Iterator<byte[]> myHelloIterator = myHello.msgIterator();
        for (GordianHandshakeAgreement myAgreement : theAgreements) {
            myASN1.addMessage(myAgreement.acceptClientHello(myClientIterator.next(), myServerIterator.next(), myHelloIterator.next()));
        }

        /* Return the message */
        return myASN1.getEncodedMessage();
    }

    /**
     * Accept the serverHello.
     * @param pServer the server keyPair
     * @param pServerHello the serverHello message
     * @return the clientConfirm (or null if no confirmation)
     * @throws OceanusException on error
     */
    public byte[] acceptServerHello(final GordianKeyPairSet pServer,
                                    final byte[] pServerHello) throws OceanusException {
        /* Parse the serverHello */
        final GordianKeyPairSetAgreeASN1 myHello = GordianKeyPairSetAgreeASN1.getInstance(pServerHello);
        final AlgorithmIdentifier myResId = myHello.getResultId();

        /* Create the result */
        final GordianKeyPairSetSpec mySpec = myHello.getSpec();
        final int myPartLen = GordianLength.LEN_512.getByteLength();
        final byte[] myResult = new byte[mySpec.numKeyPairs() * myPartLen];
        int myOffset = 0;

        /* Loop through the agreements */
        final GordianCoreKeyPairSet mySet = (GordianCoreKeyPairSet) pServer;
        final Iterator<GordianKeyPair> myPairIterator = mySet.iterator();
        final Iterator<byte[]> myHelloIterator = myHello.msgIterator();
        for (GordianHandshakeAgreement myAgreement : theAgreements) {
            myAgreement.acceptServerHello(myPairIterator.next(), myHelloIterator.next());
            final byte[] myPart = (byte[]) myAgreement.getResult();
            System.arraycopy(myPart, 0, myResult, myOffset, myPartLen);
            myOffset += myPartLen;
        }

        /* Store the secret */
        storeSecret(myResult);

        /* No confirm at present */
        return null;
    }

    /**
     * Accept the clientConfirm.
     * @param pClientConfirm the clientConfirm message
     * @throws OceanusException on error
     */
    public void acceptClientConfirm(final byte[] pClientConfirm) throws OceanusException {
        /* Parse the clientConfirm */
        final GordianKeyPairSetAgreeASN1 myConfirm = GordianKeyPairSetAgreeASN1.getInstance(pClientConfirm);

        /* Create the result */
        final GordianKeyPairSetSpec mySpec = myConfirm.getSpec();
        final int myPartLen = GordianLength.LEN_512.getByteLength();
        final byte[] myResult = new byte[mySpec.numKeyPairs() * myPartLen];
        int myOffset = 0;

        /* Loop through the agreements */
        final Iterator<byte[]> myConfirmIterator = myConfirm.msgIterator();
        for (GordianHandshakeAgreement myAgreement : theAgreements) {
            myAgreement.acceptClientConfirm(myConfirmIterator.next());
            final byte[] myPart = (byte[]) myAgreement.getResult();
            System.arraycopy(myPart, 0, myResult, myOffset, myPartLen);
            myOffset += myPartLen;
        }

        /* Store the secret */
        storeSecret(myResult);
    }
}
