/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.quicken.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Parent category registration.
 * @author Tony Washer
 */
public class MoneyWiseQIFParentCategory
        implements Comparable<MoneyWiseQIFParentCategory> {
    /**
     * Self definition.
     */
    private final MoneyWiseQIFEventCategory theSelf;

    /**
     * Children.
     */
    private final List<MoneyWiseQIFEventCategory> theChildren;

    /**
     * Constructor.
     * @param pFile the file definition
     * @param pParent the parent category
     */
    protected MoneyWiseQIFParentCategory(final MoneyWiseQIFFile pFile,
                                         final MoneyWiseTransCategory pParent) {
        this(new MoneyWiseQIFEventCategory(pFile, pParent));
    }

    /**
     * Constructor.
     * @param pParent the parent category
     */
    protected MoneyWiseQIFParentCategory(final MoneyWiseQIFEventCategory pParent) {
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
    public MoneyWiseQIFEventCategory getParent() {
        return theSelf;
    }

    /**
     * Obtain the children.
     * @return the children
     */
    public List<MoneyWiseQIFEventCategory> getChildren() {
        return theChildren;
    }

    /**
     * Register child.
     * @param pChild the child
     */
    protected void registerChild(final MoneyWiseQIFEventCategory pChild) {
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
    public void formatRecord(final TethysUIDataFormatter pFormatter,
                             final StringBuilder pBuilder) {
        /* Format own record */
        theSelf.formatRecord(pFormatter, pBuilder);

        /* Loop through the children */
        final Iterator<MoneyWiseQIFEventCategory> myIterator = theChildren.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseQIFEventCategory myCategory = myIterator.next();

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
        final MoneyWiseQIFParentCategory myParent = (MoneyWiseQIFParentCategory) pThat;

        /* Check parent */
        if (!theSelf.equals(myParent.getParent())) {
            return false;
        }

        /* Check children */
        return theChildren.equals(myParent.getChildren());
    }

    @Override
    public int hashCode() {
        final int myResult = MoneyWiseQIFFile.HASH_BASE * theSelf.hashCode();
        return myResult + theChildren.hashCode();
    }

    @Override
    public int compareTo(final MoneyWiseQIFParentCategory pThat) {
        return theSelf.compareTo(pThat.getParent());
    }
}
