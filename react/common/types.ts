import { ModalProps } from 'choerodon-ui/pro/lib/modal/interface';

export interface DetailContext {
  id: number
  store: any
  topAnnouncementHeight: number
  projectId?: number
  hasAdminPermission: boolean
  disabledDetailEdit: boolean
  disabledStatusEdit: boolean
  disabledAssigneeEdit: boolean
  disabledFeedbackEdit: boolean
  disabledAttachmentEdit: (userId?: string) => boolean
  disabledSolveEdit: boolean
  disabledCommentEdit: boolean
  disabledApproveEdit: boolean
  outside?: boolean
  organizationId?: string
  closeButton?: boolean
}
export type IUseDetailContext = () => DetailContext;
export interface User {
  email: string
  enabled?: boolean
  id: string
  imageUrl: string | null
  ldap: boolean
  loginName: string
  realName: string
  name: string,
}
export interface INodeHistory {
  instanceId: string,
  nodeId: string,
  nodeCode: string,
  nodeType: string,
  historyType: string,
  startDate: string
  endDate: string
  tenantId: string

  nodeHistoryId: string,
  nodeName: string,
  taskHistoryList: ITaskHistory[]
}

export interface ITaskHistory {
  instanceId: string,
  nodeId: string,
  nodeCode: string,
  nodeType: string,
  historyType: string,
  startDate: string
  endDate: string
  tenantId: string

  parentNodeId: string,
  taskHistoryId: string,
  taskId: string,
  assignee: string,
  status: string,
  statusMeaning: string,
  commentContent: string,
  positionName: null | string,
  unitName: null | string,
}

export interface ITaskHistoryWithNodeName extends ITaskHistory {
  nodeName: string
}

export interface ApproveLog {
  assignee?: string
  attachmentUuid?: string
  commentContent?: string
  createdBy: string | number
  creationDate: string
  deploymentId: number
  endDate: string
  historyType: string
  instanceId: string | number
  lastUpdateDate: string
  lastUpdatedBy: string | number
  nodeCode: string
  nodeId: string | number
  nodeName: string
  nodeType: string
  objectVersionNumber: number
  parentNodeId?: string | number
  parentTaskId?: string | number
  startDate: string
  status: string
  statusMeaning?: string
  taskHistoryId: string | number
  taskId?: string | number
  tenantId: string | number
  remark?: string
  toPerson?: string
  carbonCopyComment?: string
  subProcessHistoryList?: ApproveLog[]
}

export interface ProcessHistory {
  userDTO: User
  runTaskHistory: ApproveLog
}
export interface IApproveBtn {
  actionType: string
  checked: boolean
  meaning: string
  value: string
}

export interface TaskDetail {
  tenantId: string
  taskId: string
  selfUserId: string
  instanceId: string
  businessKey: string
  nodeId: string
}
export interface ICommentTemplate {
  commentContent: string
  commentTemplateId: symbol
  createdBy: string
  creationDate: string
  employeeNum: string
  enabledFlag: 1 | 0
  lastUpdateDate: string
  lastUpdatedBy: string
  objectVersionNumber: number
  tenantId: string
}
export type ICommentTemplateCreate = Pick<ICommentTemplate, 'enabledFlag' | 'tenantId' | 'commentContent'>
export interface IProcess {
  attachmentUuid: string | null
  approveActionList: IApproveBtn[]
  historyList: ApproveLog[]
  taskDetail: TaskDetail
  commentTemplates: ICommentTemplate[]
}
export interface IProcessAttachment {
  bucketName: string
  createdBy: string
  creationDate: string
  fileName: string
  fileSize: number
  fileType: string
  fileUrl: string
  loginName: string
  realName: string
}

export interface InstanceNodeHistory {
  endDate: string
  historyType: string
  instanceId: string
  nodeCode: string
  nodeHistoryId: string
  nodeId: string
  nodeName: string
  nodeType: string
  startDate: string
  taskHistoryList: object[]
  tenantId: string
}

export interface IFlowData {
  flowModel: string
  processInstanceNodeHistory: InstanceNodeHistory[]
}

export interface IHistoryAttachment {
  bucketName: string
  createdBy: string
  creationDate: string
  fileName: string
  fileSize: number
  fileType: string
  fileUrl: string
  loginName: string
  realName: string
}
export interface IModalProps extends ModalProps {
  handleOk: (promise: () => Promise<boolean>) => Promise<void>,
  handleCancel: (promise: () => Promise<boolean>) => Promise<void>,
  close: (destroy?: boolean) => void,
  update: (modalProps: ModalProps) => void
}
