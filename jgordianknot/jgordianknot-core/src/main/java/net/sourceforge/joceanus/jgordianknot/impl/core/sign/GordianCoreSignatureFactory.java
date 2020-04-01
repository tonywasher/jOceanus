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
package net.sourceforge.joceanus.jgordianknot.impl.core.sign;

import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianEdwardsElliptic;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
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
    public GordianCoreSignatureFactory(final GordianCoreFactory pFactory) {
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
    public Predicate<GordianSignatureSpec> supportedSignatures() {
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
    public boolean validSignatureSpecForKeySpec(final GordianAsymKeySpec pKeySpec,
                                                final GordianSignatureSpec pSignSpec) {
        /* Reject invalid signatureSpec */
        if (pSignSpec == null || !pSignSpec.isValid()) {
            return false;
        }

        /* Check signature matches keySpec */
        if (pSignSpec.getAsymKeyType() != pKeySpec.getKeyType()) {
            return false;
        }

        /* Check that the signatureSpec is supported */
        if (!validSignatureSpec(pSignSpec)) {
            return false;
        }

        /* Disallow ECNR if keySize is smaller than digestSize */
        if (GordianSignatureType.NR.equals(pSignSpec.getSignatureType())) {
            return pKeySpec.getElliptic().getKeySize() > pSignSpec.getDigestSpec().getDigestLength().getLength();
        }

        /* Disallow incorrectly sized digest for GOST */
        if (GordianAsymKeyType.GOST2012.equals(pKeySpec.getKeyType())) {
            final int myDigestLen = pSignSpec.getDigestSpec().getDigestLength().getLength();
            return pKeySpec.getElliptic().getKeySize() == myDigestLen;
        }

        /* Disallow NATIVE signature for ed448 */
        if (GordianAsymKeyType.EDDSA.equals(pKeySpec.getKeyType())) {
            return pSignSpec.getSignatureType() != GordianSignatureType.NATIVE
                    || pKeySpec.getEdwardsElliptic() == GordianEdwardsElliptic.CURVE25519;
        }

        /* If this is a RSA Signature */
        if (GordianAsymKeyType.RSA.equals(pKeySpec.getKeyType())) {
            /* If this is a PSS signature */
            if (GordianSignatureType.PSS.equals(pSignSpec.getSignatureType())) {
                /* The digest length cannot be too large wrt to the modulus */
                int myLen = pSignSpec.getDigestSpec().getDigestLength().getLength();
                myLen += Byte.SIZE;
                if (pKeySpec.getRSAModulus().getLength() < (myLen << 1)) {
                    return false;
                }
            }

            /* Must be X931/ISO9796d2 Signature */
            /* The digest length cannot be too large wrt to the modulus */
            int myLen = pSignSpec.getDigestSpec().getDigestLength().getLength();
            myLen += Integer.SIZE;
            if (pKeySpec.getRSAModulus().getLength() < myLen) {
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
        final GordianAsymKeyType myType = pSignSpec.getAsymKeyType();
        final GordianSignatureType mySignType = pSignSpec.getSignatureType();
        if (!mySignType.isSupported(myType)) {
            return false;
        }

        /* Don't worry about digestSpec if it is irrelevant */
        final GordianDigestSpec mySpec = pSignSpec.getDigestSpec();
        if (myType.nullDigestForSignatures()) {
            return mySpec == null;
        }

        /* Check that the digestSpec is supported */
        if (mySpec == null
                || !validSignatureDigestSpec(mySpec)) {
            return false;
        }

        /* Check RSA signatures */
        if (GordianAsymKeyType.RSA.equals(myType)) {
            return validRSASignature(pSignSpec);
        }

        /* Only allow SM3 for SM2 signature */
        if (GordianAsymKeyType.SM2.equals(myType)) {
            return GordianDigestType.SM3.equals(mySpec.getDigestType());
        }

        /* Only allow GOST for DSTU signature */
        if (GordianAsymKeyType.DSTU4145.equals(myType)) {
            return GordianDigestType.GOST.equals(mySpec.getDigestType());
        }

        /* Only allow STREEBOG for GOST signature */
        if (GordianAsymKeyType.GOST2012.equals(myType)) {
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
}
