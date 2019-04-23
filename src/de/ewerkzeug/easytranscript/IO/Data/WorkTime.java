/**
 *
 *
 * easytranscript Copyright (C) 2013 e-werkzeug
 *
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License,
 * wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, entweder gemäß
 * Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren Version. Die Veröffentlichung dieses
 * Programms erfolgt in der Hoffnung, dass es Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE,
 * sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK.
 * Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. 
 * Falls nicht, siehe <http://www.gnu.org/licenses/>.
 *
 *
 */
package de.ewerkzeug.easytranscript.IO.Data;

import de.ewerkzeug.easytranscript.Core.ErrorReport;
import de.ewerkzeug.easytranscript.Core.V;

import static de.ewerkzeug.easytranscript.Core.V.errors;
import static de.ewerkzeug.easytranscript.Core.V.logger;
import static de.ewerkzeug.easytranscript.Core.V.messages;
import static de.ewerkzeug.easytranscript.Core.V.zeitFrame;
import static de.ewerkzeug.easytranscript.IO.Data.TranscriptHandler.transcriptPath;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Speichert die Arbeitszeit an einem Projekt.
 */
public class WorkTime extends EasyData {

    private boolean recordingTime = false;
    private int minutenvalue;

    /**
     * Liefert die totale Anzahl an Minuten zurück.
     *
     * @return Minutenanzahl
     */
    public int getTotalTime() {
        return minutenvalue;
    }

    /**
     * Setzt die totale Anzahl an Minuten
     *
     * @param minuten Minutenanzahl
     */
    public void setTotalTime(int minuten) {
        minutenvalue = 0;
    }

    /**
     * Liefert zurück, ob Zeit gerade aufgenommen wird.
     *
     * @return true, falls aufgenommen wird. false, sonst
     */
    public boolean getRecordingTime() {
        return recordingTime;
    }

    /**
     * Setzt fest, ob die Zeit gerade aufgenommen wird.
     *
     * @param time false oder true
     */
    public void setRecordingTime(boolean time) {
        recordingTime = time;
    }

