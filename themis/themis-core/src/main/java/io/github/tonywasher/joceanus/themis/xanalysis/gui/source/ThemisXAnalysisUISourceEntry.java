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

package io.github.tonywasher.joceanus.themis.xanalysis.gui.source;

import io.github.tonywasher.joceanus.tethys.api.base.TethysUIIconId;
import io.github.tonywasher.joceanus.themis.xanalysis.gui.base.ThemisXAnalysisUIResource;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisDeclarationInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisExpressionInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisNodeInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisStatementInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisTypeInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclClassInterface;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclEnum;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclEnumValue;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclMethod;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclRecord;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclaration;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExpression;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNode;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeModifier;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeParameter;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeSimpleName;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeVariable;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStatement;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStmtClass;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStmtRecord;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.type.ThemisXAnalysisType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Source Panel Tree Entry.
 */
public class ThemisXAnalysisUISourceEntry {
    /**
     * Entry name prefix.
     */
    private static final String ENTRY_PREFIX = "TreeItem";

    /**
     * The Next entryId.
     */
    private static final AtomicInteger NEXT_ENTRY_ID = new AtomicInteger(1);

    /**
     * The Parent.
     */
    private final ThemisXAnalysisUISourceEntry theParent;

    /**
     * The id of the entry.
     */
    private final int theId;

    /**
     * The Child List.
     */
    private List<ThemisXAnalysisUISourceEntry> theChildList;

    /**
     * The unique name of the entry.
     */
    private final String theUniqueName;

    /**
     * The display name of the entry.
     */
    private final String theDisplayName;

    /**
     * The icon of the entry.
     */
    private final TethysUIIconId theIcon;

    /**
     * The object for the entry.
     */
    private final ThemisXAnalysisInstance theObject;

    /**
     * Constructor.
     *
     * @param pElement the sourceElement
     */
    ThemisXAnalysisUISourceEntry(final ThemisXAnalysisInstance pElement) {
        this(null, pElement);
    }

    /**
     * Constructor.
     *
     * @param pParent  the parent entry
     * @param pElement the sourceElement
     */
    ThemisXAnalysisUISourceEntry(final ThemisXAnalysisUISourceEntry pParent,
                                 final ThemisXAnalysisInstance pElement) {
        /* Store parameters */
        theParent = pParent;
        theObject = pElement;

        /* Allocate id and unique name */
        theId = NEXT_ENTRY_ID.getAndIncrement();
        theUniqueName = ENTRY_PREFIX + theId;
        theDisplayName = getElementDisplay(pElement);
        theIcon = ThemisXAnalysisUISourceIcon.getElementIcon(pElement);

        /* If we have a parent */
        if (pParent != null) {
            /* Add the entry to the child list */
            pParent.addChild(this);
        }
    }

    /**
     * Get parent.
     *
     * @return the parent
     */
    ThemisXAnalysisUISourceEntry getParent() {
        return theParent;
    }

    /**
     * Get unique name.
     *
     * @return the name
     */
    String getUniqueName() {
        return theUniqueName;
    }

    /**
     * Get object.
     *
     * @return the object
     */
    ThemisXAnalysisInstance getObject() {
        return theObject;
    }

    @Override
    public String toString() {
        return theDisplayName;
    }

    /**
     * Obtain the icon.
     *
     * @return the icon
     */
    TethysUIIconId getIcon() {
        return theIcon;
    }

    /**
     * Get child iterator.
     *
     * @return the iterator
     */
    Iterator<ThemisXAnalysisUISourceEntry> childIterator() {
        return theChildList == null
                ? Collections.emptyIterator()
                : theChildList.iterator();
    }

    /**
     * Add child.
     *
     * @param pChild the child to add
     */
    private void addChild(final ThemisXAnalysisUISourceEntry pChild) {
        if (theChildList == null) {
            theChildList = new ArrayList<>();
        }
        theChildList.add(pChild);
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check class */
        if (!(pThat instanceof ThemisXAnalysisUISourceEntry myThat)) {
            return false;
        }

        /* Must have same id */
        return theId == myThat.theId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(theId);
    }

