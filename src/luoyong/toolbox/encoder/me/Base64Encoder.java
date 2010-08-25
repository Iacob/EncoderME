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
