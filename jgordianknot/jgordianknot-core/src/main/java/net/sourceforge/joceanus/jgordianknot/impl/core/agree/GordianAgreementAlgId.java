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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDSAElliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDSTU4145Elliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianGOSTElliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianLMSKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianLMSKeySpec.GordianHSSKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianNTRUPrimeSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianSM2Elliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianXMSSKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianKeySetSpecASN1;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Mappings from EncodedId to AgreementSpec.
 */
public class GordianAgreementAlgId {
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
     * Null Result OID branch.
     */
    static final ASN1ObjectIdentifier NULLRESULTOID = AGREEOID.branch("3");

    /**
     * Null KeyPairSpec for Partial AgreementSpec.
     */
    private static final GordianKeyPairSpec NULLKEYPAIRSPEC = GordianKeyPairSpec.ed448();

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
     * @param pFactory the factory
     */
    GordianAgreementAlgId(final GordianFactory pFactory) {
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
        for (GordianAgreementType myType : GordianAgreementType.values()) {
            /* Loop through all the KDF types */
            for (GordianKDFType myKDF : GordianKDFType.values()) {
                /* Add agreements */
                addAgreement(new GordianAgreementSpec(NULLKEYPAIRSPEC, myType, myKDF));
                addAgreement(new GordianAgreementSpec(NULLKEYPAIRSPEC, myType, myKDF, Boolean.TRUE));
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
        final GordianKDFType myKDFType = pSpec.getKDFType();
        myId = myId.branch(Integer.toString(myKDFType.ordinal() + 1));

        /* Add branch for confirm (if present) */
        if (Boolean.TRUE.equals(pSpec.withConfirm())) {
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
        for (GordianKeyPairSpec mySpec : theFactory.getKeyPairFactory().listPossibleKeySpecs()) {
            /* Add agreements */
            addKeyPair(mySpec);
        }
        addKeyPair(GordianKeyPairSpec.composite());
    }

    /**
     * Add the keyPairSpec to the maps.
     *
     * @param pSpec the keyPairSpec
     */
    private void addKeyPair(final GordianKeyPairSpec pSpec) {
        /* Add branch for agreementType */
        final GordianKeyPairType myType = pSpec.getKeyPairType();
        ASN1ObjectIdentifier myId = KEYPAIRSPECOID.branch(Integer.toString(myType.ordinal() + 1));

        /* switch on type */
        switch (myType) {
            case RSA:
                myId = myId.branch(Integer.toString(pSpec.getRSAModulus().ordinal() + 1));
                break;
            case DH:
            case ELGAMAL:
                myId = myId.branch(Integer.toString(pSpec.getDHGroup().ordinal() + 1));
                break;
            case DSA:
                myId = myId.branch(Integer.toString(pSpec.getDSAKeyType().ordinal() + 1));
                break;
            case XDH:
            case EDDSA:
                myId = myId.branch(Integer.toString(pSpec.getEdwardsElliptic().ordinal() + 1));
                break;
            case EC:
                myId = myId.branch(Integer.toString(((GordianDSAElliptic) pSpec.getElliptic()).ordinal() + 1));
                break;
            case SM2:
                myId = myId.branch(Integer.toString(((GordianSM2Elliptic) pSpec.getElliptic()).ordinal() + 1));
                break;
            case GOST2012:
                myId = myId.branch(Integer.toString(((GordianGOSTElliptic) pSpec.getElliptic()).ordinal() + 1));
                break;
            case DSTU4145:
                myId = myId.branch(Integer.toString(((GordianDSTU4145Elliptic) pSpec.getElliptic()).ordinal() + 1));
                break;
            case SPHINCSPLUS:
                myId = myId.branch(Integer.toString(pSpec.getSPHINCSPlusKeySpec().ordinal() + 1));
                break;
            case CMCE:
                myId = myId.branch(Integer.toString(pSpec.getCMCEKeySpec().ordinal() + 1));
                break;
            case FRODO:
                myId = myId.branch(Integer.toString(pSpec.getFRODOKeySpec().ordinal() + 1));
                break;
            case SABER:
                myId = myId.branch(Integer.toString(pSpec.getSABERKeySpec().ordinal() + 1));
                break;
            case KYBER:
                myId = myId.branch(Integer.toString(pSpec.getKyberKeySpec().ordinal() + 1));
                break;
            case HQC:
                myId = myId.branch(Integer.toString(pSpec.getHQCKeySpec().ordinal() + 1));
                break;
            case BIKE:
                myId = myId.branch(Integer.toString(pSpec.getBIKEKeySpec().ordinal() + 1));
                break;
            case NTRU:
                myId = myId.branch(Integer.toString(pSpec.getNTRUKeySpec().ordinal() + 1));
                break;
            case NTRUPRIME:
                final GordianNTRUPrimeSpec myNTRUPrime = pSpec.getNTRUPrimeKeySpec();
                myId = myId.branch(Integer.toString(myNTRUPrime.getType().ordinal() + 1));
                myId = myId.branch(Integer.toString(myNTRUPrime.getParams().ordinal() + 1));
                break;
            case XMSS:
                final GordianXMSSKeySpec myXMSS = pSpec.getXMSSKeySpec();
                myId = myId.branch(Integer.toString(myXMSS.getKeyType().ordinal() + 1));
                myId = myId.branch(Integer.toString(myXMSS.getDigestType().ordinal() + 1));
                myId = myId.branch(Integer.toString(myXMSS.getHeight().ordinal() + 1));
                if (myXMSS.getLayers() != null) {
                    myId = myId.branch(Integer.toString(myXMSS.getLayers().ordinal() + 1));
                }
                break;
            case LMS:
                final Object mySubType = pSpec.getSubKeyType();
                if (mySubType instanceof GordianLMSKeySpec) {
                    final GordianLMSKeySpec myLMS = (GordianLMSKeySpec) mySubType;
                    myId = myId.branch(Integer.toString(myLMS.getHash().ordinal() + 1));
                    myId = myId.branch(Integer.toString(myLMS.getHeight().ordinal() + 1));
                    myId = myId.branch(Integer.toString(myLMS.getWidth().ordinal() + 1));
                    myId = myId.branch(Integer.toString(myLMS.getLength().ordinal() + 1));
                } else if (mySubType instanceof GordianHSSKeySpec) {
                    final GordianHSSKeySpec myHSS = (GordianHSSKeySpec) mySubType;
                    final GordianLMSKeySpec myLMS = myHSS.getKeySpec();
                    myId = myId.branch(Integer.toString(myLMS.getHash().ordinal() + 1));
                    myId = myId.branch(Integer.toString(myLMS.getHeight().ordinal() + 1));
                    myId = myId.branch(Integer.toString(myLMS.getWidth().ordinal() + 1));
                    myId = myId.branch(Integer.toString(myLMS.getLength().ordinal() + 1));
                    myId = myId.branch(Integer.toString(myHSS.getTreeDepth()));
                }
                break;
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
        final GordianAgreementSpec myPartial = new GordianAgreementSpec(NULLKEYPAIRSPEC, pSpec.getAgreementType(),
                                                                        pSpec.getKDFType(), pSpec.withConfirm());
        final ASN1ObjectIdentifier myAgreeId = theAgree2IdMap.get(myPartial);

        /* Handle composite keyPairSpec */
        final GordianKeyPairSpec mySpec = pSpec.getKeyPairSpec();
        if (mySpec.getKeyPairType() == GordianKeyPairType.COMPOSITE) {
            final ASN1EncodableVector v = new ASN1EncodableVector();
            final ASN1ObjectIdentifier myId = theKeyPair2IdMap.get(GordianKeyPairSpec.composite());
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
        final ASN1ObjectIdentifier myId  = pId.getAlgorithm();
        final AlgorithmIdentifier myPairId = AlgorithmIdentifier.getInstance(pId.getParameters());

        /* Derive the specs */
        final GordianAgreementSpec mySpec = theId2AgreeMap.get(myId);
        final GordianKeyPairSpec myPairSpec = theId2KeyPairMap.get(myPairId.getAlgorithm());
        if (myPairSpec.getKeyPairType() == GordianKeyPairType.COMPOSITE) {
            final ASN1Sequence myPairs = ASN1Sequence.getInstance(myPairId.getParameters());
            final List<GordianKeyPairSpec> mySpecs = new ArrayList<>();
            final Enumeration<?> en = myPairs.getObjects();
            while (en.hasMoreElements()) {
                mySpecs.add(theId2KeyPairMap.get(ASN1ObjectIdentifier.getInstance(en.nextElement())));
            }
            return new GordianAgreementSpec(GordianKeyPairSpec.composite(mySpecs), mySpec.getAgreementType(), mySpec.getKDFType(), mySpec.withConfirm());
        }

        /* Return the AgreementSpec */
        return new GordianAgreementSpec(myPairSpec, mySpec.getAgreementType(), mySpec.getKDFType(), mySpec.withConfirm());
    }

    /**
     * Add agreement to maps.
     * @param pSpec the agreementSpec
     * @param pIdentifier the identifier
     */
    private void addToMaps(final GordianAgreementSpec pSpec,
                           final ASN1ObjectIdentifier pIdentifier) {
        theAgree2IdMap.put(pSpec, pIdentifier);
        theId2AgreeMap.put(pIdentifier, pSpec);
    }

    /**
     * Add keyPair to maps.
     * @param pSpec the keyPairSpec
     * @param pIdentifier the identifier
     */
    private void addToMaps(final GordianKeyPairSpec pSpec,
                           final ASN1ObjectIdentifier pIdentifier) {
        theKeyPair2IdMap.put(pSpec, pIdentifier);
        theId2KeyPairMap.put(pIdentifier, pSpec);
    }
    /**
     * Obtain identifier for result.
     * @param pResultType the result type
     * @return the identifier
     * @throws OceanusException on error
     */
    protected AlgorithmIdentifier getIdentifierForResult(final Object pResultType) throws OceanusException {
        if (pResultType instanceof GordianFactoryType) {
            final ASN1ObjectIdentifier myOID = pResultType == GordianFactoryType.BC
                    ? GordianCoreFactory.BCFACTORYOID
                    : GordianCoreFactory.JCAFACTORYOID;
            return new AlgorithmIdentifier(myOID, null);
        }
        if (pResultType instanceof GordianKeySetSpec) {
            final GordianKeySetSpecASN1 myParms = new GordianKeySetSpecASN1((GordianKeySetSpec) pResultType);
            return myParms.getAlgorithmId();
        }
        if (pResultType instanceof GordianSymCipherSpec) {
            final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) theFactory.getCipherFactory();
            return myCipherFactory.getIdentifierForSpec((GordianSymCipherSpec) pResultType);
        }
        if (pResultType instanceof GordianStreamCipherSpec) {
            final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) theFactory.getCipherFactory();
            return myCipherFactory.getIdentifierForSpec((GordianStreamCipherSpec) pResultType);
        }
        if (pResultType == null) {
            return new AlgorithmIdentifier(NULLRESULTOID, null);
        }
        throw new GordianDataException("Illegal resultType set");
    }

    /**
     * process result algorithmId.
     * @param pResId the result algorithmId.
     * @return the resultType
     * @throws OceanusException on error
     */
    public Object  processResultIdentifier(final AlgorithmIdentifier pResId) throws OceanusException {
        /* Look for a Factory */
        final ASN1ObjectIdentifier myAlgId = pResId.getAlgorithm();
        if (GordianCoreFactory.BCFACTORYOID.equals(myAlgId)) {
            return GordianFactoryType.BC;
        }
        if (GordianCoreFactory.JCAFACTORYOID.equals(myAlgId)) {
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

        /* Look for a Factory */
        if (NULLRESULTOID.equals(myAlgId)) {
            return null;
        }
        throw new GordianDataException("Unrecognised resultType");
    }
}
