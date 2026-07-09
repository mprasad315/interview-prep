package coding.algorithms.twoPointer;

import java.util.*;

public class ThreeSum {
    /*
     * Sort array to easily skip over dupes & so we know if l or r should be incremented/decremented
     * i is leftmost value, fix it and then start two-pointer approach on the rest of the array to reach target sum
     */
    public static List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i-1]) continue;
            int left = i + 1;
            int right = nums.length - 1;
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    left++;
                    right--;
                    while (left < right && nums[left] == nums[left - 1]) {
                        left++;
                    }
                } else if (sum > 0) {
                    right--;
                } else {
                    left++;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[] nums = {-1, -2, 3, 4, 5};
        List<List<Integer>> result = threeSum(nums);
        System.out.println(result);
    }
}
