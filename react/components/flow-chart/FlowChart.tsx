// @ts-nocheck
import React from 'react';
import { FlowChart as Chart } from './flow-chart';

export interface FlowChartProps{
  flowData:any
}
const FlowChart: React.FC<FlowChartProps> = ({ flowData }) => (
  <Chart
    flowData={flowData}
    language="zh_CN"
  />
);

export default FlowChart;
