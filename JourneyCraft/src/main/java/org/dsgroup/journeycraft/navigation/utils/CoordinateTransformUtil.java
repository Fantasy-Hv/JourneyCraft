package org.dsgroup.journeycraft.navigation.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility for WGS-84 ↔ GCJ-02 coordinate conversion using the standard GCJ-02
 * offset algorithm (Chinese national standard for map coordinate obfuscation).
 *
 * <p>All methods use pure Java math — no external dependencies.</p>
 */
public final class CoordinateTransformUtil {

    private static final double A = 6378245.0;
    private static final double EE = 0.00669342162296594323;
    private static final double PI = Math.PI;

    private CoordinateTransformUtil() {
    }

    /**
     * GCJ-02 coordinate pair. GCJ-02 is the obfuscated coordinate system used
     * by Chinese map providers (Amap, Tencent Maps, etc.).
     */
    public record Gcj02Coord(double lat, double lng) {
    }

    /**
     * WGS-84 coordinate pair. WGS-84 is the standard GPS coordinate system
     * used worldwide.
     */
    public record Wgs84Coord(double lat, double lng) {
    }

    /**
     * Converts a single WGS-84 coordinate to GCJ-02.
     * <p>
     * If the coordinate is outside mainland China (lat &lt; 0.8293 or
     * lat &gt; 55.8271 or lng &lt; 72.004 or lng &gt; 137.8347), the
     * coordinate is returned unchanged.
     *
     * @param lat WGS-84 latitude in degrees
     * @param lng WGS-84 longitude in degrees
     * @return the corresponding GCJ-02 coordinate
     */
    public static Gcj02Coord wgs84ToGcj02(double lat, double lng) {
        if (isOutsideChina(lat, lng)) {
            return new Gcj02Coord(lat, lng);
        }
        double dLat = transformLat(lng - 105.0, lat - 35.0);
        double dLng = transformLng(lng - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLng = (dLng * 180.0) / (A / sqrtMagic * Math.cos(radLat) * PI);
        return new Gcj02Coord(lat + dLat, lng + dLng);
    }

    /**
     * Converts a single GCJ-02 coordinate back to WGS-84 using iterative
     * approximation (typically 2–3 iterations, converging to within 0.1 m).
     *
     * @param lat GCJ-02 latitude in degrees
     * @param lng GCJ-02 longitude in degrees
     * @return the corresponding WGS-84 coordinate
     */
    public static Wgs84Coord gcj02ToWgs84(double lat, double lng) {
        if (isOutsideChina(lat, lng)) {
            return new Wgs84Coord(lat, lng);
        }
        double wgsLat = lat;
        double wgsLng = lng;
        for (int i = 0; i < 3; i++) {
            Gcj02Coord gcj = wgs84ToGcj02(wgsLat, wgsLng);
            double dLat = gcj.lat() - lat;
            double dLng = gcj.lng() - lng;
            wgsLat -= dLat;
            wgsLng -= dLng;
            if (Math.abs(dLat) < 1e-7 && Math.abs(dLng) < 1e-7) {
                break;
            }
        }
        return new Wgs84Coord(wgsLat, wgsLng);
    }

    /**
     * Batch-converts a list of WGS-84 coordinates to GCJ-02.
     *
     * @param coords list of WGS-84 coordinates (non-null elements expected)
     * @return a new list of corresponding GCJ-02 coordinates in the same order
     */
    public static List<Gcj02Coord> wgs84ToGcj02Batch(List<Wgs84Coord> coords) {
        List<Gcj02Coord> result = new ArrayList<>(coords.size());
        for (Wgs84Coord c : coords) {
            result.add(wgs84ToGcj02(c.lat(), c.lng()));
        }
        return result;
    }

    private static boolean isOutsideChina(double lat, double lng) {
        return lat < 0.8293 || lat > 55.8271 || lng < 72.004 || lng > 137.8347;
    }

    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320.0 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLng(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y
                + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 15.0 * PI)) * 2.0 / 3.0;
        return ret;
    }
}
