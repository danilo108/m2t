package m2t.jobloader.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.aws.jdbc.datasource.TomcatJdbcDataSourceFactory;
import org.springframework.stereotype.Component;

@Component
public class PoolConfiguration implements BeanPostProcessor {

@Override
public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException	 {
    if (bean instanceof TomcatJdbcDataSourceFactory) {
        TomcatJdbcDataSourceFactory tomcatJdbcDataSourceFactory = (TomcatJdbcDataSourceFactory) bean;
        tomcatJdbcDataSourceFactory.setTestOnBorrow(true);
        tomcatJdbcDataSourceFactory.setTestWhileIdle(true);
        tomcatJdbcDataSourceFactory.setValidationQuery("SELECT 1");
    }
    return bean;
}
}