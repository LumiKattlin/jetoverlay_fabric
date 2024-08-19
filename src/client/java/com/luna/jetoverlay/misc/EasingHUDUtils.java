package com.luna.jetoverlay.misc;

public class EasingHUDUtils {
    public double EaseInSine(double __number) {
        return 1 - Math.cos((__number * Math.PI) / 2);
    }
    public double EaseInCircle(double __number) {
        return 1 - Math.sqrt(1 - Math.pow(__number, 2));
    }
    public double EaseInOutCircle(double __number) {
        return __number < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * __number, 2))) / 2
        : (Math.sqrt( 1 - Math.pow(-2 * __number + 2, 2)) + 1) / 2;
    }

}
