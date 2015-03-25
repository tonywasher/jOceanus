/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.swing;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 * Wrappers for simple Swing objects that enable/disable child elements.
 */
public final class JEnableWrapper {
    /**
     * Constructor.
     */
    protected JEnableWrapper() {
    }

    /**
     * Panel Enabler Wrapper.
     */
    public static class JEnablePanel
            extends JPanel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 6457301214791456189L;

        /**
         * List of components.
         */
        private final transient List<Component> theList = new ArrayList<Component>();

        @Override
        public Component add(final Component pComponent) {
            /* Add to list */
            theList.add(pComponent);
            return super.add(pComponent);
        }

        @Override
        public void add(final Component pComponent,
                        final Object pConstraints) {
            registerComponent(pComponent);
            super.add(pComponent, pConstraints);
        }

        /**
         * Register component.
         * @param pComponent the component to register
         */
        public void registerComponent(final Component pComponent) {
            /* Add component to list */
            theList.add(pComponent);
        }

        @Override
        public void setEnabled(final boolean bEnabled) {
            /* Loop through the registered components */
            for (Component myComp : theList) {
                /* Pass call on */
                myComp.setEnabled(bEnabled);
            }
        }
    }

    /**
     * ScrollPane Enabler Wrapper.
     */
    public static class JEnableScroll
            extends JScrollPane {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 5270503063000294565L;

        /**
         * List of components.
         */
        private final transient List<Component> theList = new ArrayList<Component>();

        @Override
        public void setViewportView(final Component pComponent) {
            /* Add to list */
            theList.add(pComponent);
            super.setViewportView(pComponent);
        }

        @Override
        public void setEnabled(final boolean bEnabled) {
            /* Loop through the registered components */
            for (Component myComp : theList) {
                /* Pass call on */
                myComp.setEnabled(bEnabled);
            }
        }
    }

    /**
     * TabbedPane Enabler Wrapper.
     */
    public static class JEnableTabbed
            extends JTabbedPane {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -6858420962478125147L;

        /**
         * List of components.
         */
        private final transient List<Component> theList = new ArrayList<Component>();

        @Override
        public void addTab(final String pName,
                           final Component pComponent) {
            /* Add to list */
            theList.add(pComponent);
            super.addTab(pName, pComponent);
        }

        @Override
        public void setEnabled(final boolean bEnabled) {
            /* Loop through the registered components */
            for (Component myComp : theList) {
                /* Pass call on */
                myComp.setEnabled(bEnabled);
            }

            /* Pass on the call */
            super.setEnabled(bEnabled);
        }
    }
}
