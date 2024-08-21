package com.luna.jetoverlay.misc;

public class EasingHUDUtils {
    public static double EaseInSine(double __number) {
        return 1 - Math.cos((__number * Math.PI) / 2);
    }
    public static double EaseInCircle(double __number) {
        return 1 - Math.sqrt(1 - Math.pow(__number, 2));
    }
    public static double EaseInOutCircle(double __number) {
        return __number < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * __number, 2))) / 2
        : (Math.sqrt( 1 - Math.pow(-2 * __number + 2, 2)) + 1) / 2;
    }
    public static float Lerp(float __startValue, float __endValue, float __pct ) {
        return (__startValue + (__endValue - __startValue) * __pct);
    }

}
