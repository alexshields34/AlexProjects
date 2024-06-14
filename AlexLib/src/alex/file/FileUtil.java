
package alex.file;


import alex.date.CommonDate;
import alex.math.Constants;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;

/**
 *
 * @author alex
 */
public class FileUtil {
    
    public final static long fiveMegabytes=5L * 1024L * 1024L;
    public final static long tenMegabytes=10L * 1024L * 1024L;
    
    
    /**
     * Don't close either stream.
     * @param is May not be null.
     * @param os May not be null.
     * @throws IOException
     */
    public static void copyBinaryContent(InputStream is, OutputStream os)
            throws IOException
    {
        int amountRead;
        byte[] tempStorage;
        
        tempStorage=new byte[(int)tenMegabytes];
        
        while (-1!=(amountRead=is.read(tempStorage))) {
            os.write(tempStorage, 0, amountRead);
        }
    }
    
    
    public static void appendToFile(File targetFile, String content, boolean shouldAppendNewline)
            throws FileNotFoundException, IOException
    {
        RandomAccessFile raf;
        
        raf=new RandomAccessFile(targetFile, "rw");
        
        raf.seek(raf.length());
        
        raf.writeBytes(content);
        if (shouldAppendNewline) {
            raf.writeByte('\n');
        }
        
        raf.close();
    }
    
    
    public static void saveArrayAsTextFile(File file, Collection<String> content)
            throws FileNotFoundException
    {
        
        StringBuilder sb;
        
        
        sb=new StringBuilder(1000);
        
        for (String s: content) {
            sb.append(s)
                    .append('\n');
        }
        
        saveTextAsFile(file, sb.toString());
    }
    
    /**
     * Return the contents of the file as an ArrayList.  Each line in the 
     * file is trimmed and is an entry in the ArrayList.
     * 
     * @param filePath
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static ArrayList<String> readTextFileAsArray(File filePath)
            throws FileNotFoundException, IOException
    {
        ArrayList<String> retVal;
        String line;
        FileReader fis;
        BufferedReader br;
        
        fis=new FileReader(filePath);
        br=new BufferedReader(fis);
        retVal=new ArrayList();
        
        while (null!=(line=br.readLine())) {
            retVal.add(line.trim());
        }
        
        br.close();
        fis.close();
        
        return retVal;
    }
    
    /**
     * An example of a valid value for classPathItem is
     * "alex/tugofwords/categories.txt".
     * 
     * @param classPathItem
     * @param anyClassLoader Use this class to get its class loader.
     * @return 
     * @throws IOException
     */
    public static String readTextFileFromClasspath(ClassLoader anyClassLoader, String classPathItem)
            throws IOException
    {
        URL url;
        InputStream is;
        String retVal;
        
        url=anyClassLoader.getResource(classPathItem);
        is=url.openStream();
        retVal=readTextStream(is);
        is.close();
        
        return retVal;
    }
    
    
    public static String readTextFile(File file)
            throws FileNotFoundException, IOException
    {
        String retVal;
        FileInputStream fis;
        
        fis=new FileInputStream(file);
        retVal=readTextStream(fis);
        fis.close();
        
        return retVal;
    }
    
    
    public static String readTextFile(String filePath)
            throws FileNotFoundException, IOException
    {
        return readTextFile(new File(filePath));
    }
    
    
    /**
     * Don't close the stream when finished. UTF-8.
     * 
     * 
     * The code within this method can potentially throw an 
     * UnsupportedEncodingException, but that should never happen.
     * 
     * @param is
     * @return
     * @throws IOException
     */
    public static String readTextStream(InputStream is)
            throws IOException
    {
        byte[] buffer;
        StringBuilder sb;
        int numCharactersRead;
        
        buffer=new byte[1024*1024];
        sb=new StringBuilder(1024*1024);
        
        while (-1!=(numCharactersRead=is.read(buffer))) {
            sb.append(new String(buffer, 0, numCharactersRead, "utf-8"));
        }
        
        return sb.toString();
    }
    
    public static void saveBinaryFile(File targetFile,
            InputStream is)
            throws FileNotFoundException, IOException
    {
        saveBinaryFile(targetFile, is, false);
    }
    
