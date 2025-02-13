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
package net.sourceforge.joceanus.gordianknot.impl.jca;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianFactoryGenerator;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianCoreKeySetFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.lock.GordianCoreLockFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.random.GordianCoreRandomFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.zip.GordianCoreZipFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import java.security.Provider;
import java.security.Security;

/**
 * Jca Factory.
 */
public class JcaFactory
    extends GordianCoreFactory {
    /*
     * Static Constructor.
     */
    static {
        /* Select unlimited security */
        Security.setProperty("crypto.policy", "unlimited");
    }

    /**
     * Note the standard provider.
     */
    static final Provider BCPROV = new BouncyCastleProvider();

    /**
     * Note the post quantum provider.
     */
    static final Provider BCPQPROV = new BouncyCastlePQCProvider();

    /**
     * Zip Factory.
     */
    private GordianCoreZipFactory theZipFactory;

    /**
     * keyPair Factory.
     */
    private JcaKeyPairFactory theKeyPairFactory;

    /**
     * Constructor.
     * @param pGenerator the factory generator
     * @param pParameters the security parameters
     * @throws GordianException on error
     */
    public JcaFactory(final GordianFactoryGenerator pGenerator,
                      final GordianParameters pParameters) throws GordianException {
        /* initialise underlying factory */
        super(pGenerator, pParameters);
    }

    @Override
    protected void declareFactories() throws GordianException {
        /* Create the factories */
        setValidator(new JcaValidator());
        setDigestFactory(new JcaDigestFactory(this));
        setCipherFactory(new JcaCipherFactory(this));
        setMacFactory(new JcaMacFactory(this));
        setRandomFactory(new GordianCoreRandomFactory(this));
        setKeySetFactory(new GordianCoreKeySetFactory(this));
        setLockFactory(new GordianCoreLockFactory(this));
    }

    @Override
    public JcaDigestFactory getDigestFactory() {
        return (JcaDigestFactory) super.getDigestFactory();
    }

    @Override
    public JcaCipherFactory getCipherFactory() {
        return (JcaCipherFactory) super.getCipherFactory();
    }

    @Override
    public JcaMacFactory getMacFactory() {
        return (JcaMacFactory) super.getMacFactory();
    }

    @Override
    public GordianZipFactory getZipFactory() {
        if (theZipFactory == null) {
            theZipFactory = new GordianCoreZipFactory(this);
        }
        return theZipFactory;
    }

    @Override
    public JcaKeyPairFactory getKeyPairFactory() {
        if (theKeyPairFactory == null) {
            theKeyPairFactory = new JcaKeyPairFactory(this);
        }
        return theKeyPairFactory;
    }
}
