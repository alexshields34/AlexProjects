package alex.pfn.image;

import java.util.ArrayList;

public class ImageGroup {

	private final ImagePrefixType type;
	private final ArrayList<ImageSpec> images;
	private final String imageFilePrefix;
	
	public ImageGroup(final ImagePrefixType type, final String imageFilePrefix)
	{
		this.type=type;
		this.imageFilePrefix=imageFilePrefix;
		
		images=new ArrayList<ImageSpec>();
	}

	public ImagePrefixType getType() {
		return type;
	}

	public ArrayList<ImageSpec> getImages() {
		return images;
	}

	public String getImageFilePrefix() {
		return imageFilePrefix;
	}
	
}
