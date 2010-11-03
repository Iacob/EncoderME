package luoyong.toolbox.encoder.me;

/**
 *
 * @author Luo Yong &lt; luo.yong.name@gmail.com &gt;
 */
public class Base64Encoder {

   public static String encode(byte[] bytes) {
      if (bytes == null) {
         return null;
      }
      if (bytes.length == 0) {
         return "";
      }

      int groupCount = bytes.length / 3;

      StringBuffer resultBuffer = new StringBuffer();

      byte bufferByte = 0;
      for (int i=0; i<groupCount; i++) {
         int startIndex = i * 3;
         
         bufferByte = (byte)(bytes[startIndex] >> 2);
         resultBuffer.append(encodeBase64Byte(bufferByte));
         
         bufferByte = (byte)(((bytes[startIndex] & 0x03) << 4)
                 | (bytes[startIndex + 1] >> 4));
         resultBuffer.append(encodeBase64Byte(bufferByte));
         
         bufferByte = (byte)(((bytes[startIndex + 1] & 0x0F) << 2)
                 | (bytes[startIndex + 2] >> 6));
         resultBuffer.append(encodeBase64Byte(bufferByte));
         
         bufferByte = (byte)(bytes[startIndex + 2] & 0x3F);
         resultBuffer.append(encodeBase64Byte(bufferByte));
      }

      if ((bytes.length - (groupCount * 3)) == 2)  {
         resultBuffer.append(encodeLast2Bytes(bytes));
      }

      if ((bytes.length - (groupCount * 3)) == 1)  {
         resultBuffer.append(encodeLast1Byte(bytes));
      }

      return resultBuffer.toString();
   }

   public static byte[] decode(String base64String)
           throws Base64FormatException {
      
      if (base64String == null) {
         return null;
      }

      int base64StringLength = base64String.length();

      if ((base64StringLength % 4) != 0) {
         throw new Base64FormatException(
                 "The base 64 string does not have correct format.");
      }

      int resultLength = 0;

      int fullDataGroupCount = 0;
      int paddingGroupCount = 0;

      if (base64String.endsWith("==")) {
         
         resultLength = base64String.length() / 4 * 3 - 2;
         
         fullDataGroupCount = base64StringLength / 4 - 1;
         paddingGroupCount = 1;
         
      }else if (base64String.endsWith("=")) {

         resultLength = base64String.length() / 4 * 3 - 1;

         fullDataGroupCount = base64StringLength / 4 - 1;
         paddingGroupCount = 1;
         
      }else {
         
         resultLength = base64String.length() / 4 * 3;

         fullDataGroupCount = base64StringLength / 4;
         paddingGroupCount = 0;
      }

      byte resultData[] = new byte[resultLength];

      for (int i=0; i<fullDataGroupCount; i++) {
         decodeBase64FullDataGroup(resultData, base64String, i);
      }

      if (paddingGroupCount > 0) {
         decodeBase64PaddingGroup(resultData, base64String);
      }

      return resultData;
   }

   private static void decodeBase64FullDataGroup(
           byte[] resultData, String base64String, int group)
              throws Base64FormatException {

      int base64Offset = group * 4;
      byte base64Byte1 = decodeBase64Char(base64String.charAt(base64Offset));
      base64Offset++;
      byte base64Byte2 = decodeBase64Char(base64String.charAt(base64Offset));
      base64Offset++;
      byte base64Byte3 = decodeBase64Char(base64String.charAt(base64Offset));
      base64Offset++;
      byte base64Byte4 = decodeBase64Char(base64String.charAt(base64Offset));

      int dataOffset = group * 3;
      resultData[dataOffset] = (byte)((base64Byte1 << 2) | (base64Byte2 >> 4));
      dataOffset++;
      resultData[dataOffset] = (byte)((base64Byte2 << 4) | (base64Byte3 >> 2));
      dataOffset++;
      resultData[dataOffset] = (byte)((base64Byte3 << 6) | (base64Byte4));
   }

