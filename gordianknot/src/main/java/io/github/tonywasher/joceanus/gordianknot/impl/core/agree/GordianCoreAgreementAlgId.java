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
package io.github.tonywasher.joceanus.gordianknot.impl.core.agree;

import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementKDF;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementType;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactory;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactoryType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.keyset.spec.GordianKeySetSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianASN1Util;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cipher.GordianCoreCipherFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keyset.GordianKeySetSpecASN1;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreNTRUPrimeSpec;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERGeneralString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Mappings from EncodedId to AgreementSpec.
 */
public class GordianCoreAgreementAlgId {
    /**
     * AgreementOID branch.
     */
    private static final ASN1ObjectIdentifier AGREEOID = GordianASN1Util.ASYMOID.branch("2");

    /**
     * AgreementSpec OID branch.
     */
    private static final ASN1ObjectIdentifier AGREESPECOID = AGREEOID.branch("1");

    /**
     * AgreementKeyPairSpec OID branch.
     */
    private static final ASN1ObjectIdentifier KEYPAIRSPECOID = AGREEOID.branch("2");

    /**
     * Byte Result OID branch.
     */
    static final ASN1ObjectIdentifier BYTERESULTOID = AGREEOID.branch("3");

    /**
     * Error Result OID branch.
     */
    static final ASN1ObjectIdentifier ERRORRESULTOID = AGREEOID.branch("4");

    /**
     * Null KeyPairSpec for Partial AgreementSpec.
     */
    private static final GordianKeyPairSpec NULLKEYPAIRSPEC = GordianCoreKeyPairSpecBuilder.newInstance().ed448();

    /**
     * The factory.
     */
    private final GordianFactory theFactory;

    /**
     * Map of AgreementSpec to ObjectIdentifier.
     */
    private final Map<GordianAgreementSpec, ASN1ObjectIdentifier> theAgree2IdMap;

    /**
     * Map of Identifier to AgreementSpec.
     */
    private final Map<ASN1ObjectIdentifier, GordianAgreementSpec> theId2AgreeMap;

    /**
     * Map of KeyPairSpec to ObjectIdentifier.
     */
    private final Map<GordianKeyPairSpec, ASN1ObjectIdentifier> theKeyPair2IdMap;

    /**
     * Map of Identifier to AgreementSpec.
     */
    private final Map<ASN1ObjectIdentifier, GordianKeyPairSpec> theId2KeyPairMap;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    GordianCoreAgreementAlgId(final GordianFactory pFactory) {
        /* Store parameters */
        theFactory = pFactory;

        /* Create the maps */
        theId2AgreeMap = new HashMap<>();
        theId2KeyPairMap = new HashMap<>();
        theAgree2IdMap = new HashMap<>();
        theKeyPair2IdMap = new HashMap<>();

        /* Add all agreement types and keyPairs */
        addAllAgreements();
        addAllKeyPairs();
    }

    /**
     * Add all agreements to the maps.
     */
    private void addAllAgreements() {
        /* Loop through all the Agreement types */
        final GordianAgreementSpecBuilder myBuilder = GordianCoreAgreementSpecBuilder.newInstance();
        for (GordianAgreementType myType : GordianAgreementType.values()) {
            /* Loop through all the KDF types */
            for (GordianAgreementKDF myKDF : GordianAgreementKDF.values()) {
                /* Add agreements */
                addAgreement(myBuilder.agree(NULLKEYPAIRSPEC, myType, myKDF));
                addAgreement(myBuilder.agree(NULLKEYPAIRSPEC, myType, myKDF, Boolean.TRUE));
            }
        }
    }

