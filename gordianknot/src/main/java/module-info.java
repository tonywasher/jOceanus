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
module io.github.tonywasher.joceanus.gordianknot {
    /* Java libraries */
    requires java.xml;

    /* External libraries */
    requires org.bouncycastle.provider;
    requires org.bouncycastle.util;

    /* Exports */
    exports io.github.tonywasher.joceanus.gordianknot.api.agree;
    exports io.github.tonywasher.joceanus.gordianknot.api.base;
    exports io.github.tonywasher.joceanus.gordianknot.api.cert;
    exports io.github.tonywasher.joceanus.gordianknot.api.cipher;
    exports io.github.tonywasher.joceanus.gordianknot.api.digest;
    exports io.github.tonywasher.joceanus.gordianknot.api.encrypt;
    exports io.github.tonywasher.joceanus.gordianknot.api.factory;
    exports io.github.tonywasher.joceanus.gordianknot.api.key;
    exports io.github.tonywasher.joceanus.gordianknot.api.keypair;
    exports io.github.tonywasher.joceanus.gordianknot.api.keyset;
    exports io.github.tonywasher.joceanus.gordianknot.api.keystore;
    exports io.github.tonywasher.joceanus.gordianknot.api.lock;
    exports io.github.tonywasher.joceanus.gordianknot.api.mac;
    exports io.github.tonywasher.joceanus.gordianknot.api.random;
    exports io.github.tonywasher.joceanus.gordianknot.api.sign;
    exports io.github.tonywasher.joceanus.gordianknot.api.zip;
    exports io.github.tonywasher.joceanus.gordianknot.util;
}

