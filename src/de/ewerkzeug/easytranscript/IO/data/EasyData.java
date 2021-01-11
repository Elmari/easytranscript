/*
 * Copyright (C) 2014 e-werkzeug <administrator@e-werkzeug.eu>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.ewerkzeug.easytranscript.IO.data;

import static de.ewerkzeug.easytranscript.core.Variables.logger;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import static java.util.Collections.synchronizedMap;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.map.LinkedMap;

/**
 * Klasse, um Daten für easytranscript effizient zu behandeln, sowie zu
 * speichern und zu lesen. Orientiert sich am ini-Design (Section, Key, Value)
 * Benutzt die Commons Collections von Apache, um die Daten zu handlen und
 * erweitert die Speichern und Lesen Methoden von java.util.Properties.
 * Design-technisch handelt es sich bei EasyData um eine OrderedMap, wobei der
 * Key die Section ist und der Value eine weitere OrderedMap, in welche die Keys
 * und Values gespeichert werden.
 *
 * @see org.apache.commons.collections4.OrderedMap
 * @see java.util.Properties
 * @since easytranscript 2.20
 * @author e-werkzeug <administrator@e-werkzeug.eu>
 */
public class EasyData {

    Map catmap = synchronizedMap(new LinkedMap());

    /**
     * Erstellt die Standard Kategorie "default"
     */
    public EasyData() {
        catmap.put("default", new LinkedMap());
    }

    /**
     * Löscht alle Kategorien und die dazugehörige Map von Keys und Values.
     */
    public void clear() {
        catmap = new LinkedMap();
        catmap.put("default", new LinkedMap());
    }

    public void remove(String key) {
        ((OrderedMap) catmap.get("default")).remove(key);
    }

    public void remove(String category, String key) {
        ((OrderedMap) catmap.get(category)).remove(key);
    }

    /**
     * Setzt eine Eigenschaft unter Angabe der Kategorie, des Schlüssels und des
     * Wertes. Ein Schlüssel kann in einem EasyData Objekt mehrmals vorkommen,
     * in einer Kategorie jedoch nur maximal 1 mal. Es gibt keine Dopplungen von
     * Kategorien.
     *
     * @param category die Kategorie
     * @param key Der Schlüssel
     * @param value Der Wert
     */
    public void setProperty(String category, String key, String value) {

        category = category.replace(" ", "");
        category = category.replace("=", "");

        OrderedMap map;

        if (!catmap.containsKey(category)) {
            catmap.put(category, new LinkedMap());
        }

        map = (OrderedMap) catmap.get(category);

        key = key.replace("=", "&equiv;");
        value = value.replace("=", "&equiv;");

        map.put(key, value);

    }

    /**
     * Setzt einen Eintrag mit key und value in die Kategorie "default".
     *
     * @see setProperty(String category, String key, String value)
     * @param key
     * @param value
     */
    public void setProperty(String key, String value) {

        OrderedMap map;

        if (!catmap.containsKey("default")) {
            catmap.put("default", new LinkedMap());
        }

        map = (OrderedMap) catmap.get("default");

        map.put(key, value);

    }

    /**
     * Überträgt alle Einträge von einem EasyData Objekt zum Anderen.
     *
     * @param easy das EasyData Objekt, von welchem die Daten kopiert werden
     * sollen.
     */
    public void putAll(EasyData easy) {
        catmap.putAll(easy.catmap);
    }

    /**
     * Überträgt alle Einträge von einer LinkedMap in eine Kategorie der anderen
     *
     * @param category die Kategorie
     * @param map die LinkedMap
     */
    public void putAll(String category, LinkedMap map) {

        if (!catmap.containsKey(category)) {
            throw new IllegalArgumentException("category does not exist");
        }
        ((LinkedMap) catmap.get(category)).putAll(map);

    }

    public Set<String> keySet() {
        return ((OrderedMap) catmap.get("default")).keySet();
    }

    public Set<String> keySet(String category) {
        return ((OrderedMap) catmap.get(category)).keySet();
    }

    /**
     * Gibt eine ArrayList von allen eingetragenen Kategorien zurück.
     *
     * @return ArrayList vom Typ String
     */
    public ArrayList<String> getAllCategories() {
        ArrayList l = new ArrayList<>();
        l.addAll(catmap.keySet());
        return l;
    }

