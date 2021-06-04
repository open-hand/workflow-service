/**
 * GatewayNode 网关节点（审批链预览的串行、并行网关节点）
 * @author zhuyan.luo@hand-china.com
 * @date 2020/1/21
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import React from 'react';
import { RegisterNode } from 'gg-editor2';

import { optimizeMultilineText } from '../../utils';

import parallelNodeIcon from '../assets/icons/parallel@2x.png';
import serialNodeIcon from '../assets/icons/serial@2x.svg';

class GatewayNode extends React.Component {
  render() {
    const config = {
      draw(item) {
        const group = item.getGraphicGroup();
        const { nodeType, label } = item.getModel();
        const iconFont = nodeType === 'PARALLEL_GATEWAY_NODE' ? parallelNodeIcon : serialNodeIcon;
        const gatewayColor = nodeType === 'PARALLEL_GATEWAY_NODE' ? '#e8b6d4' : '#e9fbf9';
        const shape = group.addShape('polygon', {
          attrs: {
            points: [
              [-60, 0],
              [0, 38],
              [60, 0],
              [0, -38],
            ],
            fill: gatewayColor,
            stroke: 'black',
          },
        });
        // 绘制图标
        group.addShape('image', {
          attrs: {
            x: -48,
            y: -7,
            width: 14,
            height: 14,
            img: iconFont,
          },
        });

        // 绘制标签（若标签在最前面绘制，会被遮挡）
        group.addShape('text', {
          attrs: {
            x: -30,
            y: 2,
            fontSize: 12,
            textAlign: 'left',
            fill: 'var(--text-color3)',
            text: optimizeMultilineText(label, 50, 3, 55),
            textBaseline: 'middle',
          },
        });
        return shape;
      },
      anchor: [
        [0.5, 0],
        [0.5, 1],
        [0, 0.5],
        [1, 0.5],
      ],
    };

    return <RegisterNode name="gatewayNode" config={config} extend="flow-rhombus" />;
  }
}

export default GatewayNode;
