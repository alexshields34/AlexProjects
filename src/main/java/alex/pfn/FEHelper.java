package alex.pfn;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import alex.pfn.image.ImageGroup;
import alex.pfn.image.ImageSpec;
import alex.pfn.image.ImagePrefixType;



/**
 * "yourTurn" wasn't originally a prefix type.
 * 
 * @author alex
 *
 */
public class FEHelper {
	

    @Autowired
    private ServletContext servletContext;
//	@Autowired
//	private GameServer gameServer;
    
    private EnumMap<ImagePrefixType, ImageGroup> imageGroups;
    
    private final Random rnd;
    
	public FEHelper()
	{
		imageGroups=null;
		rnd=new Random(System.currentTimeMillis());
	}
	
	@PostConstruct
	public void postConstruct() {
		System.out.println(this.getClass().getName() + ":: postConstruct:: ENTERING");
		imageGroups=buildAllImageGroupsAsMap();
	}
	
	public EnumMap<ImagePrefixType, ImageSpec> getRandomImageForAllVictoryTypes()
	{
		EnumMap<ImagePrefixType, ImageSpec> retVal;
		
		retVal=new EnumMap<ImagePrefixType, ImageSpec>(ImagePrefixType.class);
		for (ImagePrefixType ivt: ImagePrefixType.values()) {
			retVal.put(ivt, getRandomImageForPrefixType(ivt));
		}
		
		return retVal;
	}
	
	
	
	public ImageSpec getRandomImageForPrefixType(ImagePrefixType ivt)
	{
		ImageGroup ig;
		int size, whichImg;
		
		ig=this.imageGroups.get(ivt);
		
		size=ig.getImages().size();
		
		whichImg=rnd.nextInt(size);
		
		return ig.getImages().get(whichImg);
	}
	
	

	/**
	 * Return the location of all end game images.  It gets returned as an EnumMap and the calling
	 * method must turn it into json if necessary.
	 * @return
	 */
	public EnumMap<ImagePrefixType, ImageGroup> getAllImageGroupsAsMap() {
		return imageGroups;
	}

	
	
    private EnumMap<ImagePrefixType, ImageGroup> buildAllImageGroupsAsMap() {
    	
    	EnumMap<ImagePrefixType, ImageGroup> map;
    	ImageGroup oneGroup;
		Set<String> paths;
		
    	
    	map=new EnumMap<ImagePrefixType, ImageGroup>(ImagePrefixType.class);
    	
    	for (ImagePrefixType oneType: ImagePrefixType.values()) {
			oneGroup=new ImageGroup(oneType, Configuration.getConfigProperties().getProperty(oneType.getConfigParameterForPrefix()));
			map.put(oneType, oneGroup);
    	}
    	
       	paths=servletContext.getResourcePaths("/"+Constants.imageDirectoryURLPath);
       	for (String onePath: paths) {
       		for (ImagePrefixType oneType: ImagePrefixType.values()) {
				oneGroup=map.get(oneType);
				
       			if (onePath.contains(oneGroup.getImageFilePrefix())) {
       				oneGroup.getImages().add(new ImageSpec(onePath));
       			}
        	}
       		
       		// Let's create a file for testing.
       		// THIS WORKED to create a file in the image directory.
       		// I want to use something similar to create temporary files that
       		// get deleted when the jvm exits.
//       		try {
//       		String realPath;
//       		File parentDirectory, child, imgFile;
//       		realPath=servletContext.getRealPath(onePath);
//       		imgFile=new File(realPath);
//       		parentDirectory=imgFile.getParentFile();
//       		child=new File(parentDirectory, "junk.txt");
//       		FileUtil.appendToFile(child, (new java.util.Date()).toString(), true);
//       		} catch (Exception e) {
//       			e.printStackTrace();
//       		}
       	}
       	
       	return map;
    }
    
    public String getSingleRandomCardFile()
    {
    	List<String> list;
    	
    	list=getAllCardFileNames();
    	
    	return list.get(rnd.nextInt(list.size()));
    }
    
    
    public List<String> getAllCardFileNames() {
    	String s;
    	List<String> nameList;
    	File cardDirectory;
    	String[] textFileNames;
    	

    	nameList=new ArrayList<String>(); 

    	s=Configuration.getConfigProperties().getProperty(Constants.config_dir_cards);
    	cardDirectory=new File(s);
    	
    	textFileNames=cardDirectory.list(new FilenameFilter() {
    		@Override
    		public boolean accept(File arg0, String arg1) {
    			return arg1.toLowerCase().endsWith(".txt");
    		}
    	});
    	
       	for (String oneName: textFileNames) {
       		nameList.add(oneName);
       	}
       	
    	return nameList;
    }

}
