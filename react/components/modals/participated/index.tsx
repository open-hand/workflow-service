import React, { useCallback, useEffect, useState } from 'react';
import { Button, Modal } from 'choerodon-ui/pro';
import {
  IFlowData, IModalProps, ProcessHistory,
} from '@/common/types';
import { approveApi, InstanceDetail } from '@/api';
import History from '@/components/history';
import FlowChart from '@/components/flow-chart';
import ProcessDetail from '@/components/process-detail';
import { ButtonColor, FuncType } from 'choerodon-ui/pro/lib/button/enum';
import BaseModal, { BaseModalProps } from '../components/base-modal';

const prefix = 'c7n-backlogApprove-modal';
export interface ParticipatedModalProps {
  modal?: IModalProps,
  taskId: string
  instanceId: string
  onClose: () => void
  SummaryComponent?: React.ReactNode
  extraTabs?: BaseModalProps['tabs']
}

const ParticipatedModal: React.FC<ParticipatedModalProps> = (props) => {
  const {
    modal, SummaryComponent, extraTabs, taskId, instanceId, onClose,
  } = props;
  const [state, setState] = useState<{
    loading: boolean
    data: {
      instanceDetail: Partial<InstanceDetail>
    },
    historyList: ProcessHistory[]
    flowData: IFlowData | null
  }>({
    loading: false,
    historyList: [],
    data: {
      instanceDetail: {},
    },
    flowData: null,
  });
  const refresh = useCallback(async () => {
    const data = await approveApi.getParticipated(instanceId);
    let historyList = [];
    if (instanceId) {
      historyList = await approveApi.loadHistoryByInstanceId(instanceId);
    }
    const flowData = await approveApi.getFlowData(instanceId);
    setState({
      loading: false,
      historyList,
      data,
      flowData,
    });
  }, [instanceId]);
  useEffect(() => {
    refresh();
  }, [refresh]);
  const handleClose = useCallback(() => {
    modal?.close();
    onClose();
  }, [modal, onClose]);
  return (
    <BaseModal
      loading={state.loading}
      tabTop={SummaryComponent}
      tabs={[...(extraTabs ?? []),
        {
          title: '审核意见',
          key: 'suggest',
          component: () => state.data.instanceDetail && <ProcessDetail data={state.data.instanceDetail} />,
        },
        {
          title: '审核历史',
          key: 'history',
          component: () => (
            <div style={{ padding: '0 20px' }}>
              <History
                historyList={state.historyList}
              />
            </div>
          ),
        },
        {
          title: '缩略图',
          key: 'flowChart',
          component: () => state.flowData && <FlowChart flowData={state.flowData} />,
        }]}
      footer={() => (
        <Button
          onClick={handleClose}
          color={'blue' as ButtonColor}
          funcType={'raised' as FuncType}
        >
          关闭
        </Button>
      )}
    />
  );
};

const openParticipatedModal = (props: ParticipatedModalProps) => Modal.open({
  key: 'ParticipatedModal',
  title: '流程明细',
  drawer: true,
  children: <ParticipatedModal {...props} />,
  className: prefix,
  style: {
    width: 740,
  },
  footer: null,
});
export default openParticipatedModal;
