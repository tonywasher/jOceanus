/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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

import java.io.IOException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianEphemeralAgreement;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Ephemeral Agreement.
 */
public abstract class GordianCoreEphemeralAgreement
        extends GordianCoreAgreement
        implements GordianEphemeralAgreement {
    /**
     * The owning KeyPair.
     */
    private GordianKeyPair theOwner;

    /**
     * The ephemeral KeyPair.
     */
    private GordianKeyPair theEphemeral;

    /**
     * The partner ephemeral KeyPair.
     */
    private GordianKeyPair thePartnerEphemeral;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     */
    protected GordianCoreEphemeralAgreement(final GordianCoreFactory pFactory,
                                            final GordianAgreementSpec pSpec) {
        super(pFactory, pSpec);
    }

    /**
     * Obtain the Ephemeral keyPair.
     * @return  the keyPair
     */
    protected GordianKeyPair getOwnerKeyPair() {
        return theOwner;
    }

    /**
     * Obtain the Ephemeral keyPair.
     * @return  the keyPair
     */
    protected GordianKeyPair getEphemeralKeyPair() {
        return theEphemeral;
    }

    /**
     * Obtain the partner Ephemeral keyPair.
     * @return  the keyPair
     */
    protected GordianKeyPair getPartnerEphemeralKeyPair() {
        return thePartnerEphemeral;
    }

    @Override
    public byte[] initiateAgreement(final GordianKeyPair pInitiator) throws OceanusException {
        /* Check the keyPair */
        checkKeyPair(pInitiator);

        /* Store the keyPair */
        theOwner = pInitiator;

        /* Create ephemeral key */
        final GordianAsymFactory myAsym = getFactory().getAsymmetricFactory();
        final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(theOwner.getKeySpec());
        theEphemeral = myGenerator.generateKeyPair();
        final X509EncodedKeySpec myKeySpec = myGenerator.getX509Encoding(theEphemeral);
        final byte[] myKeyBytes = myKeySpec.getEncoded();

        /* Build the sequence */
        try {
            /* Create the request */
            final GordianCoreAgreementFactory myFactory = getAgreementFactory();
            final AlgorithmIdentifier myAlgId = myFactory.getIdentifierForSpec(getAgreementSpec());
            final AlgorithmIdentifier myResId = getIdentifierForResult();
            final GordianAgreementRequestASN1 myRequest = new GordianAgreementRequestASN1(myAlgId, myResId, newInitVector(), myKeyBytes);
            return myRequest.getEncoded();

        } catch (IOException e) {
            throw new GordianIOException("Unable to build ASN1 sequence", e);
        }
    }

    /**
     * Parse the incoming request and create the response.
     * <pre>
     * GordianEphemeralResponse ::= SEQUENCE  {
     *      id AlgorithmIdentifier
     *      keyBytes OCTET STRING
     * }
     * </pre>
     * @param pResponder the responding keyPair
     * @param pMessage the incoming message
     * @return the ephemeral keySpec
     * @throws OceanusException on error
     */
    protected byte[] parseMessage(final GordianKeyPair pResponder,
                                  final byte[] pMessage) throws OceanusException {
        /* Check the keyPair */
        checkKeyPair(pResponder);

        /* Store the keyPair */
        theOwner = pResponder;

        /* Parse the sequence */
        try {
            /* Access the sequence */
            final GordianAgreementRequestASN1 myRequest = GordianAgreementRequestASN1.getInstance(pMessage);

            /* Access message parts */
            final AlgorithmIdentifier myAlgId = myRequest.getAgreementId();
            final AlgorithmIdentifier myResId = myRequest.getResultId();
            final byte[] myInitVector = myRequest.getInitVector();
            byte[] myKeyBytes = myRequest.getData();

            /* Check agreementSpec */
            final GordianCoreAgreementFactory myFactory = getAgreementFactory();
            final GordianAgreementSpec mySpec = myFactory.getSpecForIdentifier(myAlgId);
            if (!Objects.equals(mySpec, getAgreementSpec())) {
                throw new GordianDataException(ERROR_INVSPEC);
            }

            /* Process result identifier */
            processResultIdentifier(myResId);

            /* Store the initVector */
            storeInitVector(myInitVector);

            /* Parse the ephemeral encoding */
            final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myKeyBytes);

            /* Create ephemeral key */
            final GordianAsymFactory myAsym = getFactory().getAsymmetricFactory();
            final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(theOwner.getKeySpec());
            theEphemeral = myGenerator.generateKeyPair();

            /* Derive partner ephemeral key */
            thePartnerEphemeral = myGenerator.derivePublicOnlyKeyPair(myKeySpec);

            /* Create the response */
            myKeyBytes = myGenerator.getX509Encoding(theEphemeral).getEncoded();
            final GordianAgreementResponseASN1 myResponse = new GordianAgreementResponseASN1(myAlgId, myKeyBytes);
            return myResponse.getEncoded();

            /* Catch exceptions */
        } catch (IllegalArgumentException
                 | IOException e) {
            throw new GordianIOException("Unable to parse ASN1 sequence", e);
        }
    }

    /**
     * Parse the ephemeral keySpec.
     * @param pKeySpec the target ephemeral keySpec
     * @throws OceanusException on error
     */
    protected void parseEphemeral(final byte[] pKeySpec) throws OceanusException {
        /* Parse the sequence */
        try {
            /* Access the sequence */
            final GordianAgreementResponseASN1 myRequest = GordianAgreementResponseASN1.getInstance(pKeySpec);

            /* Access message parts */
            final AlgorithmIdentifier myAlgId = myRequest.getAgreementId();
            final byte[] myKeyBytes = myRequest.getData();

            /* Check agreementSpec */
            final GordianCoreAgreementFactory myFactory = getAgreementFactory();
            final GordianAgreementSpec mySpec = myFactory.getSpecForIdentifier(myAlgId);
            if (!Objects.equals(mySpec, getAgreementSpec())) {
                throw new GordianDataException(ERROR_INVSPEC);
            }

            /* Obtain keySpec */
            final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myKeyBytes);

            /* Derive partner ephemeral key */
            final GordianAsymFactory myAsym = getFactory().getAsymmetricFactory();
            final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(theOwner.getKeySpec());
            thePartnerEphemeral = myGenerator.derivePublicOnlyKeyPair(myKeySpec);

            /* Catch exceptions */
        } catch (IllegalArgumentException e) {
            throw new GordianIOException("Unable to parse ASN1 sequence", e);
        }
    }
}
