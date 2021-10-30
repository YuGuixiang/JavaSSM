package com.sicnu.crud.test;

import com.sicnu.crud.bean.Department;
import com.sicnu.crud.bean.Employee;
import com.sicnu.crud.dao.DepartmentMapper;

import com.sicnu.crud.dao.EmployeeMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

/**
 * 测试dao的功能
 * @author Yu
 * @create 2021-10-26 6:05 下午
 * 推荐spring项目使用spring单元测试
 * 1.导入spring-test模块
 * 2.@ContextConfiguration指定spring配置文件的位置
 * 3.使用Autowired
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:ApplicationContext.xml"})
public class MapperTest {

    @Autowired
    DepartmentMapper departmentMapper;
    @Autowired
    EmployeeMapper employeeMapper;
    @Autowired
    SqlSession sqlSession;
    /**
     * 测试departmentMapper
     */
    @Test
    public void testCRUD(){
//        //1.创建IOC容器
//        ApplicationContext ioc = new ClassPathXmlApplicationContext("ApplicationContext.xml");
//        //1.从ioc拿到mapper
//        DepartmentMapper bean = ioc.getBean(DepartmentMapper.class);
//        System.out.println(bean);

        //插入部门
//        departmentMapper.insertSelective(new Department(null,"开发部"));
//        departmentMapper.insertSelective(new Department(null,"测试部"));
        //批量插入员工
        EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
        for (int i = 0; i < 1000 ; i++) {
            String uid = UUID.randomUUID().toString().substring(0, 5)+i;
            mapper.insertSelective(new Employee(null,uid,"M",uid+"@sicnu.com",1));
        }
        System.out.println("批量完成");
    }
}
