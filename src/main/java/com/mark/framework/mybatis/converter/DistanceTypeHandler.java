package com.mark.framework.mybatis.converter;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 距离转换器
 *
 * @author mark
 * @date 2017-10-21
 */
public class DistanceTypeHandler extends BaseTypeHandler<Object> {

    private final static BigDecimal KM = new BigDecimal(1000);

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter,
                                    JdbcType jdbcType) throws SQLException {
        BigDecimal paramData = new BigDecimal(parameter.toString()).setScale(6, BigDecimal.ROUND_HALF_UP);
        ps.setString(i, paramData.toPlainString());
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        return convert(rs.getString(columnName));
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return convert(rs.getString(columnIndex));
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return convert(cs.getString(columnIndex));
    }

    private Object convert(String text) {
        if (text == null) {
            return null;
        }
        BigDecimal paramData = new BigDecimal(text.toString()).setScale(0, BigDecimal.ROUND_HALF_UP);
        if (KM.compareTo(paramData) > 0) {
            return paramData + "米";
        } else {
            return paramData.movePointLeft(3) + "千米";
        }
    }
}