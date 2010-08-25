/**
 * Author: Alexander Villalobos Yadr�
 * E-Mail: avyadro@yahoo.com.mx
 * Created on May 19, 2008, 10:27:26 AM
 * Place: Quer�taro, Quer�taro, M�xico.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: TypeCast.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0        May 19, 2008           Alexander Villalobos Yadr�           1. New class.
 **/
package com.codicentro.utils;

//import com.codicentro.model.Column;
import com.codicentro.model.Table;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;

public class TypeCast {

    /**
     * 
     * @param s
     * @return
     */
    public static boolean isNullOrEmpy(String s) {
        return ((s == null) || (s.trim().equals("")));
    }

    /**
     *
     * @param s
     * @param r
     * @param size
     * @return
     */
    public static String CompleteString(String s, String r, int size, Types.AlignmentType at) {
        int l = ((size < s.length()) ? s.length() : size) - s.length();
        for (int i = 0; i < l; i++) {
            switch (at) {
                case LEFT:
                    s = r.concat(s);
                    break;
                case RIGHT:
                    s = s.concat(r);
                    break;
            }
        }
        return s;
    }

    public static String cuotes(String s) {
        if (s == null) {
            return null;
        } else {
            return s.replaceAll("\"", "\\\\\"");
        }
    }

    public static String CompleteString(int ascii, String r, int size, Types.AlignmentType at) {
        return CompleteString(toString(ascii), r, size, at);
    }

    public static boolean ObjectToBoolean(Object obj) throws CDCException {
        String s = toString(obj);
        if (s != null) {
            s = s.trim().toUpperCase();
        } else {
            s = "N";
        }
        return (!s.equals("N")
                && !s.equals("NO")
                && !s.equals("FALSE")
                && !((ifNumber(s)) && (toInt(s) == 0))
                && !s.equals("OFF"));
    }

