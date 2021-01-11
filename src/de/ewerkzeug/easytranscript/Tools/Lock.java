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
package de.ewerkzeug.easytranscript.Tools;

import de.ewerkzeug.easytranscript.Core.ErrorReport;
import static de.ewerkzeug.easytranscript.Core.V.opFolder;
import static de.ewerkzeug.easytranscript.Tools.Tools.checkOp;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Verhindert dass das Programm zweimal läuft.
 *
 * @author Burhan Uddin (http://www.dscripts.net/2010/06/09/how-to-lock-a-process-in-java-to-prevent-multiple-instance-at-the-same-time/)
 */
public class Lock {

    private static File f;
    private static FileChannel channel;
    private static FileLock lock;

    public Lock(String path) {
        try {
            f = new File(System.getProperty("user.home") + System.getProperty("file.separator")+"process.lock");
            // Check if the lock exist
            if (f.exists()) // if exist try to delete it
            {
                f.delete();
            }
            // Try to get the lock
            channel = new RandomAccessFile(f, "rw").getChannel();
            lock = channel.tryLock();
            if (lock == null) {
                // File is lock by other application
                channel.close();
              
                if (path != null) {
                    if (!path.equals("")) {
                        checkOp();
                        PrintWriter writer = new PrintWriter(opFolder + "carry", "UTF-8");
                        writer.println(path);
                        writer.close();
                        System.exit(0);
                    }else{
                         checkOp();
                        PrintWriter writer = new PrintWriter(opFolder + "carry", "UTF-8");
                        writer.println("focus");
                        writer.close();
                        System.exit(0);
                    }
                }else{
                     checkOp();
                        PrintWriter writer = new PrintWriter(opFolder + "carry", "UTF-8");
                        writer.println("focus");
                        writer.close();
                        System.exit(0);
                }
                throw new RuntimeException("Two instance cant run at a time.");
            }
            // Add shutdown hook to release lock when application shutdown
            ShutdownHook shutdownHook = new ShutdownHook();
            Runtime.getRuntime().addShutdownHook(shutdownHook);

        } catch (IOException e) {
            throw new RuntimeException("Could not start process.", e);
        }
    }

    public static void unlockFile() {
        // release and delete file lock
        try {
            if (lock != null) {
                lock.release();
                channel.close();
                f.delete();
            }
        } catch (IOException e) {
            new ErrorReport().show("Easytranscript unlock failed: " + e.getLocalizedMessage());
        }
    }

    static class ShutdownHook extends Thread {

        @Override
        public void run() {
            unlockFile();
        }
    }
}
