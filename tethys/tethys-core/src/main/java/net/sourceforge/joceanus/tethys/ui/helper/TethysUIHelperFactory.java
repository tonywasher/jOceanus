/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.tethys.ui.helper;

import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIIcon;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIIconId;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIProgram;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIType;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIValueSet;
import net.sourceforge.joceanus.tethys.ui.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.tethys.ui.api.chart.TethysUIChartFactory;
import net.sourceforge.joceanus.tethys.ui.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.tethys.ui.api.dialog.TethysUIDialogFactory;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUILogTextArea;
import net.sourceforge.joceanus.tethys.ui.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.tethys.ui.api.menu.TethysUIMenuFactory;
import net.sourceforge.joceanus.tethys.ui.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableFactory;
import net.sourceforge.joceanus.tethys.ui.api.thread.TethysUIThread;
import net.sourceforge.joceanus.tethys.ui.api.thread.TethysUIThreadEvent;
import net.sourceforge.joceanus.tethys.ui.api.thread.TethysUIThreadFactory;
import net.sourceforge.joceanus.tethys.ui.api.thread.TethysUIThreadManager;
import net.sourceforge.joceanus.tethys.ui.api.thread.TethysUIThreadStatusManager;
import net.sourceforge.joceanus.tethys.ui.core.base.TethysUICoreDataFormatter;
import net.sourceforge.joceanus.tethys.ui.core.base.TethysUICoreValueSet;

/**
 * Tethys UI factory for non-GUI tests.
 */
public class TethysUIHelperFactory
        implements TethysUIFactory<Object> {
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
    private OceanusProfile theProfile;

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
    public TethysUIHelperFactory() {
        theFormatter = new TethysUICoreDataFormatter();
        theProfile = new OceanusProfile("Dummy");
        theValueSet = new TethysUICoreValueSet();
        theLogSink = new SinkStub();
        theThreadFactory = new ThreadFactoryStub();
    }

    @Override
    public TethysUIType getGUIType() {
        return TethysUIType.HELPER;
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
    public TethysUIIcon resolveIcon(final TethysUIIconId pIconId,
                                    final int pWidth) {
        return null;
    }

    @Override
    public TethysUILogTextArea getLogSink() {
        return theLogSink;
    }

    @Override
    public void activateLogSink() {
        /* NoOp */
    }

    @Override
    public TethysUIProgram getProgramDefinitions() {
        return null;
    }

    @Override
    public OceanusProfile getNewProfile(final String pTask) {
        theProfile = new OceanusProfile(pTask);
        return theProfile;
    }

    @Override
    public OceanusProfile getActiveProfile() {
        return theProfile;
    }

    @Override
    public OceanusProfile getActiveTask() {
        return getActiveProfile();
    }

    @Override
    public TethysUIButtonFactory<Object> buttonFactory() {
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

    /**
     * Thread factory stub.
     */
    private static final class ThreadFactoryStub
            implements TethysUIThreadFactory {
        @Override
        public TethysUIThreadManager newThreadManager() {
            return new ThreadManagerStub();
        }
    }

    /**
     * Thread factory stub.
     */
    private static final class ThreadManagerStub
            implements TethysUIThreadManager {
        /**
         * Event Manager.
         */
        private final OceanusEventManager<TethysUIThreadEvent> theEventManager;

        /**
         * Constructor.
         */
        ThreadManagerStub() {
            theEventManager = new OceanusEventManager<>();
        }

        @Override
        public OceanusEventRegistrar<TethysUIThreadEvent> getEventRegistrar() {
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
        public void setReportingSteps(final int pSteps) {
            /* NoOp */
        }

        @Override
        public Throwable getError() {
            return null;
        }

        @Override
        public void setThreadData(final Object pThreadData) {
            /* NoOp */
        }

        @Override
        public Object getThreadData() {
            return null;
        }

        @Override
        public <T> void startThread(final TethysUIThread<T> pThread) {
            /* NoOp */
        }

        @Override
        public void shutdown() {
            /* NoOp */
        }

        @Override
        public void cancelWorker() {
            /* NoOp */
        }

        @Override
        public OceanusProfile getActiveProfile() {
            return null;
        }

        @Override
        public void setNewProfile(final String pName) {
            /* NoOp */
        }

        @Override
        public void initTask(final String pTask) {
            /* NoOp */
        }

        @Override
        public void setNumStages(final int pNumStages) {
            /* NoOp */
        }

        @Override
        public void setNewStage(final String pStage) {
            /* NoOp */
        }

        @Override
        public void setNumSteps(final int pNumSteps) {
            /* NoOp */
        }

        @Override
        public void setStepsDone(final int pSteps)  {
            /* NoOp */
        }

        @Override
        public void setNextStep() {
            /* NoOp */
        }

        @Override
        public void setNextStep(final String pStep) {
            /* NoOp */
        }

        @Override
        public void setCompletion() {
            /* NoOp */
        }

        @Override
        public void checkForCancellation() {
            /* NoOp */
        }

        @Override
        public void throwCancelException() {
            /* NoOp */
        }

        @Override
        public OceanusProfile getActiveTask() {
            return null;
        }
    }

    /**
     * Sink Stub.
     */
    private static final class SinkStub
            implements TethysUILogTextArea {
        @Override
        public OceanusEventRegistrar<TethysUIEvent> getEventRegistrar() {
            return null;
        }

        @Override
        public void writeLogMessage(final String pMessage) {
            /* TODO */
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
