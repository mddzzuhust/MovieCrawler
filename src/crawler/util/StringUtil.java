package crawler.util;

public class StringUtil {

	public static String getSubstring(String src, String strFrom, String strTo) {
        int tagBegin;
        int beginLabel, endLabel;
        String desValue = "";
        if (!src.isEmpty()) {
            tagBegin = src.indexOf(strFrom);
            beginLabel = tagBegin + strFrom.length();
            endLabel = src.indexOf(strTo, beginLabel);
            if(tagBegin == -1 || endLabel == -1)
            	return desValue;
            desValue = src.substring(beginLabel, endLabel);
        }
        return desValue;
    }
    
	public static int hexString2Int(String hexStr) {
        if (hexStr.startsWith("0x") || hexStr.startsWith("0X")) {
            hexStr = hexStr.substring(2);
        }
        int scale = 1, hexStrLen = hexStr.length();
        int intValue = 0;
        for (int i = hexStrLen - 1; i >= 0; i--) {
            char c = hexStr.charAt(i);
            if (Character.isDigit(c)) {
                intValue += scale * (c - '0');
            } else if (Character.isUpperCase(c)) {
                intValue += scale * (c - 'A' + 10);
            } else if (Character.isLowerCase(c)) {
                intValue += scale * (c - 'a' + 10);
            } else {
                return -1;
            }
            scale *= 16;
        }
        return intValue;
    }
    
	/**
     * 将unicode码转为需要的格式
     * @param unicodestr
     * @return
     */
    public static String decodeUnicode(final String unicodestr) {
		final StringBuffer buffer = new StringBuffer();
		String tempStr = "";
		String operStr = unicodestr;
		
		if (operStr != null && operStr.indexOf("\\u") == -1)
			return buffer.append(operStr).toString(); // 
		if (operStr != null && !operStr.equals("") && !operStr.startsWith("\\u")) { // 
			tempStr = operStr.substring(0, operStr.indexOf("\\u")); //  
			// operStr字符一定是以unicode编码字符打头的字符串
			operStr = operStr.substring(operStr.indexOf("\\u"), operStr.length());
		}
		buffer.append(tempStr);
		while (operStr != null && !operStr.equals("") && operStr.startsWith("\\u")) {
			// 循环处理,处理对象一定是以unicode编码字符打头的字符串
			tempStr = operStr.substring(0, 6);
			operStr = operStr.substring(6, operStr.length());
			String charStr = "";
			charStr = tempStr.substring(2, tempStr.length());
			char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
			buffer.append(new Character(letter).toString());
			if (operStr.indexOf("\\u") == -1) { //  
				buffer.append(operStr);
			} else { // 处理operStr使其打头字符为unicode字符
				tempStr = operStr.substring(0, operStr.indexOf("\\u"));
				operStr = operStr.substring(operStr.indexOf("\\u"), operStr.length());
				buffer.append(tempStr);
			}
		}
		return buffer.toString();
	}
}
