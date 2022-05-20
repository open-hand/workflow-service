import { DataSet, Form, Output } from 'choerodon-ui/pro';
import React, { useMemo } from 'react';
import { observer } from 'mobx-react-lite';
import { LabelLayout } from 'choerodon-ui/pro/lib/form/enum';
import { LabelAlignType } from 'choerodon-ui/pro/lib/form/Form';
import Part from '@/components/section';
import store from '../../store';
import styles from './index.less';

const ProcessDetail:React.FC = () => {
  const { taskDetail } = store.process;
  const dataSet = useMemo(() => new DataSet({
    data: [taskDetail],
    fields: [{
      name: 'flowName',
      label: '流程名称',
    }, {
      name: 'description',
      label: '流程描述',
    }, {
      name: 'businessKey',
      label: '业务主键',
    }, {
      name: 'starter',
      label: '申请人',
    }, {
      name: 'startDate',
      label: '任务开始时间',
    }, {
      name: 'taskTypeMeaning',
      label: '任务类型',
    }],
  }), [taskDetail]);
  return (
    <Part
      title="流程信息"
      border
    >
      <Form
        style={{ marginLeft: -5 }}
        className={styles.form}
        dataSet={dataSet}
        labelLayout={'horizontal' as LabelLayout}
        labelWidth={102}
        labelAlign={'left' as LabelAlignType}
      >
        <Output name="flowName" />
        <Output name="description" />
        <Output name="businessKey" />
        <Output name="starter" />
        <Output name="startDate" />
        <Output name="taskTypeMeaning" />
      </Form>
    </Part>
  );
};

export default observer(ProcessDetail);
