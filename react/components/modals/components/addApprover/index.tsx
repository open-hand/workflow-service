import React, { useCallback, useEffect, useMemo, useRef } from 'react';
import { DataSet, Form, Modal, Select, SelectBox, TextArea, } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import SelectWorkflowUser from '@/components/select/select-employee';
import store from '../../store';
import './index.less';
import { FieldType } from 'choerodon-ui/pro/lib/data-set/enum';
import { approveApi, IWorkflowUser } from "@/api";
import { Choerodon } from "@choerodon/boot";

const prefix = 'c7n-backlogApprove-addSignModal';

const AddApproverModal = (props: any) => {
  const { process: { taskDetail } } = store;
  const { modal, onSuccess, taskId } = props;
  const { tenantId: organizationId } = taskDetail;
  const workflowUserRef = useRef<IWorkflowUser[]>();
  const addApproverDataSet: DataSet = useMemo<DataSet>(() => new DataSet({
    fields: [
      {
        name: 'addSignType',
        label: '加签类型',
        type: FieldType.string,
        required: true,
        textField: 'meaning',
        valueField: 'value',
        lookupCode: 'HWKF.ADD_SIGN_TYPE',
      },
      {
        name: 'parallelFlag',
        label: '审批顺序',
        type: FieldType.number,
        defaultValue: 1,
      },
      {
        name: 'toPersonList',
        label: '加签人',
        type: FieldType.object,
        // lovCode: 'HWKF.RULE.SELECT_USER',
        // lovPara: { enabledFlag: 1, tenantId: organizationId, selfEmpNum },
        multiple: true,
        required: true,
        textField: 'realName',
        valueField: 'id',
      },
      {
        name: 'id',
        type: FieldType.string,
        bind: 'addSignPerson.id',
      },
      {
        name: 'realName',
        type: FieldType.string,
        bind: 'addSignPerson.realName',
      },
      {
        name: 'remark',
        label: '加签备注',
        type: FieldType.string,
      },
      {
        name: 'approveComment',
        label: '处理意见',
        type: FieldType.string,
        maxLength: 240,
      },
      {
        name: 'attachmentUrl',
        label: '附件',
        type: FieldType.string,
      },
    ],
    data: [{}]
  }), [organizationId, taskId]);
  React.useEffect(() => {
    addApproverDataSet.create({});
  }, [addApproverDataSet]);
  const handleSubmit = useCallback(async () => {
    addApproverDataSet.current?.set('__dirty', true);
    addApproverDataSet.current?.set('toPersonList', workflowUserRef.current)
    const validate = await addApproverDataSet.validate();
    if (validate) {
      await approveApi.addSign(taskId, addApproverDataSet.current?.toData());
      onSuccess();
      return true;
    }
    Choerodon.prompt('请完成加签信息填写！');
    return false;
  }, [addApproverDataSet, onSuccess, taskId]);
  useEffect(() => {
    modal?.handleOk(handleSubmit);
  }, [handleSubmit, modal]);

  return (
    <Form dataSet={addApproverDataSet}>
      <Select name="addSignType" />
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
        label={'加签人'}
      />
      <TextArea name="remark" />
      {addApproverDataSet.current && addApproverDataSet.current.get('addSignType') === 'AFTER_SIGN' && (
        <TextArea name="approveComment" />
      )}
    </Form>
  );
};
const ObserverAddApproverModal = observer(AddApproverModal);
const openAddApproverModal = (props: any) => {
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