    /**
     * Lädt und wendet die Worktime Datei an. Falls nicht vorhanden, wird eine neue, leere Datei erstellt.
     */
    public void loadWorkTime() {

        this.clear();
        //  try {
        String projPath = transcriptPath;
        if (projPath != null) {
            if (!projPath.equals("")) {
                projPath = projPath.substring(0, projPath.lastIndexOf(".")) + "_Data/default.arbeitszeit";

                try {
                    if (new File(projPath).exists()) {
                        try (FileInputStream fileInputStream = new FileInputStream(projPath)) {
                            load(fileInputStream);
                            applyWorkTime();
                        }
                        
                    } else {
                        saveWorkTimeByProjectPath("default");
                    }
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("FailedLoadingWorktime")), ex);
                }

            }
        }

    }

    /**
     * Lädt und wendet die Worktime Datei an. Falls nicht vorhanden, wird eine neue, leere Datei erstellt.
     *
     * @param apply Sollen die Daten angewendet werden?
     */
    public void loadWorkTime(boolean apply) {

        this.clear();
        //  try {
        String projPath = transcriptPath;
        if (projPath != null) {
            if (!projPath.equals("")) {
                projPath = projPath.substring(0, projPath.lastIndexOf(".")) + "_Data/default.arbeitszeit";

                try {
                    if (new File(projPath).exists()) {
                        FileInputStream fileInputStream = new FileInputStream(projPath);
                        load(fileInputStream);
                        fileInputStream.close();
                        if (apply) {
                            applyWorkTime();
                        }
                    } else {
                        saveWorkTimeByProjectPath("default");
                    }
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("FailedLoadingWorktime")), ex);
                }

            }
        }

    }

    /**
     * Lädt und wendet die Worktime Datei an. Falls nicht vorhanden, wird eine neue, leere Datei erstellt. Lädt die Datei aus dem Pfad path.
     *
     * @param path der Pfad der Datei
     */
    public void loadWorkTime(String path) {

        this.clear();
        //  try {
        String projPath = path;
        if (projPath != null) {
            if (!projPath.equals("")) {

                try {
                    if (new File(projPath).exists()) {
                        FileInputStream fileInputStream = new FileInputStream(projPath);
                        load(fileInputStream);
                        fileInputStream.close();
                    } else {
                        saveWorkTimeByProjectPath("default");
                    }
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("FailedLoadingWorktime")), ex);
                }

            }
        }

    }

    /**
     * Lädt und wendet die Worktime Datei an. Falls nicht vorhanden, wird eine neue, leere Datei erstellt. Lädt die Datei aus dem Pfad path.
     *
     * @param path der Pfad der Datei
     * @param apply Sollen die Daten angewendet werden?
     */
    public void loadWorkTime(String path, boolean apply) {

        this.clear();
        //  try {
        String projPath = path;
        if (projPath != null) {
            if (!projPath.equals("")) {
                projPath = projPath.substring(0, projPath.lastIndexOf(".")) + "_Data/default.arbeitszeit";

                try {
                    if (new File(projPath).exists()) {
                        FileInputStream fileInputStream = new FileInputStream(projPath);
                        load(fileInputStream);
                        fileInputStream.close();
                        if (apply) {
                            applyWorkTime();
                        }
                    } else {
                        saveWorkTimeByProjectPath("default");
                    }
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("FailedLoadingWorktime")), ex);
                }

            }
        }

    }

    /**
     * Speichert die WorkTime Datei mit Namen name im Data Ordner des Projektes
     *
     * @param name Name der Datei
     */
    public void saveWorkTimeByProjectPath(String name) {
        String projPath = transcriptPath;
        if (projPath != null) {
            if (!projPath.equals("")) {
                projPath = projPath.substring(0, projPath.lastIndexOf(".")) + "_Data/" + name + ".arbeitszeit";
                try {
                    FileOutputStream fos = new FileOutputStream(projPath);
                    store(fos, "---DO NOT MODIFY THIS FILE!---");
                    fos.close();
                } catch (IOException ex) {
                    logger.log(Level.WARNING, "Fehler beim Speichern der Arbeitszeit.", ex);
                }
            }
        }
    }

    /**
     * Speichert die WorkTime Datei im Pfad path
     *
     * @param path Pfad
     */
    private void saveWorkTime(String path) {
        String projPath = path;
        if (projPath != null) {
            if (!projPath.equals("")) {

                try {
                    FileOutputStream fos = new FileOutputStream(projPath);
                    store(fos, "---DO NOT MODIFY THIS FILE!---");
                    fos.close();

                } catch (IOException ex) {
                    logger.log(Level.WARNING, "Fehler beim Speichern der Arbeitszeit.", ex);
                }
            }
        }
    }

    /**
     * Erstellt neuen WorkTime Eintrag.
     */
    public void startNewWorkTimeEntry() {

        if (getProperty("length") == null || "".equals(getProperty("length"))) {
            setProperty("length", "0");
            saveWorkTimeByProjectPath("default");
        }

        String lengthS = getProperty("length");
        int length = Integer.parseInt(lengthS);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy '-' HH:mm");
        Date date = new Date();

        setProperty("[" + Integer.toString(length + 1) + "]Start", dateFormat.format(date));
        setProperty("length", Integer.toString(length + 1));

        saveWorkTimeByProjectPath("default");
        recordingTime = true;
        System.out.println();
        logger.log(Level.INFO, "Started Work time entry.");
    }

    /**
     * Beendet den aktuellen Worktime-Entry Eintrag.
     */
    public void endCurrentWorkTimeEntry() {

        String lengthS = getProperty("length");

        if (lengthS != null && !lengthS.equals("") && !lengthS.equals("0")) {
            int length = Integer.parseInt(lengthS);

            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy '-' HH:mm");
            Date date = new Date();
            if (getProperty("[" + Integer.toString(length) + "]Ende") == null) {
                setProperty("[" + Integer.toString(length) + "]Ende", dateFormat.format(date));
                logger.log(Level.INFO, "current working time entry closed.");
            } else {
                logger.log(Level.WARNING, "End Property already set.");
            }
            saveWorkTimeByProjectPath("default");
            recordingTime = false;

        } else {
            logger.log(Level.WARNING, "Length equals zero");
        }

    }

    /**
     * Löscht leere WorkTime Einträge.
     */
    public void deleteWorkTimeEntriesZ() {
        int length = Integer.parseInt(getProperty("length"));
        int counter = 0;
        for (int i = 1; i < length + 1; i++) {
            String startT = getProperty("[" + Integer.toString(i) + "]Start");
            String endeT = getProperty("[" + Integer.toString(i) + "]Ende");

            if (startT != null && endeT != null) {
                if (startT.equals(endeT)) {

                    deleteWorkTimeEntry(i);
                    counter++;
                }
            }

            if (startT == null || endeT == null) {
                boolean allowed = true;
                if (i == length) {
                    if (recordingTime) {
                        allowed = false;
                    }
                }
                if (allowed) {
                    deleteWorkTimeEntry(i);
                    counter++;
                }
            }

        }
        setProperty("length", Integer.toString(Integer.parseInt(getProperty("length")) - counter));

        saveWorkTimeByProjectPath("default");

    }

    /**
     * Löscht einen einzelnen WorkTime Eintrag.
     *
     * @param id
     */
    public void deleteWorkTimeEntry(int id) {
        remove("[" + Integer.toString(id) + "]Start");
        remove("[" + Integer.toString(id) + "]Ende");
    }

    /**
     * Aktualisiert die WorkTime IDs.
     *
     * @param length Länge der Liste
     */
    public void closeWorkTimeGaps(int length) {

        for (int i = 1; i < length + 1; i++) {

            if (getProperty("[" + Integer.toString(i) + "]Start") == null || getProperty("[" + Integer.toString(i) + "]Start").equals("")) {

                for (int j = i + 1; j < length + 1; j++) {
                    String startT = getProperty("[" + Integer.toString(j) + "]Start");
                    String endeT = getProperty("[" + Integer.toString(j) + "]Ende");

                    if (startT != null) {

                        if (startT != null) {
                            setProperty("[" + Integer.toString(j - 1) + "]Start", startT);
                        }
                        if (endeT != null) {
                            setProperty("[" + Integer.toString(j - 1) + "]Ende", endeT);
                        }
                        remove("[" + Integer.toString(j) + "]Start");
                        remove("[" + Integer.toString(j) + "]Ende");
                    }
                }
            }
        }

    }

    /**
     * Wendet die WorkTime Daten im entsprechenden Dialog an.
     */
    public void applyWorkTime() {

        minutenvalue = 0;

         String[] columnNames = {"ID",
            messages.getString("Von"),
            messages.getString("Bis"), messages.getString("Dauer")
        };

        Object[][] data = {};
        zeitFrame.getZeitTableTable().setModel(new DefaultTableModel(data, columnNames) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    
        zeitFrame.getZeitActivateCheckbox().setVisible(true);
        zeitFrame.getZeitDeleteAllButton().setEnabled(true);
        
        zeitFrame.getZeitTotalLabel().setText("0");
        zeitFrame.setVisible(false);

        if (getProperty("StundenbasisSp") != null) {
            zeitFrame.getZeitHourlySpinner().setValue(Math.round((Double.parseDouble(getProperty("StundenbasisSp")) * 10) / 10));
        } else {
            zeitFrame.getZeitHourlySpinner().setValue(0d);
        }

        if (getProperty("CurrencyStundenbasis") != null) {
            zeitFrame.getZeitHourlyCurrencyCombobox().setEditable(true);
            zeitFrame.getZeitHourlyCurrencyCombobox().setSelectedItem(zeitFrame.getZeitHourlyCurrencyCombobox().getSelectedItem());
            zeitFrame.getZeitHourlyCurrencyCombobox().setEditable(false);
        } else {
            zeitFrame.getZeitHourlyCurrencyCombobox().setSelectedIndex(0);
        }

        if (getProperty("FixWertSp") != null) {
            zeitFrame.getZeitFixSpinner().setValue(Math.round((Double.parseDouble(getProperty("FixWertSp")) * 10) / 10));
        } else {
            zeitFrame.getZeitFixSpinner().setValue(0d);
        }

        if (getProperty("CurrencyFixwert") != null) {
            zeitFrame.getZeitFixCurrencyCombobox().setSelectedItem(getProperty("CurrencyFixwert"));
        } else {
            zeitFrame.getZeitFixCurrencyCombobox().setSelectedIndex(0);
        }

        boolean nec = false;
        if (getProperty("length") != null) {

            int lengthT = Integer.parseInt(getProperty("length"));
            deleteWorkTimeEntriesZ();

            for (int c = 1; c < lengthT + 1; c++) {
                closeWorkTimeGaps(lengthT);
            }
            DefaultTableModel model = (DefaultTableModel) zeitFrame.getZeitTableTable().getModel();

            int rows = model.getRowCount();

            for (int i = rows - 1; i >= 0; i--) {
                model.removeRow(i);
            }

            for (int i = 0; i < Integer.valueOf(getProperty("length")); i++) {
                model.addRow(new Object[model.getColumnCount()]);

            }

            for (int i = 0; i < Integer.valueOf(getProperty("length")); i++) {
                model.setValueAt(i + 1, i, 0);
                model.setValueAt(getProperty("[" + (i + 1) + "]Start"), i, 1);
                model.setValueAt(getProperty("[" + (i + 1) + "]Ende"), i, 2);
                String startV = getProperty("[" + (i + 1) + "]Start");
                String endeV = getProperty("[" + (i + 1) + "]Ende");
                if (i != (Integer.valueOf(getProperty("length")) - 1)) {
                    if (endeV == null || endeV.equals("")) {
                        endeV = startV;
                        model.setValueAt(startV, i, 2);
                        saveWorkTimeByProjectPath("default");
                    }
                }
                SimpleDateFormat strToDate = null;
                
               
                //if (!currentLocale.getLanguage().equals("ja")) {
                    strToDate = new SimpleDateFormat("dd.MM.yyyy '-' HH:mm");
               // } else {
             //       strToDate = new SimpleDateFormat("yyyy.MM.dd '-' HH:mm", Locale.JAPANESE);
           //    }
                
                Date startD = null, endD = null;
                if (startV != null) {
                    if (!startV.equals("")) {
                        try {
                            startD = strToDate.parse(startV);
                        } catch (ParseException ex) {
                            logger.log(Level.WARNING, "Die Auswertung der Arbeitszeitdaten schlug fehl. (startD)", ex);

                        }

                    }
                }

                if (endeV != null) {
                    if (!endeV.equals("")) {
                        try {
                            endD = strToDate.parse(endeV);
                        } catch (ParseException ex) {
                            logger.log(Level.WARNING, "Die Auswertung der Arbeitszeitdaten schlug fehl. (endD)", ex);
                        }
                    }
                }
                
                

                if (endeV != null && startV != null) {
                    boolean setze = true;
                    long difference = endD.getTime() - startD.getTime();
                    double minuten = difference / 1000 / 60;
                    minuten = Math.round(minuten * 10) / 10.0;

                    if (minuten >= 240) {

                        int a = JOptionPane.showConfirmDialog(null, messages.getString("TimeOddEntry1") + "\n" + messages.getString("Von") + ": " + startV + " \n"
                                + messages.getString("Bis") + ": " + endeV + "\n" + messages.getString("Dauer") + ": " + new Double(minuten).intValue() + " " + messages.getString("Minuten") + " \n" + messages.getString("TimeOddEntry2") + "\n"
                                + messages.getString("TimeOddEntry3"), messages.getString("TimeOddEntryTitle"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                        if (a == JOptionPane.YES_OPTION) {
                            deleteWorkTimeEntry(i + 1);
                            setze = false;
                            nec = true;
                        }

                    }

                    if (setze) {
                        minutenvalue = minutenvalue + new Double(minuten).intValue();
                        model.setValueAt((int) minuten + " " + messages.getString("Minuten"), i, 3);
                    }

                }
                if (nec) {

                    for (int c = 1; c < lengthT + 1; c++) {
                        closeWorkTimeGaps(lengthT);
                    }

                    saveWorkTimeByProjectPath("default");
                    loadWorkTime();
                }
            }

            zeitFrame.getZeitTableTable().repaint();

            if (zeitFrame.getZeitHourlyRadiobutton().isSelected()) {
                zeitFrame.getZeitHourlyCurrencyCombobox().getItemListeners()[0].itemStateChanged(null);
                zeitFrame.getZeitHourlySpinner().getChangeListeners()[0].stateChanged(null);
            }
            if (zeitFrame.getZeitFixRadiobutton().isSelected()) {
                zeitFrame.getZeitFixSpinner().getChangeListeners()[0].stateChanged(null);
                zeitFrame.getZeitFixCurrencyCombobox().getItemListeners()[0].itemStateChanged(null);
            }

            zeitFrame.getZeitTableScrollpane().getVerticalScrollBar().setValue(zeitFrame.getZeitTableScrollpane().getVerticalScrollBar().getMaximum());
            String stundenString = messages.getString("Stunden");
            String minutenString = messages.getString("Minuten");
            if (TimeUnit.MINUTES.toHours(minutenvalue) == 1) {
                stundenString = messages.getString("Stunde");
            }

            if ((TimeUnit.MINUTES.toMinutes(minutenvalue) - TimeUnit.HOURS.toMinutes(TimeUnit.MINUTES.toHours(minutenvalue))) == 1) {
                minutenString = messages.getString("Minute");
            }

            String ergebnisS = String.format(messages.getString("Gesamtdauer") + ": %d " + stundenString + ", %d " + minutenString, TimeUnit.MINUTES.toHours(minutenvalue), (TimeUnit.MINUTES.toMinutes(minutenvalue) - TimeUnit.HOURS.toMinutes(TimeUnit.MINUTES.toHours(minutenvalue))));

            zeitFrame.getZeitTotalLabel().setText(ergebnisS);

        }
    }

    /**
     * Aktionen, die nach Schliessen des Worktime Fenster ausgeführt werden müssen.
     */
    public void closeWorkTimeWindow() {
        zeitFrame.setVisible(false);

        setProperty("StundenbasisSp", String.valueOf(zeitFrame.getZeitHourlySpinner().getValue()));
        setProperty("CurrencyStundenbasis", String.valueOf(zeitFrame.getZeitHourlyCurrencyCombobox().getSelectedItem()));
        setProperty("FixWertSp", String.valueOf(zeitFrame.getZeitFixSpinner().getValue()));
        setProperty("CurrencyFixwert", String.valueOf(zeitFrame.getZeitFixCurrencyCombobox().getSelectedItem()));
        saveWorkTimeByProjectPath("default");

    }

}
