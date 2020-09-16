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
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementStatus;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetAgreement;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyPairSet signedAgreement.
 */
public class GordianCoreKeyPairSetAnonymousAgreement
        extends GordianCoreAgreement<GordianKeyPairSetAgreementSpec>
        implements GordianKeyPairSetAnonymousAgreement, GordianKeyPairSetAgreement {
    /**
     * The list of agreements.
     */
    private final List<GordianKeyPairAnonymousAgreement> theAgreements;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     * @throws OceanusException on error
     */
    GordianCoreKeyPairSetAnonymousAgreement(final GordianCoreFactory pFactory,
                                            final GordianKeyPairSetAgreementSpec pSpec) throws OceanusException {
        /* Initialise underlying class */
        super(pFactory, pSpec);
        theAgreements = new ArrayList<>();

        /* Create the agreements */
        final GordianAgreementFactory myFactory = pFactory.getKeyPairFactory().getAgreementFactory();
        final GordianKDFType myKDFType = pSpec.getKDFType();
        theAgreements.add((GordianKeyPairAnonymousAgreement) myFactory.createAgreement(
                GordianKeyPairAgreementSpec.dhAnon(myKDFType)));
        theAgreements.add((GordianKeyPairAnonymousAgreement) myFactory.createAgreement(
                GordianKeyPairAgreementSpec.ecdhAnon(GordianKeyPairType.EC, myKDFType)));
        theAgreements.add((GordianKeyPairAnonymousAgreement) myFactory.createAgreement(
                GordianKeyPairAgreementSpec.xdhAnon(myKDFType)));
    }

    @Override
    public byte[] createClientHello(final GordianKeyPairSet pServer) throws OceanusException {
        /* Must be in clean state */
        checkStatus(GordianAgreementStatus.CLEAN);

        /* Check valid spec */
        final GordianKeyPairSetSpec mySpec = pServer.getKeyPairSetSpec();
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
        final Iterator<GordianKeyPair> myIterator = ((GordianCoreKeyPairSet) pServer).iterator();
        for (GordianKeyPairAnonymousAgreement myAgreement : theAgreements) {
            /* create the clientHello and add to message */
            myASN1.addMessage(myAgreement.createClientHello(myIterator.next()));

            /* build secret part */
            final byte[] myPart = (byte[]) myAgreement.getResult();
            System.arraycopy(myPart, 0, myResult, myOffset, myPartLen);
            myOffset += myPartLen;
            Arrays.fill(myPart, (byte) 0);
        }

        /* Store the secret */
        storeSecret(myResult);

        /* Return the combined clientHello */
        return myASN1.getEncodedMessage();
    }

    @Override
    public void acceptClientHello(final GordianKeyPairSet pServer,
                                  final byte[] pClientHello)  throws OceanusException {
        /* Must be in clean state */
        checkStatus(GordianAgreementStatus.CLEAN);

        /* Check valid spec */
        final GordianKeyPairSetSpec mySpec = pServer.getKeyPairSetSpec();
        if (!mySpec.canAgree()) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(mySpec));
        }

        /* Parse the clientHello */
        final GordianKeyPairSetAgreeASN1 myHello = GordianKeyPairSetAgreeASN1.getInstance(pClientHello);
        final AlgorithmIdentifier myResId = myHello.getResultId();

        /* Process result identifier */
        processResultIdentifier(myResId);

        /* Create the result */
        final int myPartLen = GordianLength.LEN_512.getByteLength();
        final byte[] myResult = new byte[mySpec.numKeyPairs() * myPartLen];
        int myOffset = 0;

        /* Loop through the agreements */
        final GordianCoreKeyPairSet mySet = (GordianCoreKeyPairSet) pServer;
        final Iterator<GordianKeyPair> myPairIterator = mySet.iterator();
        final Iterator<byte[]> myHelloIterator = myHello.msgIterator();
        for (GordianKeyPairAnonymousAgreement myAgreement : theAgreements) {
            /* process the clientHello */
            myAgreement.acceptClientHello(myPairIterator.next(), myHelloIterator.next());

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
