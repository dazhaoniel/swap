package org.solazo.utils;

import org.solazo.R;

/**
 * Created by Daniel Zhao on 8/21/13.
 */
public final class WeatherConditionsUtils {

    public static final int SNOW_DAY = R.drawable.snow_alt;

    public static final int SNOW_NIGHT = R.drawable.snow_moon_alt;

    public static final int CLOUDY_DAY = R.drawable.cloudy_sun;

    public static final int CLOUDY_NIGHT = R.drawable.cloudy_moon;

    public static final int SUNNY_DAY = R.drawable.sun;

    public static final int CLEAR_NIGHT = R.drawable.cloudy_moon;

    public static final int OVERCAST = R.drawable.cloudy;

    public static final int FOG_DAY = R.drawable.fog_sun_alt;

    public static final int FOG_NIGHT = R.drawable.fog_moon_alt;

    public static final int HAIL_DAY = R.drawable.hail_alt;

    public static final int HAIL_NIGHT = R.drawable.hail_moon_alt;

    public static final int RAIN_DAY = R.drawable.rain;

    public static final int RAIN_NIGHT = R.drawable.rain_moon;

    public static final int DRIZZLE_DAY = R.drawable.drizzle_alt;

    public static final int DRIZZLE_NIGHT = R.drawable.drizzle_moon_alt;

    public static final int THUNDERSTORM_DAY = R.drawable.lightning;

    public static final int THUNDERSTORM_NIGHT = R.drawable.lightning_moon;

    public static final int WINDY = R.drawable.wind_alt;

    public static int getDrawablefromWeatherCondition(String weatherCondition) {
        boolean isDayTime = DateUtils.isDay();

        if (weatherCondition == null) return 0;

        weatherCondition = weatherCondition.toLowerCase();

        if (weatherCondition.contains("mostly cloudy") || weatherCondition.contains("partly cloudy")) {
            if (isDayTime)
                return CLOUDY_DAY;
            return CLOUDY_NIGHT;
        }

        if (weatherCondition.contains("snow")) {
            if (isDayTime)
                return SNOW_DAY;
            return SNOW_NIGHT;
        }

        if (weatherCondition.contains("overcast")) return OVERCAST;

        if (weatherCondition.contains("a few clouds")) {
            if (isDayTime)
                return SUNNY_DAY;
            return CLEAR_NIGHT;
        }

        if (weatherCondition.contains("thunderstorm")) {
            if (isDayTime)
                return THUNDERSTORM_DAY;
            return THUNDERSTORM_NIGHT;
        }

        if (weatherCondition.contains("ice pellets")) {
            if (isDayTime)
                return HAIL_DAY;
            return HAIL_NIGHT;
        }

        if (weatherCondition.contains("drizzle")) {
            if (isDayTime)
                return DRIZZLE_DAY;
            return DRIZZLE_NIGHT;
        }

        if (weatherCondition.contains("rain") || weatherCondition.contains("shower")) {
            if (isDayTime)
                return RAIN_DAY;
            return RAIN_NIGHT;
        }

        if (weatherCondition.contains("windy") || weatherCondition.contains("breezy")) return WINDY;

        if (weatherCondition.contains("fog") || weatherCondition.contains("smoke")) {
            if (isDayTime)
                return FOG_DAY;
            return FOG_NIGHT;
        }
        if (weatherCondition.contains("fair") || weatherCondition.contains("clear")) {
            if (isDayTime)
                return SUNNY_DAY;
            return CLEAR_NIGHT;
        }
        return 0;
    }
}
