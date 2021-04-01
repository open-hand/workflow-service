import React, {
  useCallback, useEffect, useMemo, useRef,
} from 'react';
import { observer } from 'mobx-react-lite';
import {
  DataSet, Modal, Form, Select, TextArea, CheckBox,
} from 'choerodon-ui/pro';
import { IModalProps } from '@choerodon/agile/lib/common/types';
import './AddApproveModal.less';
import { AddApproveData, approveApi, approveApiConfig } from '@/api';
import SelectEmployee, { IEmployee } from '@/components/select/select-employee';
import { FieldType } from 'choerodon-ui/pro/lib/data-set/enum';
import { includes } from 'lodash';
import store from '../../store';

const prefix = 'c7n-backlogApprove-addApproveModal';

interface Props {
  modal?: IModalProps
  onClose: () => void
}

const AddApproveModal:React.FC<Props> = ({ modal, onClose }) => {
  const employeesRef = useRef<IEmployee[]>();

  const { process: { taskDetail } } = store;

  const approveTypeDataSet = useMemo(() => new DataSet({
    autoQuery: true,
    fields: [{
      name: 'value',
      type: 'string' as FieldType,
    }, {
      name: 'meaning',
      type: 'string' as FieldType,
    }],
    transport: {
      read: approveApiConfig.getAddApproveType(),
    },
  }), []);
  const addApproveDataSet = useMemo(() => new DataSet({
    autoCreate: true,
    fields: [{
      name: 'type',
      label: '加审类型',
      required: true,
      options: approveTypeDataSet,
      textField: 'meaning',
      valueField: 'value',
    }, {
      name: 'approver',
      label: '选择审批人',
      required: true,
      multiple: true,
      textField: 'employeeName',
      valueField: 'employeeNum',
    }, {
      name: 'remark',
      label: '加审备注',
    }, {
      name: 'currentTask',
      label: '当前任务',
      dynamicProps: {
        required: ({ record }) => record.get('type') === 'AFTER_ADD_TASK_APPROVER',
      },
      required: true,
      disabled: true,
      defaultValue: 'approved',
    }, {
      name: 'suggestion',
      label: '处理意见',
    }],
    events: {
      update: ({
      // @ts-ignore
        name, value, oldValue, record,
      }) => {
        if (name === 'type') {
          if (record.get('suggestion') && value === 'BEFORE_ADD_TASK_APPROVER') {
            record.set('suggestion', undefined);
          }
        }
      },
    },
  }), [approveTypeDataSet]);

  const handleSubmit = useCallback(async () => {
    const validate = await addApproveDataSet.validate();
    if (validate) {
      const addApproverPerson = (employeesRef?.current || []).filter((item) => includes((addApproveDataSet?.current?.get('approver') || []), item.employeeNum));
      const data: AddApproveData = {
        addApproverPerson,
        addApproverType: addApproveDataSet?.current?.get('type'),
        remark: addApproveDataSet?.current?.get('remark'),
        toPersonList: addApproverPerson.map((item) => ({
          name: item.employeeName,
          value: item.employeeNum,
        })),
        approveComment: addApproveDataSet?.current?.get('suggestion'),
        currentAction: addApproveDataSet?.current?.get('type') === 'AFTER_ADD_TASK_APPROVER' ? 'APPROVED' : undefined,
      };
      await approveApi.addApprove(taskDetail.taskId, data);
      onClose();
      return true;
    }
    return false;
  }, [addApproveDataSet, onClose, taskDetail.taskId]);
  useEffect(() => {
    modal?.handleOk(handleSubmit);
  }, [handleSubmit, modal]);

  const isAfterApprove = addApproveDataSet?.current?.get('type') === 'AFTER_ADD_TASK_APPROVER';
  return (
    <div className={`${prefix}-container`}>
      <Form dataSet={addApproveDataSet}>
        <Select name="type" />
        <SelectEmployee
          name="approver"
          selfEmpNum={taskDetail.selfEmpNum}
          dataRef={employeesRef}
        />
        <TextArea name="remark" />
        {
           isAfterApprove && (
           <div className={`${prefix}-container-currentTask-field`}>
             <span className={`${prefix}-container-currentTask-field-title`}>当前任务</span>
             <CheckBox name="currentTask" checked>审批同意</CheckBox>
             <CheckBox name="currentTask">暂不处理</CheckBox>
           </div>
           )
         }
        {
           isAfterApprove && (
           <TextArea name="suggestion" />
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
    cancelProps: {
      style: {
        color: '#000',
      },
    },
    border: false,
  });
};

export default openAddApproveModal;
