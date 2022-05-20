import React, { useEffect, useCallback, useMemo } from 'react';
import {
  DataSet, Modal, Form, Select, TextArea, SelectBox,
} from 'choerodon-ui/pro';
import { forEach } from 'lodash';
import { observer } from 'mobx-react-lite';
import SelectEmployee from '@/components/select/select-employee';
import store from '../../store';
import './index.less';

const prefix = 'c7n-backlogApprove-addSignModal';

const AddApproverModal = (props) => {
  const { process: { taskDetail } } = store;
  const { modal, onSuccess, taskId } = props;
  const { tenantId: organizationId } = taskDetail;
  const dataSet = useMemo(() => new DataSet({
    selection: false,
    pageSize: 10,
    transport: {
      submit: () => {
        const url = `/cwkf/choerodon/v1/organizations/${organizationId}/organization_invoke_workflow/personal_process/${taskId}/executeTaskById`;
        return {
          url,
          data: dataSet.current.toData(),
          method: 'POST',
          params: { approveAction: 'ADD_SIGN' },
        };
      },
    },
    fields: [
      {
        name: 'addSignType',
        label: '加签类型',
        type: 'string',
        required: true,
        lookupCode: 'HWKF.ADD_SIGN_TYPE',
      },
      {
        name: 'addSignPerson',
        label: '加签人',
        type: 'object',
        // lovCode: 'HWKF.RULE.SELECT_USER',
        // lovPara: { enabledFlag: 1, tenantId: organizationId, selfEmpNum },
        multiple: true,
        required: true,
        textField: 'realName',
        valueField: 'id',
        ignore: 'always',
      },
      {
        name: 'id',
        type: 'string',
        bind: 'addSignPerson.id',
      },
      {
        name: 'realName',
        type: 'string',
        bind: 'addSignPerson.realName',
      },
      {
        name: 'remark',
        label: '加签备注',
        type: 'string',
      },
      {
        name: 'currentAction',
        label: '当前任务',
        type: 'string',
        dynamicProps: {
          required: ({ record }) => {
            const valueType = record.get('addSignType');
            if (valueType === 'AND_SIGN') {
              return true;
            }
            return false;
          },
        },
      },
      {
        name: 'approveComment',
        label: '处理意见',
        type: 'string',
        maxLength: 240,
      },
      {
        name: 'attachmentUrl',
        label: '附件',
        type: 'string',
      },
    ],
    events: {
      update: ({ record, name, value }) => {
        if (name === 'addSignType' && value === 'BEFORE_SIGN') {
          record.set('currentAction', undefined);
        }
        if (name === 'addSignType' && value === 'AFTER_SIGN') {
          record.set('currentAction', 'APPROVED');
        }

        if (name === 'addSignPerson' && value) {
          const toPersonList = [];
          forEach(value || [], (item) => {
            toPersonList.push({ value: item.id, name: item.realName, loginName: item.loginName });
          });
          record.set('toPersonList', toPersonList);
        }
      },
    },
  }), [organizationId, taskId]);
  React.useEffect(() => {
    dataSet.create({});
  }, [dataSet]);
  const handleSubmit = useCallback(async () => {
    try {
      const res = await dataSet.submit();
      if (res) {
        onSuccess(); // 加签成功后表示这条待办在这个节点处理完了，需要跳转到列表页
        return true;
      }
    } catch (e) {
      return false;
    }
  }, [dataSet, onSuccess]);
  useEffect(() => {
    modal?.handleOk(handleSubmit);
  }, [handleSubmit, modal]);

  return (
    <Form dataSet={dataSet}>
      <Select name="addSignType" />
      <SelectEmployee name="addSignPerson" selfEmpNum={taskDetail.selfEmpNum} />
      <TextArea name="remark" />
      {dataSet.current && dataSet.current.get('addSignType') !== 'BEFORE_SIGN' && (
        <SelectBox
          name="currentAction"
          disabled={dataSet.current.get('addSignType') === 'AFTER_SIGN'}
        >
          <SelectBox.Option value="APPROVED">
            审批同意
          </SelectBox.Option>
          <SelectBox.Option value="NONE">暂不处理</SelectBox.Option>
        </SelectBox>
      )}
      {dataSet.current && dataSet.current.get('currentAction') === 'APPROVED' && (
        <TextArea name="approveComment" />
      )}
    </Form>
  );
};
const ObserverAddApproverModal = observer(AddApproverModal);
const openAddApproverModal = (props) => {
  Modal.open({
    className: prefix,
    key: 'AddApproverModal',
    title: '加签',
    children: <ObserverAddApproverModal {...props} />,
    border: false,
    bodyStyle: { maxHeight: '60vh' },
  });
};
export default openAddApproverModal;
