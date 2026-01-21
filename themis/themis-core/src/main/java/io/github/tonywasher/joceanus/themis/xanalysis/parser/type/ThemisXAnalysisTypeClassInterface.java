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
package io.github.tonywasher.joceanus.themis.xanalysis.parser.type;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeSimpleName;

import java.util.List;

/**
 * Class/Interface Type Declaration.
 */
public class ThemisXAnalysisTypeClassInterface
        extends ThemisXAnalysisBaseType<ClassOrInterfaceType> {
    /**
     * The name of the class.
     */
    private final String theName;

    /**
     * The full name of the class.
     */
    private final String theFullName;

    /**
     * The scope.
     */
    private final ThemisXAnalysisTypeInstance theScope;

    /**
     * The type.
     */
    private final List<ThemisXAnalysisTypeInstance> theTypeParams;

    /**
     * The annotations.
     */
    private final List<ThemisXAnalysisExpressionInstance> theAnnotations;

    /**
     * The class instance.
     */
    private ThemisXAnalysisClassInstance theClassInstance;

    /**
     * Constructor.
     *
     * @param pParser the parser
     * @param pType   the type
     * @throws OceanusException on error
     */
    ThemisXAnalysisTypeClassInterface(final ThemisXAnalysisParserDef pParser,
                                      final ClassOrInterfaceType pType) throws OceanusException {
        super(pParser, pType);
        theName = ((ThemisXAnalysisNodeSimpleName) pParser.parseNode(pType.getName())).getName();
        theFullName = pType.getNameWithScope();
        theScope = pParser.parseType(pType.getScope().orElse(null));
        theTypeParams = pParser.parseTypeList(pType.getTypeArguments().orElse(null));
        theAnnotations = pParser.parseExprList(pType.getAnnotations());
    }

    /**
     * Obtain the name.
     *
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return theFullName;
    }

    /**
     * Obtain the scope.
     *
     * @return the scope
     */
    public ThemisXAnalysisTypeInstance getScope() {
        return theScope;
    }

    /**
     * Obtain the typeParams.
     *
     * @return the typeParams
     */
    public List<ThemisXAnalysisTypeInstance> getTypeParams() {
        return theTypeParams;
    }

    /**
     * Obtain the annotations.
     *
     * @return the annotations
     */
    public List<ThemisXAnalysisExpressionInstance> getAnnotations() {
        return theAnnotations;
    }

    /**
     * Obtain the class instance.
     *
     * @return the class instance
     */
    public ThemisXAnalysisClassInstance getClassInstance() {
        return theClassInstance;
    }

    /**
     * Set the class instance.
     *
     * @param pClassInstance the class instance
     */
    public void setClassInstance(final ThemisXAnalysisClassInstance pClassInstance) {
        theClassInstance = pClassInstance;
    }
}
