/**
 * easytranscript Copyright (C) 2013 e-werkzeug
 * <p>
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der
 * GNU General Public License, wie von der Free Software Foundation
 * veröffentlicht, weitergeben und/oder modifizieren, entweder gemäß Version 3
 * der Lizenz oder (nach Ihrer Option) jeder späteren Version. Die
 * Veröffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von
 * Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite
 * Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK.
 * Details finden Sie in der GNU General Public License.
 * <p>
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.ewerkzeug.easytranscript.IO.Data;

import de.ewerkzeug.easytranscript.Core.ErrorReport;
import de.ewerkzeug.easytranscript.Core.V;
import de.ewerkzeug.easytranscript.GUI.Components.ProjectFrame;
import de.ewerkzeug.easytranscript.GUI.Misc.TextfieldListener;
import de.ewerkzeug.easytranscript.Tools.PlayerFX;
import javafx.scene.media.MediaException;
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.ewerkzeug.easytranscript.Core.V.*;
import static de.ewerkzeug.easytranscript.GUI.Components.NewTranscriptFrame.clearProjWindowValues;

/**
 * Beinhaltet Methoden die für easytranscript Projekte benötigt werden.
 */
public class TranscriptHandler {

    public static boolean unsaved = false, backupNeeded = false;
    public static String transcriptPath, transcriptName, documentName, mediaPath;
    public static EasyData transConf = new EasyData();
    public static EasyData TransConfTmp = new EasyData();
    private static boolean added = false;

