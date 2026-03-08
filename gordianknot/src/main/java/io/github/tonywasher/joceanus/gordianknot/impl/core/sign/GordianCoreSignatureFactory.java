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
package io.github.tonywasher.joceanus.gordianknot.impl.core.sign;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianGOSTSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.digest.GordianCoreDigestFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.sign.GordianCoreSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.sign.GordianCoreSignatureSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.sign.GordianCoreSignatureType;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * GordianKnot base for signatureFactory.
 */
public abstract class GordianCoreSignatureFactory
        implements GordianSignatureFactory {
    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * The algorithm Ids.
     */
    private GordianCoreSignatureAlgId theAlgIds;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    protected GordianCoreSignatureFactory(final GordianBaseFactory pFactory) {
        theFactory = pFactory;
    }

    @Override
    public GordianSignatureSpecBuilder newSignatureSpecBuilder() {
        return GordianCoreSignatureSpecBuilder.newInstance();
    }

    /**
     * Obtain the factory.
     *
     * @return the factory
     */
    protected GordianBaseFactory getFactory() {
        return theFactory;
    }

    @Override
    public Predicate<GordianSignatureSpec> supportedKeyPairSignatures() {
        return this::validSignatureSpec;
    }

    /**
     * Check the signatureSpec.
     *
     * @param pSignatureSpec the signatureSpec
     * @throws GordianException on error
     */
    protected void checkSignatureSpec(final GordianSignatureSpec pSignatureSpec) throws GordianException {
        /* Check validity of signature */
        if (!validSignatureSpec(pSignatureSpec)) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pSignatureSpec));
        }
    }

    @Override
    public boolean validSignatureSpecForKeyPairSpec(final GordianKeyPairSpec pKeyPairSpec,
                                                    final GordianSignatureSpec pSignSpec) {
        /* Check signature matches keySpec */
        if (pSignSpec.getKeyPairType() != pKeyPairSpec.getKeyPairType()) {
            return false;
        }

        /* Check that the signatureSpec is supported */
        if (!validSignatureSpec(pSignSpec)) {
            return false;
        }

        /* Disallow ECNR if keySize is smaller than digestSize */
        final GordianCoreSignatureSpec mySpec = (GordianCoreSignatureSpec) pSignSpec;
        final GordianCoreKeyPairSpec myKeyPairSpec = (GordianCoreKeyPairSpec) pKeyPairSpec;
        if (GordianSignatureType.NR.equals(pSignSpec.getSignatureType())) {
            return myKeyPairSpec.getElliptic().getKeySize() > mySpec.getDigestSpec().getDigestLength().getLength();
        }

        /* Disallow incorrectly sized digest for GOST */
        if (GordianKeyPairType.GOST.equals(pKeyPairSpec.getKeyPairType())) {
            final int myDigestLen = mySpec.getDigestSpec().getDigestLength().getLength();
            return myKeyPairSpec.getElliptic().getKeySize() == myDigestLen;
        }

        /* If this is a RSA Signature */
        if (GordianKeyPairType.RSA.equals(pKeyPairSpec.getKeyPairType())) {
            /* If this is a PSS signature */
            if (mySpec.getCoreType().isPSS()) {
                /* The digest length cannot be too large wrt to the modulus */
                int myLen = mySpec.getDigestSpec().getDigestLength().getLength();
                myLen += Byte.SIZE;
                if (myKeyPairSpec.getRSASpec().getLength() < (myLen << 1)) {
                    return false;
                }
            }

            /* Must be X931/ISO9796d2 Signature */
            /* The digest length cannot be too large wrt to the modulus */
            int myLen = mySpec.getDigestSpec().getDigestLength().getLength();
            myLen += Integer.SIZE;
            if (myKeyPairSpec.getRSASpec().getLength() < myLen) {
                return false;
            }
        }


        /* For Composite EncryptorSpec */
        if (pKeyPairSpec.getKeyPairType() == GordianKeyPairType.COMPOSITE) {
            /* Loop through the keyPairs */
            final Iterator<GordianKeyPairSpec> pairIterator = myKeyPairSpec.keySpecIterator();
            final Iterator<GordianSignatureSpec> sigIterator = mySpec.signatureSpecIterator();
            while (pairIterator.hasNext() && sigIterator.hasNext()) {
                final GordianKeyPairSpec myPairSpec = pairIterator.next();
                final GordianSignatureSpec mySigSpec = sigIterator.next();
                if (!validSignatureSpecForKeyPairSpec(myPairSpec, mySigSpec)) {
                    return false;
                }
            }
            if (pairIterator.hasNext() || sigIterator.hasNext()) {
                return false;
            }
        }

        /* OK */
        return true;
    }

    /**
     * Check SignatureSpec.
     *
     * @param pSignSpec the macSpec
     * @return true/false
     */
    protected boolean validSignatureSpec(final GordianSignatureSpec pSignSpec) {
        /* Reject invalid signatureSpec */
        if (pSignSpec == null || !pSignSpec.isValid()) {
            return false;
        }

        /* Check that the signatureType is supported */
        final GordianCoreSignatureSpec mySignSpec = (GordianCoreSignatureSpec) pSignSpec;
        final GordianKeyPairType myType = pSignSpec.getKeyPairType();
        final GordianCoreSignatureType mySignType = mySignSpec.getCoreType();
        if (!mySignType.isSupported(myType)) {
            return false;
        }

        /* Don't worry about digestSpec if it is irrelevant */
        final GordianCoreKeyPairType myKeyType = GordianCoreKeyPairType.mapCoreType(myType);
        if (myKeyType.useDigestForSignatures().mustNotExist()) {
            return pSignSpec.getSignatureSpec() == null;
        }
        if (myKeyType.useDigestForSignatures().canNotExist()
                && pSignSpec.getSignatureSpec() == null) {
            return true;
        }

        /* Composite signatures */
        if (GordianKeyPairType.COMPOSITE.equals(myType)) {
            /* Loop through the specs */
            final Iterator<GordianSignatureSpec> myIterator = mySignSpec.signatureSpecIterator();
            while (myIterator.hasNext()) {
                final GordianSignatureSpec mySpec = myIterator.next();
                if (!validSignatureSpec(mySpec)) {
                    return false;
                }
            }
            return true;
        }

        /* Check that the digestSpec is supported */
        final GordianDigestSpec mySpec = mySignSpec.getDigestSpec();
        if (mySpec == null
                || !validSignatureDigestSpec(mySpec)) {
            return false;
        }

        /* Check RSA signatures */
        if (GordianKeyPairType.RSA.equals(myType)) {
            return validRSASignature(pSignSpec);
        }

        /* Check DDSA signatures */
        if (GordianSignatureType.DDSA.equals(pSignSpec.getSignatureType())) {
            return validDDSASignature(pSignSpec);
        }

        /* Only allow GOST for DSTU signature */
        if (GordianKeyPairType.DSTU.equals(myType)) {
            return GordianDigestType.GOST.equals(mySpec.getDigestType());
        }

        /* Only allow STREEBOG for GOST signature */
        if (GordianKeyPairType.GOST.equals(myType)) {
            return GordianDigestType.STREEBOG.equals(mySpec.getDigestType());
        }

        /* OK */
        return true;
    }

    /**
     * Check SignatureDigestSpec.
     *
     * @param pDigestSpec the digestSpec
     * @return true/false
     */
    protected boolean validSignatureDigestSpec(final GordianDigestSpec pDigestSpec) {
        final GordianCoreDigestFactory myDigests = (GordianCoreDigestFactory) theFactory.getDigestFactory();
        return myDigests.validDigestSpec(pDigestSpec);
    }

    /**
     * Check RSASignature.
     *
     * @param pSpec the signatureSpec
     * @return true/false
     */
    private static boolean validRSASignature(final GordianSignatureSpec pSpec) {
        /* Apply restrictions on PREHASH */
        if (GordianSignatureType.PREHASH.equals(pSpec.getSignatureType())) {
            /* Switch on DigestType */
            final GordianCoreSignatureSpec mySpec = (GordianCoreSignatureSpec) pSpec;
            final GordianDigestSpec myDigest = mySpec.getDigestSpec();
            switch (myDigest.getDigestType()) {
                case SHA1:
                case SHA2:
                case SHA3:
                case MD2:
                case MD4:
                case MD5:
                    return true;
                case RIPEMD:
                    return myDigest.getDigestLength().getLength() <= GordianLength.LEN_256.getLength();
                default:
                    return false;
            }
        }

        /* Otherwise OK */
        return true;
    }

    /**
     * Check DDSASignature.
     *
     * @param pSpec the signatureSpec
     * @return true/false
     */
    private static boolean validDDSASignature(final GordianSignatureSpec pSpec) {
        /* Switch on DigestType */
        final GordianCoreSignatureSpec mySpec = (GordianCoreSignatureSpec) pSpec;
        final GordianDigestSpec myDigest = mySpec.getDigestSpec();
        switch (myDigest.getDigestType()) {
            case ASCON:
            case ISAP:
            case PHOTONBEETLE:
            case SPARKLE:
            case XOODYAK:
                return false;
            default:
                return true;
        }
    }

    /**
     * Obtain Identifier for SignatureSpec.
     *
     * @param pSpec    the signatureSpec.
     * @param pKeyPair the keyPair
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpecAndKeyPair(final GordianSignatureSpec pSpec,
                                                              final GordianKeyPair pKeyPair) {
        return getAlgorithmIds().getIdentifierForSpecAndKeyPair(pSpec, pKeyPair);
    }

    /**
     * Obtain SignatureSpec for Identifier.
     *
     * @param pIdentifier the identifier.
     * @return the signatureSpec (or null if not found)
     */
    public GordianSignatureSpec getSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return getAlgorithmIds().getSpecForIdentifier(pIdentifier);
    }

    /**
     * Obtain the signature algorithm Ids.
     *
     * @return the signature Algorithm Ids
     */
    private GordianCoreSignatureAlgId getAlgorithmIds() {
        if (theAlgIds == null) {
            theAlgIds = new GordianCoreSignatureAlgId(theFactory);
        }
        return theAlgIds;
    }

    @Override
    public List<GordianSignatureSpec> listAllSupportedSignatures(final GordianKeyPair pKeyPair) {
        return listAllSupportedSignatures(pKeyPair.getKeyPairSpec());
    }

    @Override
    public List<GordianSignatureSpec> listAllSupportedSignatures(final GordianKeyPairSpec pKeySpec) {
        return listPossibleSignatures(pKeySpec.getKeyPairType())
                .stream()
                .filter(s -> validSignatureSpecForKeyPairSpec(pKeySpec, s))
                .toList();
    }

    @Override
    public List<GordianSignatureSpec> listPossibleSignatures(final GordianKeyPairType pKeyType) {
        return GordianCoreSignatureSpecBuilder.listAllPossibleSpecs(pKeyType);
    }

    @Override
    public GordianSignatureSpec defaultForKeyPair(final GordianKeyPairSpec pKeySpec) {
        final GordianCoreSignatureSpecBuilder myBuilder = GordianCoreSignatureSpecBuilder.newInstance();
        final GordianCoreDigestSpecBuilder myDigestBuilder = GordianCoreDigestSpecBuilder.newInstance();
        switch (pKeySpec.getKeyPairType()) {
            case RSA:
                return myBuilder.rsa(GordianSignatureType.PSSMGF1, myDigestBuilder.sha3(GordianLength.LEN_256));
            case DSA:
                return myBuilder.dsa(GordianSignatureType.DSA, myDigestBuilder.sha2(GordianLength.LEN_512));
            case EC:
                return myBuilder.ec(GordianSignatureType.DSA, myDigestBuilder.sha3(GordianLength.LEN_512));
            case SM2:
                return myBuilder.sm2(myDigestBuilder.sm3());
            case DSTU:
                return myBuilder.dstu4145();
            case GOST:
                return myBuilder.gost2012(GordianGOSTSpec.GOST256A.equals(pKeySpec.getSubSpec())
                        ? GordianLength.LEN_256 : GordianLength.LEN_512);
            case EDDSA:
                return myBuilder.edDSA();
            case SLHDSA:
                return myBuilder.slhdsa();
            case MLDSA:
                return myBuilder.mldsa();
            case FALCON:
                return myBuilder.falcon();
            case MAYO:
                return myBuilder.mayo();
            case SNOVA:
                return myBuilder.snova();
            case PICNIC:
                return myBuilder.picnic();
            case XMSS:
                return myBuilder.xmss();
            case LMS:
                return myBuilder.lms();
            case COMPOSITE:
                final List<GordianSignatureSpec> mySpecs = new ArrayList<>();
                final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
                final Iterator<GordianKeyPairSpec> myIterator = myKeySpec.keySpecIterator();
                while (myIterator.hasNext()) {
                    final GordianKeyPairSpec mySpec = myIterator.next();
                    mySpecs.add(defaultForKeyPair(mySpec));
                }
                final GordianSignatureSpec mySpec = myBuilder.composite(mySpecs);
                return mySpec.isValid() ? mySpec : null;
            default:
                return null;
        }
    }
}
