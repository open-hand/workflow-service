import { DataSet, Form, Output } from 'choerodon-ui/pro';
import React, { useMemo } from 'react';
import { observer } from 'mobx-react-lite';
import { LabelLayout } from 'choerodon-ui/pro/lib/form/enum';
import { LabelAlignType } from 'choerodon-ui/pro/lib/form/Form';
import Part from '@/components/section';
import { InstanceDetail } from '@/api';
import styles from './index.less';

export interface DetailProps {
  data: Partial<InstanceDetail>
}
const Detail: React.FC<DetailProps> = ({ data }) => {
  const dataSet = useMemo(() => new DataSet({
    data: [data],
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
      label: '开始时间',
    }, {
      name: 'instanceStatusMeaning',
      label: '流程状态',
    }],
  }), [data]);
  return (
    <Part
      title="流程信息"
      style={{ padding: '0 20px' }}
      border
    >
      <Form
        style={{ marginLeft: -5 }}
        className={styles.form}
        dataSet={dataSet}
        labelLayout={'horizontal' as LabelLayout}
        labelWidth={102}
        useColon
        labelAlign={'left' as LabelAlignType}
      >
        <Output name="flowName" />
        <Output name="description" />
        <Output name="businessKey" />
        <Output name="starter" />
        <Output name="startDate" />
        <Output name="instanceStatusMeaning" />
      </Form>
    </Part>
  );
};

export default observer(Detail);
