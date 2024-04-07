package com.baomidou.mybatisplus.extension.toolkit;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ByteOrderValues;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;

/**
 * Geometry 工具类
 * <p>仅MySQL适用</p>
 * <p>在Mysql中，Geometry类型的数据可以通过ST_AsWKT()函数转换为WKT格式的字符串。</p>
 * <p>默认的经纬度顺序是：纬度在前，经度在后，可以通过axis-order参数来指定经纬度的顺序。</p>
 * <p>SELECT id, ST_ASWKT(location, 'axis-order=long-lat') FROM geom; </p>
 */
@SuppressWarnings({"squid:S112", "unused"})
public class GeomUtils {

    /**
     * 默认坐标系ID (空间引用标识符，Spatial Reference ID)
     * <p>建议从Json序列化到对象时指定合适的坐标系</p>
     */
    public static final Integer SRID = 4326;

    /**
     * MySQL使用 {@link ByteOrder#LITTLE_ENDIAN} 存储
     * <p>读写时都需要使用 {@link ByteOrder#LITTLE_ENDIAN} </p>
     */
    private static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    private static final int BYTE_ORDER_INT = ByteOrderValues.LITTLE_ENDIAN;

    public static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), SRID);

    public static final WKTReader wktReader = new WKTReader(geometryFactory);

    public static final WKTWriter wktWriter = new WKTWriter();

    public static final WKBReader wkbReader = new WKBReader();

    public static final WKBWriter wkbWriter = new WKBWriter(2, BYTE_ORDER_INT);

    private GeomUtils() {
    }

    /**
     * WKT转Geometry
     */
    public static Geometry toGeom(String wkt) {
        try {
            return wktReader.read(wkt);
        } catch (ParseException e) {
            throw new RuntimeException("WKT -> Geometry ERR" + e.getMessage());
        }
    }

    /**
     * Geometry转WKT
     */
    public static String toWKT(Geometry geometry) {
        return wktWriter.write(geometry);
    }

    /**
     * MySQL_WKB -> Geometry
     **/
    public static Geometry fromMysqlWkb(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        byte[] geomBytes = ByteBuffer.allocate(bytes.length - 4)
                .order(BYTE_ORDER)
                .put(bytes, 4, bytes.length - 4)
                .array();
        try {
            Geometry geometry = wkbReader.read(geomBytes);
            geometry.setSRID(readSRID(bytes));
            return geometry;
        } catch (Exception e) {
            throw new RuntimeException("WKB -> Geometry ERR" + e.getMessage());
        }
    }

    /**
     * 从wkb中读取srid
     */
    private static int readSRID(byte[] bytes) {
        return ByteOrderValues.getInt(bytes, BYTE_ORDER_INT);
    }

    /**
     * Geometry -> MySQL_WKB
     **/
    public static byte[] toMysqlWkb(Geometry geometry) {
        if (geometry == null) {
            return new byte[0];
        }
        if (geometry.getSRID() == 0) {
            geometry.setSRID(SRID);
        }
        byte[] geomBytes = wkbWriter.write(geometry);
        return ByteBuffer.allocate(geomBytes.length + 4)
                .order(BYTE_ORDER)
                .putInt(geometry.getSRID())
                .put(geomBytes)
                .array();
    }
}
