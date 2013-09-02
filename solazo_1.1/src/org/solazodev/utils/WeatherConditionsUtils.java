package org.solazodev.utils;

import org.solazodev.R;

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

        if (weatherCondition.indexOf("Mostly Cloudy") > 0 || weatherCondition.indexOf("Partly Cloudy") > 0) {
            if (isDayTime)
                return CLOUDY_DAY;
            return CLOUDY_NIGHT;
        }

        if (weatherCondition.indexOf("Snow") > 0) {
            if (isDayTime)
                return SNOW_DAY;
            return SNOW_NIGHT;
        }

        if (weatherCondition.indexOf("Overcast") > 0) return OVERCAST;

        if (weatherCondition.indexOf("A Few Clouds") > 0) {
            if (isDayTime)
                return SUNNY_DAY;
            return CLEAR_NIGHT;
        }

        if (weatherCondition.indexOf("Thunderstorm") > 0) {
            if (isDayTime)
                return THUNDERSTORM_DAY;
            return THUNDERSTORM_NIGHT;
        }

        if (weatherCondition.indexOf("Ice Pellets") > 0) {
            if (isDayTime)
                return HAIL_DAY;
            return HAIL_NIGHT;
        }

        if (weatherCondition.indexOf("Drizzle") > 0) {
            if (isDayTime)
                return DRIZZLE_DAY;
            return DRIZZLE_NIGHT;
        }

        if (weatherCondition.indexOf("Rain") > 0 || weatherCondition.indexOf("Shower") > 0) {
            if (isDayTime)
                return RAIN_DAY;
            return RAIN_NIGHT;
        }

        if (weatherCondition.indexOf("Windy") > 0 || weatherCondition.indexOf("Breezy") > 0) return WINDY;

        if (weatherCondition.indexOf("Fog") > 0 || weatherCondition.indexOf("Smoke") > 0) {
            if (isDayTime)
                return FOG_DAY;
            return FOG_NIGHT;
        }
        if (weatherCondition.indexOf("Fair") > 0 || weatherCondition.indexOf("Clear") > 0) {
            if (isDayTime)
                return SUNNY_DAY;
            return CLEAR_NIGHT;
        }
        return 0;
    }
}
