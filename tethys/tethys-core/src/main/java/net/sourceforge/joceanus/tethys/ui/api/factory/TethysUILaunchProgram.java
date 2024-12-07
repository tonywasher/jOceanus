package net.sourceforge.joceanus.tethys.ui.api.factory;

import java.io.InputStream;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIProgram;

/**
 * Launch Program interface.
 */
public abstract class TethysUILaunchProgram
    extends TethysUIProgram {
    /**
     * Constructor.
     * @param pProperties the inputStream of the properties
     */
    protected TethysUILaunchProgram(final InputStream pProperties) {
        super(pProperties);
    }

    /**
     * create a new mainPanel.
     * @param pFactory the factory
     * @return the main panel
     * @throws OceanusException on error
     */
    public abstract TethysUIMainPanel createMainPanel(TethysUIFactory<?> pFactory) throws OceanusException;
}
