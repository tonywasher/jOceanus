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
        return switch (pElement) {
            case ThemisDeclarationInstance myDecl -> getDeclarationDisplay(myDecl);
            case ThemisNodeInstance myNode -> getNodeDisplay(myNode);
            case ThemisExpressionInstance myExpr -> getExpressionDisplay(myExpr);
            case ThemisStatementInstance myStmt -> getStatementDisplay(myStmt);
            case ThemisTypeInstance myType -> getTypeDisplay(myType);
            default -> throw new IllegalArgumentException("Unknown ThemisInstance");
        };
    }

    /**
     * Obtain the displayName for the declaration.
     *
     * @param pElement the element
     * @return the icon
     */
    private static String getDeclarationDisplay(final ThemisDeclarationInstance pElement) {
        /* Switch on the id */
        return switch ((ThemisDeclaration) pElement.getId()) {
            case ANNOTATION -> ThemisUIResource.SOURCEDECL_ANNOT.getValue();
            case ANNOTATIONMEMBER -> ThemisUIResource.SOURCEDECL_ANNOTMEMBER.getValue();
            case CLASSINTERFACE -> ((ThemisDeclClassInterface) pElement).getName();
            case COMPACT -> ThemisUIResource.SOURCEDECL_COMPACT.getValue();
            case CONSTRUCTOR -> ThemisUIResource.SOURCEDECL_CONSTRUCT.getValue();
            case ENUM -> ((ThemisDeclEnum) pElement).getName();
            case ENUMVALUE -> ((ThemisDeclEnumValue) pElement).getNode().getNameAsString();
            case FIELD -> ThemisUIResource.SOURCEDECL_FIELD.getValue();
            case INITIALIZER -> ThemisUIResource.SOURCEDECL_INIT.getValue();
            case METHOD -> ((ThemisDeclMethod) pElement).getName();
            case RECORD -> ((ThemisDeclRecord) pElement).getName();
            default -> throw new IllegalArgumentException("Unknown ThemisDeclaration");
        };
    }

    /**
     * Obtain the displayName for the node.
     *
     * @param pElement the element
     * @return the icon
     */
    private static String getNodeDisplay(final ThemisNodeInstance pElement) {
        /* Switch on the id */
        return switch ((ThemisNode) pElement.getId()) {
            case ARRAYLEVEL -> ThemisUIResource.SOURCENODE_ARRAYLVL.getValue();
            case CASE -> ThemisUIResource.SOURCENODE_CASE.getValue();
            case CATCH -> ThemisUIResource.SOURCENODE_CATCH.getValue();
            case COMMENT -> ThemisUIResource.SOURCENODE_COMMENT.getValue();
            case COMPILATIONUNIT -> ThemisUIResource.SOURCENODE_COMPUNIT.getValue();
            case IMPORT -> ThemisUIResource.SOURCENODE_IMPORT.getValue();
            case MODIFIER -> ((ThemisNodeModifier) pElement).getKeyword().toString();
            case NAME -> ThemisUIResource.SOURCENODE_NAME.getValue();
            case PACKAGE -> ThemisUIResource.SOURCENODE_PACKAGE.getValue();
            case PARAMETER -> ((ThemisNodeParameter) pElement).getNode().getNameAsString();
            case SIMPLENAME -> ((ThemisNodeSimpleName) pElement).getName();
            case VALUEPAIR -> ThemisUIResource.SOURCENODE_VALUEPAIR.getValue();
            case VARIABLE -> ((ThemisNodeVariable) pElement).getNode().getNameAsString();
            default -> throw new IllegalArgumentException("Unknown ThemisNode");
        };
    }

    /**
     * Obtain the displayName for the expression.
     *
     * @param pElement the element
     * @return the icon
     */
    private static String getExpressionDisplay(final ThemisExpressionInstance pElement) {
        /* Switch on the id */
        return switch ((ThemisExpression) pElement.getId()) {
            case ARRAYACCESS -> ThemisUIResource.SOURCEEXPR_ARRAYACCESS.getValue();
            case MARKER -> ThemisUIResource.SOURCEEXPR_MARKER.getValue();
            case NORMAL -> ThemisUIResource.SOURCEEXPR_NORMAL.getValue();
            case SINGLEMEMBER -> ThemisUIResource.SOURCEEXPR_SINGLE.getValue();
            case ARRAYCREATION -> ThemisUIResource.SOURCEEXPR_ARRAYCREATE.getValue();
            case CAST -> ThemisUIResource.SOURCEEXPR_CAST.getValue();
            case CHAR -> ThemisUIResource.SOURCEEXPR_CHAR.getValue();
            case ARRAYINIT -> ThemisUIResource.SOURCEEXPR_ARRAYINIT.getValue();
            case INTEGER -> ThemisUIResource.SOURCEEXPR_INTEGER.getValue();
            case ASSIGN -> ThemisUIResource.SOURCEEXPR_ASSIGN.getValue();
            case BINARY -> ThemisUIResource.SOURCEEXPR_BINARY.getValue();
            case BOOLEAN -> ThemisUIResource.SOURCEEXPR_BOOLEAN.getValue();
            case CLASS -> ThemisUIResource.SOURCEEXPR_CLASS.getValue();
            case CONDITIONAL -> ThemisUIResource.SOURCEEXPR_CONDITIONAL.getValue();
            case NULL -> ThemisUIResource.SOURCEEXPR_NULL.getValue();
            case DOUBLE -> ThemisUIResource.SOURCEEXPR_DOUBLE.getValue();
            case ENCLOSED -> ThemisUIResource.SOURCEEXPR_ENCLOSED.getValue();
            case FIELDACCESS -> ThemisUIResource.SOURCEEXPR_FIELDACCESS.getValue();
            case INSTANCEOF -> ThemisUIResource.SOURCEEXPR_INSTANCEOF.getValue();
            case LAMBDA -> ThemisUIResource.SOURCEEXPR_LAMBDA.getValue();
            case LONG -> ThemisUIResource.SOURCEEXPR_LONG.getValue();
            case METHODCALL -> ThemisUIResource.SOURCEEXPR_METHODCALL.getValue();
            case METHODREFERENCE -> ThemisUIResource.SOURCEEXPR_METHODREF.getValue();
            case NAME -> ThemisUIResource.SOURCEEXPR_NAME.getValue();
            case OBJECTCREATE -> ThemisUIResource.SOURCEEXPR_OBJCREATE.getValue();
            case STRING -> ThemisUIResource.SOURCEEXPR_STRING.getValue();
            case SUPER -> ThemisUIResource.SOURCEEXPR_SUPER.getValue();
            case SWITCH -> ThemisUIResource.SOURCEEXPR_SWITCH.getValue();
            case TEXTBLOCK -> ThemisUIResource.SOURCEEXPR_TEXT.getValue();
            case THIS -> ThemisUIResource.SOURCEEXPR_THIS.getValue();
            case TYPE -> ThemisUIResource.SOURCEEXPR_TYPE.getValue();
            case TYPEPATTERN -> ThemisUIResource.SOURCEEXPR_TYPEPATTERN.getValue();
            case UNARY -> ThemisUIResource.SOURCEEXPR_UNARY.getValue();
            case VARIABLE -> ThemisUIResource.SOURCEEXPR_VARIABLE.getValue();
            default -> throw new IllegalArgumentException("Unknown ThemisExpression");
        };
    }

    /**
     * Obtain the icon for the statement.
     *
     * @param pElement the element
     * @return the icon
     */
    private static String getStatementDisplay(final ThemisStatementInstance pElement) {
        /* Switch on the id */
        return switch ((ThemisStatement) pElement.getId()) {
            case ASSERT -> ThemisUIResource.SOURCESTMT_ASSERT.getValue();
            case BLOCK -> ThemisUIResource.SOURCESTMT_BLOCK.getValue();
            case BREAK -> ThemisUIResource.SOURCESTMT_BREAK.getValue();
            case LOCALCLASS -> getDeclarationDisplay(((ThemisStmtClass) pElement).getBody());
            case CONSTRUCTOR -> ThemisUIResource.SOURCESTMT_CONSTRUCT.getValue();
            case CONTINUE -> ThemisUIResource.SOURCESTMT_CONTINUE.getValue();
            case DO -> ThemisUIResource.SOURCESTMT_DO.getValue();
            case EMPTY -> ThemisUIResource.SOURCESTMT_EMPTY.getValue();
            case EXPRESSION -> ThemisUIResource.SOURCESTMT_EXPR.getValue();
            case FOR -> ThemisUIResource.SOURCESTMT_FOR.getValue();
            case FOREACH -> ThemisUIResource.SOURCESTMT_FOREACH.getValue();
            case IF -> ThemisUIResource.SOURCESTMT_IF.getValue();
            case LABELED -> ThemisUIResource.SOURCESTMT_LABELED.getValue();
            case LOCALRECORD -> getDeclarationDisplay(((ThemisStmtRecord) pElement).getBody());
            case RETURN -> ThemisUIResource.SOURCESTMT_RETURN.getValue();
            case SWITCH -> ThemisUIResource.SOURCESTMT_SWITCH.getValue();
            case SYNCHRONIZED -> ThemisUIResource.SOURCESTMT_SYNC.getValue();
            case THROW -> ThemisUIResource.SOURCESTMT_THROW.getValue();
            case TRY -> ThemisUIResource.SOURCESTMT_TRY.getValue();
            case WHILE -> ThemisUIResource.SOURCESTMT_WHILE.getValue();
            case YIELD -> ThemisUIResource.SOURCESTMT_YIELD.getValue();
            default -> throw new IllegalArgumentException("Unknown ThemisStatement");
        };
    }

    /**
     * Obtain the icon for the type.
     *
     * @param pElement the element
     * @return the icon
     */
    private static String getTypeDisplay(final ThemisTypeInstance pElement) {
        /* Switch on the id */
        return switch ((ThemisType) pElement.getId()) {
            case ARRAY -> ThemisUIResource.SOURCETYPE_ARRAY.getValue();
            case CLASSINTERFACE -> ThemisUIResource.SOURCETYPE_CLASS.getValue();
            case INTERSECTION -> ThemisUIResource.SOURCETYPE_INTERSECT.getValue();
            case PARAMETER -> ThemisUIResource.SOURCETYPE_PARAMETER.getValue();
            case PRIMITIVE -> ThemisUIResource.SOURCETYPE_PRIMITIVE.getValue();
            case UNION -> ThemisUIResource.SOURCETYPE_UNION.getValue();
            case UNKNOWN -> ThemisUIResource.SOURCETYPE_UNKNOWN.getValue();
            case VAR -> ThemisUIResource.SOURCETYPE_VAR.getValue();
            case VOID -> ThemisUIResource.SOURCETYPE_VOID.getValue();
            case WILDCARD -> ThemisUIResource.SOURCETYPE_WILDCARD.getValue();
            default -> throw new IllegalArgumentException("Unknown ThemisType");
        };
    }
}
