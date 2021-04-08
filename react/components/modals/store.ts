import {
  observable, action,
} from 'mobx';
import {
  IProcess, IFlowData, ProcessHistory,
} from '@/common/types';

import { approveApi } from '@/api';

class ApproveStore {
  @observable loading: boolean = false;

  @observable process: IProcess = {
    approveActionList: [],
    historyList: [],
    commentTemplates: [],
    taskDetail: {} as IProcess['taskDetail'],
    attachmentUuid: null,
  } as IProcess;

  @action setProcess = (data: IProcess) => {
    this.process = data;
  }

  @action
  async getProcess(taskId: string, type: 'project' | 'org' = 'org', instanceId?: string) {
    this.loading = true;
    const res = await approveApi.getProcess(taskId);
    this.setProcess(res);
    if (type === 'org') {
      await this.getHistory(instanceId || res.taskDetail.instanceId);
      await this.getFlowData(instanceId || res.taskDetail.instanceId);
      this.loading = false;
    } else {
      this.loading = false;
    }
  }

  @observable flowData: IFlowData = {
    flowModel: JSON.stringify({ displayJson: { edgeList: [] } }),
    processInstanceNodeHistory: [],
  };

  @action clear() {
    this.flowData = {
      flowModel: JSON.stringify({ displayJson: { edgeList: [] } }),
      processInstanceNodeHistory: [],
    };
  }

  @action
  async getFlowData(instanceId: string) {
    const res = await approveApi.getFlowData(instanceId);
    this.flowData = res;
  }

  @observable historyList: ProcessHistory[] = [];

  @action
  async getHistory(instanceId: string) {
    const res = await approveApi.loadHistoryByInstanceId(instanceId);
    this.historyList = res;
  }
}
export { ApproveStore };
export default new ApproveStore();
