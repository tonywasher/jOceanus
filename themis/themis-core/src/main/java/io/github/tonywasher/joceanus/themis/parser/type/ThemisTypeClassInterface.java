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
package io.github.tonywasher.joceanus.themis.parser.type;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNodeSimpleName;

import java.util.List;

/**
 * Class/Interface Type Declaration.
 */
public class ThemisTypeClassInterface
        extends ThemisBaseType<ClassOrInterfaceType> {
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
    private final ThemisTypeInstance theScope;

    /**
     * The type.
     */
    private final List<ThemisTypeInstance> theTypeParams;

    /**
     * The annotations.
     */
    private final List<ThemisExpressionInstance> theAnnotations;

    /**
     * The class instance.
     */
    private ThemisClassInstance theClassInstance;

    /**
     * Constructor.
     *
     * @param pParser the parser
     * @param pType   the type
     * @throws OceanusException on error
     */
    ThemisTypeClassInterface(final ThemisParserDef pParser,
                             final ClassOrInterfaceType pType) throws OceanusException {
        super(pParser, pType);
        theName = ((ThemisNodeSimpleName) pParser.parseNode(pType.getName())).getName();
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
    public ThemisTypeInstance getScope() {
        return theScope;
    }

    /**
     * Obtain the typeParams.
     *
     * @return the typeParams
     */
    public List<ThemisTypeInstance> getTypeParams() {
        return theTypeParams;
    }

    /**
     * Obtain the annotations.
     *
     * @return the annotations
     */
    public List<ThemisExpressionInstance> getAnnotations() {
        return theAnnotations;
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
}
