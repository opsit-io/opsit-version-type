package org.dudinea.version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Version extends Number {
  static final long serialVersionUID = 1;
  
  private static final int IDX_MAJOR = 0;
  private static final int IDX_MINOR = 1;
  private static final int IDX_PATCH = 2;

  public static final double KMINOR = 0.001;
  public static final double KPATCH = 0.000001;
    
  protected String prefix = "";
    
  protected List<String> versions = list();
  protected List<String> prereleaseIds = list();
  protected List<String> buildIds = list();

  protected String src;

  private static final Pattern VU_PAT = Pattern.compile("^[0-9a-z-]+$", Pattern.CASE_INSENSITIVE);

  protected static final String VERSEP = ".-+";
  protected static final String RELSEP = ".+";
  protected static final String BUILDSEP = ".";

  /**
   * Parse version specication.
   *
   * <p>The input will be parsed into parts: version numbers (major,
   * minor, patch, ...), build and prerelease identifiers.
   *
   * <p>If no such parts were identified the method won't throw exception
   * and will return an invalid version object. Use the
   * {@link #isValid()} and {@link #isSemantic()} predicates to
   * check validity of the returned object.
   *
   * <p>Use {@link #parseValidVersion()} to ensure that version is
   * minimally valid (has Major number set) or {@link
   * #parseSemanticVersion()} to ensure that version specification
   * is avalid semantic version.
   *
   * @param  str  version specification, may be null
   * @return Return created version object or null on null input
   */
  public static Version parseVersion(String str) {
    if (null == str) {
      return null;
    }
    final Version v = new Version();
    List<String> acc = v.versions = list();
    String sep = VERSEP;
    StringTokenizer st = new StringTokenizer(str, sep, true);
    while (st.hasMoreTokens()) {
      String token = st.nextToken(sep);
      String lastDelim = "";
      if (st.hasMoreTokens()) {
        lastDelim = st.nextToken(sep);
      }
      acc.add(token);
      if (VERSEP.equals(sep)) {
        if ((".".equals(lastDelim)) || "".equals(lastDelim)) {
          // NOP
        } else if (lastDelim.contains("-")) {
          sep = RELSEP;
          acc = v.prereleaseIds;
        }
      } else if (RELSEP.equals(sep)) {
        if ((".".equals(lastDelim)) || "".equals(lastDelim)) {
          // NOP
        } else if (lastDelim.contains("+")) {
          sep = BUILDSEP;
          acc = v.buildIds;
        }
      }
    }
    return v;
  }


  /**
   * Parse version specication and ensure that resulting object represents
   * valid version.
   *
   * <p>The input will be parsed into parts: version numbers (major,
   * minor, patch, ...), build and prerelease identifiers.
   *
   * <p>If no major version number was identified the method will throw exception
   *
   * @param  str  version specification, may be null
   * @return Return the created Version object or null on null input
   */
  public static Version parseValidVersion(String str) throws IllegalArgumentException {
    final Version ver = parseVersion(str);
    if (!ver.isValid()) {
      // FIXME: specify reasons of invalidity
      throw  new IllegalArgumentException("Invalid version spec");
    }
    return ver;
  }

  /**
   * Parse version specication and ensure that resulting Version object represents
   * valid semantic version.
   *
   * <p>The input will be parsed into parts: version numbers (major,
   * minor, patch, ...), build and prerelease identifiers.
   *
   * <p>If no major version number was identified the method will throw exception
   *
   * @param  str  version specification, may be null
   * @return Return the created Version object or null on null input
   */
  public static Version parseSemanticVersion(String str) throws IllegalArgumentException {
    final Version ver = parseVersion(str);
    if (!ver.isSemantic()) {
      // FIXME: explane why it is invalid
      throw new IllegalArgumentException("Not a semantic version specification");
    }
    return ver;
  }

  /**
   * Create Version object from a Number object
   *
   * <p>The input will be converted according to its actual type:
   * <ul>
   * <li>If it is a Version it will be returned as is.
   * <li>if it is a Double or Float it will be converted using {@link #fromDouble()}
   * <li>Other number types will be converted using {@link #fromLong()}
   * </ul>
   *
   * @param  num a number
   * @return Return version object or null on null input
   */
  public static Version fromNumber(Number num) {
    if (num == null) {
      return null;
    }
    if (num instanceof Version) {
      return (Version) num;
    }
    if ((num instanceof Double) || (num instanceof Float)) {
      return fromDouble(num.doubleValue());
    }
    return fromLong(num.longValue());
  }

  /**
   * Create Version object from a double number
   *
   * <p>FIXME: describe.
   *
   * @param doubleNum a Double number
   * @return Return version object or null on null input
   */
  public static Version fromDouble(Double doubleNum) {
    if (doubleNum == null) {
      return null;
    }
    // FIXME: implement
    long major = doubleNum.longValue();
    long minor = 0L;
    long patch = 0L;
    return mkSemVersion(major, minor, patch, null, null);
  }

  /**
   * Create Version object from a Long number.
   *
   * @param  longNum a number
   * @return Version object with longNum as Major version or null on null input
   */
  public static Version fromLong(Long longNum) {
    if (longNum == null) {
      return null;
    }
    return mkSemVersion(longNum, 0L, 0L, null, null);
  }

  /**
   * Create Version object from an Integer number.
   *
   * <p>Will create a Version object with intNum as Major version
   * number.
   *
   * @param  intNum a number
   * @return Version object with intNum as Major version or null on null input
   */
  public static Version fromInt(Integer intNum) {
    if (intNum == null) {
      return null;
    }    
    return mkSemVersion((long)intNum, 0L, 0L, null, null);
  }

  /** Build a Version object representing a Semantic Version from its parts.
   *
   * @param major Major version component
   * @param minor Minor version component
   * @param patch Patch version component
   * @param build lists of build identifiers   
   * @param prerelease list of prerelease identifiers
   * @return Version object made of provided parts
   */
  public static Version mkSemVersion(Long major,
                                     Long minor,
                                     Long patch,
                                     List<Object> prerelease,
                                     List<Object> build)
    throws IllegalArgumentException {
    final Version v = new Version();
    v.setVersions(list(chkVerNumberPart(major, "major", true),
                       chkVerNumberPart(minor, "minor", true),
                       chkVerNumberPart(patch, "patch", true)));
    v.prereleaseIds = chkSemVerVUnions(prerelease);
    v.buildIds = chkSemVerVUnions(build);
    return v;
  }

  /**
   * Predicate to check for Development version according to the
   * SemVer spec.
   * 
   * @return true for for semantic versions with Major==0.
   */
  public boolean isDevelopment() {
    return isSemantic() && getMajorNum() == 0;
  }


  /**
   * Predicate to check if Version has Major version number.
   * 
   * @return true only if Major version is specified.
   */    
  public boolean hasMajor() {
    return IDX_MAJOR < versions.size();
  }

  /**
   * Predicate to check if Version has Minor version number.
   * 
   * @return true only if Minor version is specified.
   */  
  public boolean hasMinor() {
    return IDX_MINOR < versions.size();
  }
  
  /**
   * Predicate to check if Version has Patch version number (the third
   * component).
   * 
   * @return true only if Patch version is specified.
   */      
  public boolean hasPatch() {
    return IDX_PATCH < versions.size();
  }

  /**
   * Get part of version number by its position.
   *
   * @param idx - part index (Major is 0)
   * @return long value of the part, 0 if the specified part is missing
   */
  public long getVersionPartNum(int idx) {
    final String vu = getElement(versions, idx);
    if (null == vu) {
      return 0L;
    }
    try {
      long result = Long.parseLong(vu);
      return result;
    } catch (Exception ex) {
      return 0L;
    }
  }

  /**
   * Get Major version number.
   *
   * @return Major version number
   */
  public long getMajorNum() {
    return getVersionPartNum(IDX_MAJOR);
  }

  /**
   * Get Minor version number by its position.
   *
   * @return Minor version number
   */
  public long getMinorNum() {
    return getVersionPartNum(IDX_MINOR);
  }

  /**
   * Get Patch version number by its position.
   *
   * @return Patch version number
   */
  public long getPatchNum() {
    return getVersionPartNum(IDX_PATCH);
  }

  
  public List<String> getPrereleaseIds() {
    return this.prereleaseIds;
  }

  public List<String> getVersions() {
    return this.versions;
  }

  public void setVersions(List<String> versions) {
    this.versions = versions;
  }

  /**
   * Return list of build identifiers
   *
   * @return Wist of build identifiers. Empty list if there is none.
   */
  public List<String> getBuildIds() {
    return this.buildIds;
  }

  /**
   * Check if the Version conforms to the SemVer specification.
   *
   * @return true when conforming
   */
  public boolean isSemantic() {
    return isSemanticVersionParts()
      && areAllPartsSemantic(this.prereleaseIds)
      && areAllPartsSemantic(this.buildIds);
  }
  
  /**
   * Check if the Version is valid
   *
   * <p>Check if Version objects makes sense as a version specification.
   * It must have at least non negative major version number.
   *
   * @return true when valid
   */
  public boolean isValid() {
    return hasMajor() && this.getMajorNum() >= 0;
  }

  
    
  @Override
  public byte byteValue() {
    return (byte) this.getMajorNum();
  }


  @Override
  public double doubleValue() {
    return this.getMajorNum() + this.getMinorNum() * KMINOR + this.getPatchNum() * KPATCH;
  }

  @Override
  public float floatValue() {
    return (float)(this.getMajorNum() + this.getMinorNum() * KMINOR);
  }

  @Override
  public int intValue() {
    return (int) this.getMajorNum();
  }

  @Override
  public long longValue() {
    return this.getMajorNum();
  }

  @Override
  public short shortValue() {
    return (short)this.getMajorNum();
  }


  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Version)) {
      return false;
    }
    Version ov = (Version) obj;
    return equal(this.getVersions(), ov.getVersions())
      && equal(this.getPrereleaseIds(), ov.getPrereleaseIds())
      && equal(this.getBuildIds(), ov.getBuildIds());
  }

  @Override
  public int hashCode() {
    return this.asString().hashCode();
  }

  @Override
  public String toString() {
    return null != src ? src : asString();
  }

  
  /**
   * Substract the argument from the current version number.
   *
   * <p>The argument will be interpreded as a version and substracted
   * from the curent version specification.
   *
   * <p>See the project documentation on details of version arithmetics.
   *
   * @param other The substrahend
   */
  public Version sub(Number other) {
    if (null == other) {
      return this;
    } else if (other instanceof Version) {
      return sub((Version) other);
    } else {
      return sub(fromDouble(other.doubleValue()));
    }
  }

  /**
   * Substract the argument from the current version number.
   *
   * <p>The argument will be interpreded as a version and substracted
   * from the curent version specification.
   *
   * <p>See the project documentation on details of version arithmetics.
   *
   * @param other The substrahend
   */
  public Version sub(Version other) {
    final Multiop addop = new Multiop() {
        @Override
        public Object perform(String... objs) {
          String objA = objs[0];
          String objB = objs[1];
          if (null == objA) {
            return null;
          } else {
            if (isANum(objA) && isANum(objB)) {
              return Long.toString(atol(objA) - atol(objB));
            } else if (null == objB) {
              return objA;
            } else {
              if (objA.endsWith(objB)) {
                return objA.substring(0, objA.length() - objB.length());
              } else {
                return objA;
              }

            }

          }
        }
      };
    return doOp(other, addop);
  }


  /**
   * Add the argument to the current version number.
   *
   * <p>The argument will be interpreded as a version and added
   * to the curent version specification.
   *
   * <p>See the project documentation on details of version arithmetics.
   *
   * @param other Number to be added
   */  
  public Version add(Number other) {
    if (null == other) {
      return this;
    } else if (other instanceof Version) {
      return add((Version) other);
    } else {
      return add(fromDouble(other.doubleValue()));
    }
  }


  /**
   * Add the argument to the current version number.
   *
   * <p>The argument will be interpreded as a version and added
   * to the curent version specification.
   *
   * <p>See the project documentation on details of version arithmetics.
   *
   * @param other Version to be added
   */      
  public Version add(Version other) {
    final Multiop addop = new Multiop() {
        @Override
        public Object perform(String... objs) {
          String objA = objs[0];
          String objB = objs[1];
          if (null == objA) {
            return objB;
          } else {
            if (isANum(objA) && isANum(objB)) {
              return Long.toString(atol(objA) + atol(objB));
            } else if (null == objB) {
              return objA;
            } else {
              return objA + objB;
            }
          }
        }
      };
    return doOp(other, addop);
  }


  /**
   * Compare with another version
   *
   * <p>The comparison is performed according to the Semantic Versioning specification.
   *
   * @param other Version to compare to
   * @return: negative if lower, positive if bigger than the other. 0 if equal.
   */
  public int compareTo(Version other) {
    int result = 0;
    for (int i = 0; i < versions.size(); i++) {
      final String tel = getElement(this.versions, i);
      final String oel = getElement(other.versions, i);
      result = compareVerComp(tel, oel);
      if (result != 0) {
        return result;
      }
    }
    result = versions.size() - other.versions.size();
    if (result != 0) {
      return result;
    }
    // this = 1.0.0 > 1.0.0.pre
    if (this.prereleaseIds.size() == 0) {
      return other.prereleaseIds.size();
    }
    // this = 1.0.0.pre < 1.0.0
    if (other.prereleaseIds.size() == 0) {
      return - this.prereleaseIds.size();
    }
    for (int i = 0; i < this.prereleaseIds.size(); i++) {
      final String tel = getElement(this.prereleaseIds, i);
      final String oel = getElement(other.prereleaseIds, i);
      result = compareVerComp(tel, oel);
      if (result != 0) {
        return result;
      }
    }
    result = prereleaseIds.size() - other.prereleaseIds.size();
    return result;
  }

  protected static String getElement(List<String> seq, int index) {
    if (null == seq) {
      return null;
    } else {
      try {
        return seq.get(index);
      } catch (IndexOutOfBoundsException bex) {
        return null;
      }
    }
  }

  
  protected Version doOp(Version other, Multiop op) {
    if (null == other) {
      return this;
    }
    Version result = new Version();
    result.versions = mapall(op, this.versions, other.versions);
    result.prereleaseIds = mapall(op, this.prereleaseIds, other.prereleaseIds);
    result.buildIds = mapall(op, this.buildIds, other.buildIds);
    return result;
  }
  

  protected static interface  Multiop{
    public Object perform(String... objs);
  }


  @SafeVarargs
  protected static int maxLength(List<String>... seqs) {
    int result = 0;
    for (int i = 0; i < seqs.length; i++) {
      int len = seqs[i].size();
      if (len > result) {
        result = len;
      }
    }
    return result;
  }


  @SafeVarargs
  protected static  List<String> mapall(Multiop op, List<String>... seqs) {
    int maxlen = maxLength(seqs);
    List<String> result = new ArrayList<String>(maxlen);
    String[] args = new String[seqs.length];
    for (int i = 0; i < maxlen; i++) {
      for (int j = 0; j < seqs.length; j++) {
        args[j] = getElement(seqs[j], i);
      }
      result.add(i, (String)op.perform(args));
    }
    return result;
  }
    
  @SafeVarargs
  protected static <T> List<T> list(T ... objs) {
    final List<T> lst = new ArrayList<T>(objs.length);
    for (int i = 0; i < objs.length; i++) {
      lst.add(objs[i]);
    }
    return lst;
  }
 
  @Override
  protected Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  protected static String objToString(Object val) {
    return null == val ? "NIL" : val.toString();
  }

  protected static List<String>  chkSemVerVUnions(List<Object> prerelease)
    throws IllegalArgumentException {
    List<String> result = list();
    if (null != prerelease) {
      for (Object val : prerelease) {
        result.add(objToString(val));
      }
    } 
    return result;
  }

  protected boolean isSemanticRelOrBuildPart(String val) {
    if (null == val) {
      return true;
    }
    final Matcher matcher = VU_PAT.matcher(val);
    return matcher.matches();
  }
  
  protected static String chkVerNumberPart(Long num,
                                           String name,
                                           boolean mandatory)
    throws IllegalArgumentException {
    if (null == num) {
      throw new IllegalArgumentException("Invalid null "
                                         + name 
                                         + " version");
    }
    return Long.toString(num);
  }

  protected boolean isSemanticVersionParts() {
    if (null == versions || versions.size() != 3) {
      return false;
    }
    for (String v : versions) {
      if (null == v || ! this.isSemanticRelOrBuildPart(v)) {
        return false;
      }
    }
    return true;
  }

  
  protected boolean areAllPartsSemantic(List<String> vs) {
    if (null != vs) {
      for (String v : vs) {
        if (null == v || !isSemanticRelOrBuildPart(v)) {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean equal(Object objA, Object objB) {
    if (null == objA) {
      return null == objB;
    }
    return objA.equals(objB);
  }

    
  private static void appendPart(StringBuilder buf, String sep, String val) {
    if (null != val) {
      if (buf.length() > 0) {
        buf.append(sep);
      }
    }
    buf.append(val);
  }

  private void appendPart(StringBuilder buf,
                          String sep1,
                          String sep2,
                          Collection<String> vals) {
    if (null != vals && !vals.isEmpty()) {
      if (buf.length() > 0) {
        buf.append(sep1);
      }
      int idx = 0;
      for (Object val : vals) {
        if (idx > 0) {
          buf.append(sep2);
        }
        buf.append(val);
        idx++;
      }
    }

  }

    
  private String asString() {
    StringBuilder buf = new StringBuilder();
    appendPart(buf, "", this.prefix);
    appendPart(buf, "", ".", this.versions);
    appendPart(buf, "-", ".", this.prereleaseIds);
    appendPart(buf, "+", ".", this.buildIds);
    return buf.toString();
  }
    
  private static long atol(String str) {
    try {
      return Long.parseLong(str);
    } catch (Exception ex) {
      throw new RuntimeException("Cannot convert to Long: '" + str + "'");
    }
  }

  static int compareVerComp(String objA, String objB) {
    if (null == objA && null == objB) {
      return 0;
    } else if (null == objA) {
      return -1;
    } else if (null == objB) {
      return 1;
    } else if (isANum(objA) && isANum(objB)) {
      final long al = atol(objA);
      final long bl = atol(objB);
      return al > bl ? 1 : ((al < bl) ? -1 : 0);
    } else {
      return objA.compareTo(objB);
    }
        
  }

  private static boolean isANum(String str) {
    try {
      Long.parseLong(str);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }
}
