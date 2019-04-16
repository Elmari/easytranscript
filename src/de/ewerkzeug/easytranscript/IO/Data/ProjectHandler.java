/*
 * Copyright (C) 2014 e-werkzeug
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
package de.ewerkzeug.easytranscript.IO.Data;

import static de.ewerkzeug.easytranscript.Core.V.logger;
import static de.ewerkzeug.easytranscript.Core.V.messages;
import static de.ewerkzeug.easytranscript.Core.V.projectFolderFrame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Stellt Properties-Datei des Projektmappe dar.
 */
public class ProjectHandler extends EasyData {

    public static final String TYPE_PROJECT = "project";
    public static final String TYPE_FILE = "file";
    public String path = "";

    /**
     * Schließt die Mappen-Datei und setzt auf Default-Werte.
     */
    public void close() {
        if (!path.equals("")) {

            save();
            path = "";

            DefaultMutableTreeNode root = (DefaultMutableTreeNode) projectFolderFrame.getPFtreeTree().getModel().getRoot();
            DefaultMutableTreeNode projects = ((DefaultMutableTreeNode) root.getChildAt(0));
            DefaultMutableTreeNode files = ((DefaultMutableTreeNode) root.getChildAt(1));

            projects.removeAllChildren();
            files.removeAllChildren();

            this.clear();

        }
    }

    /**
     * Lädt eine Projektmappendatei und wendet sie an.
     *
     * @param path Pfad der Projektmappendatei
     */
    public void load(String path) {
        try {
            FileInputStream is = new FileInputStream(path);
            load(is);
            is.close();
            this.path = path;
            apply();

        } catch (IOException ex) {
            logger.log(Level.WARNING, "Fehler beim Laden der Projektmappe", ex);
        }
    }

    /**
     * Speichert eine Projektemappendatei in Pfad path. Catched eine IOException
     * falls Datei nicht existiert.
     *
     * @param path Pfad der Datei
     */
    public void save(String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            store(fos, null);
            this.path = path;
            fos.close();

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Fehler beim Speichern der Projektmappen-Datei", ex);

        }
    }

    /**
     * Speichert eine Projektmappendatei im davorgeladenen Pfad. Catched eine
     * IOException falls Datei nicht existiert.
     */
    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            store(fos, null);
            fos.close();

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Fehler beim Speichern der Konfigurations-Datei", ex);

        }
    }

    /**
     * Fügt eine Datei vom Typ type hinzu.
     *
     * @param project Datei
     * @param type TYPE_PROJECT oder TYPE_FILE
     */
    public void add(String project, String type) {
        setProperty(project, type);

    }

    /**
     * Fügt eine Datei vom Typ type hinzu.
     *
     * @param project Datei
     * @param type TYPE_PROJECT oder TYPE_FILE
     */
    public void add(File project, String type) {
        setProperty(project.getAbsolutePath(), type);

    }

    /**
     * Fügt Dateien vom Typ type hinzu.
     *
     * @param projects Dateiarray
     * @param type TYPE_PROJECT oder TYPE_FILE
     */
    public void add(File[] projects, String type) {
        for (File f : projects) {
            add(f, type);
        }
    }

    /**
     * Löscht eine Datei mit Namen name und Typ type aus der Lsite
     *
     * @param name Name
     * @param type Typ
     */
    public void delete(String name, String type) {
        Set<String> set = this.keySet();

        String projPath = "";

        for (String s : set) {
            if (((String) s).equals(name)) {
                if (this.getProperty(s).equals(type)) {
                    projPath = (String) s;
                    break;

                }
            }
        }

        this.remove(projPath);

    }

    /**
     * Liefert die komplette Liste eines Types zurück.
     *
     * @param type Typ
     * @return Liste
     */
    public List<String> getList(String type) {
        Set<String> set = this.keySet();

        List<String> list = new ArrayList<>();

        for (String s : set) {

            if (this.getProperty(s).equals(type)) {
                list.add((String) s);
            }

        }

        return list;
    }

    /**
     * Checkt, ob ein Eintrag mit String s in der Liste ist
     *
     * @param s der zu überprüfende String
     * @return true, falls in der Liste. false, sonst
     */
    public boolean isInList(String s) {
        Set<String> list = this.keySet();

        for (String o : list) {
            if (((String) o).equals(s)) {
                return true;
            }
        }

        return false;

    }

    /**
     * Liefert den Pfad der aktuell geladenen Datei zurück.
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * Fügt Liste in den Baum ein.
     */
    private void apply() {

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) projectFolderFrame.getPFtreeTree().getModel().getRoot();
        DefaultMutableTreeNode projects = ((DefaultMutableTreeNode) root.getChildAt(0));
        DefaultMutableTreeNode files = ((DefaultMutableTreeNode) root.getChildAt(1));

        List<String> list = getList(TYPE_PROJECT);
        for (String s : list) {

            String a = new File(s).getAbsolutePath();
            a = a.substring(a.lastIndexOf(System.getProperty("file.separator")) + 1);

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(new File(s).getName());
            node.setUserObject(a);
            projects.add(node);

        }

        list = getList(TYPE_FILE);
        for (String s : list) {

            String a = new File(s).getAbsolutePath();
            //    a=a.substring(a.lastIndexOf(System.getProperty("file.separator")));

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(new File(s).getName());
            node.setUserObject(a);
            files.add(node);

        }

        projectFolderFrame.setTitle(new File(path).getName());
        ((DefaultMutableTreeNode) projectFolderFrame.getPFtreeTree().getModel().getRoot()).setUserObject((String) new File(path).getName());
        projects.setUserObject(messages.getString("Transkripte"));
        files.setUserObject(messages.getString("Dateien"));
        ((DefaultTreeModel) projectFolderFrame.getPFtreeTree().getModel()).reload();
        projectFolderFrame.setVisible(true);

    }

}
