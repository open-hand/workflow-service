package org.hzero.workflow.helper;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.boot.interfaces.sdk.dto.RequestPayloadDTO;
import org.hzero.boot.interfaces.sdk.dto.ResponsePayloadDTO;
import org.hzero.boot.interfaces.sdk.invoke.InterfaceInvokeSdk;
import org.hzero.boot.modeler.client.ModelExecutorClient;
import org.hzero.boot.modeler.executor.helper.ExecutorEncryptHelper;
import org.hzero.boot.modeler.infra.feign.vo.ScriptExecutionResult;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.constant.LovConstants;
import org.hzero.boot.platform.lov.dto.LovDTO;
import org.hzero.boot.platform.lov.dto.LovViewDTO;
import org.hzero.common.HZeroService;
import org.hzero.core.base.AopProxy;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.properties.CoreProperties;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.AssertUtils;
import org.hzero.core.util.EncoderUtils;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.starter.keyencrypt.core.EncryptProperties;
import org.hzero.workflow.adaptor.json.ApproverRuleJsonConverter;
import org.hzero.workflow.def.api.vo.MessageTemplateVO;
import org.hzero.workflow.def.domain.entity.DefInterface;
import org.hzero.workflow.def.domain.entity.DefParameterValue;
import org.hzero.workflow.def.domain.entity.DefWorkflow;
import org.hzero.workflow.def.domain.repository.DefInterfaceRepository;
import org.hzero.workflow.def.domain.repository.DefWorkflowRepository;
import org.hzero.workflow.def.infra.constant.WorkflowConstants;
import org.hzero.workflow.def.infra.feign.AdminFeignClient;
import org.hzero.workflow.def.infra.feign.IamFeignClient;
import org.hzero.workflow.def.infra.feign.LovFeignClient;
import org.hzero.workflow.def.infra.feign.PlatformFeignClient;
import org.hzero.workflow.def.infra.feign.dto.EmployeeDTO;
import org.hzero.workflow.def.infra.feign.dto.PermissionDTO;
import org.hzero.workflow.def.infra.feign.dto.ServiceRouteDTO;
import org.hzero.workflow.def.infra.feign.dto.UserDTO;
import org.hzero.workflow.engine.model.FlowAssignee;
import org.hzero.workflow.engine.model.node.FlowApproverRuleLine;
import org.hzero.workflow.engine.model.node.FlowDefParameterValue;
import org.hzero.workflow.engine.model.node.FlowInterface;
import org.hzero.workflow.engine.model.node.FlowInterfaceRequestDTO;
import org.hzero.workflow.engine.run.IVariableService;
import org.hzero.workflow.engine.util.EngineConstants;
import org.hzero.workflow.engine.util.JsonUtils;

/***
 * 接口请求、LOV请求工具类
 * @author xiuhong.chen@hand-china.com
 */
@Component
@Import({FeignClientsConfiguration.class})
public class InterfaceRequestHelper implements AopProxy<InterfaceRequestHelper> {
    public static final Logger logger = LoggerFactory.getLogger(InterfaceRequestHelper.class);

    @Autowired
    private DefInterfaceRepository defInterfaceRepository;
    @Autowired
    @Qualifier("hwkfRestTemplate")
    @LoadBalanced
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Resource
    private IamFeignClient iamFeignClient;
    @Resource
    private PlatformFeignClient platformFeignClient;
    @Resource
    private AdminFeignClient adminFeignClient;
    @Resource
    private RedisHelper redisHelper;
    @Autowired
    private IVariableService variableService;
    @Autowired
    private ApproverRuleJsonConverter approverRuleJsonConverter;
    @Autowired
    private LovAdapter lovAdapter;
    @Autowired
    private EncryptProperties encryptProperties;
    @Autowired
    private FlowAssigneeHelper flowAssigneeHelper;
    @Autowired
    private InterfaceInvokeSdk interfaceInvokeSdk;
    @Autowired
    private RetryTemplate retryTemplate;
    @Autowired
    private BoHelper boHelper;
    @Autowired
    private DefWorkflowRepository defWorkflowRepository;
    @Autowired
    private ModelExecutorClient modelExecutorClient;

    /**Feign 原生构造器*/
    Feign.Builder builder;

    @Value(HZeroService.Message.NAME)
    private String messageName;
    private static final String JWT_TOKEN = "jwt_token";

    @Value("${hzero.platform.httpProtocol:http://}")
    private String REQUEST_PROTOCOL;

    private static final CustomUserDetails ANONYMOUS;
    private static Signer signer;
    public static String EMPLOYEE_NUM ="employeeNum";
    private static final String LOV_CODE = "HWKF.MESSAGE_CONFIG_TYPE";
    private static final String FAIL = "fail";
    /**
     * 移动端语言
     */
    private static final String H_MOBILE_LANG = "H-Mobile-Lang";

    static {
        ANONYMOUS = new CustomUserDetails(BaseConstants.ANONYMOUS_USER_NAME, "unknown", Collections.emptyList());
        ANONYMOUS.setUserId(BaseConstants.ANONYMOUS_USER_ID);
        ANONYMOUS.setOrganizationId(BaseConstants.DEFAULT_TENANT_ID);
        ANONYMOUS.setLanguage("zh_CN");
        ANONYMOUS.setTimeZone("GMT+8");
    }

    /***
     * 动态Feign获取LOV数据使用
     * @param decoder
     * @param encoder
     * @param client
     * @param contract
     * @param requestInterceptor
     */
    public InterfaceRequestHelper(Decoder decoder, Encoder encoder, Client client, Contract contract, RequestInterceptor requestInterceptor) {
        this.builder = Feign.builder().client(client).encoder(encoder).decoder(decoder).contract(contract).requestInterceptor(requestInterceptor);
    }

