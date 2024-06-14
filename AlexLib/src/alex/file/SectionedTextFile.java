
package alex.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * This is a class that represents a text file that contains sections delimited
 * by section titles like [some name].
 * 
 * 
 * @author alex
 */
public class SectionedTextFile {
    
    // key=section title
    // value=every non-blank line in the section
    private HashMap<String, ArrayList<String>> content;
    
    // value=section titles.  Same as the key for content.
    private final ArrayList<String> sectionOrder;
    
    private int currentSectionForReading;
    
    /**
     * 
     * @param textualFileData
     * @throws IOException
     */
    public SectionedTextFile(String textualFileData)
            throws IOException
    {
        
        currentSectionForReading=0;
        sectionOrder=new ArrayList<String>();
        parse(textualFileData);
    }
    
    public SectionedTextFile(File sourceFile)
            throws FileNotFoundException, IOException
    {
        String s;
        
        currentSectionForReading=0;
        sectionOrder=new ArrayList<String>();
        
        s=FileUtil.readTextFile(sourceFile);
        
        parse(s);
    }
    
    public void resetSectionCounter() {
        this.currentSectionForReading=0;
    }
    
    /**
     * Return null if there are no more.
     * @return 
     */
    public String getNextSectionName() {
        String retVal;
        
        retVal=null;
        if (this.currentSectionForReading<sectionOrder.size()) {
            retVal=this.sectionOrder.get(this.currentSectionForReading++);
        }
        
        return retVal;
    }
    
    /**
     * Detect the end using getNextSectionName().
     * @param sectionName
     * @return 
     */
    public ArrayList<String> getDataForSection(String sectionName) {
        return this.content.get(sectionName);
    }
    
    
    public HashMap<String, ArrayList<String>> getSections()
    {
        return content;
    }
    
    public void addSection(String sectionName, ArrayList<String> lines) {
        ArrayList<String> value;
        
        value=content.get(sectionName);
        if (value==null) {
            value=new ArrayList<String>();
            content.put(sectionName, value);
            this.sectionOrder.add(sectionName);
        }
        
        value.addAll(lines);
    }
    
    public void addSection(String sectionName, String... lines) {
        ArrayList<String> list;
        
        list=new ArrayList<String>();
        list.addAll(Arrays.asList(lines));
        
        addSection(sectionName, list);
    }
    
    
    private void parse(String sourceText)
            throws IOException
    {
        
        BufferedReader br;
        String line, currentSectionName;
        StringReader sr;
        int index;
        ArrayList<String> listOfLines;
        
        content=new HashMap<String, ArrayList<String>>();
        listOfLines=null;
        
        sr=new StringReader(sourceText);
        br=new BufferedReader(sr);
        
        while (null!=(line=br.readLine())) {
            
            index=line.indexOf("#");
            
            if (index!=-1) {
                if (index==0) {
                    line="";
                } else {
                    line=line.substring(0, index);
                }
            }
            
            line=line.trim();
            if (line.isEmpty()) {
                continue;
            }
            
            // From this point downwards, the line has no comments in it,
            // it's trimmed,
            // and its length is greater than 0.
            
            if (line.charAt(0)=='[' && line.indexOf(']')!=-1) {
                currentSectionName=line.substring(1, line.indexOf(']'));
                
                listOfLines=content.get(currentSectionName);
                
                if (listOfLines==null) {
                    listOfLines=new ArrayList<String>();
                    content.put(currentSectionName, listOfLines);
                    sectionOrder.add(currentSectionName);
                }
            } else {
                if (listOfLines!=null) {
                    listOfLines.add(line);
                }
            }
            
        } 
        
    }
    
    
    public void writeToDisk(File outFile)
            throws IOException
    {
        FileWriter fw;
        PrintWriter pw;
        ArrayList<String> chunk;
        StringBuilder sb;
        
        fw=new FileWriter(outFile);
        pw=new PrintWriter(fw);
        
        sb=new StringBuilder(100);
        for (String key: this.sectionOrder) {
//        for (String key: this.content.keySet()) {
            sb.setLength(0);
            
            chunk=content.get(key);
            
            sb.append("[")
                    .append(key)
                    .append("]");
            
            pw.println(sb);
            
            for (String line: chunk) {
                pw.println(line);
            }
            pw.println();
        }
        
        pw.close();
        fw.close();
        
    }
    
}