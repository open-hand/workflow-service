import React, { useCallback, useEffect, useMemo } from 'react';
import { observer } from 'mobx-react-lite';
import {
  DataSet, Modal, Form,
} from 'choerodon-ui/pro';
import { IModalProps } from '@choerodon/agile/lib/common/types';
import './DelegateModal.less';
import { approveApi } from '@/api';
import SelectEmployee from '@/components/select/select-employee';
import store from '../../store';

const prefix = 'c7n-backlogApprove-delegateModal';

interface Props {
  modal?: IModalProps
  onClose: () => void
}

const DelegateModal:React.FC<Props> = ({ modal, onClose }) => {
  const { process: { taskDetail } } = store;

  const delegateDataSet = useMemo(() => new DataSet({
    fields: [{
      name: 'delegate',
      label: '选择成员',
      required: true,
    }],
  }), []);

  const handleSubmit = useCallback(async () => {
    const validate = await delegateDataSet.validate();
    if (validate) {
      await approveApi.delegateTo(taskDetail.taskId, delegateDataSet.current?.get('delegate'));
      onClose();
      return true;
    }
    return false;
  }, [delegateDataSet, onClose, taskDetail.taskId]);
  useEffect(() => {
    modal?.handleOk(handleSubmit);
  }, [handleSubmit, modal]);
  return (
    <div className={`${prefix}-container`}>
      <Form>
        <SelectEmployee dataSet={delegateDataSet} name="delegate" selfEmpNum={taskDetail.selfEmpNum} />
      </Form>
    </div>
  );
};

const ObserverDelegateModal = observer(DelegateModal);

const openDelegateModal = (props: Props) => {
  Modal.open({
    key: 'delegateModal',
    title: '选择转交人',
    className: prefix,
    style: {
      width: 520,
    },
    children: <ObserverDelegateModal {...props} />,
    cancelProps: {
      style: {
        color: '#000',
      },
    },
    border: false,
  });
};

export default openDelegateModal;
