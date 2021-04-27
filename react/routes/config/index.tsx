/* eslint-disable no-underscore-dangle */
import React, { useCallback } from 'react';
import { Button, Modal } from 'choerodon-ui/pro';
import {
  Page, Content, Breadcrumb, Choerodon, Header,
} from '@choerodon/master';
import { getCookie } from '@choerodon/master/lib/utils';
import Empty from '@choerodon/agile/lib/components/Empty';
import { ButtonColor, FuncType } from 'choerodon-ui/pro/lib/button/enum';
import { workFlowApi } from '@/api';
import { useRequest } from 'ahooks';
import pic from './hzero.svg';

const Config = () => {
  const handleClick = useCallback(() => {
    const { HZERO_FRONT } = window._env_;
    const accessToken = getCookie('access_token', { path: '/' });
    const tokenType = getCookie('token_type', { path: '/' });
    window.open(`${HZERO_FRONT}/hwkf#access_token=${accessToken}&token_type=${tokenType}`);
  }, []);
  const {
    data: inited, refresh,
  } = useRequest(() => workFlowApi.checkInit());
  const handleInitClick = useCallback(() => {
    Modal.confirm({
      title: '导入预置需求审核流程',
      children: '确认导入预置需求审核流程？导入后，系统将在组织预置一套需求审核流程分类和需求审核流程，您可以按需配置好审批人规则，并且发布流程后即可使用。',
      onOk: async () => {
        await workFlowApi.init();
        await refresh();
        Choerodon.prompt('导入成功');
      },
    });
  }, [refresh]);

  return (
    <Page>
      {!inited && (
        <Header>
          <Button
            icon="archive"
            onClick={handleInitClick}
            funcType={'flat' as FuncType}
            color={'blue' as ButtonColor}
          >
            导入预置流程模板
          </Button>
        </Header>
      )}
      <Breadcrumb />
      <Content style={{ borderTop: '1px solid #d8d8d8' }}>

        <Empty
          style={{ paddingTop: '15vh' }}
          title="工作流"
          description={(
            <>
              <div>
                需求审核可使用工作流自定义审核流程。
                <br />
                点击下方按钮跳转至HZERO框架进行工作流配置。
              </div>
              <Button
                style={{ fontSize: '14px', marginTop: 15 }}
                color={'blue' as ButtonColor}
                funcType={'raised' as FuncType}
                onClick={handleClick}
              >
                跳转至HZERO框架
              </Button>
            </>
          )}
          pic={pic}
        />
      </Content>
    </Page>
  );
};

export default Config;