    /**
     * Liest eine RTF Datei im Pfad path ein und weist sie dem Textfeld zu
     *
     * @param path Pfad der RTF Datei
     */
    public static void readDocument(String path) {

        try {
            String sep = "\\";
            boolean contains = path.contains("\\");
            if (contains == false) {
                sep = "/";
            }

            easytranscript.getMainCenterEditorEditorPane().getDocument().remove(0, easytranscript.getMainCenterEditorEditorPane().getDocument().getLength());

            FileInputStream datei = new FileInputStream(new File(path));
            if (prop.getBoolProperty("usePerformanceMode")) {
                easytranscript.getMainCenterEditorEditorPane().setVisible(false);
            }
            easytranscript.getMainCenterEditorEditorPane().read(datei, easytranscript.getMainCenterEditorEditorPane().getDocument());

            simulateZoom(easytranscript.getMainCenterEditorEditorPane().getDocument(), false);
            easytranscript.getMainCenterEditorEditorPane().setVisible(true);

            datei.close();

            path = path.substring(path.lastIndexOf(sep) + 1);

            documentName = path;
            if (!prop.getBoolProperty("usePerformanceMode")) {
                easytranscript.getMainCenterEditorEditorPane().getDocument().putProperty("ZOOM_FACTOR", zoomFactor);
            } else {
                easytranscript.getMainCenterEditorEditorPane().setFont(new java.awt.Font("Arial", 0, 12 + V.performanceModeFontSizeIncrease));
            }

            undoManager.discardAllEdits();
            easytranscript.getMainCenterEditorEditorPane().getDocument().removeUndoableEditListener(undoManager);
            easytranscript.getMainCenterEditorEditorPane().getDocument().addUndoableEditListener(undoManager);

            unsaved = false;
            backupNeeded = false;

        } catch (BadLocationException | IOException ex) {
            logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("TranscriptCouldNotBeLoaded")), ex);

        }

    }

    /**
     * Öffnet das Medium im Pfad path.
     *
     * @param path Pfad des Mediums
     * @return false, falls die Medienlänge unter 1er Sekunde liegt. true falls
     * sonst
     */
    public static boolean openMedia(String path) {
        logger.log(Level.INFO, "Betrete openMedia({0})", path);
        logger.log(Level.INFO, "useFXPlayer {0}", useFXPlayer);
        mediaPath = "";

        if (!useFXPlayer) {
            easytranscript.getMainCenterVideoInternalframe().setContentPane(player);

            player.getMediaPlayer().stop();

            mediaPath = path;
            if (!mediaPath.equals("")) {
                String url;
                if (!mediaPath.contains("youtube.com/")) {
                    url = new File(mediaPath).toURI().toASCIIString();
                    url = url.replaceFirst("file:/", "file:///");
                } else {

                    url = mediaPath;
                    url = url.replace("&equiv;", "=");
                }

                logger.log(Level.INFO, "Lade ...");
                boolean started = player.getMediaPlayer().startMedia(url);
                player.getMediaPlayer().setPlaySubItems(true);

                if (!started) {

                    JOptionPane.showMessageDialog(null, errors.getString("FailedPlaying"), messages.getString("ConfigStenoShortcutErrorTitle"), JOptionPane.ERROR_MESSAGE);

                } else {
                    logger.log(Level.INFO, "Erfolg ...");
                    easytranscript.getMainTimePlayerplayButton().setEnabled(true);
                    easytranscript.getMainTimePlayerbackwButton().setEnabled(true);
                    easytranscript.getMainTimePlayerforwButton().setEnabled(true);
                    tunerDialog.getTunerVolumeSlider().setEnabled(true);
                    easytranscript.getMainTimeIntervalSlider().setEnabled(true);
                    easytranscript.getMainSlider().setEnabled(true);
                    player.setMediaLoaded(true);

                }
            }
        } else {
            // easytranscript.getMainCenterVideoInternalframe().setContentPane(player);

            mediaPath = path;
            if (!mediaPath.equals("")) {

                String url = new File(mediaPath).toURI().toString();
                logger.log(Level.INFO, "Lade ...");
                try {
                    PlayerFX.setMedia(new javafx.scene.media.Media(url));
                } catch (MediaException me) {
                    JOptionPane.showMessageDialog(null, messages.getString("NotSupportedByFX"), messages.getString("ConfigStenoShortcutErrorTitle"), JOptionPane.ERROR_MESSAGE);
                    logger.log(Level.WARNING, me.getMessage());
                    close();
                }
                logger.log(Level.INFO, "Erstelle ...");
                PlayerFX.setMediaPlayer(null);

                PlayerFX.setMediaPlayer(new javafx.scene.media.MediaPlayer(PlayerFX.getMedia()));
                logger.log(Level.INFO, "Listeners ...");
                PlayerFX.getMediaPlayer().currentTimeProperty().addListener(new javafx.beans.value.ChangeListener<javafx.util.Duration>() {

                    @Override
                    public void changed(javafx.beans.value.ObservableValue<? extends javafx.util.Duration> ov, javafx.util.Duration t, javafx.util.Duration t1) {

                        if (PlayerFX.getMediaPlayer().getStatus().equals(javafx.scene.media.MediaPlayer.Status.PLAYING)) {
                            easytranscript.getBufferingLabel().setText(" ");
                            if (currentPlayerTime < 0) {
                                currentPlayerTime = 0;
                            }
                            if (currentPlayerTime > PlayerFX.getMediaPlayer().getTotalDuration().toMillis()) {
                                currentPlayerTime = (long) PlayerFX.getMediaPlayer().getTotalDuration().toMillis();
                            }

                            easytranscript.getMainSlider().setValue((int) PlayerFX.getMediaPlayer().getCurrentTime().toMillis() / 1000);
                            currentPlayerTime = (long) t1.toMillis();

                            Stunden_current = (int) (currentPlayerTime / 1000 / 60 / 60);
                            Minuten_current = (int) ((currentPlayerTime - (Stunden_current * 1000 * 60 * 60)) / 1000 / 60);
                            Sekunden_current = (int) ((currentPlayerTime - (Stunden_current * 1000 * 60 * 60) - (Minuten_current * 1000 * 60)) / 1000);
                            Millisekunden_current = (int) ((currentPlayerTime - (Stunden_current * 1000 * 60 * 60) - (Minuten_current * 1000 * 60) - (Sekunden_current * 1000)) / 100);

                            if (Stunden_current < 10) {
                                StundenC_string = "0" + String.valueOf(Stunden_current);
                            } else {
                                StundenC_string = String.valueOf(Stunden_current);
                            }
                            if (Minuten_current < 10) {
                                MinutenC_string = "0" + String.valueOf(Minuten_current);
                            } else {
                                MinutenC_string = String.valueOf(Minuten_current);
                            }
                            if (Sekunden_current < 10) {
                                SekundenC_string = "0" + String.valueOf(Sekunden_current);
                            } else {
                                SekundenC_string = String.valueOf(Sekunden_current);
                            }

                            easytranscript.getMainTimeLabel().setText(StundenC_string + ":" + MinutenC_string + ":" + SekundenC_string);

                            easytranscript.getMainTimeMilliLabel().setText("-" + Millisekunden_current);
                            String stern = "";
                            if (TranscriptHandler.isUnsaved()) {
                                stern = "*";
                            }
                            easytranscript.setTitle("easytranscript - " + transcriptName + stern + " - " + easytranscript.getMainTimeLabel().getText() + " " + messages.getString("vonTime") + " " + sfull);

                        }
                    }
                });
                logger.log(Level.INFO, "onReady ...");
                PlayerFX.getMediaPlayer().setOnReady(new Runnable() {
                    @Override
                    public void run() {
                        int Stunden, Minuten, Sekunden, Millisekunden;

                        if (PlayerFX.mediaLength == 0) {
                            PlayerFX.mediaLength = (long) PlayerFX.getMediaPlayer().getTotalDuration().toMillis();

                            Stunden = (int) (PlayerFX.mediaLength / 1000 / 60 / 60);
                            Minuten = (int) ((PlayerFX.mediaLength - (Stunden * 1000 * 60 * 60)) / 1000 / 60);
                            Sekunden = (int) ((PlayerFX.mediaLength - (Stunden * 1000 * 60 * 60) - (Minuten * 1000 * 60)) / 1000);
                            Millisekunden = (int) ((PlayerFX.mediaLength - (Stunden * 1000 * 60 * 60) - (Minuten * 1000 * 60) - (Sekunden * 1000)) / 100);

                            sfull = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(PlayerFX.mediaLength),
                                    TimeUnit.MILLISECONDS.toMinutes(PlayerFX.mediaLength) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(PlayerFX.mediaLength)),
                                    TimeUnit.MILLISECONDS.toSeconds(PlayerFX.mediaLength) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(PlayerFX.mediaLength)));

                            easytranscript.getMainSlider().setMaximum((int) PlayerFX.mediaLength / 1000);

                            currentPlayerTime = 0;
                            PlayerFX.getMediaPlayer().pause();
                            PlayerFX.getMediaPlayer().seek(new Duration(0));
                            easytranscript.getMainTimeLabel().setText("00:00:00");
                            easytranscript.getMainTimeMilliLabel().setText("-0");

                            easytranscript.getMainTotalLabel().setText(sfull + "-" + Millisekunden);

                            String stern = "";
                            if (TranscriptHandler.isUnsaved()) {
                                stern = "*";
                            }
                            easytranscript.setTitle("easytranscript - " + transcriptName + stern + " - " + easytranscript.getMainTimeLabel().getText() + " " + messages.getString("vonTime") + " " + sfull);

                        }
                    }
                });
                logger.log(Level.INFO, "onError ...");
                PlayerFX.getMediaPlayer().setOnError(new Runnable() {

                    @Override
                    public void run() {
                        new ErrorReport().show(messages.getString("mediaFXError") + PlayerFX.getMediaPlayer().getError().getLocalizedMessage());
                        logger.log(Level.SEVERE, "{0}", new Object[]{PlayerFX.getMediaPlayer().getError().getMessage()});

                    }

                });
                PlayerFX.getMediaPlayer().setOnStalled(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPath.startsWith("http")) {
                            easytranscript.getBufferingLabel().setText(messages.getString("Wait"));
                        }
                    }
                });
                logger.log(Level.INFO, "play ...");
                javafx.application.Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        if (PlayerFX.mediaViewFX == null) {
                            PlayerFX.setMediaViewFX(new javafx.scene.media.MediaView(PlayerFX.getMediaPlayer()));
                            PlayerFX.getAnchorPaneFX().setCenter(PlayerFX.getMediaViewFX());
                        } else {
                            PlayerFX.getMediaViewFX().setMediaPlayer(PlayerFX.getMediaPlayer());
                        }

                    }
                });
                logger.log(Level.INFO, "Abschluss ...");
                //  PlayerFX.getMediaPlayer().play();

                // falls er bei 70% blockiert, hier war irgendwo das problem.

                //   player.setMediaLoaded(true);
                logger.log(Level.INFO, "Abschluss!");
            }
        }

        return true;
    }

    /**
     * Speichert den Inhalt des Textfeldes in eine RTF Datei.
     *
     * @param path
     * @return path
     * @throws IOException Falls Datei nicht gefunden wurde
     * @throws BadLocationException Falls Intervall fehlerhaft
     */
    public static String exportRTF(String path) throws IOException, BadLocationException {

        FileOutputStream out = new FileOutputStream(path);
        Document doc = easytranscript.getMainCenterEditorEditorPane().getDocument();

        easytranscript.getMainCenterEditorEditorPane().getDocument().removeUndoableEditListener(undoManager);
        simulateZoom(doc, true);
        RTFEditorKit kit = new RTFEditorKit();
        kit.write(out, doc, doc.getStartPosition().getOffset(), doc.getLength());
        simulateZoom(doc, false);
        easytranscript.getMainCenterEditorEditorPane().getDocument().addUndoableEditListener(undoManager);

        out.close();

        return path;

    }

    /**
     * Speichert ein Backup des Transkripts mit Zeitstempel in den Backup Ordner
     * des Projektes, falls backupEnabled true ist.
     */
    public static void saveBackup() {

        SwingWorker worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                if (easytranscript.getMainCenterEditorEditorPane().getDocument().getLength() != 0) {
                    try {

                        if (prop.getBoolProperty("backupEnabled")) {
                            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
                            Date date = new Date();

                            if (!new File(transcriptPath.substring(0, transcriptPath.lastIndexOf(".")) + "_Data" + System.getProperty("file.separator") + "Backups" + System.getProperty("file.separator")).exists()) {
                                new File(transcriptPath.substring(0, transcriptPath.lastIndexOf(".")) + "_Data" + System.getProperty("file.separator") + "Backups" + System.getProperty("file.separator")).mkdirs();
                                logger.log(Level.WARNING, "Backup-Ordner und evtl Eltern-Ordner mussten erst erstellt werden!");
                            }

                            exportRTF(transcriptPath.substring(0, transcriptPath.lastIndexOf(".")) + "_Data" + System.getProperty("file.separator") + "Backups" + System.getProperty("file.separator") + documentName + "_" + dateFormat.format(date) + ".rtf");

                        }

                    } catch (IOException | BadLocationException ex) {
                        logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("BackupFailed")), ex);
                    }
                }
                return null;
            }

        };

        worker.execute();

    }

    /**
     * Speichert ein Backup des Transkripts mit Zeitstempel und Prefix in den
     * Backup-Ordner des Projektes, falls backupEnabled true ist.
     *
     * @param preT das Prefix, welches an den Dateinamen vorweg gehangen wird.
     */
    public static void saveBackup(String preT) {
        final String pre = preT;
        SwingWorker worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                if (easytranscript.getMainCenterEditorEditorPane().getDocument().getLength() != 0) {
                    try {
                        if (prop.getBoolProperty("backupEnabled")) {
                            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
                            Date date = new Date();

                            exportRTF(transcriptPath.substring(0, transcriptPath.lastIndexOf(".")) + "_Data" + System.getProperty("file.separator") + "Backups" + System.getProperty("file.separator") + pre + "_" + documentName + "_" + dateFormat.format(date) + ".rtf");

                        }

                    } catch (IOException | BadLocationException ex) {
                        logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("BackupFailed2")), ex);
                    }
                }
                return null;
            }
        };
        worker.execute();

    }

    public static void save(final String path, final boolean readIt, final boolean saveAs) {
        save(path, readIt, saveAs, null);
    }

    /**
     * Speichert ein Projekt. Wahlweise kann hier der Save-Dialog aufgerufen
     * werden und nach erfolgreichem Speichern das Projekt gelesen werden.
     *
     * @param path Pfad in welchen gespeichert werden soll.
     * @param readIt Soll das Projekt gelesen werden?
     * @param saveAs Speicherdialog anzeigen?
     */
    public static void save(final String path, final boolean readIt, final boolean saveAs, Callable<Void> callAfter) {

        final SwingWorker worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                try {
                    writeProjConf(path);
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("FailedtoSaveProjectConf")), ex);

                    return "";
                }

                String transcriptNameTmp = transConf.getProperty("transcriptName");
                String transcriptPathTmp = transConf.getProperty("transcriptPath");

                setProgress(40);

                if (saveAs && !transcriptPath.equals(path)) {
                    if (new File(transcriptPath.substring(0, transcriptPath.lastIndexOf(".")) + "_Data").exists()) {
                        try {
                            if (!(transcriptPath.substring(0, transcriptPath.lastIndexOf(".")) + "_Data").equals(path.substring(0, path.lastIndexOf(".")) + "_Data")) {
                                FileUtils.copyDirectory(new File(transcriptPath.substring(0, transcriptPath.lastIndexOf(".")) + "_Data"), new File(path.substring(0, path.lastIndexOf(".")) + "_Data"));
                            }
                            //
                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("FailedToCopyProjectFolder")), ex);

                        }
                    } else {
                        //   writeProjDirs(path);
                    }
                }
                setProgress(70);
                if (transcriptPathTmp != null) {
                    if (!transcriptPathTmp.equals("")) {
                        try {
                            exportRTF(transcriptPathTmp);
                        } catch (IOException | BadLocationException ex) {
                            logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("FaileedToExportRTF")), ex);
                        }
                    } else {

                        String pfad = path.substring(0, path.lastIndexOf(".")) + "_Data/" + transcriptNameTmp + ".rtf";
                        try {
                            exportRTF(pfad);
                        } catch (IOException | BadLocationException ex) {
                            logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("FaileedToExportRTF")), ex);
                        }
                    }
                }

                setUnsaved(false);
                backupNeeded = false;
                setProgress(100);

                if (readIt) {
                    read(path);
                }
                return "";

            }

            @Override
            protected void done() {

                Timer timer = new Timer(1000, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        workerFrame.setVisible(false);
                    }
                });
                timer.setRepeats(false);
                workerFrame.getWorkerProgressbar().setString(messages.getString("ProjectWorkerSaved"));
                timer.start();
                if (callAfter != null) {
                    try {
                        callAfter.call();
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Could not call callable.", e);
                    }
                }

            }
        };
        worker.addPropertyChangeListener(new WorkerListener());
        workerFrame.setVisible(true);
        workerFrame.toFront();
        workerFrame.repaint();
        workerFrame.setLocationRelativeTo(null);
        worker.execute();

    }

    /**
     * Schreibt die Projektkonfigurationsdatei in den angegebenen Pfad.
     *
     * @param path
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void writeProjConf(String path) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(path);
        transConf.store(fos, null);
        fos.close();
    }

    /**
     * Erstellt die notwendigen Projektordner nach Angabe des
     * Projektdateipfades.
     *
     * @param path Pfad des Projektes
     */
    public static void writeProjDirs(String path) {
        String name = path.substring(path.lastIndexOf(System.getProperty("file.separator")) + 1, path.lastIndexOf("."));
        String path_data = path.substring(0, path.lastIndexOf(System.getProperty("file.separator"))) + "/" + name + "_Data/";

        if (new File(path_data).exists()) {

            logger.log(Level.WARNING, "Projektdatenordner existiert bereits. Wird überschrieben.");

            try {
                if (!useFXPlayer) {
                    player.getMediaPlayer().stop();
                } else {
                    PlayerFX.getMediaPlayer().pause();
                    PlayerFX.getMediaPlayer().stop();
                }
                FileUtils.deleteDirectory(new File(path_data));

            } catch (IOException ex) {
                logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("ErrorDeletingOldProjectFolder")), ex);

            }
        }

        new File(path_data).mkdir();
        new File(path_data + "Backups").mkdir();

    }

    /**
     * Liest ein Projekt nach Angabe des Projektdateipfades.
     *
     * @param path Projektdateipfad
     */
    public static void read(final String path) {

        SwingWorker worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                boolean fehler = false;

                startRecentUsed = null;

                String sep = "\\";
                boolean contains = path.contains("\\");
                if (contains == false) {
                    sep = "/";
                }

                close();

                workerFrame.setVisible(true);
                workerFrame.toFront();
                workerFrame.repaint();
                workerFrame.setLocationRelativeTo(null);
                setProgress(0);

                setProjDepButtonsVisible(true);
                easytranscript.getMainCenterEditorEditorPane().setEnabled(true);

                File project = new File(path);

                if (!project.exists()) {
                    logger.log(Level.WARNING, "Die Projektdatei existiert nicht mehr.");

                    return "unterbrochen";
                }

                String projectNameTmp = path.substring(path.lastIndexOf(sep) + 1, path.lastIndexOf("."));
                transcriptName = projectNameTmp;
                transcriptPath = path;
                String projectDataS = path.substring(0, path.lastIndexOf(sep) + 1) + projectNameTmp + "_Data";
                File projectData = new File(projectDataS);

                setProgress(10);
                if (!projectData.exists()) {
                    logger.log(Level.WARNING, "Die Projektdatenordner existiert nicht mehr.");
                    return "unterbrochen";
                }

                transConf.clear();
                try {
                    transConf.load(new FileInputStream(path));

                } catch (IOException ex) {
                    logger.log(Level.WARNING, "Die Projektdatei existiert nicht mehr: {0}", ex.getLocalizedMessage());
                    return "unterbrochen";
                }

                if (transConf.getProperty("transcriptName") != null) {
                    if (!transConf.getProperty("transcriptName").equals("")) {

                        String name = transConf.getProperty("transcriptName");
                        if (new File(projectDataS + sep + name + ".rtf").exists()) {
                            readDocument(projectDataS + sep + name + ".rtf");
                        } else {
                            JOptionPane.showMessageDialog(null, messages.getString("TranscriptFileNotExists"), messages.getString("Error"), JOptionPane.ERROR_MESSAGE);
                            projectCorrupt = 1;

                            fehler = true;
                        }
                    } else if (transConf.getProperty("transcriptPath") != null) {
                        if (new File(transConf.getProperty("transcriptPath")).exists()) {
                            readDocument(transConf.getProperty("transcriptPath"));
                        } else {
                            JOptionPane.showMessageDialog(null, messages.getString("TranscriptFileNotExists"), messages.getString("Error"), JOptionPane.ERROR_MESSAGE);
                            fehler = true;
                            projectCorrupt = 1;

                        }
                    } else {
                        fehler = true;
                        projectCorrupt = 1;
                    }
                } else if (transConf.getProperty("transcriptPath") != null) {
                    if (new File(transConf.getProperty("transcriptPath")).exists()) {
                        readDocument(transConf.getProperty("transcriptPath"));
                    } else {
                        JOptionPane.showMessageDialog(null, messages.getString("TranscriptFileNotExists"), messages.getString("Error"), JOptionPane.ERROR_MESSAGE);
                        fehler = true;
                        projectCorrupt = 1;

                    }
                } else {
                    fehler = true;
                    projectCorrupt = 1;
                }

                setProgress(70);
                logger.log(Level.INFO, "Betrete 70 Prozentbereich");
                if (transConf.getProperty("mediaPath") != null) {
                    if (!transConf.getProperty("mediaPath").equals("")) {

                        if (new File(transConf.getProperty("mediaPath")).exists() || transConf.getProperty("mediaPath").startsWith("http")) {

                            openMedia(transConf.getProperty("mediaPath"));
                        } else {
                            JOptionPane.showMessageDialog(null, messages.getString("MediaFileNotExists"), messages.getString("Error"), JOptionPane.ERROR_MESSAGE);
                            fehler = true;
                            if (projectCorrupt == 1) {
                                projectCorrupt = 2;
                            }
                            if (projectCorrupt == 0) {
                                projectCorrupt = 3;
                            }

                        }
                    } else {
                        String name = transConf.getProperty("mediaName");
                        if (new File(projectDataS + sep + name).exists()) {
                            openMedia(projectDataS + sep + name);
                        } else {
                            JOptionPane.showMessageDialog(null, messages.getString("MediaFileNotExists"), messages.getString("Error"), JOptionPane.ERROR_MESSAGE);
                            fehler = true;
                            if (projectCorrupt == 1) {
                                projectCorrupt = 2;
                            }
                            if (projectCorrupt == 0) {
                                projectCorrupt = 3;
                            }

                        }
                    }
                } else {
                    String name = transConf.getProperty("mediaName");
                    if (new File(projectDataS + sep + name).exists()) {
                        openMedia(projectDataS + sep + name);
                    } else {
                        JOptionPane.showMessageDialog(null, messages.getString("MediaFileNotExists"), messages.getString("Error"), JOptionPane.ERROR_MESSAGE);
                        fehler = true;
                        if (projectCorrupt == 1) {
                            projectCorrupt = 2;
                        }
                        if (projectCorrupt == 0) {
                            projectCorrupt = 3;
                        }

                    }
                }
                logger.log(Level.INFO, "Worktime ...");
                try {
                    workTime.loadWorkTime();
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Fehler: " + e.getMessage(), e);
                }
                setProgress(100);
                logger.log(Level.INFO, "100%!");
                workerFrame.setVisible(false);
                recentUsed.addRecentUsed(path);
                return String.valueOf(fehler);

            }

            @Override
            protected void done() {
                String getter = "";
                boolean fehler = false;

                workerFrame.setVisible(false);
                workerFrame.getWorkerProgressbar().setValue(0);

                try {
                    getter = this.get();
                } catch (InterruptedException | ExecutionException ex) {
                    logger.log(Level.WARNING, ("Das Ergebnis des Ladevorgangs konnte nicht ausgewertet werden."), ex);

                }

                if (!useFXPlayer) {
                    if (player.getMediaPlayer().isPlaying()) {
                        //           player.getMediaPlayer().setPause(true);
                        currentPlayerTime = 0;
                        easytranscript.getMainSlider().setValue(0);
                        easytranscript.getMainTimeLabel().setText("00:00:00");
                        easytranscript.getMainTimeMilliLabel().setText("-0");
                        Millisekunden_current = 0;
                        SekundenC_string = "00";
                        StundenC_string = "00";
                        MinutenC_string = "00";

                    }
                } else {

                    currentPlayerTime = 0;
                    easytranscript.getMainSlider().setValue(0);
                    easytranscript.getMainTimeLabel().setText("00:00:00");
                    easytranscript.getMainTimeMilliLabel().setText("-0");
                    Millisekunden_current = 0;
                    SekundenC_string = "00";
                    StundenC_string = "00";
                    MinutenC_string = "00";

                    easytranscript.getMainTimePlayerplayButton().setIcon(PlayerFX.start);

                }
                if (getter.equals("unterbrochen")) {

                    JOptionPane.showMessageDialog(null, messages.getString("ProjectReadError2"), messages.getString("Error"), JOptionPane.ERROR_MESSAGE);
                    recentUsed.resetRecentEntry(path);
                    close();
                } else {
                    fehler = Boolean.parseBoolean(getter);
                }

                if (fehler) {
                    logger.log(Level.WARNING, "Das Projekt scheint unlesbar oder nicht vollständig zu sein");
                    JOptionPane.showMessageDialog(null, messages.getString("ProjectReadError"), messages.getString("Error"), JOptionPane.ERROR_MESSAGE);
                    workTime.closeWorkTimeWindow();
                    changeProjectFrame.toggleChangeProjProp();

                } else {

                    if (transConf.getProperty("PPorganizeTime") != null) {
                        zeitFrame.getZeitActivateCheckbox().setSelected(Boolean.valueOf(transConf.getProperty("PPorganizeTime")));
                    }

                    easytranscript.activateElementsAfterTranscriptRead();
                    workTime.setRecordingTime(false);
                    unsaved = false;
                    backupNeeded = false;
                    easytranscript.adjustSplitter();
                    checkForNewerBackup();

                    if (!projFolder.isInList(transcriptPath) && !projFolder.getPath().equals("")) {
                        int value = JOptionPane.showConfirmDialog(null, messages.getString("addTrtoPr"), "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                        if (value == JOptionPane.YES_OPTION) {
                            File[] file = new File[1];
                            file[0] = new File(transcriptPath);
                            ProjectFrame.addFilesToFolder(file, ProjectHandler.TYPE_PROJECT);
                            projectFolderFrame.toFront();
                        }

                    }


                    //  for (DocumentListener dl : list){
                    //     easytranscript.getMainCenterEditorEditorPane().getDocument().removeDocumentListener(dl);
                    // }
                    // if (list.length>2){
                    //     easytranscript.getMainCenterEditorEditorPane().getDocument().removeDocumentListener(list[list.length-1]);
                    // }

                    if (!added) {
                        logger.log(Level.INFO, "Adding textfield listener to editor.");
                        easytranscript.getMainCenterEditorEditorPane().getDocument().addDocumentListener(new TextfieldListener());
                        added = true;
                    } else {
                        logger.log(Level.INFO, "Textfield listener already added to editor.");
                    }
                    Suggestions.build(easytranscript.getMainCenterEditorEditorPane().getDocument());

                }

            }
        };

        worker.addPropertyChangeListener(new WorkerListener());
        worker.execute();

    }

    /**
     * Checkt, ob es eine Backup-Datei gibt, welche neuer als das jetzige
     * Transkript ist und fragt ggf, ob es ersetzt werden soll.
     */
    private static void checkForNewerBackup() {
        if (prop.getBoolProperty("backupEnabled")) {

            File dir = new File(transcriptPath.substring(0, transcriptPath.lastIndexOf(".")) + "_Data" + System.getProperty("file.separator") + "Backups" + System.getProperty("file.separator"));//+ transcriptName + "_";
            File[] list = dir.listFiles();

            Arrays.sort(list, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                }
            });

            File current = new File(transcriptPath.substring(0, transcriptPath.lastIndexOf(".")) + "_Data" + System.getProperty("file.separator") + documentName);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            for (int i = list.length - 1; i > 0; i--) {

                if (list[i].isFile() && list[i].getName().startsWith(documentName) && list[i].lastModified() > current.lastModified()) {

                    int value = JOptionPane.showConfirmDialog(null, messages.getString("newerBackup") + formatter.format(new Date(current.lastModified())) + " \n" + messages.getString("Backup") + " " + formatter.format(new Date(list[i].lastModified())));

                    if (value == JOptionPane.YES_OPTION) {
                        saveBackup("NB");
                        try {
                            org.apache.commons.io.FileUtils.copyFile(list[i], current);
                        } catch (IOException ex) {
                            Logger.getLogger(TranscriptHandler.class.getName()).log(Level.SEVERE, null, ex);
                            new ErrorReport().show(errors.getString("BackupNB"));
                        }
                    }

                    readDocument(current.getAbsolutePath());

                    break;
                }
            }

        }
    }

    /**
     * Schließt das aktuelle Projekt und setzt alle notwendigen Werte wieder
     * zurück.
     */
    public static void close() {

        if (workTime.getRecordingTime() == true) {
            workTime.endCurrentWorkTimeEntry();
            workTime.setRecordingTime(false);
        }

        transConf.setProperty("PPorganizeTime", String.valueOf(zeitFrame.getZeitActivateCheckbox().isSelected()));

        if (!useFXPlayer) {
            player.getMediaPlayer().stop();
        } else {
            javafx.application.Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (PlayerFX.getMediaPlayer() != null) {
                        PlayerFX.getMediaPlayer().stop();
                        PlayerFX.getMediaPlayer().dispose();
                    }
                }
            });
        }
        transcriptPath = "";
        TransConfTmp.clear();
        transConf.clear();
        clearProjWindowValues();
        easytranscript.getMainCenterEditorEditorPane().setText("");

        if (!useFXPlayer) {
            player.setMediaLoaded(false);
        }
        Stunden_current = 0;
        Minuten_current = 0;
        Sekunden_current = 0;
        Millisekunden_current = 0;
        SekundenC_string = "00";
        StundenC_string = "00";
        MinutenC_string = "00";

        unsaved = false;
        backupNeeded = false;

        projectCorrupt = 0;
        transcriptName = "";
        documentName = "";
        mediaPath = "";
        PPprojNameWarning = false;
        PPtransNameWarning = false;
        PPtransNameWarning1 = false;
        currentPlayerTime = 0;
        workTime.setTotalTime(0);
        easytranscript.getMainTimeLabel().setText("00:00:00");
        easytranscript.getMainTimeMilliLabel().setText("-0");
        easytranscript.getMainTotalLabel().setText("00:00:00-0");

        easytranscript.getMainCenterSplitpane().setDividerLocation(1.0d);
        easytranscript.getMainSlider().setValue(0);
        PlayerFX.mediaLength = 0;

        setProjDepButtonsVisible(false);
    }

    /**
     * Erstellt aus der ProjConfTmp Properties, welche zuvor im "Neues Projekt
     * erstellen" Dialog angelegt wurde, ein neues Projekt.
     */
    public static void create() {
        SwingWorker worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                setProgress(0);
                newProjectFrame.setVisible(false);

                if (newProjectFrame.getNPtransCreateRadiobutton().isSelected()) {
                    TransConfTmp.setProperty("transcriptPath", "");
                } else {
                    TransConfTmp.setProperty("transcriptName", "");
                }

                transConf.putAll(TransConfTmp);

                TransConfTmp.clear();

                String path;
                setProgress(10);

                if (newProjectFrame.getNPprojProgrammVerwRadiobutton().isSelected()) {
                    // path = new File("").getAbsolutePath() + System.getProperty("file.separator") + "Projects" + System.getProperty("file.separator") + PPprojProgrammTf.getText();

                    path = opFolder + "Projects" + System.getProperty("file.separator") + newProjectFrame.getNPprojProgrammTextfield().getText();
                    if (!path.endsWith(".etp")) {
                        path = path + ".etp";
                    }
                } else {
                    path = newProjectFrame.getNPprojSpeicherortTextfield().getText();
                }

                writeProjDirs(path);
                setProgress(20);

                // RTF erstellen
                if (newProjectFrame.getNPtransCreateRadiobutton().isSelected()) {
                    FileWriter fstream = null;
                    try {
                        String text = newProjectFrame.getNPtransCreateTextfield().getText();
                        if (text.endsWith(".rtf") == false) {
                            text = text + ".rtf";
                        }
                        fstream = new FileWriter(path.substring(0, path.lastIndexOf(".")) + "_Data/" + text);
                        BufferedWriter out = new BufferedWriter(fstream);
                        out.write("");

                        fstream.close();
                        transConf.setProperty("transcriptName", newProjectFrame.getNPtransCreateTextfield().getText());
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("WritingRTFFailed")), ex);
                    } finally {
                        try {
                            fstream.close();
                        } catch (IOException ex) {
                            logger.log(Level.WARNING, "Der Stream konnte nicht geschlossen werden.", ex);

                        }
                    }

                } else // RTF kopieren
                    if (newProjectFrame.getNPtranscopyTrtoProjCheckbox().isSelected() == true) {

                        File file = new File(transConf.getProperty("transcriptPath"));

                        try {

                            File dest = new File(path.substring(0, path.lastIndexOf(".")) + "_Data" + System.getProperty("file.separator"));

                            FileUtils.copyFileToDirectory(file, dest);

                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("FailedCopyingTranscript")), ex);

                        }
                    }
                setProgress(40);
                // Mediendatei kopieren
                if (newProjectFrame.getNPmediumCopyMedtoProjCheckbox().isSelected() == true) {
                    File file = new File(transConf.getProperty("mediaPath"));
                    try {

                        File dest = new File(path.substring(0, path.lastIndexOf(".")) + "_Data" + System.getProperty("file.separator"));

                        FileUtils.copyFileToDirectory(file, dest);

                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("FailedCopyingMedia")), ex);

                    }
                }
                setProgress(80);
                // ITP schreiben

                if (!newProjectFrame.getNPtransCreateRadiobutton().isSelected()) {
                    if (newProjectFrame.getNPtranscopyTrtoProjCheckbox().isSelected() == true) {
                        transConf.setProperty("transcriptName", "");
                        transConf.setProperty("transcriptPath", "");
                        String tmp = newProjectFrame.getNPtransReadTextfield().getText();
                        transConf.setProperty("transcriptName", tmp.substring(tmp.lastIndexOf(System.getProperty("file.separator")) + 1, tmp.lastIndexOf(".")));
                    }
                }
