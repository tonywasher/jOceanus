/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianECSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianHybridKEMSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianMLKEMSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianRSASpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

import java.util.EnumMap;
import java.util.Map;

/**
 * KEM Hybrid Spec.
 */
public final class GordianCoreHybridKEMSpec
        implements GordianHybridSpec, GordianCoreKeyPairIdSpec<GordianHybridKEMSpec> {
    /**
     * The MLKEM PrivateSeed Length.
     */
    private static final int MLKEM_PRIVATE_SEED_LENGTH = 64;

    /**
     * The MLKEM768 PublicSeed Length.
     */
    private static final int MLKEM768_PUBLIC_SEED_LENGTH = 1184;

    /**
     * The MLKEM1024 PublicSeed Length.
     */
    private static final int MLKEM1024_PUBLIC_SEED_LENGTH = 1568;

    /**
     * The specMap.
     */
    private static final Map<GordianHybridKEMSpec, GordianCoreHybridKEMSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreHybridKEMSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreHybridKEMSpec[0]);

    /**
     * The Spec.
     */
    private final GordianHybridKEMSpec theSpec;

    /**
     * The KeyPair Builder.
     */
    private final GordianKeyPairSpecBuilder theBuilder = GordianCoreKeyPairSpecBuilder.newInstance();

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreHybridKEMSpec(final GordianHybridKEMSpec pSpec) {
        theSpec = pSpec;
    }

    @Override
    public GordianKeyPairType getKeyPairType() {
        return GordianKeyPairType.HYBRIDKEM;
    }

    @Override
    public GordianHybridKEMSpec getSpec() {
        return theSpec;
    }

    @Override
    public GordianKeyPairSpec getPrimaryKeyPairSpec() {
        return switch (theSpec) {
            case MLKEM768_RSA2048, MLKEM768_RSA3072, MLKEM768_RSA4096, MLKEM768_ECDH_P256,
                 MLKEM768_ECDH_P384, MLKEM768_ECDH_BP256, MLKEM768_X25519 ->
                    theBuilder.mlkem(GordianMLKEMSpec.MLKEM768);
            case MLKEM1024_RSA3072, MLKEM1024_ECDH_P384, MLKEM1024_ECDH_BP384, MLKEM1024_ECDH_P521, MLKEM1024_X448 ->
                    theBuilder.mlkem(GordianMLKEMSpec.MLKEM1024);
        };
    }

    @Override
    public GordianKeyPairSpec getTraditionalKeyPairSpec() {
        return switch (theSpec) {
            case MLKEM768_RSA2048 -> theBuilder.rsa(GordianRSASpec.MOD2048);
            case MLKEM768_RSA3072, MLKEM1024_RSA3072 -> theBuilder.rsa(GordianRSASpec.MOD3072);
            case MLKEM768_RSA4096 -> theBuilder.rsa(GordianRSASpec.MOD4096);
            case MLKEM768_ECDH_P256 -> theBuilder.ec(GordianECSpec.SECP256R1);
            case MLKEM768_ECDH_P384, MLKEM1024_ECDH_P384 -> theBuilder.ec(GordianECSpec.SECP384R1);
            case MLKEM768_ECDH_BP256 -> theBuilder.ec(GordianECSpec.BRAINPOOLP256R1);
            case MLKEM768_X25519 -> theBuilder.x25519();
            case MLKEM1024_ECDH_BP384 -> theBuilder.ec(GordianECSpec.BRAINPOOLP384R1);
            case MLKEM1024_ECDH_P521 -> theBuilder.ec(GordianECSpec.SECP521R1);
            case MLKEM1024_X448 -> theBuilder.x448();
        };
    }

    @Override
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case MLKEM768_RSA2048 -> IANAObjectIdentifiers.id_MLKEM768_RSA2048_SHA3_256;
            case MLKEM768_RSA3072 -> IANAObjectIdentifiers.id_MLKEM768_RSA3072_SHA3_256;
            case MLKEM768_RSA4096 -> IANAObjectIdentifiers.id_MLKEM768_RSA4096_SHA3_256;
            case MLKEM768_ECDH_P256 -> IANAObjectIdentifiers.id_MLKEM768_ECDH_P256_SHA3_256;
            case MLKEM768_ECDH_P384 -> IANAObjectIdentifiers.id_MLKEM768_ECDH_P384_SHA3_256;
            case MLKEM768_ECDH_BP256 -> IANAObjectIdentifiers.id_MLKEM768_ECDH_BP256_SHA3_256;
            case MLKEM768_X25519 -> IANAObjectIdentifiers.id_MLKEM768_X25519_SHA3_256;
            case MLKEM1024_RSA3072 -> IANAObjectIdentifiers.id_MLKEM1024_RSA3072_SHA3_256;
            case MLKEM1024_ECDH_P384 -> IANAObjectIdentifiers.id_MLKEM1024_ECDH_P384_SHA3_256;
            case MLKEM1024_ECDH_BP384 -> IANAObjectIdentifiers.id_MLKEM1024_ECDH_BP384_SHA3_256;
            case MLKEM1024_ECDH_P521 -> IANAObjectIdentifiers.id_MLKEM1024_ECDH_P521_SHA3_256;
            case MLKEM1024_X448 -> IANAObjectIdentifiers.id_MLKEM1024_X448_SHA3_256;
        };
    }

    @Override
    public AlgorithmIdentifier getPrimaryIdentifier() {
        return switch (theSpec) {
            case MLKEM768_RSA2048, MLKEM768_RSA3072, MLKEM768_RSA4096, MLKEM768_ECDH_P256,
                 MLKEM768_ECDH_P384, MLKEM768_ECDH_BP256, MLKEM768_X25519 ->
                    new AlgorithmIdentifier(NISTObjectIdentifiers.id_alg_ml_kem_768);
            case MLKEM1024_RSA3072, MLKEM1024_ECDH_P384, MLKEM1024_ECDH_BP384,
                 MLKEM1024_ECDH_P521, MLKEM1024_X448 ->
                    new AlgorithmIdentifier(NISTObjectIdentifiers.id_alg_ml_kem_1024);
        };
    }

    @Override
    public AlgorithmIdentifier getSecondaryIdentifier() {
        return switch (theSpec) {
            case MLKEM768_RSA2048, MLKEM768_RSA3072, MLKEM768_RSA4096, MLKEM1024_RSA3072 ->
                    new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption);
            case MLKEM768_ECDH_P256 ->
                    new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, SECObjectIdentifiers.secp256r1);
            case MLKEM768_ECDH_P384, MLKEM1024_ECDH_P384 ->
                    new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, SECObjectIdentifiers.secp384r1);
            case MLKEM768_ECDH_BP256 ->
                    new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, TeleTrusTObjectIdentifiers.brainpoolP256r1);
            case MLKEM768_X25519 -> new AlgorithmIdentifier(EdECObjectIdentifiers.id_X25519);
            case MLKEM1024_ECDH_BP384 ->
                    new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, TeleTrusTObjectIdentifiers.brainpoolP384r1);
            case MLKEM1024_ECDH_P521 ->
                    new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, SECObjectIdentifiers.secp521r1);
            case MLKEM1024_X448 -> new AlgorithmIdentifier(EdECObjectIdentifiers.id_X448);
        };
    }

    @Override
    public byte[] getLabel() {
        return switch (theSpec) {
            case MLKEM768_RSA2048 -> Strings.toByteArray("MLKEM768-RSAOAEP2048");
            case MLKEM768_RSA3072 -> Strings.toByteArray("MLKEM768-RSAOAEP3072");
            case MLKEM768_RSA4096 -> Strings.toByteArray("MLKEM768-RSAOAEP4096");
            case MLKEM768_ECDH_P256 -> Strings.toByteArray("MLKEM768-P256");
            case MLKEM768_ECDH_P384 -> Strings.toByteArray("MLKEM768-P384");
            case MLKEM768_ECDH_BP256 -> Strings.toByteArray("MLKEM768-BP256");
            case MLKEM768_X25519 -> Hex.decode("5c2e2f2f5e5c");
            case MLKEM1024_RSA3072 -> Strings.toByteArray("MLKEM1024-RSAOAEP3072");
            case MLKEM1024_ECDH_P384 -> Strings.toByteArray("MLKEM1024-P384");
            case MLKEM1024_ECDH_BP384 -> Strings.toByteArray("MLKEM1024-BP384");
            case MLKEM1024_ECDH_P521 -> Strings.toByteArray("MLKEM1024-P521");
            case MLKEM1024_X448 -> Strings.toByteArray("MLKEM1024-X448");
        };
    }

    @Override
    public String getJCAName() {
        return switch (theSpec) {
            case MLKEM768_RSA2048 -> "MLKEM768-RSA2048-SHA3-256";
            case MLKEM768_RSA3072 -> "MLKEM768-RSA3072-SHA3-256";
            case MLKEM768_RSA4096 -> "MLKEM768-RSA4096-SHA3-256";
            case MLKEM768_ECDH_P256 -> "MLKEM768-ECDH-P256-SHA3-256";
            case MLKEM768_ECDH_P384 -> "MLKEM768-ECDH-P384-SHA3-256";
            case MLKEM768_ECDH_BP256 -> "MLKEM768-ECDH-BP256-SHA3-256";
            case MLKEM768_X25519 -> "MLKEM768-X25519-SHA3-256";
            case MLKEM1024_RSA3072 -> "MLKEM1024-RSA3072-SHA3-256";
            case MLKEM1024_ECDH_P384 -> "MLKEM1024-ECDH-P384-SHA3-256";
            case MLKEM1024_ECDH_BP384 -> "MLKEM1024-ECDH-BP384-SHA3-256";
            case MLKEM1024_ECDH_P521 -> "MLKEM1024-ECDH-P521-SHA3-256";
            case MLKEM1024_X448 -> "MLKEM1024-X448-SHA3-256";
        };
    }

    @Override
    public int getPrivateSeedLength() {
        return MLKEM_PRIVATE_SEED_LENGTH;
    }

    @Override
    public int getPublicSeedLength() {
        return switch (theSpec) {
            case MLKEM768_RSA2048, MLKEM768_RSA3072, MLKEM768_RSA4096,
                 MLKEM768_ECDH_P256, MLKEM768_ECDH_P384, MLKEM768_ECDH_BP256,
                 MLKEM768_X25519 -> MLKEM768_PUBLIC_SEED_LENGTH;
            case MLKEM1024_RSA3072, MLKEM1024_ECDH_P384, MLKEM1024_ECDH_BP384,
                 MLKEM1024_ECDH_P521, MLKEM1024_X448 -> MLKEM1024_PUBLIC_SEED_LENGTH;
        };
    }

    @Override
    public String toString() {
        return theSpec.toString();
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check subFields */
        return pThat instanceof GordianCoreHybridKEMSpec myThat
                && theSpec == myThat.getSpec();
    }

    @Override
    public int hashCode() {
        return theSpec.hashCode();
    }

    /**
     * Obtain the core spec.
     *
     * @param pSpec the base spec
     * @return the core spec
     */
    public static GordianCoreHybridKEMSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianHybridKEMSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianHybridKEMSpec, GordianCoreHybridKEMSpec> newSpecMap() {
        final Map<GordianHybridKEMSpec, GordianCoreHybridKEMSpec> myMap = new EnumMap<>(GordianHybridKEMSpec.class);
        for (GordianHybridKEMSpec mySpec : GordianHybridKEMSpec.values()) {
            myMap.put(mySpec, new GordianCoreHybridKEMSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreHybridKEMSpec[] values() {
        return VALUES;
    }
}