    /**
     * Store the content of the specified InputStream to the target location.
     * 
     * @param targetFile
     * @param is
     * @param shouldOutputDebugInfo If true, output a message every 5 megs.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void saveBinaryFile(File targetFile,
            InputStream is,
            boolean shouldOutputDebugInfo)
            throws FileNotFoundException, IOException
    {
	FileOutputStream fos;
	byte[] buffer;
	int end;
        long sizeOfChunkSoFar, totalSizeSoFar;

        sizeOfChunkSoFar=totalSizeSoFar=0L;
	buffer=new byte[1024*10];

	fos=new FileOutputStream(targetFile);

	while (-1!=(end=is.read(buffer))) {
	    fos.write(buffer, 0, end);
            
            
            if (shouldOutputDebugInfo) {
                totalSizeSoFar+=end;
                sizeOfChunkSoFar+=end;
                if (sizeOfChunkSoFar>=tenMegabytes) {
                    System.out.println("Downloaded " + totalSizeSoFar + " bytes so far.");
                    sizeOfChunkSoFar=0L;
                }
            }
            
	}

	fos.close();
    }
    
    
    public static void saveTextAsFile(String content, String targetDirectory)
            throws Exception
    {
        saveTextAsFile(content, new File(targetDirectory));
    }
    
    public static String generateTimeBasedFileName() {
        return "text"+CommonDate.getNowAsFilenameFormat()+".txt";
    }
    
    
    /**
     * 
     * @param content
     * @param targetDirectory The name of the file to save is based on the 
     * current date and time.
     * @throws Exception 
     */
    public static void saveTextAsFile(String content, File targetDirectory)
            throws Exception
    {
        File targetFile;
        
        targetFile=new File(targetDirectory, generateTimeBasedFileName());
        
        saveTextAsFile(targetFile, content);
        
    }
    
    public static void saveTextAsFile(File targetFile, String content)
            throws FileNotFoundException
    {
        PrintWriter pw;
        
        pw=new PrintWriter(targetFile);
        pw.print(content);
        pw.close();
        
    }
    
