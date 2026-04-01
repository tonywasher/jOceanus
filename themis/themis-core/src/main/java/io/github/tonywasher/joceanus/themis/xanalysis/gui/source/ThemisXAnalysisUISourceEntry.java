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
                return "Annotation";
            case ANNOTATIONMEMBER:
                return "AnnotationMember";
            case CLASSINTERFACE:
                return ((ThemisXAnalysisDeclClassInterface) pElement).getName();
            case COMPACT:
                return "Compact";
            case CONSTRUCTOR:
                return "Constructor";
            case ENUM:
                return ((ThemisXAnalysisDeclEnum) pElement).getName();
            case ENUMVALUE:
                return ((ThemisXAnalysisDeclEnumValue) pElement).getNode().getNameAsString();
            case FIELD:
                return "Field";
            case INITIALIZER:
                return "Initializer";
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
                return "ArrayLevel";
            case CASE:
                return "Case";
            case CATCH:
                return "Catch";
            case COMMENT:
                return "Comment";
            case COMPILATIONUNIT:
                return "CompilationUnit";
            case IMPORT:
                return "Import";
            case MODIFIER:
                return ((ThemisXAnalysisNodeModifier) pElement).getKeyword().toString();
            case NAME:
                return "Name";
            case PACKAGE:
                return "Package";
            case PARAMETER:
                return ((ThemisXAnalysisNodeParameter) pElement).getNode().getNameAsString();
            case SIMPLENAME:
                return ((ThemisXAnalysisNodeSimpleName) pElement).getName();
            case VALUEPAIR:
                return "ValuePair";
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
                return "ArrayAccess";
            case MARKER:
                return "MarkerAnnot";
            case NORMAL:
                return "NormalAnnot";
            case SINGLEMEMBER:
                return "SingleAnnot";
            case ARRAYCREATION:
                return "ArrayCreate";
            case CAST:
                return "Cast";
            case CHAR:
                return "Char";
            case ARRAYINIT:
                return "ArrayInit";
            case INTEGER:
                return "Integer";
            case ASSIGN:
                return "Assign";
            case BINARY:
                return "Binary";
            case BOOLEAN:
                return "Boolean";
            case CLASS:
                return "Class";
            case CONDITIONAL:
                return "Conditional";
            case NULL:
                return "Null";
            case DOUBLE:
                return "Double";
            case ENCLOSED:
                return "Enclosed";
            case FIELDACCESS:
                return "FieldAccess";
            case INSTANCEOF:
                return "InstanceOf";
            case LAMBDA:
                return "Lambda";
            case LONG:
                return "Long";
            case METHODCALL:
                return "MethodCalL";
            case METHODREFERENCE:
                return "MethodRef";
            case NAME:
                return "Name";
            case OBJECTCREATE:
                return "ObjectCreate";
            case STRING:
                return "String";
            case SUPER:
                return "Super";
            case SWITCH:
                return "Switch";
            case TEXTBLOCK:
                return "TextBlock";
            case THIS:
                return "This";
            case TYPE:
                return "Type";
            case TYPEPATTERN:
                return "TypePattern";
            case UNARY:
                return "Unary";
            case VARIABLE:
                return "Variable";
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
                return "Assert";
            case BLOCK:
                return "Block";
            case BREAK:
                return "Break";
            case LOCALCLASS:
                return getDeclarationDisplay(((ThemisXAnalysisStmtClass) pElement).getBody());
            case CONSTRUCTOR:
                return "Constructor";
            case CONTINUE:
                return "Continue";
            case DO:
                return "Do";
            case EMPTY:
                return "Empty";
            case FOR:
                return "For";
            case FOREACH:
                return "ForEach";
            case IF:
                return "If";
            case LABELED:
                return "Labelled";
            case LOCALRECORD:
                return getDeclarationDisplay(((ThemisXAnalysisStmtRecord) pElement).getBody());
            case RETURN:
                return "Return";
            case SWITCH:
                return "Switch";
            case SYNCHRONIZED:
                return "Synchronized";
            case THROW:
                return "Throw";
            case TRY:
                return "Try";
            case WHILE:
                return "While";
            case YIELD:
                return "Yield";
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
                return "Array";
            case CLASSINTERFACE:
                return "ClassRef";
            case INTERSECTION:
                return "Intersection";
            case PARAMETER:
                return "Parameter";
            case PRIMITIVE:
                return "Primitive";
            case UNION:
                return "Union";
            case UNKNOWN:
                return "Unknown";
            case VAR:
                return "Var";
            case VOID:
                return "Void";
            case WILDCARD:
                return "Wildcard";
            default:
                throw new IllegalArgumentException("Unknown ThemisXAnalysisType");
        }
    }
}
