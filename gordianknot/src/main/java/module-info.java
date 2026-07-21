/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
/**
 * GordianKnot.
 */
@SuppressWarnings("module")
module io.github.tonywasher.joceanus.gordianknot {
    /* Java libraries */
    requires java.xml;

    /* External libraries */
    requires org.bouncycastle.provider;
    requires org.bouncycastle.util;

    /* Exports */
    exports io.github.tonywasher.joceanus.gordianknot.api.agree;
    exports io.github.tonywasher.joceanus.gordianknot.api.agree.spec;
    exports io.github.tonywasher.joceanus.gordianknot.api.base;
    exports io.github.tonywasher.joceanus.gordianknot.api.cert;
    exports io.github.tonywasher.joceanus.gordianknot.api.cipher;
    exports io.github.tonywasher.joceanus.gordianknot.api.cipher.spec;
    exports io.github.tonywasher.joceanus.gordianknot.api.digest;
    exports io.github.tonywasher.joceanus.gordianknot.api.digest.spec;
    exports io.github.tonywasher.joceanus.gordianknot.api.encrypt;
    exports io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec;
    exports io.github.tonywasher.joceanus.gordianknot.api.factory;
    exports io.github.tonywasher.joceanus.gordianknot.api.key;
    exports io.github.tonywasher.joceanus.gordianknot.api.keypair;
    exports io.github.tonywasher.joceanus.gordianknot.api.keypair.spec;
    exports io.github.tonywasher.joceanus.gordianknot.api.keyset;
    exports io.github.tonywasher.joceanus.gordianknot.api.keyset.spec;
    exports io.github.tonywasher.joceanus.gordianknot.api.keystore;
    exports io.github.tonywasher.joceanus.gordianknot.api.lock;
    exports io.github.tonywasher.joceanus.gordianknot.api.lock.spec;
    exports io.github.tonywasher.joceanus.gordianknot.api.mac;
    exports io.github.tonywasher.joceanus.gordianknot.api.mac.spec;
    exports io.github.tonywasher.joceanus.gordianknot.api.random;
    exports io.github.tonywasher.joceanus.gordianknot.api.random.spec;
    exports io.github.tonywasher.joceanus.gordianknot.api.sign;
    exports io.github.tonywasher.joceanus.gordianknot.api.sign.spec;
    exports io.github.tonywasher.joceanus.gordianknot.api.zip;
    exports io.github.tonywasher.joceanus.gordianknot.util;

    /* Test exports */
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.agree to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.base to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.cipher to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.digest to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.encrypt to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.exc to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.factory to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.key to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.keypair to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.keyset to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.keystore to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.mac to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.random to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.sign to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.spec.mac to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.core.spec.sign to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.ext.engines to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.ext.digests to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.ext.macs to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.ext.modes to io.github.tonywasher.joceanus.gordianknot.test;
    exports io.github.tonywasher.joceanus.gordianknot.impl.ext.params to io.github.tonywasher.joceanus.gordianknot.test;
}
