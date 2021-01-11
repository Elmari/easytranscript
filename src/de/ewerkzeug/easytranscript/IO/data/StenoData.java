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

import static de.ewerkzeug.easytranscript.core.Variables.configFrame;
import static de.ewerkzeug.easytranscript.core.Variables.logger;
import static de.ewerkzeug.easytranscript.core.Variables.opFolder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author e-werkzeug <administrator@e-werkzeug.eu>
 */
public class StenoData extends Properties {

    String path;

    /**
     * Lädt die Steno Daten
     */
    public void load() {
        try {
            load(new FileInputStream(opFolder + "conf/steno.data"));
            apply();
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Fehler beim Laden der Stenodatei. Womöglich noch nicht erstellt.");
        }
    }

    public void apply() {
        ((DefaultTableModel) configFrame.getConfigStenoTable().getModel()).setRowCount(0);

        for (Object k : keySet()) {
            ((DefaultTableModel) configFrame.getConfigStenoTable().getModel()).addRow(new Object[]{k, this.getProperty((String) k)});
        }

        ((DefaultTableModel) configFrame.getConfigStenoTable().getModel()).addRow(new Object[2]);

    }

    public void merge(Properties prop) {
        try {
            putAll(prop);
        } catch (Exception e) {

        }
    }

    public void replace(Properties prop) {
        clear();
        putAll(prop);

    }

    public boolean check(Properties prop) {

        for (Object key : prop.keySet()) {

            if (((String) key).contains(" ") || ((String) key).contains("\t")) {
                return false;
            }

        }

        return true;

    }

    public void setTable() {

        this.clear();

        for (int i = 0; i < configFrame.getConfigStenoTable().getModel().getRowCount(); i++) {
            String k = (String) configFrame.getConfigStenoTable().getModel().getValueAt(i, 0);
            String v = (String) configFrame.getConfigStenoTable().getModel().getValueAt(i, 1);

            if (k != null) {
                if (v != null) {
                    if (!k.equals("")) {
                        if (!v.equals("")) {
                            add(k, v);
                        }
                    }
                }
            }
        }
    }

    public boolean add(String key, String value) {

        if (containsKey(key)) {
            return false;
        } else {

            put(key, value);

            return true;
        }

    }

    /**
     * Speichert eine Stenodatei. Catched eine IOException falls Datei nicht existiert.
     */
    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream(opFolder + "conf/steno.data");
            store(fos, null);
            fos.close();

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Fehler beim Speichern der Steno-Datei", ex);

        }
    }

    /**
     * Speichert eine Stenodatei. Catched eine IOException falls Datei nicht existiert.
     *
     * @param path Pfad in den die Datei gespeichert werden soll.
     */
    public void save(String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            store(fos, null);
            fos.close();

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Fehler beim Speichern der Steno-Datei", ex);

        }
    }

}
