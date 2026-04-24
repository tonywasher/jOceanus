/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.themis.gui.base;

import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleLoader;

import java.util.ResourceBundle;

/**
 * Resource IDs for Themis UI Fields.
 */
public enum ThemisUIResource
        implements OceanusBundleId, MetisDataFieldId {
    /**
     * Source Tab.
     */
    TAB_SOURCE("tab.source"),

    /**
     * References Tab.
     */
    TAB_REFERENCES("tab.references"),

    /**
     * Statistics Tab.
     */
    TAB_STATS("tab.stats"),

    /**
     * Source Tab.
     */
    TAB_LOG("tab.log"),

    /**
     * Select Project Title.
     */
    SELECT_PROJECT("select.project"),

    /**
     * Project Prompt.
     */
    PROMPT_PROJECT("prompt.project"),

    /**
     * Module Prompt.
     */
    PROMPT_MODULE("prompt.module"),

    /**
     * Package Prompt.
     */
    PROMPT_PACKAGE("prompt.package"),

    /**
     * File Prompt.
     */
    PROMPT_FILE("prompt.file"),

    /**
     * Local Package.
     */
    PACKAGE_LOCAL("package.local"),

    /**
     * Family Package.
     */
    PACKAGE_FAMILY("package.family"),

    /**
     * Root Package.
     */
    PACKAGE_ROOT("package.root"),

    /**
     * SourceDecl Annotation.
     */
    SOURCEDECL_ANNOT("source.decl.annot"),

    /**
     * SourceDecl AnnotationMember.
     */
    SOURCEDECL_ANNOTMEMBER("source.decl.annotMember"),

    /**
     * SourceDecl Compact.
     */
    SOURCEDECL_COMPACT("source.decl.compact"),

    /**
     * SourceDecl Constructor.
     */
    SOURCEDECL_CONSTRUCT("source.decl.construct"),

    /**
     * SourceDecl Field.
     */
    SOURCEDECL_FIELD("source.decl.field"),

    /**
     * SourceDecl Initializer.
     */
    SOURCEDECL_INIT("source.decl.init"),

    /**
     * SourceNode ArrayLevel.
     */
    SOURCENODE_ARRAYLVL("source.node.arrayLevel"),

    /**
     * SourceNode Case.
     */
    SOURCENODE_CASE("source.node.case"),

    /**
     * SourceNode Catch.
     */
    SOURCENODE_CATCH("source.node.catch"),

    /**
     * SourceNode Comment.
     */
    SOURCENODE_COMMENT("source.node.comment"),

    /**
     * SourceNode CompilationUnit.
     */
    SOURCENODE_COMPUNIT("source.node.compUnit"),

    /**
     * SourceNode Import.
     */
    SOURCENODE_IMPORT("source.node.import"),

    /**
     * SourceNode Name.
     */
    SOURCENODE_NAME("source.node.name"),

    /**
     * SourceNode Package.
     */
    SOURCENODE_PACKAGE("source.node.package"),

    /**
     * SourceNode ValuePair.
     */
    SOURCENODE_VALUEPAIR("source.node.valuePair"),

    /**
     * SourceExpr ArrayAccess.
     */
    SOURCEEXPR_ARRAYACCESS("source.expr.arrayAccess"),

    /**
     * SourceExpr MarkerAnnotation.
     */
    SOURCEEXPR_MARKER("source.expr.marker"),

    /**
     * SourceExpr NormalAnnotation.
     */
    SOURCEEXPR_NORMAL("source.expr.normal"),

    /**
     * SourceExpr SingleMemberAnnotation.
     */
    SOURCEEXPR_SINGLE("source.expr.single"),

    /**
     * SourceExpr ArrayCreation.
     */
    SOURCEEXPR_ARRAYCREATE("source.expr.arrayCreate"),

    /**
     * SourceExpr ArrayAccess.
     */
    SOURCEEXPR_CAST("source.expr.cast"),

    /**
     * SourceExpr Char.
     */
    SOURCEEXPR_CHAR("source.expr.char"),

    /**
     * SourceExpr ArrayInit.
     */
    SOURCEEXPR_ARRAYINIT("source.expr.arrayInit"),

    /**
     * SourceExpr Integer.
     */
    SOURCEEXPR_INTEGER("source.expr.integer"),

    /**
     * SourceExpr Assign.
     */
    SOURCEEXPR_ASSIGN("source.expr.assign"),

    /**
     * SourceExpr Binary.
     */
    SOURCEEXPR_BINARY("source.expr.binary"),

    /**
     * SourceExpr Boolean.
     */
    SOURCEEXPR_BOOLEAN("source.expr.boolean"),

    /**
     * SourceExpr Class.
     */
    SOURCEEXPR_CLASS("source.expr.class"),

    /**
     * SourceExpr Conditional.
     */
    SOURCEEXPR_CONDITIONAL("source.expr.conditional"),

    /**
     * SourceExpr Null.
     */
    SOURCEEXPR_NULL("source.expr.null"),

    /**
     * SourceExpr Double.
     */
    SOURCEEXPR_DOUBLE("source.expr.double"),

    /**
     * SourceExpr Enclosed.
     */
    SOURCEEXPR_ENCLOSED("source.expr.enclosed"),

    /**
     * SourceExpr FieldAccess.
     */
    SOURCEEXPR_FIELDACCESS("source.expr.fieldyAccess"),

    /**
     * SourceExpr InstanceOf.
     */
    SOURCEEXPR_INSTANCEOF("source.expr.instanceOf"),

    /**
     * SourceExpr Lambda.
     */
    SOURCEEXPR_LAMBDA("source.expr.lambda"),

    /**
     * SourceExpr Long.
     */
    SOURCEEXPR_LONG("source.expr.long"),

    /**
     * SourceExpr MethodCall.
     */
    SOURCEEXPR_METHODCALL("source.expr.methodCall"),

    /**
     * SourceExpr MethodRef.
     */
    SOURCEEXPR_METHODREF("source.expr.methodRef"),

    /**
     * SourceExpr Name.
     */
    SOURCEEXPR_NAME("source.expr.name"),

    /**
     * SourceExpr ObjectCreate.
     */
    SOURCEEXPR_OBJCREATE("source.expr.objCreate"),

    /**
     * SourceExpr String.
     */
    SOURCEEXPR_STRING("source.expr.string"),

    /**
     * SourceExpr Super.
     */
    SOURCEEXPR_SUPER("source.expr.super"),

    /**
     * SourceExpr Switch.
     */
    SOURCEEXPR_SWITCH("source.expr.switch"),

    /**
     * SourceExpr TextBlock.
     */
    SOURCEEXPR_TEXT("source.expr.text"),

    /**
     * SourceExpr This.
     */
    SOURCEEXPR_THIS("source.expr.this"),

    /**
     * SourceExpr Type.
     */
    SOURCEEXPR_TYPE("source.expr.type"),

    /**
     * SourceExpr TypePattern.
     */
    SOURCEEXPR_TYPEPATTERN("source.expr.typePattern"),

    /**
     * SourceExpr Unary.
     */
    SOURCEEXPR_UNARY("source.expr.unary"),

    /**
     * SourceExpr Variable.
     */
    SOURCEEXPR_VARIABLE("source.expr.variable"),

    /**
     * SourceStmt Assert.
     */
    SOURCESTMT_ASSERT("source.stmt.assert"),

    /**
     * SourceStmt Block.
     */
    SOURCESTMT_BLOCK("source.stmt.block"),

    /**
     * SourceStmt Break.
     */
    SOURCESTMT_BREAK("source.stmt.break"),

    /**
     * SourceStmt Constructor.
     */
    SOURCESTMT_CONSTRUCT("source.stmt.construct"),

    /**
     * SourceStmt Continue.
     */
    SOURCESTMT_CONTINUE("source.stmt.continue"),

    /**
     * SourceStmt Do.
     */
    SOURCESTMT_DO("source.stmt.do"),

    /**
     * SourceStmt Empty.
     */
    SOURCESTMT_EMPTY("source.stmt.empty"),

    /**
     * SourceStmt For.
     */
    SOURCESTMT_FOR("source.stmt.for"),

    /**
     * SourceStmt ForEach.
     */
    SOURCESTMT_FOREACH("source.stmt.forEach"),

    /**
     * SourceStmt Id.
     */
    SOURCESTMT_IF("source.stmt.if"),

    /**
     * SourceStmt Labelled.
     */
    SOURCESTMT_LABELED("source.stmt.labeled"),

    /**
     * SourceStmt Return.
     */
    SOURCESTMT_RETURN("source.stmt.return"),

    /**
     * SourceStmt Switch.
     */
    SOURCESTMT_SWITCH("source.stmt.switch"),

    /**
     * SourceStmt Synchronised.
     */
    SOURCESTMT_SYNC("source.stmt.sync"),

    /**
     * SourceStmt Throw.
     */
    SOURCESTMT_THROW("source.stmt.throw"),

    /**
     * SourceStmt Try.
     */
    SOURCESTMT_TRY("source.stmt.try"),

    /**
     * SourceStmt While.
     */
    SOURCESTMT_WHILE("source.stmt.while"),

    /**
     * SourceStmt Yield.
     */
    SOURCESTMT_YIELD("source.stmt.yield"),

    /**
     * SourceType Array.
     */
    SOURCETYPE_ARRAY("source.type.array"),

    /**
     * SourceType Class.
     */
    SOURCETYPE_CLASS("source.type.class"),

    /**
     * SourceType Intersection.
     */
    SOURCETYPE_INTERSECT("source.type.intersect"),

    /**
     * SourceType Parameter.
     */
    SOURCETYPE_PARAMETER("source.type.parameter"),

    /**
     * SourceType Primitive.
     */
    SOURCETYPE_PRIMITIVE("source.type.primitive"),

    /**
     * SourceType Union.
     */
    SOURCETYPE_UNION("source.type.union"),

    /**
     * SourceType Unknown.
     */
    SOURCETYPE_UNKNOWN("source.type.unknown"),

    /**
     * SourceType Var.
     */
    SOURCETYPE_VAR("source.type.var"),

    /**
     * SourceType Void.
     */
    SOURCETYPE_VOID("source.type.void"),

    /**
     * SourceType Wildcard.
     */
    SOURCETYPE_WILDCARD("source.type.wildCard");

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(ThemisUIResource.class.getCanonicalName(),
            ResourceBundle::getBundle);

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     *
     * @param pKeyName the key name
     */
    ThemisUIResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "Themis.ui";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    @Override
    public String getId() {
        return getValue();
    }
}