    /**
     * Obtain the displayName for the element.
     *
     * @param pElement the element
     * @return the displayName
     */
    static String getElementDisplay(final ThemisXAnalysisInstance pElement) {
        /* Switch on the element type */
        if (pElement instanceof ThemisXAnalysisDeclarationInstance myDecl) {
            return getDeclarationDisplay(myDecl);
        }
        if (pElement instanceof ThemisXAnalysisNodeInstance myNode) {
            return getNodeDisplay(myNode);
        }
        if (pElement instanceof ThemisXAnalysisExpressionInstance myExpr) {
            return getExpressionDisplay(myExpr);
        }
        if (pElement instanceof ThemisXAnalysisStatementInstance myStmt) {
            return getStatementDisplay(myStmt);
        }
        if (pElement instanceof ThemisXAnalysisTypeInstance myType) {
            return getTypeDisplay(myType);
        }
        throw new IllegalArgumentException("Unknown ThemisXAnalysisInstance");
    }

    /**
     * Obtain the displayName for the declaration.
     *
     * @param pElement the element
     * @return the icon
     */
    private static String getDeclarationDisplay(final ThemisXAnalysisDeclarationInstance pElement) {
        /* Switch on the id */
        switch ((ThemisXAnalysisDeclaration) pElement.getId()) {
            case ANNOTATION:
                return ThemisXAnalysisUIResource.SOURCEDECL_ANNOT.getValue();
            case ANNOTATIONMEMBER:
                return ThemisXAnalysisUIResource.SOURCEDECL_ANNOTMEMBER.getValue();
            case CLASSINTERFACE:
                return ((ThemisXAnalysisDeclClassInterface) pElement).getName();
            case COMPACT:
                return ThemisXAnalysisUIResource.SOURCEDECL_COMPACT.getValue();
            case CONSTRUCTOR:
                return ThemisXAnalysisUIResource.SOURCEDECL_CONSTRUCT.getValue();
            case ENUM:
                return ((ThemisXAnalysisDeclEnum) pElement).getName();
            case ENUMVALUE:
                return ((ThemisXAnalysisDeclEnumValue) pElement).getNode().getNameAsString();
            case FIELD:
                return ThemisXAnalysisUIResource.SOURCEDECL_FIELD.getValue();
            case INITIALIZER:
                return ThemisXAnalysisUIResource.SOURCEDECL_INIT.getValue();
            case METHOD:
                return ((ThemisXAnalysisDeclMethod) pElement).getName();
            case RECORD:
                return ((ThemisXAnalysisDeclRecord) pElement).getName();
            default:
                throw new IllegalArgumentException("Unknown ThemisXAnalysisDeclaration");
        }
    }

    /**
     * Obtain the displayName for the node.
     *
     * @param pElement the element
     * @return the icon
     */
    private static String getNodeDisplay(final ThemisXAnalysisNodeInstance pElement) {
        /* Switch on the id */
        switch ((ThemisXAnalysisNode) pElement.getId()) {
            case ARRAYLEVEL:
                return ThemisXAnalysisUIResource.SOURCENODE_ARRAYLVL.getValue();
            case CASE:
                return ThemisXAnalysisUIResource.SOURCENODE_CASE.getValue();
            case CATCH:
                return ThemisXAnalysisUIResource.SOURCENODE_CATCH.getValue();
            case COMMENT:
                return ThemisXAnalysisUIResource.SOURCENODE_COMMENT.getValue();
            case COMPILATIONUNIT:
                return ThemisXAnalysisUIResource.SOURCENODE_COMPUNIT.getValue();
            case IMPORT:
                return ThemisXAnalysisUIResource.SOURCENODE_IMPORT.getValue();
            case MODIFIER:
                return ((ThemisXAnalysisNodeModifier) pElement).getKeyword().toString();
            case NAME:
                return ThemisXAnalysisUIResource.SOURCENODE_NAME.getValue();
            case PACKAGE:
                return ThemisXAnalysisUIResource.SOURCENODE_PACKAGE.getValue();
            case PARAMETER:
                return ((ThemisXAnalysisNodeParameter) pElement).getNode().getNameAsString();
            case SIMPLENAME:
                return ((ThemisXAnalysisNodeSimpleName) pElement).getName();
            case VALUEPAIR:
                return ThemisXAnalysisUIResource.SOURCENODE_VALUEPAIR.getValue();
            case VARIABLE:
                return ((ThemisXAnalysisNodeVariable) pElement).getNode().getNameAsString();
            default:
                throw new IllegalArgumentException("Unknown ThemisXAnalysisNode");
        }
    }

