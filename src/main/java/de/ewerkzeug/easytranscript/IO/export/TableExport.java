/**
 *
 *
 * easytranscript Copyright (C) 2013 e-werkzeug
 *
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, entweder gemäß Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren Version. Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/>.
 *
 *
 */
package de.ewerkzeug.easytranscript.IO.export;

import de.ewerkzeug.easytranscript.core.ErrorReport;
import static de.ewerkzeug.easytranscript.core.Variables.errors;
import static de.ewerkzeug.easytranscript.core.Variables.projFolder;
import static de.ewerkzeug.easytranscript.core.Variables.zeitFrame;
import static de.ewerkzeug.easytranscript.IO.data.TranscriptHandler.transcriptName;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Beinhaltet Export-Methoden einer JTable
 */
public class TableExport {

    /**
     * Exportiert die Arbeitzeittabelle in eine CSV Datei.
     *
     * @param path Pfad
     */
    public static void toCSV(String path) {

        try {
            if (new File(path).exists()) {
                new File(path).delete();
            }
            new File(path).createNewFile();
            FileWriter fw = new FileWriter(new File(path));
            BufferedWriter bw = new BufferedWriter(fw);
            TableModel model = zeitFrame.getZeitTableTable().getModel();
            for (int h = 0; h < model.getColumnCount(); h++) {

                bw.write("\"" + model.getColumnName(h) + "\"");
                if (h + 1 != model.getColumnCount()) {
                    bw.write(",");
                }
            }
            bw.newLine();

            for (int clmCnt = model.getColumnCount(), rowCnt = model.getRowCount(), i = 0; i < rowCnt; i++) {
                for (int j = 0; j < clmCnt; j++) {
                    if (model.getValueAt(i, j) != null) {
                        String value = "\"" + model.getValueAt(i, j).toString() + "\"";
                        bw.write(value);
                    }
                    if (j + 1 != clmCnt) {
                        bw.write(",");
                    }
                }
                bw.newLine();
            }
            bw.write(",");
            bw.write(",");
            if (transcriptName != null) {
                if (!transcriptName.equals("")) {
                    bw.write(transcriptName + ",");
                }
            } else if (new File(projFolder.getPath()).exists()) {

                bw.write(new File(projFolder.getPath()).getName() + ",");
            } else {
                bw.write(",");
            }

            String value = "\"" + zeitFrame.getZeitTotalLabel().getText() + "\"";
            bw.write(value);
            bw.write(",");
            bw.newLine();

            bw.flush();
            bw.close();
            fw.close();

        } catch (IOException e) {
            new ErrorReport().show(errors.getString("FailedToExport"));
        }

    }

    /**
     * Exportiert die Arbeitzeittabelle in eine XLS Datei.
     *
     * @param path Pfad
     */
    public static void toXLS(String path) {

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("easytranscript");

        TableModel model = zeitFrame.getZeitTableTable().getModel();

        Row row = sheet.createRow(0);
        for (int i = 0; i < model.getColumnCount(); i++) {

            Cell cell = row.createCell(i);
            cell.setCellValue(model.getColumnName(i));
        }
        int j;
        for (int i = 0; i < model.getRowCount(); i++) {
            row = sheet.createRow(i + 1);
            for (j = 0; j < model.getColumnCount(); j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(model.getValueAt(i, j).toString());
            }
        }

        Row r = sheet.createRow(model.getRowCount() + 1);

        if (transcriptName != null) {
            if (!transcriptName.equals("")) {
                r.createCell(model.getColumnCount() - 1).setCellValue(transcriptName);

            }
        } else if (new File(projFolder.getPath()).exists()) {

            r.createCell(model.getColumnCount() - 1).setCellValue(new File(projFolder.getPath()).getName());

        }
        r.createCell(model.getColumnCount()).setCellValue(zeitFrame.getZeitTotalLabel().getText());

        try {
            FileOutputStream out = new FileOutputStream(new File(path));
            workbook.write(out);
            out.close();

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        

    }

    /**
     * Exportiert die Arbeitzeittabelle in eine XLSX Datei.
     *
     * @param path Pfad
     */
    public static void toXLSX(String path) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("easytranscript");

        TableModel model = zeitFrame.getZeitTableTable().getModel();

        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < model.getColumnCount(); i++) {

            XSSFCell cell = row.createCell(i);
            cell.setCellValue(model.getColumnName(i));
        }
        int j;
        for (int i = 0; i < model.getRowCount(); i++) {
            row = sheet.createRow(i + 1);
            for (j = 0; j < model.getColumnCount(); j++) {
                XSSFCell cell = row.createCell(j);
                cell.setCellValue(model.getValueAt(i, j).toString());
            }
        }