    /**
     * Add the agreement to the maps.
     *
     * @param pSpec the agreementSpec
     */
    private void addAgreement(final GordianAgreementSpec pSpec) {
        /* Add branch for agreementType */
        final GordianAgreementType myType = pSpec.getAgreementType();
        ASN1ObjectIdentifier myId = AGREESPECOID.branch(Integer.toString(myType.ordinal() + 1));

        /* Add branch for kdfType */
        final GordianAgreementKDF myKDFType = pSpec.getKDFType();
        myId = myId.branch(Integer.toString(myKDFType.ordinal() + 1));

        /* Add branch for confirm (if present) */
        if (pSpec.withConfirm()) {
            myId = myId.branch("1");
        }

        /* Add the spec to the maps */
        addToMaps(pSpec, myId);
    }

    /**
     * Add all keyPairSpecs to the maps.
     */
    private void addAllKeyPairs() {
        /* Loop through all the Agreement types */
        for (GordianKeyPairSpec mySpec : theFactory.getAsyncFactory().getKeyPairFactory().listPossibleKeySpecs()) {
            /* Add agreements */
            addKeyPair(mySpec);
        }
        final GordianKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
        addKeyPair(myBuilder.composite());
    }

    /**
     * Add the keyPairSpec to the maps.
     *
     * @param pSpec the keyPairSpec
     */
    private void addKeyPair(final GordianKeyPairSpec pSpec) {
        /* Add branch for agreementType */
        final GordianCoreKeyPairSpec mySpec = (GordianCoreKeyPairSpec) pSpec;
        final GordianKeyPairType myType = pSpec.getKeyPairType();
        ASN1ObjectIdentifier myId = KEYPAIRSPECOID.branch(Integer.toString(myType.ordinal() + 1));

        /* switch on type */
        switch (myType) {
            case RSA:
                myId = myId.branch(Integer.toString(mySpec.getRSASpec().getSpec().ordinal() + 1));
                break;
            case DH:
                myId = myId.branch(Integer.toString(mySpec.getDHSpec().getSpec().ordinal() + 1));
                break;
            case XDH:
                myId = myId.branch(Integer.toString(mySpec.getEdwardsSpec().getSpec().ordinal() + 1));
                break;
            case EC:
                myId = myId.branch(Integer.toString(mySpec.getECSpec().getSpec().ordinal() + 1));
                break;
            case SM2:
                myId = myId.branch(Integer.toString(mySpec.getSM2Spec().getSpec().ordinal() + 1));
                break;
            case GOST:
                myId = myId.branch(Integer.toString(mySpec.getGOSTSpec().getSpec().ordinal() + 1));
                break;
            case DSTU:
                myId = myId.branch(Integer.toString(mySpec.getDSTUSpec().getSpec().ordinal() + 1));
                break;
            case CMCE:
                myId = myId.branch(Integer.toString(mySpec.getCMCESpec().getSpec().ordinal() + 1));
                break;
            case FRODO:
                myId = myId.branch(Integer.toString(mySpec.getFRODOSpec().getSpec().ordinal() + 1));
                break;
            case SABER:
                myId = myId.branch(Integer.toString(mySpec.getSABERSpec().getSpec().ordinal() + 1));
                break;
            case MLKEM:
                myId = myId.branch(Integer.toString(mySpec.getMLKEMSpec().getSpec().ordinal() + 1));
                break;
            case HQC:
                myId = myId.branch(Integer.toString(mySpec.getHQCSpec().getSpec().ordinal() + 1));
                break;
            case BIKE:
                myId = myId.branch(Integer.toString(mySpec.getBIKESpec().getSpec().ordinal() + 1));
                break;
            case NTRU:
                myId = myId.branch(Integer.toString(mySpec.getNTRUSpec().getSpec().ordinal() + 1));
                break;
            case NTRUPLUS:
                myId = myId.branch(Integer.toString(mySpec.getNTRUPlusSpec().getSpec().ordinal() + 1));
                break;
            case NTRUPRIME:
                final GordianCoreNTRUPrimeSpec myNTRUPrime = mySpec.getNTRUPrimeSpec();
                myId = myId.branch(Integer.toString(myNTRUPrime.getType().ordinal() + 1));
                myId = myId.branch(Integer.toString(myNTRUPrime.getParams().ordinal() + 1));
                break;
            case NEWHOPE:
            case COMPOSITE:
            default:
                break;
        }

        /* Add the spec to the maps */
        addToMaps(pSpec, myId);
    }

