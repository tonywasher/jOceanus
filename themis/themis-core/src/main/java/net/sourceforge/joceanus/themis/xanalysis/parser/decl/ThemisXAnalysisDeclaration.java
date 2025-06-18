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
package net.sourceforge.joceanus.themis.xanalysis.parser.decl;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisDeclarationInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisId;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

import java.util.function.Predicate;

/**
 * Analysis BodyType.
 */
public enum ThemisXAnalysisDeclaration
        implements ThemisXAnalysisId {
    /**
     * Annotation.
     */
    ANNOTATION(BodyDeclaration::isAnnotationDeclaration),

    /**
     * Annotation Member.
     */
    ANNOTATIONMEMBER(BodyDeclaration::isAnnotationMemberDeclaration),

    /**
     * Class.
     */
    CLASS(s -> s instanceof ClassOrInterfaceDeclaration cls
            && !cls.isInterface()),

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
     * Interface.
     */
    INTERFACE(s -> s instanceof ClassOrInterfaceDeclaration cls
            && cls.isInterface()),

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
     * @param pTester the test method
     */
    ThemisXAnalysisDeclaration(final Predicate<BodyDeclaration<?>> pTester) {
        theTester = pTester;
    }

    /**
     * Determine type of Declaration.
     * @param pParser the parser
     * @param pDecl the declaration
     * @return the declType
     * @throws OceanusException on error
     */
    public static ThemisXAnalysisDeclaration determineDeclaration(final ThemisXAnalysisParserDef pParser,
                                                                  final BodyDeclaration<?> pDecl) throws OceanusException {
        /* Loop testing each declaration type */
        for (ThemisXAnalysisDeclaration myDecl : values()) {
            if (myDecl.theTester.test(pDecl)) {
                return myDecl;
            }
        }

        /* Unrecognised declType */
        throw pParser.buildException("Unexpected Declaration", pDecl);
    }

    /**
     * Parse a declaration.
     * @param pParser the parser
     * @param pDecl the declaration
     * @return the parsed declaration
     * @throws OceanusException on error
     */
    public static ThemisXAnalysisDeclarationInstance parseDeclaration(final ThemisXAnalysisParserDef pParser,
                                                                      final BodyDeclaration<?> pDecl) throws OceanusException {
        /* Handle null Declaration */
        if (pDecl == null) {
            return null;
        }

        /* Create appropriate declaration */
        switch (ThemisXAnalysisDeclaration.determineDeclaration(pParser, pDecl)) {
            case ANNOTATION:       return new ThemisXAnalysisDeclAnnotation(pParser, pDecl.asAnnotationDeclaration());
            case ANNOTATIONMEMBER: return new ThemisXAnalysisDeclAnnotationMember(pParser, pDecl.asAnnotationMemberDeclaration());
            case CLASS:            return new ThemisXAnalysisDeclClass(pParser, pDecl.asClassOrInterfaceDeclaration());
            case COMPACT:          return new ThemisXAnalysisDeclCompact(pParser, pDecl.asCompactConstructorDeclaration());
            case CONSTRUCTOR:      return new ThemisXAnalysisDeclConstructor(pParser, pDecl.asConstructorDeclaration());
            case ENUM:             return new ThemisXAnalysisDeclEnum(pParser, pDecl.asEnumDeclaration());
            case ENUMVALUE:        return new ThemisXAnalysisDeclEnumValue(pParser, pDecl.asEnumConstantDeclaration());
            case FIELD:            return new ThemisXAnalysisDeclField(pParser, pDecl.asFieldDeclaration());
            case INITIALIZER:      return new ThemisXAnalysisDeclInitializer(pParser, pDecl.asInitializerDeclaration());
            case INTERFACE:        return new ThemisXAnalysisDeclInterface(pParser, pDecl.asClassOrInterfaceDeclaration());
            case METHOD:           return new ThemisXAnalysisDeclMethod(pParser, pDecl.asMethodDeclaration());
            case RECORD:           return new ThemisXAnalysisDeclRecord(pParser, pDecl.asRecordDeclaration());
            default:               throw pParser.buildException("Unsupported Declaration Type", pDecl);
        }
    }

}
