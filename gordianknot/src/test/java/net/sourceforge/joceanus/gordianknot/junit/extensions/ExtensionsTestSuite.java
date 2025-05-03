package net.sourceforge.joceanus.gordianknot.junit.extensions;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * TestSuite for BouncyCastle Extensions.
 */
@Suite
@SuiteDisplayName("BouncyCastle Extensions")
@SelectClasses({
        Blake3Test.class,
        BlockCipherTest.class,
        DigestTest.class,
        RandomTest.class,
        StreamCipherTest.class,
        XofStreamCipherTest.class
})
public class ExtensionsTestSuite {
}
