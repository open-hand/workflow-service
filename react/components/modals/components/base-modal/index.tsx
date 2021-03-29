import React, { useState } from 'react';
import { observer } from 'mobx-react-lite';
import { Tabs } from 'choerodon-ui';
import { find } from 'lodash';
import Loading from '@choerodon/agile/lib/components/Loading';
import './index.less';

export interface FooterProps {
  activeTab: string,
  setActiveTab: (key: string) => void
}
export interface BaseModalProps {
  loading?: boolean
  title?: string
  tabs: {
    title: string,
    key: string,
    component: React.FC<any>
  }[]
  defaultActiveTab?: string
  tabTop?: React.ReactNode,
  footer: (props: FooterProps) => React.ReactNode
}
const { TabPane } = Tabs;

const prefix = 'c7n-backlogApprove-modal';
const BaseModal: React.FC<BaseModalProps> = (props) => {
  const {
    loading, tabs = [], defaultActiveTab, tabTop, footer,
  } = props;
  const [activeTab, setActiveTab] = useState(defaultActiveTab || tabs[0].key);
  const Component = find(tabs, { key: activeTab })?.component;
  return (
    <div className={`${prefix}-container`}>
      <div className={`${prefix}-container-title`}>
        <div className={`${prefix}-container-title-summary`}>
          {tabTop}
        </div>
        <div className={`${prefix}-container-title-tab`}>
          <Tabs activeKey={activeTab} onChange={setActiveTab}>
            {
              tabs.map((item) => (
                <TabPane
                  key={item.key}
                  tab={item.title}
                />
              ))
            }
          </Tabs>
        </div>
      </div>
      {
        loading ? (
          <Loading loading />
        ) : (
          <div className={`${prefix}-container-content`}>
            {Component && <Component />}
          </div>

        )
      }
      {
        !loading && (
          <div className={`${prefix}-container-footer`}>
            {footer({ activeTab, setActiveTab })}
          </div>
        )
      }
    </div>
  );
};
const ObserverBaseModal = observer(BaseModal);
export default ObserverBaseModal;
// const openBaseModal = (props: BaseModalProps) => Modal.open({
//   key: 'BaseModal',
//   title: props.title ?? '审批详情',
//   drawer: true,
//   children: <ObserverBaseModal {...props} />,
//   className: prefix,
//   style: {
//     width: 740,
//   },
//   footer: null,
// });
// export default openBaseModal;
