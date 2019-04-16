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

/**
 *
 * @author e-werkzeug <administrator@e-werkzeug.eu>
 */
public class VersionState {

    public static final String PREALPHA = "PreAlpha";
    public static final String ALPHA = "Alpha";
    public static final String BETA = "Beta";
    public static final String GAMMA = "Gamma";
    public static final String FINAL = "final";

    public static int getValue(String s) {
        int i;

        switch (s) {
            case PREALPHA:
                i = 1;
                break;
            case ALPHA:
                i = 2;
                break;
            case BETA:
                i = 3;
                break;
            case GAMMA:
                i = 4;
                break;
            case FINAL:
                i = 5;
                break;
            default:
                i = 5;
                break;
        }

        return i;
    }

    public static String getString(int i) {
        String s;

        if (i == 1) {
            s = PREALPHA;

        } else if (i == 2) {
            s = ALPHA;

        } else if (i == 3) {
            s = BETA;

        } else if (i == 4) {
            s = GAMMA;

        } else if (i == 5) {
            s = FINAL;

        } else {
            s = FINAL;
        }

        return s;
    }

}
