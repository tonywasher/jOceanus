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
package net.sourceforge.joceanus.jgordianknot.impl.core.keypairset;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairSetAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKeyPairSet algorithmIds.
 */
public final class GordianKeyPairSetAlgId {
    /**
     * KeySetOID branch.
     */
    private static final ASN1ObjectIdentifier KEYPAIRSETOID = GordianASN1Util.EXTOID.branch("2");

    /**
     * The algorithm error.
     */
    private static final String ERROR_ALGO = "Unrecognised algorithm";

    /**
     * The KeyPairSetSpec to Id map.
     */
    private static final Map<GordianKeyPairSetSpec, AlgorithmIdentifier> KEYPAIRALGIDS = new EnumMap<>(GordianKeyPairSetSpec.class);

    /**
     * The Id to KeyPairSetSpec map.
     */
    private static final Map<AlgorithmIdentifier, GordianKeyPairSetSpec> KEYPAIRSPECS = new HashMap<>();

    /**
     * The KeyPairSetSpec to Id map.
     */
    private static final Map<GordianKeyPairSetAgreementSpec, AlgorithmIdentifier> AGREEALGIDS = new HashMap<>();

    /**
     * The Id to KeyPairSetSpec map.
     */
    private static final Map<AlgorithmIdentifier, GordianKeyPairSetAgreementSpec> AGREESPECS = new HashMap<>();

    /* Initialise maps. */
    static {
        /* Declare KeyPairSetSpec algorithms */
        EnumSet.allOf(GordianKeyPairSetSpec.class)
                .forEach(GordianKeyPairSetAlgId::declareKeyPairSetSpec);

        /* Declare KeyPairSetAlgorithmSpec algorithms */
        GordianKeyPairSetAgreementSpec.listPossibleAgreements()
                .forEach(GordianKeyPairSetAlgId::declareAgreementSpec);
    }

    /**
     * Private constructor.
     */
    private GordianKeyPairSetAlgId() {
    }

    /**
     * Obtain KeyPairSetSpec from X509KeySpec.
     * @param pEncoded X509 keySpec
     * @return the keyPairSetSpec
     * @throws OceanusException on error
     */
    static GordianKeyPairSetSpec determineKeyPairSetSpec(final PKCS8EncodedKeySpec pEncoded) throws OceanusException {
        /* Determine the algorithm Id. */
        final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pEncoded.getEncoded());
        final AlgorithmIdentifier myId = myInfo.getPrivateKeyAlgorithm();
        return determineKeyPairSetSpec(myId);
    }

    /**
     * Obtain KeyPairSetSpec from X509KeySpec.
     * @param pEncoded X509 keySpec
     * @return the keyPairSetSpec
     * @throws OceanusException on error
     */
    static GordianKeyPairSetSpec determineKeyPairSetSpec(final X509EncodedKeySpec pEncoded) throws OceanusException {
        /* Determine the algorithm Id. */
        final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncoded.getEncoded());
        final AlgorithmIdentifier myId = myInfo.getAlgorithm();
        return determineKeyPairSetSpec(myId);
    }

    /**
     * Obtain KeyPairSetSpec from algorithmId.
     * @param pAlgId algorithmIdc
     * @return the keyPairSetSpec
     * @throws OceanusException on error
     */
    public static GordianKeyPairSetSpec determineKeyPairSetSpec(final AlgorithmIdentifier pAlgId) throws OceanusException {
        /* Obtain the spec */
        final GordianKeyPairSetSpec mySpec = KEYPAIRSPECS.get(pAlgId);
        if (mySpec != null) {
            return mySpec;
        }
        throw new GordianDataException(ERROR_ALGO);
    }

    /**
     * Obtain KeyPairSetSpec from algorithmId.
     * @param pAlgId algorithmIdc
     * @return the keyPairSetSpec
     */
    public static GordianKeyPairSetSpec lookUpKeyPairSetSpec(final AlgorithmIdentifier pAlgId) {
        return KEYPAIRSPECS.get(pAlgId);
    }

    /**
     * Obtain AlgorithmId from KeyPairSetSpec.
     * @param pSpec the keyPairSetSpec
     * @return the algorithmId
     */
    public static AlgorithmIdentifier determineAlgorithmId(final GordianKeyPairSetSpec pSpec) {
        /* Obtain the algorithmId */
        return KEYPAIRALGIDS.get(pSpec);
    }

    /**
     * Obtain KeyPairSetSpec from algorithmId.
     * @param pAlgId algorithmIdc
     * @return the keyPairSetSpec
     * @throws OceanusException on error
     */
    public static GordianKeyPairSetAgreementSpec determineKeyPairSetAgreementSpec(final AlgorithmIdentifier pAlgId) throws OceanusException {
        /* Obtain the spec */
        final GordianKeyPairSetAgreementSpec mySpec = AGREESPECS.get(pAlgId);
        if (mySpec != null) {
            return mySpec;
        }
        throw new GordianDataException(ERROR_ALGO);
    }

    /**
     * Obtain AlgorithmId from KeyPairSetSpec.
     * @param pSpec the keyPairSetSpec
     * @return the algorithmId
     */
    public static AlgorithmIdentifier determineAlgorithmId(final GordianKeyPairSetAgreementSpec pSpec) {
        /* Obtain the algorithmId */
        return AGREEALGIDS.get(pSpec);
    }

    /**
     * Declare KeyPairSetSpec.
     * @param pSpec the spec.
     */
    private static void declareKeyPairSetSpec(final GordianKeyPairSetSpec pSpec) {
        /* Determine the id */
        ASN1ObjectIdentifier myId = KEYPAIRSETOID.branch("1");
        myId = myId.branch(Integer.toString(pSpec.ordinal() + 1));
        final AlgorithmIdentifier myAlgId = new AlgorithmIdentifier(myId, DERNull.INSTANCE);

        /* Add to maps */
        KEYPAIRALGIDS.put(pSpec, myAlgId);
        KEYPAIRSPECS.put(myAlgId, pSpec);
    }

    /**
     * Declare KeyPairSetAgreementSpec.
     * @param pSpec the spec.
     */
    private static void declareAgreementSpec(final GordianKeyPairSetAgreementSpec pSpec) {
        /* Access details */
        final GordianKeyPairSetSpec mySpec = pSpec.getKeyPairSetSpec();
        final GordianAgreementType myType = pSpec.getAgreementType();

        /* Determine the id */
        ASN1ObjectIdentifier myId = KEYPAIRSETOID.branch("2");
        myId = myId.branch(Integer.toString(mySpec.ordinal() + 1));
        myId = myId.branch(Integer.toString(myType.ordinal() + 1));

        /* Add branch for confirm (if present) */
        if (Boolean.TRUE.equals(pSpec.withConfirm())) {
            myId = myId.branch("1");
        }

        /* Create algorithmId */
        final AlgorithmIdentifier myAlgId = new AlgorithmIdentifier(myId, DERNull.INSTANCE);

        /* Add to maps */
        AGREEALGIDS.put(pSpec, myAlgId);
        AGREESPECS.put(myAlgId, pSpec);
    }
}
