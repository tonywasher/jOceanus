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
package net.sourceforge.joceanus.jgordianknot.impl.core.mac;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianRandomSource;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.key.GordianCoreKeyGenerator;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot interface for Message Authentication Codes.
 */
public abstract class GordianCoreMac
        implements GordianMac {
    /**
     * MacSpec.
     */
    private final GordianMacSpec theMacSpec;

    /**
     * The Security Factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The Random Generator.
     */
    private final GordianRandomSource theRandom;

    /**
     * The KeyGenerator.
     */
    private GordianCoreKeyGenerator<GordianMacSpec> theGenerator;

    /**
     * keyLength.
     */
    private final GordianLength theKeyLength;

    /**
     * Key.
     */
    private GordianKey<GordianMacSpec> theKey;

    /**
     * InitialisationVector.
     */
    private byte[] theInitVector;

    /**
     * Constructor.
     *
     * @param pFactory the Security Factory
     * @param pMacSpec the macSpec
     */
    protected GordianCoreMac(final GordianCoreFactory pFactory,
                             final GordianMacSpec pMacSpec) {
        theMacSpec = pMacSpec;
        theFactory = pFactory;
        theRandom = pFactory.getRandomSource();
        theKeyLength = pMacSpec.getKeyLength();
    }

    @Override
    public GordianMacSpec getMacSpec() {
        return theMacSpec;
    }

    @Override
    public GordianKey<GordianMacSpec> getKey() {
        return theKey;
    }

    @Override
    public byte[] getInitVector() {
        return theInitVector;
    }

    /**
     * Store key.
     *
     * @param pKey the key
     */
    protected void setKey(final GordianKey<GordianMacSpec> pKey) {
        theKey = pKey;
    }

    /**
     * Store initVector.
     *
     * @param pInitVector the initVector
     */
    protected void setInitVector(final byte[] pInitVector) {
        theInitVector = pInitVector;
    }

    /**
     * Check that the key matches the keyType.
     *
     * @param pKey the passed key.
     * @throws OceanusException on error
     */
    protected void checkValidKey(final GordianKey<GordianMacSpec> pKey) throws OceanusException {
        if (!theMacSpec.equals(pKey.getKeyType())) {
            throw new GordianLogicException("MisMatch on macSpec");
        }
    }

    @Override
    public void initMac(final byte[] pKeyBytes) throws OceanusException {
        /* Create generator if needed */
        if (theGenerator == null) {
            final GordianMacFactory myFactory = theFactory.getMacFactory();
            theGenerator = (GordianCoreKeyGenerator<GordianMacSpec>) myFactory.getKeyGenerator(theMacSpec);
        }

        /* Create the key and initialise */
        final GordianKey<GordianMacSpec> myKey = theGenerator.buildKeyFromBytes(pKeyBytes);
        initMac(myKey);
    }

    @Override
    public void initMac(final GordianKey<GordianMacSpec> pKey) throws OceanusException {
        /* Determine the required length of IV */
        final int myLen = getMacSpec().getIVLen(theKeyLength);
        byte[] myIV = null;

        /* If we need an IV */
        if (myLen > 0) {
            /* Create a random IV */
            myIV = new byte[myLen];
            theRandom.getRandom().nextBytes(myIV);
        }

        /* initialise with this IV */
        initMac(pKey, myIV);
    }
}
