/**
 * ChainMergeNode 收敛网关
 * @author zhuyan.luo@hand-china.com
 * @date 2020/1/21
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import React from 'react';
import { RegisterNode } from 'gg-editor2';

import parallelNodeIcon from '../assets/icons/parallel@2x.png';
import serialNodeIcon from '../assets/icons/serial@2x.svg';

class ChainMergeNode extends React.Component {
  render() {
    const nodeConfig = {
      draw(item) {
        // const keyShape = this.drawKeyShape(item);
        const group = item.getGraphicGroup();
        const { nodeType } = item.getModel();
        const nodeColor = nodeType === 'PARALLEL_GATEWAY_NODE' ? '#e8b6d4' : '#e9fbf9';
        const iconFont = nodeType === 'PARALLEL_GATEWAY_NODE' ? parallelNodeIcon : serialNodeIcon;
        const keyShape = group.addShape('circle', {
          attrs: {
            x: 0,
            y: 0,
            r: 15,
            type: 'circle',
            fill: nodeColor,
            stroke: 'black',
          },
        });
        // 绘制图标
        group.addShape('image', {
          attrs: {
            x: -8,
            y: -8,
            width: 16,
            height: 16,
            img: iconFont,
          },
        });
        return keyShape;
      },
      afterDraw() {},
      anchor: [
        [0.5, 0],
        [0.5, 1],
        [0, 0.5],
        [1, 0.5],
      ],
    };

    return <RegisterNode name="mergeNode" config={nodeConfig} extend="flow-circle" />;
  }
}

export default ChainMergeNode;
