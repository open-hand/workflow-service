import React, { useCallback, useEffect, useMemo, useRef, } from 'react';
import { observer } from 'mobx-react-lite';
import { CheckBox, DataSet, Form, Modal, Select, TextArea, TextField, } from 'choerodon-ui/pro';
import { IModalProps } from '@choerodon/agile/lib/common/types';
import './NextApproveModal.less';
import { approveApi, approveApiConfig, INextNodeApprover, IWorkflowUser, NextNodeApproveData, } from '@/api';
import SelectWorkflowUser from '@/components/select/select-employee';
import { FieldIgnore, FieldType } from 'choerodon-ui/pro/lib/data-set/enum';
import { includes } from 'lodash';
import store from '../../store';
import { Choerodon } from "@choerodon/boot";

const { Option } = Select;
const prefix = 'c7n-backlogApprove-nextApproveModal';
interface IForecastNextNode {
  nextNodeCode: string,
  nextNodeName: string,
  nextNodeApprover: INextNodeApprover[]
}
interface Props {
  modal?: IModalProps
  forecastNextNode: IForecastNextNode
  onClose: () => void
  taskId: string
}

const NextApproveModal:React.FC<Props> = ({
  modal, forecastNextNode, onClose, taskId,
}) => {
  const { process: { taskDetail } } = store;
  const workflowUserRef = useRef<IWorkflowUser[]>()
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
      name: 'toPersonList',
      label: '选择审批人',
      multiple: true,
      dynamicProps: {
        required: ({ record }) => record?.get('source') === 'ANY_APPROVER',
      },
    },
    {
      name: 'nodeDefaultApprover',
      label: '节点默认审批人',
      dynamicProps: {
        required: ({ record }) => record?.get('source') === 'DEFAULT_APPROVER',
      },
      multiple: true,
      ignore: FieldIgnore.always,
    },
    {
      name: 'currentTask',
      label: '当前任务',
      required: true,
      disabled: true,
      defaultValue: true,
    },
    {
      name: 'approveComment',
      label: '处理意见',
      maxLength: 240,
    }],
    data: [{
      nextNode: forecastNextNode?.nextNodeName,
      currentTask: true
    }],
    events: {
      update: ({
        // @ts-ignore
        record, name, value, oldValue,
      }) => {
        if (name === 'source' && oldValue) {
          if (value === 'DEFAULT_APPROVER') {
            record.set('toPersonList', undefined);
          } else if (value === 'ANY_APPROVER') {
            record.set('nodeDefaultApprover', undefined);
          } else {
            record.set('toPersonList', undefined);
            record.set('nodeDefaultApprover', undefined);
          }
        }
      },
    },
  }), [forecastNextNode.nextNodeName, sourceDataSet]);

  const handleSubmit = useCallback(async () => {
    nextApproveDataSet.current?.set('__dirty', true);
    nextApproveDataSet.current?.set('toPersonList', workflowUserRef.current)
    const validate = await nextApproveDataSet.validate();
    const source = nextApproveDataSet?.current?.get('source');
    if (validate) {
      const data: NextNodeApproveData = {
        nextNodeCode: forecastNextNode.nextNodeCode,
        approverSourceType: source,
        toPersonList: source === 'DEFAULT_APPROVER' ?
            (forecastNextNode.nextNodeApprover || []).filter((item) => includes(nextApproveDataSet?.current?.get('nodeDefaultApprover') || [], item.code))
            : nextApproveDataSet?.current?.get('toPersonList'),
        approveComment: nextApproveDataSet?.current?.get('approveComment'),
      };

      await approveApi.nextNodeApprove(taskId, data);
      onClose();
      return true;
    }
    Choerodon.prompt('请完成信息填写！');
    return false;
  }, [forecastNextNode.nextNodeApprover, forecastNextNode.nextNodeCode, nextApproveDataSet, onClose, taskId]);
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
            <SelectWorkflowUser
              name="toPersonList"
              selfUserId={taskDetail.selfUserId}
              dataRef={workflowUserRef}
              multiple={true}
              label={'审批人'}
            />
          )
        }
        {
          source === 'DEFAULT_APPROVER' && (
            <Select name="nodeDefaultApprover">
              {
                (forecastNextNode?.nextNodeApprover || []).map((item) => (
                  <Option key={item.code} value={item.code}>{item.name}</Option>
                ))
              }
            </Select>
          )
        }
        <CheckBox name="currentTask">审批同意</CheckBox>
        <TextArea name="approveComment" />
      </Form>
    </div>
  );
};

const ObserverNextApproveModal = observer(NextApproveModal);

const openNextApproveModal = (props: Props) => {
  Modal.open({
    key: 'nextApproveModal',
    title: '指定下一审批人',
    className: prefix,
    style: {
      width: 520,
    },
    children: <ObserverNextApproveModal {...props} />,

    border: false,
  });
};

export default openNextApproveModal;
