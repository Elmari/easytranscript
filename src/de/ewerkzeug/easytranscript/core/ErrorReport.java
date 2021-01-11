/*
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
package de.ewerkzeug.easytranscript.core;

import static de.ewerkzeug.easytranscript.core.Variables.logger;
import static de.ewerkzeug.easytranscript.core.Variables.messages;
import static de.ewerkzeug.easytranscript.core.Variables.opFolder;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 * Simpler OptionDialog für den Fall, dass easytranscript in einen
 * Ausnahmezustand gerät.
 */
public class ErrorReport {

    private String email = "";
    private final ImageIcon error = new ImageIcon(Easytranscript.class.getResource("Images/dialog-error-7.png"));
    private String nachricht;

    /**
     * Zeigt eine Absturznachricht und die Java Exception an
     *
     * @param nachricht2 Absturznachricht
     * @return nachricht
     */
    public String show(String nachricht2) {
        nachricht = nachricht2;
        Object[] options = {"OK", messages.getString("send")};

        int n = JOptionPane.showOptionDialog(null,
                "<html><h3> " + messages.getString("Error") + ": "
                        + nachricht.replace("\n", " ") + "</h3></html>" + "\n\n"
                + messages.getString("StatusMessage")
                + " \"" + "log_" + new SimpleDateFormat("yyyy'-'MMM'-'W").format(new Date()) + ".xml\"\n"
                + messages.getString("StatusMessage2") + opFolder + "Logs.",
                messages.getString("ThatShouldNotHappen"),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                error,
                options,
                options[0]);
        if (n == 1) {

            email = JOptionPane.showInputDialog(messages.getString("errorReportMail"));

            reportException();

        }



        return nachricht;

    }

    /**
     * Lädt die aktuelle Log-Datei auf den isinova FTP-Server hoch.
     *
     */
    private void reportException() {

        FTPClient client = new FTPClient();

        FileInputStream fis;

        DateFormat dateFormat = new SimpleDateFormat("yyyy'-'MMM'-'W", Locale.ENGLISH);

        Date date = new Date();

        PrintWriter out;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(opFolder + "Logs/log_" + dateFormat.format(date) + ".log", true)));
            out.println("\n Nachricht: " + nachricht);
            out.println("\n\n Email: " + email);

            out.close();

            out = new PrintWriter(new BufferedWriter(new FileWriter(opFolder + "Logs/VLCJ-Info-log_" + dateFormat.format(date) + ".log", true)));
            out.println("\n Nachricht: " + nachricht);
            out.println("\n\n Email: " + email);
            out.close();

        } catch (IOException ex) {
            Logger.getLogger(ErrorReport.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {

            long millis = System.currentTimeMillis();

            client.connect("ftp.e-werkzeug.eu");
            client.login("u79560844-easylogs", "wC4,rsb3z%hnc2$N4Aswt");

            client.enterLocalPassiveMode();
            client.setFileTransferMode(FTP.BINARY_FILE_TYPE);

            fis = new FileInputStream(opFolder + "Logs/log_" + dateFormat.format(date) + ".log");
           

            client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);

            boolean success = client.storeFile("log_" + dateFormat.format(date) + "_" + millis + ".txt", fis);
          
            fis.close();

            fis = new FileInputStream(opFolder + "Logs/VLCJ-Info-log_" + dateFormat.format(date) + ".log");
            client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);

            client.storeFile("VLCJlog_" + dateFormat.format(date) + "_" + millis + ".txt", fis);
            fis.close();

            client.logout();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Fehler beim Upload.", e);
        }

    }

}
