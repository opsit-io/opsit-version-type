package org.dudinea.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import static org.dudinea.version.Version.list;



public class VersionTests {
    @Test
    public void testSemVersion() throws Exception {
        Version v = Version.mkSemVersion(1L,2L,3L, null, null);
        assertNotNull(v);
        assertEquals(1L, v.getMajorNum());
        assertEquals(2L, v.getMinorNum());
        assertEquals(3L, v.getPatchNum());
        assertEquals(list(), v.getPrereleaseIds());
        assertEquals(list(), v.getBuildIds());
        assertTrue(v.hasMajor());
        assertTrue(v.hasMinor());
        assertTrue(v.hasPatch());
        assertTrue(v.isSemantic());
        assertEquals("1.2.3", v.toString());
    }

    @Test
    public void testSemVersionPrerelease1_2() throws Exception {
        Version v = Version.mkSemVersion(1L,2L,3L, list("1","2"), null);
        assertNotNull(v);
        assertEquals(1L, v.getMajorNum());
        assertEquals(2L, v.getMinorNum());
        assertEquals(3L, v.getPatchNum());
        assertEquals(list("1","2"),v.getPrereleaseIds());
        assertEquals(list(), v.getBuildIds());
        assertTrue(v.hasMajor());
        assertTrue(v.hasMinor());
        assertTrue(v.hasPatch());
        assertTrue(v.isSemantic());
        assertEquals("1.2.3-1.2", v.toString());
    }

    @Test
    public void testSemVersionPrerelease0_a2() throws Exception {
        Version v = Version.mkSemVersion(1L,2L,3L, list("0","a2"), null);
        assertNotNull(v);
        assertEquals(1L, v.getMajorNum());
        assertEquals(2L, v.getMinorNum());
        assertEquals(3L, v.getPatchNum());
        assertEquals(list("0", "a2"),
                     v.getPrereleaseIds());
        assertEquals(list(), v.getBuildIds());
        assertTrue(v.hasMajor());
        assertTrue(v.hasMinor());
        assertTrue(v.hasPatch());
        assertTrue(v.isSemantic());
        assertEquals("1.2.3-0.a2", v.toString());
    }

    @Test
    public void testSemVersionPrereleaseEmpty() throws Exception {
        Version v = Version.mkSemVersion(1L,2L,3L, list(), null);
        assertNotNull(v);
        assertEquals(1L, v.getMajorNum());
        assertEquals(2L, v.getMinorNum());
        assertEquals(3L, v.getPatchNum());
        assertEquals(list(), v.getPrereleaseIds());
        assertEquals(list(), v.getBuildIds());
        assertTrue(v.hasMajor());
        assertTrue(v.hasMinor());
        assertTrue(v.hasPatch());
        assertTrue(v.isSemantic());
        assertEquals("1.2.3", v.toString());
    }

    
    @Test
    public void testNotSemVersionMajorMinor() {
        try {
            Version v = Version.mkSemVersion(1L,2L,null, null, null);
            fail("Exception was expected, but got "+v);
        } catch (IllegalArgumentException ex) {
            assertEquals("Invalid null patch version",ex.getMessage());
        }
    }

    @Test
    public void testNotSemVersionMajorPatch() {
        try {
            Version v = Version.mkSemVersion(1L,null,2L, null, null);
            fail("Exception was expected, but got "+v);
        } catch (IllegalArgumentException ex) {
            assertEquals("Invalid null minor version",ex.getMessage());
            //System.out.println("Expected exception: "+ ex);
        }
    }

    @Test
    public void testSemVersionBuild0_a2() throws Exception {
        Version v = Version.mkSemVersion(1L,2L,3L, null, list("0","a2"));
        assertNotNull(v);
        assertEquals(1L, v.getMajorNum());
        assertEquals(2L, v.getMinorNum());
        assertEquals(3L, v.getPatchNum());
        assertEquals(list("0","a2"), v.getBuildIds());
        assertEquals(list(), v.getPrereleaseIds());
        assertTrue(v.hasMajor());
        assertTrue(v.hasMinor());
        assertTrue(v.hasPatch());
        assertTrue(v.isSemantic());
        assertEquals("1.2.3+0.a2", v.toString());
    }

    @Test
    public void testSemVersionRelease1_x6_Build0_a2() throws Exception {
        Version v = Version.mkSemVersion(1L,2L,3L,
                                         list(1,"x6"),
                                         list(0,"a2"));
        assertNotNull(v);
        assertEquals(1L, v.getMajorNum());
        assertEquals(2L, v.getMinorNum());
        assertEquals(3L, v.getPatchNum());
        assertEquals(list("0", "a2"),v.getBuildIds());
        assertEquals(list("1","x6"), v.getPrereleaseIds());
        assertTrue(v.hasMajor());
        assertTrue(v.hasMinor());
        assertTrue(v.hasPatch());
        assertTrue(v.isSemantic());
        assertEquals("1.2.3-1.x6+0.a2", v.toString());
    }

