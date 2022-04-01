/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.quicken.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;

/**
 * Parent category registration.
 * @author Tony Washer
 */
public class QIFParentCategory
        implements Comparable<QIFParentCategory> {
    /**
     * Self definition.
     */
    private final QIFEventCategory theSelf;

    /**
     * Children.
     */
    private final List<QIFEventCategory> theChildren;

    /**
     * Constructor.
     * @param pFile the file definition
     * @param pParent the parent category
     */
    protected QIFParentCategory(final QIFFile pFile,
                                final TransactionCategory pParent) {
        this(new QIFEventCategory(pFile, pParent));
    }

    /**
     * Constructor.
     * @param pParent the parent category
     */
    protected QIFParentCategory(final QIFEventCategory pParent) {
        /* Record self definition */
        theSelf = pParent;

        /* Create child list */
        theChildren = new ArrayList<>();
    }

    @Override
    public String toString() {
        return theSelf.toString();
    }

    /**
     * Obtain number of children.
     * @return the number of children
     */
    protected int numChildren() {
        return theChildren.size();
    }

    /**
     * Obtain the security.
     * @return the security
     */
    public QIFEventCategory getParent() {
        return theSelf;
    }

    /**
     * Obtain the children.
     * @return the children
     */
    public List<QIFEventCategory> getChildren() {
        return theChildren;
    }

    /**
     * Register child.
     * @param pChild the child
     */
    protected void registerChild(final QIFEventCategory pChild) {
        /* Add the child */
        theChildren.add(pChild);
    }

    /**
     * Sort the children.
     */
    protected void sortChildren() {
        Collections.sort(theChildren);
    }

    /**
     * Format record.
     * @param pFormatter the data formatter
     * @param pBuilder the string builder
     */
    public void formatRecord(final MetisDataFormatter pFormatter,
                             final StringBuilder pBuilder) {
        /* Format own record */
        theSelf.formatRecord(pFormatter, pBuilder);

        /* Loop through the children */
        final Iterator<QIFEventCategory> myIterator = theChildren.iterator();
        while (myIterator.hasNext()) {
            final QIFEventCategory myCategory = myIterator.next();

            /* Format the child */
            myCategory.formatRecord(pFormatter, pBuilder);
        }
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
        if (!getClass().equals(pThat.getClass())) {
            return false;
        }

        /* Cast correctly */
        final QIFParentCategory myParent = (QIFParentCategory) pThat;

        /* Check parent */
        if (!theSelf.equals(myParent.getParent())) {
            return false;
        }

        /* Check children */
        return theChildren.equals(myParent.getChildren());
    }

    @Override
    public int hashCode() {
        final int myResult = QIFFile.HASH_BASE * theSelf.hashCode();
        return myResult + theChildren.hashCode();
    }

    @Override
    public int compareTo(final QIFParentCategory pThat) {
        return theSelf.compareTo(pThat.getParent());
    }
}
