package coding.algorithms.twoPointer;

public class StringCompression {
    /*
     * Iterate through chars, technically 3 pointers – one for tracking the length of result, and left and right
     * Right should start at left, increment while chars are equal. Then once it breaks, calculate the count of repeated
     * chars and set the result len pointer to chars[left] and then ++ to count (converted into char array). Then set left
     * pointer to location of the right and start again.
     */
    public int compress(char[] chars) {
        int result = 0;
        int i = 0;
        int len = chars.length;
        
        while (i < len) {
            int r = i;
            while (r < len && chars[r] == chars[i]) r++;
            int count = r - i;
            chars[result++] = chars[i];
            if (count > 1) {
                for (char letter : String.valueOf(count).toCharArray()) {
                    chars[result++] = letter;
                }
            }
            i = r;
        }
        return result;
    }
}
