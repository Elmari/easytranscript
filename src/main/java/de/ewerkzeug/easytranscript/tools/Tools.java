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
package de.ewerkzeug.easytranscript.tools;

import de.ewerkzeug.easytranscript.core.Easytranscript;
import de.ewerkzeug.easytranscript.core.ErrorReport;
import static de.ewerkzeug.easytranscript.core.Variables.currentLocale;
import static de.ewerkzeug.easytranscript.core.Variables.errors;
import static de.ewerkzeug.easytranscript.core.Variables.logger;
import static de.ewerkzeug.easytranscript.core.Variables.messages;
import static de.ewerkzeug.easytranscript.core.Variables.opFolder;
import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;

/**
 *
 * @author e-werkzeug <administrator@e-werkzeug.eu>
 */
public class Tools {

    public void browse(String urlS) {
        try {
            URL url = new URL(urlS);
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(url.toURI());

                } catch (IOException | URISyntaxException e) {

                    logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("errorOpeningLink")), e);

                }
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    public static void checkOp() {
        if (new File("opFolder.cfg").exists()) {

            InputStreamReader converter;
            try {
                converter = new InputStreamReader(new FileInputStream("opFolder.cfg"));
                BufferedReader in = new BufferedReader(converter);

                String s = in.readLine();

                if (s == null) {
                    return;
                }

                if (s.equals("PROGPATH")) {
                    s = "";
                }

                opFolder = s;

            } catch (FileNotFoundException ex) {
                logger.log(Level.WARNING, ex.getLocalizedMessage());
            } catch (IOException ex) {
                logger.log(Level.WARNING, ex.getLocalizedMessage());
            }

        }

    }

    /**
     * checkt den übermittelten String auf problematische Zeichen.
     *
     * @param string
     * @return
     */
    public static boolean isStringSafe(String string) {
        final char[] disallowedChar = {'/', '\\', '*', '?', '|', '<', '>', ':', '\"', '~'};
        for (int i = 0; i < disallowedChar.length; i++) {
            if (string.contains(String.valueOf(disallowedChar[i]))) {
                return false;
            }
            if (string.startsWith("-") || string.startsWith(".")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Lösche bestimmten Dateityp in einem Ordner
     *
     * @param dir
     * @param pattern
     */
    public static void deleteFilesWithExtension(final File dir, final Pattern pattern) {
        final File[] files = dir.listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.isDirectory()) {
                    deleteFilesWithExtension(file, pattern);
                } else if (pattern.matcher(file.getName()).matches()) {
                    file.delete();
                }
            }
        }
    }

    /**
     * extrahiert ein Archiv.
     *
     * @param archive
     * @param destDir
     * @throws ZipException
     * @throws IOException e
     */
    public static void extractArchive(File archive, File destDir) throws ZipException, IOException {
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipFile zipFile = new ZipFile(archive);
        Enumeration entries = zipFile.entries();

        byte[] buffer = new byte[16384];
        int len;
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            String entryFileName = entry.getName();

            File dir = buildDirectoryHierarchyFor(entryFileName, destDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            if (!entry.isDirectory()) {
                if (new File(destDir, entryFileName).exists() == true) {
                    new File(destDir, entryFileName).delete();
                }
                BufferedInputStream bis;
                FileOutputStream fos = new FileOutputStream(new File(destDir, entryFileName));
                BufferedOutputStream bos = new BufferedOutputStream(fos
                );
                bis = new BufferedInputStream(zipFile
                        .getInputStream(entry));
                while ((len = bis.read(buffer)) > 0) {
                    bos.write(buffer, 0, len);
                }
                bos.flush();

                bis.close();
                fos.close();

            }
        }
        zipFile.close();
    }

    public static void createWindowsShortcut(String name, String where, String target, String icon)
            throws IOException {
        if (getOS().equals("Win")) {
            FileWriter fw = new FileWriter(where + "\\"+ name + ".url");
            fw.write("[InternetShortcut]\n");
            fw.write("URL=file://" + target + "\n");
            if (!icon.equals("")) {
                fw.write("IconIndex=0\n");
                fw.write("IconFile=file://" + icon + "\n");
            }
            fw.flush();
            fw.close();
        }
    }

    /**
     * Erstellt die Ordnerhierarchie. Notwendig zum Entpacken.
     *
     * @param entryName
     * @param destDir
     * @return
     */
    public static File buildDirectoryHierarchyFor(String entryName, File destDir) {
        int lastIndex = entryName.lastIndexOf('/');
        String entryFileName = entryName.substring(lastIndex + 1);
        String internalPathToEntry = entryName.substring(0, lastIndex + 1);
        return new File(destDir, internalPathToEntry);

    }
        public static String getOS() {
        String t = System.getProperty("os.name");

        if (t.startsWith("Windows")) {
            return "Win";
        } else if (t.startsWith("Mac")) {
            return "Mac";
        } else {
            return "Lin";
        }

    }

    public static void mergeFiles(File[] files, File mergedFile) {

        FileWriter fstream = null;
        BufferedWriter out = null;
        try {
            fstream = new FileWriter(mergedFile, true);
            out = new BufferedWriter(fstream);
        } catch (IOException e1) {

            logger.log(Level.WARNING, e1.getLocalizedMessage());
        }

        for (File f : files) {

            FileInputStream fis;
            try {
                fis = new FileInputStream(f);
                BufferedReader in = new BufferedReader(new InputStreamReader(fis));

                String aLine;
                while ((aLine = in.readLine()) != null) {
                    out.write(aLine);
                    out.newLine();
                }

                in.close();
            } catch (IOException e) {

                logger.log(Level.WARNING, e.getLocalizedMessage());
            }
        }

        try {
            out.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getLocalizedMessage());
        }

    }

    public void spenden() {
        int auswahl = JOptionPane.showOptionDialog(null, messages.getString("chooseOption"), messages.getString("Easytranscript.MaindonateMenuitem.text"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"PayPal", "Flattr", messages.getString("Abbrechen")}, 0);
        if (auswahl == 0) {
            browse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=V6QHPD3AGWLDA");
        } else if (auswahl == 1) {
            browse("https://flattr.com/thing/7c5533cb710dc6f29254cc8bed6c8f20/easytranscript");
        }
    }

    public void social() {

        int auswahl = JOptionPane.showOptionDialog(null, messages.getString("social"), "Social Networks", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(Easytranscript.class.getResource("images/network.png")), new Object[]{"Facebook", "Google+", "App.net", "Twitter", "BBM Channel", "Forum", messages.getString("Abbrechen")}, 0);

        switch (auswahl) {
            case 0:
                browse("https://www.facebook.com/pages/E-Werkzeug/1436925539899625");
                break;
            case 1:
                browse("https://plus.google.com/+EwerkzeugEu0");
                break;
            case 2:
                browse("https://alpha.app.net/ewerkzeug");
                break;
            case 3:
                browse("https://twitter.com/ewerkzeug");
                break;
            case 4:
                if (currentLocale.getLanguage().equals(new Locale("de").getLanguage())) {
                    browse("https://cmc-11.channels.blackberry.com/bbmchannels-web-portal/public/widgets/pin-qr/bbmc/C0014CA15.jpg");
                } else {
                    browse("https://cmc-11.channels.blackberry.com/bbmchannels-web-portal/public/widgets/pin-qr/bbmc/C0025E53B.jpg");
                }
                break;
            case 5:
                browse("http://e-werkzeug.eu/index.php/forum");
                break;

        }

    }
    
    	public static String rtfToHtml(Reader rtf) throws IOException {
		JEditorPane p = new JEditorPane();
		p.setContentType("text/rtf");
		EditorKit kitRtf = p.getEditorKitForContentType("text/rtf");
		try {
                    
			kitRtf.read(rtf, p.getDocument(), 0);
                        p.setText(p.getDocument().getText(0, p.getDocument().getLength()).replace("  ", "&nbsp;&nbsp;"));
			kitRtf = null;
			EditorKit kitHtml = p.getEditorKitForContentType("text/html");
			Writer writer = new StringWriter();
			kitHtml.write(writer, p.getDocument(), 0, p.getDocument().getLength());
			return writer.toString();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return null;
	}

    public static ArrayList<String> readFileLineByLine(String path) {
        ArrayList<String> list = new ArrayList<>();
        BufferedReader buffReader = null;
        try {
            buffReader = new BufferedReader(new FileReader(path));
            String line = buffReader.readLine();
            while (line != null) {
                list.add(line);
                line = buffReader.readLine();
            }
        } catch (IOException ioe) {
            logger.log(Level.WARNING, ioe.getLocalizedMessage());
        } finally {
            try {
                buffReader.close();
            } catch (IOException ioe1) {
                //Leave It
            }
        }
        return list;
    }

}