    /**
     * If the extension is missing from the given fileName, try to 
     * guess it using Tika.  The file must exist already and it must be readable.
     * 
     * Some files have periods in them but there is still no extension.  
     * If the period in the filename is more than 5 characters away from the end
     * of the filename then treat it as if it doesn't have an extension.
     * 
     * @param file 
     * @return null if the file can't be read, or if it already has
     * an extension.  Otherwise, a new extension is returned, without
     * the period.  "mp3" for example.
     * @throws IOException
     */
    public static String determineExtensionIfMissing(File file)
            throws IOException
    {
        boolean shouldAttemptDetectingFileType;
        int index, slashIndex;
        String retVal, mimeType;
        String fileName;
        org.apache.tika.Tika tika;
        
        retVal=null;
        
        if (!file.isDirectory() && file.canRead())
        { 
            fileName=file.getName();
            
            index=fileName.lastIndexOf(".");
            shouldAttemptDetectingFileType=index==-1
                    || index<=(fileName.length()-5);
            
            if (shouldAttemptDetectingFileType) {
//            if (fileName.lastIndexOf(".")==-1) {
                 tika= new org.apache.tika.Tika();
                 
                 mimeType = tika.detect(file);
                 System.out.println("FileUtil.determineExtensionIfMissing:: Detected the mimeType ["+mimeType+"]");
                 
                 if (mimeType.contains("audio/mpeg")) {
                     retVal="mp3";
                 } else if (mimeType.contains("audio/mp4")) {
                     retVal="m4a"; 
                 } else if (mimeType.contains("image/webp")) {
                     retVal="webp";
                 } else {
                    // Just get the part of the mimeType after the last /.
                    if ((slashIndex=mimeType.lastIndexOf("/"))!=-1) {
                        retVal=mimeType.substring(slashIndex+1);
                    }
                 }
            }
        }
        
        return retVal;
    }
    
    
    /**
     * Check to see if the file's extension matches what it should be as reported
     * by tika. If the two don't match, then rename the file.
     * 
     * Return the newly named file.  If no change is done, return the sourceFile. 
     * 
     * This method is primarily for the use of renaming image files.
     * 
     * @param sourceFile
     * @param shouldOutputDebugInfo
     * @param shouldEnsureUniqueNewName
     * @return
     * @throws IOException 
     */
    public static File fixFileExtension(final File sourceFile,
            final boolean shouldOutputDebugInfo,
            final boolean shouldEnsureUniqueNewName)
            throws IOException
    {
        String mimeType, newExtension;
        File newFileName, retVal;
        Filename fn, oldfn;
        
        newExtension=determineExtensionIfMissing(sourceFile);
        if (newExtension==null) {
            mimeType=FileUtil.getMimeType(sourceFile);
            newExtension=mimeType.substring(mimeType.indexOf("/")+1);
        }
        
        // If the extant extension doesn't match the new extension, perform
        // the renaming.
        oldfn=new Filename(sourceFile.getName(), !(sourceFile.getName().contains(".")));
        
        if (!newExtension.equals(oldfn.getExtension())) {
            fn=new Filename(oldfn);
            fn.setExtension(newExtension);

            newFileName=new File(sourceFile.getParent(), fn.getFullName());
            if (shouldEnsureUniqueNewName) {
                while (newFileName.exists()) {
                    fn.appendNumberToFileName(1);
                    newFileName=new File(sourceFile.getParent(), fn.getFullName());
                }
            }

            if (shouldOutputDebugInfo) {
                System.out.format("Renaming %s to %s\n", sourceFile.getName(), newFileName.getName());
            }

            sourceFile.renameTo(newFileName);
            
            retVal=newFileName;
        } else {
            retVal=sourceFile;
        }
        
        
        return retVal;
    }
    
    
    public static String getMimeType(File file)
            throws IOException
    {
        
        String retVal;
        org.apache.tika.Tika tika;
        
        retVal=null;
        
        if (!file.isDirectory() && file.canRead())
        {
             tika= new org.apache.tika.Tika();

             retVal=tika.detect(file);
        }
        
        return retVal;
    }
    
    
    /**
     * If the contents of the two files are equal, return true.  If necessary,
     * only check the first few bytes.
     * 
     * @param f1 Should not be null.  The file should exist and be a regular file.
     * @param f2 Should not be null.  The file should exist and be a regular file.
     * @param numberOfBytesToCheck -1 if this method should check all of the bytes.
     * Otherwise, only compare the specified number of bytes to determine equality.
     * 
     * @return 
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static boolean areEqual(File f1, File f2, long numberOfBytesToCheck)
        throws FileNotFoundException, IOException
    {
        boolean retVal;
        int bytesRead;
        long maxBytesToRead, remainingBytesToRead;
        byte[] array1, array2;
        FileInputStream fis1, fis2;
        boolean doFilesMatch;
        
        if (f1.length()!=f2.length()) {
            retVal=false;
        } else {
            if (numberOfBytesToCheck==-1L) {
                maxBytesToRead=f1.length();
            } else {
                maxBytesToRead=numberOfBytesToCheck;
            }
            array1=new byte[1024];
            array2=new byte[1024];
            
            fis1=new FileInputStream(f1);
            fis2=new FileInputStream(f2);
            
            remainingBytesToRead=maxBytesToRead;
            doFilesMatch=true;
            while (doFilesMatch && remainingBytesToRead>0L)
            {
                Arrays.fill(array1, Constants.byte_zero);
                Arrays.fill(array2, Constants.byte_zero);
                fis1.read(array1);
                bytesRead=fis2.read(array2);
                remainingBytesToRead-=bytesRead;
                
                doFilesMatch=Arrays.equals(array1, array2);
            }
            retVal=doFilesMatch;
            
            fis1.close();
            fis2.close();
        }
        
        return retVal;
    }
    
    /**
     * Return the portion of the file after the last '.' character.  It's possible
     * that this isn't really an extension in the case of file names with periods
     * in them, and the file doesn't actually have an extension.  This has
     * come up with some mp3 files that I downloaded from podcast webpages.
     * 
     * Return null if no extension was found.
     * 
     * @param fileName The extension without the '.' character.
     * @return 
     * @deprecated Use Filename.java instead.
     */
    @Deprecated
    public static String getExtension(String fileName) {
        
        int index;
        String retVal;
        
        index=fileName.lastIndexOf('.');
        if (index!=-1) {
            // IF the period was the last character in the filename, then the
            // file has no extension.
            if (index==fileName.length()-1) {
                retVal=null;
            } else {
                retVal=fileName.substring(index+1);
            }
        } else {
            retVal=null;
        }
        
        return retVal;
    }
    
    
    
