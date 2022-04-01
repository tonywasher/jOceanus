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
package net.sourceforge.joceanus.jgordianknot.impl.core.digest;

import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Base Digest Factory.
 */
public abstract class GordianCoreDigestFactory
        implements GordianDigestFactory {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * Constructor.
     * @param pFactory the factory.
     */
    protected GordianCoreDigestFactory(final GordianCoreFactory pFactory) {
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
    public Predicate<GordianDigestSpec> supportedDigestSpecs() {
        return this::validDigestSpec;
    }

    @Override
    public Predicate<GordianDigestType> supportedDigestTypes() {
        return this::validDigestType;
    }

    @Override
    public Predicate<GordianDigestType> supportedExternalDigestTypes() {
        final GordianMacFactory myMacFactory = theFactory.getMacFactory();
        return myMacFactory.supportedHMacDigestTypes().and(GordianDigestType::isExternalHashDigest);
    }

    /**
     * Check digestSpec.
     * @param pDigestSpec the digestSpec
     * @throws OceanusException on error
     */
    public void checkDigestSpec(final GordianDigestSpec pDigestSpec) throws OceanusException {
        /* Check validity of DigestType */
        if (!supportedDigestSpecs().test(pDigestSpec)) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pDigestSpec));
        }
    }

    /**
     * Check DigestSpec.
     * @param pDigestSpec the digestSpec
     * @return true/false
     */
    public boolean validDigestSpec(final GordianDigestSpec pDigestSpec) {
        /* Reject invalid digestSpec */
        if (pDigestSpec == null || !pDigestSpec.isValid()) {
            return false;
        }

        /* Access details */
        final GordianDigestType myType = pDigestSpec.getDigestType();
        final GordianLength myStateLen = pDigestSpec.getStateLength();
        final GordianLength myLen = pDigestSpec.getDigestLength();

        /* Check validity */
        return supportedDigestTypes().test(myType)
                && myType.isLengthValid(myLen)
                && myType.isStateValidForLength(myStateLen, myLen);
    }

    /**
     * Check DigestType.
     * @param pDigestType the digestType
     * @return true/false
     */
    public boolean validDigestType(final GordianDigestType pDigestType) {
        return true;
    }
}
