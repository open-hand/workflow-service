import { axios } from '@choerodon/boot';
import Api from '@choerodon/agile/lib/api/Api';
import { getOrganizationId } from '@choerodon/agile/lib/utils/common';
import {
  ICommentTemplate, ICommentTemplateCreate, IFlowData, IProcessAttachment,
} from '@/common/types';

export interface INextNodeApprover {
  code: string,
  employee: {
    realName: string,
    employeeCode: string,
    id: string,
  }
  name: string,
}
export interface NextNodeApproveData {
  approveComment?: string
  approverSourceType: 'ANY_APPROVER' | 'DEFAULT_APPROVER'
  nextNodeCode: string
  toPersonList: ({
    value: string
    name: string
    loginName: string
  } | INextNodeApprover)[]
}

export interface AddApproveData {
  addApproverPerson: {
    id: string
    realName: string
  }[],
  addApproverType: string
  remark: string
  toPersonList: {
    value: string
    name: string
  }[]
  approveComment?: string
  currentAction?: 'APPROVED'
}
export interface IAttachUpload {
  attachmentUuid: string
  instanceId: string
  nodeId: string
  taskId: string
  tenantId: string
}
export interface InstanceDetail {
  deploymentId?: any;
  instanceId: number;
  taskId?: any;
  parentTaskId?: any;
  taskCode?: any;
  flowName: string;
  description: string;
  nodeName: string;
  assignee: string;
  currentNodeAssignee: string;
  selfEmpNum?: any;
  starter: string;
  starterNumber: string;
  startDate: string;
  instanceStartDate?: any;
  instanceEndDate?: any;
  endDate?: any;
  taskName?: any;
  taskStatus?: any;
  instanceStatus: string;
  instanceStatusMeaning: string;
  remark?: any;
  taskType?: any;
  taskTypeMeaning?: any;
  nodeId?: any;
  taskHistoryId?: any;
  lastUpdateDate?: any;
  taskDescription?: any;
  todoType?: any;
  parentInstanceId?: any;
  parentInstanceNodeId?: any;
  subProcessChildren?: any;
  parentDescription?: any;
  urgeEnableFlag: 0 | 1
}
class ApproveApi extends Api<ApproveApi> {
  get prefix() {
    return `/hwkf/v1/${this.orgId}`;
  }

  get ProcessPrefix() {
    return this.levelType === 'project' ? `/cwkf/choerodon/v1/projects/${this.projectId}/project_invoke_workflow` : `/cwkf/choerodon/v1/organizations/${this.orgId}/organization_invoke_workflow`;
  }

  get levelType() {
    return 'org';
  }

  type(type: 'project' | 'org') {
    return this.overwrite('levelType', type);
  }

  getFlowData(instanceId: string): Promise<IFlowData> {
    return axios({
      method: 'get',
      url: `${this.ProcessPrefix}/monitor_process/${instanceId}/diagram`,
      params: {
      },
    });
  }

  getProcess(taskId: string) {
    return axios({
      method: 'get',
      url: `${this.ProcessPrefix}/personal_process/task/${taskId}`,
      params: {
      },
    });
  }

  getCarbonCopied(taskHistoryId: string, carbonCopyTodoFlag: 0 | 1) {
    return axios({
      method: 'get',
      url: `${this.ProcessPrefix}/personal_process/carbon_copied/${taskHistoryId}`,
      params: {
        carbonCopyTodoFlag,
      },
    });
  }

  getSubmitted(instanceId: string) {
    return axios({
      method: 'get',
      url: `${this.ProcessPrefix}/personal_process/submitted/${instanceId}`,
    });
  }

  getParticipated(instanceId: string) {
    return axios({
      method: 'get',
      url: `${this.ProcessPrefix}/personal_process/participated/${instanceId}`,
    });
  }

  urge(instanceIds: string) {
    return axios({
      method: 'post',
      url: `${this.ProcessPrefix}/personal_process/urge`,
      params: {
        instanceIds,
      },
    });
  }

  approved(taskId: string, comment: string) {
    return axios({
      method: 'post',
      url: `${this.ProcessPrefix}/personal_process/approve`,
      params: {
        comment,
        taskIds: taskId,
      },
      data: {},
    });
  }

  reject(taskId: string, comment: string) {
    return axios({
      method: 'post',
      url: `${this.ProcessPrefix}/personal_process/reject`,
      params: {
        comment,
        taskIds: taskId,
      },
      data: {},
    });
  }

  /**
   * 抄送
   * @param taskId
   * @param toPersons
   * @returns
   */
  carbonCopy(taskId: string, toPersons: string[]) {
    return axios({
      method: 'post',
      url: `${this.ProcessPrefix}/personal_process/carbon-copy`,
      params: {
        taskId,
        toPerson: toPersons.join(','),
      },
      data: {},
    });
  }

  delegateTo(taskId: string, delegateTo: string) {
    return axios({
      method: 'post',
      url: `${this.ProcessPrefix}/personal_process/${taskId}/executeTaskById`,
      params: {
        approveAction: 'DELEGATE',
      },
      data: {
        delegateTo,
      },
    });
  }

  forecastNextNode(taskId: string) {
    return axios({
      method: 'get',
      url: `${this.ProcessPrefix}/personal_process/${taskId}/forecastNextNode`,
    });
  }

