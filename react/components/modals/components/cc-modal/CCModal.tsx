import React, { useCallback, useEffect, useMemo } from 'react';
import { observer } from 'mobx-react-lite';
import {
  DataSet, Modal, Form,
} from 'choerodon-ui/pro';
import { IModalProps } from '@choerodon/agile/lib/common/types';
import './CCModal.less';
import { approveApi } from '@/api';
import SelectEmployee from '@/components/select/select-employee';
import store from '../../store';

const prefix = 'c7n-backlogApprove-ccModal';

interface Props {
  modal?: IModalProps
}

const CCModal:React.FC<Props> = ({ modal }) => {
  const { process: { taskDetail } } = store;

  const ccDataSet = useMemo(() => new DataSet({
    fields: [{
      name: 'cc',
      label: '选择成员',
      required: true,
      multiple: true,
    }],
  }), []);

  const handleSubmit = useCallback(async () => {
    const validate = await ccDataSet.validate();
    console.log(ccDataSet?.current?.data);
    if (validate) {
      approveApi.carbonCopy(taskDetail.taskId, ccDataSet.current?.get('cc'));
      return true;
    }
    return false;
  }, [ccDataSet, taskDetail.taskId]);
  useEffect(() => {
    modal?.handleOk(handleSubmit);
  }, [handleSubmit, modal]);
  return (
    <div className={`${prefix}-container`}>
      <Form>
        <SelectEmployee dataSet={ccDataSet} name="cc" selfEmpNum={taskDetail.selfEmpNum} />
      </Form>
    </div>
  );
};

const ObserverCCModal = observer(CCModal);

const openCCModal = (props: Props) => {
  Modal.open({
    key: 'backlogApproveCCModal',
    title: '选择抄送人',
    className: prefix,
    style: {
      width: 520,
    },
    children: <ObserverCCModal {...props} />,
    cancelProps: {
      style: {
        color: '#000',
      },
    },
    border: false,
  });
};

export default openCCModal;
