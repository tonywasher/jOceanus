/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.metis.field;

import net.sourceforge.joceanus.metis.data.MetisDataResource;

/**
 * Paired Item.
 */
public class MetisFieldPairedItem implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<MetisFieldPairedItem> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisFieldPairedItem.class);

    /*
     * Field IDs.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_ID, MetisFieldPairedItem::getExternalId);
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_PARENT, MetisFieldPairedItem::getParent);
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_CHILD, MetisFieldPairedItem::getChild);
    }

    /**
     * The external id.
     */
    private final Long theExternalId;

    /**
     * The parent.
     */
    private final MetisFieldVersionedItem theParent;

    /**
     * The child.
     */
    private final MetisFieldVersionedItem theChild;

    /**
     * Constructor.
     * @param pParent the parent
     * @param pChild the child
     * @param pExternalId the externalId
     */
    public MetisFieldPairedItem(final MetisFieldVersionedItem pParent,
                                final MetisFieldVersionedItem pChild,
                                final Long pExternalId) {
        theParent = pParent;
        theChild = pChild;
        theExternalId = pExternalId;
    }

    /**
     * Obtain the id.
     * @return the parent
     */
    public Long getExternalId() {
        return theExternalId;
    }

    /**
     * Does this Item reference the supplied Id.
     * @param pId the id
     * @return true/false
     */
    public boolean referencesId(final Integer pId) {
        return pId.equals(getChildIdFromPairedId(theExternalId))
               || pId.equals(getParentIdFromPairedId(theExternalId));
    }

    /**
     * Obtain the parent.
     * @return the parent
     */
    public MetisFieldVersionedItem getParent() {
        return theParent;
    }

    /**
     * Obtain the parentId from the pairedId.
     * @param pPairedId the paired Id
     * @return the parentId
     */
    public static int getParentIdFromPairedId(final Long pPairedId) {
        return (int) (pPairedId & (-1L >>> Integer.SIZE));
    }

    /**
     * Obtain the child.
     * @return the child
     */
    public MetisFieldVersionedItem getChild() {
        return theChild;
    }

    /**
     * Obtain the childId from the pairedId.
     * @param pPairedId the paired id
     * @return the childId
     */
    public static int getChildIdFromPairedId(final Long pPairedId) {
        return (int) (pPairedId >>> Integer.SIZE);
    }

    /**
     * Obtain the pairedId from the parentId and childId.
     * @param pParentId the parent id
     * @param pChildId the childId
     * @return the pairedId
     */
    public static Long getPairedIdFromParentAndChild(final Integer pParentId,
                                                     final Integer pChildId) {
        return (((long) pChildId) << Integer.SIZE) | Integer.toUnsignedLong(pParentId);
    }

    @Override


    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String toString() {
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(theParent);
        if (theChild != null) {
            myBuilder.append(':');
            myBuilder.append(theChild);
        }
        return myBuilder.toString();
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        } else if (!(pThat instanceof MetisFieldPairedItem)) {
            return false;
        }

        /* Access object correctly */
        final MetisFieldPairedItem myThat = (MetisFieldPairedItem) pThat;

        /* Ensure parent is identical */
        if (!theParent.equals(myThat.getParent())) {
            return false;
        }

        /* Check child */
        return theChild == null
                                ? myThat.getChild() == null
                                : theChild.equals(myThat.getChild());
    }

    @Override
    public int hashCode() {
        final int myHash = theChild == null
                                            ? 0
                                            : theChild.hashCode();
        return myHash * MetisFieldSet.HASH_PRIME + theParent.hashCode();
    }
}