    /**
     * Gibt alle Schlüssel, Wert Paare von einer bestimmten Kategorie zurück.
     *
     * @param category die gewünschte Kategorie
     * @return ArrayList vom Type String[]. Das Array hat genau 2 Einträge.
     */
    public ArrayList<String[]> getAllPairs(String category) {
        ArrayList<String[]> list = new ArrayList<String[]>();
        if (!catmap.containsKey(category)) {
            return null;
        }
        OrderedMap map = (OrderedMap) catmap.get(category);

        MapIterator it = map.mapIterator();
        String key, value;
        while (it.hasNext()) {
            key = (String) it.next();

            value = (String) it.getValue();
            list.add(new String[]{key, value});
        }
        return list;

    }

    /**
     * Liefert den Wert zu einem Key aus der Kategorie "default" zurück.
     *
     * @param key der Key
     * @return der Wert
     */
    public String getProperty(String key) {

        OrderedMap map;

        map = (OrderedMap) catmap.get("default");
        if (!map.containsKey(key)) {
            return null;
        }
        return (String) map.get(key);

    }

    /**
     * Liefert den Wert zu einem Key aus der angegebenen Kategorie zurück.
     *
     * @param category Die Kategorie
     * @param key der Schlüssel
     * @return der Wert
     */
    public String getProperty(String category, String key) {
        logger.log(Level.WARNING, "Searching in default category");

        OrderedMap map;

        map = (OrderedMap) catmap.get(category);
        if (!map.containsKey(key)) {
            return null;
        }
        return (String) map.get(key);

    }

    /**
     * Übernommen von Properties
     *
     * @see java.util.Properties
     * @param writer
     * @param comments
     * @throws IOException
     */
    public void store(Writer writer, String comments)
            throws IOException {
        store0((writer instanceof BufferedWriter) ? (BufferedWriter) writer
                : new BufferedWriter(writer),
                comments,
                false);
    }

    /**
     *
     * Übernommen von Properties
     *
     * @see java.util.Properties
     * @param out
     * @param comments
     * @throws IOException
     */
    public void store(OutputStream out, String comments)
            throws IOException {
        store0(new BufferedWriter(new OutputStreamWriter(out, "8859_1")),
                comments,
                true);
    }

    /**
     *
     * Übernommen von Properties. Um Kategorien erweitert.
     *
     * @see java.util.Properties
     * @param bw
     * @param comments
     * @param escUnicode
     * @throws IOException
     */
    private void store0(BufferedWriter bw, String comments, boolean escUnicode)
            throws IOException {
        if (comments != null) {
            writeComments(bw, comments);
        }
        bw.write("#" + new Date().toString());
        bw.newLine();
        synchronized (this) {
            Object[] catarray = catmap.keySet().toArray();
            for (Object catarray1 : catarray) {
                if (!catarray1.equals("default")) {
                    bw.write("[" + catarray1 + "]");
                    bw.newLine();
                }
                OrderedMap map = (OrderedMap) catmap.get(catarray1);
                Object[] maparray = map.keySet().toArray();
                for (Object maparray1 : maparray) {
                    String key = (String) maparray1;
                    String val = (String) map.get(maparray1);
                    key = saveConvert(key, true, escUnicode);
                    /* No need to escape embedded and trailing spaces for value, hence
                     * pass false to flag.
                     */
                    val = saveConvert(val, false, escUnicode);
                    bw.write(key + "=" + val);
                    bw.newLine();
                }
            }
        }
        bw.flush();
    }

    /**
     *
     * Übernommen von Properties
     *
     * @see java.util.Properties
     * @param theString
     * @param escapeSpace
     * @param escapeUnicode
     * @return
     */
    private String saveConvert(String theString,
            boolean escapeSpace,
            boolean escapeUnicode) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuilder outBuffer = new StringBuilder(bufLen);

