/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.gordianknot.impl.jca;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianCipherFactory;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestFactory;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianAsyncFactory;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMacFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianFactoryGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianParameters;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianValidator;
import io.github.tonywasher.joceanus.gordianknot.impl.core.factory.GordianCoreFactory;

/**
 * Jca Factory.
 */
public class JcaFactory
        extends GordianCoreFactory {
    /**
     * Constructor.
     *
     * @param pGenerator  the factory generator
     * @param pParameters the security parameters
     * @throws GordianException on error
     */
    public JcaFactory(final GordianFactoryGenerator pGenerator,
                      final GordianParameters pParameters) throws GordianException {
        /* initialize underlying factory */
        super(pGenerator, pParameters);
    }

    @Override
    public GordianDigestFactory newDigestFactory(final GordianBaseFactory pFactory) {
        return new JcaDigestFactory(this);
    }

    @Override
    public GordianCipherFactory newCipherFactory(final GordianBaseFactory pFactory) {
        return new JcaCipherFactory(this);
    }

    @Override
    public GordianMacFactory newMacFactory(final GordianBaseFactory pFactory) {
        return new JcaMacFactory(this);
    }

    @Override
    public GordianValidator newValidator() {
        return new JcaValidator();
    }

    @Override
    public GordianAsyncFactory newAsyncFactory(final GordianBaseFactory pFactory) {
        return new JcaAsyncFactory(this);
    }
}
