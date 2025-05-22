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
package net.sourceforge.joceanus.themis.xanalysis;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisModifiers;

/**
 * Analysis Object.
 */
public class ThemisXAnalysisObject {
    /**
     * The Analysis Object.
     */
    private TypeDeclaration<?> theContents;

    /**
     * The ShortName.
     */
    private String theShortName;

    /**
     * The FullName.
     */
    private String theFullName;

    /**
     * The Modifiers.
     */
    private ThemisXAnalysisModifiers theModifiers;

    /**
     * Constructor.
     * @param pType the type declaration.
     */
    ThemisXAnalysisObject(final TypeDeclaration<?> pType) {
        /* Store contents */
        theContents = pType;
        theShortName = theContents.getName().asString();
        theFullName = theContents.getFullyQualifiedName().orElse(null);
        theModifiers = new ThemisXAnalysisModifiers(pType.getModifiers());
    }

    /**
     * Obtain the contents.
     * @return the contents
     */
    TypeDeclaration<?> getContents() {
        return theContents;
    }

    /**
     * Obtain the shortName.
     * @return the shortName
     */
    String getShortName() {
        return theShortName;
    }

    /**
     * Obtain the fullName.
     * @return the fullName
     */
    String getFullName() {
        return theFullName;
    }

    /**
     * Obtain the modifiers.
     * @return the modifiers
     */
    ThemisXAnalysisModifiers getModifiers() {
        return theModifiers;
    }

    /**
     * Analysis Class/Interface.
     */
    public static class ThemisXAnalysisClassOrInterface
            extends ThemisXAnalysisObject {
        /**
         * Constructor.
         *
         * @param pType the type declaration.
         */
        ThemisXAnalysisClassOrInterface(final ClassOrInterfaceDeclaration pType) {
            super(pType);
        }

        /**
         * Obtain the contents.
         *
         * @return the contents
         */
        ClassOrInterfaceDeclaration getContents() {
            return super.getContents().asClassOrInterfaceDeclaration();
        }
    }

    /**
     * Analysis Enum.
     */
    public static class ThemisXAnalysisEnum
            extends ThemisXAnalysisObject {
        /**
         * Constructor.
         *
         * @param pType the type declaration.
         */
        ThemisXAnalysisEnum(final EnumDeclaration pType) {
            super(pType);
        }

        /**
         * Obtain the contents.
         *
         * @return the contents
         */
        EnumDeclaration getContents() {
            return super.getContents().asEnumDeclaration();
        }
    }

    /**
     * Analysis Record.
     */
    public static class ThemisXAnalysisRecord
            extends ThemisXAnalysisObject {
        /**
         * Constructor.
         *
         * @param pType the type declaration.
         */
        ThemisXAnalysisRecord(final RecordDeclaration pType) {
            super(pType);
        }

        /**
         * Obtain the contents.
         *
         * @return the contents
         */
        RecordDeclaration getContents() {
            return super.getContents().asRecordDeclaration();
        }
    }
}
