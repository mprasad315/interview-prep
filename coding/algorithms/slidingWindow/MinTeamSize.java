package coding.algorithms.slidingWindow;

import java.util.Arrays;

public class MinTeamSize {
    public static int[] minTeamSize(int[] talent, int talentsCount) {
        int[] result = new int[talent.length];
        int[] talentFreq = new int[talentsCount + 1];
        int left = 0;
        int distinct = 0;

        for (int right = 0; right < talent.length; right++) {
            int currTalent = talent[right];

            if (talentFreq[currTalent] == 0) {
                distinct++;
            }
            talentFreq[currTalent]++;

            while (distinct == talentsCount) {
                result[left] = right - left + 1;

                talentFreq[talent[left]]--;
                if (talentFreq[talent[left]] == 0) {
                    distinct--;
                }

                left++;
            }
        }

        while (left < talent.length) {
            result[left++] = -1;
        }

        return result;
    }

    public static void main(String[] args) {
        runTests();
        System.out.println("All MinTeamSize tests passed.");
    }

    private static void runTests() {
        assertArrayEquals(new int[]{3, 4, 3, -1, -1},
                minTeamSize(new int[]{1, 2, 3, 2, 1}, 3),
                "Example case with 3 distinct talents");

        assertArrayEquals(new int[]{2, 2, -1},
                minTeamSize(new int[]{1, 2, 1}, 2),
                "Two distinct talents in a sliding window");

        assertArrayEquals(new int[]{1, 1, 1},
                minTeamSize(new int[]{1, 1, 1}, 1),
                "Single distinct talent should always produce window size 1");

        assertArrayEquals(new int[]{4, -1, -1, -1},
                minTeamSize(new int[]{1, 2, 3, 4}, 4),
                "Exact overall distinct count should produce one valid window");

        assertArrayEquals(new int[]{-1, -1, -1, -1},
                minTeamSize(new int[]{1, 2, 1, 2}, 5),
                "Requested talent count larger than unique values should produce no valid windows");

        assertArrayEquals(new int[]{4, 3, -1, -1, -1},
                minTeamSize(new int[]{1, 1, 2, 3, 2}, 3),
                "Window size should shrink while preserving all required distinct talents");
    }

    private static void assertArrayEquals(int[] expected, int[] actual, String message) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(message + "\nExpected: " + Arrays.toString(expected)
                    + "\nActual:   " + Arrays.toString(actual));
        }
    }
}
