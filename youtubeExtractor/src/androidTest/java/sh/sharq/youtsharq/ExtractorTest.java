package sh.sharq.youtsharq;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;


public class ExtractorTest extends TestSuite {

    public static Test suite() {
        return new TestSuiteBuilder(ExtractorTest.class).includeAllPackagesUnderHere().build();
    }
}



