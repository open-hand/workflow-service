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
import Comment from './comment';

const prefix = 'c7n-backlogApprove-modal';
export interface CarbonCopiedModalProps {
  carbonCopyTodoFlag: 0 | 1
  taskHistoryId: string
  modal?: IModalProps,
  taskId: string
  instanceId: string
  onClose: () => void
  SummaryComponent?: React.ReactNode
  extraTabs?: BaseModalProps['tabs']
}

const CarbonCopiedModal: React.FC<CarbonCopiedModalProps> = (props) => {
  const {
    modal, SummaryComponent, extraTabs, taskId, instanceId, carbonCopyTodoFlag, onClose, taskHistoryId,
  } = props;
  const [state, setState] = useState<{
    loading: boolean
    data: {
      carbonCopyDetail: Partial<InstanceDetail>
    },
    historyList: ProcessHistory[]
    flowData: IFlowData | null
  }>({
    loading: false,
    historyList: [],
    data: {
      carbonCopyDetail: {},
    },
    flowData: null,
  });
  const refresh = useCallback(async () => {
    const data = await approveApi.getCarbonCopied(taskHistoryId, carbonCopyTodoFlag);
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
  }, [carbonCopyTodoFlag, instanceId, taskHistoryId]);
  useEffect(() => {
    refresh();
  }, [refresh]);
  const handleClose = useCallback(() => {
    modal?.close();
    onClose();
  }, [modal, onClose]);
  const handleComment = useCallback(() => {
    handleClose();
  }, [handleClose]);
  return (
    <BaseModal
      loading={state.loading}
      tabTop={SummaryComponent}
      tabs={[...(extraTabs ?? []),
        {
          title: carbonCopyTodoFlag ? '审核意见' : '流程信息',
          key: 'suggest',
          component: () => (
            <>
              {state.data.carbonCopyDetail ? <ProcessDetail data={state.data.carbonCopyDetail} /> : null}
              {carbonCopyTodoFlag && taskHistoryId ? <Comment onSubmit={handleComment} taskHistoryId={taskHistoryId} /> : null}
            </>
          ),
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

const openCarbonCopiedModal = (props: CarbonCopiedModalProps) => Modal.open({
  key: 'CarbonCopiedModal',
  title: '流程明细',
  drawer: true,
  children: <CarbonCopiedModal {...props} />,
  className: prefix,
  style: {
    width: 740,
  },
  footer: null,
});
export default openCarbonCopiedModal;
