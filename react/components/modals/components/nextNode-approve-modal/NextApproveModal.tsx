import React, {
  useCallback, useEffect, useMemo, useRef,
} from 'react';
import { observer } from 'mobx-react-lite';
import {
  DataSet, Modal, Form, TextField, Select, CheckBox, TextArea,
} from 'choerodon-ui/pro';
import { IModalProps } from '@choerodon/agile/lib/common/types';
import './NextApproveModal.less';
import { approveApi, approveApiConfig, NextNodeApproveData } from '@/api';
import SelectEmployee, { IEmployee } from '@/components/select/select-employee';
import { FieldType } from 'choerodon-ui/pro/lib/data-set/enum';
import { includes } from 'lodash';
import store from '../../store';

const { Option } = Select;
const prefix = 'c7n-backlogApprove-nextApproveModal';

interface INextNodeApprover {
  employeeNum: string,
  employeeCode: string,
  value: string,
  name: string,
  employeeName: string,
}
interface IForecastNextNode {
  nextNodeCode: string,
  nextNodeName: string,
  nextNodeApprover: INextNodeApprover[]
}
interface Props {
  modal?: IModalProps
  forecastNextNode: IForecastNextNode
  onClose: () => void
}

const NextApproveModal:React.FC<Props> = ({
  modal, forecastNextNode, onClose,
}) => {
  const employeesRef = useRef<IEmployee[]>();
  const { process: { taskDetail } } = store;

  const sourceDataSet = useMemo(() => new DataSet({
    autoQuery: true,
    fields: [{
      name: 'meaning',
      type: 'string' as FieldType,
    }, {
      name: 'value',
      type: 'string' as FieldType,
    }],
    transport: {
      read: approveApiConfig.getSourceType(),
    },
  }), []);

  const nextApproveDataSet = useMemo(() => new DataSet({
    autoCreate: true,
    fields: [{
      name: 'nextNode',
      label: '下一节点',
      required: true,
      disabled: true,
      defaultValue: forecastNextNode.nextNodeName,
    }, {
      name: 'source',
      label: '审批人来源',
      required: true,
      textField: 'meaning',
      valueField: 'value',
      options: sourceDataSet,
    }, {
      name: 'approver',
      label: '选择审批人',
      multiple: true,
      dynamicProps: {
        required: ({ record }) => record?.get('source') === 'ANY_APPROVER',
      },
    },
    {
      name: 'defaultApprover',
      label: '默认审批人',
      dynamicProps: {
        required: ({ record }) => record?.get('source') === 'DEFAULT_APPROVER',
      },
      multiple: true,
    },
    {
      name: 'currentTask',
      label: '当前任务',
      required: true,
      disabled: true,
      defaultValue: true,
    },
    {
      name: 'comment',
      label: '处理意见',
      maxLength: 240,
    }],
    events: {
      update: ({
        // @ts-ignore
        record, name, value, oldValue,
      }) => {
        if (name === 'source' && oldValue) {
          if (value === 'DEFAULT_APPROVER') {
            record.set('approver', undefined);
          } else if (value === 'ANY_APPROVER') {
            record.set('defaultApprover', undefined);
          } else {
            record.set('approver', undefined);
            record.set('defaultApprover', undefined);
          }
        }
      },
    },
  }), [forecastNextNode.nextNodeName, sourceDataSet]);

  const handleSubmit = useCallback(async () => {
    const validate = await nextApproveDataSet.validate();
    const source = nextApproveDataSet?.current?.get('source');
    if (validate) {
      const data: NextNodeApproveData = {
        nextNodeCode: forecastNextNode.nextNodeCode,
        approverSourceType: source,
        toPersonList: source === 'DEFAULT_APPROVER' ? (forecastNextNode.nextNodeApprover || []).filter((item) => includes(nextApproveDataSet?.current?.get('defaultApprover') || [], item.value)).map(((item) => ({
          value: item.value,
          name: item.employeeName,
        }))) : (employeesRef.current || []).filter((item) => includes(nextApproveDataSet?.current?.get('approver') || [], item.employeeNum)).map((item) => ({
          value: item.employeeNum,
          name: item.employeeName,
        })),
        approveComment: nextApproveDataSet?.current?.get('comment'),
      };

      await approveApi.nextNodeApprove(taskDetail.taskId, data);
      onClose();
      return true;
    }
    return false;
  }, [forecastNextNode.nextNodeApprover, forecastNextNode.nextNodeCode, nextApproveDataSet, onClose, taskDetail.taskId]);
  useEffect(() => {
    modal?.handleOk(handleSubmit);
  }, [handleSubmit, modal]);

  const source = nextApproveDataSet?.current?.get('source');
  return (
    <div className={`${prefix}-container`}>
      <Form dataSet={nextApproveDataSet}>
        <TextField name="nextNode" />
        <Select name="source" />
        {
          source === 'ANY_APPROVER' && (
            <SelectEmployee
              name="approver"
              selfEmpNum={taskDetail.selfEmpNum}
              dataRef={employeesRef}
            />
          )
        }
        {
          source === 'DEFAULT_APPROVER' && (
            <Select name="defaultApprover">
              {
                (forecastNextNode?.nextNodeApprover || []).map((item) => (
                  <Option key={item.value} value={item.value}>{item.employeeName}</Option>
                ))
              }
            </Select>
          )
        }
        <CheckBox name="currentTask">审批同意</CheckBox>
        <TextArea name="comment" />
      </Form>
    </div>
  );
};

const ObserverNextApproveModal = observer(NextApproveModal);

const openNextApproveModal = (props: Props) => {
  Modal.open({
    key: 'nextApproveModal',
    title: '指定下一审核人',
    className: prefix,
    style: {
      width: 520,
    },
    children: <ObserverNextApproveModal {...props} />,
    cancelProps: {
      style: {
        color: '#000',
      },
    },
    border: false,
  });
};

export default openNextApproveModal;
