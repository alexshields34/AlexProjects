package alex.http.download;

/**
 * This contains the filename as gotten by a retrieveBinary call and it includes
 * the data as a byte[].
 * @author alex
 */
public class DataWithName {
    private final String name;
    
    private final byte[] data;

    public DataWithName(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }
    
    
}
