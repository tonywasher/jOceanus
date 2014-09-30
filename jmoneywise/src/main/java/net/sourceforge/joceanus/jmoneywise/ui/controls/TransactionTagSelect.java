/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.ui.controls;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldElement;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag.TransactionTagList;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.TagFilter;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

/**
 * TransactionTag Selection.
 */
public class TransactionTagSelect
        extends JEventPanel
        implements AnalysisFilterSelection {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 1982086108264042602L;

    /**
     * Text for TransactionTag Label.
     */
    private static final String NLS_TAG = MoneyWiseDataType.TRANSTAG.getItemName();

    /**
     * The active transaction tag list.
     */
    private TransactionTagList theTags;

    /**
     * The state.
     */
    private TagState theState;

    /**
     * The savePoint.
     */
    private TagState theSavePoint;

    /**
     * The tag button.
     */
    private final JScrollButton<TransactionTag> theTagButton;

    @Override
    public TagFilter getFilter() {
        TransactionTag myTag = theState.getTag();
        return myTag != null
                            ? new TagFilter(myTag)
                            : null;
    }

    @Override
    public boolean isAvailable() {
        return (theTags != null) && !theTags.isEmpty();
    }

    /**
     * Constructor.
     */
    public TransactionTagSelect() {
        /* Create the tags button */
        theTagButton = new JScrollButton<TransactionTag>();

        /* Create the label */
        JLabel myTagLabel = new JLabel(NLS_TAG + JFieldElement.STR_COLON);

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalGlue());
        add(myTagLabel);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(theTagButton);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));

        /* Create initial state */
        theState = new TagState();
        theState.applyState();

        /* Create the listener */
        TagListener myListener = new TagListener();
        theTagButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, myListener);
    }

    /**
     * Create SavePoint.
     */
    protected void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new TagState(theState);
    }

    /**
     * Restore SavePoint.
     */
    protected void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new TagState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Determine whether there are any Accounts to select */
        boolean csAvailable = bEnabled && isAvailable();

        /* Pass call on to buttons */
        theTagButton.setEnabled(csAvailable);
    }

    /**
     * Set analysis.
     * @param pAnalysis the analysis.
     */
    public void setAnalysis(final Analysis pAnalysis) {
        /* Access buckets */
        theTags = pAnalysis.getTransactionTags();

        /* Obtain the current tag */
        TransactionTag myTag = theState.getTag();

        /* If we have a selected Tag */
        if (myTag != null) {
            /* Look for the equivalent tag */
            myTag = theTags.findItemById(myTag.getOrderedId());
        }

        /* If we do not have an active tag and the list is non-empty */
        if ((myTag == null) && (!theTags.isEmpty())) {
            /* Determine the next tag */
            myTag = theTags.peekFirst();
        }

        /* Set the tag */
        theState.setTag(myTag);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof TagFilter) {
            /* Access filter */
            TagFilter myFilter = (TagFilter) pFilter;

            /* Obtain the tag */
            TransactionTag myTag = myFilter.getTag();

            /* Obtain equivalent bucket */
            myTag = theTags.findItemById(myTag.getOrderedId());

            /* Set the tag */
            theState.setTag(myTag);
            theState.applyState();
        }
    }

    /**
     * Listener class.
     */
    private final class TagListener
            implements PropertyChangeListener, ChangeListener {
        /**
         * Tag menu builder.
         */
        private final JScrollMenuBuilder<TransactionTag> theTagMenuBuilder;

        /**
         * Constructor.
         */
        private TagListener() {
            /* Access builders */
            theTagMenuBuilder = theTagButton.getMenuBuilder();
            theTagMenuBuilder.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source of the event */
            Object o = pEvent.getSource();

            /* Handle buttons */
            if (theTagMenuBuilder.equals(o)) {
                buildTagMenu();
            }
        }

        /**
         * Build Tag menu.
         */
        private void buildTagMenu() {
            /* Reset the popUp menu */
            theTagMenuBuilder.clearMenu();

            /* Record active item */
            TransactionTag myCurrent = theState.getTag();
            JMenuItem myActive = null;

            /* Loop through the available tag values */
            Iterator<TransactionTag> myIterator = theTags.iterator();
            while (myIterator.hasNext()) {
                TransactionTag myTag = myIterator.next();

                /* Create a new JMenuItem and add it to the popUp */
                JMenuItem myItem = theTagMenuBuilder.addItem(myTag);

                /* If this is the active category */
                if (myTag.equals(myCurrent)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theTagMenuBuilder.showItem(myActive);
        }

        @Override
        public void propertyChange(final PropertyChangeEvent pEvent) {
            /* Access the source */
            Object o = pEvent.getSource();

            /* If this is the tag button */
            if (theTagButton.equals(o)) {
                /* Select the new tag */
                if (theState.setTag(theTagButton.getValue())) {
                    theState.applyState();
                    fireStateChanged();
                }
            }
        }
    }

    /**
     * SavePoint values.
     */
    private final class TagState {
        /**
         * The active Tag.
         */
        private TransactionTag theTransTag;

        /**
         * Obtain the TransactionTag.
         * @return the Tag
         */
        private TransactionTag getTag() {
            return theTransTag;
        }

        /**
         * Constructor.
         */
        private TagState() {
            /* Initialise the tag */
            theTransTag = null;
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private TagState(final TagState pState) {
            /* Initialise state */
            theTransTag = pState.getTag();
        }

        /**
         * Set new Tag.
         * @param pTag the Transaction Tag
         * @return true/false did a change occur
         */
        private boolean setTag(final TransactionTag pTag) {
            /* Adjust the selected tag */
            if (!Difference.isEqual(pTag, theTransTag)) {
                /* Store the tag */
                theTransTag = pTag;

                /* We have changed */
                return true;
            }
            return false;
        }

        /**
         * Apply the State.
         */
        private void applyState() {
            /* Adjust the lock-down */
            setEnabled(true);
            theTagButton.setValue(theTransTag);
        }
    }
}
