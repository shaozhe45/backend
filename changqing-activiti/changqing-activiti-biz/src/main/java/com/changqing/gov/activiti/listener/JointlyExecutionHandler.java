package com.changqing.gov.activiti.listener;

import com.changqing.gov.activiti.constant.ReimbursementVarConstant;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * 执行监听器，报销流程的会签变量初始化
 *
 * @author wz
 * @date 2020-08-07
 */
@Component
public class JointlyExecutionHandler implements ExecutionListener {
    private TaskService taskService;

    /**
     * 监听
     * @param delegateExecution 任务
     */
    @Override
    public void notify(DelegateExecution delegateExecution) {
        if (taskService == null) {
            taskService = (TaskService) ApplicationContextHandler.getBean("taskServiceBean");
        }

        List<String> userList = new ArrayList<>();
        // 5,6分别是用户id，可以在前端填表单的时候把用户列表存业务数据库，然后取出来动态赋值
        userList.add("5");
        userList.add("6");
        delegateExecution.setVariable(ReimbursementVarConstant.USER_LIST, userList);
        delegateExecution.setVariable(ReimbursementVarConstant.UP, 0);
        delegateExecution.setVariable(ReimbursementVarConstant.DOWN, 0);
    }
}
