package io.choerodon.workflow.infra.util;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.choerodon.core.exception.CommonException;
import io.choerodon.workflow.domain.handler.DevopsPipelineBpmnHandler;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.validation.ProcessValidator;
import org.activiti.validation.ProcessValidatorFactory;
import org.activiti.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 动态工作流：生成BPMN2.0格式的
 * Created by Sheep on 2019/4/2.
 */
public class DynamicWorkflowUtil {

    private Logger logger = LoggerFactory.getLogger(DevopsPipelineBpmnHandler.class);

    /**
     * 将XML转换成BPMN对象
     *
     * @param xml
     * @return 工作流BPMN对象
     * @throws XMLStreamException
     * @throws UnsupportedEncodingException
     */
    public static BpmnModel converterXMLToBpmn(byte[] xml) {

        ByteArrayInputStream bis = new ByteArrayInputStream(xml);
        BpmnXMLConverter converter = new BpmnXMLConverter();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = null;
        try {
            reader = factory.createXMLStreamReader(bis);
        } catch (XMLStreamException e) {
            throw new CommonException(e);
        }
        BpmnModel bpmnModel = converter.convertToBpmnModel(reader);
        return bpmnModel;
    }

    /**
     * bpmnModel 转换为标准的bpmn xml文本
     *
     * @param bpmnModel 工作流BPMN对象
     * @return XML格式的文本字符串
     */
    public static String converterBpmnToXML(BpmnModel bpmnModel) {
        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
        byte[] convertToXML = bpmnXMLConverter.convertToXML(bpmnModel);
        String bytes = new String(convertToXML);
        return bytes;
    }

    /**
     * 验证BPMN对象是否合法
     *
     * @param model 工作流BPMN对象
     * @return 合法返回true，否则返回false
     */
    public static boolean checkValidate(BpmnModel model) {
        // 验证model 是否是正确的bpmn xml文件
        ProcessValidatorFactory processValidatorFactory = new ProcessValidatorFactory();
        ProcessValidator defaultProcessValidator = processValidatorFactory.createDefaultProcessValidator();

        // 验证失败信息的封装ValidationError
        List<ValidationError> validate = defaultProcessValidator.validate(model);
        // ValidationError封装的是验证信息，如果size为0说明，bpmnmodel正确;
        // 大于0,说明自定义的bpmnmodel是错误的，不可以使用的。
        return 0 == validate.size();
    }

    /**
     * 创建UserTask任务节点-用户
     *
     * @param id
     * @param name
     * @param assignee
     * @return
     */
    public UserTask createUserTask(String id, String name, String assignee) {
        UserTask userTask = new UserTask();
        userTask.setName(name);
        userTask.setId(id);
        if (assignee != null) {
            userTask.setAssignee(assignee);
        }
        return userTask;
    }

    /**
     * 创建UserTask任务节点-组
     *
     * @param id
     * @param name
     * @param candidateGroup
     * @return
     */
    public UserTask createGroupTask(String id, String name, String candidateGroup) {
        List<String> candidateGroups = new ArrayList<String>();
        candidateGroups.add(candidateGroup);
        UserTask userTask = new UserTask();
        userTask.setName(name);
        userTask.setId(id);
        userTask.setCandidateGroups(candidateGroups);
        return userTask;
    }

    /**
     * 创建UserTask任务节点-锁定者
     *
     * @param id
     * @param name
     * @param assignee
     * @return
     */
    public UserTask createAssigneeTask(String id, String name, String assignee) {
        UserTask userTask = new UserTask();
        userTask.setName(name);
        userTask.setId(id);
        userTask.setAssignee(assignee);
        return userTask;
    }

    /**
     * 创建ServiceTask任务节点
     *
     * @param id
     * @param name
     * @return
     */
    public ServiceTask createServiceTask(String id, String name) {
        ServiceTask serviceTask = new ServiceTask();
        serviceTask.setName(name);
        serviceTask.setId(id);

        return serviceTask;
    }

    /**
     * 创建连线
     *
     * @param from
     * @param to
     * @param name
     * @param conditionExpression
     * @return
     */
    public SequenceFlow createSequenceFlow(String from, String to, String name, String conditionExpression) {
        SequenceFlow flow = new SequenceFlow();

        flow.setId("sequenceFlow-" + UUID.randomUUID().toString());
        flow.setSourceRef(from);
        flow.setTargetRef(to);

        if (null != name && !name.isEmpty()) {
            flow.setName(name);
        }

        if (null != conditionExpression && !conditionExpression.isEmpty()) {
            flow.setConditionExpression(conditionExpression);
        }

        return flow;
    }

    /**
     * 创建连线
     *
     * @param from 连线的源端
     * @param to   连线的目的端
     * @return
     */
    public SequenceFlow createSequenceFlow(String from, String to) {
        return createSequenceFlow(from, to, "", "");
    }

    /**
     * 创建排他网关
     *
     * @param id
     * @param name
     * @return
     */
    public ExclusiveGateway createExclusiveGateway(String id, String name) {
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setId(id);
        exclusiveGateway.setName(name);
        return exclusiveGateway;
    }

    /**
     * 创建并行网关
     *
     * @param id
     * @param name
     * @return
     */
    public ParallelGateway createParallelGateway(String id, String name) {
        ParallelGateway gateway = new ParallelGateway();
        gateway.setId(id);
        gateway.setName(name);
        return gateway;
    }

    /**
     * 创建开始节点
     *
     * @param id
     * @return
     */
    public StartEvent createStartEvent(String id) {
        StartEvent startEvent = new StartEvent();
        startEvent.setId(id);
        return startEvent;
    }

    /**
     * 创建结束节点
     *
     * @param id
     * @return
     */
    public EndEvent createEndEvent(String id) {
        EndEvent endEvent = new EndEvent();
        endEvent.setId(id);
        return endEvent;
    }

}