    /**
     * Obtain the displayName for the expression.
     *
     * @param pElement the element
     * @return the icon
     */
    private static String getExpressionDisplay(final ThemisXAnalysisExpressionInstance pElement) {
        /* Switch on the id */
        switch ((ThemisXAnalysisExpression) pElement.getId()) {
            case ARRAYACCESS:
                return ThemisXAnalysisUIResource.SOURCEEXPR_ARRAYACCESS.getValue();
            case MARKER:
                return ThemisXAnalysisUIResource.SOURCEEXPR_MARKER.getValue();
            case NORMAL:
                return ThemisXAnalysisUIResource.SOURCEEXPR_NORMAL.getValue();
            case SINGLEMEMBER:
                return ThemisXAnalysisUIResource.SOURCEEXPR_SINGLE.getValue();
            case ARRAYCREATION:
                return ThemisXAnalysisUIResource.SOURCEEXPR_ARRAYCREATE.getValue();
            case CAST:
                return ThemisXAnalysisUIResource.SOURCEEXPR_CAST.getValue();
            case CHAR:
                return ThemisXAnalysisUIResource.SOURCEEXPR_CHAR.getValue();
            case ARRAYINIT:
                return ThemisXAnalysisUIResource.SOURCEEXPR_ARRAYINIT.getValue();
            case INTEGER:
                return ThemisXAnalysisUIResource.SOURCEEXPR_INTEGER.getValue();
            case ASSIGN:
                return ThemisXAnalysisUIResource.SOURCEEXPR_ASSIGN.getValue();
            case BINARY:
                return ThemisXAnalysisUIResource.SOURCEEXPR_BINARY.getValue();
            case BOOLEAN:
                return ThemisXAnalysisUIResource.SOURCEEXPR_BOOLEAN.getValue();
            case CLASS:
                return ThemisXAnalysisUIResource.SOURCEEXPR_CLASS.getValue();
            case CONDITIONAL:
                return ThemisXAnalysisUIResource.SOURCEEXPR_CONDITIONAL.getValue();
            case NULL:
                return ThemisXAnalysisUIResource.SOURCEEXPR_NULL.getValue();
            case DOUBLE:
                return ThemisXAnalysisUIResource.SOURCEEXPR_DOUBLE.getValue();
            case ENCLOSED:
                return ThemisXAnalysisUIResource.SOURCEEXPR_ENCLOSED.getValue();
            case FIELDACCESS:
                return ThemisXAnalysisUIResource.SOURCEEXPR_FIELDACCESS.getValue();
            case INSTANCEOF:
                return ThemisXAnalysisUIResource.SOURCEEXPR_INSTANCEOF.getValue();
            case LAMBDA:
                return ThemisXAnalysisUIResource.SOURCEEXPR_LAMBDA.getValue();
            case LONG:
                return ThemisXAnalysisUIResource.SOURCEEXPR_LONG.getValue();
            case METHODCALL:
                return ThemisXAnalysisUIResource.SOURCEEXPR_METHODCALL.getValue();
            case METHODREFERENCE:
                return ThemisXAnalysisUIResource.SOURCEEXPR_METHODREF.getValue();
            case NAME:
                return ThemisXAnalysisUIResource.SOURCEEXPR_NAME.getValue();
            case OBJECTCREATE:
                return ThemisXAnalysisUIResource.SOURCEEXPR_OBJCREATE.getValue();
            case STRING:
                return ThemisXAnalysisUIResource.SOURCEEXPR_STRING.getValue();
            case SUPER:
                return ThemisXAnalysisUIResource.SOURCEEXPR_SUPER.getValue();
            case SWITCH:
                return ThemisXAnalysisUIResource.SOURCEEXPR_SWITCH.getValue();
            case TEXTBLOCK:
                return ThemisXAnalysisUIResource.SOURCEEXPR_TEXT.getValue();
            case THIS:
                return ThemisXAnalysisUIResource.SOURCEEXPR_THIS.getValue();
            case TYPE:
                return ThemisXAnalysisUIResource.SOURCEEXPR_TYPE.getValue();
            case TYPEPATTERN:
                return ThemisXAnalysisUIResource.SOURCEEXPR_TYPEPATTERN.getValue();
            case UNARY:
                return ThemisXAnalysisUIResource.SOURCEEXPR_UNARY.getValue();
            case VARIABLE:
                return ThemisXAnalysisUIResource.SOURCEEXPR_VARIABLE.getValue();
            default:
                throw new IllegalArgumentException("Unknown ThemisXAnalysisExpression");
        }
    }

