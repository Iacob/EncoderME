package luoyong.toolbox.encoder.me;

/**
 *
 * @author Luo Yong &lt; luo.yong.name@gmail.com &gt;
 */
public class UrlEncoder {

   public static String encode(byte[] bytes) {

      if (bytes == null) {
         return null;
      }

      if (bytes.length < 1) {
         return null;
      }

      StringBuffer result = new StringBuffer();

      int arrayLength = bytes.length;

      int currentMath = 0;
      int currentCharValue = 0;

      for (int i=0; i<arrayLength; i++) {

         currentMath = bytes[i] & 0xFF;

         result.append('%');
         
         currentCharValue = currentMath / 16;
         if ((currentCharValue >=0) && (currentCharValue <10)) {
            result.append((char)(currentCharValue + 48));
         }
         if ((currentCharValue > 9) && (currentCharValue < 16)) {
            result.append((char)(currentCharValue + 55));
         }
         
         currentCharValue = currentMath % 16;
         if ((currentCharValue >=0) && (currentCharValue <10)) {
            result.append((char)(currentCharValue + 48));
         }
         if ((currentCharValue > 9) && (currentCharValue < 16)) {
            result.append((char)(currentCharValue + 55));
         }
      }

      return result.toString();
   }
}
