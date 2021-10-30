package com.sicnu.crud.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sicnu.crud.bean.Employee;
import com.sicnu.crud.bean.Msg;
import com.sicnu.crud.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yu
 * @create 2021-10-27 10:36 上午
 */
@Controller
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    /**
     *
     * @param ids
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/emp/{ids}",method = RequestMethod.DELETE)
    public Msg deleteEmp(@PathVariable("ids") String ids){
        if (ids.contains("-")){
            //批量删除
            List<Integer> del_ids = new ArrayList<>();
            String[] str_ids = ids.split("-");
            for (String s:str_ids){
                del_ids.add(Integer.parseInt(s));
            }
            employeeService.deleteBatch(del_ids);
        }else {
            //单个删除
            Integer id = Integer.parseInt("ids");
            employeeService.deleteEmp(id);
        }
        return Msg.success();
    }

    /**
     * 员工更新
     * 如果ajax直接发送PUT请求
     * * 请求体中有数据；
     * 	 * 但是Employee对象封装不上；
     * 	 * update tbl_emp  where emp_id = 1014;
     * 	 *
     * 	 * 原因：
     * 	 * Tomcat：
     * 	 * 		1、将请求体中的数据，封装一个map。
     * 	 * 		2、request.getParameter("empName")就会从这个map中取值。
     * 	 * 		3、SpringMVC封装POJO对象的时候。
     * 	 * 				会把POJO中每个属性的值，request.getParamter("email");
     * 	 * AJAX发送PUT请求引发的血案：
     * 	 * 		PUT请求，请求体中的数据，request.getParameter("empName")拿不到
     * 	 * 		Tomcat一看是PUT不会封装请求体中的数据为map，只有POST形式的请求才封装请求体为map
     * 	 * org.apache.catalina.connector.Request--parseParameters() (3111);
     * 	 *
     * 	 * protected String parseBodyMethods = "POST";
     * 	 * if( !getConnector().isParseBodyMethod(getMethod()) ) {
     *                 success = true;
     *                 return;
     *             }
     * 	 * 解决方案；
     * 	 * 我们要能支持直接发送PUT之类的请求还要封装请求体中的数据
     * 	 * 1、配置上HttpPutFormContentFilter；
     * 	 * 2、他的作用；将请求体中的数据解析包装成一个map。
     * 	 * 3、request被重新包装，request.getParameter()被重写，就会从自己封装的map中取数据
     * @param employee
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/emp/{empId}",method = RequestMethod.PUT)
    public Msg saveEmp(Employee employee){
        employeeService.updateEmp(employee);
        return Msg.success().add("emp",employee);
    }

    /**
     * 根据id获取员工信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/emp/{id}",method = RequestMethod.GET)
    @ResponseBody
    public Msg getEmp(@PathVariable("id") Integer id){
        Employee employee = employeeService.getEmp(id);
        return Msg.success().add("emp",employee);
    }

    /**
     * 检查用户名字是否可用
     *
     * @param empName
     * @return
     */
    @ResponseBody
    @RequestMapping("/checkuser")
    public Msg checkuser(String empName) {
        //名字合法性校验
        String regx = "(^[a-zA-Z0-9_-]{6,16}$)|(^[\\u2E80-\\u9FFF]{2,5})";
        if(!empName.matches(regx)){
            return Msg.fail().add("va_msg","用户名必须是6-16位的数字和字母组合或者2-5位中文");
        }
        //名字数据库校验
        boolean b = employeeService.checkUser(empName);
        if (b) {
            return Msg.success();
        } else return Msg.fail().add("va_msg","用户名已存在!");
    }

    /**
     * 员工保存
     * 1.支持JSR303校验
     * 2.导入hibernate-validator
     * @param employee
     * @return
     */
    @RequestMapping(value = "/emp", method = RequestMethod.POST)
    @ResponseBody
    public Msg saveEmp(@Valid Employee employee , BindingResult result) {
        if (result.hasErrors()){
            Map<String,Object> map = new HashMap<>();
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError fieldError:errors){
                System.out.println("错误的字段："+fieldError.getField());
                System.out.println("错误信息："+fieldError.getDefaultMessage());
                map.put(fieldError.getField(),fieldError.getDefaultMessage());
            }
            return Msg.fail().add("errorFields",map);
        }else {
            employeeService.saveEmp(employee);
            return Msg.success();
        }

    }

    /**
     * 分页查询2.0（ajax）,需要导入jackson包
     *
     * @param pn
     * @return
     */
    @RequestMapping("/emps")
    @ResponseBody
    public Msg getEmpsWithJson(@RequestParam(value = "pn", defaultValue = "1") Integer pn) {
        //引入pagehelper分页查询
        PageHelper.startPage(pn, 5);

        List<Employee> emps = employeeService.getAll();
        //包装查询的结果，封装了分页信息，传入连续显示的页数5
        PageInfo pageInfo = new PageInfo(emps, 5);
        return Msg.success().add("pageInfo", pageInfo);
    }

}
