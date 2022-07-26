package io.opsit.version;



import static io.opsit.version.Version.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.util.Arrays;

public class VersionTests {
  // FIXME:
  // test invalid cases, whitespace, etc

  @Test
  public void testSemanticVersionPart() throws Exception {
    Version ver = Version.mkSemVersion(1L, 2L, 3L, null, null);
    assertTrue(ver.isSemanticVersionPart("0"));
    assertTrue(ver.isSemanticVersionPart("1"));
    assertTrue(ver.isSemanticVersionPart("2"));
    assertTrue(ver.isSemanticVersionPart(Integer.toString(Integer.MAX_VALUE)));
    assertTrue(ver.isSemanticVersionPart(Long.toString(Long.MAX_VALUE)));
    assertFalse(ver.isSemanticVersionPart("-1"));
    assertFalse(ver.isSemanticVersionPart(Long.toString(Long.MIN_VALUE)));
    assertFalse(ver.isSemanticVersionPart("00"));
    assertFalse(ver.isSemanticVersionPart("01"));
    assertFalse(ver.isSemanticVersionPart("1ab"));
    assertFalse(ver.isSemanticVersionPart("1 "));
    assertFalse(ver.isSemanticVersionPart(" 1"));
    assertFalse(ver.isSemanticVersionPart("1.1"));
    assertFalse(ver.isSemanticVersionPart("1.0"));
    assertFalse(ver.isSemanticVersionPart(".0"));
  }

  @Test
  public void testSemanticVersionParts() throws Exception {
    Version ver = Version.mkSemVersion(1L, 2L, 3L, null, null);
    assertTrue(ver.isSemanticVersionParts(list("0","0","0")));
    assertTrue(ver.isSemanticVersionParts(list("0","0","0")));
    assertTrue(ver.isSemanticVersionParts(list("1","2","3")));
    assertFalse(ver.isSemanticVersionParts(list("1","2")));
    assertFalse(ver.isSemanticVersionParts(list("1")));
    assertFalse(ver.isSemanticVersionParts(list()));
    assertFalse(ver.isSemanticVersionParts(null));
    assertFalse(ver.isSemanticVersionParts(list("-1","0","0")));
    assertFalse(ver.isSemanticVersionParts(list("0","0",null)));
  }
  

  @Test
  public void testMkVersion() throws Exception {
    Version ver = Version.mkVersion(list(1L,2L,3), null, null);
    assertNotNull(ver);
    assertEquals(1L, ver.getMajorNum());
    assertEquals(2L, ver.getMinorNum());
    assertEquals(3L, ver.getPatchNum());
    assertEquals(list(), ver.getPrereleaseIds());
    assertEquals(list(), ver.getBuildIds());
    assertTrue(ver.hasMajor());
    assertTrue(ver.hasMinor());
    assertTrue(ver.hasPatch());
    assertTrue(ver.isSemantic());
    assertEquals("1.2.3", ver.toString());
  }


  @Test
  public void testMkVersionNeg() throws Exception {
    Version ver = Version.mkVersion(list(-1L,-2L,-3), null, null);
    assertNotNull(ver);
    assertEquals(-1L, ver.getMajorNum());
    assertEquals(-2L, ver.getMinorNum());
    assertEquals(-3L, ver.getPatchNum());
    assertEquals(list(), ver.getPrereleaseIds());
    assertEquals(list(), ver.getBuildIds());
    assertTrue(ver.hasMajor());
    assertTrue(ver.hasMinor());
    assertTrue(ver.hasPatch());
    assertFalse(ver.isSemantic());
    assertEquals("-1.-2.-3", ver.toString());
  }

  
  @Test
  public void testMkSemVersion() throws Exception {
    Version ver = Version.mkSemVersion(1L,2L,3L, null, null);
    assertNotNull(ver);
    assertEquals(1L, ver.getMajorNum());
    assertEquals(2L, ver.getMinorNum());
    assertEquals(3L, ver.getPatchNum());
    assertEquals(list(), ver.getPrereleaseIds());
    assertEquals(list(), ver.getBuildIds());
    assertTrue(ver.hasMajor());
    assertTrue(ver.hasMinor());
    assertTrue(ver.hasPatch());
    assertTrue(ver.isSemantic());
    assertEquals("1.2.3", ver.toString());
  }