    /**
     * Obtain the icon for the statement.
     *
     * @param pElement the element
     * @return the icon
     */
    private static String getStatementDisplay(final ThemisXAnalysisStatementInstance pElement) {
        /* Switch on the id */
        switch ((ThemisXAnalysisStatement) pElement.getId()) {
            case ASSERT:
                return ThemisXAnalysisUIResource.SOURCESTMT_ASSERT.getValue();
            case BLOCK:
                return ThemisXAnalysisUIResource.SOURCESTMT_BLOCK.getValue();
            case BREAK:
                return ThemisXAnalysisUIResource.SOURCESTMT_BREAK.getValue();
            case LOCALCLASS:
                return getDeclarationDisplay(((ThemisXAnalysisStmtClass) pElement).getBody());
            case CONSTRUCTOR:
                return ThemisXAnalysisUIResource.SOURCESTMT_CONSTRUCT.getValue();
            case CONTINUE:
                return ThemisXAnalysisUIResource.SOURCESTMT_CONTINUE.getValue();
            case DO:
                return ThemisXAnalysisUIResource.SOURCESTMT_DO.getValue();
            case EMPTY:
                return ThemisXAnalysisUIResource.SOURCESTMT_EMPTY.getValue();
            case FOR:
                return ThemisXAnalysisUIResource.SOURCESTMT_FOR.getValue();
            case FOREACH:
                return ThemisXAnalysisUIResource.SOURCESTMT_FOREACH.getValue();
            case IF:
                return ThemisXAnalysisUIResource.SOURCESTMT_IF.getValue();
            case LABELED:
                return ThemisXAnalysisUIResource.SOURCESTMT_LABELED.getValue();
            case LOCALRECORD:
                return getDeclarationDisplay(((ThemisXAnalysisStmtRecord) pElement).getBody());
            case RETURN:
                return ThemisXAnalysisUIResource.SOURCESTMT_RETURN.getValue();
            case SWITCH:
                return ThemisXAnalysisUIResource.SOURCESTMT_SWITCH.getValue();
            case SYNCHRONIZED:
                return ThemisXAnalysisUIResource.SOURCESTMT_SYNC.getValue();
            case THROW:
                return ThemisXAnalysisUIResource.SOURCESTMT_THROW.getValue();
            case TRY:
                return ThemisXAnalysisUIResource.SOURCESTMT_TRY.getValue();
            case WHILE:
                return ThemisXAnalysisUIResource.SOURCESTMT_WHILE.getValue();
            case YIELD:
                return ThemisXAnalysisUIResource.SOURCESTMT_YIELD.getValue();
            default:
                throw new IllegalArgumentException("Unknown ThemisXAnalysisStatement");
        }
    }

    /**
     * Obtain the icon for the type.
     *
     * @param pElement the element
     * @return the icon
     */
    private static String getTypeDisplay(final ThemisXAnalysisTypeInstance pElement) {
        /* Switch on the id */
        switch ((ThemisXAnalysisType) pElement.getId()) {
            case ARRAY:
                return ThemisXAnalysisUIResource.SOURCETYPE_ARRAY.getValue();
            case CLASSINTERFACE:
                return ThemisXAnalysisUIResource.SOURCETYPE_CLASS.getValue();
            case INTERSECTION:
                return ThemisXAnalysisUIResource.SOURCETYPE_INTERSECT.getValue();
            case PARAMETER:
                return ThemisXAnalysisUIResource.SOURCETYPE_PARAMETER.getValue();
            case PRIMITIVE:
                return ThemisXAnalysisUIResource.SOURCETYPE_PRIMITIVE.getValue();
            case UNION:
                return ThemisXAnalysisUIResource.SOURCETYPE_UNION.getValue();
            case UNKNOWN:
                return ThemisXAnalysisUIResource.SOURCETYPE_UNKNOWN.getValue();
            case VAR:
                return ThemisXAnalysisUIResource.SOURCETYPE_VAR.getValue();
            case VOID:
                return ThemisXAnalysisUIResource.SOURCETYPE_VOID.getValue();
            case WILDCARD:
                return ThemisXAnalysisUIResource.SOURCETYPE_WILDCARD.getValue();
            default:
                throw new IllegalArgumentException("Unknown ThemisXAnalysisType");
        }
    }
}
