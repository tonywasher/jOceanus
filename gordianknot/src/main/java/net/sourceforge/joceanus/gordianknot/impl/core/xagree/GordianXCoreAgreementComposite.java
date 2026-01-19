/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.gordianknot.impl.core.xagree;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianAgreementResult.GordianDerivationId;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianDataConverter;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.kdf.GordianHKDFEngine;
import net.sourceforge.joceanus.gordianknot.impl.core.kdf.GordianHKDFParams;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianCompositeKeyPair;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Implementation engine for composite Agreements.
 */
public class GordianXCoreAgreementComposite extends GordianXCoreAgreementEngine {
    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * List of underlying engines.
     */
    private final List<GordianXCoreAgreementEngine> theEngines;

    /**
     * The builder.
     */
    private final GordianXCoreAgreementBuilder theBuilder;

    /**
     * The builder.
     */
    private final GordianXCoreAgreementState theState;

    /**
     * Constructor.
     *
     * @param pSupplier the supplier
     * @param pSpec     the agreementSpec
     * @param pEngines  the engines
     * @throws GordianException on error
     */
    GordianXCoreAgreementComposite(final GordianXCoreAgreementSupplier pSupplier,
                                   final GordianAgreementSpec pSpec,
                                   final List<GordianXCoreAgreementEngine> pEngines) throws GordianException {
        super(pSupplier, pSpec);
        theFactory = pSupplier.getFactory();
        theEngines = pEngines;
        theBuilder = getBuilder();
        theState = theBuilder.getState();
    }

    /**
     * Obtain list of subAgreementSpecs.
     *
     * @param pSpec the composite agreement Spec
     * @return the list of subAgreementSpecs
     */
    static List<GordianAgreementSpec> getSubAgreements(final GordianAgreementSpec pSpec) {
        /* Create the list */
        final List<GordianAgreementSpec> mySpecs = new ArrayList<>();

        /* Switch signed agreements to Basic sub agreements */
        final GordianAgreementType myType = pSpec.getAgreementType().isSigned()
                ? GordianAgreementType.BASIC
                : pSpec.getAgreementType();
        final GordianKDFType myKDF = pSpec.getKDFType();

        /* Loop through the keyPairs */
        final GordianKeyPairSpec myKeyPairSpec = pSpec.getKeyPairSpec();
        final Iterator<GordianKeyPairSpec> myIterator = myKeyPairSpec.keySpecIterator();
        while (myIterator.hasNext()) {
            final GordianKeyPairSpec mySpec = myIterator.next();
            /* Determine the agreementType (note that we have no confirmation) */
            mySpecs.add(new GordianAgreementSpec(mySpec, myType, myKDF));
        }

        /* Return the list */
        return mySpecs;
    }

