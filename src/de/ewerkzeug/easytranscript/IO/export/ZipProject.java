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
package de.ewerkzeug.easytranscript.IO.export;

import static de.ewerkzeug.easytranscript.core.Variables.errors;
import static de.ewerkzeug.easytranscript.core.Variables.exportFrame;
import static de.ewerkzeug.easytranscript.core.Variables.logger;
import static de.ewerkzeug.easytranscript.core.Variables.messages;
import static de.ewerkzeug.easytranscript.core.Variables.workerFrame;
import de.ewerkzeug.easytranscript.IO.data.TranscriptHandler;
import static de.ewerkzeug.easytranscript.IO.data.TranscriptHandler.transcriptPath;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * SwingWorker zum zippen eines Projektes
 */
public class ZipProject extends SwingWorker<String, String> {

    String projPath, destS, medPath, trPath;
    de.ewerkzeug.easytranscript.core.ErrorReport Status = new de.ewerkzeug.easytranscript.core.ErrorReport();

    /**
     * Konstruktor mit Angabe von Projektpfad, Zielpfad, Mediendateipfad und Transkriptpfad
     * @param projPath Projektpfad
     * @param destS Zielpfad
     * @param medPath Mediendateipfad
     * @param trPath Transkriptpfad
     */
    public ZipProject(String projPath, String destS, String medPath, String trPath) {
        this.projPath = projPath;
        this.destS = destS;
        this.medPath = medPath;
        this.trPath = trPath;

    }
/**
 * Zippen des Projektes
 * @return nicht benötigt
 */
    @Override
    public String doInBackground() {

        FileOutputStream dest = null;
        final int BUFFER = 2048;
        byte data[] = new byte[BUFFER];
        BufferedInputStream origin = null;
FileInputStream fi2 = null;
        String sep = "\\";
        boolean contains = projPath.contains("\\");
        if (contains == false) {
            sep = "/";
        }

        //!!!
        if (projPath.endsWith(".tmp")){
            logger.log(Level.SEVERE, "Unbekannter Bug. Erstmaliges Auftreten am: 5.12.14, Report-ID: log_2014-Dec-1_1417809697317, Bug-Nr: 005: projectPath endet auf .tmp");
            projPath=projPath.replace(".tmp", "");
        }
        //!!!
        String projName = projPath.substring(projPath.lastIndexOf(sep) + 1, projPath.lastIndexOf("."));
         TranscriptHandler.save(transcriptPath, false, false);
        ZipOutputStream out = null;

        try {
            dest = new FileOutputStream(new File(destS));
            out = new ZipOutputStream(new BufferedOutputStream(dest));

             fi2 = new FileInputStream(projPath + ".tmp");
            origin = new BufferedInputStream(fi2, BUFFER);
            ZipEntry entry = new ZipEntry("" + projName + ".etp");
            out.putNextEntry(entry);

            int count;
            logger.log(Level.INFO, "Füge Projektdatei hinzu");
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
                out.flush();
            }

        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, new de.ewerkzeug.easytranscript.core.ErrorReport().show(errors.getString("FailedToExport")), ex);
            
            
        } catch (IOException ex) {
            logger.log(Level.SEVERE, Status.show(errors.getString("FailedToExport")), ex);
        }

        try {
            if (!trPath.equals("")) {

                if (!trPath.contains(projPath.substring(0, projPath.lastIndexOf(".")) + "_Data")) {

                    FileInputStream fi = new FileInputStream(new File(trPath));
                    origin = new BufferedInputStream(fi, BUFFER);
                    String trName = trPath.substring(trPath.lastIndexOf(sep) + 1);
                    ZipEntry entry = new ZipEntry("" + projName + "_Data/" + trName);

                    out.putNextEntry(entry);
                    int count;
                    logger.log(Level.INFO, "Füge Transkript hinzu");
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {

                        out.write(data, 0, count);
                        out.flush();
                    }
                }
            }

        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, Status.show(errors.getString("FailedToExport")), ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, Status.show(errors.getString("FailedToExport")), ex);
        }

        try {
            if (!medPath.equals("")) {
                if (!medPath.contains(projPath.substring(0, projPath.lastIndexOf(".")) + "_Data")) {
                    FileInputStream fi = new FileInputStream(new File(medPath));
                    origin = new BufferedInputStream(fi, BUFFER);
                    String medName = medPath.substring(medPath.lastIndexOf(sep) + 1);
                    ZipEntry entry = new ZipEntry("" + projName + "_Data/" + medName);
                    out.putNextEntry(entry);
                    int count;
                    logger.log(Level.INFO, "Füge Mediendatei hinzu");
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                        out.flush();
                    }
                }
            }

        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, Status.show(errors.getString("FailedToExport")), ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, Status.show(errors.getString("FailedToExport")), ex);
        }

        try {

            String dataPath = projPath.substring(0, projPath.lastIndexOf("."));
            File subDir = new File(dataPath + "_Data/");
            String subdirList[] = subDir.list();
            for (String sd : subdirList) {
                // get a list of files from current directory
                File f = new File(dataPath + "_Data/" + sd);
                if (f.isDirectory()) {
                    String files[] = f.list();
                    for (String file : files) {
                        logger.log(Level.INFO, "F\u00fcge hinzu: {0}", file);
                        FileInputStream fi = new FileInputStream(dataPath + "_Data/" + sd + "/" + file);
                        origin = new BufferedInputStream(fi, BUFFER);
                        ZipEntry entry = new ZipEntry("" + projName + "_Data/" + sd + "/" + file);
                        try {
                            out.putNextEntry(entry);
                            int count;
                            long length = new File(dataPath + "_Data/" + sd + "/" + file).length();
                            while ((count = origin.read(data, 0, BUFFER)) != -1) {

                                out.write(data, 0, count);

                                out.flush();
                            }
                        } catch (ZipException e) {
                            logger.log(Level.WARNING, "Konnte Datei " + f.getName() + " nicht hinzufügen. Möglicherweise wurde sie bereits hinzugefügt.", e);
                        }
                    }
                } else //it is just a file
                                {
                    FileInputStream fi = new FileInputStream(f);
                    origin = new BufferedInputStream(fi, BUFFER);
                    ZipEntry entry = new ZipEntry("" + projName + "_Data/" + sd);
                    try {
                        out.putNextEntry(entry);
                        int count;
                        logger.log(Level.INFO, "F\u00fcge hinzu: {0}", f.getName());
                        while ((count = origin.read(data, 0, BUFFER)) != -1) {
                            out.write(data, 0, count);
                            out.flush();
                        }
                    } catch (ZipException e) {
                        logger.log(Level.WARNING, "Konnte Datei " + f.getName() + " nicht hinzufügen. Möglicherweise wurde sie bereits hinzugefügt.", e);
                    }
                }
            }

        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, Status.show(errors.getString("FailedToExport")), ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, Status.show(errors.getString("FailedToExport")), ex);
        }

        try {
            origin.close();
            out.flush();
            out.close();
            fi2.close();
            dest.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, Status.show(errors.getString("FailedToExport")), ex);
        }

        return "";

    }

    /**
     * Abschluss des Zippens. Ausgabe einer Erfolgsnachricht
     */
    @Override
    public void done() {

        workerFrame.setVisible(false);
        new File(transcriptPath + ".tmp").delete();
        exportFrame.setVisible(false);
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(null, messages.getString("projectExportSuccess"));
    }
}