    public static boolean ifNumber(Object o) throws CDCException {
        try {
            Double.parseDouble(toString(o));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static Integer toInteger(int pInt) {
        return new Integer(pInt);
    }

    public static int toInt(Object obj) throws CDCException {
        return toInt(toString(obj));
    }

    public static String OnlyWords(String s) throws CDCException {
        String result = "";
        try {
            result = s.replaceAll("[^a-z]||[^A-Z]", "");
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    public static Integer OnlyNumber(String s) throws CDCException {
        Integer result = new Integer(0);
        try {
            result = Integer.valueOf(s.replaceAll("[^0-9]", ""));
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    public static int toInt(String str) throws CDCException {
        int r = 0;
        try {
            r = (((str == null) || (str.trim().equals(""))) ? 0 : Integer.parseInt(str.trim()));
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return r;
    }

    public static Integer toInteger(String str) throws CDCException {
        Integer result = new Integer(0);
        try {
            if ((str != null) && (!str.trim().equals(""))) {
                result = Integer.valueOf(OnlyNumber(str));
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    public static BigInteger toBigInteger(Object o) {
        try {
            return new BigInteger(toString(o));
        } catch (Exception e) {
            return null;
        }
    }

    public static Long toLong(Object o) {
        try {
            return toBigDecimal(o).longValue();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Converts Object Type to String Type
     * @param o
     * @return
     */
    public static String toString(Object o) throws CDCException {
        String result = null;
        try {
            result = String.valueOf(o);
            result = ((o == null) || result.toLowerCase().equals("null")) ? null : result;
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    /**
     * Converts Object Type to String Type and Replace value r if value is null
     * @param o
     * @return
     */
    public static String toString(Object o, String r) throws CDCException {
        String result = toString(o);
        try {
            result = (result == null) ? r : result;
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }
    /*
    public static Object NVL(Object o) {
    return ((o == null) ? "NULL" : o);
    }
     */

    public static Object NVL(Object o, Object r) {
        return (((o == null) || (o.equals("") || (o.equals("NULL")))) ? r : o);
    }

    public static String NVL(String o, String r) {
        return (((o == null) || (o.equals("") || (o.equals("NULL")))) ? r : o);
    }

    public static String NVL(String o, String r, boolean trim) {
        o = (o == null) ? "" : o;
        r = (r == null) ? "" : o;
        o = (trim) ? o.trim() : o;
        r = (trim) ? r.trim() : r;
        return (((o.equals("") || (o.equals("NULL")))) ? r : o);
    }

    public static Integer toInteger(Object o) throws CDCException {
        try {
            return Integer.valueOf(toInt(o));
        } catch (Exception ex) {
            return null;
        }
    }

    public static BigDecimal toNumber(String s) throws CDCException {
        BigDecimal result = null;
        try {
            result = new BigDecimal(NVL(s, "0"));
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    public static BigDecimal toNumber(Object o) throws CDCException {
        BigDecimal result = null;
        try {
            result = toNumber(toString(o));
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    /**
     * 
     * @param o
     * @return
     * @throws CDCException
     */
    public static Short toShort(Object o) throws CDCException {
        try {
            return toBigDecimal(o).shortValue();
        } catch (Exception ex) {
            return null;
        }
    }

    public static Float toFloat(Object o) throws CDCException {
        Float result = null;
        try {
            BigDecimal bd = toNumber(o);
            if (bd != null) {
                result = Float.valueOf(bd.floatValue());
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    public static String toString(int i) {
        return String.valueOf(i);
    }

    public static String EMPTY(String o, String r) {
        return (((o == null) || (o.equals(""))) ? r : o);
    }

    public static BigDecimal toBigDecimal(Object o) {
        try {
            return new BigDecimal(toString(o));
        } catch (Exception e) {
            return null;
        }
    }
    /*
     *
     */

    public static java.sql.Date toDate(Object o, String f) throws CDCException {
        java.sql.Date sqlDate = null;
        try {
            java.util.Date utilDate = null;
            SimpleDateFormat df = new SimpleDateFormat(f.trim());
            df.setLenient(false); // Force read format date into param f
            utilDate = df.parse(toString(o));
            sqlDate = new java.sql.Date(utilDate.getTime());
        } catch (ParseException ex) {
            throw new CDCException(ex);
        }
        return sqlDate;
    }
    /*
    public static java.sql.Date ObjectToDate(Object o) throws CDCException {
    return new java.util.Date();
    // return (((o == null) || (NVL(ObjectToString(o), "", true).equals("")) || (NVL(ObjectToString(o), "").equals("-1"))) ? null : java.sql.Date.valueOf(ObjectToString(o)));
    }
     */

    public static String StringtoBlanc(String o) {
        return (((o == null) || (o.equals("-1"))) ? "" : o);
    }

    public static BigDecimal ObjectToBigDecimalOrNull(Object o) throws CDCException {
        if ((o == null) || (o.equals(""))) {
            return null;
        }
        return new BigDecimal(toString(o));
    }
    /*
    public static java.sql.Date stringToDate(String fecha) throws ParseException {
    return stringToDate(fecha, "yyyyMMdd");
    }

    public static java.sql.Date stringToDate(String fecha, String formato) throws ParseException {
    java.util.Date utilDate = null;
    java.sql.Date sqlDate = null;

    SimpleDateFormat df = new SimpleDateFormat(formato.trim());
    df.setLenient(false);

    utilDate = df.parse(fecha);
    sqlDate = new java.sql.Date(utilDate.getTime());

    return sqlDate;
    }
     */

    public static Calendar dateToCalendar(int date) {
        int day = date % 100;
        int month = date / 100 % 100 - 1;
        int year = date / 10000;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal;
    }

    public static int calendarToDate(Calendar cal) {
        int day = cal.get(5);
        int month = cal.get(2) + 1;
        int year = cal.get(1);
        return (year * 10000 + month * 100 + day);
    }

    public static Object[] toArray(String s) {
        ArrayList al = new ArrayList();
        StringTokenizer st = new StringTokenizer(s, ",");
        ArrayList o = new ArrayList();
        while (st.hasMoreTokens()) {
            o.add(st.nextToken());
        }
        al.add(o.toArray());
        return al.toArray();
    }

    /**
     *
     * @param s Data
     * @param r - the delimiters of row.
     * @param e - the delimiters of element.
     * @param o - the delimiters of key and value.
     * @return
     * @throws CDCException
     */
    public static Object[] toArray(String s, String r, String e, String o) throws CDCException {
        StringTokenizer idx = new StringTokenizer(s, r);
        ArrayList rs = (idx.hasMoreTokens()) ? new ArrayList() : null;
        StringTokenizer cm = null;
        StringTokenizer tp = null;
        HashMap hm = null;
        String k = null;// Key
        String v = null;// Value
        while (idx.hasMoreTokens()) {
            hm = new HashMap();
            cm = new StringTokenizer(idx.nextToken(), e);
            while (cm.hasMoreTokens()) {
                tp = new StringTokenizer(cm.nextToken(), o);
                if (tp.hasMoreTokens()) {
                    k = tp.nextToken();
                    v = (tp.hasMoreTokens()) ? tp.nextToken() : "";
                } else {
                    throw new CDCException("lng.msg.error.malformedarray('" + s + "')");
                }
                hm.put(k, v);
            }
            rs.add(hm);
        }
        return rs.toArray();
    }

    public static ArrayList tableToJSON(Table table) throws com.codicentro.utils.CDCException {
        ArrayList nt = new ArrayList();
        try {
            if (table != null) {
                String ct = "";
                String key = "";
                for (int i = 0; i < table.getValues().size(); ++i) {
                    String str = "";
                    key = "";

                    for (int k = 0; k < table.getColumns().size(); ++k) {
                        ct = "";
                        if (!(((com.codicentro.model.Column) table.getColumns().get(k)).getTypeName().equals("int4"))) {
                            ct = "'";
                        }
                        key = toString(((com.codicentro.model.Column) table.getColumns().get(k)).getName());
                        String o = toString(table.getValue(i).get(key)).trim();
                        key = "\"" + key + "\":";
                        str = str + ((str.equals("")) ? key + ct + o + ct : new StringBuilder().append(",").append(key).append(ct).append(o).append(ct).toString());
                    }

                    nt.add("{" + str + "}");
                }
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return nt;
    }

    /**
     *
     * @param c
     * @return
     */
    public static String toString(char[] c) {
        String result = "";
        for (int i = 0; i < c.length; i++) {
            result += c[i];
        }
        return result;
    }

    /**
     * 
     * @param s
     * @param separator
     * @return
     */
    public static String toString(String[] s, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length; i++) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(s[i]);
        }
        return sb.toString();
    }

    public static String toString(ArrayList a) {
        String result = "";
        for (int i = 0; i < a.size(); i++) {
            result += a.get(i);
        }
        return result;
    }

    public static String toString(byte[] b) {
        //StringList sb = new StringList();
        String result = "";
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                result += "0";
            }
            result += Integer.toHexString(v);
        }
        return result;
    }

    public static String toFirtLowerCase(String s) {
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    public static String toFirtUpperCase(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static String toOctal(String v) {
        v = v.replaceAll("/\r\n/g", "\n");
        String rs = "";
        char c;
        for (int n = 0; n < v.length(); n++) {
            c = v.charAt(n);
            if (c < 128) {
                rs += String.valueOf(c);
            } else {
                // String.valueOf(c).
                //      rs += "\\" + String.valueOf(c).;
            }
        }

        return null;
    }

   
    /**
     * Get Object by name, used reflections for find method
     * @param o
     * @param n
     * @return
     */
    public static Object GN(Object o, String n) {
        Method m = getMethod(o.getClass(), n, null);
        if (m != null) {
            return invoke(m, o, null);
        } else {
            return null;
        }
    }

    /**
     * 
     * @param c, Class name
     * @param n, Public method name
     * @param p, 
     * @return
     */
    public static Method getMethod(Class c, String n, Class p) {
        try {
            if (p == null) {
                return c.getMethod(n);
            } else {
                return c.getMethod(n, p);
            }
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 
     * @param m
     * @param o
     * @param p
     * @return
     */
    public static Object invoke(Method m, Object o, Object p) {
        try {
            if (p == null) {
                return m.invoke(o);
            } else {
                return m.invoke(o, p);
            }
        } catch (Exception ex) {
            return null;
        }
    }
}
