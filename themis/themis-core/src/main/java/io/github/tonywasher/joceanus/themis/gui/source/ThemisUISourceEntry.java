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

package io.github.tonywasher.joceanus.themis.gui.source;

import io.github.tonywasher.joceanus.tethys.api.base.TethysUIIconId;
import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIResource;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisDeclarationInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisExpressionInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisNodeInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisStatementInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisTypeInstance;
import io.github.tonywasher.joceanus.themis.parser.decl.ThemisDeclClassInterface;
import io.github.tonywasher.joceanus.themis.parser.decl.ThemisDeclEnum;
import io.github.tonywasher.joceanus.themis.parser.decl.ThemisDeclEnumValue;
import io.github.tonywasher.joceanus.themis.parser.decl.ThemisDeclMethod;
import io.github.tonywasher.joceanus.themis.parser.decl.ThemisDeclRecord;
import io.github.tonywasher.joceanus.themis.parser.decl.ThemisDeclaration;
import io.github.tonywasher.joceanus.themis.parser.expr.ThemisExpression;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNode;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNodeModifier;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNodeParameter;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNodeSimpleName;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNodeVariable;
import io.github.tonywasher.joceanus.themis.parser.stmt.ThemisStatement;
import io.github.tonywasher.joceanus.themis.parser.stmt.ThemisStmtClass;
import io.github.tonywasher.joceanus.themis.parser.stmt.ThemisStmtRecord;
import io.github.tonywasher.joceanus.themis.parser.type.ThemisType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Source Panel Tree Entry.
 */
public class ThemisUISourceEntry {
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
    private final ThemisUISourceEntry theParent;

    /**
     * The id of the entry.
     */
    private final int theId;

    /**
     * The Child List.
     */
    private List<ThemisUISourceEntry> theChildList;

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
    private final ThemisInstance theObject;

    /**
     * Constructor.
     *
     * @param pElement the sourceElement
     */
    ThemisUISourceEntry(final ThemisInstance pElement) {
        this(null, pElement);
    }

