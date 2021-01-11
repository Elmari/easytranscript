/**
 *
 *
 * easytranscript Copyright (C) 2013 e-werkzeug
 *
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der
 * GNU General Public License, wie von der Free Software Foundation
 * veröffentlicht, weitergeben und/oder modifizieren, entweder gemäß Version 3
 * der Lizenz oder (nach Ihrer Option) jeder späteren Version. Die
 * Veröffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von
 * Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite
 * Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK.
 * Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/>.
 *
 *
 */
package de.ewerkzeug.easytranscript.IO.data;

import static de.ewerkzeug.easytranscript.core.Easytranscript.Mainrecent1Menuitem;
import static de.ewerkzeug.easytranscript.core.Easytranscript.Mainrecent2Menuitem;
import static de.ewerkzeug.easytranscript.core.Easytranscript.Mainrecent3Menuitem;
import de.ewerkzeug.easytranscript.core.ErrorReport;
import static de.ewerkzeug.easytranscript.core.Variables.errors;
import static de.ewerkzeug.easytranscript.core.Variables.logger;
import static de.ewerkzeug.easytranscript.core.Variables.opFolder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Gespeicherte zuletzt benutzte Projekte
 */
public class RecentUsed extends Configuration {

    /**
     * Lädt und wendet die recent.properties an.
     */
    public void load() {

        try {
            load(new FileInputStream(opFolder + "conf/recent.properties"));
            applyRecentUsed();
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Recent-Datei konnte nicht gelesen werden.", ex);
        }
    }

    /**
     * wendet die recent Datei an. Entsprechende Menü-Einträge werden mit Werten
     * gefüllt.
     */
    public void applyRecentUsed() {
        String recents[] = new String[3];

        for (int i = 0; i < 3; i++) {
            recents[i] = getProperty("recent" + i);
        }

        if (recents[0] != null) {
            Mainrecent1Menuitem.setText(recents[0]);
            Mainrecent1Menuitem.setVisible(true);
            Mainrecent2Menuitem.setVisible(!Mainrecent1Menuitem.getText().equals(""));
        } else {
            Mainrecent1Menuitem.setVisible(false);
        }

        if (recents[1] != null) {
            Mainrecent2Menuitem.setText(recents[1]);
            Mainrecent2Menuitem.setVisible(true);

            Mainrecent2Menuitem.setVisible(!Mainrecent2Menuitem.getText().equals(""));

        } else {
            Mainrecent2Menuitem.setVisible(false);
        }
        if (recents[2] != null) {
            Mainrecent3Menuitem.setText(recents[2]);
            Mainrecent3Menuitem.setVisible(true);
            Mainrecent3Menuitem.setVisible(!Mainrecent3Menuitem.getText().equals(""));
        } else {
            Mainrecent3Menuitem.setVisible(false);
        }

        // checkRecentUsed();
    }

    /**
     * Löscht RecentEntry, welcher dem angegebenem String entspricht.
     *
     * @param path
     */
    public void resetRecentEntry(String path) {

        Mainrecent1Menuitem.setEnabled(true);
        Mainrecent2Menuitem.setEnabled(true);
        Mainrecent3Menuitem.setEnabled(true);

        if (getProperty("recent1").equals(path)) {
            String recent2T = "";
            if (getProperty("recent2") != null) {
                recent2T = getProperty("recent2");
            }

            Mainrecent2Menuitem.setText(recent2T);
            setProperty("recent1", Mainrecent2Menuitem.getText());

        } else if (getProperty("recent0").equals(path)) {
            String recent2T = "", recent1T = "";
            if (getProperty("recent2") != null) {
                recent2T = getProperty("recent2");
            }
            if (getProperty("recent1") != null) {
                recent1T = getProperty("recent1");
            }

            Mainrecent1Menuitem.setText(recent1T);
            Mainrecent2Menuitem.setText(recent2T);
            setProperty("recent0", Mainrecent1Menuitem.getText());
            setProperty("recent1", Mainrecent2Menuitem.getText());
        }

        setProperty("recent2", "");
        Mainrecent3Menuitem.setText("");
        Mainrecent3Menuitem.setVisible(false);

        checkRecentUsed();

        saveRecentUsed();

    }

    /**
     * Fügt einen entsprechenden RecentUsed Eintrag hinzu.
     *
     * @param recent
     */
    public void addRecentUsed(String recent) {

        String rec1T = "", rec2T = "", rec3T = "";

        if (getProperty("recent0") != null) {
            rec1T = getProperty("recent0");
        }

        if (getProperty("recent1") != null) {
            rec2T = getProperty("recent1");
        }

        if (getProperty("recent2") != null) {
            rec3T = getProperty("recent2");
        }

        if (!recent.equals(rec1T) && !recent.equals(rec2T) && !recent.equals(rec3T)) {

            if (getProperty("recent1") != null) {

                setProperty("recent2", getProperty("recent1"));
            }

            if (getProperty("recent0") != null) {
                setProperty("recent1", getProperty("recent0"));
            }

            setProperty("recent0", recent);

            checkRecentUsed();

            saveRecentUsed();
        }

    }

    /**
     * Speichert die recent.properties.
     */
    public void saveRecentUsed() {
        try {
            FileOutputStream fos = new FileOutputStream(opFolder + "conf/recent.properties");
            if (getProperty("recent2") == null) {
                setProperty("recent2", "");
            }
            store(fos, null);

            applyRecentUsed();
        } catch (IOException ex) {
            new ErrorReport().show(errors.getString("FailedSavingRecentUsed"));
        }
    }

    /**
     * Deaktiviert Menü-Einträge, falls der Text leer sein sollte.
     */
    public void checkRecentUsed() {
        if (Mainrecent1Menuitem.getText().equals("")) {
            Mainrecent1Menuitem.setVisible(false);
        } else {
            Mainrecent1Menuitem.setVisible(true);
        }

        if (Mainrecent2Menuitem.getText().equals("")) {
            Mainrecent2Menuitem.setVisible(false);

        } else {
            Mainrecent2Menuitem.setVisible(true);
        }

        if (Mainrecent3Menuitem.getText().equals("")) {
            Mainrecent3Menuitem.setVisible(false);
        } else {
            Mainrecent3Menuitem.setVisible(true);
        }
    }

}
