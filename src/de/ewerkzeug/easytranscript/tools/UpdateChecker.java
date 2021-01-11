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
package de.ewerkzeug.easytranscript.tools;

import de.ewerkzeug.easytranscript.core.Easytranscript;
import de.ewerkzeug.easytranscript.core.Variables;
import static de.ewerkzeug.easytranscript.core.Variables.UPDATECHANNEL;
import static de.ewerkzeug.easytranscript.core.Variables.VERSION;
import static de.ewerkzeug.easytranscript.core.Variables.logger;
import static de.ewerkzeug.easytranscript.core.Variables.messages;
import static de.ewerkzeug.easytranscript.core.Variables.opFolder;
import static de.ewerkzeug.easytranscript.core.Variables.prop;
import static de.ewerkzeug.easytranscript.core.Variables.updVersion;
import static de.ewerkzeug.easytranscript.core.Variables.updateFrame;
import de.ewerkzeug.easytranscript.core.Version;
import de.ewerkzeug.easytranscript.core.VersionState;
import static de.ewerkzeug.easytranscript.tools.Tools.extractArchive;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.apache.commons.io.FileUtils;

/**
 * SwingWorker. Checkt auf Updates
 */
public class UpdateChecker extends SwingWorker<Version, Void> {

    boolean res;
    Version update = new Version("0.00.0");

    /**
     * Setzt, ob das Resultat der Suche angezeigt werden soll
     *
     * @param showResult
     */
    public UpdateChecker(boolean showResult) {
        this.res = showResult;
    }

