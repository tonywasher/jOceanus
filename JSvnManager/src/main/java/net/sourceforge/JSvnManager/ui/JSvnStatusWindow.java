/*******************************************************************************
 * Subversion: Java SubVersion Management
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
package net.sourceforge.JSvnManager.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sourceforge.JSvnManager.data.JSvnReporter.ReportTask;
import net.sourceforge.JSvnManager.threads.DiscoverData;

/**
 * Status window for SubVersion operations.
 * @author Tony Washer
 */
public class JSvnStatusWindow extends JPanel implements ReportTask {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -97846502218500569L;

    /**
     * SvnManager.
     */
    private final JSvnManager theManager;

    /**
     * The scroll pane.
     */
    private final JScrollPane theScroll;

    /**
     * The TextArea.
     */
    private final JTextArea theText;

    /**
     * The Clear button.
     */
    private final JButton theClearButton;

    /**
     * Constructor.
     * @param pManager the manager
     */
    protected JSvnStatusWindow(final JSvnManager pManager) {
        /* Store the manager */
        theManager = pManager;

        /* Create text area and button */
        theText = new JTextArea();
        theText.setEditable(false);
        theClearButton = new JButton("Clear");

        /* Create the scrollPane */
        theScroll = new JScrollPane();
        theScroll.setViewportView(theText);
        theScroll.setPreferredSize(new Dimension(600, 400));

        /* Add listener */
        theClearButton.addActionListener(new StatusListener());

        /* Add the components */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(theScroll);
        add(theClearButton);
    }

    @Override
    public void reportStatus(final String pStatus) {
        /* Add status to text area */
        theText.append(pStatus);
        theText.append("\n");
    }

    @Override
    public void completeTask(final Object pTask) {
        /* If this is the discoverData thread */
        if (pTask instanceof DiscoverData) {
            /* Access correctly */
            DiscoverData myThread = (DiscoverData) pTask;

            /* If there was no error */
            if (myThread.getError() == null) {
                /* Report data to manager */
                theManager.setData(myThread);
            }
        }
    }

    /**
     * Status Listener class.
     */
    private final class StatusListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent evt) {
            /* Clear contents */
            theText.setText(null);
        }
    }
}