    /**
     * Determine Identifier for an agreementSpec.
     *
     * @param pSpec the agreementSpec
     * @return the identifier
     */
    public AlgorithmIdentifier determineIdentifier(final GordianAgreementSpec pSpec) {
        /* Add branch for agreementType */
        final GordianKeyPairSpecBuilder myKeyPairBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
        final GordianAgreementSpecBuilder myBuilder = GordianCoreAgreementSpecBuilder.newInstance();
        final GordianAgreementSpec myPartial = myBuilder.agree(NULLKEYPAIRSPEC, pSpec.getAgreementType(),
                pSpec.getKDFType(), pSpec.withConfirm());
        final ASN1ObjectIdentifier myAgreeId = theAgree2IdMap.get(myPartial);

        /* Handle composite keyPairSpec */
        final GordianCoreKeyPairSpec mySpec = (GordianCoreKeyPairSpec) pSpec.getKeyPairSpec();
        if (mySpec.getKeyPairType() == GordianKeyPairType.COMPOSITE) {
            final ASN1EncodableVector v = new ASN1EncodableVector();
            final ASN1ObjectIdentifier myId = theKeyPair2IdMap.get(myKeyPairBuilder.composite());
            final Iterator<GordianKeyPairSpec> myIterator = mySpec.keySpecIterator();
            while (myIterator.hasNext()) {
                final GordianKeyPairSpec myPair = myIterator.next();
                v.add(theKeyPair2IdMap.get(myPair));
            }
            final AlgorithmIdentifier myPairId = new AlgorithmIdentifier(myId, new DERSequence(v));
            return new AlgorithmIdentifier(myAgreeId, myPairId);

        } else {
            return new AlgorithmIdentifier(myAgreeId, new AlgorithmIdentifier(theKeyPair2IdMap.get(pSpec.getKeyPairSpec())));
        }
    }

    /**
     * DetermineIdentifier the agreementSpec for an AlgorithmIdentifier.
     *
     * @param pId the identifier
     * @return the agreementSpec
     */
    public GordianAgreementSpec determineAgreementSpec(final AlgorithmIdentifier pId) {
        /* Obtain the ObjectIdentifiers */
        final ASN1ObjectIdentifier myId = pId.getAlgorithm();
        final AlgorithmIdentifier myPairId = AlgorithmIdentifier.getInstance(pId.getParameters());

        /* Derive the specs */
        final GordianAgreementSpecBuilder myBuilder = GordianCoreAgreementSpecBuilder.newInstance();
        final GordianAgreementSpec mySpec = theId2AgreeMap.get(myId);
        final GordianKeyPairSpec myPairSpec = theId2KeyPairMap.get(myPairId.getAlgorithm());
        if (myPairSpec.getKeyPairType() == GordianKeyPairType.COMPOSITE) {
            final ASN1Sequence myPairs = ASN1Sequence.getInstance(myPairId.getParameters());
            final List<GordianKeyPairSpec> mySpecs = new ArrayList<>();
            final Enumeration<?> en = myPairs.getObjects();
            while (en.hasMoreElements()) {
                mySpecs.add(theId2KeyPairMap.get(ASN1ObjectIdentifier.getInstance(en.nextElement())));
            }
            final GordianKeyPairSpecBuilder myKeyPairBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            return myBuilder.agree(myKeyPairBuilder.composite(mySpecs), mySpec.getAgreementType(), mySpec.getKDFType(), mySpec.withConfirm());
        }

        /* Return the AgreementSpec */
        return myBuilder.agree(myPairSpec, mySpec.getAgreementType(), mySpec.getKDFType(), mySpec.withConfirm());
    }

