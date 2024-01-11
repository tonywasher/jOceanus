/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.password;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianFactoryGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianRandomSource;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Builder methods.
 */
public final class GordianBuilder {
    /**
     * Private class.
     */
    private GordianBuilder() {
    }

    /**
     * Create a new factory instance.
     * @param pFactoryType the factoryType
     * @param pSecurityPhrase the securityPhrase
     * @return the new factory
     * @throws OceanusException on error
     */
    public static GordianFactory createFactory(final GordianFactoryType pFactoryType,
                                               final char[] pSecurityPhrase) throws OceanusException {
        /* Allocate a generator and the parameters */
        final GordianFactoryGenerator myGenerator = new GordianUtilGenerator();
        final GordianParameters myParams = new GordianParameters(pFactoryType);
        myParams.setSecurityPhrase(pSecurityPhrase);
        return myGenerator.newFactory(myParams);
    }

    /**
     * Create a new random bouncyCastle factory instance.
     * @return the new factory
     * @throws OceanusException on error
     */
    public static GordianFactory createRandomFactory() throws OceanusException {
        /* Allocate a generator and the parameters */
        final GordianFactoryGenerator myGenerator = new GordianUtilGenerator();
        final GordianParameters myParams = new GordianParameters(GordianFactoryType.BC);
        myParams.setSecuritySeeds(GordianRandomSource.getStrongRandom());
        myParams.setInternal();
        return myGenerator.newFactory(myParams);
    }

    /**
     * True Factory generator.
     */
    private static class GordianUtilGenerator
            implements GordianFactoryGenerator {
        /**
         * Constructor.
         */
        GordianUtilGenerator() {
        }

        @Override
        public GordianFactory newFactory(final GordianParameters pParameters) throws OceanusException {
            /* Allocate the factory */
            return GordianFactoryType.BC.equals(pParameters.getFactoryType())
                    ? new BouncyFactory(this, pParameters)
                    : new JcaFactory(this, pParameters);
        }
    }
}
