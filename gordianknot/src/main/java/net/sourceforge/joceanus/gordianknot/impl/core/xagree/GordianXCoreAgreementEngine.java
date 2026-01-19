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
package net.sourceforge.joceanus.gordianknot.impl.core.xagree;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianCoreKeyPair;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianPrivateKey;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianPublicKey;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementDerivation.GordianXCoreNullKeyDerivation;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.agreement.kdf.ConcatenationKDFGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.util.Arrays;

import java.security.SecureRandom;

/**
 * Implementation engine for Agreements.
 */
public abstract class GordianXCoreAgreementEngine {
    /**
     * The supplier.
     */
    private final GordianXCoreAgreementSupplier theSupplier;

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
     * The keyDerivation function.
     */
    private GordianXCoreAgreementDerivation theKDF;

    /**
     * Constructor.
     *
     * @param pSupplier the supplier
     * @param pSpec     the agreementSpec
     * @throws GordianException on error
     */
    protected GordianXCoreAgreementEngine(final GordianXCoreAgreementSupplier pSupplier,
                                          final GordianAgreementSpec pSpec) throws GordianException {
        theSupplier = pSupplier;
        theBuilder = new GordianXCoreAgreementBuilder(pSupplier, pSpec);
        theState = theBuilder.getState();
        theClient = theState.getClient();
        theServer = theState.getServer();
    }

    /**
     * Obtain the supplier.
     *
     * @return the supplier
     */
    GordianXCoreAgreementSupplier getSupplier() {
        return theSupplier;
    }

    /**
     * Obtain the builder.
     *
     * @return the builder
     */
    public GordianXCoreAgreementBuilder getBuilder() {
        return theBuilder;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianAgreementSpec getSpec() {
        return theState.getSpec();
    }

    /**
     * Obtain the random.
     *
     * @return the random
     */
    public SecureRandom getRandom() {
        return theBuilder.getRandom();
    }

    /**
     * Obtain client keyPair.
     *
     * @return the client keyPair
     */
    public GordianKeyPair getClientKeyPair() {
        return theClient.getKeyPair();
    }

    /**
     * Obtain server keyPair.
     *
     * @return the server keyPair
     */
    public GordianKeyPair getServerKeyPair() {
        return theServer.getKeyPair();
    }

    /**
     * Obtain client ephemeral keyPair.
     *
     * @return the ephemeral keyPair
     */
    public GordianKeyPair getClientEphemeral() {
        return theClient.getEphemeralKeyPair();
    }

    /**
     * Set client ephemeral keyPair as Encapsulated.
     *
     * @param pEphemeral the ephemeral keyPair
     * @throws GordianException on error
     */
    public void setClientEphemeralAsEncapsulated(final GordianKeyPair pEphemeral) throws GordianException {
        theBuilder.setClientEphemeralAsEncapsulated(pEphemeral);
    }

    /**
     * Obtain server ephemeral keyPair.
     *
     * @return the ephemeral keyPair
     */
    public GordianKeyPair getServerEphemeral() {
        return theServer.getEphemeralKeyPair();
    }

    /**
     * Obtain encapsulation.
     *
     * @return the encapsulation
     */
    public byte[] getEncapsulated() {
        return theState.getEncapsulated();
    }

    /**
     * Set encapsulation.
     *
     * @param pEncapsulated the encapsulated
     */
    public void setEncapsulated(final byte[] pEncapsulated) {
        theState.setEncapsulated(pEncapsulated);
    }

    /**
     * Set the client confirm.
     *
     * @param pConfirm the clientConfirm
     * @return noError true/false
     */
    public boolean setClientConfirm(final byte[] pConfirm) {
        return theBuilder.setClientConfirm(pConfirm);
    }

    /**
     * Obtain server Confirm.
     *
     * @return the serverConfirm
     */
    public byte[] getServerConfirm() {
        return theServer.getConfirm();
    }

    /**
     * Set the server confirm.
     *
     * @param pConfirm the serverConfirm
     */
    public void setServerConfirm(final byte[] pConfirm) {
        theBuilder.setServerConfirm(pConfirm);
    }

    /**
     * Obtain public key from pair.
     *
     * @param pKeyPair the keyPair
     * @return the public key
     * @throws GordianException on error
     */
    protected GordianPublicKey getPublicKey(final GordianKeyPair pKeyPair) throws GordianException {
        /* Access public key */
        return ((GordianCoreKeyPair) pKeyPair).getPublicKey();
    }

    /**
     * Obtain private key from pair.
     *
     * @param pKeyPair the keyPair
     * @return the private key
     * @throws GordianException on error
     */
    protected GordianPrivateKey getPrivateKey(final GordianKeyPair pKeyPair) throws GordianException {
        /* Validate the keyPair */
        if (pKeyPair.isPublicOnly()) {
            throw new GordianDataException("missing privateKey");
        }

        /* Access private key */
        return ((GordianCoreKeyPair) pKeyPair).getPrivateKey();
    }

    /**
     * Build the clientHello.
     *
     * @throws GordianException on error
     */
    public void buildClientHello() throws GordianException {
        /* NoOp */
    }

    /**
     * Process the clientHello.
     *
     * @throws GordianException on error
     */
    public abstract void processClientHello() throws GordianException;

    /**
     * Process the serverHello.
     *
     * @throws GordianException on error
     */
    public void processServerHello() throws GordianException {
        /* NoOp */
    }

    /**
     * Process the clientConfirm.
     *
     * @throws GordianException on error
     */
    public void processClientConfirm() throws GordianException {
        /* NoOp */
    }

    /**
     * Store the secret.
     *
     * @param pSecret the secret
     * @throws GordianException on error
     */
    public void storeSecret(final byte[] pSecret) throws GordianException {
        /* Protect against failure */
        try {
            /* If we have a kdf */
            if (theKDF != null) {
                /* Create the secret */
                theKDF.deriveBytes(pSecret);

                /* Just process the secret */
            } else {
                theBuilder.storeSecret(pSecret);
            }

            /* Clear the secret */
        } finally {
            Arrays.fill(pSecret, (byte) 0);
        }
    }

    /**
     * Enable additional derivation of secret.
     */
    protected void enableDerivation() {
        /* Only enable derivation if it is not none */
        final GordianAgreementSpec mySpec = theState.getSpec();
        if (!GordianKDFType.NONE.equals(mySpec.getKDFType())) {
            theKDF = new GordianXCoreAgreementDerivation(theBuilder);
        }
    }

    /**
     * Obtain the required derivation function.
     *
     * @return the derivation function
     */
    public DerivationFunction newDerivationFunction() {
        final GordianAgreementSpec mySpec = theState.getSpec();
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
                return new GordianXCoreNullKeyDerivation();
        }
    }
}