    @Override
    public void buildClientHello() throws GordianException {
        /* Access the client and server keyPairs */
        final GordianXCoreAgreementParticipant myClient = theState.getClient();
        final GordianCompositeKeyPair myClientKeyPair = (GordianCompositeKeyPair) myClient.getKeyPair();
        final Iterator<GordianKeyPair> myClientIterator = myClientKeyPair == null ? null : myClientKeyPair.iterator();
        final GordianCompositeKeyPair myEphemeralKeyPair = (GordianCompositeKeyPair) myClient.getEphemeralKeyPair();
        final Iterator<GordianKeyPair> myEphemeralIterator = myEphemeralKeyPair == null ? null : myEphemeralKeyPair.iterator();
        final GordianXCoreAgreementParticipant myServer = theState.getServer();
        final GordianCompositeKeyPair myServerKeyPair = (GordianCompositeKeyPair) myServer.getKeyPair();
        final Iterator<GordianKeyPair> myServerIterator = myServerKeyPair == null ? null : myServerKeyPair.iterator();

        /* Protect against exceptions */
        try {
            /* Create vector for encapsulated */
            final ASN1EncodableVector myEncapsulated = new ASN1EncodableVector();

            /* Loop through the engines */
            for (GordianXCoreAgreementEngine myEngine : theEngines) {
                /* Access engine details */
                final GordianXCoreAgreementState myEngState = myEngine.getBuilder().getState();
                final GordianXCoreAgreementParticipant myEngClient = myEngState.getClient();
                final GordianXCoreAgreementParticipant myEngServer = myEngState.getServer();

                /* Update keyPairs and initVector */
                myEngClient.setKeyPair(myClientKeyPair == null ? null : myClientIterator.next());
                myEngClient.setEphemeralKeyPair(myEphemeralKeyPair == null ? null : myEphemeralIterator.next());
                myEngServer.setKeyPair(myServerKeyPair == null ? null : myServerIterator.next());
                myEngClient.setInitVector(myClient.getInitVector());

                /* Build clientHello details in the engine */
                myEngine.buildClientHello();

                /* Add any encapsulated to sequence */
                final byte[] myEngEncapsulated = myEngState.getEncapsulated();
                if (myEngEncapsulated != null) {
                    myEncapsulated.add(new DEROctetString(myEngEncapsulated));
                }
            }

            /* Record combined ephemeral and encapsulated */
            if (myEncapsulated.size() > 0) {
                theState.setEncapsulated(new DERSequence(myEncapsulated).getEncoded());
            }

            /* catch exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to build combined clientHello", e);
        }

        /* Sort out the result if anonymous */
        if (theState.getSpec().getAgreementType().isAnonymous()) {
            mergeResults();
        }
    }

    /**
     * Process the clientHello.
     *
     * @throws GordianException on error
     */
    public void processClientHello() throws GordianException {
        /* Access the client and server keyPairs */
        final GordianXCoreAgreementParticipant myClient = theState.getClient();
        final GordianCompositeKeyPair myClientKeyPair = (GordianCompositeKeyPair) myClient.getKeyPair();
        final Iterator<GordianKeyPair> myClientIterator = myClientKeyPair == null ? null : myClientKeyPair.iterator();
        final GordianXCoreAgreementParticipant myServer = theState.getServer();
        final GordianCompositeKeyPair myServerKeyPair = (GordianCompositeKeyPair) myServer.getKeyPair();
        final Iterator<GordianKeyPair> myServerIterator = myServerKeyPair == null ? null : myServerKeyPair.iterator();
        final GordianCompositeKeyPair myClientEphemeralKeyPair = (GordianCompositeKeyPair) myClient.getEphemeralKeyPair();
        final Iterator<GordianKeyPair> myClientEphemeralIterator = myClientEphemeralKeyPair == null ? null : myClientEphemeralKeyPair.iterator();
        final GordianCompositeKeyPair myServerEphemeralKeyPair = (GordianCompositeKeyPair) myServer.getEphemeralKeyPair();
        final Iterator<GordianKeyPair> myServerEphemeralIterator = myServerEphemeralKeyPair == null ? null : myServerEphemeralKeyPair.iterator();

        /* Access encapsulated sequence */
        final byte[] myEncapsulated = theState.getEncapsulated();
        final Enumeration<?> enEnc = myEncapsulated == null ? null : ASN1Sequence.getInstance(myEncapsulated).getObjects();

        /* Loop through the engines */
        for (GordianXCoreAgreementEngine myEngine : theEngines) {
            /* Access engine details */
            final GordianXCoreAgreementBuilder myEngBuilder = myEngine.getBuilder();
            final GordianXCoreAgreementState myEngState = myEngBuilder.getState();
            final GordianXCoreAgreementParticipant myEngClient = myEngState.getClient();
            final GordianXCoreAgreementParticipant myEngServer = myEngState.getServer();

            /* Update keyPairs and initVector */
            myEngClient.setKeyPair(myClientKeyPair == null ? null : myClientIterator.next());
            myEngServer.setKeyPair(myServerKeyPair == null ? null : myServerIterator.next());
            myEngClient.setInitVector(myClient.getInitVector());
            myEngServer.setInitVector(myServer.getInitVector());
            myEngClient.setEphemeralKeyPair(myClientEphemeralKeyPair == null ? null : myClientEphemeralIterator.next());
            myEngServer.setEphemeralKeyPair(myServerEphemeralKeyPair == null ? null : myServerEphemeralIterator.next());
            myEngBuilder.parseEncapsulated(enEnc == null ? null : ASN1OctetString.getInstance(enEnc.nextElement()).getOctets());

            /* Process clientHello details in the engine */
            myEngine.processClientHello();
        }

        /* Sort out the result if no confirm */
        if (!Boolean.TRUE.equals(theState.getSpec().withConfirm())) {
            mergeResults();
        }
    }

    @Override
    public void processServerHello() throws GordianException {
        /* Access the client and server keyPairs */
        final GordianXCoreAgreementParticipant myServer = theState.getServer();
        final boolean isSigned = theState.getSpec().getAgreementType().isSigned();
        final GordianCompositeKeyPair myEphemeralKeyPair = (GordianCompositeKeyPair) myServer.getEphemeralKeyPair();
        final Iterator<GordianKeyPair> myEphemeralIterator = myEphemeralKeyPair == null ? null : myEphemeralKeyPair.iterator();

        /* Loop through the engines */
        for (GordianXCoreAgreementEngine myEngine : theEngines) {
            /* Access engine details */
            final GordianXCoreAgreementBuilder myEngBuilder = myEngine.getBuilder();
            final GordianXCoreAgreementState myEngState = myEngBuilder.getState();
            final GordianXCoreAgreementParticipant myEngServer = myEngState.getServer();

            /* Store initVector and ephemeral keyPair */
            myEngServer.setInitVector(myServer.getInitVector());
            myEngServer.setEphemeralKeyPair(myEphemeralIterator == null ? null : myEphemeralIterator.next());
            if (isSigned) {
                myEngBuilder.copyEphemerals();
            }

            /* Process serverHello details in the engine */
            myEngine.processServerHello();
        }

        /* Sort out the result */
        mergeResults();
    }

    @Override
    public void processClientConfirm() throws GordianException {
        /* Sort out the result */
        mergeResults();
    }

    /**
     * Merge and store the result.
     *
     * @throws GordianException on error
     */
    private void mergeResults() throws GordianException {
        /* Protect against exceptions */
        final GordianHKDFParams myParams = GordianHKDFParams.extractOnly();
        try {
            /* Create the HKDF parameters */
            final GordianDigestSpec myDigestSpec = new GordianDigestSpec(GordianDerivationId.COMPOSITE.getDigestType());
            Random myRandom = null;

            /* Loop through the engines */
            for (GordianXCoreAgreementEngine myEngine : theEngines) {
                /* Access the secret result */
                final byte[] myPart = (byte[]) myEngine.getBuilder().getState().getResult();

                /* Handle random bytes */
                if (myRandom == null) {
                    /* Build the 64-bit seed, create the seeded random and populate bytes */
                    final long mySeed = GordianDataConverter.byteArrayToLong(myPart);
                    myRandom = new Random(mySeed);
                    final byte[] myBytes = new byte[Long.BYTES];
                    myRandom.nextBytes(myBytes);
                    myParams.withSalt(myBytes);
                }

                /* Add part to parameters */
                myParams.withIKM(myPart);
                Arrays.fill(myPart, (byte) 0);
            }

            /* Derive the bytes and store as secret */
            final GordianHKDFEngine myEngine = new GordianHKDFEngine(theFactory, myDigestSpec);
            theBuilder.storeSecret(myEngine.deriveBytes(myParams));

        } finally {
            myParams.clearParameters();
        }
    }
}
