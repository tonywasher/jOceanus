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
package net.sourceforge.joceanus.gordianknot.util;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianFactoryGenerator;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaFactory;

/**
 * Factory generator.
 */
public final class GordianGenerator {
    /**
     * Private  Constructor.
     */
    private GordianGenerator() {
    }

    /**
     * Create a new factory instance.
     * @param pFactoryType the factoryType
     * @return the new factory
     * @throws GordianException on error
     */
    public static GordianFactory createFactory(final GordianFactoryType pFactoryType) throws GordianException {
        /* Create a factory with null security phrase */
        return createFactory(pFactoryType, null);
    }

    /**
     * Create a new factory instance.
     * @param pFactoryType the factoryType
     * @param pSecurityPhrase the securityPhrase
     * @return the new factory
     * @throws GordianException on error
     */
    public static GordianFactory createFactory(final GordianFactoryType pFactoryType,
                                               final char[] pSecurityPhrase) throws GordianException {
        /* Allocate a generator and the parameters */
        final GordianFactoryGenerator myGenerator = new GordianUtilGenerator();
        final GordianParameters myParams = new GordianParameters(pFactoryType, pSecurityPhrase);
        return myGenerator.newFactory(myParams);
    }

    /**
     * Create a new random bouncyCastle factory instance.
     * @param pFactoryType the factory type
     * @return the new factory
     * @throws GordianException on error
     */
    public static GordianFactory createRandomFactory(final GordianFactoryType pFactoryType) throws GordianException {
        /* Allocate a generator and the parameters */
        final GordianFactoryGenerator myGenerator = new GordianUtilGenerator();
        final GordianParameters myParams = GordianParameters.randomParams(pFactoryType);
        return myGenerator.newFactory(myParams);
    }

    /**
     * True Factory generator.
     */
    static class GordianUtilGenerator
            implements GordianFactoryGenerator {
        /**
         * Constructor.
         */
        GordianUtilGenerator() {
        }

        @Override
        public GordianFactory newFactory(final GordianParameters pParameters) throws GordianException {
            /* Allocate the factory */
            return GordianFactoryType.BC.equals(pParameters.getFactoryType())
                    ? new BouncyFactory(this, pParameters)
                    : new JcaFactory(this, pParameters);
        }
    }
}