    /***
     * 界面获取接口结果--根据接口ID、参数映射关系、查询参数调用接口
     * @param interfaceId 			接口ID
     * @param defParameterValueList	接口参数映射关系，如规则行参数、变量行参数
     * @param args					模拟变量值，如{"employeeNum":"admin","employeeName":"管理员"}
     * @return
     */
    public String getApiResult(long interfaceId, List<DefParameterValue> defParameterValueList,Map<String, Object> args) {
        DefInterface defInterface = defInterfaceRepository.select(DefInterface.FIELD_INTERFACE_ID,interfaceId).stream().findFirst().orElse(null);
        AssertUtils.notNull(defInterface,"error.interface.empty");
        FlowInterface flowInterface = new FlowInterface();
        BeanUtils.copyProperties(defInterface,flowInterface);
        flowInterface.setInterfaceUrl(this.getApiUrl(defInterface));
        List<FlowDefParameterValue> paramList = approverRuleJsonConverter.convertDefParamValueToFlowParamValue(defParameterValueList);

        FlowInterfaceRequestDTO flowInterfaceRequestDTO = this.buildInterfaceRequestMap(flowInterface,paramList,null,null,null,args,null);
        flowInterfaceRequestDTO.setInterfaceContainer(flowInterface);
        String result = this.self().executeInterface(flowInterfaceRequestDTO);

        try {
            String content = "content";
            if(result != null && result.startsWith(BaseConstants.Symbol.LEFT_BIG_BRACE)){
                Map<String,Object> resultMap = StringUtils.isEmpty(result) ? Collections.emptyMap() : objectMapper.readValue(result,Map.class);
                if(resultMap.containsKey(content)){
                    return objectMapper.writeValueAsString(resultMap.get(content));
                }
            }else {
                return result;
            }
        }catch (Exception e){
            logger.error("error:", e);
            throw new CommonException(e.getMessage());
        }
        return result;
    }

