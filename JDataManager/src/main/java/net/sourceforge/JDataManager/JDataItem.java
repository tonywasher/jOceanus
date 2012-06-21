/*******************************************************************************
 * JDataManager: Java Data Manager
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JDataManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ListIterator;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.StyleSheet;

import net.sourceforge.JDataManager.JDataManager.JDataEntry;

/**
 * Data Item.
 * @author Tony Washer
 */
public class JDataItem {
    /**
     * Window Gap.
     */
    private static final int WINDOW_GAP = 100;

    /**
     * Shift one.
     */
    private static final int SHIFT_ONE = 1;

    /**
     * Shift ten.
     */
    private static final int SHIFT_TEN = 10;

    /**
     * Shift hundred.
     */
    private static final int SHIFT_HUNDRED = 100;

    /**
     * Shift thousand.
     */
    private static final int SHIFT_THOU = 1000;

    /**
     * The panel.
     */
    private final JPanel thePanel;

    /**
     * The list panel.
     */
    private final JPanel theListPanel;

    /**
     * The list.
     */
    private List<?> theList = null;

    /**
     * The report item.
     */
    private Object theItem = null;

    /**
     * The iterator.
     */
    private ListIterator<?> theIterator = null;

    /**
     * The entry.
     */
    private JDataEntry theEntry = null;

    /**
     * The object.
     */
    private Object theObject = null;

    /**
     * The detail.
     */
    private JDataDetail theDetail = null;

    /**
     * The next button.
     */
    private final JButton theNext;

    /**
     * The Next ten button.
     */
    private final JButton theNextTen;

    /**
     * The Next hundred button.
     */
    private final JButton theNextHun;

    /**
     * The Next thousand button.
     */
    private final JButton theNextThou;

    /**
     * The Previous button.
     */
    private final JButton thePrev;

    /**
     * The Previous ten button.
     */
    private final JButton thePrevTen;

    /**
     * The Previous hundred button.
     */
    private final JButton thePrevHun;

    /**
     * The Previous thousand button.
     */
    private final JButton thePrevThou;

    /**
     * The Toggle button.
     */
    private final JToggleButton theToggle;

    /**
     * The Label.
     */
    private final JLabel theLabel;

    /**
     * The editor.
     */
    private final JEditorPane theEditor;

    /**
     * Are we in list mode.
     */
    private boolean isListMode = false;

    /**
     * Get the panel.
     * @return the panel
     */
    protected JPanel getPanel() {
        return thePanel;
    }

    /**
     * Constructor.
     */
    protected JDataItem() {
        JScrollPane myScroll;
        HTMLEditorKit myKit;
        StyleSheet myStyle;
        Document myDoc;

        /* Create the Buttons */
        theNext = new JButton("+" + SHIFT_ONE);
        theNextTen = new JButton("+" + SHIFT_TEN);
        theNextHun = new JButton("+" + SHIFT_HUNDRED);
        theNextThou = new JButton("+" + SHIFT_THOU);
        thePrev = new JButton("-" + SHIFT_ONE);
        thePrevTen = new JButton("-" + SHIFT_TEN);
        thePrevHun = new JButton("-" + SHIFT_HUNDRED);
        thePrevThou = new JButton("-" + SHIFT_THOU);

        /* Create the toggle button */
        theToggle = new JToggleButton("Show items");

        /* Create the label */
        theLabel = new JLabel();

        /* Create the editor pane as non-editable */
        theEditor = new JEditorPane();
        theEditor.setEditable(false);

        /* Add an editor kit to the editor */
        myKit = new HTMLEditorKit();
        theEditor.setEditorKit(myKit);

        /* Create a scroll-pane for the editor */
        myScroll = new JScrollPane(theEditor);

        /* Create the style-sheet for the window */
        myStyle = myKit.getStyleSheet();
        myStyle.addRule("body { color:#000; font-family:times; margins; 4px; }");
        myStyle.addRule("h1 { color: black; }");
        myStyle.addRule("h2 { color: black; }");

        /* Create the document for the window */
        myDoc = myKit.createDefaultDocument();
        theEditor.setDocument(myDoc);

        /* Create the list panel */
        theListPanel = new JPanel();
        theListPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("List Control"));

