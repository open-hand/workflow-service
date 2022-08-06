import React, { useCallback, useEffect, useMemo, } from 'react';
import { observer } from 'mobx-react-lite';
import { DataSet, Form, Modal, Select, TextArea } from 'choerodon-ui/pro';
import { IModalProps } from '@choerodon/agile/lib/common/types';
import { Choerodon } from '@choerodon/boot';
import './RebutModal.less';
import { approveApi } from '@/api';
import store from '../../store';
import { FieldType } from 'choerodon-ui/pro/lib/data-set/enum';

const prefix = 'c7n-backlogApprove-rebutModal';
const {Option} = Select

interface IRebutNode {
  approveComment: string,
  code: string,
  name: string,
  remark: string,
  sumPassParallelGateway: number,
  topParallelGatewayCode?: string,
  traverseMethod: string,
  type: string,
}

interface Props {
  modal?: IModalProps
  rebutNodeList: IRebutNode[]
  onClose: () => void
  taskId: string
}

const RebutModal:React.FC<Props> = ({
  rebutNodeList, modal, onClose, taskId,
}) => {
  const { process: { taskDetail } } = store;
  const rebutDataSet = useMemo(() => new DataSet({
    fields: [{
      name: 'rebutNode',
      type: FieldType.object,
      // textField: 'name',
      // valueField: 'code',
      label: '驳回节点',
      required: true,
    }, {
      name: 'approveComment',
      type: FieldType.string,
      label: '驳回原因',
      required: true,
    }],
    data: [{}]
  }), []);
  const handleSubmit = useCallback(async () => {
    rebutDataSet.current?.set('__dirty', true);
    const validate = await rebutDataSet.validate();
    if (validate) {
      try {
        const selectedRebutNode: IRebutNode = rebutDataSet.current?.toData()?.rebutNode?.value;
        const approveComment: string = rebutDataSet.current?.toData()?.approveComment;
        await approveApi.rebut(taskId, {...selectedRebutNode, approveComment});
        onClose();
        return true;
      } catch (e) {
        return false;
      }
    }
    Choerodon.prompt('请完成驳回信息填写！');
    return false;
  }, [rebutDataSet, onClose, taskId]);

  useEffect(() => {
    modal?.handleOk(handleSubmit);
  }, [handleSubmit, modal]);
  return (
    <div className={`${prefix}-container`}>
      <Form dataSet={rebutDataSet}>
        <Select name="rebutNode">
          {rebutNodeList.map((rebutNode) => (
            <Option value={rebutNode}>{rebutNode.name}</Option>
          ))}
        </Select>
        <TextArea name="approveComment" />
      </Form>
    </div>
  );
};

const ObserverRebutModal = observer(RebutModal);

const OpenRebutModal = (props: Props) => {
  Modal.open({
    key: 'rebutModal',
    title: '选择驳回节点',
    className: prefix,
    style: {
      width: 520,
    },
    children: <ObserverRebutModal {...props} />,

    border: false,
  });
};

export default OpenRebutModal;
