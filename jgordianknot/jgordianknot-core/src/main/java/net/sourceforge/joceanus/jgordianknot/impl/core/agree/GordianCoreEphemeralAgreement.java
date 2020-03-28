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

import java.security.spec.X509EncodedKeySpec;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianEphemeralAgreement;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
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

        /* Create the request */
        return createRequest(myKeyBytes);
    }

    /**
     * Parse the incoming request and create the response.
     * @param pResponder the responding keyPair
     * @param pMessage the incoming message
     * @return the response message
     * @throws OceanusException on error
     */
    protected byte[] parseRequest(final GordianKeyPair pResponder,
                                  final byte[] pMessage) throws OceanusException {
        /* Check the keyPair */
        checkKeyPair(pResponder);

        /* Store the keyPair */
        theOwner = pResponder;

        /* Parse the request */
        final byte[] myKeyBytes = parseRequest(pMessage);

        /* Parse the ephemeral encoding */
        final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myKeyBytes);

        /* Create ephemeral key */
        final GordianAsymFactory myAsym = getFactory().getAsymmetricFactory();
        final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(theOwner.getKeySpec());
        theEphemeral = myGenerator.generateKeyPair();

        /* Derive partner ephemeral key */
        thePartnerEphemeral = myGenerator.derivePublicOnlyKeyPair(myKeySpec);

        /* Create the response */
        return createResponse(myGenerator.getX509Encoding(theEphemeral).getEncoded());
    }

    /**
     * Parse the ephemeral keySpec.
     * @param pResponse the response message
     * @throws OceanusException on error
     */
    protected void parseEphemeral(final byte[] pResponse) throws OceanusException {
        /* Obtain keySpec */
        final X509EncodedKeySpec myKeySpec = parseResponse(pResponse);

        /* Derive partner ephemeral key */
        final GordianAsymFactory myAsym = getFactory().getAsymmetricFactory();
        final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(theOwner.getKeySpec());
        thePartnerEphemeral = myGenerator.derivePublicOnlyKeyPair(myKeySpec);
    }
}
