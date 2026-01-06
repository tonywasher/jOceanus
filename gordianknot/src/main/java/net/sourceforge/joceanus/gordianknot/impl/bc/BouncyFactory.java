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
package net.sourceforge.joceanus.gordianknot.impl.bc;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianFactoryGenerator;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianValidator;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianCoreKeySetFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.lock.GordianCoreLockFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.random.GordianCoreRandomFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.zip.GordianCoreZipFactory;

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
     * async Factory.
     */
    private BouncyAsyncFactory theAsyncFactory;

    /**
     * Constructor.
     * @param pGenerator the factory generator
     * @param pParameters the security parameters
     * @throws GordianException on error
     */
    public BouncyFactory(final GordianFactoryGenerator pGenerator,
                         final GordianParameters pParameters) throws GordianException {
        /* initialise underlying factory */
        super(pGenerator, pParameters);
    }

    @Override
    protected void declareFactories() throws GordianException {
        /* Create the factories */
        setValidator(new GordianValidator());
        setDigestFactory(new BouncyDigestFactory(this));
        setCipherFactory(new BouncyCipherFactory(this));
        setMacFactory(new BouncyMacFactory(this));
        setRandomFactory(new GordianCoreRandomFactory(this));
        setKeySetFactory(new GordianCoreKeySetFactory(this));
        setLockFactory(new GordianCoreLockFactory(this));
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
            theZipFactory = new GordianCoreZipFactory(this);
        }
        return theZipFactory;
    }

    @Override
    public BouncyAsyncFactory getAsyncFactory() {
        if (theAsyncFactory == null) {
            theAsyncFactory = new BouncyAsyncFactory(this);
        }
        return theAsyncFactory;
    }
}
