/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
module net.sourceforge.joceanus.gordianknot {
    /* Java libraries */
    requires java.xml;

    /* External libraries */
    requires org.bouncycastle.provider;
    requires org.bouncycastle.util;
    requires org.bouncycastle.pg;

    /* Exports */
    exports net.sourceforge.joceanus.gordianknot.api.agree;
    exports net.sourceforge.joceanus.gordianknot.api.base;
    exports net.sourceforge.joceanus.gordianknot.api.cipher;
    exports net.sourceforge.joceanus.gordianknot.api.digest;
    exports net.sourceforge.joceanus.gordianknot.api.encrypt;
    exports net.sourceforge.joceanus.gordianknot.api.factory;
    exports net.sourceforge.joceanus.gordianknot.api.key;
    exports net.sourceforge.joceanus.gordianknot.api.keypair;
    exports net.sourceforge.joceanus.gordianknot.api.keyset;
    exports net.sourceforge.joceanus.gordianknot.api.keystore;
    exports net.sourceforge.joceanus.gordianknot.api.lock;
    exports net.sourceforge.joceanus.gordianknot.api.mac;
    exports net.sourceforge.joceanus.gordianknot.api.random;
    exports net.sourceforge.joceanus.gordianknot.api.sign;
    exports net.sourceforge.joceanus.gordianknot.api.zip;
    exports net.sourceforge.joceanus.gordianknot.util;
}