    /**
     * Constructor.
     *
     * @param pParent  the parent entry
     * @param pElement the sourceElement
     */
    ThemisUISourceEntry(final ThemisUISourceEntry pParent,
                        final ThemisInstance pElement) {
        /* Store parameters */
        theParent = pParent;
        theObject = pElement;

        /* Allocate id and unique name */
        theId = NEXT_ENTRY_ID.getAndIncrement();
        theUniqueName = ENTRY_PREFIX + theId;
        theDisplayName = getElementDisplay(pElement);
        theIcon = ThemisUISourceIcon.getElementIcon(pElement);

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
    ThemisUISourceEntry getParent() {
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
    ThemisInstance getObject() {
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
    Iterator<ThemisUISourceEntry> childIterator() {
        return theChildList == null
                ? Collections.emptyIterator()
                : theChildList.iterator();
    }

    /**
     * Add child.
     *
     * @param pChild the child to add
     */
    private void addChild(final ThemisUISourceEntry pChild) {
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
        if (!(pThat instanceof ThemisUISourceEntry myThat)) {
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
    static String getElementDisplay(final ThemisInstance pElement) {
        /* Switch on the element type */
        if (pElement instanceof ThemisDeclarationInstance myDecl) {
            return getDeclarationDisplay(myDecl);
        }
        if (pElement instanceof ThemisNodeInstance myNode) {
            return getNodeDisplay(myNode);
        }
        if (pElement instanceof ThemisExpressionInstance myExpr) {
            return getExpressionDisplay(myExpr);
        }
        if (pElement instanceof ThemisStatementInstance myStmt) {
            return getStatementDisplay(myStmt);
        }
        if (pElement instanceof ThemisTypeInstance myType) {
            return getTypeDisplay(myType);
        }
        throw new IllegalArgumentException("Unknown ThemisInstance");
    }

    /**
     * Obtain the displayName for the declaration.
     *
     * @param pElement the element
     * @return the icon
     */
    private static String getDeclarationDisplay(final ThemisDeclarationInstance pElement) {
        /* Switch on the id */
        switch ((ThemisDeclaration) pElement.getId()) {
            case ANNOTATION:
                return ThemisUIResource.SOURCEDECL_ANNOT.getValue();
            case ANNOTATIONMEMBER:
                return ThemisUIResource.SOURCEDECL_ANNOTMEMBER.getValue();
            case CLASSINTERFACE:
                return ((ThemisDeclClassInterface) pElement).getName();
            case COMPACT:
                return ThemisUIResource.SOURCEDECL_COMPACT.getValue();
            case CONSTRUCTOR:
                return ThemisUIResource.SOURCEDECL_CONSTRUCT.getValue();
            case ENUM:
                return ((ThemisDeclEnum) pElement).getName();
            case ENUMVALUE:
                return ((ThemisDeclEnumValue) pElement).getNode().getNameAsString();
            case FIELD:
                return ThemisUIResource.SOURCEDECL_FIELD.getValue();
            case INITIALIZER:
                return ThemisUIResource.SOURCEDECL_INIT.getValue();
            case METHOD:
                return ((ThemisDeclMethod) pElement).getName();
            case RECORD:
                return ((ThemisDeclRecord) pElement).getName();
            default:
                throw new IllegalArgumentException("Unknown ThemisDeclaration");
        }
    }

    /**
     * Obtain the displayName for the node.
     *
     * @param pElement the element
     * @return the icon
     */
    private static String getNodeDisplay(final ThemisNodeInstance pElement) {
        /* Switch on the id */
        switch ((ThemisNode) pElement.getId()) {
            case ARRAYLEVEL:
                return ThemisUIResource.SOURCENODE_ARRAYLVL.getValue();
            case CASE:
                return ThemisUIResource.SOURCENODE_CASE.getValue();
            case CATCH:
                return ThemisUIResource.SOURCENODE_CATCH.getValue();
            case COMMENT:
                return ThemisUIResource.SOURCENODE_COMMENT.getValue();
            case COMPILATIONUNIT:
                return ThemisUIResource.SOURCENODE_COMPUNIT.getValue();
            case IMPORT:
                return ThemisUIResource.SOURCENODE_IMPORT.getValue();
            case MODIFIER:
                return ((ThemisNodeModifier) pElement).getKeyword().toString();
            case NAME:
                return ThemisUIResource.SOURCENODE_NAME.getValue();
            case PACKAGE:
                return ThemisUIResource.SOURCENODE_PACKAGE.getValue();
            case PARAMETER:
                return ((ThemisNodeParameter) pElement).getNode().getNameAsString();
            case SIMPLENAME:
                return ((ThemisNodeSimpleName) pElement).getName();
            case VALUEPAIR:
                return ThemisUIResource.SOURCENODE_VALUEPAIR.getValue();
            case VARIABLE:
                return ((ThemisNodeVariable) pElement).getNode().getNameAsString();
            default:
                throw new IllegalArgumentException("Unknown ThemisNode");
        }
    }

    /**
     * Obtain the displayName for the expression.
     *
     * @param pElement the element
     * @return the icon
     */
    private static String getExpressionDisplay(final ThemisExpressionInstance pElement) {
        /* Switch on the id */
        switch ((ThemisExpression) pElement.getId()) {
            case ARRAYACCESS:
                return ThemisUIResource.SOURCEEXPR_ARRAYACCESS.getValue();
            case MARKER:
                return ThemisUIResource.SOURCEEXPR_MARKER.getValue();
            case NORMAL:
                return ThemisUIResource.SOURCEEXPR_NORMAL.getValue();
            case SINGLEMEMBER:
                return ThemisUIResource.SOURCEEXPR_SINGLE.getValue();
            case ARRAYCREATION:
                return ThemisUIResource.SOURCEEXPR_ARRAYCREATE.getValue();
            case CAST:
                return ThemisUIResource.SOURCEEXPR_CAST.getValue();
            case CHAR:
                return ThemisUIResource.SOURCEEXPR_CHAR.getValue();
            case ARRAYINIT:
                return ThemisUIResource.SOURCEEXPR_ARRAYINIT.getValue();
            case INTEGER:
                return ThemisUIResource.SOURCEEXPR_INTEGER.getValue();
            case ASSIGN:
                return ThemisUIResource.SOURCEEXPR_ASSIGN.getValue();
            case BINARY:
                return ThemisUIResource.SOURCEEXPR_BINARY.getValue();
            case BOOLEAN:
                return ThemisUIResource.SOURCEEXPR_BOOLEAN.getValue();
            case CLASS:
                return ThemisUIResource.SOURCEEXPR_CLASS.getValue();
            case CONDITIONAL:
                return ThemisUIResource.SOURCEEXPR_CONDITIONAL.getValue();
            case NULL:
                return ThemisUIResource.SOURCEEXPR_NULL.getValue();
            case DOUBLE:
                return ThemisUIResource.SOURCEEXPR_DOUBLE.getValue();
            case ENCLOSED:
                return ThemisUIResource.SOURCEEXPR_ENCLOSED.getValue();
            case FIELDACCESS:
                return ThemisUIResource.SOURCEEXPR_FIELDACCESS.getValue();
            case INSTANCEOF:
                return ThemisUIResource.SOURCEEXPR_INSTANCEOF.getValue();
            case LAMBDA:
                return ThemisUIResource.SOURCEEXPR_LAMBDA.getValue();
            case LONG:
                return ThemisUIResource.SOURCEEXPR_LONG.getValue();
            case METHODCALL:
                return ThemisUIResource.SOURCEEXPR_METHODCALL.getValue();
            case METHODREFERENCE:
                return ThemisUIResource.SOURCEEXPR_METHODREF.getValue();
            case NAME:
                return ThemisUIResource.SOURCEEXPR_NAME.getValue();
            case OBJECTCREATE:
                return ThemisUIResource.SOURCEEXPR_OBJCREATE.getValue();
            case STRING:
                return ThemisUIResource.SOURCEEXPR_STRING.getValue();
            case SUPER:
                return ThemisUIResource.SOURCEEXPR_SUPER.getValue();
            case SWITCH:
                return ThemisUIResource.SOURCEEXPR_SWITCH.getValue();
            case TEXTBLOCK:
                return ThemisUIResource.SOURCEEXPR_TEXT.getValue();
            case THIS:
                return ThemisUIResource.SOURCEEXPR_THIS.getValue();
            case TYPE:
                return ThemisUIResource.SOURCEEXPR_TYPE.getValue();
            case TYPEPATTERN:
                return ThemisUIResource.SOURCEEXPR_TYPEPATTERN.getValue();
            case UNARY:
                return ThemisUIResource.SOURCEEXPR_UNARY.getValue();
            case VARIABLE:
                return ThemisUIResource.SOURCEEXPR_VARIABLE.getValue();
            default:
                throw new IllegalArgumentException("Unknown ThemisExpression");
        }
    }

    /**
     * Obtain the icon for the statement.
     *
     * @param pElement the element
     * @return the icon
     */
    private static String getStatementDisplay(final ThemisStatementInstance pElement) {
        /* Switch on the id */
        switch ((ThemisStatement) pElement.getId()) {
            case ASSERT:
                return ThemisUIResource.SOURCESTMT_ASSERT.getValue();
            case BLOCK:
                return ThemisUIResource.SOURCESTMT_BLOCK.getValue();
            case BREAK:
                return ThemisUIResource.SOURCESTMT_BREAK.getValue();
            case LOCALCLASS:
                return getDeclarationDisplay(((ThemisStmtClass) pElement).getBody());
            case CONSTRUCTOR:
                return ThemisUIResource.SOURCESTMT_CONSTRUCT.getValue();
            case CONTINUE:
                return ThemisUIResource.SOURCESTMT_CONTINUE.getValue();
            case DO:
                return ThemisUIResource.SOURCESTMT_DO.getValue();
            case EMPTY:
                return ThemisUIResource.SOURCESTMT_EMPTY.getValue();
            case FOR:
                return ThemisUIResource.SOURCESTMT_FOR.getValue();
            case FOREACH:
                return ThemisUIResource.SOURCESTMT_FOREACH.getValue();
            case IF:
                return ThemisUIResource.SOURCESTMT_IF.getValue();
            case LABELED:
                return ThemisUIResource.SOURCESTMT_LABELED.getValue();
            case LOCALRECORD:
                return getDeclarationDisplay(((ThemisStmtRecord) pElement).getBody());
            case RETURN:
                return ThemisUIResource.SOURCESTMT_RETURN.getValue();
            case SWITCH:
                return ThemisUIResource.SOURCESTMT_SWITCH.getValue();
            case SYNCHRONIZED:
                return ThemisUIResource.SOURCESTMT_SYNC.getValue();
            case THROW:
                return ThemisUIResource.SOURCESTMT_THROW.getValue();
            case TRY:
                return ThemisUIResource.SOURCESTMT_TRY.getValue();
            case WHILE:
                return ThemisUIResource.SOURCESTMT_WHILE.getValue();
            case YIELD:
                return ThemisUIResource.SOURCESTMT_YIELD.getValue();
            default:
                throw new IllegalArgumentException("Unknown ThemisStatement");
        }
    }

    /**
     * Obtain the icon for the type.
     *
     * @param pElement the element
     * @return the icon
     */
    private static String getTypeDisplay(final ThemisTypeInstance pElement) {
        /* Switch on the id */
        switch ((ThemisType) pElement.getId()) {
            case ARRAY:
                return ThemisUIResource.SOURCETYPE_ARRAY.getValue();
            case CLASSINTERFACE:
                return ThemisUIResource.SOURCETYPE_CLASS.getValue();
            case INTERSECTION:
                return ThemisUIResource.SOURCETYPE_INTERSECT.getValue();
            case PARAMETER:
                return ThemisUIResource.SOURCETYPE_PARAMETER.getValue();
            case PRIMITIVE:
                return ThemisUIResource.SOURCETYPE_PRIMITIVE.getValue();
            case UNION:
                return ThemisUIResource.SOURCETYPE_UNION.getValue();
            case UNKNOWN:
                return ThemisUIResource.SOURCETYPE_UNKNOWN.getValue();
            case VAR:
                return ThemisUIResource.SOURCETYPE_VAR.getValue();
            case VOID:
                return ThemisUIResource.SOURCETYPE_VOID.getValue();
            case WILDCARD:
                return ThemisUIResource.SOURCETYPE_WILDCARD.getValue();
            default:
                throw new IllegalArgumentException("Unknown ThemisType");
        }
    }
}
