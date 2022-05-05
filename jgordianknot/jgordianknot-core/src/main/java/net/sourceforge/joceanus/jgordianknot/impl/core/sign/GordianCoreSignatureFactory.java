/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.sign;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.digest.GordianCoreDigestFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot base for signatureFactory.
 */
public abstract class GordianCoreSignatureFactory
    implements GordianSignatureFactory {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The algorithm Ids.
     */
    private GordianSignatureAlgId theAlgIds;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    protected GordianCoreSignatureFactory(final GordianCoreFactory pFactory) {
        theFactory = pFactory;
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    protected GordianCoreFactory getFactory() {
        return theFactory;
    }

    @Override
    public Predicate<GordianSignatureSpec> supportedKeyPairSignatures() {
        return this::validSignatureSpec;
    }

    /**
     * Check the signatureSpec.
     * @param pSignatureSpec the signatureSpec
     * @throws OceanusException on error
     */
    protected void checkSignatureSpec(final GordianSignatureSpec pSignatureSpec) throws OceanusException {
        /* Check validity of signature */
        if (!validSignatureSpec(pSignatureSpec)) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pSignatureSpec));
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
        if (GordianSignatureType.NR.equals(pSignSpec.getSignatureType())) {
            return pKeyPairSpec.getElliptic().getKeySize() > pSignSpec.getDigestSpec().getDigestLength().getLength();
        }

        /* Disallow incorrectly sized digest for GOST */
        if (GordianKeyPairType.GOST2012.equals(pKeyPairSpec.getKeyPairType())) {
            final int myDigestLen = pSignSpec.getDigestSpec().getDigestLength().getLength();
            return pKeyPairSpec.getElliptic().getKeySize() == myDigestLen;
        }

        /* If this is a RSA Signature */
        if (GordianKeyPairType.RSA.equals(pKeyPairSpec.getKeyPairType())) {
            /* If this is a PSS signature */
            if (pSignSpec.getSignatureType().isPSS()) {
                /* The digest length cannot be too large wrt to the modulus */
                int myLen = pSignSpec.getDigestSpec().getDigestLength().getLength();
                myLen += Byte.SIZE;
                if (pKeyPairSpec.getRSAModulus().getLength() < (myLen << 1)) {
                    return false;
                }
            }

            /* Must be X931/ISO9796d2 Signature */
            /* The digest length cannot be too large wrt to the modulus */
            int myLen = pSignSpec.getDigestSpec().getDigestLength().getLength();
            myLen += Integer.SIZE;
            if (pKeyPairSpec.getRSAModulus().getLength() < myLen) {
                return false;
            }
        }


        /* For Composite EncryptorSpec */
        if (pKeyPairSpec.getKeyPairType() == GordianKeyPairType.COMPOSITE) {
            /* Loop through the keyPairs */
            final Iterator<GordianKeyPairSpec> pairIterator = pKeyPairSpec.keySpecIterator();
            final Iterator<GordianSignatureSpec> sigIterator = pSignSpec.signatureSpecIterator();
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
     * @param pSignSpec the macSpec
     * @return true/false
     */
    protected boolean validSignatureSpec(final GordianSignatureSpec pSignSpec) {
        /* Reject invalid signatureSpec */
        if (pSignSpec == null || !pSignSpec.isValid()) {
            return false;
        }

        /* Check that the signatureType is supported */
        final GordianKeyPairType myType = pSignSpec.getKeyPairType();
        final GordianSignatureType mySignType = pSignSpec.getSignatureType();
        if (!mySignType.isSupported(myType)) {
            return false;
        }

        /* Don't worry about digestSpec if it is irrelevant */
        if (myType.nullDigestForSignatures()) {
            return pSignSpec.getSignatureSpec() == null;
        }

        /* Composite signatures */
        if (GordianKeyPairType.COMPOSITE.equals(myType)) {
            /* Loop through the specs */
            final Iterator<GordianSignatureSpec> myIterator = pSignSpec.signatureSpecIterator();
            while (myIterator.hasNext()) {
                final GordianSignatureSpec mySpec = myIterator.next();
                if (!validSignatureSpec(mySpec)) {
                    return false;
                }
            }
            return true;
        }

        /* Check that the digestSpec is supported */
        final GordianDigestSpec mySpec = pSignSpec.getDigestSpec();
        if (mySpec == null
                || !validSignatureDigestSpec(mySpec)) {
            return false;
        }

        /* Check RSA signatures */
        if (GordianKeyPairType.RSA.equals(myType)) {
            return validRSASignature(pSignSpec);
        }

        /* Only allow SM3 for SM2 signature */
        if (GordianKeyPairType.SM2.equals(myType)) {
            return GordianDigestType.SM3.equals(mySpec.getDigestType());
        }

        /* Only allow GOST for DSTU signature */
        if (GordianKeyPairType.DSTU4145.equals(myType)) {
            return GordianDigestType.GOST.equals(mySpec.getDigestType());
        }

        /* Only allow STREEBOG for GOST signature */
        if (GordianKeyPairType.GOST2012.equals(myType)) {
            return GordianDigestType.STREEBOG.equals(mySpec.getDigestType());
        }

        /* OK */
        return true;
    }

    /**
     * Check SignatureDigestSpec.
     * @param pDigestSpec the digestSpec
     * @return true/false
     */
    protected boolean validSignatureDigestSpec(final GordianDigestSpec pDigestSpec) {
        final GordianCoreDigestFactory myDigests = (GordianCoreDigestFactory) theFactory.getDigestFactory();
        return myDigests.validDigestSpec(pDigestSpec);
    }

    /**
     * Check RSASignature.
     * @param pSpec the signatureSpec
     * @return true/false
     */
    private static boolean validRSASignature(final GordianSignatureSpec pSpec) {
        /* Apply restrictions on PREHASH */
        if (GordianSignatureType.PREHASH.equals(pSpec.getSignatureType())) {
            /* Switch on DigestType */
            final GordianDigestSpec myDigest = pSpec.getDigestSpec();
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
     * Obtain Identifier for SignatureSpec.
     * @param pSpec the signatureSpec.
     * @param pKeyPair the keyPair
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpecAndKeyPair(final GordianSignatureSpec pSpec,
                                                              final GordianKeyPair pKeyPair) {
        return getAlgorithmIds().getIdentifierForSpecAndKeyPair(pSpec, pKeyPair);
    }

    /**
     * Obtain SignatureSpec for Identifier.
     * @param pIdentifier the identifier.
     * @return the signatureSpec (or null if not found)
     */
    public GordianSignatureSpec getSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return getAlgorithmIds().getSpecForIdentifier(pIdentifier);
    }

    /**
     * Obtain the signature algorithm Ids.
     * @return the signature Algorithm Ids
     */
    private GordianSignatureAlgId getAlgorithmIds() {
        if (theAlgIds == null) {
            theAlgIds = new GordianSignatureAlgId(theFactory);
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
                .collect(Collectors.toList());
    }

    @Override
    public List<GordianSignatureSpec> listPossibleSignatures(final GordianKeyPairType pKeyType) {
        /* Access the list of possible digests */
        final List<GordianSignatureSpec> mySignatures = new ArrayList<>();
        final List<GordianDigestSpec> myDigests = theFactory.getDigestFactory().listAllPossibleSpecs();

        /* For each supported signature */
        for (GordianSignatureType mySignType : GordianSignatureType.values()) {
            /* Skip if the signatureType is not valid */
            if (mySignType.isSupported(pKeyType)) {
                /* If we need null-digestSpec */
                if (pKeyType.nullDigestForSignatures()) {
                    /* Add the signature */
                    mySignatures.add(new GordianSignatureSpec(pKeyType, mySignType));
                    continue;
                }

                /* For each possible digestSpec */
                for (GordianDigestSpec mySpec : myDigests) {
                    /* Add the signature */
                    mySignatures.add(new GordianSignatureSpec(pKeyType, mySignType, mySpec));
                }
            }
        }

        /* Return the list */
        return mySignatures;
    }

    @Override
    public GordianSignatureSpec defaultForKeyPair(final GordianKeyPairSpec pKeySpec) {
        switch (pKeySpec.getKeyPairType()) {
            case RSA:
                return GordianSignatureSpec.rsa(GordianSignatureType.PSSMGF1, GordianDigestSpec.sha3(GordianLength.LEN_512));
            case DSA:
                return GordianSignatureSpec.dsa(GordianSignatureType.DSA, GordianDigestSpec.sha2(GordianLength.LEN_512));
            case EC:
                return GordianSignatureSpec.ec(GordianSignatureType.DSA, GordianDigestSpec.sha3(GordianLength.LEN_512));
            case SM2:
                return GordianSignatureSpec.sm2();
            case DSTU4145:
                return GordianSignatureSpec.dstu4145();
            case GOST2012:
                return GordianSignatureSpec.gost2012(GordianLength.LEN_512);
            case EDDSA:
                return GordianSignatureSpec.edDSA();
            case RAINBOW:
                return GordianSignatureSpec.rainbow(GordianDigestSpec.sha2(GordianLength.LEN_512));
            case SPHINCS:
                return GordianSignatureSpec.sphincs();
            case SPHINCSPLUS:
                return GordianSignatureSpec.sphincsPlus();
            case XMSS:
                return GordianSignatureSpec.xmss();
            case LMS:
                return GordianSignatureSpec.lms();
            case COMPOSITE:
                final List<GordianSignatureSpec> mySpecs = new ArrayList<>();
                final Iterator<GordianKeyPairSpec> myIterator = pKeySpec.keySpecIterator();
                while (myIterator.hasNext()) {
                    final GordianKeyPairSpec mySpec = myIterator.next();
                    mySpecs.add(defaultForKeyPair(mySpec));
                }
                final GordianSignatureSpec mySpec = GordianSignatureSpec.composite(mySpecs);
                return mySpec.isValid() ? mySpec : null;
            default:
                return null;
        }
    }
}
