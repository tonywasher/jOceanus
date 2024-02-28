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
package net.sourceforge.joceanus.jgordianknot.impl.core.agree;

import java.io.IOException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreement;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementStatus;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCompositeKeyPair;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Composite Agreements.
 */
public final class GordianCompositeAgreement {
    /**
     * Private constructor.
     */
    private GordianCompositeAgreement() {
    }

    /**
     * Obtain list of subAgreementSpecs.
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
            /* Determine the agreementType */
            mySpecs.add(new GordianAgreementSpec(mySpec, myType, myKDF));
        }

        /* Return the list */
        return mySpecs;
    }

    /**
     * Derive the composite ephemeral.
     * @param pSubs the subMessages
     * @return the ephemeral (or null)
     * @throws OceanusException on error
     */
    private static X509EncodedKeySpec deriveCompositeEphemeral(final List<GordianAgreementMessageASN1> pSubs) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* initialise the vector */
            final ASN1EncodableVector ks = new ASN1EncodableVector();

            /* Loop through the subMessages */
            for (GordianAgreementMessageASN1 mySub : pSubs) {
                /* Access the ephemeral keySpec and return null if there is no ephemeral */
                final X509EncodedKeySpec myX509 = mySub.getEphemeral();
                if (myX509 == null) {
                    return null;
                }

                /* Add to the vector */
                ks.add(SubjectPublicKeyInfo.getInstance(myX509.getEncoded()));
            }

            /* Build the composite clientHello */
            final AlgorithmIdentifier myId = new AlgorithmIdentifier(MiscObjectIdentifiers.id_alg_composite);
            final SubjectPublicKeyInfo myInfo = new SubjectPublicKeyInfo(myId, new DERSequence(ks).getEncoded());
            return new X509EncodedKeySpec(myInfo.getEncoded());

            /* catch exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to build composite X509", e);
        }
    }

    /**
     * Split composite ephemeral.
     * @param pEphemeral the composite ephemeral
     * @return the ephemeral list (maybe empty)
     * @throws OceanusException on error
     */
    private static List<X509EncodedKeySpec> splitCompositeEphemeral(final X509EncodedKeySpec pEphemeral) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Handle null ephemeral */
            if (pEphemeral == null) {
                return Collections.emptyList();
            }

            /* Access the subKeys */
            final ASN1Sequence myPubSequence = ASN1Sequence.getInstance(pEphemeral.getEncoded());
            final SubjectPublicKeyInfo myPubInfo = SubjectPublicKeyInfo.getInstance(myPubSequence);
            final ASN1Sequence myPubKeys = ASN1Sequence.getInstance(myPubInfo.getPublicKeyData().getBytes());
            final Enumeration<?> enPub = myPubKeys.getObjects();

            /* initialise the vector */
            final List<X509EncodedKeySpec> mySpecs = new ArrayList<>();

            /* Loop through the subMessages */
            while (enPub.hasMoreElements()) {
                final SubjectPublicKeyInfo myPubKInfo = SubjectPublicKeyInfo.getInstance(enPub.nextElement());
                mySpecs.add(new X509EncodedKeySpec(myPubKInfo.getEncoded()));
            }

            /* Return the list */
            return mySpecs;

            /* catch exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to parse composite X509", e);
        }
    }

    /**
     * Create a merged result.
     * @param pSubs the subAgreements
     * @return the merged result
     * @throws OceanusException on error
     */
    private static byte[] mergeResults(final List<GordianAgreement> pSubs) throws OceanusException {
        /* Create the result */
        final int myPartLen = GordianLength.LEN_512.getByteLength();
        final byte[] myResult = new byte[pSubs.size() * myPartLen];
        int myOffset = 0;

        /* Loop through the agreements */
        for (GordianAgreement myAgreement : pSubs) {
            /* build secret part */
            final byte[] myPart = (byte[]) myAgreement.getResult();
            System.arraycopy(myPart, 0, myResult, myOffset, myPartLen);
            myOffset += myPartLen;
            Arrays.fill(myPart, (byte) 0);
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Anonymous agreement.
     */
    public static class GordianCompositeAnonymousAgreement
        extends GordianCoreAnonymousAgreement {
        /**
         * List of agreements.
         */
        private final List<GordianAgreement> theAgreements;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the agreementSpec
         * @throws OceanusException on error
         */
        GordianCompositeAnonymousAgreement(final GordianCoreFactory pFactory,
                                           final GordianAgreementSpec pSpec) throws OceanusException {
            /* Initialise super class */
            super(pFactory, pSpec);

            /* Create the list */
            theAgreements = new ArrayList<>();
            final GordianAgreementFactory myFactory = pFactory.getKeyPairFactory().getAgreementFactory();
            final List<GordianAgreementSpec> mySubAgrees = getSubAgreements(pSpec);
            for (GordianAgreementSpec mySpec : mySubAgrees) {
                theAgreements.add(myFactory.createAgreement(mySpec));
            }
        }

        @Override
        public GordianAgreementMessageASN1 createClientHelloASN1(final GordianKeyPair pServer) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pServer);

            /* Access the composite server key  */
            final GordianCompositeKeyPair myComposite = (GordianCompositeKeyPair) pServer;
            final Iterator<GordianKeyPair> pairIterator = myComposite.iterator();

            /* Create a new clientIV */
            newClientIV();

            /* Loop through the subAgreements */
            final List<GordianAgreementMessageASN1> mySubMsgs = new ArrayList<>();
            for (GordianAgreement myAgree : theAgreements) {
                /* Create the subMsg for the subAgreement */
                final GordianCoreAnonymousAgreement mySub = (GordianCoreAnonymousAgreement) myAgree;
                mySub.storeClientIV(getClientIV());
                final GordianAgreementMessageASN1 mySubMsg = mySub.createClientHelloASN1(pairIterator.next());
                mySubMsgs.add(mySubMsg);
            }

            /* Build the composite clientHello */
            final byte[] myEncapsulated = deriveCompositeEncapsulated(mySubMsgs);
            final X509EncodedKeySpec myEphemeral = deriveCompositeEphemeral(mySubMsgs);
            final GordianAgreementMessageASN1 myClientHello = buildClientHelloASN1(myEncapsulated, myEphemeral);

            /* merge results and store the secret */
            storeSecret(mergeResults(theAgreements));

            /* Return the client hello */
            return myClientHello;
        }

        @Override
        public void acceptClientHelloASN1(final GordianKeyPair pSelf,
                                          final GordianAgreementMessageASN1 pClientHello) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pSelf);

            /* Access the composite server key  */
            final GordianCompositeKeyPair myComposite = (GordianCompositeKeyPair) pSelf;
            final Iterator<GordianKeyPair> pairIterator = myComposite.iterator();
            final Iterator<byte[]> encIterator = splitCompositeEncapsulated(pClientHello.getEncapsulated()).iterator();
            final Iterator<X509EncodedKeySpec> specIterator = splitCompositeEphemeral(pClientHello.getEphemeral()).iterator();

            /* Loop through the subAgreements */
            for (GordianAgreement myAgree : theAgreements) {
                /* Create the subMsg for the subAgreement */
                final GordianCoreAnonymousAgreement mySub = (GordianCoreAnonymousAgreement) myAgree;
                mySub.storeClientIV(pClientHello.getInitVector());
                final byte[] myEnc = encIterator.hasNext() ? encIterator.next() : null;
                final X509EncodedKeySpec myX509 = specIterator.hasNext() ? specIterator.next() : null;
                final GordianAgreementMessageASN1 mySubMsg = mySub.buildClientHelloASN1(myEnc, myX509);

                /* process the subMsg */
                mySub.acceptClientHelloASN1(pairIterator.next(), mySubMsg);
            }

            /* merge results and store the secret */
            storeSecret(mergeResults(theAgreements));
        }

        /**
         * Derive the composite encapsulated.
         * @param pSubs the subMessages
         * @return the encapsulation (or null)
         * @throws OceanusException on error
         */
        private static byte[] deriveCompositeEncapsulated(final List<GordianAgreementMessageASN1> pSubs) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* initialise the vector */
                final ASN1EncodableVector ks = new ASN1EncodableVector();

                /* Loop through the subMessages */
                for (GordianAgreementMessageASN1 mySub : pSubs) {
                    /* Access the ephemeral keySpec and return null if there is no ephemeral */
                    final byte[] myEnc = mySub.getEncapsulated();
                    if (myEnc == null) {
                        return null;
                    }

                    /* Add to the vector */
                    ks.add(new DEROctetString(myEnc));
                }

                /* Build the composite encapsulated */
                return new DERSequence(ks).getEncoded();

                /* catch exceptions */
            } catch (IOException e) {
                throw new GordianIOException("Failed to build composite encapsulated", e);
            }
        }

        /**
         * Split composite encapsulated.
         * @param pEncapsulated the composite encapsulated
         * @return the encapsulated list (maybe empty)
         * @throws OceanusException on error
         */
        private static List<byte[]> splitCompositeEncapsulated(final byte[] pEncapsulated) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Handle null ephemeral */
                if (pEncapsulated == null) {
                    return Collections.emptyList();
                }

                /* Access the subArrays */
                final ASN1Sequence mySequence = ASN1Sequence.getInstance(pEncapsulated);
                final Enumeration<?> enEnc = mySequence.getObjects();

                /* initialise the vector */
                final List<byte[]> myList = new ArrayList<>();

                /* Loop through the subMessages */
                while (enEnc.hasMoreElements()) {
                    myList.add(ASN1OctetString.getInstance(enEnc.nextElement()).getOctets());
                }

                /* Return the list */
                return myList;

                /* catch exceptions */
            } catch (IllegalArgumentException e) {
                throw new GordianIOException("Failed to parse composite Encapsulated", e);
            }
        }
    }

    /**
     * Basic agreement.
     */
    public static class GordianCompositeBasicAgreement
            extends GordianCoreBasicAgreement {
        /**
         * List of agreements.
         */
        private final List<GordianAgreement> theAgreements;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the agreementSpec
         * @throws OceanusException on error
         */
        GordianCompositeBasicAgreement(final GordianCoreFactory pFactory,
                                       final GordianAgreementSpec pSpec) throws OceanusException {
            /* Initialise super class */
            super(pFactory, pSpec);

            /* Create the list */
            theAgreements = new ArrayList<>();
            final GordianAgreementFactory myFactory = pFactory.getKeyPairFactory().getAgreementFactory();
            final List<GordianAgreementSpec> mySubAgrees = getSubAgreements(pSpec);
            for (GordianAgreementSpec mySpec : mySubAgrees) {
                theAgreements.add(myFactory.createAgreement(mySpec));
            }
        }

        @Override
        public GordianAgreementMessageASN1 createClientHelloASN1(final GordianKeyPair pClient) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pClient);

            /* Create ephemeral key */
            final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
            final GordianCompositeKeyPair myClient = (GordianCompositeKeyPair) pClient;
            final Iterator<GordianKeyPair> pairIterator = myClient.iterator();

            /* Create a new clientIV */
            newClientIV();
            storeClientId(((GordianCoreAgreementFactory) myFactory.getAgreementFactory()).getNextId());

            /* Loop through the subAgreements */
            for (GordianAgreement myAgree : theAgreements) {
                /* Create the subMsg for the subAgreement */
                final GordianCoreBasicAgreement mySub = (GordianCoreBasicAgreement) myAgree;
                mySub.storeClientId(getClientId());
                mySub.storeClientIV(getClientIV());
                mySub.createClientHelloASN1(pairIterator.next());
            }

            /* Build the composite clientHello */
            return super.createClientHelloASN1(pClient);
        }

        @Override
        public GordianAgreementMessageASN1 acceptClientHelloASN1(final GordianKeyPair pClient,
                                                                 final GordianKeyPair pServer,
                                                                 final GordianAgreementMessageASN1 pClientHello) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pClient);
            checkKeyPair(pServer);

            /* Access the composite keys  */
            final GordianCompositeKeyPair myClient = (GordianCompositeKeyPair) pClient;
            final Iterator<GordianKeyPair> clientIterator = myClient.iterator();
            final GordianCompositeKeyPair myServer = (GordianCompositeKeyPair) pServer;
            final Iterator<GordianKeyPair> serverIterator = myServer.iterator();

            /* Process the clientHello */
            parseClientHelloASN1(pClientHello);

            /* Create a new serverIV */
            newServerIV();

            /* Loop through the subAgreements */
            for (GordianAgreement myAgree : theAgreements) {
                /* Create the subMsg for the subAgreement */
                final GordianCoreBasicAgreement mySub = (GordianCoreBasicAgreement) myAgree;
                mySub.storeClientId(pClientHello.getClientId());
                mySub.storeClientIV(pClientHello.getInitVector());
                mySub.storeServerIV(getServerIV());
                final GordianAgreementMessageASN1 mySubMsg = mySub.buildClientHelloASN1();

                /* process the subMsg */
                mySub.acceptClientHelloASN1(clientIterator.next(), serverIterator.next(), mySubMsg);
            }

            /* Create the serverHello */
            final GordianAgreementMessageASN1 myServerHello = buildServerHello();

            /* merge results and store the secret */
            storeSecret(mergeResults(theAgreements));

            /* return the server hello */
            return myServerHello;
        }

        @Override
        public void acceptServerHelloASN1(final GordianKeyPair pServer,
                                          final GordianAgreementMessageASN1 pServerHello) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pServer);

            /* Access the composite keys  */
            final GordianCompositeKeyPair myServer = (GordianCompositeKeyPair) pServer;
            final Iterator<GordianKeyPair> serverIterator = myServer.iterator();

            /* process the serverHello */
            processServerHelloASN1(pServerHello);

            /* Loop through the subAgreements */
            for (GordianAgreement myAgree : theAgreements) {
                /* Create the subMsg for the subAgreement */
                final GordianCoreBasicAgreement mySub = (GordianCoreBasicAgreement) myAgree;
                mySub.storeClientId(pServerHello.getClientId());
                mySub.storeClientIV(getClientIV());
                mySub.storeServerIV(pServerHello.getInitVector());
                final GordianAgreementMessageASN1 mySubMsg = mySub.buildServerHello();

                /* process the subMsg */
                mySub.acceptServerHelloASN1(serverIterator.next(), mySubMsg);
            }

            /* merge results and store the secret */
            storeSecret(mergeResults(theAgreements));
        }
    }

    /**
     * Signed agreement.
     */
    public static class GordianCompositeSignedAgreement
            extends GordianCoreSignedAgreement {
        /**
         * List of agreements.
         */
        private final List<GordianAgreement> theAgreements;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the agreementSpec
         * @throws OceanusException on error
         */
        GordianCompositeSignedAgreement(final GordianCoreFactory pFactory,
                                        final GordianAgreementSpec pSpec) throws OceanusException {
            /* Initialise super class */
            super(pFactory, pSpec);

            /* Create the list */
            theAgreements = new ArrayList<>();
            final GordianAgreementFactory myFactory = pFactory.getKeyPairFactory().getAgreementFactory();
            final List<GordianAgreementSpec> mySubAgrees = getSubAgreements(pSpec);
            for (GordianAgreementSpec mySpec : mySubAgrees) {
                theAgreements.add(myFactory.createAgreement(mySpec));
            }
        }

        @Override
        public GordianAgreementMessageASN1 createClientHelloASN1() throws OceanusException {
            /* Create ephemeral key */
            final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(getAgreementSpec().getKeyPairSpec());
            final GordianCompositeKeyPair myEphemeral = (GordianCompositeKeyPair) myGenerator.generateKeyPair();
            final X509EncodedKeySpec myKeySpec = myGenerator.getX509Encoding(myEphemeral);
            final Iterator<GordianKeyPair> pairIterator = myEphemeral.iterator();

            /* Create a new clientIV */
            newClientIV();
            storeClientId(((GordianCoreAgreementFactory) myFactory.getAgreementFactory()).getNextId());
            storeClientEphemeral(myEphemeral);

            /* Loop through the subAgreements */
            for (GordianAgreement myAgree : theAgreements) {
                /* Create the subMsg for the subAgreement */
                final GordianCoreBasicAgreement mySub = (GordianCoreBasicAgreement) myAgree;
                mySub.storeClientId(getClientId());
                mySub.storeClientIV(getClientIV());
                mySub.createClientHelloASN1(pairIterator.next());
            }

            /* Build the composite clientHello */
            final GordianAgreementMessageASN1 myClientHello = buildClientHelloASN1(myKeySpec);

            /* Set status */
            setStatus(GordianAgreementStatus.AWAITING_SERVERHELLO);
            return myClientHello;
        }

        @Override
        public GordianAgreementMessageASN1 acceptClientHelloASN1(final GordianKeyPair pSigner,
                                                                 final GordianAgreementMessageASN1 pClientHello) throws OceanusException {
            /* Create ephemeral key */
            final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(getAgreementSpec().getKeyPairSpec());
            final GordianCompositeKeyPair myEphemeral = (GordianCompositeKeyPair) myGenerator.generateKeyPair();
            final X509EncodedKeySpec myKeySpec = myGenerator.getX509Encoding(myEphemeral);
            final Iterator<GordianKeyPair> serverIterator = myEphemeral.iterator();

            /* Process the clientHello */
            parseClientHelloASN1(pClientHello);

            /* Store ephemeral keyPairs */
            storeClientEphemeral(myGenerator.derivePublicOnlyKeyPair(pClientHello.getEphemeral()));
            storeServerEphemeral(myEphemeral);

            /* Access the composite client key  */
            final X509EncodedKeySpec myClientSpec = pClientHello.getEphemeral();
            final GordianCompositeKeyPair myComposite = (GordianCompositeKeyPair) myGenerator.derivePublicOnlyKeyPair(myClientSpec);
            final Iterator<GordianKeyPair> clientIterator = myComposite.iterator();

            /* Create a new serverIV */
            newServerIV();

            /* Loop through the subAgreements */
            for (GordianAgreement myAgree : theAgreements) {
                /* Create the subMsg for the subAgreement */
                final GordianCoreBasicAgreement mySub = (GordianCoreBasicAgreement) myAgree;
                mySub.storeClientId(pClientHello.getClientId());
                mySub.storeClientIV(pClientHello.getInitVector());
                mySub.storeServerIV(getServerIV());
                final GordianAgreementMessageASN1 mySubMsg = mySub.buildClientHelloASN1();

                /* process the subMsg */
                mySub.acceptClientHelloASN1(clientIterator.next(), serverIterator.next(), mySubMsg);
            }

            /* Create the serverHello */
            final GordianAgreementMessageASN1 myServerHello = buildServerHelloASN1(pSigner).setEphemeral(myKeySpec);

            /* merge results and store the secret */
            storeSecret(mergeResults(theAgreements));

            /* return the server hello */
            return myServerHello;
        }

        @Override
        public void acceptServerHelloASN1(final GordianKeyPair pServer,
                                          final GordianAgreementMessageASN1 pServerHello) throws OceanusException {
            /* Access the composite client key  */
            final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(getAgreementSpec().getKeyPairSpec());
            final X509EncodedKeySpec myClientSpec = pServerHello.getEphemeral();
            final GordianCompositeKeyPair myComposite = (GordianCompositeKeyPair) myGenerator.derivePublicOnlyKeyPair(myClientSpec);
            final Iterator<GordianKeyPair> serverIterator = myComposite.iterator();

            /* process the serverHello */
            processServerHelloASN1(pServer, pServerHello);

            /* Loop through the subAgreements */
            for (GordianAgreement myAgree : theAgreements) {
                /* Create the subMsg for the subAgreement */
                final GordianCoreBasicAgreement mySub = (GordianCoreBasicAgreement) myAgree;
                mySub.storeClientId(pServerHello.getClientId());
                mySub.storeClientIV(getClientIV());
                mySub.storeServerIV(pServerHello.getInitVector());
                final GordianAgreementMessageASN1 mySubMsg = mySub.buildServerHello();

                /* process the subMsg */
                mySub.acceptServerHelloASN1(serverIterator.next(), mySubMsg);
            }

            /* merge results and store the secret */
            storeSecret(mergeResults(theAgreements));
        }
    }

    /**
     * Handshake agreement.
     */
    public static class GordianCompositeHandshakeAgreement
            extends GordianCoreEphemeralAgreement {
        /**
         * List of agreements.
         */
        private final List<GordianAgreement> theAgreements;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the agreementSpec
         * @throws OceanusException on error
         */
        GordianCompositeHandshakeAgreement(final GordianCoreFactory pFactory,
                                           final GordianAgreementSpec pSpec) throws OceanusException {
            /* Initialise super class */
            super(pFactory, pSpec);

            /* Create the list */
            theAgreements = new ArrayList<>();
            final GordianAgreementFactory myFactory = pFactory.getKeyPairFactory().getAgreementFactory();
            final List<GordianAgreementSpec> mySubAgrees = getSubAgreements(pSpec);
            for (GordianAgreementSpec mySpec : mySubAgrees) {
                theAgreements.add(myFactory.createAgreement(mySpec));
            }
        }

        @Override
        public GordianAgreementMessageASN1 createClientHelloASN1(final GordianKeyPair pClient) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pClient);

            /* Access subKeys */
            final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(getAgreementSpec().getKeyPairSpec());
            final GordianCompositeKeyPair myClient = (GordianCompositeKeyPair) pClient;
            final Iterator<GordianKeyPair> pairIterator = myClient.iterator();

            /* Create a new clientIV */
            newClientIV();
            storeClientId(((GordianCoreAgreementFactory) myFactory.getAgreementFactory()).getNextId());

            /* Loop through the subAgreements */
            final List<GordianAgreementMessageASN1> mySubMsgs = new ArrayList<>();
            for (GordianAgreement myAgree : theAgreements) {
                /* Create the subMsg for the subAgreement */
                final GordianCoreEphemeralAgreement mySub = (GordianCoreEphemeralAgreement) myAgree;
                mySub.storeClientId(getClientId());
                mySub.storeClientIV(getClientIV());
                mySubMsgs.add(mySub.createClientHelloASN1(pairIterator.next()));
            }

            /* Derive the ephemeral keySpec */
            final X509EncodedKeySpec myEphemeral = deriveCompositeEphemeral(mySubMsgs);
            storeClientEphemeral(myGenerator.derivePublicOnlyKeyPair(myEphemeral));
            storeClient(pClient);

            /* Build the composite clientHello */
            final GordianAgreementMessageASN1 myClientHello = buildClientHelloASN1(myEphemeral);

            /* Set status */
            setStatus(GordianAgreementStatus.AWAITING_SERVERHELLO);
            return myClientHello;
        }

        @Override
        public GordianAgreementMessageASN1 acceptClientHelloASN1(final GordianKeyPair pClient,
                                                                 final GordianKeyPair pServer,
                                                                 final GordianAgreementMessageASN1 pClientHello) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pClient);
            checkKeyPair(pServer);

            /* Access the composite keys  */
            final GordianCompositeKeyPair myClient = (GordianCompositeKeyPair) pClient;
            final Iterator<GordianKeyPair> clientIterator = myClient.iterator();
            final GordianCompositeKeyPair myServer = (GordianCompositeKeyPair) pServer;
            final Iterator<GordianKeyPair> serverIterator = myServer.iterator();
            final Iterator<X509EncodedKeySpec> specIterator = splitCompositeEphemeral(pClientHello.getEphemeral()).iterator();

            /* Process the clientHello */
            parseClientHelloASN1(pClientHello);

            /* Create a new serverIV */
            newServerIV();
            final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(getAgreementSpec().getKeyPairSpec());
            storeClientEphemeral(myGenerator.derivePublicOnlyKeyPair(pClientHello.getEphemeral()));
            storeClient(pClient);
            storeServer(pServer);

            /* Loop through the subAgreements */
            final List<GordianAgreementMessageASN1> mySubMsgs = new ArrayList<>();
            for (GordianAgreement myAgree : theAgreements) {
                /* Create the subMsg for the subAgreement */
                final GordianCoreEphemeralAgreement mySub = (GordianCoreEphemeralAgreement) myAgree;
                mySub.storeClientId(pClientHello.getClientId());
                mySub.storeClientIV(pClientHello.getInitVector());
                mySub.storeServerIV(getServerIV());
                final GordianAgreementMessageASN1 mySubMsg = mySub.buildClientHelloASN1(specIterator.next());

                /* process the subMsg */
                mySubMsgs.add(mySub.acceptClientHelloASN1(clientIterator.next(), serverIterator.next(), mySubMsg));
            }

            /* Create the serverHello */
            final X509EncodedKeySpec myEphemeral = deriveCompositeEphemeral(mySubMsgs);
            storeServerEphemeral(myGenerator.derivePublicOnlyKeyPair(myEphemeral));

            /* merge results and store the secret */
            storeSecret(mergeResults(theAgreements));

            /* return the server hello */
            return buildServerHello();
        }

        @Override
        public GordianAgreementMessageASN1 acceptServerHelloASN1(final GordianKeyPair pServer,
                                                                 final GordianAgreementMessageASN1 pServerHello) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pServer);

            /* Access the composite keys  */
            final GordianCompositeKeyPair myServer = (GordianCompositeKeyPair) pServer;
            final Iterator<GordianKeyPair> serverIterator = myServer.iterator();
            final Iterator<X509EncodedKeySpec> specIterator = splitCompositeEphemeral(pServerHello.getEphemeral()).iterator();
            storeServer(pServer);

            /* process the serverHello */
            processServerHelloASN1(pServer, pServerHello);

            /* Loop through the subAgreements */
            for (GordianAgreement myAgree : theAgreements) {
                /* Create the subMsg for the subAgreement */
                final GordianCoreEphemeralAgreement mySub = (GordianCoreEphemeralAgreement) myAgree;
                mySub.storeClientId(pServerHello.getClientId());
                mySub.storeClientIV(getClientIV());
                mySub.storeServerIV(pServerHello.getInitVector());
                final GordianAgreementMessageASN1 mySubMsg = mySub.buildServerHello(specIterator.next(), null);

                /* process the subMsg */
                mySub.acceptServerHelloASN1(serverIterator.next(), mySubMsg);
            }

            /* merge results and store the secret */
            storeSecret(mergeResults(theAgreements));
            return buildClientConfirmASN1();
        }
    }
}
