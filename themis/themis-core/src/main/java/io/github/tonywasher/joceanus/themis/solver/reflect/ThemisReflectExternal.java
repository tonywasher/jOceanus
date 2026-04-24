/*
 * Themis: Java Project Framework
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
package io.github.tonywasher.joceanus.themis.solver.reflect;

import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisClassInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisModifierList;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNodeImport;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNodeModifierList;

import java.util.ArrayList;
import java.util.List;

/**
 * External Class representation.
 */
public class ThemisReflectExternal
        implements ThemisClassInstance {
    /**
     * The javaLang prefix.
     */
    static final String JAVALANG = "java.lang.";

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
    private final ThemisModifierList theModifiers;

    /**
     * The ancestors.
     */
    private final List<String> theAncestors;

    /**
     * The class instance.
     */
    private ThemisClassInstance theClassInstance;

    /**
     * Constructor.
     *
     * @param pImport the import definition
     */
    public ThemisReflectExternal(final ThemisNodeImport pImport) {
        theName = pImport.getShortName();
        theFullName = pImport.getFullName();
        theModifiers = new ThemisNodeModifierList(new ArrayList<>());
        theAncestors = new ArrayList<>();
    }

    /**
     * Constructor.
     *
     * @param pClazz the loaded class instance
     */
    ThemisReflectExternal(final ThemisClassInstance pClazz) {
        theName = pClazz.getName();
        theFullName = pClazz.getFullName();
        theModifiers = pClazz.getModifiers();
        theAncestors = new ArrayList<>();
        theClassInstance = pClazz;
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
    public ThemisModifierList getModifiers() {
        return theModifiers;
    }

    @Override
    public boolean isTopLevel() {
        return true;
    }

    /**
     * Obtain the class instance.
     *
     * @return the class instance
     */
    public ThemisClassInstance getClassInstance() {
        return theClassInstance;
    }

    /**
     * Set the class instance.
     *
     * @param pClassInstance the class instance
     */
    public void setClassInstance(final ThemisClassInstance pClassInstance) {
        theClassInstance = pClassInstance;
    }

    /**
     * Obtain the list of ancestors.
     *
     * @return the list
     */
    public List<String> getAncestors() {
        return theAncestors;
    }

    /**
     * Add ancestor.
     *
     * @param pAncestor the ancestor
     */
    public void addAncestor(final ThemisReflectExternal pAncestor) {
        theAncestors.add(pAncestor.getFullName());
    }
}
