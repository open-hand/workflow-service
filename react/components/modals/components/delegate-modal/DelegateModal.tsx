import React, { useCallback, useEffect, useMemo, useRef } from 'react';
import { observer } from 'mobx-react-lite';
import { DataSet, Form, Modal, Select, } from 'choerodon-ui/pro';
import { IModalProps } from '@choerodon/agile/lib/common/types';
import './DelegateModal.less';
import { approveApi, IWorkflowUser } from '@/api';
import SelectWorkflowUser from '@/components/select/select-employee';
import store from '../../store';
import { FieldType } from 'choerodon-ui/pro/lib/data-set/enum';
import { Choerodon } from "@choerodon/boot";

const {Option} = Select

const prefix = 'c7n-backlogApprove-delegateModal';

interface Props {
  modal?: IModalProps
  onClose: () => void
  taskId: string
}

const DelegateModal:React.FC<Props> = ({ modal, onClose, taskId }) => {
  const { process: { taskDetail } } = store;
  const workflowUserRef = useRef<IWorkflowUser>()

  const delegateDataSet = useMemo(() => new DataSet({
    fields: [{
      name: 'toPerson',
      type: FieldType.object,
      textField: 'realName',
      valueField: 'id',
      label: '转交人',
      required: true,
      multiple: false,
    }],
    data: [{}]
  }), []);

  const handleSubmit = useCallback(async () => {
    delegateDataSet.current?.set('__dirty', true);
    delegateDataSet.current?.set('toPerson', workflowUserRef.current)
    const validate = await delegateDataSet.validate();
    if (validate) {
      await approveApi.delegateTo(taskId, delegateDataSet.current?.get('toPerson'));
      onClose();
      return true;
    }
    Choerodon.prompt('请完成转交信息填写！');
    return false;
  }, [delegateDataSet, onClose, taskId]);
  useEffect(() => {
    modal?.handleOk(handleSubmit);
  }, [handleSubmit, modal]);
  return (
    <div className={`${prefix}-container`}>
      <Form dataSet={delegateDataSet}>
        <SelectWorkflowUser
          name="toPerson"
          selfUserId={taskDetail.selfUserId}
          dataRef={workflowUserRef}
          multiple={false}
          label={'转交人'}
        />
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

    border: false,
  });
};

export default openDelegateModal;
