import React, { useCallback, useEffect, useRef } from 'react';
import { Modal } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { IModalProps } from '@/common/types';
import Buttons from '@/components/buttons';
import { SuggestRef } from '@/components/suggest';
import { includes } from 'lodash';
import BaseModal, { BaseModalProps } from '../components/base-modal';
import History from '../components/history';
import FlowChart from '../components/flow-chart';
import Suggest from '../components/suggest';
import store from '../store';

const prefix = 'c7n-backlogApprove-modal';
const needValidateActions = ['APPROVED', 'REJECTED'];
export interface ApproveModalProps {
  modal?: IModalProps,
  taskId: string
  onClose: () => void
  SummaryComponent?: React.ReactNode
  extraTabs?: BaseModalProps['tabs']
}
const ApproveModal: React.FC<ApproveModalProps> = (props) => {
  const {
    modal, SummaryComponent, extraTabs, taskId, onClose,
  } = props;
  useEffect(() => {
    if (taskId) {
      store.getProcess(taskId);
    }
  }, [taskId]);
  const handleClose = useCallback(() => {
    modal?.close();
  }, [modal]);
  const handleRefresh = useCallback(() => {
    onClose && onClose();
    handleClose();
  }, [handleClose, onClose]);
  const ref = useRef<SuggestRef>({} as SuggestRef);
  const getComment = async () => {
    const dataSet = ref.current?.dataSet;
    if (dataSet && await dataSet.validate()) {
      return dataSet.current?.get('commentContent');
    }
    return false;
  };
  return (
    <BaseModal
      loading={store.loading}
      tabTop={SummaryComponent}
      tabs={[...(extraTabs ?? []),
        {
          title: '审核意见',
          key: 'suggest',
          component: () => <Suggest innerRef={ref} />,
        },
        {
          title: '审核历史',
          key: 'history',
          component: History,
        },
        {
          title: '缩略图',
          key: 'flowChart',
          component: FlowChart,
        }]}
      footer={({ activeTab, setActiveTab }) => (
        <Buttons
          taskId={taskId}
          getComment={getComment}
          onSubmit={handleRefresh}
          onClick={(item) => {
            if (activeTab !== 'suggest' && includes(needValidateActions, item.value)) {
              setActiveTab('suggest');
              return false;
            }
            return true;
          }}
          extraBtns={[{
            meaning: '关闭',
            value: 'close',
            checked: true,
            onClick: handleClose,
            style: {
              backgroundColor: '#3f51b5',
              color: '#FFF',
            },
            className: `${prefix}-container-footer-closeBtn`,
          }]}
          outLoading={store.loading}
          buttonStyle={{
            backgroundColor: '#FFF',
            color: '#3f51b5',
          }}
        />
      )}
    />
  );
};
const ObserverApproveModal = observer(ApproveModal);
const openApproveModal = (props: ApproveModalProps) => Modal.open({
  key: 'BaseModal',
  title: '审批详情',
  drawer: true,
  children: <ObserverApproveModal {...props} />,
  style: {
    width: 740,
  },
  className: prefix,
  footer: null,
});
export default openApproveModal;
