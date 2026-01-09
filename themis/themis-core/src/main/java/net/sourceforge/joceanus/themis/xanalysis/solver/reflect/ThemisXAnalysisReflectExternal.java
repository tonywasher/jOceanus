/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012-2026 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.themis.xanalysis.solver.reflect;

import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisModifierList;
import net.sourceforge.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeImport;
import net.sourceforge.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeModifierList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * External Class representation.
 */
public class ThemisXAnalysisReflectExternal
        implements ThemisXAnalysisClassInstance {
    /**
     * The javaLang prefix.
     */
    private static final String JAVALANG = "java.lang.";

    /**
     * The name of the class.
     */
    private final String theName;

    /**
     * The full name of the class.
     */
    private final String theFullName;

    /**
     * The modifierList.
     */
    private final ThemisXAnalysisModifierList theModifiers;

    /**
     * The class instance.
     */
    private ThemisXAnalysisClassInstance theClassInstance;

    /**
     * Constructor.
     * @param pImport the import definition
     */
    public ThemisXAnalysisReflectExternal(final ThemisXAnalysisNodeImport pImport) {
        theName = pImport.getShortName();
        theFullName = pImport.getFullName();
        theModifiers = new ThemisXAnalysisNodeModifierList(new ArrayList<>());
    }

    /**
     * Constructor.
     * @param pClazz the loaded class instance
     */
    ThemisXAnalysisReflectExternal(final ThemisXAnalysisClassInstance pClazz) {
        theName = pClazz.getName();
        theFullName = pClazz.getFullName();
        theModifiers = pClazz.getModifiers();
        theClassInstance = pClazz;
    }

    /**
     * Constructor.
     * @param pLang the javaLang class
     */
    private ThemisXAnalysisReflectExternal(final ThemisXAnalysisReflectJavaLang pLang) {
        theName = pLang.getName();
        theFullName = JAVALANG + theName;
        theModifiers = new ThemisXAnalysisNodeModifierList(new ArrayList<>());
    }

    @Override
    public String getName() {
        return theName;
    }

    @Override
    public String getFullName() {
        return theFullName;
    }

    @Override
    public ThemisXAnalysisModifierList getModifiers() {
        return theModifiers;
    }

    @Override
    public boolean isTopLevel() {
        return true;
    }

    /**
     * Obtain map of java.lang classes.
     * @return the map
     */
    public static Map<String, ThemisXAnalysisReflectExternal> getJavaLangMap() {
        final Map<String, ThemisXAnalysisReflectExternal> myMap = new LinkedHashMap<>();
        for (ThemisXAnalysisReflectJavaLang myLang : ThemisXAnalysisReflectJavaLang.values()) {
            myMap.put(myLang.getName(), new ThemisXAnalysisReflectExternal(myLang));
        }
        return myMap;
    }

    /**
     * Obtain the class instance.
     * @return the class instance
     */
    public ThemisXAnalysisClassInstance getClassInstance() {
        return theClassInstance;
    }

    /**
     * Set the class instance.
     * @param pClassInstance the class instance
     */
    public void setClassInstance(final ThemisXAnalysisClassInstance pClassInstance) {
        theClassInstance = pClassInstance;
    }
}
