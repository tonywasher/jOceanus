/* *****************************************************************************
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
package net.sourceforge.joceanus.jmoneywise.test.data;

import java.awt.Color;

import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseUKTaxYearCache;
import net.sourceforge.joceanus.jprometheus.lethe.PrometheusToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIcon;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIconId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIProgram;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIType;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIValueSet;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.jtethys.ui.api.chart.TethysUIChartFactory;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIDialogFactory;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUILogTextArea;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIMenuFactory;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableFactory;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThread;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadEvent;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadFactory;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusManager;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreValueSet;

/**
 * Test security.
 */
public class MoneyWiseTestControl {
    /**
     * Main entry point.
     * @param pArgs the arguments
     */
    public static void main(final String[] pArgs) {
        /* Protect against exceptions */
        try {
            /* Create the data */
            final FactoryStub myFactory = new FactoryStub();
            final PrometheusToolkit myToolkit = new PrometheusToolkit(myFactory);
            final MoneyWiseData myData = new MoneyWiseData(myToolkit, new MoneyWiseUKTaxYearCache());

            /* Initialise the data */
            new MoneyWiseTestSecurity(myData).initSecurity();
            new MoneyWiseTestCategories(myData).buildBasic();
            new MoneyWiseTestAccounts(myData).createAccounts();

            /* Catch exceptions */
        } catch (OceanusException e) {
            e.printStackTrace();
        }
    }

    /**
     * Factory stub.
     */
    private static class FactoryStub
        implements TethysUIFactory<Color> {
        /**
         * The DataFormatter.
         */
        private final TethysUIDataFormatter theFormatter;

        /**
         * The ValueSet.
         */
        private final TethysUIValueSet theValueSet;

        /**
         * The Profile.
         */
        private final TethysProfile theProfile;

        /**
         * The LogSink.
         */
        private final TethysUILogTextArea theLogSink;

        /**
         * The ThreadFactory.
         */
        private final TethysUIThreadFactory theThreadFactory;

        /**
         * Constructor.
         */
        FactoryStub() {
            theFormatter = new TethysUICoreDataFormatter();
            theProfile = new TethysProfile("Dummy");
            theValueSet = new TethysUICoreValueSet();
            theLogSink = new SinkStub();
            theThreadFactory = new ThreadFactoryStub();
        }

        @Override
        public TethysUIType getGUIType() {
            return null;
        }

        @Override
        public TethysUIDataFormatter getDataFormatter() {
            return theFormatter;
        }

        @Override
        public TethysUIDataFormatter newDataFormatter() {
            return new TethysUICoreDataFormatter();
        }

        @Override
        public TethysUIValueSet getValueSet() {
            return theValueSet;
        }

        @Override
        public TethysUIIcon resolveIcon(TethysUIIconId pIconId, int pWidth) {
            return null;
        }

        @Override
        public TethysUILogTextArea getLogSink() {
            return theLogSink;
        }

        @Override
        public void activateLogSink() {
        }

        @Override
        public TethysUIProgram getProgramDefinitions() {
            return null;
        }

        @Override
        public TethysProfile getNewProfile(String pTask) {
            return new TethysProfile(pTask);
        }

        @Override
        public TethysProfile getActiveProfile() {
            return theProfile;
        }

        @Override
        public TethysProfile getActiveTask() {
            return theProfile;
        }

        @Override
        public TethysUIButtonFactory<Color> buttonFactory() {
            return null;
        }

        @Override
        public TethysUIChartFactory chartFactory() {
            return null;
        }

        @Override
        public TethysUIControlFactory controlFactory() {
            return null;
        }

        @Override
        public TethysUIDialogFactory dialogFactory() {
            return null;
        }

        @Override
        public TethysUIFieldFactory fieldFactory() {
            return null;
        }

        @Override
        public TethysUIMenuFactory menuFactory() {
            return null;
        }

        @Override
        public TethysUIPaneFactory paneFactory() {
            return null;
        }

        @Override
        public TethysUITableFactory tableFactory() {
            return null;
        }

        @Override
        public TethysUIThreadFactory threadFactory() {
            return theThreadFactory;
        }
    }

    /**
     * Thread factory stub.
     */
    private static class ThreadFactoryStub
        implements TethysUIThreadFactory {
        @Override
        public TethysUIThreadManager newThreadManager() {
            return new ThreadManagerStub();
        }
    }

    /**
     * Thread factory stub.
     */
    private static class ThreadManagerStub
            implements TethysUIThreadManager {
        /**
         * Event Manager.
         */
        private final TethysEventManager<TethysUIThreadEvent> theEventManager;

        /**
         * Constructor.
         */
        ThreadManagerStub() {
            theEventManager = new TethysEventManager<>();
        }

        @Override
        public TethysEventRegistrar<TethysUIThreadEvent> getEventRegistrar() {
            return theEventManager.getEventRegistrar();
        }

        @Override
        public TethysUIThreadStatusManager getStatusManager() {
            return null;
        }

        @Override
        public String getTaskName() {
            return null;
        }

        @Override
        public boolean hasWorker() {
            return false;
        }

        @Override
        public void setReportingSteps(int pSteps) {
        }

        @Override
        public Throwable getError() {
            return null;
        }

        @Override
        public void setThreadData(Object pThreadData) {
        }

        @Override
        public Object getThreadData() {
            return null;
        }

        @Override
        public <T> void startThread(TethysUIThread<T> pThread) {
        }

        @Override
        public void shutdown() {
        }

        @Override
        public void cancelWorker() {
        }

        @Override
        public TethysProfile getActiveProfile() {
            return null;
        }

        @Override
        public void initTask(String pTask) throws OceanusException {
        }

        @Override
        public void setNumStages(int pNumStages) throws OceanusException {
        }

        @Override
        public void setNewStage(String pStage) throws OceanusException {
        }

        @Override
        public void setNumSteps(int pNumSteps) throws OceanusException {
        }

        @Override
        public void setStepsDone(int pSteps) throws OceanusException {
        }

        @Override
        public void setNextStep() throws OceanusException {
        }

        @Override
        public void setNextStep(String pStep) throws OceanusException {
        }

        @Override
        public void setCompletion() throws OceanusException {
        }

        @Override
        public void checkForCancellation() throws OceanusException {
        }

        @Override
        public void throwCancelException() throws OceanusException {
        }

        @Override
        public TethysProfile getActiveTask() {
            return null;
        }
    }

    /**
     * Sink Stub.
     */
    private static class SinkStub
        implements TethysUILogTextArea {
        @Override
        public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
            return null;
        }

        @Override
        public void writeLogMessage(String pMessage) {

        }

        @Override
        public TethysUIComponent getUnderlying() {
            return null;
        }

        @Override
        public boolean isActive() {
            return true;
        }
    }
}