    public static void main(String[] args)
            throws Exception
    {
        File f;
        
        f=new File("c:/tmp/podcasts/");
        org.apache.tika.Tika tika = new org.apache.tika.Tika();
        
        for (File oneFile: f.listFiles())
        {
            
            if (oneFile.isDirectory() || !oneFile.canRead()) {
                continue;
            }
            
            System.out.println("The type of ["
                    +oneFile.getName()
                    +"] is ["
                    +tika.detect(oneFile)
                    +"]");
        }
        
    }
    
    
    
    
    /**
     * Retain characters, numbers, underscores, hyphens, and periods.  Any other 
     * character is replaced with an underscore.
     * @param name
     * @param shouldKeepParens If true, keep ()
     * @return 
     */
    public static String cleanFileName(String name, boolean shouldKeepParens) {
        StringBuilder sb;
        boolean shouldKeep;
        
        sb=new StringBuilder();
        
        for (char c: name.toCharArray()) {
            shouldKeep=Character.isLetterOrDigit(c)
                    || c == '_'
                    || c == '-'
                    || c == '.';
            
            shouldKeep = shouldKeep || (shouldKeepParens && (c=='(' || c==')'));
            
            if (shouldKeep) {
                sb.append(c);
            } else {
                sb.append("_");
            }
            
        }
        
        return sb.toString();
    }
    
    /**
     * Retain characters, numbers, underscores, hyphens, and periods.  Any other 
     * character is replaced with an underscore.
     * @param name
     * @return 
     */
    public static String cleanFileName(String name) {
        return cleanFileName(name, false);
    }
    
    
    public static void ensureFileExists(File f)
            throws IOException
    {
        if (!f.exists()) {
            FileWriter fw;
            
            fw=new FileWriter(f);
            fw.write("");
            fw.flush();
            fw.close();
        }
        
    }
    
    /**
     * If the file doesn't exist, it gets created.  If it exists, its contents
     * are removed.
     * @param f 
     * @throws IOException
     */
    public static void clearFile(File f)
            throws IOException
    {
        FileWriter fw;

        fw=new FileWriter(f);
        fw.write("");
        fw.flush();
        fw.close();
    }
    
    
    
    public static byte[] readBinaryFile(File f)
            throws IOException
    {
        FileInputStream fis;
        ByteArrayOutputStream baos;
        byte[] buffer;
        int bytesRead;
        
        buffer=new byte[1024 * 1024];
        baos=new ByteArrayOutputStream(1024);
        
        fis=new FileInputStream(f);
        
        while (-1 != (bytesRead = fis.read(buffer))) {
            baos.write(buffer, 0, bytesRead);
        }
        fis.close();
        
        return baos.toByteArray();
    }
    
    
    public static void saveBinaryFile(File targetFile,
            byte[] content)
            throws IOException
    {
	FileOutputStream fos;

	fos=new FileOutputStream(targetFile);
        fos.write(content);
	fos.close();
    }
    
    /**
     * It is assumed that proposedFilename has an extension.
     * 
     * @param targetDirectory
     * @param proposedFilename
     * @return 
     */
    public static Filename getUniqueFilename(final File targetDirectory, final String proposedFilename)
    {
        Filename retVal;
        File possibleTargetFile;
        
        retVal=new Filename(proposedFilename, false);
        possibleTargetFile=new File(targetDirectory, retVal.getFullName());
        while (possibleTargetFile.exists()) {
            retVal.appendNumberToFileName(1);
            possibleTargetFile=new File(targetDirectory, retVal.getFullName());
        } 
        
        return retVal;
    }
}
