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
package de.ewerkzeug.easytranscript.Core;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Versionsklasse.
 *
 * @author e-werkzeug <administrator@e-werkzeug.eu>
 */
public class Version implements Comparable<Version> {

    private int major, minor, fix;
    private String codeName = "";
    private String state = VersionState.FINAL;

    public Version(int major, int minor, int fix) {

        this.major = major;
        this.minor = minor;
        this.fix = fix;
    }

    public Version(int major, int minor, int fix, String codeName) {
        this.major = major;
        this.minor = minor;
        this.fix = fix;
        this.codeName = codeName;
    }

    public Version(int major, int minor, int fix, String codeName, String versionState) {
        this.major = major;
        this.minor = minor;
        this.fix = fix;
        this.codeName = codeName;
        this.state = versionState;

    }

    public Version(int code) {
        String codeStr = String.valueOf(code);
        this.major = Integer.valueOf(codeStr.substring(0, 1));
        this.minor = Integer.valueOf(codeStr.substring(1, 3));
        this.fix = Integer.valueOf(codeStr.substring(3, 4));
        this.state = VersionState.getString(Integer.valueOf(codeStr.substring(4, 5)));
    }

    public Version(String s) {
        String[] total = s.split(" ");

        String[] result = total[0].split("\\.");

        try {
            if (total.length > 0) {
                if (result.length > 0) {
                    if (result.length < 2) {

                        major = Integer.valueOf(result[0]);

                    } else if (result.length < 3) {
                        major = Integer.valueOf(result[0]);
                        minor = Integer.valueOf(result[1]);

                    } else if (result.length < 4) {
                        major = Integer.valueOf(result[0]);
                        minor = Integer.valueOf(result[1]);
                        fix = Integer.valueOf(result[2]);
                    }

                    if (total.length > 1) {

                        codeName = total[1];
                        if (total.length >= 3) {
                            String stateT = String.valueOf(total[2]);

                            if (stateT.equals(VersionState.ALPHA)) {
                                state = VersionState.ALPHA;
                            }

                            if (stateT.equals(VersionState.PREALPHA)) {
                                state = VersionState.PREALPHA;
                            }

                            if (stateT.equals(VersionState.BETA)) {
                                state = VersionState.BETA;
                            }

                            if (stateT.equals(VersionState.FINAL)) {
                                state = VersionState.FINAL;
                            }

                        }
                    }

                }
            }
        } catch (NumberFormatException e) {
            Logger.getLogger(Version.class.getName()).log(Level.SEVERE, "Fehler beim Konvertieren des Strings in eine Version", e);
        }
    }

    public int getMajorVersion() {
        return major;
    }

    public int getMinorVersion() {
        return minor;
    }

    public int getHotfixVersion() {
        return fix;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getVersionState() {
        return state;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    @Override
    public String toString() {
        return String.format(fix > 0 ? "%d.%02d.%d" : "%d.%02d", major, minor, fix) + " " + codeName + (state.equals(VersionState.FINAL) ? "" : " " + state);
    }

    public int asInteger() {
        return Integer.valueOf(String.format("%d%02d%d%d", major, minor, fix, VersionState.getValue(state)));
    }

    public boolean atLeast(Version v) {

        int i = compareTo(v);

        return i == 0 || i == 1;

    }

    @Override
    public int compareTo(Version v) {
        return Integer.compare(asInteger(), v.asInteger());

    }

}
