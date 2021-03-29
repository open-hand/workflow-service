/* eslint-disable no-underscore-dangle */
import React, { useCallback } from 'react';
import { Button } from 'choerodon-ui/pro';
import { Page, Content, Breadcrumb } from '@choerodon/master';
import Empty from '@choerodon/agile/lib/components/Empty';
import { ButtonColor, FuncType } from 'choerodon-ui/pro/lib/button/enum';
import pic from './hzero.svg';

const Config = () => {
  const handleClick = useCallback(() => {
    const { HZERO_FRONT } = window._env_;
    window.open(`${HZERO_FRONT}/hwkf`);
  }, []);
  return (
    <Page>
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
