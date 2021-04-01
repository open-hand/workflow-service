import { axios } from '@choerodon/boot';
import Api from '@choerodon/agile/lib/api/Api';

class WorkFlowApi extends Api<WorkFlowApi> {
  get prefix() {
    return `/cwkf/choerodon/v1/organizations/${this.orgId}/organization_invoke_workflow`;
  }

  init() {
    return axios({
      method: 'post',
      url: `${this.prefix}/def_workflow/init`,
    });
  }

  checkInit(): Promise<boolean> {
    return axios({
      method: 'get',
      url: `${this.prefix}/def_workflow/check_init`,
    });
  }
}

const workFlowApi = new WorkFlowApi();
export { workFlowApi };
