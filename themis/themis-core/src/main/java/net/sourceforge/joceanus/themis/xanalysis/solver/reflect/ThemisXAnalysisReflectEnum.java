/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2025 Tony Washer
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

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

import java.util.Optional;

/**
 * Resolved enum JavaParser representation.
 */
public class ThemisXAnalysisReflectEnum
        extends EnumDeclaration {
    /**
     * The underlying class.
     */
    private final Class<?> theClass;

    /**
     * The fully qualified name.
     */
    private final String theFullName;

    /**
     * Constructor.
     * @param pClazz the class definition.
     * @throws OceanusException on error
     */
    ThemisXAnalysisReflectEnum(final Class<?> pClazz) throws OceanusException {
        /* Store the class */
        theClass = pClazz;

        /* Store the fully qualified name */
        theFullName = theClass.getCanonicalName();

        /* Set the name */
        setName(new SimpleName(theClass.getSimpleName()));

        /* Set the modifiers */
        setModifiers(ThemisXAnalysisReflectUtils.buildModifiers(theClass));

        /* Set implements lists */
        setImplementedTypes(ThemisXAnalysisReflectUtils.buildImplements(theClass));

        /* Build the enum value list */
        final NodeList<EnumConstantDeclaration> myEnums = new NodeList<>();
        for (Enum<?> myValue : (Enum<?>[]) theClass.getEnumConstants()) {
            final EnumConstantDeclaration myEnum = new EnumConstantDeclaration(myValue.name());
            myEnums.add(myEnum);
        }
        setEntries(myEnums);

        /* Set members */
        setMembers(ThemisXAnalysisReflectMemberUtils.buildMembers(theClass));
    }

    @Override
    public Optional<String> getFullyQualifiedName() {
        return Optional.of(theFullName);
    }

    @Override
    public boolean isTopLevelType() {
        return !theClass.isMemberClass();
    }
}
