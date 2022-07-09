package org.dudinea.version;


import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.reflect.Array;


public class Version extends Number {
    final int IDX_MAJOR = 0;
    final int IDX_MINOR = 1;
    final int IDX_PATCH = 2;

    final double KMINOR = 0.001;
    final double KPATCH = 0.000001;
    
    protected String prefix = "";
    
    protected List<String> versions = list();
    protected List<String> prereleaseIds = list();
    protected List<String> buildIds = list();

    protected String src;

    final static protected Pattern VU_PAT =
        Pattern.compile("^[0-9a-z-]+$", Pattern.CASE_INSENSITIVE);
    final static protected Pattern VU_NUMPAT =
        Pattern.compile("^[0-9]+$", Pattern.CASE_INSENSITIVE);

    
    protected static final String VERSEP=".-+";
    protected static final String RELSEP=".+";
    protected static final String BUILDSEP=".";

    public static Version parseVersion(String str)
        throws IllegalArgumentException {
    
        final Version v = new Version();
        if (null == str) {
            return null;
        }
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

    public static Version fromNumber(Number n) {
        if (n == null) {
            return null;
        }
        if (n instanceof Version) {
            return (Version) n;
        }
        if ((n instanceof Double) || (n instanceof Float)) {
            return fromDouble(n.doubleValue());
        }
        return fromLong(n.longValue());
    }

    public static Version fromDouble(double d) {
        long major = (long) d;
        long minor = 0L;
        long patch = 0L;
        try {
            return mkSemVersion(major, minor, patch, null, null);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Version fromLong(long l) {
        try {
            return mkSemVersion(l, 0L, 0L, null, null);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        }
    }


    public boolean isSemanticRelOrBuildPart(String v) {
        if (null == v) {
            return true;
        }
        Matcher m = VU_PAT.matcher(v);
        return m.matches();
    }
    

    
    protected static String chkVerNumberPart(Long num,
                                           String name,
                                           boolean mandatory)
        throws IllegalArgumentException{
        if (null != num) {
            /*if (num < 0) {
                throw new IllegalArgumentException("Invalid negative "+
                                                     name +
                                                     " version: " + num);
                                                     }*/
        } else {
            throw new IllegalArgumentException("Invalid null "+
                                                 name +
                                                 " version");
        }
        return Long.toString(num);
    }

    protected static List<String>  chkSemVerVUnions(List<Object> prerelease)
        throws IllegalArgumentException{
        List<String> result = list();
        if (null != prerelease) {
            for (Object val : prerelease) {
                result.add(objToString(val));
            }
        } 
        return result;
    }

    /* prerelease and build - are lists of
       object - each one can be a number or a string which a valid
       prerelease/build component according to the spec */
    public static Version mkSemVersion(Long major,
                                       Long minor,
                                       Long patch,
                                       List<Object> prerelease,
                                       List<Object> build)
        throws IllegalArgumentException
    {
        final Version v = new Version();
        v.setVersions(list(chkVerNumberPart(major, "major", true),
                           chkVerNumberPart(minor, "minor", true),
                           chkVerNumberPart(patch, "patch", true)));
        //v.hasMajor = v.hasMinor = v.hasPatch = true;
        v.prereleaseIds = chkSemVerVUnions(prerelease);
        v.buildIds = chkSemVerVUnions(build);
        return v;
    }

    public boolean isDevelopment() {
        return
            isSemantic() &&
            getMajorNum() == 0;
    }
    
    public boolean hasMinor() {
        return IDX_MINOR < versions.size();
    }
    public boolean hasMajor() {
        return IDX_MAJOR < versions.size();
    }
    public boolean hasPatch() {
        return IDX_PATCH < versions.size();
    }

    public long getVersionPartNum(int idx) {
        final String vu = (String)getElement(versions, idx);
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
    
    public long getMajorNum() {
        return getVersionPartNum(IDX_MAJOR);
    }
    public long getMinorNum() {
        return getVersionPartNum(IDX_MINOR);
    }

    public long getPatchNum() {
        return getVersionPartNum(IDX_PATCH);
    }

    public List<String> getPrereleaseIds() {
        return this.prereleaseIds;
    }

    public List<String> getVersions() {
        return this.versions;
    }

    public void  setVersions(List<String> versions) {
        this.versions = versions;
    }
    
    public List<String> getBuildIds() {
        return this.buildIds;
    }


    
    protected boolean isSemanticVersionParts() {
        if (null == versions ||
                versions.size() != 3) {
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
    
    public boolean isSemantic() {
        return isSemanticVersionParts() &&
            areAllPartsSemantic(this.prereleaseIds) &&
            areAllPartsSemantic(this.buildIds);
    }

    
    @Override
    public byte byteValue() {
        return (byte) this.getMajorNum();
    }


    @Override
    public double doubleValue() {
        return this.getMajorNum() +
            this.getMinorNum() * KMINOR +
            this.getPatchNum() * KPATCH;
    }

    @Override
    public float floatValue() {
        return (float)(this.getMajorNum() +
                       this.getMinorNum() * KMINOR);
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
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Version)) {
            return false;
        }
        Version ov = (Version) o;
        return equal(this.getVersions(), ov.getVersions()) &&
            equal(this.getPrereleaseIds(), ov.getPrereleaseIds()) &&
            equal(this.getBuildIds(), ov.getBuildIds());
        
    }

    public static boolean equal(Object a, Object b) {
        if (null == a) {
            return null == b;
        }
        return a.equals(b);
    }

    
    protected void appendPart(StringBuilder b, String sep, Number val) {
        if (null != val) {
            if (b.length() > 0) {
                b.append(sep);
            }
            b.append(val);
        }
    }

    protected void appendPart(StringBuilder b, String sep, String val) {
        if (null != val) {
            if (b.length() > 0) {
                b.append(sep);
            }
        }
        b.append(val);
    }

    protected void appendPart(StringBuilder b,
                              String sep1,
                              String sep2,
                              Collection<String> vals) {
        if (null != vals && !vals.isEmpty()) {
            if (b.length() > 0) {
                b.append(sep1);
            }
            int i = 0;
            for (Object val : vals) {
                if (i > 0) {
                    b.append(sep2);
                }
                b.append(val);
                i++;
            }
        }

    }

    
    protected String asString() {
        StringBuilder b = new StringBuilder();
        appendPart(b, "", this.prefix);
        appendPart(b, "", ".", this.versions);
        appendPart(b, "-", ".", this.prereleaseIds);
        appendPart(b, "+", ".", this.buildIds);
        
        return b.toString();
    }
    
    
    @Override
    public String toString() {
        return null != src ? src : asString();
    }


    protected static long atol(String a) {
        try {
            return Long.parseLong(a);
        } catch (Exception ex) {
            throw new RuntimeException("Cannot convert to Long: '" + a + "'");
        }
    }
    
    protected static boolean isANum(String v) {
        try {
            Long.parseLong(v);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    public Version sub(Version o) {
        final Multiop addop = new Multiop() {
                @Override
                public Object perform(Object... objs) {
                    String a = (String)objs[0];
                    String b = (String)objs[1];
                    if (null == a) {
                        return null;
                    } else {
                        if (isANum(a) && isANum(b)) {
                            return Long.toString(atol(a) - atol(b));
                        } else if (null == b) {
                            return a;
                        } else {
                            if (a.endsWith(b)) {
                                return a.substring(0, a.length() - b.length());
                            } else {
                                return a;
                            }

                        }

                    }
                }
            };
        return doOp(o, addop);
    }

    public static int compareVerComp(String a, String b) {
        if (null == a && null == b) {
            return 0;
        } else if (null == a) {
            return -1;
        } else if (null == b) {
            return 1;
        } else if (isANum(a) && isANum(b)) {
            final long al = atol(a);
            final long bl = atol(b);
            return al > bl ? 1 : ((al < bl) ? -1 : 0);
        } else {
            return a.compareTo(b);
        }
        
    }

    public int compareTo(Version o) {
        int result = 0;
        for (int i = 0; i < versions.size(); i++) {
            final String tel = (String)getElement(this.versions, i);
            final String oel = (String)getElement(o.versions, i);
            result = compareVerComp(tel, oel);
            if (result != 0) {
                return result;
            }
        }
        result = versions.size() - o.versions.size();
        if (result != 0) {
            return result;
        }
        // this = 1.0.0 > 1.0.0.pre
        if (this.prereleaseIds.size() == 0) {
            return o.prereleaseIds.size();
        }
        // this = 1.0.0.pre < 1.0.0
        if (o.prereleaseIds.size() == 0) {
            return - this.prereleaseIds.size();
        }
        for (int i = 0; i < this.prereleaseIds.size(); i++) {
            final String tel = (String)getElement(this.prereleaseIds, i);
            final String oel = (String)getElement(o.prereleaseIds, i);
            result = compareVerComp(tel, oel);
            if (result != 0) {
                return result;
            }
        }
        result = prereleaseIds.size() - o.prereleaseIds.size();
        return result;
    }

    public Version add(Number o) {
        if (null == o) {
            return this;
        } else if (o instanceof Version) {
            return add((Version) o);
        } else {
            return add(fromDouble(o.doubleValue()));
        }
    }

    public Version sub(Number o) {
        if (null == o) {
            return this;
        } else if (o instanceof Version) {
            return sub((Version) o);
        } else {
            return sub(fromDouble(o.doubleValue()));
        }
    }

    
    public Version add(Version o) {
        final Multiop addop = new Multiop() {
                @Override
                public Object perform(Object... objs) {
                    String a = (String)objs[0];
                    String b = (String)objs[1];
                    if (null == a) {
                        return b;
                    } else {
                        if (isANum(a) && isANum(b)) {
                            return Long.toString(atol(a) + atol(b));
                        } else if (null == b) {
                            return a;
                        } else {
                            return a + b;
                        }
                    }
                }
            };
        return doOp(o, addop);
    }

    
    public Version doOp(Version o, Multiop op) {
        if (null == o) {
            return this;
        }
        Version result = new Version();
        result.versions = mapall(op, this.versions, o.versions);
        result.prereleaseIds = mapall(op, this.prereleaseIds, o.prereleaseIds);
        result.buildIds = mapall(op, this.buildIds, o.buildIds);
        return result;
    }

    public static int getLength(Object val,
                                boolean allowNonSeq) {
        if (null == val) {
            if (allowNonSeq) {
                return 0;
            } else {
                throw new RuntimeException("NIL not a sequence: ");
            }
        } else if (val instanceof Collection) {
            return ((Collection)val).size();
        } else if (val instanceof CharSequence) {
            return ((CharSequence)val).length();
        } else if (val.getClass().isArray()) {
            return Array.getLength(val);
        } else if (val instanceof Map) {
            return ((Map)val).size();
        } else {
            if (allowNonSeq) {
                return 1;
            } else {
                throw new RuntimeException("Given object type is not a sequence: "+val.getClass());
            }
        }
    }
    

    public static interface Multiop{
        public Object perform(Object... objs);
    }


    public static int maxLength(Object... seqs) {
        int result = 0;
        for (int i = 0; i < seqs.length; i++) {
            int len = getLength(seqs[i], false);
            if (len > result) {
                result = len;
            }
        }
        return result;
    }


    
    public static <T> List<T> mapall(Multiop op, Object... seqs) {
        int maxlen = maxLength(seqs);
        List<T> result = new ArrayList<T>(maxlen);
        Object[] args = new Object[seqs.length];
        for (int i = 0; i < maxlen; i++) {
            for (int j = 0; j < seqs.length; j++) {
                args[j] = getElement(seqs[j], i);
            }
            result.add(i, (T)op.perform(args));
        }
        return result;
    }
    



    public static <T>List<T> list(T ... objs) {
        List <T>lst = new ArrayList<T>(objs.length);
        lst.addAll(Arrays.asList(objs));
        return lst;
    }

    public static Object getElement(Object seq, int index) {
	    if (null == seq) {
            return null;
	    } else if (seq instanceof List) {
            try {
                final List<Object> list = (List<Object>)seq;
                return list.get(index);
            } catch (IndexOutOfBoundsException bex) {
                return null;
            }
	    } else if (seq.getClass().isArray()) {
            try {
                return Array.get(seq, index);
            } catch (ArrayIndexOutOfBoundsException bex) {
                return null;
            }
	    } else if (seq instanceof CharSequence) {
            try {
                final CharSequence cs = (CharSequence)seq;
                return cs.charAt(index);
            } catch (IndexOutOfBoundsException bex) {
                return null;
            }
	    } else {
            throw new RuntimeException("Unupported sequence type "+
                                       seq.getClass().getName());
	    }
    }

    protected static String objToString(Object val) {
        return null == val ? "NIL" :  val.toString();
    }

    protected static boolean isRoundNumber(Number val) {
        if (null == val) {
            return false;
        }
        final double d = val.doubleValue();
        return d % 1 == 0;
    }

}
