package ohi.andre.consolelauncher.tuils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import dalvik.system.DexFile;
import ohi.andre.consolelauncher.managers.MusicManager;
import ohi.andre.consolelauncher.tuils.tutorial.TutorialIndexActivity;

public class Tuils {

    public static final String SPACE = " ";
    public static final String DOUBLE_SPACE = "  ";
    public static final String NEWLINE = "\n";
    public static final String TRIBLE_SPACE = "   ";
    public static final String DOT = ".";
    public static final String EMPTYSTRING = "";
    private static final String TUI_FOLDER = "t-ui";

    public static boolean arrayContains(int[] array, int value) {
        for(int i : array) {
            if(i == value) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsExtension(String[] array, String value) {
        value = value.toLowerCase().trim();
        for(String s : array) {
            if(value.endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public static List<File> getSongsInFolder(File folder) {
        List<File> songs = new ArrayList<>();

        File[] files = folder.listFiles();
        if(files == null || files.length == 0) {
            return null;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                songs.addAll(getSongsInFolder(file));
            }
            else if (containsExtension(MusicManager.MUSIC_EXTENSIONS, file.getName())) {
                songs.add(file);
            }
        }

        return songs;
    }

    public static void showTutorial(Context context) {
        Intent intent = new Intent(context, TutorialIndexActivity.class);
        context.startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void openSettingsPage(Activity c, String toast) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", c.getPackageName(), null);
        intent.setData(uri);
        c.startActivity(intent);
        Toast.makeText(c, toast, Toast.LENGTH_LONG).show();
    }

    public static void requestAdmin(Activity a, ComponentName component, String label) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, component);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, label);
        a.startActivityForResult(intent, 0);
    }

    public static String removeExtension(String s) {
        return s.substring(0, s.lastIndexOf("."));
    }

    public static String ramDetails(ActivityManager mgr, MemoryInfo info) {
        mgr.getMemoryInfo(info);
        long availableMegs = info.availMem / 1048576L;

        return availableMegs + " MB";
    }

    public static Integer getPID() {
        int pid = android.os.Process.myPid();

        return pid;
    }

    public static List<String> getClassesInPackage(String packageName, Context c)
            throws IOException {
        List<String> classes = new ArrayList<>();
        String packageCodePath = c.getPackageCodePath();
        DexFile df = new DexFile(packageCodePath);
        for (Enumeration<String> iter = df.entries(); iter.hasMoreElements(); ) {
            String className = iter.nextElement();
            if (className.contains(packageName) && !className.contains("$"))
                classes.add(className.substring(className.lastIndexOf(".") + 1, className.length()));
        }

        return classes;
    }

    public static int findPrefix(List<String> list, String prefix) {
        for (int count = 0; count < list.size(); count++)
            if (list.get(count).startsWith(prefix))
                return count;
        return -1;
    }

    public static boolean verifyRoot() {
        Process p;
        try {
            p = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("echo \"root?\" >/system/sd/temporary.txt\n");

            os.writeBytes("exit\n");
            os.flush();
            try {
                p.waitFor();
                return p.exitValue() != 255;
            } catch (InterruptedException e) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public static void insertHeaders(List<String> s, boolean newLine) {
        char current = 0;
        for (int count = 0; count < s.size(); count++) {
            char c = 0;

            String st = s.get(count);
            for (int count2 = 0; count2 < st.length(); count2++) {
                c = st.charAt(count2);
                if (c != ' ')
                    break;
            }

            if (current != c) {
                s.add(count, (newLine ? NEWLINE : EMPTYSTRING) + Character.toString(c).toUpperCase() + (newLine ? NEWLINE : EMPTYSTRING));
                current = c;
            }
        }
    }

    public static void addPrefix(List<String> list, String prefix) {
        for (int count = 0; count < list.size(); count++)
            list.set(count, prefix.concat(list.get(count)));
    }

    public static void addSeparator(List<String> list, String separator) {
        for (int count = 0; count < list.size(); count++)
            list.set(count, list.get(count).concat(separator));
    }

    public static String toPlanString(String[] strings, String separator) {
        String output = "";
        for (int count = 0; count < strings.length; count++) {
            output = output.concat(strings[count]);
            if (count < strings.length - 1)
                output = output.concat(separator);
        }
        return output;
    }

    public static String toPlanString(String[] strings) {
        return Tuils.toPlanString(strings, Tuils.NEWLINE);
    }

    public static String toPlanString(List<String> strings, String separator) {
        String[] object = new String[strings.size()];
        return Tuils.toPlanString(strings.toArray(object), separator);
    }

    public static String filesToPlanString(List<File> files, String separator) {
        StringBuilder builder = new StringBuilder();
        int limit = files.size() - 1;
        for (int count = 0; count < files.size(); count++) {
            builder.append(files.get(count).getName());
            if (count < limit) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }

    public static String toPlanString(List<String> strings) {
        return Tuils.toPlanString(strings, NEWLINE);
    }

    public static String toPlanString(Object[] objs, String separator) {
        StringBuilder output = new StringBuilder();
        for(int count = 0; count < objs.length; count++) {
            output.append(objs[count]);
            if(count < objs.length - 1) {
                output.append(separator);
            }
        }
        return output.toString();
    }

    public static CharSequence toPlanSequence(List<CharSequence> sequences, CharSequence separator) {
        return toPlanSequence(sequences.toArray(new CharSequence[sequences.size()]), separator);
    }

    public static CharSequence toPlanSequence(CharSequence[] sequences, CharSequence separator) {
        if (sequences.length == 0)
            return null;

        CharSequence sequence = null;
        int count;
        for (count = 0; (sequence = sequences[count]) == null; count++) {
        }

        CharSequence output = sequences[count];
        do {
            count++;
            CharSequence current = sequences[count];
            if (current == null)
                continue;

            output = TextUtils.concat(output, current);
            if (count < sequences.length - 1 && !current.toString().contains(separator))
                output = TextUtils.concat(output, separator);
        } while (count + 1 < sequences.length);
        return output;
    }

    public static CharSequence toPlanSequence(CharSequence[] sequences) {
        return TextUtils.concat(sequences);
    }

    public static String removeUnncesarySpaces(String string) {
        while (string.contains(DOUBLE_SPACE)) {
            string = string.replace(DOUBLE_SPACE, SPACE);
        }
        return string;
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static boolean isAlpha(String s) {
        char[] chars = s.toCharArray();

        for (char c : chars)
            if (!Character.isLetter(c))
                return false;

        return true;
    }

    public static boolean isNumber(String s) {
        char[] chars = s.toCharArray();

        for (char c : chars) {
            if (Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }

    public static CharSequence trimWhitespaces(CharSequence source) {

        if(source == null) {
            return Tuils.EMPTYSTRING;
        }

        int i = source.length();

        // loop back to the first non-whitespace character
        while(--i >= 0 && Character.isWhitespace(source.charAt(i))) {}

        return source.subSequence(0, i+1);
    }

    public static String getSDK() {
        return "android-sdk " + Build.VERSION.SDK_INT;
    }

    public static String getUsername(Context context) {
        Pattern email = Patterns.EMAIL_ADDRESS;
        Account[] accs = AccountManager.get(context).getAccounts();
        for (Account a : accs)
            if (email.matcher(a.name).matches())
                return a.name;
        return null;
    }

    public static Intent openFile(File url) {
        Uri uri = Uri.fromFile(url);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.toString().contains(".apk")) {
            // apk
            intent.setDataAndType(uri,
                    "application/vnd.android.package-archive");
        } else if (url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.toString().contains(".ppt")
                || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.toString().contains(".xls")
                || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.toString().contains(".zip")
                || url.toString().contains(".rar")) {
            // ZIP Files
            intent.setDataAndType(uri, "application/zip");
        } else if (url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.toString().contains(".wav")
                || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.toString().contains(".jpg")
                || url.toString().contains(".jpeg")
                || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.toString().contains(".3gp")
                || url.toString().contains(".mpg")
                || url.toString().contains(".mpeg")
                || url.toString().contains(".mpe")
                || url.toString().contains(".mp4")
                || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static String getInternalDirectoryPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static File getTuiFolder() {
        return new File(Tuils.getInternalDirectoryPath(), TUI_FOLDER);
    }

}
