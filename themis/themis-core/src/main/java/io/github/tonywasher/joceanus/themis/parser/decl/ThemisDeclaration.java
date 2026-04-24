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
package io.github.tonywasher.joceanus.themis.parser.decl;

import com.github.javaparser.ast.body.BodyDeclaration;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisId;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

import java.util.function.Predicate;

/**
 * Analysis BodyType.
 */
public enum ThemisDeclaration
        implements ThemisId {
    /**
     * Annotation.
     */
    ANNOTATION(BodyDeclaration::isAnnotationDeclaration),

    /**
     * Annotation Member.
     */
    ANNOTATIONMEMBER(BodyDeclaration::isAnnotationMemberDeclaration),

    /**
     * Class/Interface.
     */
    CLASSINTERFACE(BodyDeclaration::isClassOrInterfaceDeclaration),

    /**
     * Compact Constructor.
     */
    COMPACT(BodyDeclaration::isCompactConstructorDeclaration),

    /**
     * Class.
     */
    CONSTRUCTOR(BodyDeclaration::isConstructorDeclaration),

    /**
     * Enum.
     */
    ENUM(BodyDeclaration::isEnumDeclaration),

    /**
     * EnumValue.
     */
    ENUMVALUE(BodyDeclaration::isEnumConstantDeclaration),

    /**
     * Field.
     */
    FIELD(BodyDeclaration::isFieldDeclaration),

    /**
     * Initializer.
     */
    INITIALIZER(BodyDeclaration::isInitializerDeclaration),

    /**
     * Method.
     */
    METHOD(BodyDeclaration::isMethodDeclaration),

    /**
     * Record.
     */
    RECORD(BodyDeclaration::isRecordDeclaration);

    /**
     * The test.
     */
    private final Predicate<BodyDeclaration<?>> theTester;

    /**
     * Constructor.
     *
     * @param pTester the test method
     */
    ThemisDeclaration(final Predicate<BodyDeclaration<?>> pTester) {
        theTester = pTester;
    }

    /**
     * Determine type of Declaration.
     *
     * @param pParser the parser
     * @param pDecl   the declaration
     * @return the declType
     * @throws OceanusException on error
     */
    public static ThemisDeclaration determineDeclaration(final ThemisParserDef pParser,
                                                         final BodyDeclaration<?> pDecl) throws OceanusException {
        /* Loop testing each declaration type */
        for (ThemisDeclaration myDecl : values()) {
            if (myDecl.theTester.test(pDecl)) {
                return myDecl;
            }
        }

        /* Unrecognised declType */
        throw pParser.buildException("Unexpected Declaration", pDecl);
    }
}
