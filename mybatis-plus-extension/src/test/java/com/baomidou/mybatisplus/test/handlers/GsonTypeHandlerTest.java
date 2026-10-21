package com.baomidou.mybatisplus.test.handlers;

import com.baomidou.mybatisplus.extension.handlers.GsonTypeHandler;
import com.baomidou.mybatisplus.test.model.UserBean;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author nieqiurong 2020/2/28.
 */
@ExtendWith(MockitoExtension.class)
public class GsonTypeHandlerTest extends TypeHandlerTestSupport {

    private static final GsonTypeHandler GSON_TYPE_HANDLER = new GsonTypeHandler(UserBean.class);

    @Test
    @Override
    public void setParameter() throws Exception {
        GSON_TYPE_HANDLER.setParameter(preparedStatement, 1, null, JdbcType.VARCHAR);
        verify(preparedStatement).setNull(1, JdbcType.VARCHAR.TYPE_CODE);
        GSON_TYPE_HANDLER.setParameter(preparedStatement, 2, "", JdbcType.VARCHAR);
        verify(preparedStatement).setString(2, "\"\"");
        GSON_TYPE_HANDLER.setParameter(preparedStatement, 3, "{}", JdbcType.VARCHAR);
        verify(preparedStatement).setString(3, "\"{}\"");
    }

    @Test
    @Override
    public void getResultFromResultSetByColumnName() throws Exception {
        UserBean bean;
        when(resultSet.getString("column")).thenReturn(null);
        bean = (UserBean) GSON_TYPE_HANDLER.getResult(resultSet, "column");
        assertThat(bean).isNull();
        when(resultSet.getString("column")).thenReturn("");
        bean = (UserBean) GSON_TYPE_HANDLER.getResult(resultSet, "column");
        assertThat(bean).isNull();
        when(resultSet.getString("column")).thenReturn("{\"id\":123,\"name\":\"测试\"}");
        bean = (UserBean) GSON_TYPE_HANDLER.getResult(resultSet, "column");
        assertThat(bean.getId()).isEqualTo(123L);
        assertThat(bean.getName()).isEqualTo("测试");
    }

    @Test
    @Override
    public void getResultFromResultSetByColumnIndex() throws Exception {
        UserBean bean;
        when(resultSet.getString(1)).thenReturn(null);
        bean = (UserBean) GSON_TYPE_HANDLER.getResult(resultSet, 1);
        assertThat(bean).isNull();
        when(resultSet.getString(2)).thenReturn("");
        bean = (UserBean) GSON_TYPE_HANDLER.getResult(resultSet, 2);
        assertThat(bean).isNull();
        when(resultSet.getString(3)).thenReturn("{\"id\":123,\"name\":\"测试\"}");
        bean = (UserBean) GSON_TYPE_HANDLER.getResult(resultSet, 3);
        assertThat(bean.getId()).isEqualTo(123L);
        assertThat(bean.getName()).isEqualTo("测试");
    }

    @Test
    @Override
    public void getResultFromCallableStatement() throws Exception {
        UserBean bean;
        when(callableStatement.getString(1)).thenReturn(null);
        bean = (UserBean) GSON_TYPE_HANDLER.getResult(callableStatement, 1);
        assertThat(bean).isNull();
        when(callableStatement.getString(2)).thenReturn("");
        bean = (UserBean) GSON_TYPE_HANDLER.getResult(callableStatement, 2);
        assertThat(bean).isNull();
        when(callableStatement.getString(3)).thenReturn("{\"id\":123,\"name\":\"测试\"}");
        bean = (UserBean) GSON_TYPE_HANDLER.getResult(callableStatement, 3);
        assertThat(bean.getId()).isEqualTo(123L);
        assertThat(bean.getName()).isEqualTo("测试");
    }
}
