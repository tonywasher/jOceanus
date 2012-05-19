/*******************************************************************************
 * Copyright 2012 Tony Washer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.JGordianKnot;

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.PreferenceSet;

public class SecurityPreferences extends PreferenceSet {
    /**
     * Registry name for Security Provider
     */
    protected final static String nameProvider = "SecurityProvider";

    /**
     * Registry name for Restricted Security
     */
    protected final static String nameRestricted = "RestrictedKeys";

    /**
     * Registry name for Cipher Steps
     */
    protected final static String nameCipherSteps = "CipherSteps";

    /**
     * Registry name for Hash iterations
     */
    protected final static String nameHashIterations = "HashIterations";

    /**
     * Registry name for SecurityPhrase
     */
    protected final static String nameSecurityPhrase = "SecurityPhrase";

    /**
     * Display name for Security Provider
     */
    protected final static String dispProvider = "Security Provider";

    /**
     * Display name for Restricted Security
     */
    protected final static String dispRestricted = "Restricted Keys";

    /**
     * Display name for Cipher Steps
     */
    protected final static String dispCipherSteps = "Number of CipherSteps";

    /**
     * Display name for Cipher Steps
     */
    protected final static String dispHashIterations = "Hash Iterations";

    /**
     * Display name for Security Phrase
     */
    protected final static String dispSecurityPhrase = "SecurityPhrase";

    /**
     * Default Security Provider
     */
    private final static SecurityProvider defProvider = SecurityProvider.BouncyCastle;

    /**
     * Default Restricted Security
     */
    private final static Boolean defRestricted = Boolean.FALSE;

    /**
     * Default Cipher Steps
     */
    private final static Integer defCipherSteps = 3;

    /**
     * Default Hash iterations
     */
    private final static Integer defHashIterations = 2051;

    /**
     * Default Security Phrase
     */
    private final static String defSecurityPhrase = "JG0rdianKn0t";

    /**
     * Constructor
     * @throws ModelException
     */
    public SecurityPreferences() throws ModelException {
        super();
    }

    @Override
    protected void definePreferences() {
        /* Define the properties */
        definePreference(nameProvider, SecurityProvider.class);
        definePreference(nameRestricted, PreferenceType.Boolean);
        definePreference(nameCipherSteps, PreferenceType.Integer);
        definePreference(nameHashIterations, PreferenceType.Integer);
        definePreference(nameSecurityPhrase, PreferenceType.String);
    }

    @Override
    protected Object getDefaultValue(String pName) {
        /* Handle default values */
        if (pName.equals(nameProvider))
            return defProvider;
        if (pName.equals(nameRestricted))
            return defRestricted;
        if (pName.equals(nameCipherSteps))
            return defCipherSteps;
        if (pName.equals(nameHashIterations))
            return defHashIterations;
        if (pName.equals(nameSecurityPhrase))
            return defSecurityPhrase;
        return null;
    }

    @Override
    protected String getDisplayName(String pName) {
        /* Handle default values */
        if (pName.equals(nameProvider))
            return dispProvider;
        if (pName.equals(nameRestricted))
            return dispRestricted;
        if (pName.equals(nameCipherSteps))
            return dispCipherSteps;
        if (pName.equals(nameHashIterations))
            return dispHashIterations;
        if (pName.equals(nameSecurityPhrase))
            return dispSecurityPhrase;
        return null;
    }
}
