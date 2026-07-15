package com.farmkart.starter.common.geo;

/**
 * Haversine distance for geo-matching (warehouse, logistics, etc.).
 */
public final class GeoDistanceUtil {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private GeoDistanceUtil() {}

    /**
     * Great-circle distance in kilometres between two WGS-84 points.
     */
    public static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    public static boolean withinRadiusKm(double lat1, double lon1, double lat2, double lon2, double radiusKm) {
        return haversineKm(lat1, lon1, lat2, lon2) <= radiusKm;
    }
}