        for (int x = 0; x < len; x++) {
            char aChar = theString.charAt(x);
            // Handle common case first, selecting largest block that
            // avoids the specials below
            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    outBuffer.append('\\');
                    outBuffer.append('\\');
                    continue;
                }
                outBuffer.append(aChar);
                continue;
            }
            switch (aChar) {
                case ' ':
                    if (x == 0 || escapeSpace) {
                        outBuffer.append('\\');
                    }
                    outBuffer.append(' ');
                    break;
                case '\t':
                    outBuffer.append('\\');
                    outBuffer.append('t');
                    break;
                case '\n':
                    outBuffer.append('\\');
                    outBuffer.append('n');
                    break;
                case '\r':
                    outBuffer.append('\\');
                    outBuffer.append('r');
                    break;
                case '\f':
                    outBuffer.append('\\');
                    outBuffer.append('f');
                    break;
                case '=': // Fall through
                case ':': // Fall through
                case '#': // Fall through
                case '!':
                    outBuffer.append('\\');
                    outBuffer.append(aChar);
                    break;
                default:
                    if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode) {
                        outBuffer.append('\\');
                        outBuffer.append('u');
                        outBuffer.append(toHex((aChar >> 12) & 0xF));
                        outBuffer.append(toHex((aChar >> 8) & 0xF));
                        outBuffer.append(toHex((aChar >> 4) & 0xF));
                        outBuffer.append(toHex(aChar & 0xF));
                    } else {
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }

    /**
     *
     * Übernommen von Properties
     *
     * @see java.util.Properties
     * @param bw
     * @param comments
     * @throws IOException
     */
    private static void writeComments(BufferedWriter bw, String comments)
            throws IOException {
        bw.write("#");
        int len = comments.length();
        int current = 0;
        int last = 0;
        char[] uu = new char[6];
        uu[0] = '\\';
        uu[1] = 'u';
        while (current < len) {
            char c = comments.charAt(current);
            if (c > '\u00ff' || c == '\n' || c == '\r') {
                if (last != current) {
                    bw.write(comments.substring(last, current));
                }
                if (c > '\u00ff') {
                    uu[2] = toHex((c >> 12) & 0xf);
                    uu[3] = toHex((c >> 8) & 0xf);
                    uu[4] = toHex((c >> 4) & 0xf);
                    uu[5] = toHex(c & 0xf);
                    bw.write(new String(uu));
                } else {
                    bw.newLine();
                    if (c == '\r'
                            && current != len - 1
                            && comments.charAt(current + 1) == '\n') {
                        current++;
                    }
                    if (current == len - 1
                            || (comments.charAt(current + 1) != '#'
                            && comments.charAt(current + 1) != '!')) {
                        bw.write("#");
                    }
                }
                last = current + 1;
            }
            current++;
        }
        if (last != current) {
            bw.write(comments.substring(last, current));
        }
        bw.newLine();
    }

    /**
     *
     * Übernommen von Properties
     *
     * @see java.util.Properties
     * @param inStream
     * @throws IOException
     */
    public synchronized void load(InputStream inStream) throws IOException {
        load0(new LineReader(inStream));
    }

    /**
     *
     * Übernommen von Properties. Um Kategorien Lesen erweitert
     *
     * @see java.util.Properties
     * @param lr
     * @throws IOException
     */
    private void load0(LineReader lr) throws IOException {
        char[] convtBuf = new char[1024];
        int limit;
        int keyLen;
        int valueStart;
        char c;
        boolean hasSep;
        boolean precedingBackslash;
        String section = "default";

        while ((limit = lr.readLine()) >= 0) {
            c = 0;
            keyLen = 0;
            valueStart = limit;
            hasSep = false;

            boolean isSection = false;

            String precheck = loadConvert(lr.lineBuf, 0, limit, convtBuf);
            if (!precheck.contains("=") && precheck.startsWith("[") && precheck.endsWith("]")) {
                isSection = true;
                precheck = precheck.replace("[", "");
                precheck = precheck.replace("]", "");
                section = precheck;
            }

            precedingBackslash = false;
            if (true) {
                while (keyLen < limit) {
                    c = lr.lineBuf[keyLen];

                    //need check if escaped.
                    if ((c == '=' || c == ':') && !precedingBackslash) {
                        valueStart = keyLen + 1;
                        hasSep = true;
                        break;
                    } else if ((c == ' ' || c == '\t' || c == '\f') && !precedingBackslash) {
                        valueStart = keyLen + 1;
                        break;
                    }
                    if (c == '\\') {
                        precedingBackslash = !precedingBackslash;
                    } else {
                        precedingBackslash = false;
                    }
                    keyLen++;
                }
                while (valueStart < limit) {
                    c = lr.lineBuf[valueStart];
                    if (c != ' ' && c != '\t' && c != '\f') {
                        if (!hasSep && (c == '=' || c == ':')) {
                            hasSep = true;
                        } else {
                            break;
                        }
                    }
                    valueStart++;
                }
                if (!isSection) {
                    String key = loadConvert(lr.lineBuf, 0, keyLen, convtBuf);
                    String value = loadConvert(lr.lineBuf, valueStart, limit - valueStart, convtBuf);

                    setProperty(section, key, value);
                }
            }
        }
    }

    /* Read in a "logical line" from an InputStream/Reader, skip all comment
     * and blank lines and filter out those leading whitespace characters 
     * (\u0020, \u0009 and \u000c) from the beginning of a "natural line". 
     * Method returns the char length of the "logical line" and stores 
     * the line in "lineBuf". 
     */
    class LineReader {

        public LineReader(InputStream inStream) {
            this.inStream = inStream;
            inByteBuf = new byte[8192];
        }

        public LineReader(Reader reader) {
            this.reader = reader;
            inCharBuf = new char[8192];
        }

        byte[] inByteBuf;
        char[] inCharBuf;
        char[] lineBuf = new char[1024];
        int inLimit = 0;
        int inOff = 0;
        InputStream inStream;
        Reader reader;

        int readLine() throws IOException {
            int len = 0;
            char c;

            boolean skipWhiteSpace = true;
            boolean isCommentLine = false;
            boolean isNewLine = true;
            boolean appendedLineBegin = false;
            boolean precedingBackslash = false;
            boolean skipLF = false;

            while (true) {
                if (inOff >= inLimit) {
                    inLimit = (inStream == null) ? reader.read(inCharBuf)
                            : inStream.read(inByteBuf);
                    inOff = 0;
                    if (inLimit <= 0) {
                        if (len == 0 || isCommentLine) {
                            return -1;
                        }
                        return len;
                    }
                }
                if (inStream != null) {
                    //The line below is equivalent to calling a 
                    //ISO8859-1 decoder.
                    c = (char) (0xff & inByteBuf[inOff++]);
                } else {
                    c = inCharBuf[inOff++];
                }
                if (skipLF) {
                    skipLF = false;
                    if (c == '\n') {
                        continue;
                    }
                }
                if (skipWhiteSpace) {
                    if (c == ' ' || c == '\t' || c == '\f') {
                        continue;
                    }
                    if (!appendedLineBegin && (c == '\r' || c == '\n')) {
                        continue;
                    }
                    skipWhiteSpace = false;
                    appendedLineBegin = false;
                }
                if (isNewLine) {
                    isNewLine = false;
                    if (c == '#' || c == '!') {
                        isCommentLine = true;
                        continue;
                    }
                }

                if (c != '\n' && c != '\r') {
                    lineBuf[len++] = c;
                    if (len == lineBuf.length) {
                        int newLength = lineBuf.length * 2;
                        if (newLength < 0) {
                            newLength = Integer.MAX_VALUE;
                        }
                        char[] buf = new char[newLength];
                        System.arraycopy(lineBuf, 0, buf, 0, lineBuf.length);
                        lineBuf = buf;
                    }
                    //flip the preceding backslash flag
                    if (c == '\\') {
                        precedingBackslash = !precedingBackslash;
                    } else {
                        precedingBackslash = false;
                    }
                } else {
                    // reached EOL
                    if (isCommentLine || len == 0) {
                        isCommentLine = false;
                        isNewLine = true;
                        skipWhiteSpace = true;
                        len = 0;
                        continue;
                    }
                    if (inOff >= inLimit) {
                        inLimit = (inStream == null)
                                ? reader.read(inCharBuf)
                                : inStream.read(inByteBuf);
                        inOff = 0;
                        if (inLimit <= 0) {
                            return len;
                        }
                    }
                    if (precedingBackslash) {
                        len -= 1;
                        //skip the leading whitespace characters in following line
                        skipWhiteSpace = true;
                        appendedLineBegin = true;
                        precedingBackslash = false;
                        if (c == '\r') {
                            skipLF = true;
                        }
                    } else {
                        return len;
                    }
                }
            }
        }
    }

    /*
     * Converts encoded &#92;uxxxx to unicode chars
     * and changes special saved chars to their original forms
     */
    private String loadConvert(char[] in, int off, int len, char[] convtBuf) {
        if (convtBuf.length < len) {
            int newLen = len * 2;
            if (newLen < 0) {
                newLen = Integer.MAX_VALUE;
            }
            convtBuf = new char[newLen];
        }
        char aChar;
        char[] out = convtBuf;
        int outLen = 0;
        int end = off + len;

        while (off < end) {
            aChar = in[off++];
            if (aChar == '\\') {
                aChar = in[off++];
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = in[off++];
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed \\uxxxx encoding.");
                        }
                    }
                    out[outLen++] = (char) value;
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }
                    out[outLen++] = aChar;
                }
            } else {
                out[outLen++] = (char) aChar;
            }
        }
        return new String(out, 0, outLen);
    }

    /**
     *
     * Übernommen von Properties
     *
     * @see java.util.Properties
     * @param nibble
     * @return
     */
    private static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }

    /**
     * A table of hex digits
     */
    private static final char[] hexDigit = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

}
