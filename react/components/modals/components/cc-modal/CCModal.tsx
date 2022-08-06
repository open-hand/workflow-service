import React, { useCallback, useEffect, useMemo, useRef } from 'react';
import { observer } from 'mobx-react-lite';
import { DataSet, Form, Modal, TextArea } from 'choerodon-ui/pro';
import { IModalProps } from '@choerodon/agile/lib/common/types';
import './CCModal.less';
import { approveApi, IWorkflowUser } from '@/api';
import SelectWorkflowUser from '@/components/select/select-employee';
import { Choerodon } from '@choerodon/boot';
import store from '../../store';
import { FieldType } from 'choerodon-ui/pro/lib/data-set/enum';

const prefix = 'c7n-backlogApprove-ccModal';

interface Props {
  modal?: IModalProps
  taskId: string
}

const CCModal:React.FC<Props> = ({ modal, taskId }) => {
  const { process: { taskDetail } } = store;
  const workflowUserRef = useRef<IWorkflowUser>()

  const ccDataSet = useMemo(() => new DataSet({
    fields: [{
      name: 'toPersonList',
      label: '抄送人',
      required: true,
      multiple: true,
      type: FieldType.object,
    },
    {
      name: 'remark',
      label: '抄送备注',
    }],
    data: [{}],
  }), []);

  const handleSubmit = useCallback(async () => {
    ccDataSet.current?.set('__dirty', true);
    ccDataSet.current?.set('toPersonList', workflowUserRef.current)
    const validate = await ccDataSet.validate();
    if (validate) {
      await approveApi.carbonCopy(taskId, ccDataSet.current?.get('toPersonList'), ccDataSet.current?.get('remark'));
      Choerodon.prompt('抄送完成');
      return true;
    }
    Choerodon.prompt('请完成抄送信息填写！');
    return false;
  }, [ccDataSet, taskId]);
  useEffect(() => {
    modal?.handleOk(handleSubmit);
  }, [handleSubmit, modal]);
  return (
    <div className={`${prefix}-container`}>
      <Form dataSet={ccDataSet}>
        <SelectWorkflowUser
          name="toPersonList"
          selfUserId={taskDetail.selfUserId}
          dataRef={workflowUserRef}
          multiple={true}
          label={"抄送人"}
        />
        <TextArea name="remark" />
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
    border: false,
  });
};

export default openCCModal;