  @Test
  public void testSemVersionPrerelease1_2() throws Exception {
    Version ver = Version.mkSemVersion(1L,2L,3L, list("1","2"), null);
    assertNotNull(ver);
    assertEquals(1L, ver.getMajorNum());
    assertEquals(2L, ver.getMinorNum());
    assertEquals(3L, ver.getPatchNum());
    assertEquals(list("1","2"),ver.getPrereleaseIds());
    assertEquals(list(), ver.getBuildIds());
    assertTrue(ver.hasMajor());
    assertTrue(ver.hasMinor());
    assertTrue(ver.hasPatch());
    assertTrue(ver.isSemantic());
    assertEquals("1.2.3-1.2", ver.toString());
  }

  @Test
  public void testSemVersionPrerelease0_a2() throws Exception {
    Version ver = Version.mkSemVersion(1L,2L,3L, list("0","a2"), null);
    assertNotNull(ver);
    assertEquals(1L, ver.getMajorNum());
    assertEquals(2L, ver.getMinorNum());
    assertEquals(3L, ver.getPatchNum());
    assertEquals(list("0", "a2"),
                 ver.getPrereleaseIds());
    assertEquals(list(), ver.getBuildIds());
    assertTrue(ver.hasMajor());
    assertTrue(ver.hasMinor());
    assertTrue(ver.hasPatch());
    assertTrue(ver.isSemantic());
    assertEquals("1.2.3-0.a2", ver.toString());
  }

  @Test
  public void testSemVersionPrereleaseEmpty() throws Exception {
    Version ver = Version.mkSemVersion(1L,2L,3L, list(), null);
    assertNotNull(ver);
    assertEquals(1L, ver.getMajorNum());
    assertEquals(2L, ver.getMinorNum());
    assertEquals(3L, ver.getPatchNum());
    assertEquals(list(), ver.getPrereleaseIds());
    assertEquals(list(), ver.getBuildIds());
    assertTrue(ver.hasMajor());
    assertTrue(ver.hasMinor());
    assertTrue(ver.hasPatch());
    assertTrue(ver.isSemantic());
    assertEquals("1.2.3", ver.toString());
  }

    
  @Test
  public void testNotSemVersionMajorMinor() {
    try {
      Version ver = Version.mkSemVersion(1L,2L,null, null, null);
      fail("Exception was expected, but got " + ver);
    } catch (IllegalArgumentException ex) {
      assertEquals("Invalid patch version: null",ex.getMessage());
    }
  }

  @Test
  public void testNotSemVersionMajorPatch() {
    try {
      Version ver = Version.mkSemVersion(1L,null,2L, null, null);
      fail("Exception was expected, but got " + ver);
    } catch (IllegalArgumentException ex) {
      assertEquals("Invalid minor version: null",ex.getMessage());
    }
  }

  @Test
  public void testSemVersionBuild0_a2() throws Exception {
    Version ver = Version.mkSemVersion(1L,2L,3L, null, list("0","a2"));
    assertNotNull(ver);
    assertEquals(1L, ver.getMajorNum());
    assertEquals(2L, ver.getMinorNum());
    assertEquals(3L, ver.getPatchNum());
    assertEquals(list("0","a2"), ver.getBuildIds());
    assertEquals(list(), ver.getPrereleaseIds());
    assertTrue(ver.hasMajor());
    assertTrue(ver.hasMinor());
    assertTrue(ver.hasPatch());
    assertTrue(ver.isSemantic());
    assertEquals("1.2.3+0.a2", ver.toString());
  }

