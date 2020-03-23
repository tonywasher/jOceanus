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
package net.sourceforge.joceanus.jgordianknot.impl.core.base;

import java.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Security Parameters.
 */
public class GordianParameters {
    /**
     * Default Factory.
     */
    public static final GordianFactoryType DEFAULT_FACTORY = GordianFactoryType.BC;

    /**
     * The Factory Type.
     */
    private GordianFactoryType theFactoryType;

    /**
     * The Security phrase.
     */
    private byte[] theSecurityPhrase;

    /**
     * Is this an internal set?
     */
    private boolean isInternal;

    /**
     * Default Constructor.
     */
    public GordianParameters() {
        this(DEFAULT_FACTORY);
    }

    /**
     * Constructor.
     * @param pFactoryType the factory type
     */
    public GordianParameters(final GordianFactoryType pFactoryType) {
        /* Store parameters */
        theFactoryType = pFactoryType;
    }

    /**
     * Access the factory type.
     * @return the factory type
     */
    public GordianFactoryType getFactoryType() {
        return theFactoryType;
    }

    /**
     * Access the security phrase in bytes format.
     * @return the security phrase
     */
    public byte[] getSecurityPhrase() {
        return theSecurityPhrase;
    }

    /**
     * Is this an internal set of parameters.
     * @return true/false
     */
    public boolean isInternal() {
        return isInternal;
    }

    /**
     * Set factory type.
     * @param pType the factory type
     */
    public void setFactoryType(final GordianFactoryType pType) {
        theFactoryType = pType;
    }

    /**
     * Set security phrase.
     * @param pSecurityPhrase the security phrase (or null)
     * @throws OceanusException on error
     */
    public void setSecurityPhrase(final char[] pSecurityPhrase) throws OceanusException {
        theSecurityPhrase = pSecurityPhrase == null
                                ? null
                                : TethysDataConverter.charsToByteArray(pSecurityPhrase);
    }

    /**
     * Set security phrase.
     * @param pSecurityPhrase the security phrase (or null)
     */
    public void setSecurityPhrase(final byte[] pSecurityPhrase) {
        theSecurityPhrase = pSecurityPhrase == null
                            ? null
                            : Arrays.copyOf(pSecurityPhrase, pSecurityPhrase.length);
    }

    /**
     * Set internal.
     */
    public void setInternal() {
        isInternal = true;
    }

    /**
     * Validate the Parameters.
     * @return valid true/false
     */
    public boolean validate() {
        /* Check factory type */
        return theFactoryType != null;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (pThat == this) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is the same class */
        if (!(pThat instanceof GordianParameters)) {
            return false;
        }

        /* Access the target field */
        final GordianParameters myThat = (GordianParameters) pThat;

        /* Check Differences */
        if (theFactoryType != myThat.getFactoryType()
            || isInternal != myThat.isInternal()) {
            return false;
        }

        /* Check phrase */
        return theSecurityPhrase == null
               ? myThat.getSecurityPhrase() == null
               : Arrays.equals(theSecurityPhrase, myThat.getSecurityPhrase());
    }

    @Override
    public int hashCode() {
        /* Access multiplier */
        final int myPrime = GordianCoreFactory.HASH_PRIME;

        /* Calculate hash from types */
        int myCode = theFactoryType.hashCode();
        if (isInternal) {
            myCode++;
        }
        myCode *= myPrime;

        /* Calculate hash from phrase */
        return myCode + (theSecurityPhrase == null
                         ? 0
                         : Arrays.hashCode(theSecurityPhrase));
    }
}