    /***
     * 查询表单接口结果
     * @param flowInterface
     * @param defParameterValueList
     * @param instanceId
     * @param nodeId
     * @param taskId
     * @return
     */
    public List<Object> getFormApiResult(FlowInterface flowInterface, List<FlowDefParameterValue> defParameterValueList, Long instanceId,Long nodeId,Long taskId) {
        try {
            // 请求头添加移动端语言参数
            Map<String, String> headerParamMap = new HashMap<>();
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            Assert.notNull(requestAttributes, BaseConstants.ErrorCode.DATA_INVALID);
            String mobileLang = ((ServletRequestAttributes) requestAttributes).getRequest().getHeader(H_MOBILE_LANG);
            headerParamMap.put(H_MOBILE_LANG, mobileLang);
            FlowInterfaceRequestDTO flowInterfaceRequestDTO = this.buildInterfaceRequestMap(flowInterface,defParameterValueList,instanceId,nodeId,taskId,null,headerParamMap,null);
            flowInterfaceRequestDTO.setInterfaceContainer(flowInterface);
            String apiResult = this.self().executeInterface(flowInterfaceRequestDTO);
            return objectMapper.readValue(apiResult, new TypeReference<List<Object>>() {});
        } catch (Exception e) {
            logger.error("error:", e);
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
    }

    /***
     * 流程运转获取接口结果--根据接口编码、参数映射关系、参数值调用接口
     * @param flowInterface 		接口对象
     * @param defParameterValueList	接口参数映射关系，如规则行参数、变量行参数，（若参数来源=变量，则包含变量详情）
     * @param instanceId 流程实例Id
     * @return String
     */
    public String getApiResult(FlowInterface flowInterface, List<FlowDefParameterValue> defParameterValueList, Long instanceId,Long nodeId,Long taskId) {
        FlowInterfaceRequestDTO flowInterfaceRequestDTO = this.buildInterfaceRequestMap(flowInterface,defParameterValueList,instanceId,nodeId,taskId,null,null);
        flowInterfaceRequestDTO.setInterfaceContainer(flowInterface);
        return this.self().executeInterface(flowInterfaceRequestDTO);
    }

    public String getForecastApiResult(FlowInterface flowInterface, List<FlowDefParameterValue> defParameterValueList, Map<String, Object> frontParamMap, Long instanceId, Long nodeId, Long taskId) {
        FlowInterfaceRequestDTO flowInterfaceRequestDTO = this.buildInterfaceRequestMap(flowInterface, defParameterValueList, instanceId, nodeId, taskId, frontParamMap, null);
        flowInterfaceRequestDTO.setInterfaceContainer(flowInterface);
        return this.self().executeInterface(flowInterfaceRequestDTO);
    }

    /***
     * 解析接口，拼接参数，调用接口
     * @param defInterface  		接口对象
     * @param defParameterValueList	接口参数映射关系，如规则行参数、变量行参数，（若参数来源=变量，则包含变量详情）
     * @param instanceId 流程实例Id
     * @param frontParamMap				前端选择的变量值如{"departmentId":10001}
     * @param headerParam
     * @param eventTriggerTypeVar  事件触发时机 EngineConstants.EventTriggerTypeVar
     * @return
     */
    public RequestEntity<String> buildRequestEntity(FlowInterface defInterface, List<FlowDefParameterValue> defParameterValueList,
                                                    Long instanceId,Long nodeId,Long taskId,
                                                    Map<String, Object> frontParamMap, Map<String, String> headerParam,String eventTriggerTypeVar) {
        AssertUtils.notNull(defInterface,"error.interface.empty");
        String apiUrl = defInterface.getInterfaceUrl();
        String serviceName = defInterface.getServiceName();
        String methodType = StringUtils.isEmpty(defInterface.getMethod()) ? null : defInterface.getMethod().toUpperCase();

        //拼接url路径变量、body变量
        StringBuilder queryParamSb = new StringBuilder(BaseConstants.Symbol.QUESTION);
        Map<String,Object> bodyParamMap = new HashMap<>(16);
        if(CollectionUtils.isNotEmpty(defParameterValueList)){
            for(FlowDefParameterValue dto : defParameterValueList){
                String paramValue = getParamValue(dto, instanceId, nodeId, taskId, frontParamMap, eventTriggerTypeVar);
                if (paramValue != null) {
                    paramValue = EncoderUtils.encodeFilename(paramValue);
                }
                switch (dto.getUrlType()){
                    case WorkflowConstants.ParameterUrlType.PATH:
                        if (paramValue == null) {
                            throw new CommonException("error.engine.helper.path.param.value.empty",dto.getParameterCode());
                        }
                        String str = BaseConstants.Symbol.LEFT_BIG_BRACE + dto.getParameterCode() +BaseConstants.Symbol.RIGHT_BIG_BRACE;
                        apiUrl = apiUrl.replace(str,paramValue);
                        break;
                    case WorkflowConstants.ParameterUrlType.PARAM:
                        if(StringUtils.isNotEmpty(paramValue)){
                            queryParamSb.append(dto.getParameterCode()).append(BaseConstants.Symbol.EQUAL).append(paramValue).append(BaseConstants.Symbol.AND);
                        }
                        break;
                    case WorkflowConstants.ParameterUrlType.BODY:
                        bodyParamMap.put(dto.getParameterCode(), paramValue);
                        break;
                    case WorkflowConstants.ParameterUrlType.HEADER:
                        if (Objects.isNull(headerParam.get(dto.getParameterCode()))) {
                            // 特定值的传入不进行取值的覆盖
                            headerParam.put(dto.getParameterCode(), paramValue);
                        }
                        break;
                    default:break;
                }
            }
            if (!BaseConstants.Symbol.QUESTION.equals(queryParamSb.toString())) {
                apiUrl = apiUrl + (queryParamSb.toString().endsWith(BaseConstants.Symbol.AND) ? queryParamSb.substring(0, queryParamSb.length() - 1) : queryParamSb.toString());
            }
        }
        StringBuilder requestUrl = new StringBuilder(REQUEST_PROTOCOL).append(serviceName).append(apiUrl);

        //调用接口
        try {
            HttpMethod method = Optional.ofNullable(HttpMethod.resolve(methodType)).orElse(HttpMethod.GET);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            // ↓↓↓↓ 二开部分 ↓↓↓↓
            // zknow-boot-admin引入了jackson-dataformat-xml包, 污染了所有项目的jackson, 导致RestController默认返回了xml格式
            // 擦屁股, 强制指定内部接口返回值为json格式
            httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            // ↑↑↑↑ 二开部分 ↑↑↑↑
            httpHeaders.set(JWT_TOKEN, getJwtToken());
            for (Map.Entry<String, String> entry : headerParam.entrySet()) {
                httpHeaders.set(entry.getKey(), entry.getValue());
            }
            return new RequestEntity<String>(objectMapper.writeValueAsString(bodyParamMap), httpHeaders, method, URI.create(requestUrl.toString()));
        } catch (JsonProcessingException e) {
            logger.error("error:", e);
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
    }

    /***
     * 获取jwt_token
     * @return String
     */
    public String getJwtToken() {
        String jwtToken = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //设置子线程共享
        //RequestContextHolder.setRequestAttributes(requestAttributes,true);
        if (requestAttributes instanceof ServletRequestAttributes) {
            jwtToken = ((ServletRequestAttributes) requestAttributes).getRequest().getHeader(JWT_TOKEN);
        }
        if (StringUtils.isEmpty(jwtToken)) {
            try {
                jwtToken = "Bearer " + JwtHelper.encode(objectMapper.writeValueAsString(Optional.ofNullable(DetailsHelper.getUserDetails()).orElse(ANONYMOUS)), getSigner()).getEncoded();
            } catch (JsonProcessingException e) {
                logger.error("Error write userDetails to json.");
                return null;
            }
        }
        return jwtToken;
    }

    private static Signer getSigner() {
        if (signer == null) {
            synchronized (InterfaceRequestHelper.class) {
                CoreProperties coreProperties = ApplicationContextHelper.getContext().getBean(CoreProperties.class);
                Assert.notNull(coreProperties, "Not found bean typed CoreProperties.class");
                signer = new MacSigner(coreProperties.getOauthJwtKey());
            }
        }
        return signer;
    }

    /***
     * 界面获取LOV结果--根据LOV编码、参数映射关系、参数值调用接口获取LOV结果
     * @param viewCode 				值集视图
     * @param defParameterValueList	参数映射关系，如LOV行参数
     * @param pageRequest 			分页
     * @param queryParam			LOV查询条件
     * @return String
     */
    public String getLovResult(String viewCode, String lovCode,List<DefParameterValue> defParameterValueList, PageRequest pageRequest ,Map<String, Object> queryParam) {
        List<FlowDefParameterValue> list = approverRuleJsonConverter.convertDefParamValueToFlowParamValue(defParameterValueList);
        return this.getLovResult(viewCode, lovCode,list,pageRequest,queryParam,null,null,null);
    }

    /***
     * 流程运转获取LOV结果--根据LOV编码、参数映射关系、参数值调用接口获取LOV结果
     * @param viewCode 值集视图编码
     * @param flowDefParameterValueList 参数
     * @param pageRequest 分页
     * @param instanceId 流程实例ID
     * @param nodeId 节点ID
     * @param taskId 任务ID
     * @return
     */
    public String getLovResult(String viewCode,String lovCode, List<FlowDefParameterValue> flowDefParameterValueList, PageRequest pageRequest,Long instanceId,Long nodeId,Long taskId) {
        return this.getLovResult(viewCode, lovCode,flowDefParameterValueList,pageRequest, new HashMap<>(1),instanceId,nodeId,taskId);
    }

    private String getLovResult(String viewCode, String lovCode,List<FlowDefParameterValue> defParameterValueList, PageRequest pageRequest, Map<String, Object> queryParam,Long instanceId,Long nodeId,Long taskId) {
        int page = pageRequest == null ? 0 : pageRequest.getPage();
        int size = pageRequest == null ? 1000 : pageRequest.getSize();
        //1.根据viewCode获取LOV详细信息
        if(StringUtils.isEmpty(viewCode)){
            throw new CommonException("error.engine.viewCode.empty");
        }
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();

        if(StringUtils.isEmpty(lovCode)){
            LovViewDTO lovViewDTO = lovAdapter.queryLovViewInfo(viewCode,tenantId);
            if(lovViewDTO == null){
                throw new CommonException("error.feign.get.viewcode");
            }
            lovCode = lovViewDTO.getLovCode();
        }
        if(CollectionUtils.isNotEmpty(defParameterValueList)){
            for(FlowDefParameterValue dto : defParameterValueList){
                String paramValue = getParamValue(dto,instanceId,nodeId,taskId,queryParam,null);
                queryParam.put(dto.getParameterCode(),paramValue);
            }
        }
        List<Map<String, Object>> mapList = platformFeignClient.queryLovDataSiteToMap(lovCode,tenantId,null,page,size,queryParam);
        try {
            return objectMapper.writeValueAsString(mapList);
        } catch (JsonProcessingException e) {
            logger.error("error:", e);
        }
        return null;
    }

    /***
     * 根据路由名称获取服务名称
     * @param routeName
     * @return
     */
    private String getServiceName(String routeName){
        //根据路由名称从缓存中获取服务名称
        redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        String serviceName = redisHelper.hshGet("hadm:routes",routeName);
        redisHelper.clearCurrentDatabase();
        if(StringUtils.isEmpty(serviceName)){
            //从数据库获取服务名称
            Page<ServiceRouteDTO> page = adminFeignClient.pageRoutes(0L,routeName);
            serviceName = page==null ? null : page.getContent().get(0).getServiceName();
        }
        if(StringUtils.isEmpty(serviceName)){
            throw new CommonException("error.interface.service.name.empty");
        }
        return serviceName;
    }

    /***
     * 根据行参数详情，获取行参数值
     * 常量、变量(根据实例ID获取)、LOV(调用LOV逻辑获取)
     * @param defParameterValue
     * @param instanceId
     * @param frontParamMap				前端选择的变量值如{"departmentId":10001}
     * @return
     */
    public String getParamValue(FlowDefParameterValue defParameterValue,Long instanceId,Long nodeId,Long taskId,Map<String, Object> frontParamMap,String eventTriggerTypeVar){
        String paramValueSourceType = defParameterValue.getValueType();

        //预览测试--获取审批人、获取特定值
        if(instanceId == null){
            if(frontParamMap == null){
                frontParamMap = new HashMap<>(16);
            }
            //若预览时未输入使用参数值=默认值
            frontParamMap.computeIfAbsent(defParameterValue.getParameterCode(), k -> defParameterValue.getDefaultValue());
            Object frontParamValue = frontParamMap.get(defParameterValue.getParameterCode());
            if(frontParamValue != null && !WorkflowConstants.ParameterValueType.LOV.equals(paramValueSourceType)){
                return frontParamValue.toString();
            }

            if (WorkflowConstants.ParameterValueType.VARIABLE.equals(paramValueSourceType)) {
                // 参数为流程变量，则需要是自定义类型的，instanceId为空时需要能从frontParamMap中获取值
                String apiVariableParamVariableCode = defParameterValue.getVariableDto().getVariableCode();
                Object valueVariableValue = frontParamMap.get(apiVariableParamVariableCode);
                if (Objects.isNull(valueVariableValue) || StringUtils.isEmpty(valueVariableValue.toString())) {
                    throw new CommonException("error.interface.api_variable_param_failed", defParameterValue.getParameterCode(), apiVariableParamVariableCode);
                }
                return valueVariableValue.toString();
            }
        }
        if(instanceId!=null && frontParamMap!=null){
            //自定义节点取值
            if(paramValueSourceType==null){
                Object frontParamValue = frontParamMap.get(defParameterValue.getParameterCode());
                return frontParamValue==null?null:frontParamValue.toString();
            }
            //任务层事件,获取任务变量值
            if(StringUtils.equalsAny(paramValueSourceType,WorkflowConstants.ParameterValueType.VARIABLE) && defParameterValue.getVariableDto()!=null){
                Object frontParamValue = frontParamMap.get(defParameterValue.getVariableDto().getVariableCode());
                if(frontParamValue!=null){
                    return frontParamValue.toString();
                }
            }
        }

        //编辑器或者流程运行中，获取变量值
        String value = null;
        switch (paramValueSourceType){
            case WorkflowConstants.ParameterValueType.CONSTANT:
                value = defParameterValue.getDefaultValue();
                break;
            case WorkflowConstants.ParameterValueType.VARIABLE:
            case WorkflowConstants.ParameterValueType.VARIABLE_CAN_REPLACE:
                value = variableService.getParameterValue(instanceId,nodeId,taskId,defParameterValue,eventTriggerTypeVar);
                break;
            case WorkflowConstants.ParameterValueType.LOV:
                if(instanceId == null){
                    Object paramValue = frontParamMap.get(defParameterValue.getParameterCode());
                    if(paramValue != null){
                        String content = paramValue.toString();
                        value = decryptLovField(defParameterValue.getViewCode(),content);
                        break;
                    }
                }
                List<Map<String,Object>> lovValueList = defParameterValue.getLovValueList();
                //若参数来源LOV，且已选择值集，则拼接值集为字符串
                if(CollectionUtils.isNotEmpty(lovValueList)){
                    StringBuffer paramValueSb = new StringBuffer();
                    lovValueList.forEach(dto->paramValueSb.append(getFirstValueInMap(dto)).append(BaseConstants.Symbol.COMMA));
                    paramValueSb.deleteCharAt(paramValueSb.length()-1);
                    value = paramValueSb.toString();
                    break;
                }
                // lov没有选择值时，是否查询所有值
                if (defParameterValue.getLovQueryAll() == null || defParameterValue.getLovQueryAll()) {
                    PageRequest pageRequest = new PageRequest(0,0);
                    value = getLovResult(defParameterValue.getViewCode(),null,defParameterValue.getLovParamValueList(),pageRequest,instanceId,nodeId,taskId);
                }
                break;
            case WorkflowConstants.ParameterValueType.BUSINESS_OBJECT:
                if(instanceId!=null){
                    DefWorkflow defWorkflow = defWorkflowRepository.selectBusinessObjectByInstanceId(instanceId);
                    Map<String, Object> queryParams = defWorkflow==null?null:variableService.getBoPrimaryFieldValueByInstanceId(defWorkflow.getTypeId(),instanceId);
                    if(queryParams != null){
                        BoHelper.setParamMapByExpression(defParameterValue.getBusinessObjectExpr(),queryParams);
                        value = modelExecutorClient.computeExpression(defWorkflow.getTenantId(),defParameterValue.getBusinessObjectExpr(),String.class,queryParams);
                        ExecutorEncryptHelper.clear();
                    }
                }
                break;
            case WorkflowConstants.ParameterValueType.LEVEL:
                Integer levelFrom = defParameterValue.getLevelFrom();
                Object levelValue = variableService.getVariable(EngineConstants.VariableScopeType.NODE_LOCAL, instanceId, nodeId, taskId, EngineConstants.BuiltInVariables.HWKF_LEVEL_VALUE, true);
                value = levelValue!=null?levelValue.toString():(levelFrom!=null?levelFrom.toString():null);
                break;
            default:break;
        }
        return value;
    }

    /***
     * LOV值集视图编码、需要解密的内容(若是多个值列表，则逗号拼接)
     * @param viewCode 值集视图编码
     * @param content 内容
     * @return
     */
    private String decryptLovField(String viewCode,String content){
        if(!(content != null && content.startsWith(BaseConstants.Symbol.EQUAL) && content.endsWith(BaseConstants.Symbol.EQUAL))){
            return content;
        }
        String[] contentArray = content.split(BaseConstants.Symbol.COMMA);
        //针对lov加密字段解密
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        LovViewDTO lovViewDTO = lovAdapter.queryLovViewInfo(viewCode, tenantId);
        if(lovViewDTO == null ){
            throw new CommonException("error.feign.get.viewcode");
        }
        LovDTO lovDTO = lovAdapter.queryLovInfo(lovViewDTO.getLovCode(), tenantId);
        if(lovDTO == null){
            throw new CommonException("error.feign.get.viewcode");
        }
        if(StringUtils.isNotEmpty(lovDTO.getEncryptField())){
            CustomDecryptHelper customDecryptHelper = new CustomDecryptHelper(encryptProperties);
            String[] encryptFields = lovDTO.getEncryptField().split(BaseConstants.Symbol.COMMA);
            for(int i=0; i<contentArray.length;i++){
                for (String encryptField : encryptFields) {
                    if (encryptField.equals(lovDTO.getValueField())){
                        contentArray[i] = customDecryptHelper.decrypt(contentArray[i], "");
                        break;
                    }
                }
            }
        }
        return StringUtils.join(contentArray,BaseConstants.Symbol.COMMA);
    }

    /**
     * 查询消息代码lov返回结果
     *
     * @param messageCode 消息代码列表 "code1,code2"
     * @return
     */
    public Map<String, MessageTemplateVO> queryMessageTemplateInfo(Long tenantId, String messageCode) {
        if (StringUtils.isEmpty(messageCode)) {
            return new HashMap<>();
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("messageCode", messageCode);
        paramMap.put("tenantId", tenantId);
        LovFeignClient lovFeignClient = this.builder.target(LovFeignClient.class, REQUEST_PROTOCOL + messageName);
        String result = lovFeignClient.getSqlLovData(paramMap, LOV_CODE, 0, 999999);

        Map<String, MessageTemplateVO> templateMap;
        try {
            Object content = objectMapper.readValue(result, Map.class).get("content");
            List<MessageTemplateVO> templateList = objectMapper.readValue(objectMapper.writeValueAsString(content), new TypeReference<List<MessageTemplateVO>>(){});
            if (CollectionUtils.isEmpty(templateList)) {
                // 查询0租户数据
                paramMap.put("tenantId", BaseConstants.DEFAULT_TENANT_ID);
                result = lovFeignClient.getSqlLovData(paramMap, LOV_CODE, 0, 999999);
                content = objectMapper.readValue(result, Map.class).get("content");
                templateList = objectMapper.readValue(objectMapper.writeValueAsString(content), new TypeReference<List<MessageTemplateVO>>(){});
            }

            templateMap = Optional.ofNullable(templateList).orElseGet(ArrayList::new).stream().collect(Collectors.toMap(MessageTemplateVO::getTemplateCode, Function.identity()));

        } catch (IOException e) {
            throw new CommonException("error.feign.queryLovInfo");
        }

        return templateMap;
    }

    /***
     * 根据接口信息获取接口URL
     * @param defInterface
     * @return
     */
    public String getApiUrl(DefInterface defInterface){
        if(defInterface.getOutInterfaceFlag()!=null && defInterface.getOutInterfaceFlag()!=WorkflowConstants.OutInterfaceType.IN_API){
            return null;
        }
        //根据接口编码调用feign获取接口详情
        Long tenantId = DetailsHelper.getUserDetails() == null ? BaseConstants.DEFAULT_TENANT_ID : DetailsHelper.getUserDetails().getTenantId();
        PermissionDTO permission = iamFeignClient.queryPermissionByCode(tenantId,defInterface.getPermissionCode(), defInterface.getPermissionLevel()).getBody();
        AssertUtils.notNull(permission,"error.interface.empty",defInterface.getInterfaceCode(),defInterface.getInterfaceName());
        return permission.getPath();
    }

    /**
     * 获取map中第一个数据值
     *
     * @param map 数据源
     * @return
     */
    private static Object getFirstValueInMap(Map<String, Object> map) {
        Object obj = null;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            obj = entry.getValue();
            if (obj != null) {
                break;
            }
        }
        return obj != null ? obj.toString() : null;
    }

    public List<FlowAssignee> callbackMethod(FlowApproverRuleLine ruleLine, List<String> valueList, String convertType) {
        try {
            //回调时存储加密
            boolean employeeVariableFlag = WorkflowConstants.ApproverRuleSourceType.VARIABLE.equals(ruleLine.getSourceType()) && WorkflowConstants.ApproverRuleResultType.EMPLOYEE.equals(ruleLine.getResultType());
            if((employeeVariableFlag || EMPLOYEE_NUM.equals(ruleLine.getReturnValueField())) && DataSecurityHelper.isTenantOpen()){
                List<String> tempList = new ArrayList<>(valueList.size());
                valueList.forEach(value->tempList.add(DataSecurityHelper.encrypt(value)));
                valueList = new ArrayList<>(tempList);
            }
            return executeCallbackMethod(ruleLine, valueList,convertType);
        } catch (IOException e) {
            logger.error("error:", e);
            throw new CommonException("error.engine.callback",e.getMessage());
        }
    }
    /***
     * 非默认组织结构：调用回调函数获取审批人详情
     * @param ruleLine 规则行
     * @param valueList 值列表
     * @return List<EmployeeDTO> 员工列表
     * @throws IOException
     */
    private List<FlowAssignee> executeCallbackMethod(FlowApproverRuleLine ruleLine, List<String> valueList,String convertType) throws IOException {
        if(CollectionUtils.isEmpty(valueList)){
            return new ArrayList<>();
        }
        logger.debug(">>>executeCallbackMethod:valueData={}",StringUtils.join(valueList, BaseConstants.Symbol.COMMA));
        //构建参数
        Map<String,Object> bodyParamMap = new HashMap<>(5);
        bodyParamMap.put("tenantId",ruleLine.getTenantId());
        bodyParamMap.put("organizationId",ruleLine.getTenantId());
        bodyParamMap.put("resultType",ruleLine.getResultType());
        bodyParamMap.put("valueFieldCode",ruleLine.getReturnValueField());
        bodyParamMap.put("valueData", StringUtils.join(valueList, BaseConstants.Symbol.COMMA));

        if(WorkflowConstants.ApproverRuleCallbackType.LOV.equals(ruleLine.getCallbackType())){
            LovDTO lovDTO = lovAdapter.queryLovInfo(ruleLine.getCallbackCode(),ruleLine.getTenantId());
            if(LovConstants.LovTypes.SQL.equals(lovDTO.getLovTypeCode())){
                String result = platformFeignClient.getSqlLovData(bodyParamMap,ruleLine.getCallbackCode(),0,0);
                //logger.info(">>>lovCode:{},lov-sql-result:{}",lovDTO.getLovCode(),result);
                return convertCallbackResponse(ruleLine,convertType,result,bodyParamMap);
            }
            if(LovConstants.LovTypes.URL.equals(lovDTO.getLovTypeCode())){
                String serviceName = getServiceName(lovDTO.getRouteName());
                StringBuffer requestUrl = new StringBuffer(lovDTO.getCustomUrl());
                urlAppendParam(requestUrl,bodyParamMap);
                String result = restTemplate.getForObject(REQUEST_PROTOCOL + serviceName + requestUrl.toString(), String.class, bodyParamMap);
                //logger.info(">>>lovCode:{},lov-url-result:{}",lovDTO.getLovCode(),result);
                return convertCallbackResponse(ruleLine,convertType,result,bodyParamMap);
            }
        }
        if(WorkflowConstants.ApproverRuleCallbackType.API.equals(ruleLine.getCallbackType())){
            return convertCallbackResponse(ruleLine,convertType,null,bodyParamMap);
        }
        return new ArrayList<>();
    }

    private void urlAppendParam(StringBuffer requestUrl,Map<String,Object> bodyParamMap){
        requestUrl.append(BaseConstants.Symbol.QUESTION);
        bodyParamMap.forEach((key,value)->{
            requestUrl.append(key).append(BaseConstants.Symbol.EQUAL).append(value).append(BaseConstants.Symbol.AND);
        });
        requestUrl.deleteCharAt(requestUrl.length()-1);
    }

    /***
     * 解析回调函数response转为员工或者用户
     * @param ruleLine
     * @param convertType 回调结果转换成的类型，EMPLOYEE、USER 报文解析为员工还是用户
     * @param lovResult lov结果
     * @param requestParamMap api请求参数
     * @return
     * @throws IOException
     */
    private List<FlowAssignee> convertCallbackResponse(FlowApproverRuleLine ruleLine,String convertType,String lovResult,Map<String,Object> requestParamMap) throws IOException {
        String callbackType = ruleLine.getCallbackType();
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        List<UserDTO> userDTOList = new ArrayList<>();
        if(WorkflowConstants.ApproverRuleCallbackType.LOV.equals(callbackType)){
            Map<String,Object> map = objectMapper.readValue(lovResult,new TypeReference<Map<String,Object>>(){});
            if(EngineConstants.ApproveDimension.EMPLOYEE.equals(convertType)){
                employeeDTOList = map==null ? new ArrayList<>() : objectMapper.readValue(objectMapper.writeValueAsString(map.get("content")),new TypeReference<List<EmployeeDTO>>(){});
            }else if(EngineConstants.ApproveDimension.USER.equals(convertType)){
                userDTOList = map==null ? new ArrayList<>() : objectMapper.readValue(objectMapper.writeValueAsString(map.get("content")),new TypeReference<List<UserDTO>>(){});
            }
        }
        if(WorkflowConstants.ApproverRuleCallbackType.API.equals(callbackType)){
            FlowInterfaceRequestDTO flowInterfaceRequestDTO = new FlowInterfaceRequestDTO();
            FlowInterface callbackInterfaceDto = ruleLine.getCallbackInterfaceDto();
            if(callbackInterfaceDto.getOutInterfaceFlag()==null){
                callbackInterfaceDto.setOutInterfaceFlag(WorkflowConstants.OutInterfaceType.IN_API);
            }
            switch (callbackInterfaceDto.getOutInterfaceFlag()) {
                case WorkflowConstants.OutInterfaceType.IN_API:
                    //调用内部接口获取审批人
                    String apiUrl = callbackInterfaceDto.getInterfaceUrl().replace("{organizationId}",ruleLine.getTenantId().toString()).replace("{tenantId}",ruleLine.getTenantId().toString());
                    StringBuffer requestUrl = new StringBuffer(REQUEST_PROTOCOL).append(callbackInterfaceDto.getServiceName()).append(apiUrl);
                    urlAppendParam(requestUrl,requestParamMap);
                    callbackInterfaceDto.setInterfaceUrl(apiUrl);
                    flowInterfaceRequestDTO = this.buildInterfaceRequestMap(callbackInterfaceDto,null,null,null,null,null,null);
                    break;
                case WorkflowConstants.OutInterfaceType.OUT_API:
                    //调用外部接口获取审批人
                    Map<String,String> headerMap = new HashMap<>(16);
                    headerMap.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
                    Map<String,String> pathVariableMap = new HashMap<>(16);
                    pathVariableMap.put("organizationId",ruleLine.getTenantId().toString());
                    Map<String,String> requestVariableMap = new HashMap<>(16);
                    if(requestParamMap.size()>0){
                        for (String key : requestParamMap.keySet()){
                            requestVariableMap.put(key,requestParamMap.get(key).toString());
                        }
                    }
                    RequestPayloadDTO requestPayloadDTO = new RequestPayloadDTO(headerMap,pathVariableMap,requestVariableMap,new HashMap<>(16),MediaType.APPLICATION_JSON_VALUE,null);
                    flowInterfaceRequestDTO = new FlowInterfaceRequestDTO(callbackInterfaceDto.getOutInterfaceFlag(),callbackInterfaceDto.getNamespace(),callbackInterfaceDto.getServerCode(),callbackInterfaceDto.getOutInterfaceCode(),null,requestPayloadDTO);
                    break;
                case WorkflowConstants.OutInterfaceType.IN_BEAN:
                    //todo 构造bean参数
                    break;
                default:break;
            }
            flowInterfaceRequestDTO.setInterfaceContainer(callbackInterfaceDto);
            String msg = this.self().executeInterface(flowInterfaceRequestDTO);
            if(EngineConstants.ApproveDimension.EMPLOYEE.equals(convertType)){
                employeeDTOList = msg==null ? new ArrayList<>() : objectMapper.readValue(msg,new TypeReference<List<EmployeeDTO>>(){});
            }else if(EngineConstants.ApproveDimension.USER.equals(convertType)){
                userDTOList = msg==null ? new ArrayList<>() : objectMapper.readValue(msg,new TypeReference<List<UserDTO>>(){});
            }
        }
        return flowAssigneeHelper.convertToFlowAssigneeListByRuleLine(ruleLine,employeeDTOList,userDTOList);
    }

    public RequestPayloadDTO buildOutInterfaceRequestPayload(List<FlowDefParameterValue> paramList,Long instanceId,Long nodeId,Long taskId,Map<String, Object> frontParamMap,Map<String, String> headerParamMap,String eventTriggerTypeVar){
        Map<String,String> headerMap = new HashMap<>(16);
        headerMap.put("Content-Type", "application/json");
        if(headerParamMap != null){
            headerMap.putAll(headerParamMap);
        }
        Map<String,String> pathVariableMap = new HashMap<>(16);
        Map<String,String> requestParamMap = new HashMap<>(16);
        Map<String, MultipartFile> multipartFileMap = new HashMap<>(16);
        Map<String,Object> bodyParamMap = new HashMap<>(16);

        if(CollectionUtils.isNotEmpty(paramList)){
            for(FlowDefParameterValue dto : paramList){
                String paramValue = getParamValue(dto,instanceId,nodeId,taskId,frontParamMap, eventTriggerTypeVar);
                String encodeParamValue = null;
                if (paramValue != null) {
                    encodeParamValue = EncoderUtils.encodeFilename(paramValue);
                }
                switch (dto.getUrlType()){
                    case WorkflowConstants.ParameterUrlType.PATH:
                        AssertUtils.notNull(encodeParamValue,"error.engine.helper.path.param.value.empty",dto.getParameterCode());
                        pathVariableMap.put(dto.getParameterCode(),encodeParamValue);
                        break;
                    case WorkflowConstants.ParameterUrlType.PARAM:
                        if(StringUtils.isNotEmpty(encodeParamValue)){
                            requestParamMap.put(dto.getParameterCode(),encodeParamValue);
                        }
                        break;
                    case WorkflowConstants.ParameterUrlType.BODY:
                        bodyParamMap.put(dto.getParameterCode(), paramValue);
                        break;
                    case WorkflowConstants.ParameterUrlType.HEADER:
                        if (Objects.isNull(headerParamMap.get(dto.getParameterCode()))) {
                            // 特定值的传入不进行取值的覆盖
                            headerParamMap.put(dto.getParameterCode(), paramValue);
                        }
                        break;
                    default:break;
                }
            };
        }
        return new RequestPayloadDTO(headerMap,pathVariableMap,requestParamMap,multipartFileMap,MediaType.APPLICATION_JSON_VALUE,JsonUtils.toJson(objectMapper,bodyParamMap));
    }


    public FlowInterfaceRequestDTO buildInterfaceRequestMap(FlowInterface flowInterface,List<FlowDefParameterValue> parameterValueList,Long instanceId,Long nodeId,Long taskId,Map<String, Object> frontParamMap,String eventTriggerTypeVar) {
        return this.buildInterfaceRequestMap(flowInterface,parameterValueList,instanceId,nodeId,taskId,frontParamMap,new HashMap<>(1),eventTriggerTypeVar);
    }
    /***
     * 构造接口请求信息
     * @param flowInterface  接口定义
     * @param parameterValueList 接口参数
     * @param instanceId 可为null
     * @param nodeId 可为null
     * @param taskId 可为null
     * @param frontParamMap 可为null，审批人预览时传输的界面参数、或者自定义节点以及构造好的参数值
     * @param headerParamMap header参数，如{"Content-type":"application/json"}
     * @return
     */
    public FlowInterfaceRequestDTO buildInterfaceRequestMap(FlowInterface flowInterface,List<FlowDefParameterValue> parameterValueList,Long instanceId,Long nodeId,Long taskId,Map<String, Object> frontParamMap,Map<String, String> headerParamMap,String eventTriggerTypeVar){
        if(flowInterface.getOutInterfaceFlag()==null){
            flowInterface.setOutInterfaceFlag(WorkflowConstants.OutInterfaceType.IN_API);
        }
        FlowInterfaceRequestDTO result = new FlowInterfaceRequestDTO();
        switch (flowInterface.getOutInterfaceFlag()){
            case WorkflowConstants.OutInterfaceType.IN_API:
                RequestEntity<String> requestEntity = this.buildRequestEntity(flowInterface, parameterValueList, instanceId, nodeId,taskId,frontParamMap,headerParamMap,eventTriggerTypeVar);
                result = new FlowInterfaceRequestDTO(flowInterface.getOutInterfaceFlag(),flowInterface.getNamespace(),flowInterface.getServerCode(),flowInterface.getOutInterfaceCode(),requestEntity,null);
                break;
            case WorkflowConstants.OutInterfaceType.OUT_API:
            case WorkflowConstants.OutInterfaceType.SCRIPT:
            case WorkflowConstants.OutInterfaceType.TX_FLOW:
                RequestPayloadDTO requestPayloadDTO = this.buildOutInterfaceRequestPayload(parameterValueList, instanceId, nodeId,taskId,frontParamMap,headerParamMap,eventTriggerTypeVar);
                result = new FlowInterfaceRequestDTO(flowInterface.getOutInterfaceFlag(),flowInterface.getNamespace(),flowInterface.getServerCode(),flowInterface.getOutInterfaceCode(),null,requestPayloadDTO);
                result.setBodyParamMap(JsonUtils.fromJson(requestPayloadDTO.getPayload(), new TypeReference<Map<String, Object>>() {}));
                break;
            case WorkflowConstants.OutInterfaceType.IN_BEAN:
                result.setBodyParamMap(frontParamMap);
                result.setServiceName(flowInterface.getServiceName());
                result.setHandlerCode(flowInterface.getHandlerCode());
                result.setOutInterfaceFlag(flowInterface.getOutInterfaceFlag());
                break;
            default:break;
        }
        result.setTenantId(flowInterface.getTenantId()).setInstanceId(instanceId).setNodeId(nodeId).setTaskId(taskId);
        result.setEventTriggerTypeVar(eventTriggerTypeVar);
        return result;
    }

    /***
     * 执行接口：内部&&外部接口&&内部bean
     * @param flowInterfaceRequestDTO 接口请求DTO
     */
    public String executeInterface(FlowInterfaceRequestDTO flowInterfaceRequestDTO) {
        AssertUtils.notNull(flowInterfaceRequestDTO,"error.interface.empty");
        if(flowInterfaceRequestDTO.getOutInterfaceFlag()==null){
            flowInterfaceRequestDTO.setOutInterfaceFlag(WorkflowConstants.OutInterfaceType.IN_API);
        }
        switch (flowInterfaceRequestDTO.getOutInterfaceFlag()){
            case WorkflowConstants.OutInterfaceType.IN_API:
                ResponseEntity<String> responseEntity = retryTemplate.execute(new RetryCallback<ResponseEntity<String>, RuntimeException>() {
                    @Override
                    public ResponseEntity<String> doWithRetry(RetryContext retryContext) throws RuntimeException {
                        logger.info("retry...:{}", retryContext.getRetryCount());
                        return restTemplate.exchange(flowInterfaceRequestDTO.getRequestEntity(), String.class);
                    }
                }, new RecoveryCallback<ResponseEntity<String>>() {
                    @Override
                    public ResponseEntity<String> recover(RetryContext retryContext){
                        logger.info("error:", retryContext.getLastThrowable().getMessage());
                        return new ResponseEntity<String>(retryContext.getLastThrowable().getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                });
                if(responseEntity.getStatusCode().is2xxSuccessful()){
                    return responseEntity.getBody();
                }else {
                    throw new CommonException(responseEntity.getBody());
                }
            case WorkflowConstants.OutInterfaceType.OUT_API:
                ResponsePayloadDTO responsePayloadDTO = interfaceInvokeSdk.invoke(flowInterfaceRequestDTO.getNamespace(), flowInterfaceRequestDTO.getServerCode(), flowInterfaceRequestDTO.getOutInterfaceCode(), flowInterfaceRequestDTO.getRequestPayloadDTO());
                boolean successFlag = StringUtils.equalsAny(responsePayloadDTO.getStatus(),"200","success");
                if(successFlag){
                    return responsePayloadDTO.getPayload();
                }else {
                    throw new CommonException(responsePayloadDTO.getMessage());
                }
            case WorkflowConstants.OutInterfaceType.IN_BEAN:
                String url = String.format("%s%s/v1/%s/workflow-plugin/event/%s", REQUEST_PROTOCOL,flowInterfaceRequestDTO.getServiceName(), DetailsHelper.getUserDetails().getTenantId(), flowInterfaceRequestDTO.getHandlerCode());
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(flowInterfaceRequestDTO.getBodyParamMap(), headers), String.class);
                if(response.getStatusCode().is2xxSuccessful()){
                    return response.getBody();
                }else {
                    throw new CommonException(response.getBody());
                }
            case WorkflowConstants.OutInterfaceType.SCRIPT:
                // 业务对象脚本
                // FlowInterface scriptInstance = (FlowInterface) flowInterfaceRequestDTO.getInterfaceContainer();
                ScriptExecutionResult scriptResult = boHelper.boScriptExecute(flowInterfaceRequestDTO);
                return scriptResult.getResult()==null?null:JsonUtils.toJson(objectMapper,scriptResult.getResult());
            case WorkflowConstants.OutInterfaceType.TX_FLOW:
                // 事务处理流
                // FlowInterface txFlowInstance = (FlowInterface) flowInterfaceRequestDTO.getInterfaceContainer();
                ScriptExecutionResult flowResult = boHelper.boFlowExecute(flowInterfaceRequestDTO);
                return flowResult.getResult()==null?null:JsonUtils.toJson(objectMapper,flowResult.getResult());
            default:break;
        }
        return null;
    }
}