  @Test
  public void testSemVersionRelease1_x6_Build0_a2() throws Exception {
    Version ver = Version.mkSemVersion(1L,2L,3L,
                                     list(1,"x6"),
                                     list(0,"a2"));
    assertNotNull(ver);
    assertEquals(1L, ver.getMajorNum());
    assertEquals(2L, ver.getMinorNum());
    assertEquals(3L, ver.getPatchNum());
    assertEquals(list("0", "a2"),ver.getBuildIds());
    assertEquals(list("1","x6"), ver.getPrereleaseIds());
    assertTrue(ver.hasMajor());
    assertTrue(ver.hasMinor());
    assertTrue(ver.hasPatch());
    assertTrue(ver.isSemantic());
    assertEquals("1.2.3-1.x6+0.a2", ver.toString());
  }

  @Test
  public void testParseVersion1_2_3() throws Exception {
    Version ver = Version.parseVersion("1.2.3");
    assertNotNull(ver);
    assertEquals(1L, ver.getMajorNum());
    assertEquals(2L, ver.getMinorNum());
    assertEquals(3L, ver.getPatchNum());
    assertEquals(list(), ver.getPrereleaseIds());
    assertEquals(list(), ver.getBuildIds());
    assertTrue(ver.hasMajor());
    assertTrue(ver.hasMinor());
    assertTrue(ver.hasPatch());
    assertTrue(ver.isSemantic());
    assertEquals("1.2.3", ver.toString());
  }

  @Test
  public void testParseVersion1_2() throws Exception {
    Version ver = Version.parseVersion("1.2");
    assertNotNull(ver);
    assertEquals(1L, ver.getMajorNum());
    assertEquals(2L, ver.getMinorNum());
    assertEquals(0L, ver.getPatchNum());
    assertEquals(list(), ver.getPrereleaseIds());
    assertEquals(list(), ver.getBuildIds());
    assertTrue(ver.hasMajor());
    assertTrue(ver.hasMinor());
    assertFalse(ver.hasPatch());
    assertFalse(ver.isSemantic());
    assertEquals("1.2", ver.toString());
  }
    
  @Test
  public void testSemVersionParseRelease1_x6_Build0_a2() throws Exception {
    Version ver = Version.parseVersion("1.2.3-1.x6+0.a2");
    assertNotNull(ver);
    assertEquals(1L, ver.getMajorNum());
    assertEquals(2L, ver.getMinorNum());
    assertEquals(3L, ver.getPatchNum());
        
    assertEquals(list("1","x6"),
                 ver.getPrereleaseIds());
    assertEquals(list("0","a2"),
                 ver.getBuildIds());
    assertTrue(ver.hasMajor());
    assertTrue(ver.hasMinor());
    assertTrue(ver.hasPatch());
    assertTrue(ver.isSemantic());
    assertEquals("1.2.3-1.x6+0.a2", ver.toString());
  }

  @Test
  public void testSemVersionParseRelease1_x6_Build0_a2_() throws Exception {
    Version ver = Version.parseVersion("1.2.3-1.x6+0.a2");
    assertNotNull(ver);
    assertEquals(1L, ver.getMajorNum());
    assertEquals(2L, ver.getMinorNum());
    assertEquals(3L, ver.getPatchNum());
        
    assertEquals(list("1","x6"),
                 ver.getPrereleaseIds());
    assertEquals(list("0","a2"),
                 ver.getBuildIds());
    assertTrue(ver.hasMajor());
    assertTrue(ver.hasMinor());
    assertTrue(ver.hasPatch());
    assertTrue(ver.isSemantic());
    assertEquals("1.2.3-1.x6+0.a2", ver.toString());
  }

  
  @Test
  public void testSemVersionParseRelease1_2_3_4() throws Exception {
    Version ver = Version.parseVersion("1.2.3.4");
    assertNotNull(ver);
    assertEquals(1L, ver.getMajorNum());
    assertEquals(2L, ver.getMinorNum());
    assertEquals(3L, ver.getPatchNum());
    assertEquals(4L, ver.getVersionPartNum(3));
        
    assertEquals(list(),ver.getPrereleaseIds());
    assertEquals(list(),ver.getBuildIds());
    assertTrue(ver.hasMajor());
    assertTrue(ver.hasMinor());
    assertTrue(ver.hasPatch());
    assertFalse(ver.isSemantic());
    assertEquals("1.2.3.4", ver.toString());
  }


