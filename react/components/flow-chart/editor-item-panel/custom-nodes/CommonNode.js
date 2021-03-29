/**
 * CommonNode 普通节点（审批链预览）
 * @author zhuyan.luo@hand-china.com
 * @date 2020/1/21
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import React from 'react';
import { RegisterNode } from 'gg-editor2';

import { optimizeMultilineText } from '../../utils';

const CommonNode = () => {
  const nodeConfig = {
    draw(item) {
      const group = item.getGraphicGroup();
      const { color, label } = item.getModel();
      const shape = group.addShape('rect', {
        attrs: {
          x: -60,
          y: -20,
          width: 120,
          height: 42,
          fill: color || '#fff',
          stroke: 'black',
          radius: 2,
        },
      });
      // 绘制标签（若标签在最前面绘制，会被遮挡）
      group.addShape('text', {
        attrs: {
          x: -50,
          y: 2,
          fontSize: 12,
          textAlign: 'left',
          fill: 'rgba(0,0,0,0.65)',
          text: optimizeMultilineText(label, 50, 2, 90),
          textBaseline: 'middle',
        },
      });

      return shape;
    },
    afterDraw() {},
    anchor: [],
  };

  return <RegisterNode name="commonNode" config={nodeConfig} extend="flow-rect" />;
};

export default CommonNode;
