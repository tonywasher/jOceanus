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
package net.sourceforge.joceanus.jgordianknot.impl.bc;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianFactoryGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.random.GordianCoreRandomFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.zip.GordianCoreZipFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * BouncyCastle Factory.
 */
public class BouncyFactory
    extends GordianCoreFactory {
    /**
     * Zip Factory.
     */
    private GordianCoreZipFactory theZipFactory;

    /**
     * Asym Factory.
     */
    private BouncyAsymFactory theAsymFactory;

    /**
     * Constructor.
     * @param pGenerator the factory generator
     * @param pParameters the security parameters
     * @throws OceanusException on error
     */
    public BouncyFactory(final GordianFactoryGenerator pGenerator,
                         final GordianParameters pParameters) throws OceanusException {
        /* initialise underlying factory */
        super(pGenerator, pParameters);

        /* Create the factories */
        setDigestFactory(new BouncyDigestFactory(this));
        setCipherFactory(new BouncyCipherFactory(this));
        setMacFactory(new BouncyMacFactory(this));
        setRandomFactory(new GordianCoreRandomFactory(this));
        setKeySetFactory(new GordianCoreKeySetFactory(this));
    }

    @Override
    public BouncyDigestFactory getDigestFactory() {
        return (BouncyDigestFactory) super.getDigestFactory();
    }

    @Override
    public BouncyCipherFactory getCipherFactory() {
        return (BouncyCipherFactory) super.getCipherFactory();
    }

    @Override
    public BouncyMacFactory getMacFactory() {
        return (BouncyMacFactory) super.getMacFactory();
    }

    @Override
    public GordianZipFactory getZipFactory() {
        if (theZipFactory == null) {
            theZipFactory = new GordianCoreZipFactory();
        }
        return theZipFactory;
    }

    @Override
    public GordianAsymFactory getAsymmetricFactory() {
        if (theAsymFactory == null) {
            theAsymFactory = new BouncyAsymFactory(this);
        }
        return theAsymFactory;
    }
}
