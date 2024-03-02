/*
 * Copyright 2015-2020 Ray Fowler
 *
 * Licensed under the GNU General Public License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/gpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rotp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.util.List; // modnar: change to cleaner icon set
import java.util.Random;
import java.util.ArrayList; // modnar: change to cleaner icon set
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import rotp.model.game.GameSession;
import rotp.model.game.MOO1GameOptions;
import rotp.model.game.ModOptions;
import rotp.ui.BasePanel;
import rotp.ui.RotPUI;
import rotp.ui.SwingExceptionHandler;
import rotp.ui.UserPreferences;
import rotp.util.FontManager;
import rotp.util.ImageManager;

public class Rotp {
    private static final int MB = 1048576;
    public static final String version = RotpGovernor.governorVersion();
    public static final String modId = RotpGovernor.governorModId();
    public static final int IMG_W = 1229;
    public static final int IMG_H = 768;
    public static boolean noOptions = true; // BR: Options are not ready to be called
    public static Random random = new Random(); // BR: to allow RNG reset

    public static String jarFileName = "rotp-" + version + RotpGovernor.miniSuffix() + ".jar";
    public static String exeFileName = "rotp-" + version + ".exe";
    public static boolean countWords = false;
    private static String startupDir;
    private static Boolean isIDE;
    private static JFrame frame;
    public static String releaseId = version;
    public static long startMs = System.currentTimeMillis();
    public static long maxHeapMemory = Runtime.getRuntime().maxMemory() / 1048576;
    // BR: needs static access;
    // BR: Adjusted the values for new options
    // BR: Adjusted margin values because memory size grow as we play (events memorization)
    // BR: Small memory seems to consumes less memory!
    // BR: tested: 8GB => 75 stars/MB; 3.5GB => 100 stars/MB
    public static int maximumSystems = (maxHeapMemory > 4096 ?  75 : 100) * (int)(maxHeapMemory-600);
    public static long maxUsedMemory;
    public static long maxAllocatedMemory;
    public static boolean logging = false;
    private static float resizeAmt =  -1.0f;
    public static int actualAlloc = -1;
    public static boolean reloadRecentSave = false;

    private static GraphicsDevice device;
    
    public static String getMemoryInfo(boolean screen) {
        int  threads   = Thread.activeCount();
    	long available = Runtime.getRuntime().maxMemory() / 1048576;
    	long allocated = Runtime.getRuntime().totalMemory() / 1048576;
        long free      = Runtime.getRuntime().freeMemory() / 1048576;
        long used      = allocated - free;
        if (used > maxUsedMemory)
            maxUsedMemory = used;
        if (allocated > maxAllocatedMemory)
            maxAllocatedMemory = allocated;
        if (screen) {
            String s = used   + "M / "  +
            		allocated + "M / "  +
            		available + "M  ("  +
            		maxUsedMemory + "/" +
            		maxAllocatedMemory  + ")";
            if (threads >= 15)
                s += " T:" + Integer.toString(threads);
            return s;
        }
        else {
            String s = "Memory used:"   + String.format("% 5d", used) + " MB" +
            		" | Allocated:"     + String.format("% 5d", allocated) + " MB" +
            		" | Available:"     + String.format("% 5d", available) + " MB" +
            		" | Max used:"      + String.format("% 5d", maxUsedMemory) + " MB" +
            		" | Max allocated:" + String.format("% 5d", maxAllocatedMemory) + " MB" +
            		" | Threads:"       + String.format("% 3d", threads);
           return s;        	
        }
    }
    public static boolean noOptions(String id) {
    	if (noOptions)
    		System.out.println("### noOptions() usefully called from " + id + " ###");
    	return noOptions;
    }
    public static void main(String[] args) {
        frame = new JFrame("Remnants of the Precursors");
        String loadSaveFile = "";
        String cfgFile = "";
        if (args.length == 0) {
            if (restartWithMoreMemory(frame, false))
                return;
            logging = false;
        }
        else {
            if (args[0].toLowerCase().endsWith(".rotp"))
                loadSaveFile = args[0];

            if (args[0].endsWith("Remnants.cfg"))
                cfgFile = args[0];
        }

        reloadRecentSave = containsArg(args, "reload");
        logging = containsArg(args, "log");
        stopIfInsufficientMemory(frame, (int)maxHeapMemory);
        Thread.setDefaultUncaughtExceptionHandler(new SwingExceptionHandler());
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // BR: To initialize all mod static parameters in a controlled order
        ModOptions modStaticInit = new ModOptions();
        modStaticInit.allModOptions();
        modStaticInit = null;
        MOO1GameOptions optionsStaticInit = new MOO1GameOptions(false);
        optionsStaticInit.allModOptions();
        optionsStaticInit = null;
        UserPreferences.setCfgPath(cfgFile);

        // note: referencing the RotPUI class executes its static block
        // which loads in sounds, images, etc
        frame.setLayout(new BorderLayout());
        frame.add(RotPUI.instance(), BorderLayout.CENTER);
        noOptions = false; // BR:  Session Options are ready to be called

        // modnar: change to cleaner icon set
        List<Image> iconImages = new ArrayList<Image>();
        iconImages.add(ImageManager.current().image("ROTP_MOD_ICON3"));
        iconImages.add(ImageManager.current().image("ROTP_MOD_ICON2"));
        iconImages.add(ImageManager.current().image("ROTP_MOD_ICON1"));
        frame.setIconImages(iconImages);

        // check after ROTPUI is created
        stopIfNoFilePermissions(frame);

        Image img = ImageManager.current().image("LANDSCAPE_RUINS_ORION");
        BufferedImage bimg = RotpGovernor.toBufferedImage(img);
        BufferedImage square = bimg.getSubimage(bimg.getWidth()-bimg.getHeight(), 0, bimg.getHeight(), bimg.getHeight());
        frame.setIconImage(square);

        boolean failedFullScreen = false;
        if (UserPreferences.fullScreen()) {
            if (device().isFullScreenSupported()) {
            	frame.setUndecorated(true);
            	device().setFullScreenWindow(frame);
            	resizeAmt();
            }
            else
            	failedFullScreen = true;
        }
        else if (failedFullScreen || UserPreferences.borderless()) {
        	frame.setLocation(device().getDefaultConfiguration().getBounds().x,
					  device().getDefaultConfiguration().getBounds().y);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true);
            resizeAmt();
        }
        else {
            frame.setResizable(false);
            device().setFullScreenWindow(null);
        	frame.setLocation(device().getDefaultConfiguration().getBounds().x,
        					  device().getDefaultConfiguration().getBounds().y);
            setFrameSize();
        }

        // this will not catch 32-bit JREs on all platforms, but better than nothing
        String bits = System.getProperty("sun.arch.data.model").trim();
        if (bits.equals("32"))
            RotPUI.instance().mainUI().showJava32BitPrompt();
        else if (reloadRecentSave)
            GameSession.instance().loadRecentSession(false);
        else if (!loadSaveFile.isEmpty())
            GameSession.instance().loadSession("", loadSaveFile, false);

        becomeVisible();
    }
    private static GraphicsDevice device() {
    	if (device == null) {
            int selectedScreen = UserPreferences.selectedScreen();
            if (selectedScreen < 0)
            	device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            else {
                GraphicsDevice[] gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
                selectedScreen = Math.min(selectedScreen, gd.length-1);
                device = gd[selectedScreen];
            }
    	}
        return device;
    }
    public static int maxScreenIndex() {
    	return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length-1;
    }
    public static void becomeVisible() { frame.setVisible(true); }
    public static void setVisible(boolean b) { frame.setVisible(b); }
   
    public static boolean containsArg(String[] argList, String key) {
        for (String s: argList) {
            if (s.equalsIgnoreCase(key))
                return true;
        }
        return false;
    }
    public static void setFrameSize() {
        resizeAmt = -1;
        double adj = resizeAmt();
        int vFrame = 0;
        int hFrame = 0;
        int maxX = (int)((hFrame+IMG_W)*adj);
        int maxY = (int)((vFrame+IMG_H)*adj);
        FontManager.current().resetFonts();
        if (logging)
            System.out.println("setting size to: "+maxX+" x "+maxY);
        frame.getContentPane().setPreferredSize(new Dimension(maxX,maxY));
        frame.pack();
    }
    public static Dimension getSize() {
    	Rectangle rect = device().getDefaultConfiguration().getBounds();
    	return new Dimension(rect.width, rect.height);
    }
    public static float resizeAmt() {
        int pct = UserPreferences.windowed() ? UserPreferences.screenSizePct() : 100;
        float sizeAdj = (float) pct / 100.0f;
        if (resizeAmt < 0) {
        	
            Dimension size = getSize();

            int sizeW = (int) (sizeAdj*size.width);
            int sizeH = (int) (sizeAdj*size.height);
            int maxX = sizeH*8/5;
            int maxY = sizeW*5/8;
            if (maxY > sizeH)
                maxY = maxX*5/8;

            resizeAmt = (float) maxY/768;
            (new BasePanel()).loadScaledIntegers();
            if (logging)
                System.out.println("resize amt:"+resizeAmt);
        }
        return resizeAmt;
    }
    public static String jarPath()  {
        if (startupDir == null) {
            try {
                File jarFile = new File(Rotp.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                startupDir = jarFile.getParentFile().getPath();
            } catch (URISyntaxException ex) {
                System.out.println("Unable to resolve jar path: "+ex.toString());
                startupDir = ".";
            }
        }
        return startupDir;
    }
    public	static boolean isIDE() {
    	if (isIDE == null)
    		isIDE = jarPath().toUpperCase().endsWith("TARGET");
    	return isIDE;
    }
    private static void stopIfInsufficientMemory(JFrame frame, int allocMb) {
        if (allocMb < 260) {
            JOptionPane.showMessageDialog(frame, "Error starting game: Not enough free memory to play");
            System.exit(0);
        }
    }
    private static void stopIfNoFilePermissions(JFrame frame) {
        if (UserPreferences.save() < 0) {
            JOptionPane.showMessageDialog(frame, "Error starting game: Installed in directory with insufficient file permissions.");
            System.exit(0);
        }
    }
    public static boolean memoryLow() {
        // returns true if total memory allocated to the JVM is within 100 MB of maximum allowed
        long max = Runtime.getRuntime().maxMemory() / 1048576;
        long total = Runtime.getRuntime().totalMemory() / 1048576;
        long free = Runtime.getRuntime().freeMemory() / 1048576;
        return (max == total) && (free < 300);
    }
    @SuppressWarnings("deprecation")
	public static void restart() {
        File exeFile = new File(startupDir+"/"+exeFileName);
        String execStr = exeFile.exists() ? exeFileName : actualAlloc < 0 ? "java -jar "+jarFileName : "java -Xmx"+actualAlloc+"m -jar "+jarFileName+" arg1";

        try {
            Runtime.getRuntime().exec(execStr);
            System.exit(0);
        } catch (IOException ex) {
            System.err.println("Error attempting restart: ");
            ex.printStackTrace();
        }
    }
    public static void restartFromLowMemory() {
        restartWithMoreMemory(frame, true);
    }
  
	@SuppressWarnings("deprecation")
	private static boolean restartWithMoreMemory(JFrame frame, boolean reload) {
        // MXBeans are not supported by GraalVM Native, so skip this part
        if (RotpGovernor.GRAALVM_NATIVE) {
            System.out.println("Running as GraalVM Native image");
            return false;
        }
        @SuppressWarnings("restriction")
		long memorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
                        .getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
        @SuppressWarnings("restriction")
		long freeMemory = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
                        .getOperatingSystemMXBean()).getFreePhysicalMemorySize();
        int maxMb = (int) (memorySize / MB);
        long allocMb = Runtime.getRuntime().maxMemory() / MB;
        int freeMb = (int) (freeMemory / MB);
        String bits = System.getProperty("sun.arch.data.model");

        System.out.println("maxMB:"+maxMb+"  freeMB:"+freeMb+"  allocMb:"+allocMb+"   bits:"+bits);
        // if system has given us 2.5G+, then we're good
        if (!reload && (allocMb > 2560))
            return false;

        // desiredAlloc is 1G or 1/3rd of max memory, whichever is higher
        int desiredAlloc = Math.max(1024, (int)maxMb/3);
        // we'll alloc smallest of the desired Alloc or 75% of free memory (after 500mb overhead)
        actualAlloc = Math.min(desiredAlloc, (int)((freeMb+allocMb-500)*0.75));
        // if we're not a 64-bit JVM, limit requested heap to 1600Mb
        if (!bits.equals("64"))
            actualAlloc = Math.min(actualAlloc, 1200);
        // if that amount is <500M, then show an error
        System.out.println("restarting with MB:"+actualAlloc);
        if (!reload && (actualAlloc < allocMb))
            return false;

        try {
            stopIfInsufficientMemory(frame, actualAlloc*9/10);
            String argString = reload ? " reload" : " arg1";
            String execStr  = "java -Xmx"+actualAlloc+"m -jar "+jarFileName+argString;
            System.out.println("Only "+(int) allocMb+"Mb memory allocated by OS. Restarting game with command: "+execStr);
            Runtime.getRuntime().exec(execStr);
            System.exit(0);
            return true;
        } catch (IOException ex) {
            System.err.println("Error attempting restart: ");
            ex.printStackTrace();
        }
        return false;
    }
}
