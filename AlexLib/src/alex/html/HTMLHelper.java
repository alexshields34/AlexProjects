package alex.html;

public class HTMLHelper {

	/**
	 * 
	 * @param values
	 * @param defaultValue May be null.
	 * @param nameValue The value of the 'name' and 'id' attributes of the select tag.
	 * May not be null.
	 * @return
	 */
	public static <E extends Enum<E>> String enumToSelectList(final E[] values,
			final E defaultValue,
			final String nameValue)
	{
		StringBuilder sb;
		String selectedString;
		
		sb=new StringBuilder("<select name='")
			.append(nameValue)
			.append("' id='")
			.append(nameValue)
			.append("'>");
		
		for (E oneValue: values) {
			if (defaultValue!=null && oneValue==defaultValue) {
				selectedString=" selected ";
			} else {
				selectedString="";
			}
			
			sb.append("<option ")
				.append(selectedString)
				.append(" value='")
				.append(oneValue)
				.append("'>\n")
				.append(oneValue)
				.append("</option>\n");
		}
		  
		sb.append("</select>\n");
		
		return sb.toString();
	}
}