  @Test
  public void testSemVersionParseRelease1_2_3_4_rel1() throws Exception {
    Version ver = Version.parseVersion("1.2.3.4-Rel1");
    assertNotNull(ver);
    assertEquals(1L, ver.getMajorNum());
    assertEquals(2L, ver.getMinorNum());
    assertEquals(3L, ver.getPatchNum());
    assertEquals(4L, ver.getVersionPartNum(3));
        
    assertEquals(list("Rel1"),ver.getPrereleaseIds());
    assertEquals(list(),ver.getBuildIds());
    assertTrue(ver.hasMajor());
    assertTrue(ver.hasMinor());
    assertTrue(ver.hasPatch());
    assertFalse(ver.isSemantic());
    assertEquals("1.2.3.4-Rel1", ver.toString());
  }

  @Test
  public void testSemVersionParseRelease1_2_3_rel1() throws Exception {
    Version ver = Version.parseVersion("1.2.3-Rel1");
    assertNotNull(ver);
    assertEquals(1L, ver.getMajorNum());
    assertEquals(2L, ver.getMinorNum());
    assertEquals(3L, ver.getPatchNum());
    
    assertEquals(list("Rel1"),ver.getPrereleaseIds());
    assertEquals(list(),ver.getBuildIds());
    assertTrue(ver.hasMajor());
    assertTrue(ver.hasMinor());
    assertTrue(ver.hasPatch());
    assertTrue(ver.isSemantic());
    assertEquals("1.2.3-Rel1", ver.toString());
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
    assertTrue(Version.compareVerComp("100a","9") < 0);
    assertTrue(Version.compareVerComp("100a","9a") < 0);

    assertTrue(Version.compareVerComp("2","1") > 0);
    assertTrue(Version.compareVerComp("b","a") > 0);
    assertTrue(Version.compareVerComp("1b","1a") > 0);
    assertTrue(Version.compareVerComp("100","90") > 0);
    assertTrue(Version.compareVerComp("9","100a") > 0);
    assertTrue(Version.compareVerComp("9a","100a") > 0);

    assertTrue(Version.compareVerComp("a",null)  > 0);
    assertTrue(Version.compareVerComp("9",null)  > 0);
    assertTrue(Version.compareVerComp("0",null)  > 0);

    assertTrue(Version.compareVerComp(null, "a")  < 0);
    assertTrue(Version.compareVerComp(null, "9")  < 0);
    assertTrue(Version.compareVerComp(null, "0")  < 0);

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

  @Test
  public void testDocsExample() {
    Version v1 = Version.parseVersion("1.2.3");

    v1.isSemantic(); // => true
    assertTrue(v1.isSemantic());
    
    Version v2 = Version.parseVersion("1.2.3-beta");

    v2.isSemantic(); // => true
    assertTrue(v2.isSemantic());

    int isLater = v1.compareTo(v2); // => 1
    assertEquals(1, isLater);

    Version v3 = v1.add(Version.parseVersion("0.0.1-alpha")); // => 1.2.4-alpha
    assertEquals("1.2.4-alpha", v3.toString());

    Version v4 = Version.parseVersion("1.2.3.4"); // => 1.2.3.4
    assertEquals("1.2.3.4", v4.toString());
    
    v4.isSemantic(); // => false
    assertFalse(v4.isSemantic());

    Version v5 = Version.mkSemVersion(1, 2, 3,
                                      Arrays.asList("alpha","1"),
                                      Arrays.asList("build1")); // => 1.2.3-alpha.1+build1
    assertEquals("1.2.3-alpha.1+build1", v5.toString());
  }

}
