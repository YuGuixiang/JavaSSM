package com.sicnu.crud.test;

import com.github.pagehelper.PageInfo;
import com.sicnu.crud.bean.Employee;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.util.List;

/**
 * 使用spring单元测试，测试crud请求的准确性
 *      spring4测试 需要servlet3.0支持
 * @author Yu
 * @create 2021-10-27 11:00 上午
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:ApplicationContext.xml",
        "file:src/main/webapp/WEB-INF/dispatcherServlet-servlet.xml"})
public class MVCTest {

    //传入springmvc的ioc
    @Autowired
    WebApplicationContext context;

    //虚拟MVC请求,获取处理结果
    MockMvc mockMvc;

    @Before
    public void initMockMvc(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testPage() throws Exception {
        //模拟请求拿到返回值
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/emps").
                param("pn", "1")).andReturn();

        //请求成功后，请求域中会有pageInfo
        MockHttpServletRequest request = result.getRequest();
        PageInfo pageInfo = (PageInfo) request.getAttribute("pageInfo");
        System.out.println("当前页码："+pageInfo.getPageNum());
        System.out.println("总页码："+pageInfo.getPages());
        System.out.println("总记录数："+pageInfo.getTotal());
        System.out.println("在页面需要连续显示的页码：");
        int[] nums = pageInfo.getNavigatepageNums();
        for (int i:nums){
            System.out.println(" "+i);
        }

        //获取员工信息
        List<Employee> list = pageInfo.getList();
        for (Employee employee:list){
            System.out.println("ID : "+employee.getEmpId()+", NAME : "+employee.getEmpName());
        }
    }

}