        /* Create the layout for the panel */
        GroupLayout myLayout = new GroupLayout(theListPanel);
        theListPanel.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addComponent(theToggle)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, WINDOW_GAP,
                                                   Short.MAX_VALUE).addComponent(thePrevThou)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addComponent(thePrevHun)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addComponent(thePrevTen)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addComponent(thePrev)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                  .addComponent(theLabel)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                  .addComponent(theNext)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addComponent(theNextTen)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addComponent(theNextHun)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addComponent(theNextThou).addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(theToggle).addComponent(thePrevThou).addComponent(thePrevHun)
                .addComponent(thePrevTen).addComponent(thePrev).addComponent(theLabel).addComponent(theNext)
                .addComponent(theNextTen).addComponent(theNextHun).addComponent(theNextThou));

        /* Create the complete panel */
        thePanel = new JPanel();

        /* Create the layout for the panel */
        myLayout = new GroupLayout(thePanel);
        thePanel.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(theListPanel).addComponent(myScroll));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup().addContainerGap().addComponent(theListPanel)
                                  .addContainerGap().addComponent(myScroll).addContainerGap()));

        /* Create listener */
        DataListener myListener = new DataListener();

        /* Add hyper-link listener */
        theEditor.addHyperlinkListener(myListener);

        /* Add action Listeners */
        theNext.addActionListener(myListener);
        thePrev.addActionListener(myListener);
        theNextTen.addActionListener(myListener);
        thePrevTen.addActionListener(myListener);
        theNextHun.addActionListener(myListener);
        thePrevHun.addActionListener(myListener);
        theNextThou.addActionListener(myListener);
        thePrevThou.addActionListener(myListener);

        /* Add item Listener */
        theToggle.addItemListener(myListener);
    }

    /**
     * Display the data entry.
     * @param pEntry the data entry
     */
    protected void displayData(final JDataEntry pEntry) {
        /* Record the object */
        theEntry = pEntry;
        theObject = pEntry.getObject();

        /* If we should use the ReportList window */
        if (isEntryList(pEntry)) {
            /* Show the list window */
            theListPanel.setVisible(true);

            /* Declare the list to the list window */
            List<?> myList = (List<?>) theObject;
            setList(myList);

            /* Else hide the list window */
        } else {
            /* Hide the list window */
            theListPanel.setVisible(false);

            /* Display the object */
            displayDetail(new JDataDetail(theObject));
        }
    }

    /**
     * Is the entry a list.
     * @param pEntry the entry
     * @return true/false
     */
    private static boolean isEntryList(final JDataEntry pEntry) {
        Object myObject = pEntry.getObject();

        /* If we should use the ReportList window */
        if ((myObject != null) && (myObject instanceof List) && (!pEntry.hasChildren())) {
            return true;
        }

        /* else standard entry */
        return false;
    }

    /**
     * Update the data entry.
     * @param pEntry the entry
     */
    protected void updateData(final JDataEntry pEntry) {
        /* Access the object */
        theObject = pEntry.getObject();

        /* If we are updating the active object */
        if (theEntry == pEntry) {
            /* If this not a list window */
            if (!isEntryList(pEntry)) {
                /* Hide the list window */
                theListPanel.setVisible(false);

                /* Display the object */
                displayDetail(new JDataDetail(theObject));
            }
        }
    }

    /**
     * Display the detail.
     * @param pDetail the detail
     */
    private void displayDetail(final JDataDetail pDetail) {
        StringBuilder myValue = null;
        StringBuilder myLinks = null;

        /* Store the detail */
        theDetail = pDetail;

        /* If we have a JDataDetail */
        if (pDetail != null) {
            /* Access Data Detail */
            myValue = pDetail.getJDataDetail();

            /* Access history links */
            myLinks = pDetail.getHistoryLinks();
        }

        /* Build the HTML page */
        String myText = "<html><body>";

        /* Add the value to the output */
        if (myLinks != null) {
            myText += myLinks;
        }
        if (myValue != null) {
            myText += myValue;
        }
        myText += "</body></html>";

        /* Set the report text */
        theEditor.setText(myText);
        theEditor.setCaretPosition(0);
        theEditor.requestFocusInWindow();
    }

    /**
     * Set List.
     * @param pList the list that we are using
     */
    private void setList(final List<?> pList) {
        /* Record list */
        theList = pList;

        /* If the list has items */
        if (theList.size() > 0) {
            /* Create iterator and obtain first item */
            theIterator = theList.listIterator();
            theItem = theIterator.next();

            /* Display header initially */
            theToggle.setSelected(false);

            /* else hide the list control */
        } else {
            theListPanel.setVisible(false);
        }

        /* Note that we are not in list mode */
        isListMode = false;

        /* display the header */
        displayHeader();
    }

    /**
     * Display Item.
     */
    private void displayItem() {
        /* Access the list size */
        int mySize = theList.size();
        int myPos = theList.indexOf(theItem);

        /* Show/hide movement buttons */
        theNextThou.setVisible(mySize >= SHIFT_THOU);
        thePrevThou.setVisible(mySize >= SHIFT_THOU);
        theNextHun.setVisible(mySize >= SHIFT_HUNDRED);
        thePrevHun.setVisible(mySize >= SHIFT_HUNDRED);
        theNextTen.setVisible(mySize >= SHIFT_TEN);
        thePrevTen.setVisible(mySize >= SHIFT_TEN);
        theNext.setVisible(true);
        thePrev.setVisible(true);
        theLabel.setVisible(true);

        /* Enable movement buttons */
        theNextThou.setEnabled(myPos < mySize - SHIFT_THOU);
        thePrevThou.setEnabled(myPos >= SHIFT_THOU);
        theNextHun.setEnabled(myPos < mySize - SHIFT_HUNDRED);
        thePrevHun.setEnabled(myPos >= SHIFT_HUNDRED);
        theNextTen.setEnabled(myPos < mySize - SHIFT_TEN);
        thePrevTen.setEnabled(myPos >= SHIFT_TEN);
        theNext.setEnabled(myPos < mySize - SHIFT_ONE);
        thePrev.setEnabled(myPos >= SHIFT_ONE);

        /* Set the text detail */
        theLabel.setText("Item " + (myPos + 1) + " of " + mySize);

        /* Set the text detail */
        theToggle.setText("Show header");

        /* Display the item */
        displayDetail(new JDataDetail(theItem));
    }

    /**
     * Display Header.
     */
    private void displayHeader() {
        /* Hide movement buttons */
        theNextThou.setVisible(false);
        thePrevThou.setVisible(false);
        theNextHun.setVisible(false);
        thePrevHun.setVisible(false);
        theNextTen.setVisible(false);
        thePrevTen.setVisible(false);
        theNext.setVisible(false);
        thePrev.setVisible(false);
        theLabel.setVisible(false);

        /* Set the text detail */
        theToggle.setText("Show items");

        /* Display the header */
        displayDetail(new JDataDetail(theObject));
    }

    /**
     * Shift the iterator a number of steps.
     * @param iNumSteps the number of steps to shift (positive or negative)
     */
    private void shiftIterator(final int iNumSteps) {
        Object myNext = null;
        int myNumSteps = iNumSteps;

        /* If we are stepping forwards */
        if (myNumSteps > 0) {
            /* Loop through the steps */
            while (myNumSteps-- > 0) {
                /* Shift to next element */
                myNext = theIterator.next();

                /* If we have reached the end of the list (should never happen) */
                if (myNext == null) {
                    /* Set next element to the last in the list and break loop */
                    myNext = null;
                    break;
                }
            }

            /* Record the next entry */
            theItem = myNext;

            /* else we are stepping backwards */
        } else if (myNumSteps < 0) {
            /* Shift back one step */
            theIterator.previous();

            /* Loop through the steps */
            while (myNumSteps++ < 0) {
                /* Shift to previous element */
                myNext = theIterator.previous();

                /* If we have reached the end of the list (should never happen) */
                if (myNext == null) {
                    /* Set next element to the last in the list and break loop */
                    myNext = null;
                    break;
                }
            }

            /* Shift forward one step */
            theIterator.next();

            /* Record the next entry */
            theItem = myNext;
        }

        /* display the item */
        displayItem();
    }

    /**
     * Data Listener class.
     */
    private class DataListener implements HyperlinkListener, ActionListener, ItemListener {
        @Override
        public void hyperlinkUpdate(final HyperlinkEvent pEvent) {
            /* If this is an activated event */
            if (pEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                if (pEvent instanceof HTMLFrameHyperlinkEvent) {
                    HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) pEvent;
                    HTMLDocument doc = (HTMLDocument) theEditor.getDocument();
                    doc.processHTMLFrameHyperlinkEvent(evt);
                } else {
                    URL url = pEvent.getURL();
                    try {
                        String desc = pEvent.getDescription();
                        if (url == null) {
                            /* Access the referenced link */
                            JDataDetail myDetail = theDetail.getDataLink(desc);

                            /* Shift to this link */
                            if (myDetail != null) {
                                displayDetail(myDetail);
                            }
                        } else {
                            theEditor.setPage(pEvent.getURL());
                        }
                    } catch (IOException e) {
                        url = null;
                    }
                }
            }
        }

        @Override
        public void actionPerformed(final ActionEvent pEvent) {
            Object o = pEvent.getSource();
            /* If we asked for the next item */
            if (theNext.equals(o)) {
                shiftIterator(SHIFT_ONE);
            } else if (theNextTen.equals(o)) {
                shiftIterator(SHIFT_TEN);
            } else if (theNextHun.equals(o)) {
                shiftIterator(SHIFT_HUNDRED);
            } else if (theNextThou.equals(o)) {
                shiftIterator(SHIFT_THOU);
            } else if (thePrev.equals(o)) {
                shiftIterator(-SHIFT_ONE);
            } else if (thePrevTen.equals(o)) {
                shiftIterator(-SHIFT_TEN);
            } else if (thePrevHun.equals(o)) {
                shiftIterator(-SHIFT_HUNDRED);
            } else if (thePrevThou.equals(o)) {
                shiftIterator(-SHIFT_THOU);
            }
        }

        @Override
        public void itemStateChanged(final ItemEvent pEvent) {
            /* If the event was the toggle button */
            if (theToggle.equals(pEvent.getSource())) {
                /* If we are selecting list view */
                if (pEvent.getStateChange() == ItemEvent.SELECTED) {
                    /* If we need to switch to item view */
                    if (!isListMode) {
                        /* Set list mode and display item */
                        isListMode = true;
                        displayItem();
                    }

                    /* else if we are deselecting list view */
                } else if (pEvent.getStateChange() == ItemEvent.DESELECTED) {
                    /* If we need to switch to header view */
                    if (isListMode) {
                        /* Clear list mode and display header */
                        isListMode = false;
                        displayHeader();
                    }
                }
            }
        }
    }
}
