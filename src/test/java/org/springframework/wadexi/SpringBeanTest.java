package org.springframework.wadexi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.wadexi.beans.TestBean;

public class SpringBeanTest {

    public static final Log log = LogFactory.getLog(TestBean.class);
    public static void main(String[] args) {
        AnnotationConfigApplicationContext configApplicationContext
                = new AnnotationConfigApplicationContext("org.springframework.wadexi");
        TestBean bean = configApplicationContext.getBean(TestBean.class);
        log.info(bean.getName());
    }
}
