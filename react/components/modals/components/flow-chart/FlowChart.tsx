// @ts-nocheck
import React from 'react';
import { observer } from 'mobx-react-lite';
import Chart from '@/components/flow-chart';
import store from '../../store';

const prefix = 'c7n-backlogApprove-flowChart';

const FlowChart: React.FC = () => {
  const { flowData } = store;
  return (
    <div className={prefix}>
      <Chart
        flowData={flowData}
        language="zh_CN"
      />
    </div>
  );
};

export default observer(FlowChart);