        XSSFRow r = sheet.createRow(model.getRowCount() + 1);

        if (transcriptName != null) {
            if (!transcriptName.equals("")) {
                r.createCell(model.getColumnCount() - 1).setCellValue(transcriptName);

            }
        } else if (new File(projFolder.getPath()).exists()) {

            r.createCell(model.getColumnCount() - 1).setCellValue(new File(projFolder.getPath()).getName());

        }
        r.createCell(model.getColumnCount()).setCellValue(zeitFrame.getZeitTotalLabel().getText());

        try {
            FileOutputStream out = new FileOutputStream(new File(path));
            workbook.write(out);
            out.close();

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    /**
     * Exportiert die Arbeitzeittabelle in eine HTML Datei.
     *
     * @param path Pfad
     */
    public static void toHTML(String path) {
        try {
            if (new File(path).exists()) {
                new File(path).delete();
            }
            new File(path).createNewFile();
            FileWriter fw = new FileWriter(new File(path));
            BufferedWriter bw = new BufferedWriter(fw);
            TableModel model = zeitFrame.getZeitTableTable().getModel();

            bw.write("<TABLE BORDER=\"2\"    WIDTH=\"50%\"   CELLPADDING=\"4\" CELLSPACING=\"3\">");

            String name = "";

            if (transcriptName != null) {
                if (!transcriptName.equals("")) {
                    name = transcriptName;

                }
            } else if (new File(projFolder.getPath()).exists()) {

                name = new File(projFolder.getPath()).getName();

            }

            bw.write("<TR>");
            bw.write("<TH COLSPAN=\"2\"><BR><H3>" + name + " - " + zeitFrame.getZeitTotalLabel().getText() + " </H3>");
            bw.write("</TH>");
            bw.write("</TR>");

            bw.write("<TR>");

            for (int h = 0; h < model.getColumnCount(); h++) {
                bw.write("<TH>" + model.getColumnName(h) + "</TH>");

            }

            bw.write("</TR>");

            for (int clmCnt = model.getColumnCount(), rowCnt = model.getRowCount(), i = 0; i < rowCnt; i++) {
                bw.write("<TR>");
                for (int j = 0; j < clmCnt; j++) {
                    if (model.getValueAt(i, j) != null) {
                        String value = "<TD>" + model.getValueAt(i, j).toString() + "</TD>";
                        bw.write(value);
                    }
                }
                bw.write("</TR>");
            }

            bw.write("</TABLE>");

            bw.flush();
            bw.close();
            fw.close();

        } catch (IOException e) {
            new ErrorReport().show(errors.getString("FailedToExport"));
        }
    }

    /**
     * Exportiert die Arbeitzeittabelle in eine PNG Datei.
     *
     * @param path Pfad
     */
    public static void toPNG(String path) {
        JTableHeader h = zeitFrame.getZeitTableTable().getTableHeader();
        Dimension dH = h.getSize();
        Dimension dT = zeitFrame.getZeitTableTable().getSize();
        int x = (int) dH.getWidth();
        int y = (int) dH.getHeight() + (int) dT.getHeight() + 40;

        String name = "";

        if (transcriptName != null) {
            if (!transcriptName.equals("")) {
                name = transcriptName;

            }
        } else if (new File(projFolder.getPath()).exists()) {

            name = new File(projFolder.getPath()).getName();

        }

        BufferedImage bi = new BufferedImage(
                (int) x,
                (int) y,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics g = bi.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, x, y);

        h.paint(g);
        g.translate(0, h.getHeight());
        zeitFrame.getZeitTableTable().paint(g);
        g.setColor(Color.BLACK);
        g.drawString(name, 0, (int) (dH.getHeight() + (int) dT.getHeight()));
        g.drawString(zeitFrame.getZeitTotalLabel().getText(), 0, (int) (dH.getHeight() + (int) dT.getHeight() + 15));
        g.dispose();
        try {
            ImageIO.write(bi, "png", new File(path));

        } catch (IOException ex) {
            Logger.getLogger(TableExport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
