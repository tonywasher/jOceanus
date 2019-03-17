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
/**
 * GordianKnot core.
 */
module net.sourceforge.joceanus.jgordianknot.core {
    /* External libraries */
    requires org.bouncycastle.provider;

    /* jOceanus */
    requires net.sourceforge.joceanus.jtethys.core;

    /* Exports */
    exports net.sourceforge.joceanus.jgordianknot.api.agree;
    exports net.sourceforge.joceanus.jgordianknot.api.asym;
    exports net.sourceforge.joceanus.jgordianknot.api.base;
    exports net.sourceforge.joceanus.jgordianknot.api.cipher;
    exports net.sourceforge.joceanus.jgordianknot.api.digest;
    exports net.sourceforge.joceanus.jgordianknot.api.encrypt;
    exports net.sourceforge.joceanus.jgordianknot.api.factory;
    exports net.sourceforge.joceanus.jgordianknot.api.impl;
    exports net.sourceforge.joceanus.jgordianknot.api.key;
    exports net.sourceforge.joceanus.jgordianknot.api.keyset;
    exports net.sourceforge.joceanus.jgordianknot.api.keystore;
    exports net.sourceforge.joceanus.jgordianknot.api.mac;
    exports net.sourceforge.joceanus.jgordianknot.api.random;
    exports net.sourceforge.joceanus.jgordianknot.api.sign;
    exports net.sourceforge.joceanus.jgordianknot.api.zip;
}

