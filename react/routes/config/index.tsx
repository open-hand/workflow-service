/* eslint-disable no-underscore-dangle */
import React, { useCallback } from 'react';
import { Button, Modal } from 'choerodon-ui/pro';
import {
  Page, Content, Breadcrumb, Choerodon, Header, HeaderButtons,
} from '@choerodon/master';
import { getCookie } from '@choerodon/master/lib/utils';
import Empty from '@choerodon/agile/lib/components/Empty';
import { ButtonColor, FuncType } from 'choerodon-ui/pro/lib/button/enum';
import { useRequest } from 'ahooks';
import { pick } from 'lodash';
import { workFlowApi } from '@/api';
import pic from './hzero.svg';

const Config = () => {
  const handleClick = useCallback(() => {
    const { HZERO_FRONT } = window._env_;
    const accessToken = getCookie('access_token', { path: '/' });
    const tokenType = getCookie('token_type', { path: '/' });
    window.open(`${HZERO_FRONT}/hwkf#access_token=${accessToken}&token_type=${tokenType}`);
  }, []);
  const {
    data: inited = true, refresh, loading,
  } = useRequest(() => workFlowApi.checkInit());
  const handleInitClick = useCallback(() => {
    const modalConfig = inited ? {
      title: '更新预置需求审核流程模板',
      children: '确认更新预置需求审核工作流模板？系统将把最新的系统预置需求审核模板增量更新到您已导入的需求审核工作流模板，以便于您获取最新的预置审核变量和审批人规则。',
      prompt: '更新成功',
      request: () => workFlowApi.updateInit(),
    } : {
      title: '导入预置需求审核流程',
      children: '确认导入预置需求审核流程？导入后，系统将在组织预置一套需求审核流程分类和需求审核流程，您可以按需配置好审批人规则，并且发布流程后即可使用。',
      prompt: '导入成功',
      request: () => workFlowApi.init(),
    };
    Modal.open({
      ...pick(modalConfig, ['title', 'children']),
      onOk: async () => {
        await modalConfig.request();
        await refresh();
        Choerodon.prompt(modalConfig.prompt);
      },
    });
  }, [inited, refresh]);

  return (
    <Page>
      <Header>
        <HeaderButtons
          showClassName={false}
          items={[
            {
              name: `${inited ? '更新' : '导入'}预置流程模板`,
              icon: inited ? 'published_with_changes' : 'archive',
              display: true,
              loading,
              handler: handleInitClick,
            },
          ]}
        />
      </Header>
      <Breadcrumb />
      <Content style={{ borderTop: '1px solid var(--divider)' }}>

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
                color={'primary' as ButtonColor}
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