//

                if (newProjectFrame.getNPmediumCopyMedtoProjCheckbox().isSelected() == true) {
                    transConf.setProperty("mediaPath", "");
                    transConf.setProperty("mediaName", "");
                    String tmp = newProjectFrame.getNPmediumPathTextfield().getText();
                    transConf.setProperty("mediaName", tmp.substring(tmp.lastIndexOf(System.getProperty("file.separator")) + 1));
                }
                setProgress(100);
                workerFrame.setVisible(false);
                return path;
            }

            @Override
            protected void done() {

                try {

                    writeProjConf(get());
                } catch (FileNotFoundException ex) {
                    logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("FailedSavingProject") + ": " + ex.getMessage()), ex);

                } catch (IOException ex) {

                    logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("FailedExecutingWorker")), ex);

                } catch (InterruptedException ex) {
                    logger.log(Level.WARNING, "Der Worker wurde unterbrochen.", ex);
                } catch (ExecutionException ex) {

                    logger.log(Level.SEVERE, "Fehler bei der Ausführung des Workers.", ex);

                }

                transConf.clear();
                try {
                    read(get());
                } catch (InterruptedException | ExecutionException ex) {

                    logger.log(Level.SEVERE, "Fehler beim Abruf der Workerausgabe.", ex);

                }

            }
        };

        worker.addPropertyChangeListener(new WorkerListener());
        workerFrame.setVisible(true);
        workerFrame.toFront();
        workerFrame.repaint();
        workerFrame.setLocationRelativeTo(null);
        worker.execute();

    }

    /**
     * Schaltet notwendige Schaltflächen, um an einem Projekt zu arbeiten.
     *
     * @param b Anzeigen oder nicht anzeigen.
     */
    private static void setProjDepButtonsVisible(boolean b) {
        easytranscript.getMainCenterEditorEditorPane().setVisible(b);

        if (useFXPlayer) {
            PlayerFX.getFXPanel().setVisible(b);
        }
        easytranscript.getMainCenterEditorEditorPane().setEnabled(b);
        easytranscript.getMaineditMenu().setEnabled(b);
        easytranscript.getMaintoolsMenu().setEnabled(b);
        easytranscript.getMainSaveMenuItem().setEnabled(b);
        easytranscript.getMainSaveAsMenuItem().setEnabled(b);
        easytranscript.getMainchangeProjectMenuItem().setEnabled(b);
        startFrame.setVisible(false);
        easytranscript.getMainProjectInfosMenuitem().setEnabled(b);
        easytranscript.getMainprintMenuitem().setEnabled(b);
        easytranscript.getMaincloseProjectMenuitem().setEnabled(b);
        easytranscript.getMainToolbarSaveButton().setEnabled(b);
        easytranscript.getMainexportMenu().setEnabled(b);
        easytranscript.getMainToolbarSaveAsButton().setEnabled(b);
        easytranscript.getMainButtonPanel().setVisible(b);
        easytranscript.getMainSliderPanel().setVisible(b);
        easytranscript.getMainToolbar().setVisible(b);
        easytranscript.getMainCenterSplitpane().setEnabled(b);
        easytranscript.getMainCenterVideoInternalframe().setVisible(b);

    }

    /**
     * Liefert zurück, ob das Transkript ungespeicherte Änderungen enthält.
     *
     * @return unsaved
     */
    public static boolean isUnsaved() {
        return unsaved;
    }

    /**
     * Setzt den unsaved Wert
     *
     * @param unsaved unsaved
     */
    public static void setUnsaved(boolean unsaved) {
        boolean oldB = isUnsaved();
        TranscriptHandler.unsaved = unsaved;

        if (unsaved == true && oldB != unsaved) {

            String stern = "*";

            easytranscript.setTitle("easytranscript - " + transcriptName + stern + " - " + easytranscript.getMainTimeLabel().getText() + " " + messages.getString("vonTime") + " " + easytranscript.getMainTotalLabel().getText());
        }

        if (unsaved == false && oldB != unsaved) {

            easytranscript.setTitle("easytranscript - " + transcriptName + " - " + easytranscript.getMainTimeLabel().getText() + " " + messages.getString("vonTime") + " " + easytranscript.getMainTotalLabel().getText());

        }

    }

    /**
     * Liefert zurück, ob ein Backup benötigt wird.
     *
     * @return backupNeeded
     */
    public static boolean isBackupNeeded() {
        return backupNeeded;
    }

    /**
     * Setzt fest, ob ein Backup benötigt wird.
     *
     * @param backupNeeded backupNeeded
     */
    public static void setBackupNeeded(boolean backupNeeded) {
        TranscriptHandler.backupNeeded = backupNeeded;
    }

    public static JEditorPane simulateZoom(Document doc, boolean revert) {
        if (prop.getBoolProperty("usePerformanceMode")) {

            int pos = 0;
            boolean changed = false;

            while (pos != (doc.getLength() - 1)) {

                int oldPos = pos;
                int fontsize = (int) ((StyledDocument) doc).getCharacterElement(pos).getAttributes().getAttribute(StyleConstants.FontSize);
                SimpleAttributeSet attrs = new SimpleAttributeSet();
                if (fontsize < 8) {
                    fontsize = 12;
                }
                if (!revert) {
                    StyleConstants.setFontSize(attrs, fontsize + V.performanceModeFontSizeIncrease);
                } else {
                    StyleConstants.setFontSize(attrs, fontsize - V.performanceModeFontSizeIncrease);
                }
                for (int i = pos + 1; i < doc.getLength(); i++) {

                    pos++;
                    int fontsize2 = (int) ((StyledDocument) doc).getCharacterElement(pos).getAttributes().getAttribute(StyleConstants.FontSize);
                    if (fontsize != fontsize2) {

                        ((StyledDocument) doc).setCharacterAttributes(oldPos, pos - oldPos, attrs, false);
                        changed = true;
                        break;
                    }

                }
                if (!changed) {

                    ((StyledDocument) doc).setCharacterAttributes(0, doc.getLength(), attrs, false);
                    break;
                }

            }

        }
        return easytranscript.getMainCenterEditorEditorPane();
    }

    /**
     * PropertychangeListener Klasse, welche die WorkerProgressbar beim Laden
     * des Projektes updated.
     */
    private static class WorkerListener implements PropertyChangeListener {

        /**
         * Führt aus, wenn sich der progress Wert verändert hat
         *
         * @param evt nicht benötigt
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("progress".equals(evt.getPropertyName())) {
                int progress = (Integer) evt.getNewValue();
                workerFrame.getWorkerProgressbar().setValue(progress);
                workerFrame.getWorkerProgressbar().setString(String.valueOf(progress) + "%");

            }
        }

    }

}