    /**
     * Installiert Updater Updates, checkt auf easytranscript Updates.
     *
     * @return
     */
    @Override
    protected Version doInBackground() {

        String channel = prop.getProperty("updatechannel");

        if (channel != null) {
            logger.log(Level.INFO, "Found update channel in prop: {0}", channel);
            UPDATECHANNEL = VersionState.getValue(channel);
        }

        URL website;

        if (new File("updateUpd.zip").exists()) {
           
            try {

                logger.info("Installiere neue Updater-Version.");

                if (new File("updater_old.jar").exists() && new File("updater.jar").exists()) {

                    new File("updater_old.jar").delete();

                }
                new File("updater.jar").renameTo(new File("updater_old.jar"));
                extractArchive(new File("updateUpd.zip"), new File(new File("").getAbsolutePath()));
                if (new File("updater.jar").exists() && new File("updater_old.jar").exists()) {
                    new File("updater_old.jar").delete();
                }
                new File("updateUpd.zip").delete();

            } catch (IOException ex) {
                logger.log(Level.WARNING, "Fehler beim entpacken des Updaterarchivs.", ex);
                new File("updater_old.jar").renameTo(new File("updater.jar"));
                if (new File(new File("").getAbsolutePath() + System.getProperty("file.separator") + "easytranscript-admin.exe").exists()) {

                    int n = JOptionPane.showConfirmDialog(null, messages.getString("needsToRunAsAdmin"), "", JOptionPane.YES_NO_OPTION);
                    if (n == JOptionPane.YES_OPTION) {
                        ProcessBuilder builder = new ProcessBuilder(new String[]{"cmd.exe", "/C", "easytranscript-admin.exe"});
                        
                        try {
                            builder.start();
                        } catch (IOException ex1) {
                            Logger.getLogger(UpdateChecker.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                        Easytranscript.terminate();
                    } else {
                        return new Version("0.00.0");
                    }
                } else {
                    JOptionPane.showConfirmDialog(null, messages.getString("needsToRunAsAdminGeneric"), "", JOptionPane.OK_OPTION);
                    return new Version("0.00.0");
                }
            }

        }

        if (new File("lib_bak").exists() && new File("lib").exists()) {
            try {
                FileUtils.deleteDirectory(new File("lib_bak"));
            } catch (IOException ex) {
                Logger.getLogger(Easytranscript.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (new File("lib_bak").exists() == true && new File("lib").exists() == false) {
            new File("lib_bak").renameTo(new File("lib"));
        }

        try {

            String path = "http://e-werkzeug.eu/software/easytranscript/updates/" + VersionState.getString(Variables.UPDATECHANNEL) + "/version";

            if (!checkIfURLExists(path)) {
                logger.log(Level.WARNING, "{0} version Datei existiert nicht.", VersionState.getString(Variables.UPDATECHANNEL));
                path = "http://e-werkzeug.eu/software/easytranscript/updates/final/version";
            }

            new File(opFolder+"EWET_version.txt").delete();

            new File(opFolder+"EWET_version" + VersionState.PREALPHA + ".txt").delete();
            new File(opFolder+"EWET_version" + VersionState.ALPHA + ".txt").delete();
            new File(opFolder+"EWET_version" + VersionState.BETA + ".txt").delete();
            new File(opFolder+"EWET_version" + VersionState.GAMMA + ".txt").delete();

            if ((Variables.UPDATECHANNEL != VersionState.getValue(VersionState.FINAL))) {
                website = new URL(path);
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());

                FileOutputStream fos = new FileOutputStream(opFolder+"EWET_version" + VersionState.getString(Variables.UPDATECHANNEL) + ".txt");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                rbc.close();
                fos.close();
                path = "http://e-werkzeug.eu/software/easytranscript/updates/final/version";
            }

            website = new URL(path);
            ReadableByteChannel rbc2 = Channels.newChannel(website.openStream());

            FileOutputStream fos2 = new FileOutputStream(opFolder+"EWET_version.txt");
            fos2.getChannel().transferFrom(rbc2, 0, Long.MAX_VALUE);
            fos2.close();
            rbc2.close();

        } catch (MalformedURLException ex) {
            Logger.getLogger(Easytranscript.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            if (res) {
                JOptionPane.showMessageDialog(null, "easytranscript (Version " + VERSION.toString() + ")" + messages.getString("EasytranscriptUpdNoConnection"), "Update", JOptionPane.WARNING_MESSAGE);
                return new Version("0.00.0");
            }
        }

        try {
            if (new File(opFolder+"EWET_version" + VersionState.getString(Variables.UPDATECHANNEL) + ".txt").exists()) {
                BufferedReader in2 = new BufferedReader(new FileReader(opFolder+"EWET_version" + VersionState.getString(Variables.UPDATECHANNEL) + ".txt"));
                String u = in2.readLine();
                in2.close();

                BufferedReader in3 = new BufferedReader(new FileReader(opFolder+"EWET_version.txt"));
                String u2 = in3.readLine();
                in3.close();

                int r = new Version(u).compareTo(new Version(u2));

                if (r == 1) {
                    new File(opFolder+"EWET_version.txt").delete();
                    new File(opFolder+"EWET_version" + VersionState.getString(Variables.UPDATECHANNEL) + ".txt").renameTo(new File("version.txt"));
                } else {
                    new File(opFolder+"EWET_version" + VersionState.getString(Variables.UPDATECHANNEL) + ".txt").delete();
                }
            }

            BufferedReader in = new BufferedReader(new FileReader(opFolder+"EWET_version.txt"));

            String updateString = in.readLine();
            update = new Version(updateString);

            logger.log(Level.FINE, "Update found: {0}", update.toString());
            in.readLine();

            updVersion = "minor";

            while (true) {

                try {
                    Version majorT = new Version(in.readLine());

                    if (majorT.compareTo(VERSION) == 1) {
                        updVersion = "major";
                        in.close();

                        break;

                    }
                } catch (EOFException e) {

                }
            }

        } catch (IOException e) {
            logger.log(Level.WARNING, "Programm kann nicht auf Updates überprüft werden, da version.txt nicht ausgewertet werden konnte.", e);
            if (res) {
                JOptionPane.showMessageDialog(null, "easytranscript (Version " + VERSION.toString() + ")" + messages.getString("EasytranscriptUpdNoConnection"), "Update", JOptionPane.WARNING_MESSAGE);
            }

        }
        return update;
    }

    /**
     * Informationsfenster für das Ergebnis der Suche
     */
    @Override
    protected void done() {

        if (update.compareTo(VERSION) > 0 && UPDATECHANNEL <= VersionState.getValue(update.getVersionState())) {

            updateFrame.getUFinfoTextarea().setText("");
            updateFrame.getUFheaderLabel().setText("Version " + update.toString() + " " + messages.getString("UpdateIsAvailable"));

            if (updVersion.equals("minor")) {
                updateFrame.getUFinfoTextarea().append(messages.getString("UpdAvail1"));
            } else {
                updateFrame.getUFinfoTextarea().append(messages.getString("UpdAvail2"));
            }
            updateFrame.getUFinfoTextarea().append(messages.getString("UpdAvail3"));

            new File(opFolder+"EWET_version.txt").delete();
            updateFrame.setLocationRelativeTo(null);
            updateFrame.setVisible(true);
            updateFrame.toFront();
            updateFrame.repaint();

        } else if (update.compareTo(VERSION) <= 0 && UPDATECHANNEL <= VersionState.getValue(update.getVersionState())) {
            logger.log(Level.FINE, "easytranscript ist aktuell. Letzte server Version: {0}", update.toString());

            if (res) {

                if (update.getMajorVersion() != 0) {

                    JOptionPane.showMessageDialog(null, "easytranscript (Version " + VERSION.toString() + messages.getString("easytranscriptIsUpd"));
                }
            }
            //  }else if ((update.compareTo(VERSION) < 0)){
            //!DOWNGRADE AUSGESETZT 
//            if (update.atLeast(new Version("1.0"))){
//            logger.log(Level.WARNING, "easytranscript muss gedowngraded werden auf {0}", update.toString());
//              updateFrame.getUFinfoTextarea().setText("");
//            updateFrame.getUFheaderLabel().setText("Version " + update.toString() + " " + messages.getString("UpdateIsAvailable"));
//
//           
//            
//                updateFrame.getUFinfoTextarea().append(messages.getString("downgrade"));
//           
//                
//            updateFrame.getUFinfoTextarea().append(messages.getString("UpdAvail3"));
//
//            new File("version.txt").delete();
//            updateFrame.setLocationRelativeTo(null);
//            updateFrame.setVisible(true);
//            updateFrame.toFront();
//            updateFrame.repaint();
//            }else{
//                
//            }
        }
    }

    public static boolean checkIfURLExists(String targetUrl) {
        HttpURLConnection httpUrlConn;
        try {
            httpUrlConn = (HttpURLConnection) new URL(targetUrl)
                    .openConnection();

            // A HEAD request is just like a GET request, except that it asks
            // the server to return the response headers only, and not the
            // actual resource (i.e. no message body).
            // This is useful to check characteristics of a resource without
            // actually downloading it,thus saving bandwidth. Use HEAD when
            // you don't actually need a file's contents.
            httpUrlConn.setRequestMethod("HEAD");

            // Set timeouts in milliseconds
            httpUrlConn.setConnectTimeout(30000);
            httpUrlConn.setReadTimeout(30000);

            // Print HTTP status code/message for your information.
//            System.out.println("Response Code: "
//                    + httpUrlConn.getResponseCode());
//            System.out.println("Response Message: "
//                    + httpUrlConn.getResponseMessage());
            return (httpUrlConn.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            //  System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

}
