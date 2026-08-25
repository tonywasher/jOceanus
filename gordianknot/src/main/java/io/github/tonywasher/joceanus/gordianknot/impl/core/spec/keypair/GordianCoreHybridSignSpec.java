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

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianECSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianHybridSignSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianMLDSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianRSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.sign.GordianCoreSignatureSpecBuilder;
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

import java.util.EnumMap;
import java.util.Map;

/**
 * Sign Hybrid Spec.
 */
public final class GordianCoreHybridSignSpec
        implements GordianHybridSpec, GordianCoreKeyPairIdSpec<GordianHybridSignSpec> {
    /**
     * The MLDSA PrivateSeed Length.
     */
    private static final int MLDSA_PRIVATE_SEED_LENGTH = 32;

    /**
     * The MLDSA44 PublicSeed Length.
     */
    private static final int MLDSA44_PUBLIC_SEED_LENGTH = 1312;

    /**
     * The MLDSA65 PublicSeed Length.
     */
    private static final int MLDSA65_PUBLIC_SEED_LENGTH = 1952;

    /**
     * The MLDSA87 PublicSeed Length.
     */
    private static final int MLDSA87_PUBLIC_SEED_LENGTH = 2592;

    /**
     * The MLDSA44 Signature Length.
     */
    private static final int MLDSA44_SIGNATURE_LENGTH = 2420;

    /**
     * The MLDSA65 Signature Length.
     */
    private static final int MLDSA65_SIGNATURE_LENGTH = 3309;

    /**
     * The MLDSA87 Signature Length.
     */
    private static final int MLDSA87_SIGNATURE_LENGTH = 4627;

    /**
     * The specMap.
     */
    private static final Map<GordianHybridSignSpec, GordianCoreHybridSignSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreHybridSignSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreHybridSignSpec[0]);

    /**
     * The Spec.
     */
    private final GordianHybridSignSpec theSpec;

    /**
     * The KeyPair Builder.
     */
    private final GordianKeyPairSpecBuilder theKeyPair = GordianCoreKeyPairSpecBuilder.newInstance();

    /**
     * The Signature Builder.
     */
    private final GordianSignatureSpecBuilder theSignature = GordianCoreSignatureSpecBuilder.newInstance();

    /**
     * The Digest Builder.
     */
    private final GordianDigestSpecBuilder theDigest = GordianCoreDigestSpecBuilder.newInstance();

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreHybridSignSpec(final GordianHybridSignSpec pSpec) {
        theSpec = pSpec;
    }

    @Override
    public GordianKeyPairType getKeyPairType() {
        return GordianKeyPairType.HYBRIDSIGN;
    }

    @Override
    public GordianHybridSignSpec getSpec() {
        return theSpec;
    }

    @Override
    public GordianKeyPairSpec getPrimaryKeyPairSpec() {
        return switch (theSpec) {
            case MLDSA44_RSA2048_PSS_SHA256, MLDSA44_RSA2048_PKCS15_SHA256, MLDSA44_ECDSA_P256_SHA256,
                 MLDSA44_ED25519_SHA512 -> theKeyPair.mldsa(GordianMLDSASpec.MLDSA44);
            case MLDSA65_RSA3072_PSS_SHA512, MLDSA65_RSA3072_PKCS15_SHA512, MLDSA65_RSA4096_PSS_SHA512,
                 MLDSA65_RSA4096_PKCS15_SHA512, MLDSA65_ECDSA_P256_SHA512, MLDSA65_ECDSA_P384_SHA512,
                 MLDSA65_ECDSA_BP256_SHA512, MLDSA65_ED25519_SHA512 -> theKeyPair.mldsa(GordianMLDSASpec.MLDSA65);
            case MLDSA87_RSA3072_PSS_SHA512, MLDSA87_RSA4096_PSS_SHA512, MLDSA87_ECDSA_P384_SHA512,
                 MLDSA87_ED448_SHAKE256, MLDSA87_ECDSA_BP384_SHA512, MLDSA87_ECDSA_P521_SHA512 ->
                    theKeyPair.mldsa(GordianMLDSASpec.MLDSA87);
        };
    }

    @Override
    public GordianKeyPairSpec getTraditionalKeyPairSpec() {
        return switch (theSpec) {
            case MLDSA44_RSA2048_PSS_SHA256, MLDSA44_RSA2048_PKCS15_SHA256 -> theKeyPair.rsa(GordianRSASpec.MOD2048);
            case MLDSA44_ECDSA_P256_SHA256, MLDSA65_ECDSA_P256_SHA512 -> theKeyPair.ec(GordianECSpec.SECP256R1);
            case MLDSA44_ED25519_SHA512, MLDSA65_ED25519_SHA512 -> theKeyPair.ed25519();
            case MLDSA65_RSA3072_PSS_SHA512, MLDSA65_RSA3072_PKCS15_SHA512, MLDSA87_RSA3072_PSS_SHA512 ->
                    theKeyPair.rsa(GordianRSASpec.MOD3072);
            case MLDSA65_RSA4096_PSS_SHA512, MLDSA65_RSA4096_PKCS15_SHA512, MLDSA87_RSA4096_PSS_SHA512 ->
                    theKeyPair.rsa(GordianRSASpec.MOD4096);
            case MLDSA65_ECDSA_P384_SHA512, MLDSA87_ECDSA_P384_SHA512 -> theKeyPair.ec(GordianECSpec.SECP384R1);
            case MLDSA65_ECDSA_BP256_SHA512 -> theKeyPair.ec(GordianECSpec.BRAINPOOLP256R1);
            case MLDSA87_ED448_SHAKE256 -> theKeyPair.ed448();
            case MLDSA87_ECDSA_BP384_SHA512 -> theKeyPair.ec(GordianECSpec.BRAINPOOLP384R1);
            case MLDSA87_ECDSA_P521_SHA512 -> theKeyPair.ec(GordianECSpec.SECP521R1);
        };
    }

    /**
     * Obtain the Primary SignatureSpec.
     *
     * @return the Spec
     */
    public GordianSignatureSpec getPrimarySignatureSpec() {
        return theSignature.mlDSA();
    }

    /**
     * Obtain the Traditional SignatureSpec.
     *
     * @return the Spec
     */
    public GordianSignatureSpec getTraditionalSignatureSpec() {
        return switch (theSpec) {
            case MLDSA44_RSA2048_PSS_SHA256, MLDSA65_RSA3072_PSS_SHA512, MLDSA87_RSA3072_PSS_SHA512 ->
                    theSignature.rsa(GordianSignatureType.PSSMGF1, theDigest.sha2(GordianLength.LEN_256));
            case MLDSA44_RSA2048_PKCS15_SHA256, MLDSA65_RSA3072_PKCS15_SHA512 ->
                    theSignature.rsa(GordianSignatureType.PREHASH, theDigest.sha2(GordianLength.LEN_256));
            case MLDSA65_RSA4096_PKCS15_SHA512 ->
                    theSignature.rsa(GordianSignatureType.PREHASH, theDigest.sha2(GordianLength.LEN_384));
            case MLDSA44_ECDSA_P256_SHA256, MLDSA65_ECDSA_P256_SHA512, MLDSA65_ECDSA_BP256_SHA512 ->
                    theSignature.ec(GordianSignatureType.DSA, theDigest.sha2(GordianLength.LEN_256));
            case MLDSA65_ECDSA_P384_SHA512, MLDSA87_ECDSA_P384_SHA512, MLDSA87_ECDSA_BP384_SHA512 ->
                    theSignature.ec(GordianSignatureType.DSA, theDigest.sha2(GordianLength.LEN_384));
            case MLDSA44_ED25519_SHA512, MLDSA65_ED25519_SHA512, MLDSA87_ED448_SHAKE256 -> theSignature.edDSA();
            case MLDSA65_RSA4096_PSS_SHA512, MLDSA87_RSA4096_PSS_SHA512 ->
                    theSignature.rsa(GordianSignatureType.PSSMGF1, theDigest.sha2(GordianLength.LEN_384));
            case MLDSA87_ECDSA_P521_SHA512 ->
                    theSignature.ec(GordianSignatureType.DSA, theDigest.sha2(GordianLength.LEN_512));
        };
    }

    /**
     * Obtain the DigestSpec.
     *
     * @return the Spec
     */
    public GordianDigestSpec getDigestSpec() {
        return switch (theSpec) {
            case MLDSA44_RSA2048_PSS_SHA256, MLDSA44_RSA2048_PKCS15_SHA256, MLDSA44_ECDSA_P256_SHA256 ->
                    theDigest.sha2(GordianLength.LEN_256);
            case MLDSA65_RSA3072_PSS_SHA512, MLDSA87_RSA3072_PSS_SHA512, MLDSA65_RSA3072_PKCS15_SHA512,
                 MLDSA65_RSA4096_PKCS15_SHA512, MLDSA65_ECDSA_P256_SHA512, MLDSA65_ECDSA_BP256_SHA512,
                 MLDSA65_ECDSA_P384_SHA512, MLDSA87_ECDSA_P384_SHA512, MLDSA87_ECDSA_BP384_SHA512,
                 MLDSA44_ED25519_SHA512, MLDSA65_ED25519_SHA512, MLDSA65_RSA4096_PSS_SHA512,
                 MLDSA87_RSA4096_PSS_SHA512, MLDSA87_ECDSA_P521_SHA512 -> theDigest.sha2(GordianLength.LEN_512);
            case MLDSA87_ED448_SHAKE256 -> theDigest.shake256();
        };
    }

    @Override
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case MLDSA44_RSA2048_PSS_SHA256 -> IANAObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256;
            case MLDSA44_RSA2048_PKCS15_SHA256 -> IANAObjectIdentifiers.id_MLDSA44_RSA2048_PKCS15_SHA256;
            case MLDSA44_ECDSA_P256_SHA256 -> IANAObjectIdentifiers.id_MLDSA44_ECDSA_P256_SHA256;
            case MLDSA44_ED25519_SHA512 -> IANAObjectIdentifiers.id_MLDSA44_Ed25519_SHA512;
            case MLDSA65_RSA3072_PSS_SHA512 -> IANAObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA512;
            case MLDSA65_RSA3072_PKCS15_SHA512 -> IANAObjectIdentifiers.id_MLDSA65_RSA3072_PKCS15_SHA512;
            case MLDSA65_RSA4096_PSS_SHA512 -> IANAObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA512;
            case MLDSA65_RSA4096_PKCS15_SHA512 -> IANAObjectIdentifiers.id_MLDSA65_RSA4096_PKCS15_SHA512;
            case MLDSA65_ECDSA_P256_SHA512 -> IANAObjectIdentifiers.id_MLDSA65_ECDSA_P256_SHA512;
            case MLDSA65_ECDSA_P384_SHA512 -> IANAObjectIdentifiers.id_MLDSA65_ECDSA_P384_SHA512;
            case MLDSA65_ECDSA_BP256_SHA512 -> IANAObjectIdentifiers.id_MLDSA65_ECDSA_brainpoolP256r1_SHA512;
            case MLDSA65_ED25519_SHA512 -> IANAObjectIdentifiers.id_MLDSA65_Ed25519_SHA512;
            case MLDSA87_ECDSA_P384_SHA512 -> IANAObjectIdentifiers.id_MLDSA87_ECDSA_P384_SHA512;
            case MLDSA87_ECDSA_BP384_SHA512 -> IANAObjectIdentifiers.id_MLDSA87_ECDSA_brainpoolP384r1_SHA512;
            case MLDSA87_ED448_SHAKE256 -> IANAObjectIdentifiers.id_MLDSA87_Ed448_SHAKE256;
            case MLDSA87_RSA3072_PSS_SHA512 -> IANAObjectIdentifiers.id_MLDSA87_RSA3072_PSS_SHA512;
            case MLDSA87_RSA4096_PSS_SHA512 -> IANAObjectIdentifiers.id_MLDSA87_RSA4096_PSS_SHA512;
            case MLDSA87_ECDSA_P521_SHA512 -> IANAObjectIdentifiers.id_MLDSA87_ECDSA_P521_SHA512;
        };
    }

    @Override
    public AlgorithmIdentifier getPrimaryIdentifier() {
        return switch (theSpec) {
            case MLDSA44_RSA2048_PSS_SHA256, MLDSA44_RSA2048_PKCS15_SHA256,
                 MLDSA44_ECDSA_P256_SHA256, MLDSA44_ED25519_SHA512 ->
                    new AlgorithmIdentifier(NISTObjectIdentifiers.id_ml_dsa_44);
            case MLDSA65_RSA3072_PSS_SHA512, MLDSA65_RSA3072_PKCS15_SHA512, MLDSA65_RSA4096_PSS_SHA512,
                 MLDSA65_RSA4096_PKCS15_SHA512, MLDSA65_ECDSA_P256_SHA512, MLDSA65_ECDSA_P384_SHA512,
                 MLDSA65_ECDSA_BP256_SHA512, MLDSA65_ED25519_SHA512 ->
                    new AlgorithmIdentifier(NISTObjectIdentifiers.id_ml_dsa_65);
            case MLDSA87_ECDSA_P384_SHA512, MLDSA87_ECDSA_BP384_SHA512, MLDSA87_ED448_SHAKE256,
                 MLDSA87_RSA3072_PSS_SHA512, MLDSA87_RSA4096_PSS_SHA512, MLDSA87_ECDSA_P521_SHA512 ->
                    new AlgorithmIdentifier(NISTObjectIdentifiers.id_ml_dsa_87);
        };
    }

    @Override
    public AlgorithmIdentifier getSecondaryIdentifier() {
        return switch (theSpec) {
            case MLDSA44_RSA2048_PSS_SHA256, MLDSA44_RSA2048_PKCS15_SHA256, MLDSA65_RSA3072_PSS_SHA512,
                 MLDSA65_RSA3072_PKCS15_SHA512, MLDSA65_RSA4096_PSS_SHA512, MLDSA65_RSA4096_PKCS15_SHA512,
                 MLDSA87_RSA3072_PSS_SHA512, MLDSA87_RSA4096_PSS_SHA512 ->
                    new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption);
            case MLDSA44_ECDSA_P256_SHA256, MLDSA65_ECDSA_P256_SHA512 ->
                    new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, SECObjectIdentifiers.secp256r1);
            case MLDSA65_ECDSA_P384_SHA512, MLDSA87_ECDSA_P384_SHA512 ->
                    new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, SECObjectIdentifiers.secp384r1);
            case MLDSA65_ECDSA_BP256_SHA512 ->
                    new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, TeleTrusTObjectIdentifiers.brainpoolP256r1);
            case MLDSA87_ECDSA_P521_SHA512 ->
                    new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, SECObjectIdentifiers.secp521r1);
            case MLDSA87_ECDSA_BP384_SHA512 ->
                    new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, TeleTrusTObjectIdentifiers.brainpoolP384r1);
            case MLDSA44_ED25519_SHA512, MLDSA65_ED25519_SHA512 ->
                    new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed25519);
            case MLDSA87_ED448_SHAKE256 -> new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed448);
        };
    }

    @Override
    public byte[] getLabel() {
        return switch (theSpec) {
            case MLDSA44_RSA2048_PSS_SHA256 -> Strings.toByteArray("COMPSIG-MLDSA44-RSA2048-PSS-SHA256");
            case MLDSA44_RSA2048_PKCS15_SHA256 -> Strings.toByteArray("COMPSIG-MLDSA44-RSA2048-PKCS15-SHA256");
            case MLDSA44_ECDSA_P256_SHA256 -> Strings.toByteArray("COMPSIG-MLDSA44-ECDSA-P256-SHA256");
            case MLDSA44_ED25519_SHA512 -> Strings.toByteArray("COMPSIG-MLDSA44-Ed25519-SHA512");
            case MLDSA65_RSA3072_PSS_SHA512 -> Strings.toByteArray("COMPSIG-MLDSA65-RSA3072-PSS-SHA512");
            case MLDSA65_RSA3072_PKCS15_SHA512 -> Strings.toByteArray("COMPSIG-MLDSA65-RSA3072-PKCS15-SHA512");
            case MLDSA65_RSA4096_PSS_SHA512 -> Strings.toByteArray("COMPSIG-MLDSA65-RSA4096-PSS-SHA512");
            case MLDSA65_RSA4096_PKCS15_SHA512 -> Strings.toByteArray("COMPSIG-MLDSA65-RSA4096-PKCS15-SHA512");
            case MLDSA65_ECDSA_P256_SHA512 -> Strings.toByteArray("COMPSIG-MLDSA65-ECDSA-P256-SHA512");
            case MLDSA65_ECDSA_P384_SHA512 -> Strings.toByteArray("COMPSIG-MLDSA65-ECDSA-P384-SHA512");
            case MLDSA65_ECDSA_BP256_SHA512 -> Strings.toByteArray("COMPSIG-MLDSA65-ECDSA-BP256-SHA512");
            case MLDSA65_ED25519_SHA512 -> Strings.toByteArray("COMPSIG-MLDSA65-Ed25519-SHA512");
            case MLDSA87_ECDSA_P384_SHA512 -> Strings.toByteArray("COMPSIG-MLDSA87-ECDSA-P384-SHA512");
            case MLDSA87_ECDSA_BP384_SHA512 -> Strings.toByteArray("COMPSIG-MLDSA87-ECDSA-BP384-SHA512");
            case MLDSA87_ED448_SHAKE256 -> Strings.toByteArray("COMPSIG-MLDSA87-Ed448-SHAKE256");
            case MLDSA87_RSA3072_PSS_SHA512 -> Strings.toByteArray("COMPSIG-MLDSA87-RSA3072-PSS-SHA512");
            case MLDSA87_RSA4096_PSS_SHA512 -> Strings.toByteArray("COMPSIG-MLDSA87-RSA4096-PSS-SHA512");
            case MLDSA87_ECDSA_P521_SHA512 -> Strings.toByteArray("COMPSIG-MLDSA87-ECDSA-P521-SHA512");
        };
    }

    @Override
    public String getJCAName() {
        return switch (theSpec) {
            case MLDSA44_RSA2048_PSS_SHA256 -> "MLDSA44-RSA2048-PSS-SHA256";
            case MLDSA44_RSA2048_PKCS15_SHA256 -> "MLDSA44-RSA2048-PKCS15-SHA256";
            case MLDSA44_ECDSA_P256_SHA256 -> "MLDSA44-ECDSA-P256-SHA256";
            case MLDSA44_ED25519_SHA512 -> "MLDSA44-Ed25519-SHA512";
            case MLDSA65_RSA3072_PSS_SHA512 -> "MLDSA65-RSA3072-PSS-SHA512";
            case MLDSA65_RSA3072_PKCS15_SHA512 -> "MLDSA65-RSA3072-PKCS15-SHA512";
            case MLDSA65_RSA4096_PSS_SHA512 -> "MLDSA65-RSA4096-PSS-SHA512";
            case MLDSA65_RSA4096_PKCS15_SHA512 -> "MLDSA65-RSA4096-PKCS15-SHA512";
            case MLDSA65_ECDSA_P256_SHA512 -> "MLDSA65-ECDSA-P256-SHA512";
            case MLDSA65_ECDSA_P384_SHA512 -> "MLDSA65-ECDSA-P384-SHA512";
            case MLDSA65_ECDSA_BP256_SHA512 -> "MLDSA65-ECDSA-brainpoolP256r1-SHA512";
            case MLDSA65_ED25519_SHA512 -> "MLDSA65-Ed25519-SHA512";
            case MLDSA87_ECDSA_P384_SHA512 -> "MLDSA87-ECDSA-P384-SHA512";
            case MLDSA87_ECDSA_BP384_SHA512 -> "MLDSA87-ECDSA-brainpoolP384r1-SHA512";
            case MLDSA87_ED448_SHAKE256 -> "MLDSA87-Ed448-SHAKE256";
            case MLDSA87_RSA3072_PSS_SHA512 -> "MLDSA87-RSA3072-PSS-SHA512";
            case MLDSA87_RSA4096_PSS_SHA512 -> "MLDSA87-RSA4096-PSS-SHA512";
            case MLDSA87_ECDSA_P521_SHA512 -> "MLDSA87-ECDSA-P521-SHA512";
        };
    }

    @Override
    public int getPrivateSeedLength() {
        return MLDSA_PRIVATE_SEED_LENGTH;
    }

    @Override
    public int getPublicSeedLength() {
        return switch (theSpec) {
            case MLDSA44_RSA2048_PSS_SHA256, MLDSA44_RSA2048_PKCS15_SHA256,
                 MLDSA44_ECDSA_P256_SHA256, MLDSA44_ED25519_SHA512 -> MLDSA44_PUBLIC_SEED_LENGTH;
            case MLDSA65_RSA3072_PSS_SHA512, MLDSA65_RSA3072_PKCS15_SHA512,
                 MLDSA65_RSA4096_PSS_SHA512, MLDSA65_RSA4096_PKCS15_SHA512,
                 MLDSA65_ECDSA_P256_SHA512, MLDSA65_ECDSA_P384_SHA512,
                 MLDSA65_ECDSA_BP256_SHA512, MLDSA65_ED25519_SHA512 -> MLDSA65_PUBLIC_SEED_LENGTH;
            case MLDSA87_ECDSA_P384_SHA512, MLDSA87_ECDSA_BP384_SHA512,
                 MLDSA87_ED448_SHAKE256, MLDSA87_RSA3072_PSS_SHA512,
                 MLDSA87_RSA4096_PSS_SHA512, MLDSA87_ECDSA_P521_SHA512 -> MLDSA87_PUBLIC_SEED_LENGTH;
        };
    }

    /**
     * Obtain the Primary Signature Length.
     *
     * @return th elength
     */
    public int getSignatureLength() {
        return switch (theSpec) {
            case MLDSA44_RSA2048_PSS_SHA256, MLDSA44_RSA2048_PKCS15_SHA256,
                 MLDSA44_ECDSA_P256_SHA256, MLDSA44_ED25519_SHA512 -> MLDSA44_SIGNATURE_LENGTH;
            case MLDSA65_RSA3072_PSS_SHA512, MLDSA65_RSA3072_PKCS15_SHA512, MLDSA65_RSA4096_PSS_SHA512,
                 MLDSA65_RSA4096_PKCS15_SHA512, MLDSA65_ECDSA_P256_SHA512, MLDSA65_ECDSA_P384_SHA512,
                 MLDSA65_ECDSA_BP256_SHA512, MLDSA65_ED25519_SHA512 -> MLDSA65_SIGNATURE_LENGTH;
            case MLDSA87_ECDSA_P384_SHA512, MLDSA87_ECDSA_BP384_SHA512, MLDSA87_ED448_SHAKE256,
                 MLDSA87_RSA3072_PSS_SHA512, MLDSA87_RSA4096_PSS_SHA512, MLDSA87_ECDSA_P521_SHA512 ->
                    MLDSA87_SIGNATURE_LENGTH;
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
        return pThat instanceof GordianCoreHybridSignSpec myThat
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
    public static GordianCoreHybridSignSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianHybridSignSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianHybridSignSpec, GordianCoreHybridSignSpec> newSpecMap() {
        final Map<GordianHybridSignSpec, GordianCoreHybridSignSpec> myMap = new EnumMap<>(GordianHybridSignSpec.class);
        for (GordianHybridSignSpec mySpec : GordianHybridSignSpec.values()) {
            myMap.put(mySpec, new GordianCoreHybridSignSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreHybridSignSpec[] values() {
        return VALUES;
    }
}
