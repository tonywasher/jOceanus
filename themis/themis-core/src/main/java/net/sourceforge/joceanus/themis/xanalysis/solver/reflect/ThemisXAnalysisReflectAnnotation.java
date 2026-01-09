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

import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.expr.SimpleName;

import java.util.Optional;

/**
 * Resolved annotation JavaParser representation.
 */
public class ThemisXAnalysisReflectAnnotation
        extends AnnotationDeclaration {
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
     */
    ThemisXAnalysisReflectAnnotation(final Class<?> pClazz) {
        /* Store the class */
        theClass = pClazz;

        /* Store the fully qualified name */
        theFullName = theClass.getCanonicalName();

        /* Set the name */
        setName(new SimpleName(theClass.getSimpleName()));

        /* Set the modifiers */
        setModifiers(ThemisXAnalysisReflectUtils.buildModifiers(theClass));
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
