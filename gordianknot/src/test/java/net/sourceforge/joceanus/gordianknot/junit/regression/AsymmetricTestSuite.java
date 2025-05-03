package net.sourceforge.joceanus.gordianknot.junit.regression;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * TestSuite for Asymmetric Tests.
 */
@Suite
@SuiteDisplayName("Asymmetric Test Suite")
@SelectClasses(AsymmetricTest.class)
public class AsymmetricTestSuite {
}