  getEmployees(page: number = 1, selfEmpNum: string, empName?: string, size: number = 20) {
    return axios({
      method: 'get',
      url: '/hpfm/v1/lovs/sql/data',
      params: {
        lovCode: 'HWKF.RULE.SELECT_USER',
        enabledFlag: 1,
        tenantId: getOrganizationId(),
        page,
        size,
        selfEmpNum,
        realName: empName,
      },
    });
  }

  saveCommentTemplates(data: (ICommentTemplateCreate | ICommentTemplate)[]) {
    return axios({
      method: 'post',
      url: `${this.ProcessPrefix}/run_comment_templates`,
      data,
      params: {
        organizationId: this.orgId,
      },
    });
  }

  deleteCommentTemplates(data: ICommentTemplate) {
    return axios({
      method: 'delete',
      url: `${this.ProcessPrefix}/run_comment_templates`,
      data,
      params: {
        organizationId: this.orgId,
      },
    });
  }

  refreshCommentTemplates(): Promise<ICommentTemplate[]> {
    return axios({
      method: 'get',
      url: `${this.ProcessPrefix}/${this.levelType === 'project' ? 'run-comment-templates' : 'run_comment_templates'}`,
      params: {
        organizationId: this.orgId,
      },
    });
  }

  getUUID() {
    return axios({
      method: 'post',
      url: `hfle/v1/${this.orgId}/files/uuid`,
    });
  }

  uploadProcessAttachment(data: FormData): Promise<IProcessAttachment[]> {
    return axios({
      method: 'post',
      url: `hfle/v1/${this.orgId}/files/attachment/multipart`,
      data,
    });
  }

  refreshProcessAttachment(attachmentUUID: string): Promise<IProcessAttachment[]> {
    return axios({
      method: 'get',
      url: `hfle/v1/${this.orgId}/files/${attachmentUUID}/file`,
      params: {
        attachmentUUID,
        bucketName: 'hwkf',
      },
    });
  }

  AttachUpload(data: IAttachUpload) {
    return axios({
      method: 'post',
      url: `cwkf/choerodon/v1/organizations/${this.orgId}/organization_invoke_workflow/personal_process/attach_upload`,
      data,
    });
  }

  deleteProcessAttachment(attachmentUUID: string, urls: string[]) {
    return axios({
      method: 'post',
      url: `hfle/v1/${this.orgId}/files/delete-by-uuidurl`,
      params: {
        attachmentUUID,
        bucketName: 'hwkf',
      },
      data: urls,
    });
  }

  getSourceType() {
    return this.request({
      method: 'get',
      url: `hpfm/v1/${getOrganizationId()}/lovs/data`,
      params: {
        lovCode: 'HWKF.NEXT.APPROVER.SOURCE_TYPE',
      },
    });
  }

  nextNodeApprove(taskId: string, data: NextNodeApproveData) {
    return axios({
      method: 'post',
      url: `${this.ProcessPrefix}/personal_process/${taskId}/executeTaskById`,
      params: {
        approveAction: 'APPOINT_NEXT_NODE_APPROVER',
      },
      data,
    });
  }

  rebutNodeList(taskId: string) {
    return axios({
      method: 'get',
      url: `${this.ProcessPrefix}/personal_process/${taskId}/rebutNodeList`,
    });
  }

  rebut(taskId: string, rebutTo: string) {
    return axios({
      method: 'post',
      url: `${this.ProcessPrefix}/personal_process/${taskId}/executeTaskById`,
      params: {
        approveAction: 'REBUT',
      },
      data: {
        rebutTo,
      },
    });
  }

  getAddApproveType() {
    return this.request({
      method: 'get',
      url: `hpfm/v1/${getOrganizationId()}/lovs/data`,
      params: {
        lovCode: 'HWKF.ADD_TASK_APPROVER_TYPE',
      },
    });
  }

  addApprove(taskId: string, data: AddApproveData) {
    return axios({
      method: 'post',
      url: `${this.ProcessPrefix}/personal_process/${taskId}/executeTaskById`,
      params: {
        approveAction: 'ADD_TASK_APPROVER',
      },
      data,
    });
  }

  customAction(taskId: string, approveAction: string) {
    return axios({
      method: 'post',
      url: `${this.ProcessPrefix}/personal_process/${taskId}/executeTaskById`,
      params: {
        approveAction,
      },
      data: {
        actionType: 'CUSTOMIZE',
      },
    });
  }

  comment(comment: string, taskHistoryId: string) {
    return axios({
      method: 'post',
      url: `${this.ProcessPrefix}/personal_process/carbon_comment`,
      params: {
        comment,
        taskHistoryId,
      },
    });
  }

  getAttachments(attachmentUUID: string) {
    return axios({
      method: 'get',
      url: `hfle/v1/${this.orgId}/files/${attachmentUUID}/file`,
      params: {
        attachmentUUID,
        bucketName: 'hwkf',
      },
    });
  }

  loadHistoryByInstanceId(instanceId: string) {
    return axios({
      method: 'get',
      url: `/cwkf/choerodon/v1/organizations/${this.orgId}/personal_process/approve_history`,
      params: {
        instanceId,
      },
    });
  }

  getInstanceStatus() {
    return this.request({
      method: 'get',
      url: `hpfm/v1/${getOrganizationId()}/lovs/data`,
      params: {
        lovCode: 'HWKF.PROCESS_INSTANCE_STATUS',
      },
    });
  }
}

const approveApi = new ApproveApi();
const approveApiConfig = new ApproveApi(true);
export { approveApi, approveApiConfig };
