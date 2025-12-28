/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.bc;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianCoreKeyPairAgreement.GordianNullKeyDerivation;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianCoreKeyPair;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianPrivateKey;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianPublicKey;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementBuilder;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementParticipant;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementState;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.agreement.kdf.ConcatenationKDFGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.util.Arrays;

import java.security.SecureRandom;

/**
 * Base Agreement Engine class
 */
public abstract class BouncyXAgreementBase
        extends GordianXCoreAgreementEngine {
    /**
     * The keyDerivation function.
     */
    private DerivationFunction theKDF;

    /**
     * The builder.
     */
    private final GordianXCoreAgreementBuilder theBuilder;

    /**
     * The state.
     */
    private final GordianXCoreAgreementState theState;

    /**
     * The client.
     */
    private final GordianXCoreAgreementParticipant theClient;

    /**
     * The server.
     */
    private final GordianXCoreAgreementParticipant theServer;

    /**
     * Constructor.
     * @param pFactory the security factory
     * @param pSpec the agreementSpec
     * @throws GordianException on error
     */
    BouncyXAgreementBase(final BouncyXAgreementFactory pFactory,
                         final GordianAgreementSpec pSpec) throws GordianException {
        /* Invoke underlying constructor */
        super(pFactory, pSpec);

        /* Obtain underlying details */
        theBuilder = getBuilder();
        theState = theBuilder.getState();
        theClient = theState.getClient();
        theServer = theState.getServer();
    }

    /**
     * Obtain the random.
     * @return the random
     */
    SecureRandom getRandom() {
        return theBuilder.getRandom();
    }

    /**
     * Obtain client keyPair.
     * @return the client keyPair
     */
    GordianKeyPair getClientKeyPair() {
        return theClient.getKeyPair();
    }

    /**
     * Obtain server keyPair.
     * @return the server keyPair
     */
    GordianKeyPair getServerKeyPair() {
        return theServer.getKeyPair();
    }

    /**
     * Obtain client ephemeral keyPair.
     * @return the ephemeral keyPair
     */
    GordianKeyPair getClientEphemeral() {
        return theClient.getEphemeralKeyPair();
    }

    /**
     * Obtain server keyPair.
     * @return the server keyPair
     */
    GordianKeyPair getServerEphemeral() {
        return theServer.getEphemeralKeyPair();
    }

    /**
     * Obtain encapsulation.
     * @return the encapsulation
     */
    byte[] getEncapsulated() {
        return theState.getEncapsulated();
    }

    /**
     * Obtain server keyPair.
     * @param pEncapsulated the encapsulated
     */
    void setEncapsulated(final byte[] pEncapsulated) {
        theState.setEncapsulated(pEncapsulated);
    }

    /**
     * Obtain public key from pair.
     * @param pKeyPair the keyPair
     * @return the public key
     */
    GordianPublicKey getPublicKey(final GordianKeyPair pKeyPair) throws GordianException {
        /* Validate the keyPair */
        if (!(pKeyPair instanceof BouncyKeyPair)) {
            /* Reject keyPair */
            throw new GordianDataException("Invalid KeyPair");
        }

        /* Access private key */
        return ((GordianCoreKeyPair) pKeyPair).getPublicKey();
    }

    /**
     * Obtain private key from pair.
     * @param pKeyPair the keyPair
     * @return the private key
     * @throws GordianException on error
     */
    GordianPrivateKey getPrivateKey(final GordianKeyPair pKeyPair) throws GordianException {
        /* Validate the keyPair */
        if (!(pKeyPair instanceof BouncyKeyPair)) {
            /* Reject keyPair */
            throw new GordianDataException("Invalid KeyPair");
        }
        if (pKeyPair.isPublicOnly()) {
            throw new GordianDataException("missing privateKey");
        }

        /* Access private key */
        return ((GordianCoreKeyPair) pKeyPair).getPrivateKey();
    }

    /**
     * Store the secret.
     * @param pSecret the secret
     * @throws GordianException on error
     */
    void storeSecret(final byte[] pSecret) throws GordianException {
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
                theBuilder.storeSecret(mySecret);

            } else {
                /* Just process the secret */
                theBuilder.storeSecret(pSecret);
            }

            /* Clear buffers */
        } finally {
            /* Clear the secret */
            Arrays.fill(mySecret, (byte) 0);
            Arrays.fill(pSecret, (byte) 0);
        }
    }

    /**
     * Obtain the required derivation function.
     * @return the derivation function
     */
    DerivationFunction newDerivationFunction() {
        final GordianAgreementSpec mySpec = theState.getSpec();
        switch (mySpec.getKDFType()) {
            case SHA256KDF:     return new KDF2BytesGenerator(new SHA256Digest());
            case SHA512KDF:     return new KDF2BytesGenerator(new SHA512Digest());
            case SHA256CKDF:    return new ConcatenationKDFGenerator(new SHA256Digest());
            case SHA512CKDF:    return new ConcatenationKDFGenerator(new SHA512Digest());
            case NONE:
            default:            return new GordianNullKeyDerivation();
        }
    }
}