    /**
     * Add agreement to maps.
     *
     * @param pSpec       the agreementSpec
     * @param pIdentifier the identifier
     */
    private void addToMaps(final GordianAgreementSpec pSpec,
                           final ASN1ObjectIdentifier pIdentifier) {
        theAgree2IdMap.put(pSpec, pIdentifier);
        theId2AgreeMap.put(pIdentifier, pSpec);
    }

    /**
     * Add keyPair to maps.
     *
     * @param pSpec       the keyPairSpec
     * @param pIdentifier the identifier
     */
    private void addToMaps(final GordianKeyPairSpec pSpec,
                           final ASN1ObjectIdentifier pIdentifier) {
        theKeyPair2IdMap.put(pSpec, pIdentifier);
        theId2KeyPairMap.put(pIdentifier, pSpec);
    }

    /**
     * Obtain identifier for result.
     *
     * @param pResultType the result type
     * @return the identifier
     * @throws GordianException on error
     */
    protected AlgorithmIdentifier getIdentifierForResult(final Object pResultType) throws GordianException {
        if (pResultType instanceof GordianFactoryType) {
            final ASN1ObjectIdentifier myOID = pResultType == GordianFactoryType.BC
                    ? GordianBaseData.BCFACTORYOID
                    : GordianBaseData.JCAFACTORYOID;
            return new AlgorithmIdentifier(myOID, null);
        }
        if (pResultType instanceof GordianKeySetSpec mySpec) {
            final GordianKeySetSpecASN1 myParms = new GordianKeySetSpecASN1(mySpec);
            return myParms.getAlgorithmId();
        }
        if (pResultType instanceof GordianSymCipherSpec mySpec) {
            final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) theFactory.getCipherFactory();
            return myCipherFactory.getIdentifierForSpec(mySpec);
        }
        if (pResultType instanceof GordianStreamCipherSpec mySpec) {
            final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) theFactory.getCipherFactory();
            return myCipherFactory.getIdentifierForSpec(mySpec);
        }
        if (pResultType instanceof Integer myInt) {
            return new AlgorithmIdentifier(BYTERESULTOID, new ASN1Integer(myInt));
        }
        if (pResultType instanceof String myError) {
            return new AlgorithmIdentifier(ERRORRESULTOID, new DERGeneralString(myError));
        }
        throw new GordianDataException("Illegal resultType set");
    }

    /**
     * process result algorithmId.
     *
     * @param pResId the result algorithmId.
     * @return the resultType
     * @throws GordianException on error
     */
    public Object processResultIdentifier(final AlgorithmIdentifier pResId) throws GordianException {
        /* Look for a Factory */
        final ASN1ObjectIdentifier myAlgId = pResId.getAlgorithm();
        if (GordianBaseData.BCFACTORYOID.equals(myAlgId)) {
            return GordianFactoryType.BC;
        }
        if (GordianBaseData.JCAFACTORYOID.equals(myAlgId)) {
            return GordianFactoryType.JCA;
        }

        /* Look for a keySet Spec */
        if (GordianKeySetSpecASN1.KEYSETALGID.equals(myAlgId)) {
            return GordianKeySetSpecASN1.getInstance(pResId.getParameters()).getSpec();
        }

        /* Look for a cipher Spec */
        final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) theFactory.getCipherFactory();
        final GordianCipherSpec<?> mySpec = myCipherFactory.getCipherSpecForIdentifier(pResId);
        if (mySpec != null) {
            return mySpec;
        }

        /* Look for a Byte Type */
        if (BYTERESULTOID.equals(myAlgId)) {
            return ASN1Integer.getInstance(pResId.getParameters()).intValueExact();
        }

        /* Look for an error */
        if (ERRORRESULTOID.equals(myAlgId)) {
            return ASN1GeneralString.getInstance(pResId.getParameters()).getString();
        }

        /* Unrecognised */
        throw new GordianDataException("Unrecognised resultType");
    }
}
