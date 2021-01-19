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
package net.sourceforge.joceanus.jgordianknot.impl.core.agree;

import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.agreement.kdf.ConcatenationKDFGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementStatus;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairAgreement;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianPrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianPublicKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Core KeyPairAgreement.
 */
public class GordianCoreKeyPairAgreement
    extends GordianCoreAgreement<GordianKeyPairAgreementSpec>
    implements GordianKeyPairAgreement {
    /**
     * The keyDerivation function.
     */
    private DerivationFunction theKDF;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     */
    protected GordianCoreKeyPairAgreement(final GordianCoreFactory pFactory,
                                          final GordianKeyPairAgreementSpec pSpec) {
        super(pFactory, pSpec);
    }

    /**
     * Obtain the agreement factory.
     * @return the factory
     */
    protected GordianCoreAgreementFactory getAgreementFactory() {
        return (GordianCoreAgreementFactory) getFactory().getKeyPairFactory().getAgreementFactory();
    }

    /**
     * CheckKeyPair.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    protected void checkKeyPair(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Check that the KeyPair is valid */
        final GordianAgreementFactory myAgrees = getAgreementFactory();
        if (!myAgrees.validAgreementSpecForKeyPair(pKeyPair, getAgreementSpec())) {
            throw new GordianDataException("Incorrect KeyPair type");
        }
    }

    /**
     * Obtain public key from pair.
     * @param pKeyPair the keyPair
     * @return the public key
     */
    protected GordianPublicKey getPublicKey(final GordianKeyPair pKeyPair) {

        return ((GordianCoreKeyPair) pKeyPair).getPublicKey();
    }

    /**
     * Obtain private key from pair.
     * @param pKeyPair the keyPair
     * @return the private key
     * @throws OceanusException on error
     */
    protected GordianPrivateKey getPrivateKey(final GordianKeyPair pKeyPair) throws OceanusException {
        if (pKeyPair.isPublicOnly()) {
            throw new GordianDataException("missing privateKey");
        }
        return ((GordianCoreKeyPair) pKeyPair).getPrivateKey();
    }

    @Override
    protected void storeSecret(final byte[] pSecret) throws OceanusException {
        /* Protect against failure */
        final byte[] mySecret = new byte[pSecret.length];
        try {
            /* If we have a kdf */
            if (theKDF != null) {
                /* Create KDF Parameters */
                final KDFParameters myParms = new KDFParameters(pSecret, new byte[0]);
                theKDF.init(myParms);

                /* Create the secret */
                theKDF.generateBytes(mySecret, 0, mySecret.length);
                processSecret(mySecret);

            } else {
                /* Just process the secret */
                processSecret(pSecret);
            }

            /* Clear buffers */
        } finally {
            /* Clear the secret */
            Arrays.fill(mySecret, (byte) 0);
            Arrays.fill(pSecret, (byte) 0);
        }
    }

    /**
     * Enable additional derivation of secret.
     */
    protected void enableDerivation() {
        /* Only enable derivation if it is not none */
        final GordianKeyPairAgreementSpec mySpec = getAgreementSpec();
        if (!GordianKDFType.NONE.equals(mySpec.getKDFType())) {
            theKDF = newDerivationFunction();
        }
    }

    /**
     * Obtain the required derivation function.
     * @return the derivation function
     */
    protected DerivationFunction newDerivationFunction() {
        final GordianKeyPairAgreementSpec mySpec = getAgreementSpec();
        switch (mySpec.getKDFType()) {
            case SHA256KDF:
                return new KDF2BytesGenerator(new SHA256Digest());
            case SHA512KDF:
                return new KDF2BytesGenerator(new SHA512Digest());
            case SHA256CKDF:
                return new ConcatenationKDFGenerator(new SHA256Digest());
            case SHA512CKDF:
                return new ConcatenationKDFGenerator(new SHA512Digest());
            case NONE:
            default:
                return new GordianNullKeyDerivation();
        }
    }

    /**
     * Build clientHello message.
     * @return the clientHello message
     * @throws OceanusException on error
     */
    protected byte[] buildClientHello() throws OceanusException {
        return buildClientHello(null, null);
    }

    /**
     * Build clientHello message.
     * @param pEncapsulated the encapsulated message
     * @return the clientHello message
     * @throws OceanusException on error
     */
    protected byte[] buildClientHello(final byte[] pEncapsulated) throws OceanusException {
        return buildClientHello(pEncapsulated, null);
    }

    /**
     * Build clientHello message.
     * @param pEphemeral the ephemeral publicKey
     * @return the clientHello message
     * @throws OceanusException on error
     */
    protected byte[] buildClientHello(final X509EncodedKeySpec pEphemeral) throws OceanusException {
        return buildClientHello(null, pEphemeral);
    }

    /**
     * Build clientHello message.
     * @param pEncapsulated the encapsulated message
     * @param pEphemeral the ephemeral publicKey
     * @return the clientHello message
     * @throws OceanusException on error
     */
    private byte[] buildClientHello(final byte[] pEncapsulated,
                                    final X509EncodedKeySpec pEphemeral) throws OceanusException {
        /* Must be in clean state */
        checkStatus(GordianAgreementStatus.CLEAN);

        /* Create the clientHello */
        final GordianCoreAgreementFactory myFactory = getAgreementFactory();
        final GordianKeyPairAgreementSpec mySpec = getAgreementSpec();
        final AlgorithmIdentifier myAlgId = myFactory.getIdentifierForSpec(mySpec);
        final AlgorithmIdentifier myResId = getIdentifierForResult();
        final GordianAgreementClientHelloASN1 myClientHello
                = new GordianAgreementClientHelloASN1(myAlgId, myResId, newClientIV(), pEncapsulated, pEphemeral);
        return myClientHello.getEncodedBytes();
    }

    /**
     * Parse the incoming clientHello message.
     * @param pClientHello the incoming clientHello message
     * @return the encapsulated message
     * @throws OceanusException on error
     */
    protected GordianAgreementClientHelloASN1 parseClientHello(final byte[] pClientHello) throws OceanusException {
        /* Must be in clean state */
        checkStatus(GordianAgreementStatus.CLEAN);

        /* Access the sequence */
        final GordianAgreementClientHelloASN1 myClientHello = GordianAgreementClientHelloASN1.getInstance(pClientHello);

        /* Access message parts */
        final AlgorithmIdentifier myAlgId = myClientHello.getAgreementId();
        final AlgorithmIdentifier myResId = myClientHello.getResultId();
        final byte[] myInitVector = myClientHello.getInitVector();

        /* Check agreementSpec */
        final GordianCoreAgreementFactory myFactory = getAgreementFactory();
        final GordianKeyPairAgreementSpec mySpec = myFactory.getSpecForIdentifier(myAlgId);
        if (!Objects.equals(mySpec, getAgreementSpec())) {
            throw new GordianDataException(ERROR_INVSPEC);
        }

        /* Process result identifier */
        processResultIdentifier(myResId);

        /* Store client initVector */
        storeClientIV(myInitVector);

        /* Return the clientHello */
        return myClientHello;
    }

    /**
     * Build serverHello message.
     * @return the serverHello message
     * @throws OceanusException on error
     */
    protected byte[] buildServerHello() throws OceanusException {
        return buildServerHello(null, null);
    }

    /**
     * Build serverHello message.
     * @param pEphemeral the ephemeral publicKey
     * @param pConfirmation the confirmationTag
     * @return the serverHello message
     * @throws OceanusException on error
     */
    protected byte[] buildServerHello(final X509EncodedKeySpec pEphemeral,
                                      final byte[] pConfirmation) throws OceanusException {
        /* Create the serverHello */
        final GordianCoreAgreementFactory myFactory = getAgreementFactory();
        final GordianKeyPairAgreementSpec mySpec = getAgreementSpec();
        final AlgorithmIdentifier myAlgId = myFactory.getIdentifierForSpec(mySpec);
        final GordianAgreementServerHelloASN1 myServerHello
                = new GordianAgreementServerHelloASN1(myAlgId, getServerIV(), pEphemeral, pConfirmation);

        /* If there is a server confirmation, set status */
        if (pConfirmation != null) {
            setStatus(GordianAgreementStatus.AWAITING_CLIENTCONFIRM);
        }

        /* return the serverHello */
        return myServerHello.getEncodedBytes();
    }

    /**
     * Build serverHello message.
     * @param pEphemeral the ephemeral publicKey
     * @param pSignId the signatureId
     * @param pSignature the signature
     * @return the serverHello message
     * @throws OceanusException on error
     */
    protected byte[] buildServerHello(final X509EncodedKeySpec pEphemeral,
                                      final AlgorithmIdentifier pSignId,
                                      final byte[] pSignature) throws OceanusException {
        /* Create the serverHello */
        final GordianCoreAgreementFactory myFactory = getAgreementFactory();
        final GordianKeyPairAgreementSpec mySpec = getAgreementSpec();
        final AlgorithmIdentifier myAlgId = myFactory.getIdentifierForSpec(mySpec);
        final GordianAgreementServerHelloASN1 myServerHello
                = new GordianAgreementServerHelloASN1(myAlgId, getServerIV(), pEphemeral, pSignId, pSignature);

        /* return the serverHello */
        return myServerHello.getEncodedBytes();
    }

    /**
     * Parse the incoming serverHello message.
     * @param pServerHello the serverHello message
     * @return the parsed ASN1
     * @throws OceanusException on error
     */
    protected GordianAgreementServerHelloASN1 parseServerHello(final byte[] pServerHello) throws OceanusException {
        /* Must be in awaiting serverHello state */
        checkStatus(GordianAgreementStatus.AWAITING_SERVERHELLO);

        /* Access the sequence */
        final GordianAgreementServerHelloASN1 myServerHello = GordianAgreementServerHelloASN1.getInstance(pServerHello);

        /* Access message parts */
        final AlgorithmIdentifier myAlgId = myServerHello.getAgreementId();
        final byte[] myInitVector = myServerHello.getInitVector();

        /* Check agreementSpec */
        final GordianCoreAgreementFactory myFactory = getAgreementFactory();
        final GordianKeyPairAgreementSpec mySpec = myFactory.getSpecForIdentifier(myAlgId);
        if (!Objects.equals(mySpec, getAgreementSpec())) {
            throw new GordianDataException(ERROR_INVSPEC);
        }

        /* Store server initVector */
        storeServerIV(myInitVector);

        /* Return the ASN1 */
        return myServerHello;
    }

    /**
     * NullKeyDerivation.
     */
    public static final class GordianNullKeyDerivation
            implements DerivationFunction {
        /**
         * The key.
         */
        private byte[] theKey;

        @Override
        public int generateBytes(final byte[] pBuffer,
                                 final int pOffset,
                                 final int pLength) {
            /* Create the array that is to be copied */
            final byte[] myKey = Arrays.copyOf(theKey, pLength);
            Arrays.fill(theKey, (byte) 0);
            System.arraycopy(myKey, 0, pBuffer, pOffset, pLength);
            Arrays.fill(myKey, (byte) 0);
            return pLength;
        }

        @Override
        public void init(final DerivationParameters pParms) {
            final byte[] mySecret = ((KDFParameters) pParms).getSharedSecret();
            theKey = Arrays.copyOf(mySecret, mySecret.length);
        }
    }
}