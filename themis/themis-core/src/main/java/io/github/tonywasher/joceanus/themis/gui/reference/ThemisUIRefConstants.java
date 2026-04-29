/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.themis.gui.reference;

import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIResource;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisChar;

/**
 * Reference constants.
 */
public final class ThemisUIRefConstants {
    /**
     * Private constructor.
     */
    private ThemisUIRefConstants() {
    }

    /**
     * Separator character.
     */
    static final char SEPCHAR = ThemisChar.COLON;

    /**
     * Local package name.
     */
    static final String LOCALPACKAGE = ThemisUIResource.REF_LOCAL.getValue();

    /**
     * New package indicator.
     */
    static final String LINKPACKAGE = "newPackage" + SEPCHAR;

    /**
     * Local package indicator.
     */
    static final String LINKLOCAL = "localPackage" + SEPCHAR;

    /**
     * Link List indicator.
     */
    static final String LINKLIST = "linkList" + SEPCHAR;

    /**
     * SelfOK class.
     */
    static final String CLASSSELFOK = "refSelfOK";

    /**
     * SelfError class.
     */
    static final String CLASSSELFERROR = "refSelfError";

    /**
     * Link class.
     */
    static final String CLASSLINK = "refLink";

    /**
     * Link class.
     */
    static final String CLASSLINKLIST = "refLinkList";

    /**
     * Link class.
     */
    static final String CLASSLINKSIBLING = "refLinkSibling";

    /**
     * Link Present class.
     */
    static final String CLASSLINKPRESENT = "refLinkPresent";

    /**
     * Navigation Table class.
     */
    static final String CLASSNAVTABLE = "refNavigation";
}
