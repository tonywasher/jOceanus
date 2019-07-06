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
package net.sourceforge.joceanus.jgordianknot.util;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianFactoryGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Factory generator.
 */
public class GordianGenerator implements GordianFactoryGenerator {
    @Override
    public GordianFactory newFactory(final GordianParameters pParameters) throws OceanusException {
        /* Allocate the factory */
        return GordianFactoryType.BC.equals(pParameters.getFactoryType())
               ? new BouncyFactory(this, pParameters)
               : new JcaFactory(this, pParameters);
    }

    /**
     * Create a new factory instance.
     * @param pParameters the Security parameters
     * @return the new factory
     * @throws OceanusException on error
     */
    public static GordianFactory createFactory(final GordianParameters pParameters) throws OceanusException {
        /* Allocate a stub HashManager */
        final GordianFactoryGenerator myGenerator = new GordianGenerator();
        return GordianFactoryType.BC.equals(pParameters.getFactoryType())
               ? new BouncyFactory(myGenerator, pParameters)
               : new JcaFactory(myGenerator, pParameters);
    }
}
