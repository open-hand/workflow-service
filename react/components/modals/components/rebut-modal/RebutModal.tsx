import React, {
  useCallback, useEffect, useMemo, useState,
} from 'react';
import { observer } from 'mobx-react-lite';
import {
  DataSet, Modal, Form, CheckBox,
} from 'choerodon-ui/pro';
import { IModalProps } from '@choerodon/agile/lib/common/types';
import { Choerodon } from '@choerodon/boot';
import './RebutModal.less';
import { approveApi } from '@/api';
import store from '../../store';

const prefix = 'c7n-backlogApprove-rebutModal';

interface Props {
  modal?: IModalProps
  rebutNodeList: {
    startNode: {
      nodeId: string
    }
    previousNode: {
      nodeId: string
    }
  }
  onClose: () => void
  taskId: string
}

const RebutModal:React.FC<Props> = ({
  rebutNodeList, modal, onClose, taskId,
}) => {
  const { process: { taskDetail } } = store;
  const [checkedValue, setCheckedValue] = useState<'startNode' | 'preNode' | undefined>();
  const handleSubmit = useCallback(async () => {
    if (checkedValue) {
      approveApi.rebut(taskId, checkedValue === 'startNode' ? rebutNodeList.startNode.nodeId : rebutNodeList.previousNode.nodeId);
      onClose();
      return true;
    }
    Choerodon.prompt('请选择驳回节点！');
    return false;
  }, [checkedValue, onClose, rebutNodeList.previousNode.nodeId, rebutNodeList.startNode.nodeId, taskId]);

  const handleStartNodeChange = useCallback((value, oldValue) => {
    if (value) {
      setCheckedValue(value);
    } else if (oldValue === 'startNode' && rebutNodeList.previousNode) {
      setCheckedValue('preNode');
    } else if (oldValue === 'preNode' && rebutNodeList.startNode) {
      setCheckedValue('startNode');
    }
  }, [rebutNodeList.previousNode, rebutNodeList.startNode]);
  useEffect(() => {
    modal?.handleOk(handleSubmit);
  }, [handleSubmit, modal]);
  return (
    <div className={`${prefix}-container`}>
      {
        rebutNodeList.startNode && (
        <CheckBox
          name="rebutNode"
          value="startNode"
          checked={checkedValue === 'startNode'}
          onChange={handleStartNodeChange}
        >
          开始节点
        </CheckBox>
        )
      }

      {
        rebutNodeList.previousNode && (
        <CheckBox
          style={{
            marginLeft: 30,
          }}
          name="rebutNode"
          value="preNode"
          checked={checkedValue === 'preNode'}
          onChange={handleStartNodeChange}
        >
          上一节点
        </CheckBox>
        )
      }
    </div>
  );
};

const ObserverRebutModal = observer(RebutModal);

const OpenRebutModal = (props: Props) => {
  Modal.open({
    key: 'rebutModal',
    title: '选择驳回节点',
    className: prefix,
    style: {
      width: 520,
    },
    children: <ObserverRebutModal {...props} />,
    cancelProps: {
      style: {
        color: '#000',
      },
    },
    border: false,
  });
};

export default OpenRebutModal;
