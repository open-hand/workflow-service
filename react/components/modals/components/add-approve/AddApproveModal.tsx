import React, { useCallback, useEffect, useMemo, useRef, } from 'react';
import { observer } from 'mobx-react-lite';
import { DataSet, Form, Modal, Select, SelectBox, TextArea, } from 'choerodon-ui/pro';
import { IModalProps } from '@choerodon/agile/lib/common/types';
import './AddApproveModal.less';
import { AddApproveData, approveApi, approveApiConfig, IWorkflowUser } from '@/api';
import SelectWorkflowUser from '@/components/select/select-employee';
import { FieldType } from 'choerodon-ui/pro/lib/data-set/enum';
import store from '../../store';
import { Choerodon } from "@choerodon/boot";

const prefix = 'c7n-backlogApprove-addApproveModal';

interface Props {
  modal?: IModalProps
  onClose: () => void
  taskId: string
}

const AddApproveModal:React.FC<Props> = ({ modal, onClose, taskId }) => {
  const employeesRef = useRef<IWorkflowUser[]>();

  const { process: { taskDetail } } = store;
  const workflowUserRef = useRef<IWorkflowUser[]>();

  const approveTypeDataSet = useMemo(() => new DataSet({
    autoQuery: true,
    fields: [{
      name: 'value',
      type: FieldType.string,
    }, {
      name: 'meaning',
      type: FieldType.string,
    }],
    transport: {
      read: approveApiConfig.getAddApproveType(),
    },
  }), []);
  const addApproveDataSet = useMemo(() => new DataSet({
    fields: [{
      name: 'addApproverType',
      label: '加审类型',
      required: true,
      options: approveTypeDataSet,
      textField: 'meaning',
      valueField: 'value',
    }, {
      name: 'parallelFlag',
      label: '审批顺序',
      type: FieldType.number,
      defaultValue: 1,
    }, {
      name: 'toPersonList',
      label: '加审人',
      required: true,
      multiple: true,
      textField: 'realName',
      valueField: 'id',
      type: FieldType.object,
    }, {
      name: 'remark',
      label: '加审备注',
    }, {
      name: 'approveComment',
      label: '处理意见',
    }],
    data: [{}],
    events: {
      update: ({
      // @ts-ignore
        name, value, oldValue, record,
      }) => {
        if (name === 'addApproverType') {
          if (value === 'BEFORE_ADD_TASK_APPROVER') {
            record.set('approveComment', undefined);
          }
        }
      },
    },
  }), [approveTypeDataSet]);

  const handleSubmit = useCallback(async () => {
    addApproveDataSet.current?.set('__dirty', true);
    addApproveDataSet.current?.set('toPersonList', workflowUserRef.current)
    const validate = await addApproveDataSet.validate();
    if (validate) {
      try {
        const data: AddApproveData = {
          addApproverType: addApproveDataSet?.current?.get('addApproverType'),
          approveComment: addApproveDataSet?.current?.get('approveComment'),
          currentAction: (addApproveDataSet?.current?.get('addApproverType') === 'AFTER_ADD_TASK_APPROVER') ? 'APPROVED' : undefined,
          parallelFlag: addApproveDataSet?.current?.get('parallelFlag'),
          remark: addApproveDataSet?.current?.get('remark'),
          toPersonList: addApproveDataSet.current?.get('toPersonList'),
        };
        await approveApi.addApprove(taskId, data);
        onClose();
        return true;
      } catch (e) {
        console.error(e);
        return false;
      }
    }
    Choerodon.prompt('请完成加审信息填写！');
    return false;
  }, [addApproveDataSet, onClose, taskId]);
  useEffect(() => {
    modal?.handleOk(handleSubmit);
  }, [handleSubmit, modal]);

  return (
    <div className={`${prefix}-container`}>
      <Form dataSet={addApproveDataSet}>
        <Select name="addApproverType" />
        <SelectBox
          name="parallelFlag"
        >
          <SelectBox.Option value={1}>
            同时审批
          </SelectBox.Option>
          <SelectBox.Option value={0}>
            指定顺序审批
          </SelectBox.Option>
        </SelectBox>
        <SelectWorkflowUser
          name="toPersonList"
          selfUserId={taskDetail.selfUserId}
          dataRef={workflowUserRef}
          multiple={true}
          label={'加审人'}
        />
        <TextArea name="remark" />
        {
          (addApproveDataSet?.current?.get('addApproverType') === 'AFTER_ADD_TASK_APPROVER') && (
           <TextArea name="approveComment" />
           )
         }
      </Form>
    </div>
  );
};

const ObserverAddApprovModal = observer(AddApproveModal);

const openAddApproveModal = (props: Props) => {
  Modal.open({
    key: 'addApproveModal',
    title: '加审',
    className: prefix,
    style: {
      width: 520,
    },
    children: <ObserverAddApprovModal {...props} />,
    border: false,
  });
};

export default openAddApproveModal;