    @Test
    public void testParseVersion1_2_3() throws Exception {
        Version v = Version.parseVersion("1.2.3");
        assertNotNull(v);
        assertEquals(1L, v.getMajorNum());
        assertEquals(2L, v.getMinorNum());
        assertEquals(3L, v.getPatchNum());
        assertEquals(list(), v.getPrereleaseIds());
        assertEquals(list(), v.getBuildIds());
        assertTrue(v.hasMajor());
        assertTrue(v.hasMinor());
        assertTrue(v.hasPatch());
        assertTrue(v.isSemantic());
        assertEquals("1.2.3", v.toString());
    }

    @Test
    public void testParseVersion1_2() throws Exception {
        Version v = Version.parseVersion("1.2");
        assertNotNull(v);
        assertEquals(1L, v.getMajorNum());
        assertEquals(2L, v.getMinorNum());
        assertEquals(0L, v.getPatchNum());
        assertEquals(list(), v.getPrereleaseIds());
        assertEquals(list(), v.getBuildIds());
        assertTrue(v.hasMajor());
        assertTrue(v.hasMinor());
        assertFalse(v.hasPatch());
        assertFalse(v.isSemantic());
        assertEquals("1.2", v.toString());
    }
    
    @Test
    public void testSemVersionParseRelease1_x6_Build0_a2() throws Exception {
        Version v = Version.parseVersion("1.2.3-1.x6+0.a2");
        assertNotNull(v);
        assertEquals(1L, v.getMajorNum());
        assertEquals(2L, v.getMinorNum());
        assertEquals(3L, v.getPatchNum());
        
        assertEquals(list("1","x6"),
                     v.getPrereleaseIds());
        assertEquals(list("0","a2"),
                     v.getBuildIds());
        assertTrue(v.hasMajor());
        assertTrue(v.hasMinor());
        assertTrue(v.hasPatch());
        assertTrue(v.isSemantic());
        assertEquals("1.2.3-1.x6+0.a2", v.toString());
    }

    @Test
    public void testSemVersionParseRelease1_2_3_4() throws Exception {
        Version v = Version.parseVersion("1.2.3.4");
        assertNotNull(v);
        assertEquals(1L, v.getMajorNum());
        assertEquals(2L, v.getMinorNum());
        assertEquals(3L, v.getPatchNum());
        assertEquals(4L, v.getVersionPartNum(3));
        
        assertEquals(list(),v.getPrereleaseIds());
        assertEquals(list(),v.getBuildIds());
        assertTrue(v.hasMajor());
        assertTrue(v.hasMinor());
        assertTrue(v.hasPatch());
        assertFalse(v.isSemantic());
        assertEquals("1.2.3.4", v.toString());
    }


    @Test
    public void testSemVersionParseRelease1_2_3_4_rel1() throws Exception {
        Version v = Version.parseVersion("1.2.3.4-Rel1");
        assertNotNull(v);
        assertEquals(1L, v.getMajorNum());
        assertEquals(2L, v.getMinorNum());
        assertEquals(3L, v.getPatchNum());
        assertEquals(4L, v.getVersionPartNum(3));
        
        assertEquals(list("Rel1"),v.getPrereleaseIds());
        assertEquals(list(),v.getBuildIds());
        assertTrue(v.hasMajor());
        assertTrue(v.hasMinor());
        assertTrue(v.hasPatch());
        //assertTrue(v.isSemantic());
        assertEquals("1.2.3.4-Rel1", v.toString());
    }

    @Test
    public void testEquality() throws Exception {
        assertEquals(Version.parseVersion("1.2.3.4-1+2"), Version.parseVersion("1.2.3.4-1+2") );
        assertEquals(Version.parseVersion("1.2.3-1+2"),
                     Version.mkSemVersion(1L, 2L, 3L, list(1), list(2)));
        assertEquals(Version.parseVersion("1.2.3-1+2"),
                     Version.mkSemVersion(1L, 2L, 3L, list("1"), list("2")));
    }

    @Test
    public void testAddNum() throws Exception {
        Version v1 = Version.parseVersion("1.2.3");
        Version v2 = Version.parseVersion("1.2.3");
        Version v3 = v1.add(v2);
        
        assertEquals(Version.parseVersion("2.4.6"),
                     v3);
    }

