/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.manager;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactoryGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Factory generator.
 */
public class GordianGenerator implements GordianFactoryGenerator {
    @Override
    public GordianFactory newFactory(final GordianParameters pParameters) throws OceanusException {
        /* Allocate the factory */
        return GordianFactoryType.BC.equals(pParameters.getFactoryType())
               ? new BouncyFactory(pParameters, this)
               : new JcaFactory(pParameters, this);
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
               ? new BouncyFactory(pParameters, myGenerator)
               : new JcaFactory(pParameters, myGenerator);
    }
}
