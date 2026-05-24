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

import com.github.javaparser.ast.body.EnumDeclaration;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisDataResource;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisClassInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisDeclarationInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisModifierList;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNodeSimpleName;

import java.util.List;

/**
 * Enum Declaration.
 */
public class ThemisDeclEnum
        extends ThemisBaseDeclaration<EnumDeclaration>
        implements ThemisDeclarationInstance, ThemisClassInstance {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisDeclEnum> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisDeclEnum.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisDataResource.DATA_NAME, ThemisDeclEnum::getName);
    }

    /**
     * The Name.
     */
    private final String theName;

    /**
     * The fullName.
     */
    private final String theFullName;

    /**
     * The modifiers.
     */
    private final ThemisModifierList theModifiers;

    /**
     * The enumConstants.
     */
    private final List<ThemisDeclarationInstance> theValues;

    /**
     * The body.
     */
    private final List<ThemisDeclarationInstance> theBody;

    /**
     * The implements.
     */
    private final List<ThemisTypeInstance> theImplements;

    /**
     * The annotations.
     */
    private final List<ThemisExpressionInstance> theAnnotations;

    /**
     * Constructor.
     *
     * @param pParser      the parser
     * @param pDeclaration the declaration
     * @throws OceanusException on error
     */
    ThemisDeclEnum(final ThemisParserDef pParser,
                   final EnumDeclaration pDeclaration) throws OceanusException {
        /* Store values */
        super(pParser, pDeclaration);
        theName = ((ThemisNodeSimpleName) pParser.parseNode(pDeclaration.getName())).getName();
        theFullName = pDeclaration.getFullyQualifiedName().orElse(null);
        theModifiers = pParser.parseModifierList(pDeclaration.getModifiers());
        theAnnotations = pParser.parseExprList(pDeclaration.getAnnotations());
        theImplements = pParser.parseTypeList(pDeclaration.getImplementedTypes());
        theValues = pParser.parseDeclarationList(pDeclaration.getEntries());

        /* Register the class */
        pParser.registerClass(this);

        /* Finally parse the underlying declarations */
        theBody = pParser.parseDeclarationList(pDeclaration.getMembers());
    }

    @Override
    public MetisFieldSet<ThemisDeclEnum> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return getName();
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
    public boolean isTopLevel() {
        return getNode().isTopLevelType();
    }

    @Override
    public ThemisModifierList getModifiers() {
        return theModifiers;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public List<ThemisDeclarationInstance> getValues() {
        return theValues;
    }

    @Override
    public List<ThemisDeclarationInstance> getBody() {
        return theBody;
    }

    @Override
    public List<ThemisTypeInstance> getImplements() {
        return theImplements;
    }

    @Override
    public List<ThemisExpressionInstance> getAnnotations() {
        return theAnnotations;
    }
}