    @Test
    public void testAddTxt() throws Exception {
        Version v1 = Version.parseVersion("1.2.3");
        Version v2 = Version.parseVersion("a.b.c");
        Version v3 = v1.add(v2);
        
        assertEquals(Version.parseVersion("1a.2b.3c"),
                     v3);
    }

    @Test
    public void testAddAllFields() throws Exception {
        Version v1 = Version.parseVersion("1.2.3-4.5+6.7");
        Version v2 = Version.parseVersion("2.3.4-5.6+7.8");
        Version v3 = v1.add(v2);
        
        assertEquals(Version.parseVersion("3.5.7-9.11+13.15"),
                     v3);
    }

    @Test
    public void testSubAllFields() throws Exception {
        Version v1 = Version.parseVersion("1.2.3-4.5+6.7");
        Version v2 = Version.parseVersion("1.1.1-1.1+1.1");
        Version v3 = v1.sub(v2);
        
        assertEquals(Version.parseVersion("0.1.2-3.4+5.6"),
                     v3);
    }

    @Test
    public void testAddRelBuildFields() throws Exception {
        Version v1 = Version.parseVersion("1.2.3-1+2");
        Version v2 = Version.parseVersion("0.0.0-1+1");
        Version v3 = v1.add(v2);
        
        assertEquals(Version.parseVersion("1.2.3-2+3"), v3);
    }

    @Test
    public void testCompCompare() throws Exception {
        assertTrue(Version.compareVerComp("1","2") < 0);
        assertTrue(Version.compareVerComp("a","b") < 0);
        assertTrue(Version.compareVerComp("1a","1b") < 0);
        assertTrue(Version.compareVerComp("90","100") < 0);
        assertTrue(Version.compareVerComp("100a","9") <0);
        assertTrue(Version.compareVerComp("100a","9a")< 0);

        assertTrue(Version.compareVerComp("2","1") > 0);
        assertTrue(Version.compareVerComp("b","a") > 0);
        assertTrue(Version.compareVerComp("1b","1a") > 0);
        assertTrue(Version.compareVerComp("100","90") > 0);
        assertTrue(Version.compareVerComp("9","100a") > 0);
        assertTrue(Version.compareVerComp("9a","100a") > 0);

        assertTrue(Version.compareVerComp("a",null)  >0);
        assertTrue(Version.compareVerComp("9",null)  >0);
        assertTrue(Version.compareVerComp("0",null)  >0);

        assertTrue(Version.compareVerComp(null, "a")  <0);
        assertTrue(Version.compareVerComp(null, "9")  <0);
        assertTrue(Version.compareVerComp(null, "0")  <0);

        assertTrue(Version.compareVerComp("9","9")  == 0);
        assertTrue(Version.compareVerComp("9a","9a")  == 0);

        assertTrue(Version.compareVerComp(null,null)  == 0);
    }

    @Test
    public void testCompareTo() throws Exception {
        String [] specs = {
            "0.0.8", "0.0.9","0.1.0","0.1.1","0.1.1a",
            "1.0.0-alpha", // until 1.0.0 - example from the semVer spec 2.0
            "1.0.0-alpha.1", "1.0.0-alpha.beta", "1.0.0-beta", "1.0.0-beta.2", "1.0.0-beta.11",
            "1.0.0-rc.1", "1.0.0"};
        Version[] parsed = new Version[specs.length];
        for (int i = 0; i < specs.length; i++) {
            parsed[i] = Version.parseVersion(specs[i]);
        }

        for (int i = 0; i < parsed.length; i++) {
            for (int j = 0; j < parsed.length; j++) {
                Version vi = parsed[i];
                assertNotNull(vi);
                Version vj = parsed[j];
                int result = vi.compareTo(vj);
                if (i > j) {
                    assertTrue("" + vj + " must be less than " + vi, result > 0);
                } else if (i == j) {
                    assertTrue("" + vj + " must be equal to " + vi, result == 0);
                } else {
                    assertTrue("" + vj + " must be more than " + vi, result < 0);
                }

                result = vj.compareTo(vi);
                if (i < j) {
                    assertTrue("" + vj + " must be less than " + vi, result > 0);
                } else if (i == j) {
                    assertTrue("" + vj + " must be equal to " + vi, result == 0);
                } else {
                    assertTrue("" + vj + " must be more than " + vi, result < 0);
                }

            }
        }
    }

    
    /*@Test
    public void testFromInt() throws Exception {
        Version v1 = new Version(1L);
        assertEquals(Version.parseVersion("1.0.0"), v1);
        }*/
}
