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
package net.sourceforge.joceanus.jgordianknot.impl.core.agree;

import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementStatus;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairSignedAgreement;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianKeyPairSignature;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.sign.GordianCoreSignatureFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Signed Agreement.
 */
public abstract class GordianCoreSignedAgreement
        extends GordianCoreKeyPairAgreement
        implements GordianKeyPairSignedAgreement {
    /**
     * The client ephemeral KeyPair.
     */
    private GordianKeyPair theClientEphemeral;

    /**
     * The server ephemeral KeyPair.
     */
    private GordianKeyPair theServerEphemeral;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     */
    protected GordianCoreSignedAgreement(final GordianCoreFactory pFactory,
                                         final GordianKeyPairAgreementSpec pSpec) {
        super(pFactory, pSpec);
    }

    /**
     * Obtain the client Ephemeral keyPair.
     * @return  the keyPair
     */
    protected GordianKeyPair getClientEphemeralKeyPair() {
        return theClientEphemeral;
    }

    /**
     * Obtain the server Ephemeral keyPair.
     * @return  the keyPair
     */
    protected GordianKeyPair getServerEphemeralKeyPair() {
        return theServerEphemeral;
    }

    @Override
    public void reset() {
        /* Reset underlying details */
        super.reset();

        /* Reset keyPair details */
        theClientEphemeral = null;
        theServerEphemeral = null;
    }

    @Override
    public byte[] createClientHello(final GordianKeyPairSpec pKeySpec) throws OceanusException {
        /* Check that the keySpec matches the agreement */
        if (getAgreementSpec().getKeyPairType() != pKeySpec.getKeyPairType()) {
            throw new GordianDataException("Incorrect KeySpec type");
        }

        /* Create ephemeral key */
        final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pKeySpec);
        theClientEphemeral = myGenerator.generateKeyPair();
        final X509EncodedKeySpec myKeySpec = myGenerator.getX509Encoding(theClientEphemeral);
        final byte[] myKeyBytes = myKeySpec.getEncoded();

        /* Create the clientHello message */
        final byte[] myClientHello = buildClientHello(myKeyBytes);

        /* Set status */
        setStatus(GordianAgreementStatus.AWAITING_SERVERHELLO);

        /* Return the clientHello */
        return myClientHello;
    }

    /**
     * Process the incoming clientHello message request.
     * @param pClientHello the incoming clientHello message
     * @throws OceanusException on error
     */
    protected void processClientHello(final byte[] pClientHello) throws OceanusException {
        /* Parse the request */
        final byte[] myKeyBytes = parseClientHello(pClientHello);

        /* Parse the ephemeral encoding */
        final X509EncodedKeySpec myEncodedKeySpec = new X509EncodedKeySpec(myKeyBytes);

        /* Create ephemeral key */
        final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
        final GordianKeyPairSpec myKeySpec = myFactory.determineKeyPairSpec(myEncodedKeySpec);
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(myKeySpec);
        theServerEphemeral = myGenerator.generateKeyPair();

        /* Derive partner ephemeral key */
        theClientEphemeral = myGenerator.derivePublicOnlyKeyPair(myEncodedKeySpec);

        /* Create the new serverIV */
        newServerIV();
    }

    /**
     * build the serverHello message.
     * @param pServer the server keyPair
     * @return the serverHello message
     * @throws OceanusException on error
     */
    protected byte[] buildServerHello(final GordianKeyPair pServer) throws OceanusException {
        /* Obtain the encoding for the server ephemeral publicKey */
        final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theServerEphemeral.getKeyPairSpec());
        final byte[] myClientEncoded = myGenerator.getX509Encoding(theClientEphemeral).getEncoded();
        final byte[] myServerEncoded = myGenerator.getX509Encoding(theServerEphemeral).getEncoded();

        /* Create the signer */
        final GordianSignatureSpec mySpec = GordianSignatureSpec.defaultForKey(pServer.getKeyPairSpec());
        final GordianCoreSignatureFactory mySigns = (GordianCoreSignatureFactory) myFactory.getSignatureFactory();
        final AlgorithmIdentifier myAlgId = mySigns.getIdentifierForSpecAndKeyPair(mySpec, pServer);
        final GordianKeyPairSignature mySigner = mySigns.createKeyPairSigner(mySpec);

        /* Build the signature */
        mySigner.initForSigning(pServer);
        mySigner.update(myClientEncoded);
        mySigner.update(getClientIV());
        mySigner.update(myServerEncoded);
        mySigner.update(getServerIV());
        final byte[] mySignature = mySigner.sign();

        /* Build the server hello */
        return buildServerHello(myServerEncoded, myAlgId, mySignature);
    }

    /**
     * Process the serverHello.
     * @param pServer the server keyPair
     * @param pServerHello the serverHello message
     * @throws OceanusException on error
     */
    protected void processServerHello(final GordianKeyPair pServer,
                                      final byte[] pServerHello) throws OceanusException {
        /* Obtain keySpec */
        final GordianAgreementServerHelloASN1 myASN1 = parseServerHello(pServerHello);
        final byte[] myData = myASN1.getData();
        final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myData);

        /* Derive partner ephemeral key */
        final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theClientEphemeral.getKeyPairSpec());
        theServerEphemeral = myGenerator.derivePublicOnlyKeyPair(myKeySpec);
        final byte[] myClientEncoded = myGenerator.getX509Encoding(theClientEphemeral).getEncoded();

        /* Create the signer */
        final GordianCoreSignatureFactory mySigns = (GordianCoreSignatureFactory) myFactory.getSignatureFactory();
        final AlgorithmIdentifier myAlgId = myASN1.getSignatureId();
        final byte[] mySignature = myASN1.getSignature();
        final GordianSignatureSpec mySignSpec = mySigns.getSpecForIdentifier(myAlgId);
        final GordianKeyPairSignature mySigner = mySigns.createKeyPairSigner(mySignSpec);

        /* Build the signature */
        mySigner.initForVerify(pServer);
        mySigner.update(myClientEncoded);
        mySigner.update(getClientIV());
        mySigner.update(myKeySpec.getEncoded());
        mySigner.update(getServerIV());
        if (!mySigner.verify(mySignature)) {
            throw new GordianDataException("Signature failed");
        }
    }
}