   private static void decodeBase64PaddingGroup(
           byte[] resultData, String base64String)
              throws Base64FormatException {

      int base64StringLength = base64String.length();

      char char1 = base64String.charAt(base64StringLength - 4);
      char char2 = base64String.charAt(base64StringLength - 3);
      char char3 = base64String.charAt(base64StringLength - 2);
      char char4 = base64String.charAt(base64StringLength - 1);

      int resultDataLength = resultData.length;

      if ((char3 == '=') && (char4 == '=')) {
         byte byte1 = decodeBase64Char(char1);
         byte byte2 = decodeBase64Char(char2);
         resultData[resultDataLength-1] = (byte)((byte1 << 2) | (byte2 >> 4));
      }

      if ((char3 != '=') && (char4 == '=')) {
         byte byte1 = decodeBase64Char(char1);
         byte byte2 = decodeBase64Char(char2);
         byte byte3 = decodeBase64Char(char3);
         resultData[resultDataLength-2] = (byte)((byte1 << 2) | (byte2 >> 4));
         resultData[resultDataLength-1] = (byte)((byte2 << 4) | (byte3 >> 2));
      }

      if ((char3 != '=') && (char4 != '=')) {
         byte byte1 = decodeBase64Char(char1);
         byte byte2 = decodeBase64Char(char2);
         byte byte3 = decodeBase64Char(char3);
         byte byte4 = decodeBase64Char(char4);
         resultData[resultDataLength-3] = (byte)((byte1 << 2) | (byte2 >> 4));
         resultData[resultDataLength-2] = (byte)((byte2 << 4) | (byte3 >> 2));
         resultData[resultDataLength-1] = (byte)((byte3 << 6) | (byte4));
      }
   }

   private static byte decodeBase64Char(char base64Char) throws Base64FormatException {
      
      if ((base64Char >= 'A') && (base64Char <= 'Z')) {
         return (byte)(base64Char - 65);
      }else if ((base64Char >= 'a') && (base64Char <='z')) {
         return (byte)(base64Char - 97 + 26);
      }else if ((base64Char >= '0') && (base64Char <= '9')) {
         return (byte)(base64Char  - 48 + 52);
      }else if (base64Char == '+') {
         return 62;
      }else if (base64Char == '/') {
         return 63;
      }else {
         throw new Base64FormatException(
                 "The base64 string contains invalid character");
      }
   }

   private static char encodeBase64Byte(byte base64Byte) {
      if ((base64Byte >=0) && (base64Byte <= 25)) {
         return (char)(base64Byte + 65);
      }else if ((base64Byte >= 26) && (base64Byte <= 51)) {
         return (char)(base64Byte - 26 + 97);
      }else if ((base64Byte >= 52) && (base64Byte <= 61)) {
         return (char)(base64Byte - 52 + 48);
      }else if (base64Byte == 62) {
         return '+';
      }else if (base64Byte == 63) {
         return '/';
      }else {
         return '0';
      }
   }

   private static String encodeLast2Bytes(byte bytes[]) {
      byte firstByte = bytes[bytes.length - 2];
      byte secondByte = bytes[bytes.length - 1];

      StringBuffer resultBuffer = new StringBuffer();

      byte bufferByte = 0;

      bufferByte = (byte) (firstByte >> 2);
      resultBuffer.append(encodeBase64Byte(bufferByte));

      bufferByte = (byte) (((firstByte & 0x03) << 4)
              | (secondByte >> 4));
      resultBuffer.append(encodeBase64Byte(bufferByte));

      bufferByte = (byte) ((secondByte & 0x0F) << 2);
      resultBuffer.append(encodeBase64Byte(bufferByte));

      resultBuffer.append('=');

      return resultBuffer.toString();
   }

   private static String encodeLast1Byte(byte bytes[]) {
      byte lastByte = bytes[bytes.length - 1];

      StringBuffer resultBuffer = new StringBuffer();

      byte bufferByte = 0;

      bufferByte = (byte) (lastByte >> 2);
      resultBuffer.append(encodeBase64Byte(bufferByte));

      bufferByte = (byte) ((lastByte & 0x03) << 4);
      resultBuffer.append(encodeBase64Byte(bufferByte));

      resultBuffer.append('=');
      resultBuffer.append('=');

      return resultBuffer.toString();
   }
}
