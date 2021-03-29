/**
 * FlowChart 只读流程图
 * @author zhuyan.luo@hand-china.com
 * @date 2020/8/18
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import React from 'react';
import GGEditor, { Flow } from 'gg-editor2';
import {
  StartNode,
  EndNode,
  UserNode,
  NoticeNode,
  ServiceNode,
  ExclusiveNode,
  MergeNode,
  ParallelNode,
} from '../editor-item-panel/custom-nodes';
import { FlowTooltip } from '../FlowTooltip';
import { setColorForItems } from '../utils';

const FlowChart = ({
  flowData, graphConfig, styleData, language,
}) => {
  const flowRef = React.useRef(null);

  /**
   * 构造流程图展示数据
   * @param {object} data - 待构造的流程图节点数据
   */
  const getFlowData = (data = {}) => {
    const { page } = flowRef.current;
    const { flowModel, processInstanceNodeHistory = [] } = data;
    const diagramData = JSON.parse(flowModel);
    const { displayJson } = diagramData;
    const { edgeList = [], nodeList = [], groups } = displayJson;
    const tempData = {
      edges: setColorForItems(edgeList, processInstanceNodeHistory, language),
      nodes: setColorForItems(nodeList, processInstanceNodeHistory, language),
      groups,
    };
    page.read(tempData);
    page.executeCommand('autoZoom'); // 自适应尺寸
    page.executeCommand('resetZoom'); // 实际尺寸
  };

  React.useEffect(() => {
    if (flowData) {
      getFlowData(flowData);
    }
  }, [flowData]);
  return (
    <GGEditor>
      <Flow
        ref={flowRef}
        style={{
          backgroundColor: '#F8F8F8', height: '90vh', width: '100%', ...styleData,
        }}
        graph={{
          mode: 'readOnly',
          edgeDefaultShape: 'flow-polyline-round',
          plugins: [FlowTooltip],
          ...graphConfig,
        }}
      />
      <StartNode />
      <EndNode />
      <UserNode />
      <NoticeNode />
      <ServiceNode />
      <ExclusiveNode />
      <MergeNode />
      <ParallelNode />
    </GGEditor>
  );
};

export default FlowChart;
